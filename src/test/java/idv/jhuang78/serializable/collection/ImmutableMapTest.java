package idv.jhuang78.serializable.collection;

import static org.junit.Assert.assertEquals;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import idv.jhuang78.serializable.Serializable;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

public class ImmutableMapTest {

	private static class Person extends Serializable {
		public StringBuilder name;
		public Person() {
			this("");
		}
		public Person(String name) {
			this.name = new StringBuilder(name);
		}
		public Person clone() {
			return new Person(name.toString());
		}
		
		@Override
		public int size() {
			return Integer.BYTES + name.length();
		}
		@Override
		public int write(ByteBuffer buffer, int start) {
			int idx = start;
			int length = name.length();
			buffer.putInt(idx, length);
			idx += Integer.BYTES;
			for(int i = 0; i < length; i++, idx++) {
				buffer.put(idx, (byte)name.charAt(i));
			}
			return idx;
		}
		@Override
		public int read(ByteBuffer buffer, int start) {
			int idx = start;
			int length = buffer.getInt(idx);
			idx += Integer.BYTES;
			
			name.setLength(length);
			for(int i = 0; i < length; i++, idx++) {
				name.setCharAt(i, (char)buffer.get(idx));
			}
			return idx;
		}
	}
	
	private Random rand = new Random(0);
	private String randomString(int length) {
		return RandomStringUtils.random(length, 0, 0, true, true, null, rand);
	}
	
	//@Test
	public void testSingle() {
		ImmutableMap<Person> map = new ImmutableMap<>();
		
		Person p1 = new Person("Jack");
		map.put(0, p1);
		
		Person p2 = new Person();
		map.get(0, p2);
		
		assertEquals(p1.name.toString(), p2.name.toString());
	}
	
	@Test
	public void testSome() {
		int numItems = 1000000;
		
		ImmutableMap<Person> map = new ImmutableMap<>(numItems);
		TIntObjectMap<String> map2 = new TIntObjectHashMap<>();
		
		Person p = new Person();
		for(int i = 0; i < numItems; i++) {	
			int key = randomString(20).hashCode();
			p.name.setLength(0);
			p.name.append(randomString(20));
			map.put(key, p);
			if(!map2.containsKey(key))
				map2.put(key, p.name.toString());
		}
		
		map.freeze();
		
		for(int key : map2.keys()) {
			map.get(key, p);
			String name = map2.get(key);
			assertEquals("key=" + key, name, p.name.toString());
		}
		
		
		
		
	}
	
	

}
