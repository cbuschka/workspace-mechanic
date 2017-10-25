package com.github.cbuschka.workspace_mechanic.internal.migration.ant;

import com.github.cbuschka.workspace_mechanic.internal.migration.Migration;
import com.github.cbuschka.workspace_mechanic.internal.migration.MigrationExecutionStrategy;
import com.github.cbuschka.workspace_mechanic.internal.migration.MigrationFailedException;
import com.github.cbuschka.workspace_mechanic.internal.util.ValueHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class AntMigrationExecutionStrategy implements MigrationExecutionStrategy
{
	private static Logger log = LoggerFactory.getLogger(AntMigrationExecutionStrategy.class);

	@Override
	public boolean handles(Migration migration)
	{
		File f = migration.getExecutable();
		return f != null && f.getName().endsWith(".xml");
	}

	@Override
	public void execute(Migration migration) throws MigrationFailedException
	{
		File antFile = migration.getExecutable();

		ValueHolder<Integer> exitCodeHolder = new ValueHolder<>();
		String[] args = new String[]{"-f", antFile.getAbsolutePath()};
		org.apache.tools.ant.Main main = new org.apache.tools.ant.Main()
		{
			@Override
			protected void exit(int exitCode)
			{
				exitCodeHolder.setValue(exitCode);
			}
		};
		main.startAnt(args, null, Thread.currentThread().getContextClassLoader());
		if (exitCodeHolder.getValue() != null && exitCodeHolder.getValue() != 0)
		{
			throw new MigrationFailedException("Migration failed with exit code " + exitCodeHolder.getValue() + ".");
		}
	}
}