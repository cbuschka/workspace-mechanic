package com.github.cbuschka.workspace_mechanic.internal;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

public class MechanicContextFactory {
    private OsDetector osDetector = new OsDetector();

    public MechanicContext build(MechanicConfig config) {
        return new MechanicContext(osDetector.isWindows(), isCygwin(), isMsys(), bashAvailable(), cmdExeAvailable(), config);
    }

    private boolean isMsys() {
        return getUnameIfAvailable().toLowerCase().contains("mingw");
    }

    private boolean isCygwin() {
        return getUnameIfAvailable().toLowerCase().contains("cygwin");
    }

    private boolean bashAvailable() {
        return isExecutableAvailable("bash", "-c", "exit 0");
    }

    private boolean cmdExeAvailable() {
        return isExecutableAvailable("cmd.exe", "/C", "exit");
    }

    private String getUnameIfAvailable() {
        try {
            ProcessBuilder pb = new ProcessBuilder("uname");
            String output = IOUtils.toString(pb.start().getInputStream());
            return output;
        } catch (IOException ex) {
            return "";
        }
    }

    private boolean isExecutableAvailable(String... command) {
        try {
            Process process = new ProcessBuilder().command(command).inheritIO().start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new UndeclaredThrowableException(ex);
        } catch (IOException ex) {
            if (ex.getMessage().contains("Cannot run program")) {
                return false;
            }

            throw new UndeclaredThrowableException(ex);
        }
    }
}
