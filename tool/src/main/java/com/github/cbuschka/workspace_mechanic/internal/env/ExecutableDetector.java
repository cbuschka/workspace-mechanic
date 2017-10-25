package com.github.cbuschka.workspace_mechanic.internal.env;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

@Component
public class ExecutableDetector
{
	public boolean isExecutableAvailable(String... command)
	{
		try
		{
			Process process = new ProcessBuilder().command(command).inheritIO().start();
			int exitCode = process.waitFor();
			return exitCode == 0;
		}
		catch (InterruptedException ex)
		{
			Thread.currentThread().interrupt();
			throw new UndeclaredThrowableException(ex);
		}
		catch (IOException ex)
		{
			if (ex.getMessage().contains("Cannot run program"))
			{
				return false;
			}

			throw new UndeclaredThrowableException(ex);
		}
	}

}
