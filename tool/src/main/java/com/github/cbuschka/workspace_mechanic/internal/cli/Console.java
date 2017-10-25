package com.github.cbuschka.workspace_mechanic.internal.cli;

import org.springframework.stereotype.Component;

@Component
public class Console
{
	public void write(String line)
	{
		System.out.println(line);
	}
}
