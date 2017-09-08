package com.github.cbuschka.workspace_mechanic.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MigrationExecutor {
    private static Logger log = LoggerFactory.getLogger(MigrationExecutor.class);

    private MechanicContext context;

    public MigrationExecutor(MechanicContext context) {
        this.context = context;
    }

    public void execute(String migrationName, File executable, File dir) throws MigrationFailedException {
        try {
            log.debug("Executing migration {} (exec={}, cwd={})...", migrationName, executable.getPath(), dir.getPath());

            int exitCode = execute(executable, dir);

            log.debug("Executed migration {} (exec={}, cwd={}) with exitCode={}.", migrationName, executable.getPath(), dir.getPath(), exitCode);

            if (exitCode != 0) {
                throw new MigrationFailedException(String.format("Migration %s (exec=%s, cwd=%s) failed with exit code %d.", migrationName, executable.getPath(), dir.getPath(), exitCode));
            }
        } catch (InterruptedException | IOException ex) {
            throw new UndeclaredThrowableException(ex);
        }
    }

    private int execute(File executable, File dir) throws IOException, InterruptedException {
        if (executable.getName().endsWith(".bat")) {
            return executeWithCmdExe(executable, dir);
        } else {
            return executeWithBash(executable, dir);
        }
    }

    private int executeWithCmdExe(File executable, File dir) throws IOException, InterruptedException {
        Process process = new ProcessBuilder().directory(dir).command(executable.getAbsolutePath()).inheritIO().start();
        return process.waitFor();
    }

    private int executeWithBash(File executable, File dir) throws IOException, InterruptedException {

        if (context.isWindows()) {
            String executablePath = fixPath(executable);
            log.debug("Executable path: {}", executablePath);
            Process process = new ProcessBuilder().directory(dir).command("bash", "-xc", executablePath).inheritIO().start();
            return process.waitFor();
        } else {
            Process process = new ProcessBuilder().directory(dir).command("bash", "-xc", executable.getAbsolutePath()).inheritIO().start();
            return process.waitFor();
        }
    }

    private String fixPath(File executable) {

        if (context.isCygwin()) {
            String path = executable.getPath();
            path = path.replaceAll("^([A-Za-z])\\:", "/cygdrive/$1");
            path = path.replaceAll("\\\\+", "/");
            return path;

        } else if (context.isCygwin()) {
            String path = executable.getPath();
            path = path.replaceAll("^([A-Za-z])\\:", "/$1");
            path = path.replaceAll("\\\\+", "/");
            return path;

        } else {
            return executable.getPath();
        }
    }
}