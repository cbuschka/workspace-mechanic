package com.github.cbuschka.workspace_mechanic;

import com.github.cbuschka.workspace_mechanic.internal.*;
import com.github.cbuschka.workspace_mechanic.internal.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
	private static Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args)
	{
		MechanicConfig config = MechanicConfig.defaults();
		log.debug("Migration directories: {}", config.getMigrationDirs());
		log.debug("Work directory: {}", config.getWorkDir());
		log.debug("State directory: {}", config.getDbDir());

		MechanicContext context = new MechanicContextFactory().build(config);

		Database database = new Database(config.getDbDir());
		MigrationOutcome outcome = new Migrator(new MigrationCollector(database, context), database, context).migrate();

		log.debug("Migration finished with {}.", outcome);
	}
}
