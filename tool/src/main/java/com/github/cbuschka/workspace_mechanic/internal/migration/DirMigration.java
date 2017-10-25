package com.github.cbuschka.workspace_mechanic.internal.migration;

import com.github.cbuschka.workspace_mechanic.internal.migration.DigestUtils;
import com.github.cbuschka.workspace_mechanic.internal.migration.Migration;

import java.io.File;
import java.math.BigInteger;

public class DirMigration implements Migration
{
	private String name;
	private File executable;
	private File dir;

	public DirMigration(String name, File dir, File executable)
	{
		this.name = name;
		this.dir = dir;
		this.executable = executable;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	public File getExecutable()
	{
		return executable;
	}

	public File getDir()
	{
		return dir;
	}

    @Override
    public BigInteger getChecksum() {
        return DigestUtils.getChecksum(this.executable);
    }
}
