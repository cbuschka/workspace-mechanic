package com.github.cbuschka.workspace_mechanic.internal.database;

import java.util.HashMap;
import java.util.Map;

public class Data
{
	public String version = "1";

	public Map<String, Entry> migrations = new HashMap<>();
}
