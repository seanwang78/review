package com.weihui.csa.core.biz.process.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import com.weihui.csa.core.biz.process.domain.ColumnDetail;
import com.weihui.csa.core.biz.process.domain.ColumnDetails;

public class JdbcUtil {

    public static String getCleanColumnName(String contextColumn) {
        int lastIdx = contextColumn.lastIndexOf(".");
        if (lastIdx == -1) {
            return contextColumn;
        } else {
            return contextColumn.substring(lastIdx + 1);
        }
    }

    public static BasicDataSource createBasicDataSource(String url, String username, String password,
            String driverclass, int maxActive, int maxIdle, int minIdle) {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driverclass);
        ds.setMaxActive(maxActive);
        ds.setMaxIdle(maxIdle);
        ds.setMinIdle(minIdle);
        return ds;
    }

    public static ColumnDetails getColumns(SqlRowSet rowSet) {
        ColumnDetails ret = new ColumnDetails();
        SqlRowSetMetaData metaData = rowSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String colName = metaData.getColumnName(i).toLowerCase();
            ColumnDetail detail = new ColumnDetail(colName, i - 1, metaData.getColumnClassName(i));
            ret.add(detail);
        }
        return ret;
    }

    public static List<Object[]> getResults(SqlRowSet rowSet) {
        List<Object[]> rowList = new ArrayList<Object[]>();
        int colNum = rowSet.getMetaData().getColumnCount();
        while (rowSet.next()) {
            Object[] row = new Object[colNum];
            for (int curCol = 1; curCol <= colNum; curCol++) {
                row[curCol - 1] = rowSet.getObject(curCol);
            }
            rowList.add(row);
        }
        return rowList;
    }
}
