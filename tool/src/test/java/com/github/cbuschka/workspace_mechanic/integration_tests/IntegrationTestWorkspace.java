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

	public TestMigration addSucceedingMigration(String name)
	{
		return addFileTestMigration(name, true);
	}

	public TestMigration addSucceedingDirMigration(String name)
	{
		return addDirTestMigration(name, true);
	}

	private TestMigration addFileTestMigration(String name, boolean shallSucceed)
	{
		try
		{
			File outputFile = getOutputFile(name, shallSucceed);
			File migrationFile = getMigrationFile(name, false);
			createMigrationScript(name, migrationFile, shallSucceed, outputFile);

			return new TestMigration(name);
		}
		catch (IOException ex)
		{
			throw new UndeclaredThrowableException(ex);
		}
	}

	private File getMigrationFile(String name, boolean dirMigration)
	{
		File parentDir = getMigrationsD();
		if (dirMigration)
		{
			parentDir = new File(parentDir, name);
			name = "migrate";
		}

		if (this.context.isCmdExeAvailable())
		{
			return new File(parentDir, name + ".bat");
		}
		else if (this.context.isBashAvailable())
		{
			return new File(parentDir, name + ".sh");
		}
		else
		{
			throw new IllegalStateException("Neither bash nor cmd.exe available.");
		}
	}

	private void createMigrationScript(String name, File migrationFile, boolean shallSucceed, File outputFile) throws IOException
	{
		migrationFile.getParentFile().mkdirs();
		if (migrationFile.getName().endsWith(".bat"))
		{
			FileWriter wr = new FileWriter(migrationFile);
			String script = new BatScriptGenerator().generate(name, outputFile.getAbsolutePath(), shallSucceed, context);
			wr.write(script);
			wr.close();
			migrationFile.setExecutable(true, true);

		}
		else if (migrationFile.getName().endsWith(".sh"))
		{
			FileWriter wr = new FileWriter(migrationFile);
			String script = new BashScriptGenerator().generate(name, outputFile.getAbsolutePath(), shallSucceed, context);
			wr.write(script);
			wr.close();
			migrationFile.setExecutable(true, true);
		}
		else
		{
			throw new IllegalStateException("Migration script name neither .sh nor .bat.");
		}
	}

	private TestMigration addDirTestMigration(String name, boolean shallSucceed)
	{
		try
		{
			File outputFile = getOutputFile(name, shallSucceed);
			File migrationFile = getMigrationFile(name, true);
			createMigrationScript(name, migrationFile, shallSucceed, outputFile);

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
		return addFileTestMigration(name, false);
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
