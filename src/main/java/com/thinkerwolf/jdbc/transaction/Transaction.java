package com.thinkerwolf.jdbc.transaction;

import java.sql.Connection;
import java.sql.SQLException;

public interface Transaction {
	/**
	 * 事务提交
	 */
	void commit() throws SQLException;

	/**
	 * 事务回滚
	 */
	void rollback() throws SQLException;

	/**
	 * 事务关闭
	 * 
	 * @throws SQLException
	 */
	void close() throws SQLException;


}
