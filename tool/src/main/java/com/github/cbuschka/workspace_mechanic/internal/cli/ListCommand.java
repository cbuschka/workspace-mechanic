package com.github.cbuschka.workspace_mechanic.internal.cli;

import com.github.cbuschka.workspace_mechanic.internal.database.Database;
import com.github.cbuschka.workspace_mechanic.internal.database.MigrationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ListCommand implements Command
{
	@Autowired
	private Database database;

	@Override
	public String getCommandName()
	{
		return "list";
	}

	@Override
	public int run()
	{
		Set<String> migrationsSeen = new HashSet<>();
		for (MigrationEvent event : this.database.getMigrationEvents(true))
		{
			if (!migrationsSeen.contains(event.getMigrationName()))
			{
				System.out.println(String.format("%d\t%s\t%s\t%s", event.getId(), event.getEventTimeAsString(), event.getMigrationName(), event.getEventType()));
				// migrationsSeen.add(event.getMigrationName());
			}
		}

		return 0;
	}
}
