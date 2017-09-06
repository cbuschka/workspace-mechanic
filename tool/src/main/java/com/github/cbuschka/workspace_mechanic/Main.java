package com.github.cbuschka.workspace_mechanic;

import com.github.cbuschka.workspace_mechanic.internal.MigrationExecutor;
import com.github.cbuschka.workspace_mechanic.internal.MechanicConfig;
import com.github.cbuschka.workspace_mechanic.internal.Migrator;
import com.github.cbuschka.workspace_mechanic.internal.database.Database;

import java.io.File;
import java.util.Collections;

public class Main
{
	public static void main(String[] args)
	{
		File userHome = new File(System.getProperty("user.home"));
		File mechanicDir = new File(userHome, ".mechanic");

		Database database = new Database(mechanicDir);
		new Migrator(database).migrate(new MechanicConfig(Collections.emptyList()));
	}
}
