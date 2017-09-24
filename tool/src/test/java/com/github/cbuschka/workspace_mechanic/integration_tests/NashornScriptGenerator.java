package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.MechanicContext;

public class NashornScriptGenerator implements ScriptGenerator
{
	public static final NashornScriptGenerator INSTANCE = new NashornScriptGenerator();

	@Override
	public String getExt()
	{
		return "jjs";
	}

	@Override
	public String generate(String migrationName, String touchFilePath, boolean shallSucceed, MechanicContext context)
	{
		String script = String.format(
				"print(\"%s\");\n" +
						"var file = new java.io.File(\"%s\");\n" +
						"java.nio.file.Files.createDirectories(file.getParentFile().toPath(), []);\n" +
						"java.nio.file.Files.createFile(file.toPath(), []);\n" +
						"%s\n", migrationName, touchFilePath, !shallSucceed ? "throw new java.lang.RuntimeException(\"failure requested\");" : "");

		return script;
	}
}
