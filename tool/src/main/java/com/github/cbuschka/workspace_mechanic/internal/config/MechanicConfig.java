package com.github.cbuschka.workspace_mechanic.internal.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MechanicConfig
{
	private List<File> migrationDirs = new ArrayList<>();

	private File workDir;

	private File dbDir;

	public MechanicConfig(List<File> migrationDirs, File workDir, File dbDir)
	{
		this.migrationDirs = migrationDirs;
		this.workDir = workDir;
		this.dbDir = dbDir;
	}

	public File getDbDir()
	{
		return dbDir;
	}

	public File getWorkDir()
	{
		return this.workDir;
	}

	public List<File> getMigrationDirs()
	{
		return migrationDirs;
	}
}
