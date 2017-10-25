package com.github.cbuschka.workspace_mechanic.internal.cli;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class CommandLineParser
{
	public CommandLineParser()
	{
	}

	public CommandLine parse(String... args)
	{
		CommandLine commandLine = new CommandLine();

		List<String> argList = new LinkedList<>(Arrays.asList(args));
		while (!argList.isEmpty())
		{
			String arg = argList.remove(0);
			if (arg.equals("-v"))
			{
				commandLine.setVerbosity(commandLine.getVerbosity() + 1);
			}
			else
			{
				if (commandLine.getSubCommand() != null)
				{
					throw new IllegalStateException("Only single sub command allowed.");
				}

				commandLine.setSubCommand(arg);
			}
		}

		return commandLine;
	}
}