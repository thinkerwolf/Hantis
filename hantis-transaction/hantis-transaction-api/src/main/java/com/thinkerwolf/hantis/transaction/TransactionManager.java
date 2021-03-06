package com.thinkerwolf.hantis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import com.thinkerwolf.hantis.common.Initializing;

/**
 * 事务管理器
 *
 * @author wukai
 */
public interface TransactionManager extends Initializing {

    Transaction getTransaction(TransactionDefinition defination);

    String getName();

    void commit(Transaction transaction);

    void rollback(Transaction transaction);
    
    ConnectionHolder createResourceHolder(Connection conn) throws SQLException;

    /**
     * Check the transactionManager is distributed or not
     */
    boolean isDistributed();

}
