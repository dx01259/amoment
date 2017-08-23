package com.amoment.core;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;

import static java.util.Objects.isNull;

public class HBaseOperation {

    private List<Row> batch = new ArrayList<>();

    public void addRowData(String family, String rowKey, Map values, HBaseOperationFlag operationFlag)
            throws IOException {

        if (isNull(values) || values.size() <= 0 ) return;

        switch (operationFlag) {
            case ADD:
            case UPDATE:{
                Put put = new Put(Bytes.toBytes(rowKey));
                Set entrySet = values.entrySet();
                Map.Entry entry;
                Iterator it = entrySet.iterator();

                while(it.hasNext()) {
                    entry = (Map.Entry) it.next();
                    put.addColumn(Bytes.toBytes(family), Bytes.toBytes(String.valueOf(entry.getKey())),
                            Bytes.toBytes(String.valueOf(entry.getValue())));
                }
                batch.add(put);
                break;
            }
            case DELETE:{
                List<String> qualifiers = new ArrayList<>(values.keySet());
                Delete delete = new Delete(Bytes.toBytes(rowKey));
                for (String qualifier : qualifiers
                        ) {
                    delete.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
                }
                batch.add(delete);
                break;
            }
            default:break;
        }
    }

    public void addRowData(String family, String rowKey, Filter filter, HBaseOperationFlag operationFlag)
            throws IOException {

        switch (operationFlag) {
            case GET: {

            }
            case SCAN: {

            }
            default:break;
        }
    }

    public List<Row> getBatch() {
        return batch;
    }
}
