package com.github.cbuschka.workspace_mechanic.internal;

import java.math.BigInteger;

public interface Migration
{
	String getName();

	void execute(MigrationExecutor migrationExecutor) throws MigrationFailedException;

	BigInteger getChecksum();
}
