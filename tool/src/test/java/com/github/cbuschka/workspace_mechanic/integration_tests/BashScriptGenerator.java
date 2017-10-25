package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.env.OsDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BashScriptGenerator implements ScriptGenerator
{
	@Autowired
	private OsDetector osDetector;

	@Override
	public String generate(String migrationName, String touchFilePath, boolean shallSucceed)
	{
		String script = String.format("#!/bin/bash\necho '%s'\ntouch %s\nexit %s", migrationName, fixBashPath(touchFilePath), shallSucceed ? "0" : "1");

		return script;
	}

	@Override
	public String getExt()
	{
		return "sh";
	}


	private String fixBashPath(String path)
	{
		if (this.osDetector.isWindows())
		{
			return path.replace("\\", "\\\\");
		}

		return path;
	}

}
