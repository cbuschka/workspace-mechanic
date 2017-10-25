package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.Main;
import com.github.cbuschka.workspace_mechanic.internal.migration.MigrationOutcome;
import com.github.cbuschka.workspace_mechanic.internal.migration.Migrator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@SpringApplicationConfiguration(classes = {Main.class, IntegrationTestConfig.class})
public class MigrationIntegrationTest
{
	@Autowired
	private IntegrationTestWorkspace testWorkspace;

	@Autowired
	private Migrator migrator;

	@Autowired
	private BashScriptGenerator bashScriptGenerator;

	@Autowired
	private AntScriptGenerator antScriptGenerator;

	private MigrationOutcome migrationSucceeded;

	@Before
	public void beforeEach()
	{
	}

	@DirtiesContext
	@Test
	public void noMigrations()
	{
		whenMigrationRuns();

		assertThat(migrationSucceeded, is(MigrationOutcome.NOTHING_MIGRATED));
	}

	@DirtiesContext
	@Test
	public void singleFileMigrationSucceeding()
	{
		IntegrationTestWorkspace.TestMigration testMigration = testWorkspace.addSucceedingMigration("001_first", Mode.EXECUTABLE, bashScriptGenerator);

		whenMigrationRuns();

		assertThat(migrationSucceeded, is(MigrationOutcome.MIGRATION_SUCCEEDED));
		assertThat(testMigration.hasSucceeded(), is(true));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration.getName()), is(true));
	}

	@DirtiesContext
	@Test
	public void singleAntFileMigrationFailing()
	{
		IntegrationTestWorkspace.TestMigration testMigration = testWorkspace.addFailingMigration("001_first", Mode.NOT_EXECUTABLE, antScriptGenerator);

		whenMigrationRuns();

		assertThat(migrationSucceeded, is(MigrationOutcome.MIGRATION_FAILED));
		assertThat(testMigration.hasSucceeded(), is(false));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration.getName()), is(false));
	}

	@DirtiesContext
	@Test
	public void singleAntFileMigrationSucceeding()
	{
		IntegrationTestWorkspace.TestMigration testMigration = testWorkspace.addSucceedingMigration("001_first", Mode.NOT_EXECUTABLE, antScriptGenerator);

		whenMigrationRuns();

		assertThat(migrationSucceeded, is(MigrationOutcome.MIGRATION_SUCCEEDED));
		assertThat(testMigration.hasSucceeded(), is(true));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration.getName()), is(true));
	}

	@DirtiesContext
	@Test
	public void allSucceeding()
	{
		IntegrationTestWorkspace.TestMigration testMigration1 = testWorkspace.addSucceedingMigration("001_first", Mode.EXECUTABLE, bashScriptGenerator);
		IntegrationTestWorkspace.TestMigration testMigration2 = testWorkspace.addSucceedingMigration("002_second", Mode.EXECUTABLE, bashScriptGenerator);
		IntegrationTestWorkspace.TestMigration testMigration3 = testWorkspace.addSucceedingDirMigration("003_third_dir", Mode.EXECUTABLE, bashScriptGenerator);

		whenMigrationRuns();

		assertThat(migrationSucceeded, is(MigrationOutcome.MIGRATION_SUCCEEDED));
		assertThat(testMigration1.wasRun(), is(true));
		assertThat(testMigration2.wasRun(), is(true));
		assertThat(testMigration3.wasRun(), is(true));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration1.getName()), is(true));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration2.getName()), is(true));
		assertThat(testWorkspace.getDatabase().hasSucceeded(testMigration3.getName()), is(true));
	}

	@DirtiesContext
	@Test
	public void firstFails()
	{
		IntegrationTestWorkspace.TestMigration testMigration1 = testWorkspace.addFailingMigration("001_first", Mode.EXECUTABLE, bashScriptGenerator);
		IntegrationTestWorkspace.TestMigration testMigration2 = testWorkspace.addSucceedingMigration("002_second", Mode.EXECUTABLE, bashScriptGenerator);

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
		migrationSucceeded = this.migrator.migrate();
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
