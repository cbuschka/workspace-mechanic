package com.github.cbuschka.workspace_mechanic.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DirMigration implements Migration
{
	private File executable;
	private File dir;

	public DirMigration(File dir, File executable)
	{
		this.dir = dir;
		this.executable = executable;
	}

	@Override
	public String getName()
	{
		return this.dir.getName();
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
