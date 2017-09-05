package com.github.cbuschka.workspace_mechanic.internal;

import java.util.ArrayList;
import java.util.List;

public class MechanicConfig
{
	private List<MigrationSource> migrationSources = new ArrayList<>();

	public MechanicConfig(List<MigrationSource> migrationSources)
	{
		this.migrationSources = migrationSources;
	}

	public List<MigrationSource> getMigrationSources()
	{
		return migrationSources;
	}
}
