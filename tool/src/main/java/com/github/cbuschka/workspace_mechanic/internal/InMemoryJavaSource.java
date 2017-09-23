package com.github.cbuschka.workspace_mechanic.internal;

import java.io.IOException;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;
import javax.tools.JavaFileObject.Kind;

class InMemoryJavaSource extends SimpleJavaFileObject {

	private String source;

	public InMemoryJavaSource(String className, String source) {
		super(
				URI.create("inmemory:///" + className.replace('.', '/')
						+ ".java"), Kind.SOURCE);
		this.source = source;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
			throws IOException {
		return this.source;
	}
}