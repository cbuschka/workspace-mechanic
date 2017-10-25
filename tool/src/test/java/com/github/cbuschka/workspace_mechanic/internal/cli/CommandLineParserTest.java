package com.github.cbuschka.workspace_mechanic.internal.cli;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CommandLineParserTest
{
	@InjectMocks
	private CommandLineParser parser;

	@Test
	public void all()
	{
		CommandLine commandLine = parser.parse("-v", "-v", "migrate");
		assertThat(commandLine.getSubCommand(), is("migrate"));
		assertThat(commandLine.getVerbosity(), is(2));
	}
}