package com.github.cbuschka.workspace_mechanic.internal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

public class FileMigration implements Migration
{
	private MigrationType type;
	private File file;

	public FileMigration(MigrationType type, File file)
	{
		this.type = type;
		this.file = file;
	}

	@Override
	public String getName()
	{
		return this.file.getName();
	}

	@Override
	public MigrationType getType()
	{
		return type;
	}

	@Override
	public void execute()
	{
		try
		{
			Process process = new ProcessBuilder().command(this.file.getAbsolutePath()).inheritIO().start();
			int exitCode = process.waitFor();
			if (exitCode != 0)
			{
				throw new IllegalStateException("Migration failed.");
			}
		}
		catch (InterruptedException | IOException ex)
		{
			throw new UndeclaredThrowableException(ex);
		}
	}
}