package com.github.cbuschka.workspace_mechanic.internal;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class BshMigrationExecutionStrategy implements MigrationExecutionStrategy
{
	private static Logger log = LoggerFactory.getLogger(BshMigrationExecutionStrategy.class);

	private MechanicContext context;

	public BshMigrationExecutionStrategy(MechanicContext context)
	{
		this.context = context;
	}

	@Override
	public boolean handles(Migration migration)
	{
		File f = migration.getExecutable();
		return f != null && f.getName().endsWith(".bsh");
	}

	@Override
	public void execute(Migration migration) throws MigrationFailedException
	{
		File bshFile = migration.getExecutable();

		try
		{
			Interpreter interpreter = new Interpreter();
			interpreter.setOut(System.out);
			interpreter.setErr(System.err);
			interpreter.eval(new BufferedReader(new FileReader(bshFile)));
		}
		catch (IOException | EvalError ex)
		{
			throw new MigrationFailedException("Failed.", ex);
		}
	}
}