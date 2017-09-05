package com.github.cbuschka.workspace_mechanic.internal;

import java.util.List;

public interface MigrationSource
{
	List<Migration> getMigrations();
}
