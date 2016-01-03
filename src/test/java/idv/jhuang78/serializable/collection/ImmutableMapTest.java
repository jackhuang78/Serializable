package idv.jhuang78.serializable.collection;

import static org.junit.Assert.assertEquals;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import idv.jhuang78.serializable.Serializer;

import java.nio.ByteBuffer;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

public class ImmutableMapTest {

	private static class Person {
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
		
		public static class PersonSerializer extends Serializer<Person> {
			@Override
			public int size(Person obj) {
				return Integer.BYTES + obj.name.length();
			}
			@Override
			public int write(Person obj, ByteBuffer buffer, int start) {
				int idx = start;
				int length = obj.name.length();
				buffer.putInt(idx, length);
				idx += Integer.BYTES;
				for(int i = 0; i < length; i++, idx++) {
					buffer.put(idx, (byte)obj.name.charAt(i));
				}
				return idx;
			}
			@Override
			public int read(Person obj, ByteBuffer buffer, int start) {
				int idx = start;
				int length = buffer.getInt(idx);
				idx += Integer.BYTES;
				
				obj.name.setLength(length);
				for(int i = 0; i < length; i++, idx++) {
					obj.name.setCharAt(i, (char)buffer.get(idx));
				}
				return idx;
			}
		}
		
		
	}
	
	private Random rand = new Random(0);
	private String randomString(int length) {
		return RandomStringUtils.random(length, 0, 0, true, true, null, rand);
	}
	
	//@Test
	public void testSingle() {
		ImmutableMap<Person> map = new ImmutableMap<>(new Person.PersonSerializer());
		
		Person p1 = new Person("Jack");
		map.put(0, p1);
		
		Person p2 = new Person();
		map.get(0, p2);
		
		assertEquals(p1.name.toString(), p2.name.toString());
	}
	
	@Test
	public void testSome() {
		int numItems = 1000000;
		
		ImmutableMap<Person> map = new ImmutableMap<>(numItems, new Person.PersonSerializer());
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
