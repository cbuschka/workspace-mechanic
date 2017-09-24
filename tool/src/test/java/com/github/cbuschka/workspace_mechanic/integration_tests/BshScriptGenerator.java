package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.MechanicContext;

public class BshScriptGenerator implements ScriptGenerator
{
	public static final BshScriptGenerator INSTANCE = new BshScriptGenerator();

	@Override
	public String getExt()
	{
		return "bsh";
	}

	@Override
	public String generate(String migrationName, String touchFilePath, boolean shallSucceed, MechanicContext context)
	{
		String script = String.format("import java.io.File;\n" +
				"import java.nio.file.Files;\n" +
				"import java.nio.file.attribute.FileAttribute;\n" +
				"print(\"%s\");\n" +
				"File file = new File(\"%s\");\n" +
				"Files.createDirectories(file.getParentFile().toPath(), new FileAttribute[0]);\n" +
				"Files.createFile(file.toPath(), new FileAttribute[0]);\n" +
				"%s\n", migrationName, touchFilePath, !shallSucceed ? "throw new java.lang.RuntimeException(\"failure requested\");" : "");

		return script;
	}
}
