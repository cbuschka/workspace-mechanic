package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.DirectoryMigrationSource;
import com.github.cbuschka.workspace_mechanic.internal.MechanicConfig;
import com.github.cbuschka.workspace_mechanic.internal.MigrationType;
import com.github.cbuschka.workspace_mechanic.internal.database.Database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;

public class IntegrationTestWorkspace
{
	private Database database;
	private File baseDir;

	public IntegrationTestWorkspace()
	{
		this.baseDir = new File("/tmp", "workspace-mechanic-" + System.currentTimeMillis() + "-itest");
		this.baseDir.mkdirs();

		getMigrationsD().mkdirs();
		getTestOutputD().mkdirs();

		this.database = new Database(this.baseDir);
	}

	public File getMigrationsD()
	{
		return new File(baseDir, "migrations.d");
	}

	public MechanicConfig getConfig()
	{
		return new MechanicConfig(Arrays.asList(new DirectoryMigrationSource(MigrationType.MIGRATION, getMigrationsD())));
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
