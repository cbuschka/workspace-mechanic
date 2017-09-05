package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.Migrator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SingleScriptMigrationIntegrationTest
{

	private IntegrationTestWorkspace testWorkspace;

	@Before
	public void setUp()
	{

		testWorkspace = new IntegrationTestWorkspace();
	}

	@Test
	public void test()
	{
		IntegrationTestWorkspace.TestMigration testMigration = testWorkspace.addSucceedingMigration("001_hello");

		new Migrator(testWorkspace.getDatabase()).migrate(testWorkspace.getConfig());

		assertThat(testMigration.wasRun(), is(true));
	}

	@After
	public void tearDown()
	{
		testWorkspace.destroy();
	}
}
