package com.github.cbuschka.workspace_mechanic.internal;

import com.github.cbuschka.workspace_mechanic.internal.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Migrator
{
	private static Logger log = LoggerFactory.getLogger(Migrator.class);
	private final Database database;

	public Migrator(Database database)
	{
		this.database = database;
	}

	public boolean migrate(MechanicConfig mechanicConfig)
	{
		List<Migration> pendingMigrations = collectPendingMigrations(mechanicConfig);

		for (Migration migration : pendingMigrations)
		{
			try
			{
				apply(migration);
			}
			catch (MigrationFailedException ex)
			{
				log.error("Migration {} failed. Aborting.", migration.getName(), ex);
				return false;
			}
		}

		return true;
	}

	private void apply(Migration migration) throws MigrationFailedException
	{
		recordMigrationStarted(migration);
		try
		{
			migration.execute();
			recordMigrationSucceeded(migration);
		}
		catch (MigrationFailedException ex)
		{
			recordMigrationFailed(migration);
			throw ex;
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

		Collections.sort(migrations, Comparator.comparing(Migration::getName));

		return migrations;
	}
}
