package com.github.cbuschka.workspace_mechanic.internal.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class Database
{
	@Autowired
	private DataSource dataSource;

	public void recordMigrationStarted(String migrationName, BigInteger checksum)
	{
		insertMigrationEvent(migrationName, "MIGRATION_STARTED");
	}

	private void insertMigrationEvent(String migrationName, String eventType)
	{
		long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		new JdbcTemplate(dataSource).update("INSERT INTO migration_event ( event_time, migration_name, event_type )\n"
						+ "	VALUES ( ?, ?, ? )",
				new Object[]{now, migrationName, eventType});
	}

	public void recordMigrationFailed(String migrationName)
	{
		insertMigrationEvent(migrationName, "MIGRATION_FAILED");
	}

	public void recordMigrationSucceeded(String migrationName)
	{
		insertMigrationEvent(migrationName, "MIGRATION_SUCCEEDED");
	}

	private String getLatestEventTypeFor(String migrationName)
	{
		List<String> eventTypes = new JdbcTemplate(this.dataSource).queryForList("SELECT event_type FROM ( SELECT * FROM migration_event WHERE migration_name = ? ORDER BY id DESC) LIMIT 1", String.class,
				new Object[]{migrationName});
		if (eventTypes.isEmpty())
		{
			return null;
		}

		return eventTypes.get(0);
	}

	public boolean hasSucceeded(String migrationName)
	{
		return "MIGRATION_SUCCEEDED".equals(getLatestEventTypeFor(migrationName));
	}

	public boolean hasFailed(String migrationName)
	{
		return "MIGRATION_FAILED".equals(getLatestEventTypeFor(migrationName));
	}

	public List<MigrationEvent> getMigrationEvents(boolean desc)
	{
		List<MigrationEvent> events = new JdbcTemplate(this.dataSource).query("SELECT id, event_time, migration_name, event_type FROM migration_event ORDER BY event_time DESC",
				(rs, rowNum) -> new MigrationEvent(rs.getLong("id"),
						rs.getLong("event_time"),
						rs.getString("migration_name"),
						rs.getString("event_type")));
		return events;
	}
}
