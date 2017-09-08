package com.github.cbuschka.workspace_mechanic.internal;

import org.apache.commons.io.FileUtils;

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
		File cwd = new File(System.getProperty("user.dir"), "");
		return standard(cwd);
	}

	public static MechanicConfig standard(File cwd)
	{
		File baseDir = cwd;
		File dotMechanicDir = FileUtils.getFile(cwd, ".mechanic");
		if (dotMechanicDir.isDirectory())
		{
			baseDir = dotMechanicDir;
		}
		File migrationsDir = FileUtils.getFile(baseDir, "migrations.d");
		File workDir = FileUtils.getFile(baseDir, "work");
		File dbDir = FileUtils.getFile(baseDir, "db");
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
