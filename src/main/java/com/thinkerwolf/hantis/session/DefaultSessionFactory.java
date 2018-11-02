package com.thinkerwolf.hantis.session;

import com.thinkerwolf.hantis.transaction.DefaultTransactionDefinition;
import com.thinkerwolf.hantis.transaction.Transaction;
import com.thinkerwolf.hantis.transaction.TransactionManager;

import javax.sql.DataSource;

public class DefaultSessionFactory implements SessionFactory {


    private TransactionManager transactionManager;

    private SessionFactoryBuilder builder;



    public DefaultSessionFactory(SessionFactoryBuilder builder) {
        this.transactionManager = builder.getTransactionManager();
        this.builder = builder;
    }

    @Override
    public Session openSession() {
        Transaction transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        DefaultSession session = new DefaultSession(transaction, builder);
        return session;
    }

    @Override
    public Session openSession(boolean autoCommit) {
        return null;
    }


}
