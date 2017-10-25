package com.github.cbuschka.workspace_mechanic.internal.migration;

import com.github.cbuschka.workspace_mechanic.internal.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class MigrationCollector
{
	private static Logger log = LoggerFactory.getLogger(MigrationCollector.class);

	@Autowired
	private MigrationSourceFactory migrationSourcesFactory;
	@Autowired
	private Database database;

	public List<Migration> collectPendingMigrations()
	{
		List<Migration> migrations = new ArrayList<>();
		for (MigrationSource migrationSource : this.migrationSourcesFactory.getMigrationSources())
		{
			for (Migration migration : migrationSource.getMigrations())
			{
				boolean alreadyApplied = this.database.hasSucceeded(migration.getName());
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
}
