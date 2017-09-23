package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.MechanicContext;

public class PythonScriptGenerator implements ScriptGenerator
{
	public static final PythonScriptGenerator INSTANCE = new PythonScriptGenerator();

	@Override
	public String getExt()
	{
		return "py";
	}

	@Override
	public String generate(String migrationName, String touchFilePath, boolean shallSucceed, MechanicContext context)
	{
		String script = String.format("from sys import exit\n" +
				" \n" +
				"print '%s'\n" +
				"open('%s', 'a').close()\n" +
				"\n" +
				"exit(%s)", migrationName, touchFilePath, shallSucceed ? "0" : "1");

		return script;
	}
}
