package com.github.cbuschka.workspace_mechanic.internal;

import java.math.BigInteger;

public interface Migration
{
	String getName();

	BigInteger getChecksum();
}
