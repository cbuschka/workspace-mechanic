package com.github.cbuschka.workspace_mechanic.internal.migration;

import com.github.cbuschka.workspace_mechanic.internal.env.OsDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectoryMigrationSource implements MigrationSource
{
	private static Logger log = LoggerFactory.getLogger(DirectoryMigrationSource.class);

	private File baseDir;
	private OsDetector osDetector;

	public DirectoryMigrationSource(File baseDir, OsDetector osDetector)
	{
		this.baseDir = baseDir;
		this.osDetector = osDetector;
	}

	@Override
	public List<Migration> getMigrations()
	{
		if (!baseDir.isDirectory())
		{
			return Collections.emptyList();
		}

		return collectMigrationsFrom(baseDir);
	}

	private List<Migration> collectMigrationsFrom(File baseDir)
	{
		List<Migration> migrations = new ArrayList<>();
		for (File file : baseDir.listFiles())
		{
			if (file.isDirectory())
			{
				File executable = getExecutable(file);
				String name = basenameWithoutExt(file);
				migrations.add(new DirMigration(name, file, executable));
			}
			else if (file.isFile())
			{
				String name = basenameWithoutExt(file);
				migrations.add(new DirMigration(name, file.getParentFile(), file));
			}
			else
			{
				throw new IllegalStateException("Unknown migration format, neither file nor folder: " + file.getAbsolutePath());
			}
		}

		return migrations;
	}

	private String basenameWithoutExt(File file)
	{
		String fileName = file.getName();
		int lastDotPos = fileName.lastIndexOf('.');
		if (lastDotPos != -1)
		{
			return fileName.substring(0, lastDotPos);
		}

		return fileName;
	}

	private File getExecutable(File dir)
	{
		if (this.osDetector.isWindows())
		{
			File executable = new File(dir, "migrate.bat");
			if (executable.isFile())
			{
				return executable;
			}
		}

		File executable = new File(dir, "migrate.sh");
		if (executable.isFile())
		{
			return executable;
		}

		throw new IllegalStateException("Neither migrate.sh nor migrate.bat found in " + dir.getAbsolutePath() + ".");
	}
}
