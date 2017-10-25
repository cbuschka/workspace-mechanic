package com.github.cbuschka.workspace_mechanic.internal.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
public class MigrationExecutor
{
	private static Logger log = LoggerFactory.getLogger(MigrationExecutor.class);

	@Autowired
	private List<MigrationExecutionStrategy> strategies;

	public void execute(Migration migration) throws MigrationFailedException
	{
		MigrationExecutionStrategy strategy = getStrategyFor(migration);
		strategy.execute(migration);
	}

	private MigrationExecutionStrategy getStrategyFor(Migration migration)
	{
		for (MigrationExecutionStrategy strategy : strategies)
		{
			if (strategy.handles(migration))
			{
				return strategy;
			}
		}

		throw new NoSuchElementException("Unknown migration type " + migration.getExecutable() + ".");
	}
}
