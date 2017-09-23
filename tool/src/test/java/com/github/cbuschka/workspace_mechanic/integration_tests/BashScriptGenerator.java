package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.MechanicContext;

public class BashScriptGenerator implements ScriptGenerator
{
	@Override
	public String generate(String migrationName, String touchFilePath, boolean shallSucceed, MechanicContext context)
	{
		String script = String.format("#!/bin/bash\necho '%s'\ntouch %s\nexit %s", migrationName, fixBashPath(touchFilePath, context), shallSucceed ? "0" : "1");

		return script;
	}


	private String fixBashPath(String path, MechanicContext context) {
		if (context.isWindows()) {
			return path.replace("\\", "\\\\");
		}

		return path;
	}

}
