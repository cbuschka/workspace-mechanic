package com.github.cbuschka.workspace_mechanic;

import com.github.cbuschka.workspace_mechanic.internal.MechanicConfig;
import com.github.cbuschka.workspace_mechanic.internal.Migrator;

import java.util.Collections;

public class Main
{
	public static void main(String[] args)
	{
		new Migrator().migrate(new MechanicConfig(Collections.emptyList()));
	}
}
