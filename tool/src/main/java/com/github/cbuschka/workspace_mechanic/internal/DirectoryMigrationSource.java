package com.github.cbuschka.workspace_mechanic.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryMigrationSource implements MigrationSource
{
	private File baseDir;

	public DirectoryMigrationSource(File baseDir)
	{
		this.baseDir = baseDir;
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
				if (file.isDirectory() && new File(file, "migrate.sh").exists())
				{
					migrations.add(new DirMigration(file));
				}
				else if (file.isFile())
				{
					migrations.add(new FileMigration(file));
				}
			}
		}

		return migrations;
	}
}
