package com.github.cbuschka.workspace_mechanic.internal;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

public class MechanicContextFactory
{
	private OsDetector osDetector = new OsDetector();

	public MechanicContext build(MechanicConfig config)
	{
		return new MechanicContext(osDetector.isWindows(), bashAvailable(), cmdExeAvailable(), config);
	}

	private boolean cmdExeAvailable()
	{
		return isExecutableAvailable("cmd.exe");
	}

	private boolean bashAvailable()
	{
		return isExecutableAvailable("bash", "-c", "exit 0");
	}

	private boolean cmdAvailable()
	{
		return isExecutableAvailable("cmd.exe");
	}

	private boolean isExecutableAvailable(String... command)
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
