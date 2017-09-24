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
		File f = getFileFrom(migration);
		return f != null && f.getName().endsWith(".ant.xml");
	}

	private File getFileFrom(Migration migration)
	{
		if (migration instanceof FileMigration)
		{
			FileMigration fileMigration = (FileMigration) migration;
			return fileMigration.getFile();
		}
		else if (migration instanceof DirMigration)
		{
			DirMigration fileMigration = (DirMigration) migration;
			return fileMigration.getExecutable();
		}

		return null;
	}

	@Override
	public void execute(Migration migration) throws MigrationFailedException
	{
		File antFile = getFileFrom(migration);

		String[] args = new String[]{"-f", antFile.getAbsolutePath()};
		org.apache.tools.ant.Main.start(args, null, Thread.currentThread().getContextClassLoader());
	}
}