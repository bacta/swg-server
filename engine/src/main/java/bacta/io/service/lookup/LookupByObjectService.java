package bacta.io.service.lookup;

import java.util.Set;

public interface LookupByObjectService<T> {
	
	T get(Object key);

	T put(Object key, T t);

	T remove(Object key);

    Set<Object> keySet();
}
