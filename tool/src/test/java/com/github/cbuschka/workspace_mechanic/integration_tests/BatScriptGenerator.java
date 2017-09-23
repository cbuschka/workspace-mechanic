package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.MechanicContext;

public class BatScriptGenerator implements ScriptGenerator
{
	@Override
	public String generate(String migrationName, String touchFilePath, boolean shallSucceed, MechanicContext context)
	{
		String script = String.format("@ECHO ON\r\nECHO \"%s\"\r\nTYPE nul > \"%s\"\r\nEXIT %s", migrationName, touchFilePath, shallSucceed ? "0" : "1");

		return script;
	}
}
