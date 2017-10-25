package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.config.MechanicConfig;
import com.github.cbuschka.workspace_mechanic.internal.database.Database;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

@Component
public class IntegrationTestWorkspace
{
	private static Logger log = LoggerFactory.getLogger(IntegrationTestWorkspace.class);

	@Autowired
	private MechanicConfig mechanicConfig;
	@Autowired
	private Database database;

	private File testOutputDir;

	public IntegrationTestWorkspace()
	{
	}

	@PostConstruct
	protected void init() throws IOException
	{
		this.testOutputDir = new File(this.mechanicConfig.getWorkDir().getParentFile(), "test-output");
		this.testOutputDir.mkdirs();

		log.info("Test output dir is: {}", this.testOutputDir.getAbsolutePath());
	}

	public File getMigrationsD()
	{
		return this.mechanicConfig.getMigrationDirs().get(0);
	}

	public TestMigration addSucceedingMigration(String name, Mode fileMode, ScriptGenerator scriptGenerator)
	{
		return addFileTestMigration(name, fileMode, true, scriptGenerator);
	}

	public TestMigration addSucceedingDirMigration(String name, Mode fileMode, ScriptGenerator scriptGenerator)
	{
		return addDirTestMigration(name, fileMode, true, scriptGenerator);
	}

	private TestMigration addFileTestMigration(String name, Mode fileMode, boolean shallSucceed, ScriptGenerator scriptGenerator)
	{
		try
		{
			File outputFile = getOutputFile(name, shallSucceed);
			File migrationFile = getMigrationFile(name, scriptGenerator.getExt(), false);
			createMigrationScript(name, migrationFile, fileMode, shallSucceed, outputFile, scriptGenerator);

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

	private void createMigrationScript(String name, File migrationFile, Mode fileMode, boolean shallSucceed, File outputFile, ScriptGenerator scriptGenerator) throws IOException
	{
		migrationFile.getParentFile().mkdirs();

		FileWriter wr = new FileWriter(migrationFile);
		String script = scriptGenerator.generate(name, outputFile.getAbsolutePath(), shallSucceed);
		wr.write(script);
		wr.close();
		migrationFile.setExecutable(fileMode == Mode.EXECUTABLE, true);
	}

	private TestMigration addDirTestMigration(String name, Mode fileMode, boolean shallSucceed, ScriptGenerator scriptGenerator)
	{
		try
		{
			File outputFile = getOutputFile(name, shallSucceed);
			File migrationFile = getMigrationFile(name, scriptGenerator.getExt(), true);
			createMigrationScript(name, migrationFile, fileMode, shallSucceed, outputFile, scriptGenerator);

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

	public File getTestOutputD()
	{
		return this.testOutputDir;
	}

	public Database getDatabase()
	{
		return database;
	}

	public TestMigration addFailingMigration(String name, Mode fileMode, ScriptGenerator scriptGenerator)
	{
		return addFileTestMigration(name, fileMode, false, scriptGenerator);
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

	public void destroy()
	{
		FileUtils.deleteQuietly(this.testOutputDir);
	}
}
