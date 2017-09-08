package com.github.cbuschka.workspace_mechanic.internal;

import com.github.cbuschka.workspace_mechanic.internal.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MigrationCollector
{
	private static Logger log = LoggerFactory.getLogger(MigrationCollector.class);

	private final Database database;

	private final MechanicContext context;

	public MigrationCollector(Database database, MechanicContext context)
	{
		this.database = database;
		this.context = context;
	}

	public List<Migration> collectPendingMigrations() {
		List<Migration> migrations = new ArrayList<>();
		for (MigrationSource migrationSource : getMigrationSources())
		{
			for (Migration migration : migrationSource.getMigrations())
			{
				boolean alreadyApplied = database.isExecuted(migration.getName());
				if (!alreadyApplied)
				{
					migrations.add(migration);
				}
				else
				{
					log.debug("Migration {} already applied.", migration.getName());
				}
			}
		}

		Collections.sort(migrations, Comparator.comparing(Migration::getName));

		log.debug("{} migration(s) found.", migrations.size());

		return migrations;
	}

	private Iterable<? extends MigrationSource> getMigrationSources()
	{
		List<MigrationSource> migrationSources = new ArrayList<>();
		for (File migrationDir : this.context.getConfig().getMigrationDirs())
		{
			migrationSources.add(new DirectoryMigrationSource(migrationDir,this.context));
		}

		return migrationSources;
	}
}
