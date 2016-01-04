package idv.jhuang78.serializable.collection;

import idv.jhuang78.serializable.Serializer;

public class FixedEntryMutableMap<E> extends ImmutableMap<E> {
	
	public FixedEntryMutableMap(Serializer<E> serializer) {
		super(serializer);
	}
	
	public FixedEntryMutableMap(int hashtableSize, Serializer<E> serializer) {
		super(hashtableSize, serializer);
	}
	
	public FixedEntryMutableMap(int hashtableSize, int initialBufferSize, Serializer<E> serializer) {
		super(hashtableSize, initialBufferSize, serializer);
	}
	
	
}
