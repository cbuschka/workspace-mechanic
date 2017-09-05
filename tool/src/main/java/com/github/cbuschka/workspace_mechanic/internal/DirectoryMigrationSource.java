package com.github.cbuschka.workspace_mechanic.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryMigrationSource implements MigrationSource
{
	private File baseDir;
	private MigrationType migrationType;

	public DirectoryMigrationSource(MigrationType migrationType, File baseDir)
	{
		this.baseDir = baseDir;
		this.migrationType = migrationType;
	}

	@Override
	public List<Migration> getMigrations()
	{
		return collectMigrationsFrom(baseDir);
	}

	private List<Migration> collectMigrationsFrom(File baseDir)
	{
		List<Migration> migrations = new ArrayList<>();
		if (baseDir.isDirectory())
		{
			for (File file : baseDir.listFiles())
			{
				if (file.isDirectory())
				{
					// skipped TODO
				}
				else if (file.isFile())
				{
					migrations.add(new FileMigration(migrationType, file));
				}
			}
		}

		return migrations;
	}
}
