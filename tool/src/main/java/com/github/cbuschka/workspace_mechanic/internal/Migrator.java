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

	private final MigrationExecutor migrationExecutor;
	private final MechanicContext context;
	private final MigrationCollector migrationCollector;

	public Migrator(MigrationCollector migrationCollector, Database database, MechanicContext context)
	{
		this.migrationCollector = migrationCollector;
		this.database = database;
		this.context = context;
		this.migrationExecutor = new MigrationExecutor(context);
	}

	public MigrationOutcome migrate()
	{
		List<Migration> pendingMigrations = this.migrationCollector.collectPendingMigrations();
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
			migration.execute(migrationExecutor);
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
		this.database.recordMigrationStarted(migration.getName(), migration.getChecksum());
	}
}
