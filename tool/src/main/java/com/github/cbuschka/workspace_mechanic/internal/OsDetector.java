package com.github.cbuschka.workspace_mechanic.internal;

import java.util.function.Supplier;

public class OsDetector
{
	private Supplier<String> osNameSupplier;

	public OsDetector()
	{
		this(() -> System.getProperty("os.name"));
	}

	public OsDetector(Supplier<String> osNameSupplier)
	{
		this.osNameSupplier = osNameSupplier;
	}

	public boolean isWindows()
	{
		return getLowerOsName().contains("windows");
	}

	private String getLowerOsName()
	{
		String osName = osNameSupplier.get().toLowerCase();
		return osName;
	}
}
