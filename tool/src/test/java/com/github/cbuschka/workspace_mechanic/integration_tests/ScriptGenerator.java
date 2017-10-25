package com.github.cbuschka.workspace_mechanic.integration_tests;

public interface ScriptGenerator
{
	String getExt();

	String generate(String migrationName, String touchFilePath, boolean shallSucceed);
}
