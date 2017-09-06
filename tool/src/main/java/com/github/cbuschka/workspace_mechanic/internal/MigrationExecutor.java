package com.github.cbuschka.workspace_mechanic.internal;

public interface MigrationExecutor
{
	boolean handles(Migration migration);

	void execute(Migration migration) throws MigrationFailedException;
}
