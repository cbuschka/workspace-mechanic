package com.github.cbuschka.workspace_mechanic.internal.database;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DatabaseIntegrationTest
{
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void testX()
	{
		Database db = new Database(this.temporaryFolder.getRoot());

		for (int i = 0; i < 1000; ++i)
		{
			db.recordMigrationSucceeded("m" + i);
			db.flush();
		}
		db.close();

		assertThat(temporaryFolder.getRoot().list().length, is(7));
	}
}