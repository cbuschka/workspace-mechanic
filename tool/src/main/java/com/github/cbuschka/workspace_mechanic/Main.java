package com.github.cbuschka.workspace_mechanic;

import com.github.cbuschka.workspace_mechanic.internal.Database;
import com.github.cbuschka.workspace_mechanic.internal.MechanicConfig;
import com.github.cbuschka.workspace_mechanic.internal.Migrator;

import java.io.File;
import java.util.Collections;

public class Main
{
	public static void main(String[] args)
	{
		new Migrator(new Database(new File("/tmp"))).migrate(new MechanicConfig(Collections.emptyList()));
	}
}
