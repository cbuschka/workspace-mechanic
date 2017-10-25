package com.github.cbuschka.workspace_mechanic.internal.cli;

public class CommandLine
{
	private int verbosity = 0;
	private String subCommand;

	public CommandLine()
	{
	}

	public int getVerbosity()
	{
		return verbosity;
	}

	public void setVerbosity(int verbosity)
	{
		this.verbosity = verbosity;
	}

	public String getSubCommand()
	{
		return subCommand;
	}

	public void setSubCommand(String subCommand)
	{
		this.subCommand = subCommand;
	}
}
