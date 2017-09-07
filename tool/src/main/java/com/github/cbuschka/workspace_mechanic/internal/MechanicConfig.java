package com.github.cbuschka.workspace_mechanic.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MechanicConfig
{
	private List<File> migrationDirs = new ArrayList<>();

	private File workDir;

	private File dbDir;

	public static MechanicConfig defaults()
	{
		File cwd = new File(System.getProperty("user.dir"), ".");
		return standard(cwd);
	}

	public static MechanicConfig standard(File cwd)
	{
		File baseDir = cwd;
		File dotMechanicDir = new File(cwd, ".mechanic");
		if (dotMechanicDir.isDirectory())
		{
			baseDir = dotMechanicDir;
		}
		File migrationsDir = new File(baseDir, "migrations.d");
		File workDir = new File(baseDir, "work");
		File dbDir = new File(baseDir, "db");
		return new MechanicConfig(Arrays.asList(migrationsDir), workDir, dbDir);
	}

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
