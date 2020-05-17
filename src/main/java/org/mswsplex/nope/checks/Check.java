package org.mswsplex.nope.checks;

import javax.naming.OperationNotSupportedException;

import org.mswsplex.nope.NOPE;

public interface Check {
	public CheckType getType();

	public void register(NOPE plugin) throws OperationNotSupportedException;

	public String getCategory();

	public String getDebugName();

	public boolean lagBack();
}