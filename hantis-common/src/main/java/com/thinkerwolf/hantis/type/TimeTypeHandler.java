package com.thinkerwolf.hantis.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class TimeTypeHandler extends AbstractTypeHandler<Time> {

    public TimeTypeHandler() {
		super(Time.class);
	}

	@Override
    protected void setNotNullParameter(PreparedStatement stat, int pos, Time parameter) throws SQLException {
        stat.setTime(pos, parameter);
    }

    @Override
    protected Time getNotNullResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getTime(columnIndex);
    }

    @Override
    protected Time getNotNullResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getTime(columnName);
    }

}