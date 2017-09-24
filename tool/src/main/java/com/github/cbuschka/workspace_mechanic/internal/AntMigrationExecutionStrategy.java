package com.github.cbuschka.workspace_mechanic.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class AntMigrationExecutionStrategy implements MigrationExecutionStrategy
{
	private static Logger log = LoggerFactory.getLogger(AntMigrationExecutionStrategy.class);

	private MechanicContext context;

	public AntMigrationExecutionStrategy(MechanicContext context)
	{
		this.context = context;
	}

	@Override
	public boolean handles(Migration migration)
	{
		File f = migration.getExecutable();
		return f != null && f.getName().endsWith(".ant.xml");
	}

	@Override
	public void execute(Migration migration) throws MigrationFailedException
	{
		File antFile = migration.getExecutable();

		String[] args = new String[]{"-f", antFile.getAbsolutePath()};
		org.apache.tools.ant.Main.start(args, null, Thread.currentThread().getContextClassLoader());
	}
}