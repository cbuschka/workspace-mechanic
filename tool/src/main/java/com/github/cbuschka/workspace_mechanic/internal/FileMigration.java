package com.github.cbuschka.workspace_mechanic.internal;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class FileMigration implements Migration
{
	private File file;

	public FileMigration(File file)
	{
		this.file = file;
	}

	@Override
	public String getName()
	{
		return FilenameUtils.getBaseName(this.file.getName());
	}

	@Override
	public void execute(MigrationExecutor migrationExecutor) throws MigrationFailedException
	{
		migrationExecutor.execute(getName(), this.file, this.file.getParentFile());
	}
}
