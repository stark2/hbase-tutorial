import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class FilterOnRowKeys {

    private static void printResults(ResultScanner scanResult) {
        System.out.println();
        System.out.println("Results: ");

        for (Result res : scanResult) {
            for (Cell cell : res.listCells()) {
                String row = new String(CellUtil.cloneRow(cell));
                String family = new String(CellUtil.cloneFamily(cell));
                String column = new String(CellUtil.cloneQualifier(cell));
                String value = new String(CellUtil.cloneValue(cell));

                System.out.println(row + " " + family + " " + column + " " + value);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(conf);

        Table table = null;
        ResultScanner scanResult = null;
        try {
            table = connection.getTable(TableName.valueOf("census"));

            Filter filter = new RowFilter(CompareFilter.CompareOp.LESS,
                    new BinaryComparator(Bytes.toBytes("2")));

            Scan userScan = new Scan();
            userScan.setFilter(filter);

            scanResult = table.getScanner(userScan);

            printResults(scanResult);

            filter = new RowFilter(CompareFilter.CompareOp.EQUAL,
                    new BinaryComparator(Bytes.toBytes("3")));
            userScan.setFilter(filter);

            scanResult = table.getScanner(userScan);

            printResults(scanResult);
        } finally {
            connection.close();
            if (table != null) {
                table.close();
            }
            if (scanResult != null) {
                scanResult.close();
            }
        }
    }
}































