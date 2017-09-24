package com.github.cbuschka.workspace_mechanic.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryMigrationSource implements MigrationSource {
    private static Logger log = LoggerFactory.getLogger(DirectoryMigrationSource.class);

    private File baseDir;
    private MechanicContext context;

    public DirectoryMigrationSource(File baseDir, MechanicContext context) {
        this.baseDir = baseDir;
        this.context = context;
    }

    @Override
    public List<Migration> getMigrations() {
        return collectMigrationsFrom(baseDir);
    }

    private List<Migration> collectMigrationsFrom(File baseDir) {
        List<Migration> migrations = new ArrayList<>();
        if (baseDir.isDirectory()) {
            for (File file : baseDir.listFiles()) {
                if (file.isDirectory()) {
                    File executable = getExecutable(file);
                    migrations.add(new DirMigration(file, executable));
                } else if (file.isFile()) {
                    migrations.add(new DirMigration(file.getParentFile(), file));
                } else {
                    throw new IllegalStateException("Unknown migration format, neither file nor folder: " + file.getAbsolutePath());
                }
            }
        }

        return migrations;
    }

    private File getExecutable(File dir) {
        if (this.context.isCmdExeAvailable()) {
            File executable = new File(dir, "migrate.bat");
            if (executable.isFile()) {
                return executable;
            }
        }

        if (this.context.isBashAvailable()) {
            File executable = new File(dir, "migrate.sh");
            if (executable.isFile()) {
                return executable;
            }
        }

        throw new IllegalStateException("Neither migrate.sh nor migrate.bat found in " + dir.getAbsolutePath() + ".");
    }
}
