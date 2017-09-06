package com.github.cbuschka.workspace_mechanic.internal;

public interface Migration
{
	MigrationType getType();

	String getName();

	void execute() throws MigrationFailedException;
}
