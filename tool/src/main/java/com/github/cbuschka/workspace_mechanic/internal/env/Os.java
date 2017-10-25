package com.github.cbuschka.workspace_mechanic.internal.env;

public class Os
{
	private boolean windows;
	private boolean unixoidUserlandOnWindows;

	public Os(boolean windows, boolean unixoidUserlandOnWindows)
	{
		this.windows = windows;
		this.unixoidUserlandOnWindows = unixoidUserlandOnWindows;
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
}
