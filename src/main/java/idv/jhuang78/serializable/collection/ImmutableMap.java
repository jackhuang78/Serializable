package idv.jhuang78.serializable.collection;

import idv.jhuang78.serializable.Serializer;

import java.nio.ByteBuffer;


public class ImmutableMap<E> extends Map<E> {
	
	//==============================
	//	Constants
	//==============================
	private static final int NULL = Integer.MIN_VALUE;
	private static final int DEFAULT_HASHTABLE_SIZE = 128;
	private static final int DEFAULT_BUFFER_SIZE = 1024;
	private static final int OFFSET_NEXT = 0;
	private static final int OFFSET_KEY = OFFSET_NEXT + Integer.BYTES;
	private static final int OFFSET_VALUE = OFFSET_KEY + Integer.BYTES;
	
	
	//==============================
	//	Instance Variables
	//==============================
	private final Serializer<E> serializer;
	private ByteBuffer buffer;
	private int hashtableSize;
	private int hashtableMask;
	private boolean frozen;
	private int next;
	private int counts;
	
	
	//==============================
	//	Constructors
	//==============================
	public ImmutableMap(Serializer<E> serializer) {
		this(DEFAULT_HASHTABLE_SIZE, DEFAULT_BUFFER_SIZE, serializer);
	}
	
	public ImmutableMap(int hashtableSize, Serializer<E> serializer) {
		this(hashtableSize, DEFAULT_BUFFER_SIZE, serializer);
	}
	
	public ImmutableMap(int hashtableSize, int initialBufferSize, Serializer<E> serializer) {
		hashtableSize = roundUp(hashtableSize);
		this.buffer = ByteBuffer.allocate(hashtableSize * Integer.BYTES + initialBufferSize);
		this.serializer = serializer;
		clear(hashtableSize);
	}

	//==============================
	//	Public API
	//==============================
	@Override
	public synchronized void clear() {
		clear(hashtableSize);
	}
	
	public synchronized void clear(int hashtableSize) {
		hashtableSize = roundUp(hashtableSize);
		this.hashtableSize = hashtableSize;
		hashtableMask = hashtableSize - 1;
		
		buffer.limit(buffer.capacity());
		for(int i = 0; i < hashtableSize; i++) {
			buffer.putInt(i * Integer.BYTES, NULL);
		}
		next = hashtableSize * Integer.BYTES;
		
		counts = 0;
		frozen = false;
	}
	
	public void freeze() {
		frozen = true;
	}
	
	@Override
	public synchronized boolean put(int key, E obj) {
		return fastPut(key, obj);
	}
	
	public boolean fastPut(int key, E obj) {
		if(frozen)
			throw new IllegalStateException("Cannot use insert object while frozen.");
		
		int ptr = findPtr(key);
		int loc = buffer.getInt(ptr + OFFSET_NEXT);
		
		if(loc != NULL) {
			// do nothing if this object is already in the map
			// NOTE: you cannot update object once it is in the map
			// TODO: make it so you can insert if the objects are guaranteed to have the same size
			return false;
		}
		
		ensureCapacity(next + OFFSET_VALUE + serializer.size(obj));
		buffer.putInt(next + OFFSET_NEXT, NULL);
		buffer.putInt(next + OFFSET_KEY, key);
		int end = serializer.write(obj, buffer, next + OFFSET_VALUE);
		
		checkSize(obj, end - next - OFFSET_VALUE);
		
		
		buffer.putInt(ptr, next);
		next = end;
		
		counts++;
			
		return true;
	}
	
	@Override
	public boolean get(int key, E obj) {
		if(frozen) {
			return fastGet(key, obj);
		}

		synchronized(this) {
			return fastGet(key, obj);
		}
	}
	
	public boolean fastGet(int key, E obj) {
		int ptr = findPtr(key);
		int loc = buffer.getInt(ptr + OFFSET_NEXT);
		
		if(loc == NULL)
			return false;
		
		int end = serializer.read(obj, buffer, loc + OFFSET_VALUE);
		
		checkSize(obj, end - loc - OFFSET_VALUE);
				
		return true;
	}
	
	public int counts() {
		return counts;
	}
	
	public int capacity() {
		return buffer.capacity();
	}
	
	//==============================
	//	Private Helpers
	//==============================	
	private int roundUp(int x) {
		int y = (x <= 0) ? 1 : (int) Math.pow(2, Math.ceil(Math.log(x)/Math.log(2)));
		return y;
	}
	
	private int findPtr(int key) {
		int ptr = (key & hashtableMask) * Integer.BYTES;
		int loc = buffer.getInt(ptr + OFFSET_NEXT);
		
		
		
		while(loc != NULL) {
			if(buffer.getInt(loc + OFFSET_KEY) == key)
				return ptr;
			ptr = loc;
			loc = buffer.getInt(ptr + OFFSET_NEXT);
		}
		
		return ptr;
	}
	
	private void ensureCapacity(int capacity) {
		if(capacity > buffer.capacity()) {
			ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
			buffer.position(0);
			buffer.limit(buffer.capacity());
			newBuffer.put(buffer);
			
			buffer = newBuffer;
		}
	}
	
	private void checkSize(E obj, int accessed) {
		int size = serializer.size(obj);
		
		if(size < 0)
			return;
		
		if(size != accessed) {
			throw new IllegalStateException(String.format("Object reports %d bytes but accessed %s bytes.", 
					size, accessed));
		}
	}
	
	
	
	
	
	
}
