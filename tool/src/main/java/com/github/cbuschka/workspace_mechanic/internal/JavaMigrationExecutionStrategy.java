package com.github.cbuschka.workspace_mechanic.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.Arrays;

public class JavaMigrationExecutionStrategy implements MigrationExecutionStrategy
{
	private static Logger log = LoggerFactory.getLogger(JavaMigrationExecutionStrategy.class);

	private static JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

	private MechanicContext context;

	public JavaMigrationExecutionStrategy(MechanicContext context)
	{
		this.context = context;
	}

	@Override
	public boolean handles(Migration migration)
	{
		File f = migration.getExecutable();
		return f != null && f.getName().endsWith(".java");
	}

	@Override
	public void execute(Migration migration) throws MigrationFailedException
	{
		File javaFile = migration.getExecutable();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

		JavaFileObject migrateSource = new FileJavaSource(javaFile);

		Iterable<? extends JavaFileObject> compilationUnits = Arrays
				.asList(migrateSource);
		JavaFileManager javaFileManager = new InMemoryJavaFileManager(
				compiler.getStandardFileManager(null, null, null));
		JavaCompiler.CompilationTask task = compiler.getTask(null, javaFileManager,
				diagnostics, null, null, compilationUnits);

		boolean success = task.call();
		for (Diagnostic diagnostic : diagnostics.getDiagnostics())
		{
			log.info(diagnostic.getCode());
			/*
			log.info(diagnostic.getKind());
			log.info(diagnostic.getPosition());
			log.info(diagnostic.getStartPosition());
			log.info(diagnostic.getEndPosition());
			log.info(diagnostic.getSource());
			*/
			log.info(diagnostic.getMessage(null));

		}
		log.info("Success: " + success);

		if (success)
		{
			try
			{

				Class
						.forName(
								migration.getName(),
								true,
								javaFileManager
										.getClassLoader(StandardLocation.CLASS_OUTPUT))
						.getDeclaredMethod("main", new Class[]{String[].class})
						.invoke(null, new Object[]{new String[0]});
			}
			catch (Exception ex)
			{
				throw new MigrationFailedException("Migration failed.", ex);
			}
		}
	}
}