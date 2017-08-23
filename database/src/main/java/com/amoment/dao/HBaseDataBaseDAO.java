package com.amoment.dao;

import com.amoment.core.HBaseOperation;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Repository
@Transactional
public class HBaseDataBaseDAO {

    private Admin admin = null;
    private Connection connection = null;
    private final int maxVersion = 3;
    private static Configuration config = null;

    static {
        try {
            config = HBaseConfiguration.create();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HBaseDataBaseDAO() throws IOException {
        try {
            connection = ConnectionFactory.createConnection(config);
            admin = connection.getAdmin();
        }catch (IOException e) {
            throw e;
        }
    }

    public synchronized void getConnection() throws IOException {
        try {
            if (isNull(connection) || isNull(admin)) {
                connection = ConnectionFactory.createConnection(config);
                admin = connection.getAdmin();
            }
        }catch (IOException e) {
            throw e;
        }
    }

    public synchronized void destroy() throws IOException {
        if (nonNull(admin)) {
            admin.close();
            admin = null;
        }
        if (nonNull(connection)) {
            connection.close();
            connection = null;
        }
    }

    public synchronized void flush(String nameSpace, String tableName) throws IOException {
        TableName tb = TableName.valueOf(nameSpace, tableName);
        if (!admin.tableExists(tb)) {
            admin.flush(tb);
        }
    }

    public synchronized void withoutAndCreateTable(String nameSpace, String tableName, String[] families) throws IOException {
        if (isNull(admin.getNamespaceDescriptor(nameSpace))) {
            admin.createNamespace(NamespaceDescriptor.create(nameSpace).build());
        }
        TableName tb = TableName.valueOf(nameSpace, tableName);
        if (!admin.tableExists(tb)) {
            HTableDescriptor descriptor = new HTableDescriptor(tb);
            for (String family : families
                    ) {
                HColumnDescriptor columnDescriptor = new HColumnDescriptor(family);
                columnDescriptor.setMaxVersions(maxVersion);
                descriptor.addFamily(columnDescriptor);
            }
            admin.createTable(descriptor);
        }
    }

    public Object[] batch(String nameSpace, String tableName, HBaseOperation operation) throws IOException, InterruptedException {

        TableName tb = TableName.valueOf(nameSpace, tableName);
        if (admin.tableExists(tb)) {
            List<Row> batchList = operation.getBatch();
            if (batchList.size() > 0) {
                Object[] results = new Object[batchList.size()];
                Table table = connection.getTable(tb);
                table.batch(batchList, results);
                return results;
            }
        }

        return null;
    }

    public ResultScanner query(String nameSpace, String tableName, Filter filter) throws IOException {
        TableName tb = TableName.valueOf(nameSpace, tableName);
        if (admin.tableExists(tb)) {
            Scan scan = new Scan();
            Table table = connection.getTable(tb);
            if (nonNull(filter)) {
                scan.setFilter(filter);
            }
            return table.getScanner(scan);
        }

        return null;
    }
}
