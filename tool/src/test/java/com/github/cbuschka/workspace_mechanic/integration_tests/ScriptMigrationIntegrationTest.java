package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.Migrator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ScriptMigrationIntegrationTest
{
	private IntegrationTestWorkspace testWorkspace;

	private boolean migrationSucceeded;

	@Before
	public void setUp()
	{
		testWorkspace = new IntegrationTestWorkspace();
	}

	@Test
	public void singleSucceeding()
	{
		IntegrationTestWorkspace.TestMigration testMigration = testWorkspace.addSucceedingMigration("001_first");

		whenMigrationRuns();

		assertThat(migrationSucceeded, is(true));
		assertThat(testMigration.hasSucceeded(), is(true));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration.getName()), is(true));
	}

	@Test
	public void allSucceeding()
	{
		IntegrationTestWorkspace.TestMigration testMigration1 = testWorkspace.addSucceedingMigration("001_first");
		IntegrationTestWorkspace.TestMigration testMigration2 = testWorkspace.addSucceedingMigration("002_second");

		whenMigrationRuns();

		assertThat(migrationSucceeded, is(true));
		assertThat(testMigration1.wasRun(), is(true));
		assertThat(testMigration2.wasRun(), is(true));
		assertThat(testWorkspace.getDatabase().isExecuted(testMigration1.getName()), is(true));
		assertThat(testWorkspace.getDatabase().isExecuted(testMigration2.getName()), is(true));
	}

	@Test
	public void firstFails()
	{
		IntegrationTestWorkspace.TestMigration testMigration1 = testWorkspace.addFailingMigration("001_first");
		IntegrationTestWorkspace.TestMigration testMigration2 = testWorkspace.addSucceedingMigration("002_second");

		whenMigrationRuns();

		assertThat(migrationSucceeded, is(false));
		assertThat(testMigration1.wasRun(), is(true));
		assertThat(testMigration1.hasFailed(), is(true));
		assertThat(testMigration2.wasRun(), is(false));
		assertThat(testWorkspace.getDatabase().isExecuted(testMigration1.getName()), is(true));
		assertThat(testWorkspace.getDatabase().isExecuted(testMigration2.getName()), is(false));
	}

	private void whenMigrationRuns()
	{
		migrationSucceeded = new Migrator(testWorkspace.getDatabase()).migrate(testWorkspace.getConfig());
	}

	@After
	public void tearDown()
	{
		testWorkspace.destroy();
	}
}
