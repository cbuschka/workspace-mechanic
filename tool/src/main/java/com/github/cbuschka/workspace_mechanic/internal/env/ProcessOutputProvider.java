package com.github.cbuschka.workspace_mechanic.internal.env;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ProcessOutputProvider
{
	public String getOutputOf(List<String> commandLine, String defaultOutput)
	{
		try
		{
			ProcessBuilder pb = new ProcessBuilder(commandLine);
			String output = IOUtils.toString(pb.start().getInputStream());
			return output;
		}
		catch (IOException ex)
		{
			return defaultOutput;
		}

	}
}
