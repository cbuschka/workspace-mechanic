package com.github.cbuschka.workspace_mechanic.internal.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Database
{
	private File dataFile;
	private File dbDir;
	private Data data = new Data();

	private ObjectMapper objectMapper = new ObjectMapper();

	{
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public Database(File dbDir)
	{
		try
		{
			this.dbDir = dbDir;
			this.dbDir.mkdirs();
			this.dataFile = FileUtils.getFile(this.dbDir, "state.json");
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
		entry.startTime = nowAsString();
		entry.endTime = null;
		flush();
	}

	public void recordMigrationFailed(String migrationName)
	{
		Entry entry = getOrCreateEntry(migrationName);
		entry.status = MigrationStatus.FAILED;
		entry.endTime = nowAsString();
		flush();
	}

	public void recordMigrationSucceeded(String migrationName)
	{
		Entry entry = getOrCreateEntry(migrationName);
		entry.status = MigrationStatus.SUCCEEDED;
		entry.endTime = nowAsString();
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

	public boolean hasSucceeded(String migrationName)
	{
		Entry entry = this.data.migrations.get(migrationName);
		return entry != null && entry.status == MigrationStatus.SUCCEEDED;
	}

	public boolean hasFailed(String migrationName)
	{

		Entry entry = this.data.migrations.get(migrationName);
		return entry != null && entry.status == MigrationStatus.FAILED;
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

	private String nowAsString()
	{
		return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZZZ").format(new Date());
	}
}
