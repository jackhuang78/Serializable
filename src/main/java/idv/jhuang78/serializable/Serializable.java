package idv.jhuang78.serializable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class Serializable {
	public int size() {
		throw new UnsupportedOperationException();
	}
	
	public int write(ByteBuffer buffer, int start) {
		throw new UnsupportedOperationException();
	}
	public int read(ByteBuffer buffer, int start) {
		throw new UnsupportedOperationException();
	}
	
	public int write(DataOutputStream out, int start) throws IOException {
		throw new UnsupportedOperationException();
	}
	public int read(DataInputStream in, int start) throws IOException {
		throw new UnsupportedOperationException();
	}
	
}
