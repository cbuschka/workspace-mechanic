package com.github.cbuschka.workspace_mechanic.internal.migration;

public interface MigrationExecutionStrategy
{
	boolean handles(Migration migration);

	void execute(Migration migration) throws MigrationFailedException;
}