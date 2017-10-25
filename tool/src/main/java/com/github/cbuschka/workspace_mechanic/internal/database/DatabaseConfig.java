package com.github.cbuschka.workspace_mechanic.internal.database;

import com.github.cbuschka.workspace_mechanic.internal.config.MechanicConfig;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.File;

@Configuration
public class DatabaseConfig
{
	@Autowired
	private MechanicConfig mechanicConfig;

	@Bean
	public DataSource dataSource()
	{
		DriverManagerDataSource ds = new DriverManagerDataSource();

		File dbDir = mechanicConfig.getDbDir();
		dbDir.mkdirs();

		String h2JdbcUri = String.format("jdbc:h2:%s", new File(dbDir, "state").getAbsolutePath());
		ds.setDriverClassName("org.h2.Driver");
		ds.setUrl(h2JdbcUri);
		ds.setUsername("sa");
		ds.setPassword("");

		migrate(ds);

		return ds;
	}

	private void migrate(DataSource dataSource)
	{
		Flyway flyway = new Flyway();
		flyway.setEncoding("UTF-8");
		flyway.setLocations("classpath:/db/migrations/");
		flyway.setSqlMigrationPrefix("");
		flyway.setSqlMigrationSuffix(".sql");
		flyway.setDataSource(dataSource);
		flyway.migrate();
	}
}
