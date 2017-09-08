package com.github.cbuschka.workspace_mechanic;

import com.github.cbuschka.workspace_mechanic.internal.MechanicConfig;
import com.github.cbuschka.workspace_mechanic.internal.MechanicContext;
import com.github.cbuschka.workspace_mechanic.internal.MechanicContextFactory;
import com.github.cbuschka.workspace_mechanic.internal.MigrationOutcome;
import com.github.cbuschka.workspace_mechanic.internal.Migrator;
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
		MigrationOutcome outcome = new Migrator(database, context).migrate();

		log.debug("Migration finished with {}.", outcome);
	}
}
