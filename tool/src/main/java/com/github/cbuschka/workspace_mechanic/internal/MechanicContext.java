package com.github.cbuschka.workspace_mechanic.internal;

public class MechanicContext
{
	private final MechanicConfig config;
	private final boolean msys;
	private boolean windows;

	private boolean bashAvailable;

	private boolean cmdExeAvailable;

	private boolean cygwin;

	public MechanicContext(boolean windows, boolean cygwin, boolean msys, boolean bashAvailable, boolean cmdExeAvailable, MechanicConfig config)
	{
		this.cygwin = cygwin;
		this.msys = msys;
		this.cmdExeAvailable = cmdExeAvailable;
		this.config = config;
		this.windows = windows;
		this.bashAvailable = bashAvailable;
	}

	public boolean isCmdExeAvailable()
	{
		return cmdExeAvailable;
	}

	public boolean isCygwin() {
		return this.cygwin;
	}

	public boolean isWindows()
	{
		return windows;
	}

	public boolean isMsys() {
		return msys;
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
