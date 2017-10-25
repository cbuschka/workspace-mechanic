package com.github.cbuschka.workspace_mechanic.internal.env;

import org.python.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.function.Supplier;

@Component
public class OsDetector
{
	@Autowired
	private ProcessOutputProvider processOutputProvider;

	private Supplier<String> osNameSupplier = () -> System.getProperty("os.name");

	private boolean windows;
	private boolean unixoidUserlandOnWindows;

	public OsDetector()
	{
	}

	public boolean isUnixoid()
	{
		return !this.windows;
	}

	public boolean isWindows()
	{
		return windows;
	}

	public boolean isUnixoidUserlandOnWindows()
	{
		return unixoidUserlandOnWindows;
	}

	@PostConstruct
	@VisibleForTesting
	void init()
	{
		this.windows = getLowerOsName().contains("windows");
		this.unixoidUserlandOnWindows = detectUnixoidUserlandOnWindows();
	}

	private boolean detectUnixoidUserlandOnWindows()
	{
		String unameOutput = this.processOutputProvider.getOutputOf(Arrays.asList("uname"), "");
		return unameOutput.contains("cygwin") || unameOutput.contains("mingw");
	}

	private String getLowerOsName()
	{
		String osName = osNameSupplier.get().toLowerCase();
		return osName;
	}
}
