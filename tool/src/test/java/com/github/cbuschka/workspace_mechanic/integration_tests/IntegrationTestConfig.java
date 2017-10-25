package com.github.cbuschka.workspace_mechanic.integration_tests;

import com.github.cbuschka.workspace_mechanic.internal.config.MechanicConfig;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.List;

@Configuration
@ComponentScan
public class IntegrationTestConfig
{
	@PostConstruct
	protected void init()
	{
	}

	@Primary
	@Bean
	public MechanicConfig mechanicConfigForTest()
	{
		File baseDir = new File("/tmp", "workspace-mechanic-" + System.currentTimeMillis() + "-itest");
		baseDir.mkdirs();

		File dotMechanicDir = FileUtils.getFile(baseDir, ".mechanic");
		if (dotMechanicDir.isDirectory())
		{
			baseDir = dotMechanicDir;
		}
		File migrationsDir = FileUtils.getFile(baseDir, "migrations.d");
		migrationsDir.mkdirs();
		List<File> migrationDirs = Arrays.asList(migrationsDir);

		File workDir = FileUtils.getFile(baseDir, "work");
		workDir.mkdirs();
		File dbDir = FileUtils.getFile(baseDir, "db");

		return new MechanicConfig(migrationDirs, workDir, dbDir);
	}


}
