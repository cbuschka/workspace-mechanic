package com.github.cbuschka.workspace_mechanic.internal.migration;

public interface MigrationSource
{
	Iterable<Migration> getMigrations();
}
