package com.github.cbuschka.workspace_mechanic.internal.migration;

import com.github.cbuschka.workspace_mechanic.internal.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Migrator
{
	private static Logger log = LoggerFactory.getLogger(Migrator.class);

	@Autowired
	private MigrationExecutor migrationExecutor;
	@Autowired
	private MigrationCollector migrationCollector;
	@Autowired
	private Database database;

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
		this.database.recordMigrationStarted(migration.getName(), migration.getChecksum());
	}
}
