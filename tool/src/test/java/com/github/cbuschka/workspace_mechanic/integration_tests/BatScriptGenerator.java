package com.github.cbuschka.workspace_mechanic.integration_tests;

public class BatScriptGenerator implements ScriptGenerator
{
	public static final BatScriptGenerator INSTANCE = new BatScriptGenerator();

	@Override
	public String getExt()
	{
		return "bat";
	}

	@Override
	public String generate(String migrationName, String touchFilePath, boolean shallSucceed)
	{
		String script = String.format("@ECHO ON\r\nECHO \"%s\"\r\nTYPE nul > \"%s\"\r\nEXIT %s", migrationName, touchFilePath, shallSucceed ? "0" : "1");

		return script;
	}
}
