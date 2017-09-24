package com.github.cbuschka.workspace_mechanic.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class NashornMigrationExecutionStrategy implements MigrationExecutionStrategy
{
	private static Logger log = LoggerFactory.getLogger(NashornMigrationExecutionStrategy.class);

	private MechanicContext context;

	public NashornMigrationExecutionStrategy(MechanicContext context)
	{
		this.context = context;
	}

	@Override
	public boolean handles(Migration migration)
	{
		File f = getFileFrom(migration);
		return f != null && f.getName().endsWith(".jjs");
	}

	private File getFileFrom(Migration migration)
	{
		if (migration instanceof DirMigration)
		{
			DirMigration fileMigration = (DirMigration) migration;
			return fileMigration.getExecutable();
		}

		return null;
	}

	@Override
	public void execute(Migration migration) throws MigrationFailedException
	{
		File jjs = getFileFrom(migration);

		try
		{
			ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");
			nashorn.eval(new BufferedReader(new FileReader(jjs)));
		}
		catch (IOException | ScriptException ex)
		{
			throw new MigrationFailedException("Failed.", ex);
		}
	}
}