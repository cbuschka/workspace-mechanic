package com.github.cbuschka.workspace_mechanic.integration_tests;

import org.springframework.stereotype.Component;

@Component
public class AntScriptGenerator implements ScriptGenerator
{
	@Override
	public String getExt()
	{
		return "xml";
	}

	@Override
	public String generate(String migrationName, String touchFilePath, boolean shallSucceed)
	{
		String script = String.format("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
				"<project default=\"migrate\" basedir=\".\">\n" +
				" <target name=\"migrate\">\n" +
				"  <echo>%s</echo>\n" +
				"  <touch file=\"%s\"/>\n" +
				"  %s\n" +
				" </target>\n" +
				"</project>", migrationName, touchFilePath, !shallSucceed ? "<fail>failure requested</fail>" : "");

		return script;
	}
}
