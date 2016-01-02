package idv.jhuang78.serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class Serializer<E> {
	public int size(E obj) {
		throw new UnsupportedOperationException();
	}
	
	public int write(E obj, ByteBuffer buffer, int start) {
		throw new UnsupportedOperationException();
	}
	public int read(E obj, ByteBuffer buffer, int start) {
		throw new UnsupportedOperationException();
	}
	
	public int write(E obj, DataOutputStream out, int start) throws IOException {
		throw new UnsupportedOperationException();
	}
	public int read(E obj, DataInputStream in, int start) throws IOException {
		throw new UnsupportedOperationException();
	}
}
