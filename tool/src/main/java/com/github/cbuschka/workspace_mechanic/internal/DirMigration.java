package com.github.cbuschka.workspace_mechanic.internal;

import java.io.File;

public class DirMigration implements Migration
{
	private File executable;
	private File dir;

	public DirMigration(File dir)
	{
		this.dir = dir;
		this.executable = new File(dir, "migrate.sh");
	}

	@Override
	public String getName()
	{
		return this.dir.getName();
	}

	@Override
	public void execute(MigrationExecutor migrationExecutor) throws MigrationFailedException
	{
		migrationExecutor.execute(getName(), this.executable, this.dir);
	}
}
