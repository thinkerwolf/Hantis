package com.thinkerwolf.hantis.transaction.jpa;

import com.thinkerwolf.hantis.transaction.*;

public class JtaTransactionManager extends AbstractTransactionManager {

	@Override
	protected Transaction doGetTransaction(TransactionDefinition defination) {
		return null;
	}

	@Override
	protected boolean isExistsTransaction(Transaction transaction) {
		return false;
	}

	@Override
	protected Object doSuspend(Transaction transaction) throws TransactionException {
		return null;
	}

	@Override
	protected void doBegin(Transaction transaction, TransactionDefinition definition) throws TransactionException {

	}

	@Override
	protected void doCommit(Transaction transaction) throws TransactionException {

	}

	@Override
	protected void doResume(Transaction transaction, Object suspendResources) throws TransactionException {

	}

	@Override
	protected void doRollback(Transaction transaction) throws TransactionException {

	}

	@Override
	protected void doClearAfterCompletion(Transaction transaction) throws TransactionException {

	}

}