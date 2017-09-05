package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.DirectoryMigrationSource;
import com.github.cbuschka.workspace_mechanic.internal.MechanicConfig;
import com.github.cbuschka.workspace_mechanic.internal.MigrationType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;

public class IntegrationTestWorkspace
{
	private File baseDir;

	public IntegrationTestWorkspace()
	{
		this.baseDir = new File("/tmp", "workspace-mechanic-" + System.currentTimeMillis() + "-itest");
		this.baseDir.mkdirs();

		getMigrationsD().mkdirs();
		getTestOutputD().mkdirs();
	}

	public File getMigrationsD()
	{
		return new File(baseDir, "migrations.d");
	}

	public File getBaseDir()
	{
		return baseDir;
	}

	public MechanicConfig getConfig()
	{
		return new MechanicConfig(Arrays.asList(new DirectoryMigrationSource(MigrationType.MIGRATION, getMigrationsD())));
	}

	public TestMigration addSucceedingMigration(String name)
	{
		try
		{
			File successFile = new File(getTestOutputD(), name + ".succeeded");

			File file = new File(getMigrationsD(), name);
			FileWriter wr = new FileWriter(file);
			wr.write("#!/bin/bash\necho 'hello world.'\ntouch " + successFile.getAbsolutePath() + "\nexit 0");
			wr.close();
			file.setExecutable(true, true);

			return new TestMigration(name);
		}
		catch (IOException ex)
		{
			throw new UndeclaredThrowableException(ex);
		}
	}

	public void destroy()
	{

	}

	public File getTestOutputD()
	{
		return new File(baseDir, "test-output");
	}

	public class TestMigration
	{
		private String name;

		public TestMigration(String name)
		{
			this.name = name;
		}

		public boolean hasFailed()
		{
			return new File(getTestOutputD(), name + ".failed").isFile();
		}

		public boolean wasRun()
		{
			return hasFailed() || hasSucceeded();
		}

		public boolean hasSucceeded()
		{
			return new File(getTestOutputD(), name + ".succeeded").isFile();
		}
	}
}
