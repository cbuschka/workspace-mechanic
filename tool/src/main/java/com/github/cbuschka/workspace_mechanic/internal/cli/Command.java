package com.github.cbuschka.workspace_mechanic.internal.cli;

public interface Command
{
	String getCommandName();

	int run();
}
