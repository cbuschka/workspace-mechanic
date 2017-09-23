package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.MechanicContext;

public interface ScriptGenerator
{
	String getExt();

	String generate(String migrationName, String touchFilePath, boolean shallSucceed, MechanicContext context);
}
