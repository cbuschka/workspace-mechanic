package com.github.cbuschka.workspace_mechanic.internal.util;

public class ValueHolder<T>
{
	private T value;

	public ValueHolder()
	{
		this(null);
	}

	public ValueHolder(T value)
	{
		this.value = value;
	}

	public void setValue(T value)
	{
		this.value = value;
	}

	public T getValue()
	{
		return value;
	}
}