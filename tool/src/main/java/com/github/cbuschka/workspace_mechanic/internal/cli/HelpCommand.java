package com.github.cbuschka.workspace_mechanic.internal.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command
{
	@Autowired
	private Console console;

	@Override
	public String getCommandName()
	{
		return "help";
	}

	@Override
	public int run()
	{
		this.console.write("help");
		return 0;
	}
}
