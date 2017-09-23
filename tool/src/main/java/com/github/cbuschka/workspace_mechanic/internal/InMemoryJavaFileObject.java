package com.github.cbuschka.workspace_mechanic.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;
import javax.tools.JavaFileObject.Kind;

class InMemoryJavaFileObject extends SimpleJavaFileObject {

	private byte[] data;

	public InMemoryJavaFileObject(String path, Kind kind, byte[] data) {
		super(URI.create("inmemory:///" + path), kind);
		this.data = data;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return new ByteArrayInputStream(this.data);
	}

	public byte[] getData() {
		return data;
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return new ByteArrayOutputStream() {
			@Override
			public void close() throws IOException {
				super.close();
				data = toByteArray();
			}
		};
	}
}