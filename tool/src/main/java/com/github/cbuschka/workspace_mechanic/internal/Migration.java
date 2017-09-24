package com.github.cbuschka.workspace_mechanic.internal;

import java.io.File;
import java.math.BigInteger;

public interface Migration
{
	File getExecutable();

	String getName();

	BigInteger getChecksum();
}
