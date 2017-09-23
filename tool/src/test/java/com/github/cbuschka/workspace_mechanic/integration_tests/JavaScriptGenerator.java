package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.MechanicContext;

public class JavaScriptGenerator implements ScriptGenerator
{
	public static final JavaScriptGenerator INSTANCE = new JavaScriptGenerator();

	@Override
	public String getExt()
	{
		return "java";
	}

	@Override
	public String generate(String migrationName, String touchFilePath, boolean shallSucceed, MechanicContext context)
	{
		String script = String.format("import java.io.File;\n" +
				"import java.nio.file.Files;\n" +
				"public class %s {\n" +
				"public static void main(String... args) throws Exception {\n" +
				"System.err.println(\"%s\");\n" +
				"File file = new File(\"%s\");\n" +
				"Files.createDirectories(file.getParentFile().toPath());\n" +
				"Files.createFile(file.toPath());\n" +
				"%s\n}\n" +
				"}", migrationName, migrationName, touchFilePath, !shallSucceed ? "throw new RuntimeException(\"fail requested\");" : "");

		return script;
	}
}
