package com.github.cbuschka.workspace_mechanic;

import com.github.cbuschka.workspace_mechanic.internal.database.Database;
import com.github.cbuschka.workspace_mechanic.internal.MechanicConfig;
import com.github.cbuschka.workspace_mechanic.internal.Migrator;

import java.io.File;
import java.util.Collections;

public class Main
{
	public static void main(String[] args)
	{
		File userHome = new File(System.getProperty("user.home"));
		File mechanicDir = new File(userHome, ".mechanic");

		new Migrator(new Database(mechanicDir)).migrate(new MechanicConfig(Collections.emptyList()));
	}
}
