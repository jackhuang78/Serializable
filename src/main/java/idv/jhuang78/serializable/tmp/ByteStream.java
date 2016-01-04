package idv.jhuang78.serializable.tmp;

import idv.jhuang78.serializable.Serializer;

import java.nio.ByteBuffer;
import java.util.Date;

public class ByteStream{
	private final ByteBuffer buffer;
	private int from;
	private int length;
	private int pos;
	
	
	public ByteStream(ByteBuffer buffer, int from, int to) {
		if(from < 0)
			throw new IllegalArgumentException(String.format(
					"Begin index is less then zero: %d", from));
		if(to >= buffer.capacity()) 
			throw new IllegalArgumentException(String.format(
					"End index is greater than buffer capacity %d: %d", buffer.capacity(), to));
		this.buffer = buffer;
		this.from = from;
		this.length = to - from;
		this.pos = 0;
	}
	
	public void position(int idx) {
		pos = idx;
	}
	
	private void checkPos() {
		if(pos < 0 || pos >= length)
			throw new IndexOutOfBoundsException(String.format(
					"Position must be between 0 and %d, but got %d", length, pos));
	}
	
	public ByteStream putByte(byte v) {
		checkPos();
		buffer.put(from + pos, v);
		pos += Byte.BYTES;
		return this;
	}
	
	public ByteStream putShort(short v) {
		checkPos();
		buffer.putShort(from + pos, v);
		pos += Short.BYTES;
		return this;
	}
	
	public ByteStream putInt(int v) {
		checkPos();
		buffer.putInt(from + pos, v);
		pos += Integer.BYTES;
		return this;
	}
	
	public ByteStream putLong(long v) {
		checkPos();
		buffer.putLong(from + pos, v);
		pos += Long.BYTES;
		return this;
	}
	
	public ByteStream putFloat(float v) {
		checkPos();
		buffer.putFloat(from + pos, v);
		pos += Float.BYTES;
		return this;
	}
	
	public ByteStream putDouble(double v) {
		checkPos();
		buffer.putDouble(from + pos, v);
		pos += Double.BYTES;
		return this;
	}
	
	public ByteStream putBoolean(boolean v) {
		checkPos();
		buffer.put(from + pos, (byte)(v ? 1 : 0));
		pos += Byte.BYTES;
		return this;
	}
	
	public ByteStream putChar(char v) {
		checkPos();
		buffer.putChar(from + pos, v);
		pos += Character.BYTES;
		return this;
	}
	
	public ByteStream putString(String v) {
		int len = v.length();
		putInt(len);
		for(int i = 0; i < len; i++) {
			putChar(v.charAt(i));
		}
		return this;
	}
	
	public ByteStream putDate(Date v) {
		return putLong(v.getTime());
	}
	
	public <E> ByteStream putObject(E v, Serializer<E> serializer) {
		int oldPos = pos;
		int oldFrom = from;
		int oldLength = length;
		
		from = pos;
		pos = 0;
		length = serializer.size(v);
		
		if(length > oldLength)
			throw new IllegalStateException(String.format(
					"Nested object has longer length %d than object %d.", length, oldLength));
		
		//serializer.write(v, this);
		
		pos = oldPos;
		from = oldFrom;
		length = oldLength;
		
		return this;
	}
	
	public <E> ByteStream putObjects(Iterable<E> v, Serializer<E> serializer) {
		for(E e : v) {
			putObject(e, serializer);
		}
		return this;
	}
	
	public ByteStream putBytes(byte[] v) {
		return putBytes(v, 0, v.length);
	}
	
	public ByteStream putBytes(byte[] v, int off, int len) {
		for(int i = 0; i < len; i++) {
			putByte(v[off + i]);
		}
		return this;
	}
	
	public ByteStream putShorts(short[] v) {
		return putShorts(v, 0, v.length);
	}
	
	public ByteStream putShorts(short[] v, int off, int len) {
		for(int i = 0; i < len; i++) {
			putShort(v[off + i]);
		}
		return this;
	}	
	
	public ByteStream putInts(int[] v) {
		return putInts(v, 0, v.length);
	}
	
	public ByteStream putInts(int[] v, int off, int len) {
		for(int i = 0; i < len; i++) {
			putInt(v[off + i]);
		}
		return this;
	}
	
	public ByteStream putLongs(long[] v) {
		return putLongs(v, 0, v.length);
	}
	
	public ByteStream putLongs(long[] v, int off, int len) {
		for(int i = 0; i < len; i++) {
			putLong(v[off + i]);
		}
		return this;
	}
	
	public ByteStream putFloats(float[] v) {
		return putFloats(v, 0, v.length);
	}
	
	public ByteStream putFloats(float[] v, int off, int len) {
		for(int i = 0; i < len; i++) {
			putFloat(v[off + i]);
		}
		return this;
	}
	
	public ByteStream putDoubles(double[] v) {
		return putDoubles(v, 0, v.length);
	}
	
	public ByteStream putDoubles(double[] v, int off, int len) {
		for(int i = 0; i < len; i++) {
			putDouble(v[off + i]);
		}
		return this;
	}
	
	public ByteStream putBooleans(boolean[] v) {
		return putBooleans(v, 0, v.length);
	}
	
	public ByteStream putBooleans(boolean[] v, int off, int len) {
		for(int i = 0; i < len; i++) {
			putBoolean(v[off + i]);
		}
		return this;
	}
	
	public ByteStream putChars(char[] v) {
		return putChars(v, 0, v.length);
	}
	
	public ByteStream putChars(char[] v, int off, int len) {
		for(int i = 0; i < len; i++) {
			putChar(v[off + i]);
		}
		return this;
	}
	
	
	
}
