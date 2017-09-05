package com.github.cbuschka.workspace_mechanic.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Migrator
{
	private static Logger log = LoggerFactory.getLogger(Migrator.class);
	private final Database database;

	public Migrator(Database database)
	{
		this.database = database;
	}

	public void migrate(MechanicConfig mechanicConfig)
	{
		List<Migration> pendingMigrations = collectPendingMigrations(mechanicConfig);

		for (Migration migration : pendingMigrations)
		{
			apply(migration);
		}
	}

	private void apply(Migration migration)
	{
		recordMigrationStarted(migration);
		try
		{
			migration.execute();
			recordMigrationSucceeded(migration);
		}
		catch (Exception ex)
		{
			recordMigrationFailed(migration);
		}
	}

	private void recordMigrationFailed(Migration migration)
	{
		this.database.recordMigrationFailed(migration.getName());
	}

	private void recordMigrationSucceeded(Migration migration)
	{
		this.database.recordMigrationSucceeded(migration.getName());
	}

	private void recordMigrationStarted(Migration migration)
	{
		this.database.recordMigrationStarted(migration.getName());
	}

	private List<Migration> collectPendingMigrations(MechanicConfig mechanicConfig)
	{
		List<Migration> migrations = new ArrayList<>();
		for (MigrationSource migrationSource : mechanicConfig.getMigrationSources())
		{
			for (Migration migration : migrationSource.getMigrations())
			{
				if (!database.isExecuted(migration.getName()))
				{
					migrations.add(migration);
				}
			}
		}

		return migrations;
	}
}
