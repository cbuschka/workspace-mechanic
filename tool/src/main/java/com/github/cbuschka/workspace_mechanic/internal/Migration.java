package com.github.cbuschka.workspace_mechanic.internal;

public interface Migration
{
	String getName();

	void execute(MigrationExecutor migrationExecutor) throws MigrationFailedException;
}
