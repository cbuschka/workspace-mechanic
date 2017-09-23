package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.MigrationCollector;
import com.github.cbuschka.workspace_mechanic.internal.MigrationOutcome;
import com.github.cbuschka.workspace_mechanic.internal.Migrator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MigrationIntegrationTest
{
	private IntegrationTestWorkspace testWorkspace;

	private MigrationOutcome migrationSucceeded;

	@Before
	public void setUp()
	{
		testWorkspace = new IntegrationTestWorkspace();
	}

	@Test
	public void noMigrations()
	{
		whenMigrationRuns();

		assertThat(migrationSucceeded, is(MigrationOutcome.NOTHING_MIGRATED));
	}

	@Test
	public void singleFileMigrationSucceeding()
	{
		IntegrationTestWorkspace.TestMigration testMigration = testWorkspace.addSucceedingMigration("001_first", BashScriptGenerator.INSTANCE);

		whenMigrationRuns();

		assertThat(migrationSucceeded, is(MigrationOutcome.MIGRATION_SUCCEEDED));
		assertThat(testMigration.hasSucceeded(), is(true));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration.getName()), is(true));
	}


	@Test
	public void singlePythonFileMigrationSucceeding()
	{
		IntegrationTestWorkspace.TestMigration testMigration = testWorkspace.addSucceedingMigration("001_first", PythonScriptGenerator.INSTANCE);

		whenMigrationRuns();

		assertThat(migrationSucceeded, is(MigrationOutcome.MIGRATION_SUCCEEDED));
		assertThat(testMigration.hasSucceeded(), is(true));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration.getName()), is(true));
	}

	@Test
	public void singlePythonFileMigrationFailing()
	{
		IntegrationTestWorkspace.TestMigration testMigration = testWorkspace.addFailingMigration("001_first", PythonScriptGenerator.INSTANCE);

		whenMigrationRuns();

		assertThat(migrationSucceeded, is(MigrationOutcome.MIGRATION_FAILED));
		assertThat(testMigration.hasSucceeded(), is(false));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration.getName()), is(false));
	}

	@Test
	public void allSucceeding()
	{
		IntegrationTestWorkspace.TestMigration testMigration1 = testWorkspace.addSucceedingMigration("001_first", BashScriptGenerator.INSTANCE);
		IntegrationTestWorkspace.TestMigration testMigration2 = testWorkspace.addSucceedingMigration("002_second", BashScriptGenerator.INSTANCE);
		IntegrationTestWorkspace.TestMigration testMigration3 = testWorkspace.addSucceedingDirMigration("003_third_dir", BashScriptGenerator.INSTANCE);

		whenMigrationRuns();

		assertThat(migrationSucceeded, is(MigrationOutcome.MIGRATION_SUCCEEDED));
		assertThat(testMigration1.wasRun(), is(true));
		assertThat(testMigration2.wasRun(), is(true));
		assertThat(testMigration3.wasRun(), is(true));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration1.getName()), is(true));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration2.getName()), is(true));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration3.getName()), is(true));
	}

	@Test
	public void firstFails()
	{
		IntegrationTestWorkspace.TestMigration testMigration1 = testWorkspace.addFailingMigration("001_first", BashScriptGenerator.INSTANCE);
		IntegrationTestWorkspace.TestMigration testMigration2 = testWorkspace.addSucceedingMigration("002_second", BashScriptGenerator.INSTANCE);

		whenMigrationRuns();

		assertThat(migrationSucceeded, is(MigrationOutcome.MIGRATION_FAILED));
		assertThat(testMigration1.wasRun(), is(true));
		assertThat(testMigration1.hasFailed(), is(true));
		assertThat(testMigration2.wasRun(), is(false));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration1.getName()), is(false));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration2.getName()), is(false));
		assertThat(testWorkspace.getDatabase().hasFailed(testMigration2.getName()), is(false));
	}

	private void whenMigrationRuns()
	{
		migrationSucceeded = new Migrator(new MigrationCollector(testWorkspace.getDatabase(), testWorkspace.getContext()), testWorkspace.getDatabase(), testWorkspace.getContext()).migrate();
	}

	@After
	public void tearDown()
	{
		if (testWorkspace != null)
		{
			testWorkspace.destroy();
		}
	}
}
