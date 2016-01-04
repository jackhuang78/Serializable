package idv.jhuang78.serializable.collection;

public abstract class Map<E> {
	public abstract boolean put(int key, E obj);
	public abstract boolean get(int key, E obj);
	public abstract void clear();
}
