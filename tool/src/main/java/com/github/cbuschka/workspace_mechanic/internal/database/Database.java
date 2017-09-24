package com.github.cbuschka.workspace_mechanic.internal.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class Database
{
	private static Logger log = LoggerFactory.getLogger(Database.class);
	private static final int NUM_TO_KEEP = 5;
	private static final String TS_PATTERN = "yyyyMMdd_hhmmss_SSS";
	private Pattern bakFilePattern = Pattern.compile("^state\\.json\\.[0-9_\\-\\.A-Fa-f]+$");
	private AtomicInteger seq = new AtomicInteger(1);
	private File dataFile;
	private File dbDir;
	private Data data = new Data();
	private MetaData metaData = new MetaData();
	private int flushCount = 0;
	private boolean dirty = false;

	private ObjectMapper objectMapper = new ObjectMapper();

	{
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public Database(File dbDir)
	{
		try
		{
			this.dbDir = dbDir;
			this.dataFile = FileUtils.getFile(this.dbDir, "state.json");
			File metaJsonFile = new File(this.dbDir, "meta.json");
			if (!this.dbDir.isDirectory() || !metaJsonFile.isFile())
			{
				initNewDb(metaJsonFile);
			}
			else
			{
				openDb(metaJsonFile);
			}
		}
		catch (IOException ex)
		{
			throw new UndeclaredThrowableException(ex);
		}
	}

	private void openDb(File metaJsonFile) throws IOException
	{
		this.metaData = this.objectMapper.readerFor(MetaData.class).readValue(metaJsonFile);
		if (1 != this.metaData.version)
		{
			throw new IllegalStateException("Version '" + this.metaData.version + "' is not supported.");
		}
		this.data = this.objectMapper.readerFor(Data.class).readValue(this.dataFile);

		gcStateFiles(NUM_TO_KEEP);
	}

	private void gcStateFiles(int numToKeep)
	{
		try
		{
			List<File> bakFiles = new ArrayList<>(Arrays.asList(this.dbDir.listFiles(file -> bakFilePattern.matcher(file.getName()).matches())));
			Collections.sort(bakFiles);
			System.err.println("Deleting " + (bakFiles.size() - numToKeep) + " of " + bakFiles.size());
			for (int i = 0; i < bakFiles.size() - numToKeep; ++i)
			{
				Path path = bakFiles.get(bakFiles.size() - i - 1).toPath();
				Files.delete(path);
			}
			bakFiles = new ArrayList<>(Arrays.asList(this.dbDir.listFiles(file -> file.getName().startsWith("state.json."))));
			System.err.println(bakFiles.size() + " file(s) left.");
		}
		catch (IOException ex)
		{
			throw new UndeclaredThrowableException(ex);
		}
	}

	private void initNewDb(File metaJsonFile) throws IOException
	{
		this.dbDir.mkdirs();
		this.objectMapper.writer().writeValue(metaJsonFile, this.metaData);
		this.dirty = true;
		flushIfDirty();
	}

	public void recordMigrationStarted(String migrationName, BigInteger checksum)
	{
		Entry entry = getOrCreateEntry(migrationName);
		entry.status = MigrationStatus.STARTED;
		entry.startTime = nowAsString();
		entry.endTime = null;
		entry.checksum = checksum.toString(16);
		this.dirty = true;
		flush();
	}

	public void recordMigrationFailed(String migrationName)
	{
		Entry entry = getOrCreateEntry(migrationName);
		entry.status = MigrationStatus.FAILED;
		entry.endTime = nowAsString();
		this.dirty = true;
		flush();
	}

	public void recordMigrationSucceeded(String migrationName)
	{
		Entry entry = getOrCreateEntry(migrationName);
		entry.status = MigrationStatus.SUCCEEDED;
		entry.endTime = nowAsString();
		this.dirty = true;
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

	public void close()
	{
		this.flush();
	}

	public void flush()
	{
		flushInternal(true);
	}

	private void flushInternal(boolean forceGc)
	{
		flushIfDirty();

		if (forceGc || flushCount > NUM_TO_KEEP)
		{
			gcStateFiles(NUM_TO_KEEP);
			this.flushCount = 0;
		}
	}

	private void flushIfDirty()
	{
		if (this.dirty)
		{
			try
			{
				String nowTs = new SimpleDateFormat(TS_PATTERN).format(new Date()) + "_" + Integer.toHexString(seq.getAndIncrement());
				File newFile = new File(this.dataFile.getParentFile(), this.dataFile.getName() + "." + nowTs);
				this.objectMapper.writer().writeValue(newFile, this.data);

				if (this.dataFile.isFile())
				{
					FileTime lastModTime = Files.getLastModifiedTime(this.dataFile.toPath());
					String lastModTs = new SimpleDateFormat(TS_PATTERN).format(new Date(lastModTime.toMillis())) + "_" + Integer.toHexString(seq.getAndIncrement());
					File bakFile = new File(this.dataFile.getParentFile(), this.dataFile.getName() + "." + lastModTs);
					Files.copy(this.dataFile.toPath(), bakFile.toPath());
				}
				Files.move(newFile.toPath(), this.dataFile.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
				this.flushCount++;
				this.dirty = false;
			}
			catch (IOException ex)
			{
				throw new UndeclaredThrowableException(ex);
			}
		}
	}

	private String nowAsString()
	{
		return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZZZ").format(new Date());
	}
}
