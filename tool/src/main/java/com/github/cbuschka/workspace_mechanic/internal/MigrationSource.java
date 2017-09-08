package com.github.cbuschka.workspace_mechanic.internal;

public interface MigrationSource
{
	Iterable<Migration> getMigrations();
}
