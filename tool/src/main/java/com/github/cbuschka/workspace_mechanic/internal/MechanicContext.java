package com.github.cbuschka.workspace_mechanic.internal;

public class MechanicContext
{
	private final MechanicConfig config;
	private boolean windows;

	private boolean bashAvailable;

	private boolean cmdExeAvailable;

	public MechanicContext(boolean windows, boolean bashAvailable, boolean cmdExeAvailable, MechanicConfig config)
	{
		this.cmdExeAvailable = cmdExeAvailable;
		this.config = config;
		this.windows = windows;
		this.bashAvailable = bashAvailable;
	}

	public boolean isCmdExeAvailable()
	{
		return cmdExeAvailable;
	}

	public boolean isWindows()
	{
		return windows;
	}

	public boolean isBashAvailable()
	{
		return bashAvailable;
	}

	public MechanicConfig getConfig()
	{
		return config;
	}
}
