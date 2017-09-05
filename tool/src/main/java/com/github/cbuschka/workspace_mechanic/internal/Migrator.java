package com.github.cbuschka.workspace_mechanic.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Migrator
{
	private static Logger log = LoggerFactory.getLogger(Migrator.class);

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
	}

	private void recordMigrationSucceeded(Migration migration)
	{
	}

	private void recordMigrationStarted(Migration migration)
	{
	}

	private List<Migration> collectPendingMigrations(MechanicConfig mechanicConfig)
	{
		List<Migration> migrations = new ArrayList<>();
		for(MigrationSource migrationSource : mechanicConfig.getMigrationSources()) {
			migrations.addAll(migrationSource.getMigrations());
		}

		return migrations;
	}
}
