package com.github.cbuschka.workspace_mechanic.internal;

import org.python.core.PyBuiltinFunctionNarrow;
import org.python.core.PyException;
import org.python.core.PyModule;
import org.python.core.PyObject;
import org.python.core.PyStringMap;
import org.python.core.PySystemState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class JythonMigrationExecutionStrategy implements MigrationExecutionStrategy
{
	private static Logger log = LoggerFactory.getLogger(JythonMigrationExecutionStrategy.class);

	private MechanicContext context;

	public JythonMigrationExecutionStrategy(MechanicContext context)
	{
		this.context = context;
	}

	@Override
	public boolean handles(Migration migration)
	{
		File f = getFileFrom(migration);
		return f != null && f.getName().endsWith(".py");
	}

	private File getFileFrom(Migration migration)
	{
		if (migration instanceof FileMigration)
		{
			FileMigration fileMigration = (FileMigration) migration;
			return fileMigration.getFile();
		}
		else if (migration instanceof DirMigration)
		{
			DirMigration fileMigration = (DirMigration) migration;
			return fileMigration.getExecutable();
		}

		return null;
	}

	@Override
	public void execute(Migration migration) throws MigrationFailedException
	{
		File pyFile = getFileFrom(migration);

		System.setProperty("python.import.site", "false");
		org.python.util.PythonInterpreter interp = new org.python.util.PythonInterpreter();
		PySystemState systemState = interp.getSystemState();
		systemState.dont_write_bytecode = true;
		PyStringMap o = (PyStringMap) systemState.modules;
		PyModule fakeSys = new PyModule();
		o.__setitem__("sys", fakeSys);
		fakeSys.__dict__ = new PyStringMap();
		Integer[] exitCodeHolder = new Integer[]{null};
		((PyStringMap) fakeSys.__dict__).__setitem__("exit", new PyBuiltinFunctionNarrow("exit", 1, 1, null)
		{
			@Override
			public PyObject __call__(PyObject arg1)
			{
				exitCodeHolder[0] = Integer.valueOf(arg1.toString());
				systemState.callExitFunc();
				return null;
			}
		});
		try
		{
			interp.execfile(pyFile.getAbsolutePath());
		}
		catch (PyException ex)
		{
			throw new MigrationFailedException("", ex);
		}

		if (exitCodeHolder[0] != null && exitCodeHolder[0].intValue() != 0)
		{
			throw new MigrationFailedException("Migration failed with exit code " + exitCodeHolder[0]);
		}
	}
}