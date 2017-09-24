package com.github.cbuschka.workspace_mechanic.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class MigrationExecutor
{

	private List<Class<? extends MigrationExecutionStrategy>> strategies = Arrays.asList(
			JythonMigrationExecutionStrategy.class,
			JavaMigrationExecutionStrategy.class,
			BshMigrationExecutionStrategy.class,
			ExecutableMigrationExecutionStrategy.class
	);

	private static Logger log = LoggerFactory.getLogger(MigrationExecutor.class);

	private MechanicContext context;

	public MigrationExecutor(MechanicContext context)
	{
		this.context = context;
	}

	public void execute(Migration migration) throws MigrationFailedException
	{
		MigrationExecutionStrategy strategy = getStrategyFor(migration);
		strategy.execute(migration);
	}

	private MigrationExecutionStrategy getStrategyFor(Migration migration)
	{
		for (Class<? extends MigrationExecutionStrategy> strategyType : strategies)
		{
			try
			{
				MigrationExecutionStrategy strategy = strategyType.getConstructor(MechanicContext.class).newInstance(this.context);
				if (strategy.handles(migration))
				{
					return strategy;
				}
			}
			catch (ReflectiveOperationException ex)
			{
				throw new UndeclaredThrowableException(ex);
			}
		}

		throw new NoSuchElementException();
	}
}
