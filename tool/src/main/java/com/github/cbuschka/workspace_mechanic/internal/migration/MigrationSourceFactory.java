package com.github.cbuschka.workspace_mechanic.internal.migration;

import com.github.cbuschka.workspace_mechanic.internal.config.MechanicConfig;
import com.github.cbuschka.workspace_mechanic.internal.env.OsDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class MigrationSourceFactory
{
	@Autowired
	private MechanicConfig mechanicConfig;

	@Autowired
	private OsDetector osDetector;

	public List<MigrationSource> getMigrationSources()
	{
		List<MigrationSource> migrationSources = new ArrayList<>();
		for (File migrationDir : this.mechanicConfig.getMigrationDirs())
		{
			migrationSources.add(new DirectoryMigrationSource(migrationDir, this.osDetector));
		}

		return migrationSources;
	}
}
