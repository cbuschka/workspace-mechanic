package com.github.cbuschka.workspace_mechanic.internal;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

public class ExecutableFileMigrationExecutor implements MigrationExecutor
{
	@Override
	public boolean handles(Migration migration)
	{
		return (migration instanceof FileMigration);
	}

	@Override
	public void execute(Migration migration) throws MigrationFailedException
	{
		FileMigration fileMigration = (FileMigration) migration;
		try
		{
			Process process = new ProcessBuilder().command(fileMigration.getFile().getAbsolutePath()).inheritIO().start();
			int exitCode = process.waitFor();
			if (exitCode != 0)
			{
				throw new MigrationFailedException(String.format("Migration %s failed with exit code %d.", fileMigration.getFile().getAbsolutePath(), exitCode));
			}
		}
		catch (InterruptedException | IOException ex)
		{
			throw new UndeclaredThrowableException(ex);
		}
	}

}