package com.github.cbuschka.workspace_mechanic.internal;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

class InMemoryJavaFileManager extends
		ForwardingJavaFileManager<JavaFileManager>
{

	private Map<Location, ClassLoader> classLoaders = new HashMap<>();

	private Map<String, JavaFileObject> files = new HashMap<>();

	public InMemoryJavaFileManager(JavaFileManager fileManager)
	{
		super(fileManager);

		this.classLoaders.put(StandardLocation.CLASS_OUTPUT,
				new URLClassLoader(new URL[0])
				{
					@Override
					protected Class<?> findClass(String className)
							throws ClassNotFoundException
					{
						String path = pathForClassName(className);
						byte[] b = ((InMemoryJavaFileObject) files.get(path))
								.getData();
						return super.defineClass(className, b, 0, b.length);
					}
				});
	}

	public ClassLoader getClassLoader(Location location)
	{
		ClassLoader classLoader = this.classLoaders.get(location);
		if (classLoader != null)
		{
			return classLoader;
		}

		return super.getClassLoader(location);
	}

	@Override
	public JavaFileObject getJavaFileForInput(Location location,
											  String className, Kind kind) throws IOException
	{
		if (kind == Kind.CLASS)
		{
			String path = pathForClassName(className);
			if (this.files.containsKey(path))
			{
				System.err.println(path + " for input");
				return this.files.get(path);
			}
		}

		return super.getJavaFileForInput(location, className, kind);
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location,
											   String className, Kind kind, FileObject sibling) throws IOException
	{

		if (kind == Kind.CLASS)
		{
			String path = pathForClassName(className);
			InMemoryJavaFileObject classFile = new InMemoryJavaFileObject(path,
					kind, null);
			System.err.println(path + " for output");
			this.files.put(path, classFile);
			return classFile;
		}

		return super.getJavaFileForOutput(location, className, kind, sibling);
	}

	private static String pathForClassName(String className)
	{
		return className.replace('.', '/') + ".class";
	}

}