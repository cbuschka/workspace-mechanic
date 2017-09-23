package com.github.cbuschka.workspace_mechanic.internal;

import org.apache.commons.io.FileUtils;

import javax.tools.SimpleJavaFileObject;
import java.io.File;
import java.io.IOException;
import java.net.URI;

class FileJavaSource extends SimpleJavaFileObject {

	private final File file;
	private String source;

	public FileJavaSource(File file) {
		super(file.toURI(), Kind.SOURCE);
		this.file = file;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
			throws IOException {
		if( this.source == null )
		{
			this.source = FileUtils.readFileToString(file, "UTF-8");
		}
		return this.source;
	}
}