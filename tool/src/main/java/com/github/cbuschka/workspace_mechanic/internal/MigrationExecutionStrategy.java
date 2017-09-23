package com.github.cbuschka.workspace_mechanic.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

public interface MigrationExecutionStrategy
{
	boolean handles(Migration migration);

	void execute(Migration migration) throws MigrationFailedException;
}