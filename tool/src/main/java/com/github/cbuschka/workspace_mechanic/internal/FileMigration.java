package com.github.cbuschka.workspace_mechanic.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileMigration implements Migration
{
	private MigrationType type;
	private File file;

	public FileMigration(MigrationType type, File file)
	{
		this.type = type;
		this.file = file;
	}

	@Override
	public String getName()
	{
		return this.file.getName();
	}

	@Override
	public MigrationType getType()
	{
		return type;
	}

	public File getFile()
	{
		return file;
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		return new BufferedInputStream(new FileInputStream(this.file));
	}
}
