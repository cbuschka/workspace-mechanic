package com.github.cbuschka.workspace_mechanic.internal.cli;

import com.github.cbuschka.workspace_mechanic.internal.migration.MigrationOutcome;
import com.github.cbuschka.workspace_mechanic.internal.migration.Migrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MigrateCommand implements Command
{
	private static Logger log = LoggerFactory.getLogger(MigrateCommand.class);

	@Autowired
	private Migrator migrator;

	@Override
	public String getCommandName()
	{
		return "migrate";
	}

	@Override
	public int run()
	{
		MigrationOutcome outcome = migrator.migrate();

		log.debug("Migration finished with {}.", outcome);

		if (outcome == MigrationOutcome.MIGRATION_FAILED)
		{
			return 1;
		}

		return 0;
	}
}
