package com.github.cbuschka.workspace_mechanic.internal.database;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MigrationEvent
{
	private long id;

	private long eventTime;

	private String migrationName;

	private String eventType;

	public MigrationEvent(long id, long eventTime, String migrationName, String eventType)
	{
		this.id = id;
		this.eventTime = eventTime;
		this.migrationName = migrationName;
		this.eventType = eventType;
	}

	public long getId()
	{
		return id;
	}

	public String getEventTimeAsString()
	{
		return Instant.ofEpochMilli(this.eventTime).atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	public String getMigrationName()
	{
		return migrationName;
	}

	public String getEventType()
	{
		return eventType;
	}
}
