package com.thinkerwolf.hantis.executor;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ResultSetListHandler<T> implements ResultSetHandler<List<T>> {

    private RowHandler<T> rowHandler;

    public ResultSetListHandler(RowHandler<T> rowHandler) {
        this.rowHandler = rowHandler;
    }

    @Override
    public List<T> process(ResultSet rs) throws Exception {
        List<T> l = new ArrayList<>();
        for (; rs.next(); ) {
            l.add(rowHandler.processRow(rs));
        }
        return l;
    }
    
}
