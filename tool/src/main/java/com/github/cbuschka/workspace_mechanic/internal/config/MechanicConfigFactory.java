package com.github.cbuschka.workspace_mechanic.internal.config;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Configuration
public class MechanicConfigFactory
{
	@Bean
	public MechanicConfig mechanicConfig()
	{
		File baseDir = new File(System.getProperty("user.dir"), "");

		File dotMechanicDir = FileUtils.getFile(baseDir, ".mechanic");
		if (dotMechanicDir.isDirectory())
		{
			baseDir = dotMechanicDir;
		}
		File migrationsDir = FileUtils.getFile(baseDir, "migrations.d");
		List<File> migrationDirs = Arrays.asList(migrationsDir);

		File workDir = FileUtils.getFile(baseDir, "work");
		File dbDir = FileUtils.getFile(baseDir, "db");

		return new MechanicConfig(migrationDirs, workDir, dbDir);
	}
}
