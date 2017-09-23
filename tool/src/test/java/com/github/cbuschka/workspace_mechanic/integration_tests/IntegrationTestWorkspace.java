package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.MechanicConfig;
import com.github.cbuschka.workspace_mechanic.internal.MechanicContext;
import com.github.cbuschka.workspace_mechanic.internal.MechanicContextFactory;
import com.github.cbuschka.workspace_mechanic.internal.database.Database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

public class IntegrationTestWorkspace
{
	private final MechanicConfig config;
	private final MechanicContext context;
	private Database database;
	private File baseDir;

	public IntegrationTestWorkspace()
	{
		this.baseDir = new File("/tmp", "workspace-mechanic-" + System.currentTimeMillis() + "-itest");
		this.baseDir.mkdirs();

		this.config = MechanicConfig.standard(this.baseDir);
		this.context = new MechanicContextFactory().build(config);

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

	public TestMigration addSucceedingMigration(String name, ScriptGenerator scriptGenerator)
	{
		return addFileTestMigration(name, true, scriptGenerator);
	}

	public TestMigration addSucceedingDirMigration(String name, ScriptGenerator scriptGenerator)
	{
		return addDirTestMigration(name, true, scriptGenerator);
	}

	private TestMigration addFileTestMigration(String name, boolean shallSucceed, ScriptGenerator scriptGenerator)
	{
		try
		{
			File outputFile = getOutputFile(name, shallSucceed);
			File migrationFile = getMigrationFile(name, scriptGenerator.getExt(), false);
			createMigrationScript(name, migrationFile, shallSucceed, outputFile, scriptGenerator);

			return new TestMigration(name);
		}
		catch (IOException ex)
		{
			throw new UndeclaredThrowableException(ex);
		}
	}

	private File getMigrationFile(String name, String ext, boolean dirMigration)
	{
		File parentDir = getMigrationsD();
		if (dirMigration)
		{
			parentDir = new File(parentDir, name);
			return new File(parentDir, "migrate." + ext);
		}
		else
		{
			return new File(parentDir, name + "." + ext);
		}
	}

	private void createMigrationScript(String name, File migrationFile, boolean shallSucceed, File outputFile, ScriptGenerator scriptGenerator) throws IOException
	{
		migrationFile.getParentFile().mkdirs();

		FileWriter wr = new FileWriter(migrationFile);
		String script = scriptGenerator.generate(name, outputFile.getAbsolutePath(), shallSucceed, context);
		wr.write(script);
		wr.close();
		migrationFile.setExecutable(true, true);
	}

	private TestMigration addDirTestMigration(String name, boolean shallSucceed, ScriptGenerator scriptGenerator)
	{
		try
		{
			File outputFile = getOutputFile(name, shallSucceed);
			File migrationFile = getMigrationFile(name, scriptGenerator.getExt(), true);
			createMigrationScript(name, migrationFile, shallSucceed, outputFile, scriptGenerator);

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

	public TestMigration addFailingMigration(String name, ScriptGenerator scriptGenerator)
	{
		return addFileTestMigration(name, false, scriptGenerator);
	}

	public MechanicContext getContext()
	{
		return context;
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
