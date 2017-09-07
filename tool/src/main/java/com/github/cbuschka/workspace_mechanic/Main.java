package com.github.cbuschka.workspace_mechanic;

import com.github.cbuschka.workspace_mechanic.internal.MechanicConfig;
import com.github.cbuschka.workspace_mechanic.internal.Migrator;
import com.github.cbuschka.workspace_mechanic.internal.database.Database;

public class Main
{
	public static void main(String[] args)
	{
		MechanicConfig config = MechanicConfig.defaults();

		Database database = new Database(config.getDbDir());
		new Migrator(database, config).migrate();
	}
}
