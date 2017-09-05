package com.github.cbuschka.workspace_mechanic.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

public class Database
{
	private File dataFile;
	private File baseDir;
	private Data data = new Data();

	private ObjectMapper objectMapper = new ObjectMapper();

	{
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public static class Data
	{
		public String version = "1";

		public Map<String, Entry> migrations = new HashMap<>();
	}

	public enum MigrationStatus
	{
		STARTED, FAILED, SUCCEEDED;
	}

	public static class Entry
	{
		public MigrationStatus status;
	}

	public Database(File baseDir)
	{
		try
		{
			this.baseDir = baseDir;
			this.baseDir.mkdirs();
			this.dataFile = new File(this.baseDir, "status.dat");
			if (this.dataFile.isFile())
			{
				this.data = this.objectMapper.readerFor(Data.class).readValue(this.dataFile);
			}
		}
		catch (IOException ex)
		{
			throw new UndeclaredThrowableException(ex);
		}
	}

	public void recordMigrationStarted(String migrationName)
	{
		Entry entry = getOrCreateEntry(migrationName);
		entry.status = MigrationStatus.STARTED;
		flush();
	}

	public void recordMigrationFailed(String migrationName)
	{
		Entry entry = getOrCreateEntry(migrationName);
		entry.status = MigrationStatus.FAILED;
		flush();
	}

	public void recordMigrationSucceeded(String migrationName)
	{
		Entry entry = getOrCreateEntry(migrationName);
		entry.status = MigrationStatus.SUCCEEDED;
		flush();
	}

	private Entry getOrCreateEntry(String migrationName)
	{
		Entry entry = this.data.migrations.get(migrationName);
		if (entry == null)
		{
			entry = new Entry();
			this.data.migrations.put(migrationName, entry);
		}
		return entry;
	}

	public boolean isExecuted(String migrationName)
	{
		Entry entry = this.data.migrations.get(migrationName);
		return entry != null && (entry.status == MigrationStatus.SUCCEEDED || entry.status == MigrationStatus.FAILED);
	}

	public void flush()
	{
		try
		{
			this.objectMapper.writer().writeValue(this.dataFile, this.data);
		}
		catch (IOException ex)
		{
			throw new UndeclaredThrowableException(ex);
		}
	}
}
