package com.github.cbuschka.workspace_mechanic.internal;

import com.github.cbuschka.workspace_mechanic.internal.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Migrator
{
	private static Logger log = LoggerFactory.getLogger(Migrator.class);

	private final Database database;
	private final MechanicConfig mechanicConfig;

	private final MigrationExecutor migrationExecutor;

	public Migrator(Database database, MechanicConfig mechanicConfig)
	{
		this.database = database;
		this.mechanicConfig = mechanicConfig;
		this.migrationExecutor = new MigrationExecutor();
	}

	public MigrationOutcome migrate()
	{
		List<Migration> pendingMigrations = collectPendingMigrations(mechanicConfig);
		if (pendingMigrations.isEmpty())
		{
			return MigrationOutcome.NOTHING_MIGRATED;
		}

		for (Migration migration : pendingMigrations)
		{
			try
			{
				execute(migration);
			}
			catch (MigrationFailedException ex)
			{
				log.error("Migration {} failed. Aborting.", migration.getName(), ex);
				return MigrationOutcome.MIGRATION_FAILED;
			}
		}

		return MigrationOutcome.MIGRATION_SUCCEEDED;
	}

	public void execute(Migration migration) throws MigrationFailedException
	{
		recordMigrationStarted(migration);
		try
		{
			migrationExecutor.execute(migration);
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
		for (MigrationSource migrationSource : getMigrationSources(mechanicConfig))
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

	private Iterable<? extends MigrationSource> getMigrationSources(MechanicConfig mechanicConfig)
	{
		List<MigrationSource> migrationSources = new ArrayList<>();
		for (File migrationDir : mechanicConfig.getMigrationDirs())
		{
			migrationSources.add(new DirectoryMigrationSource(MigrationType.MIGRATION, migrationDir));
		}

		return migrationSources;
	}
}
