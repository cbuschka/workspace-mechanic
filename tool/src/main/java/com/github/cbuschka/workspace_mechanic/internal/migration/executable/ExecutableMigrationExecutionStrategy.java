package com.github.cbuschka.workspace_mechanic.internal.migration.executable;

import com.github.cbuschka.workspace_mechanic.internal.migration.Migration;
import com.github.cbuschka.workspace_mechanic.internal.migration.MigrationExecutionStrategy;
import com.github.cbuschka.workspace_mechanic.internal.migration.MigrationFailedException;
import com.github.cbuschka.workspace_mechanic.internal.env.OsDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

@Component
public class ExecutableMigrationExecutionStrategy implements MigrationExecutionStrategy
{
	private static Logger log = LoggerFactory.getLogger(ExecutableMigrationExecutionStrategy.class);

	@Autowired
	private OsDetector osDetector;

	@Override
	public boolean handles(Migration migration)
	{
		File executable = migration.getExecutable();
		return executable.isFile() && executable.canExecute();
	}

	@Override
	public void execute(Migration migration) throws MigrationFailedException
	{
		execute(migration.getName(), migration.getExecutable(), migration.getExecutable().getParentFile());
	}

	private void execute(String migrationName, File executable, File dir) throws MigrationFailedException
	{
		try
		{
			log.debug("Executing migration {} (exec={}, cwd={})...", migrationName, executable.getPath(), dir.getPath());

			int exitCode = execute(executable, dir);

			log.debug("Executed migration {} (exec={}, cwd={}) with exitCode={}.", migrationName, executable.getPath(), dir.getPath(), exitCode);

			if (exitCode != 0)
			{
				throw new MigrationFailedException(String.format("Migration %s (exec=%s, cwd=%s) failed with exit code %d.", migrationName, executable.getPath(), dir.getPath(), exitCode));
			}
		}
		catch (InterruptedException | IOException ex)
		{
			throw new UndeclaredThrowableException(ex);
		}
	}

	private int execute(File executable, File dir) throws IOException, InterruptedException
	{
		if (executable.getName().endsWith(".bat"))
		{
			return executeWithCmdExe(executable, dir);
		}
		else
		{
			return executeWithBash(executable, dir);
		}
	}

	private int executeWithCmdExe(File executable, File dir) throws IOException, InterruptedException
	{
		Process process = new ProcessBuilder().directory(dir).command(executable.getAbsolutePath()).inheritIO().start();
		return process.waitFor();
	}

	private int executeWithBash(File executable, File dir) throws IOException, InterruptedException
	{

		if (this.osDetector.isWindows())
		{
			String executablePath = fixPath(executable);
			log.debug("Executable path: {}", executablePath);
			Process process = new ProcessBuilder().directory(dir).command("bash", "-xc", executablePath).inheritIO().start();
			return process.waitFor();
		}
		else
		{
			Process process = new ProcessBuilder().directory(dir).command("bash", "-xc", executable.getAbsolutePath()).inheritIO().start();
			return process.waitFor();
		}
	}

	private String fixPath(File executable)
	{

		if (this.osDetector.isWindows() && this.osDetector.isUnixoidUserlandOnWindows())
		{
			String path = executable.getPath();
			path = path.replaceAll("^([A-Za-z])\\:", "/cygdrive/$1");
			path = path.replaceAll("\\\\+", "/");
			return path;

		}
		else
		{
			return executable.getPath();
		}
	}


}