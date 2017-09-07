package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.MechanicConfig;
import com.github.cbuschka.workspace_mechanic.internal.database.Database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

public class IntegrationTestWorkspace
{
	private final MechanicConfig config;
	private Database database;
	private File baseDir;

	public IntegrationTestWorkspace()
	{
		this.baseDir = new File("/tmp", "workspace-mechanic-" + System.currentTimeMillis() + "-itest");
		this.baseDir.mkdirs();

		this.config = MechanicConfig.standard(this.baseDir);

		getMigrationsD().mkdirs();
		getTestOutputD().mkdirs();

		this.config.getDbDir().mkdirs();
		this.database = new Database(this.config.getDbDir());

		this.config.getWorkDir().mkdirs();
	}

	public File getMigrationsD()
	{
		return this.config.getMigrationDirs().get(0);
	}

	public MechanicConfig getConfig()
	{
		return this.config;
	}

	public TestMigration addSucceedingMigration(String name)
	{
		return addTestMigration(name, true);
	}

	private TestMigration addTestMigration(String name, boolean shallSucceed)
	{
		try
		{
			File outputFile = getOutputFile(name, shallSucceed);

			File migrationFile = new File(getMigrationsD(), name);
			FileWriter wr = new FileWriter(migrationFile);
			String script = String.format("#!/bin/bash\necho '%s'\ntouch %s\nexit %s", name, outputFile.getAbsolutePath(), shallSucceed ? "0" : "1");
			wr.write(script);
			wr.close();
			migrationFile.setExecutable(true, true);

			return new TestMigration(name);
		}
		catch (IOException ex)
		{
			throw new UndeclaredThrowableException(ex);
		}
	}

	private File getOutputFile(String name, boolean shallSucceed)
	{
		String outputFileName = String.format("%s.%s", name, shallSucceed ? "succeeded" : "failed");
		return new File(getTestOutputD(), outputFileName);
	}

	public void destroy()
	{

	}

	public File getTestOutputD()
	{
		return new File(baseDir, "test-output");
	}

	public Database getDatabase()
	{
		return database;
	}

	public TestMigration addFailingMigration(String name)
	{
		return addTestMigration(name, false);
	}

	public class TestMigration
	{
		private String name;

		public TestMigration(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		public boolean hasFailed()
		{
			return getOutputFile(this.name, false).isFile();
		}

		public boolean wasRun()
		{
			return hasFailed() || hasSucceeded();
		}

		public boolean hasSucceeded()
		{
			return getOutputFile(this.name, true).isFile();
		}
	}
}
