package com.github.cbuschka.workspace_mechanic.internal.cli;

import com.github.cbuschka.workspace_mechanic.internal.config.MechanicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class CommandDispatcher
{
	private static Logger log = LoggerFactory.getLogger(CommandDispatcher.class);

	@Autowired
	private MechanicConfig mechanicConfig;

	@Autowired
	private List<Command> commands = new ArrayList<>();

	@Autowired
	private CommandLineParser commandLineParser;

	public int run(String[] args)
	{
		CommandLine commandLine = commandLineParser.parse(args);

		log.debug("Migration directories: {}", mechanicConfig.getMigrationDirs());
		log.debug("Work directory: {}", mechanicConfig.getWorkDir());
		log.debug("State directory: {}", mechanicConfig.getDbDir());

		String subCommand = commandLine.getSubCommand();
		if (subCommand == null)
		{
			subCommand = "help";
		}

		Command command = getCommandFor(subCommand);
		return command.run();
	}

	private Command getCommandFor(String commandName)
	{
		for (Command curr : this.commands)
		{
			if (curr.getCommandName().equals(commandName))
			{
				return curr;
			}
		}

		throw new NoSuchElementException("No command named: " + commandName);
	}
}
