package com.ocdsoft.bacta.engine.service.lookup;

public interface LookupByLongService<T> {
	
	public T get(long key);

	public T put(long key, T value);

	public T remove(long key);
}
