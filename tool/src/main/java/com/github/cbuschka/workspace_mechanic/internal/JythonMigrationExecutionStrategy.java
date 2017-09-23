package com.github.cbuschka.workspace_mechanic.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class JythonMigrationExecutionStrategy implements MigrationExecutionStrategy
{
	private static Logger log = LoggerFactory.getLogger(JythonMigrationExecutionStrategy.class);

	private MechanicContext context;

	public JythonMigrationExecutionStrategy(MechanicContext context)
	{
		this.context = context;
	}

	@Override
	public boolean handles(Migration migration)
	{
		File f = getFileFrom(migration);
		return f != null && f.getName().endsWith(".py");
	}

	private File getFileFrom(Migration migration) {
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
		File pyFile = getFileFrom(migration);

		org.python.util.PythonInterpreter interp = new org.python.util.PythonInterpreter();
		interp.execfile(pyFile.getAbsolutePath());
	}
}