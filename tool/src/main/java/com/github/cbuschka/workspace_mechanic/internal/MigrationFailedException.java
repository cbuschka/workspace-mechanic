package com.github.cbuschka.workspace_mechanic.internal;

public class MigrationFailedException extends Exception
{
	public MigrationFailedException(String message)
	{
		super(message);
	}

	public MigrationFailedException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
