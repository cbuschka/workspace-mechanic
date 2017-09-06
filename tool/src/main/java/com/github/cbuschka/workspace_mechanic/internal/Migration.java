package com.github.cbuschka.workspace_mechanic.internal;

import java.io.IOException;
import java.io.InputStream;

public interface Migration
{
	MigrationType getType();

	String getName();

	InputStream getInputStream() throws IOException;
}
