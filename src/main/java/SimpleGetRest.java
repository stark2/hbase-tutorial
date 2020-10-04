import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import net.iharder.base64.Base64;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SimpleGetRest {

    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    public static void main(String[] args) throws Exception {
        WebResource service = null;
        Client client = Client.create();
        try {
            // list_namespace_tables 'default'
            service = client.resource("http://localhost:8080/" +
                    URLEncoder.encode("census", UTF8_CHARSET.displayName()) + "/" +
                    URLEncoder.encode("1", UTF8_CHARSET.displayName())
            );
            service.accept("application/json");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ClientResponse response = service.accept("application/json").type("application/json").get(ClientResponse.class);

        if (response.hasEntity() && response.getStatus() == 200) {
            String originalJSONString = response.getEntity(String.class);
            System.out.println(originalJSONString);
            System.out.println();

            JSONObject jo = new JSONObject(originalJSONString);
            JSONArray row = (JSONArray) jo.get("Row");
            System.out.println(row.length());

            //personal:gender                                 timestamp=2020-08-29T12:16:51.461Z, value=male
            //personal:marital_status                         timestamp=2020-08-29T12:16:51.461Z, value=unmarried
            //personal:name                                   timestamp=2020-08-29T12:16:51.461Z, value=Mike Jones
            //professional:employed                           timestamp=2020-08-29T12:16:51.461Z, value=yes
            //professional:field                              timestamp=2020-08-29T12:16:51.461Z, value=construction

            for (int i = 0; i < row.length(); i++) {
                JSONObject col = row.getJSONObject(i);
                String key = (String) col.get("key");
                JSONArray rowCell = (JSONArray) col.get("Cell");
                System.out.println(rowCell.length());
                for (int k = 0; k < rowCell.length(); k++) {
                    JSONObject joCell = rowCell.getJSONObject(k);
                    String column = joCell.getString("column");
                    Long timestamp = joCell.getLong("timestamp");
                    String value = joCell.getString("$");

                    String tsFormatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date(timestamp));

                    System.out.println(tsFormatted + " (" + timestamp + "), "
                            + new String(Base64.decode(column)) + " (" + column + "), "
                            + new String(Base64.decode(value)) + " (" + value + ")");
                }
            }

            /*
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(originalJSONString);
            String prettyJsonString = gson.toJson(je);
            System.out.println(prettyJsonString);
            */
        } else {
            System.err.println(response.toString());
        }
    }
}
