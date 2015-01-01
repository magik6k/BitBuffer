package net.magik6k.bitbuffer;

import static org.junit.Assert.*;
import net.magik6k.bitbuffer.BitBuffer;

import org.junit.BeforeClass;
import org.junit.Test;

public class BitBufferTest {
	@BeforeClass
	public static void prepare() {
		
	}
	
	@Test
	public void floatTest(){
		BitBuffer buffer = BitBuffer.allocate(64);
		buffer.putFloat(1234.6578f);
		buffer.putFloat(-8765.4321f);
		
		buffer.flip();
		
		assertEquals(1234.6578f, buffer.getFloat(), 0);
		assertEquals(-8765.4321f, buffer.getFloat(), 0);
	}
	
	@Test
	public void partialStringTest(){
		BitBuffer buffer = BitBuffer.allocate(8*16);
		buffer.putString("12 3!!", 6);
		buffer.putString("ItWorks!", 7);
		
		buffer.flip();
		
		assertEquals("12 3!!", buffer.getString(6, 6));
		assertEquals("ItWorks!", buffer.getString(8, 7));
	}
	
	@Test
	public void basicStringTest(){
		BitBuffer buffer = BitBuffer.allocate(8*16);
		buffer.putString("foo");
		buffer.putString("bar");
		buffer.putString("123");
		buffer.putString("baz");
		
		buffer.flip();
		
		assertEquals("foo", buffer.getString(3));
		assertEquals("bar", buffer.getString(3));
		assertEquals("123", buffer.getString(3));
		assertEquals("baz", buffer.getString(3));
	}
	
	@Test
	public void partialIntegerTest(){
		BitBuffer buffer = BitBuffer.allocate(128);
		
		buffer.putInt(0xBADC0DE, 28);
		buffer.putInt(0xDEAD, 16);
		buffer.putInt(0xBEEF, 16);
		
		buffer.flip();
		
		assertHex(0xBADC0DE, buffer.getInt(28));
		assertHex(0xDEAD, buffer.getInt(16));
		assertHex(0xBEEF, buffer.getInt(16));
	}
	
	@Test
	public void basicIntegerTest(){
		BitBuffer buffer = BitBuffer.allocate(128);
		
		buffer.putInt(1654862454);
		buffer.putInt(-2346798);
		buffer.putInt(29);
		
		buffer.flip();
		
		assertHex(1654862454, buffer.getInt());
		assertHex(-2346798, buffer.getInt());
		assertHex(29, buffer.getInt());
	}
	
	@Test
	public void mixedByteTest(){
		BitBuffer buffer = BitBuffer.allocate(16);
		buffer.putByte((byte) 0x7F, 8);
		buffer.putByte((byte) 0xA2);
		
		buffer.flip();
		
		assertBits((byte) 0x7F, buffer.getByte());
		assertBits((byte) 0xA2, buffer.getByte(8));
	}
	
	@Test
	public void partialByteTest(){
		BitBuffer buffer = BitBuffer.allocate(16);
		buffer.putByte((byte) 0x7, 3);  // 111
		buffer.putByte((byte) 0xD, 4);  // 1101
		buffer.putByte((byte) 0x2A, 6); // 101010
		buffer.putByte((byte) 0x5, 3);  // 101
		
		assertBits((byte)0xFB, buffer.asByteArray()[0]);
		assertBits((byte)0x55, buffer.asByteArray()[1]);
		
		buffer.flip();
		
		assertBits((byte) 0x7, buffer.getByte(3));
		assertBits((byte) 0xD, buffer.getByte(4));
		assertBits((byte) 0x2A, buffer.getByte(6));
		assertBits((byte) 0x5, buffer.getByte(3));
		
		buffer.flip();
		
		buffer.putByte((byte) 0x5D, 7);
		buffer.putByte((byte) 0x2, 3);
		buffer.putByte((byte) 0x31, 6);
		
		buffer.flip();
		
		assertBits((byte) 0x5D, buffer.getByte(7));
		assertBits((byte) 0x2, buffer.getByte(3));
		assertBits((byte) 0x31, buffer.getByte(6));
	}
	
	@Test
	public void byteBooleanTest(){
		BitBuffer buffer = BitBuffer.allocate(32);
		buffer.putBoolean(true);
		buffer.putBoolean(true);
		buffer.putBoolean(true);
		buffer.putByte((byte) 0xBA); //1011 1010
		assertBits((byte)(0xE0 + (0xBA >>> 3)), buffer.asByteArray()[0]);
		buffer.putBoolean(false);
		buffer.putBoolean(true);
		buffer.putBoolean(true);
		assertEquals(14, buffer.position());
		assertBits((byte)0x4C, buffer.asByteArray()[1]);
		buffer.putByte((byte) 0xDC); //1101 1100(pos: 1,6 - 2,6)
		assertEquals(14+8, buffer.position());
		buffer.putBoolean(false);
		buffer.putBoolean(false);
		buffer.putByte((byte) 0x0D);
		
		buffer.flip();
		
		assertTrue(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertBits((byte) 0xBA, buffer.getByte());
		assertFalse(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertEquals((byte) 0xDC, buffer.getByte());
		assertFalse(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertEquals((byte) 0x0D, buffer.getByte());
	}
	
	@Test
	public void basicByteTest(){
		BitBuffer buffer = BitBuffer.allocate(32);
		buffer.putByte((byte) 0x1);
		buffer.putByte((byte) 0x2);
		buffer.putByte((byte) 0x3);
		buffer.putByte((byte) -1);
		
		buffer.flip();
		
		assertBits((byte) 0x1, buffer.getByte());
		assertBits((byte) 0x2, buffer.getByte());
		assertBits((byte) 0x3, buffer.getByte());
		assertBits((byte) -1, buffer.getByte());
		
		buffer.flip();
		
		buffer.putByte((byte) 127);
		buffer.putByte((byte) 56);
		buffer.putByte((byte) 18);
		buffer.putByte((byte) -15);
		
		buffer.flip();
		
		assertBits((byte) 127, buffer.getByte());
		assertBits((byte) 56, buffer.getByte());
		assertBits((byte) 18, buffer.getByte());
		assertBits((byte) -15, buffer.getByte());
		
		buffer.flip();
		
		buffer.putByte((byte) 0xAD);
		buffer.putByte((byte) 0xFF);
		buffer.putByte((byte) 0x62);
		buffer.putByte((byte) 0x2D);
		
		buffer.flip();
		
		assertBits((byte) 0xAD, buffer.getByte());
		assertBits((byte) 0xFF, buffer.getByte());
		assertBits((byte) 0x62, buffer.getByte());
		assertBits((byte) 0x2D, buffer.getByte());
	}
	
	@Test
	public void booleanTest() {
		BitBuffer buffer = BitBuffer.allocate(24);
		
		buffer.putBoolean(true);
		buffer.putBoolean(false);
		buffer.putBoolean(true);
		buffer.putBoolean(false);
		buffer.putBoolean(true);
		buffer.putBoolean(false);
		buffer.putBoolean(true);
		buffer.putBoolean(false);
		
		buffer.flip();
		
		assertTrue(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertFalse(buffer.getBoolean());

		buffer.flip();
		
		buffer.putBoolean(false);
		buffer.putBoolean(true);
		buffer.putBoolean(false);
		buffer.putBoolean(true);
		buffer.putBoolean(false);
		buffer.putBoolean(true);
		buffer.putBoolean(false);
		buffer.putBoolean(true);
		
		buffer.flip();
		
		assertFalse(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertTrue(buffer.getBoolean());

		buffer.flip();
		
		buffer.putBoolean(true);
		buffer.putBoolean(true);
		buffer.putBoolean(true);
		buffer.putBoolean(false);
		buffer.putBoolean(true);
		buffer.putBoolean(true);
		buffer.putBoolean(false);
		buffer.putBoolean(false);
		buffer.putBoolean(false);
		buffer.putBoolean(false);
		buffer.putBoolean(true);
		buffer.putBoolean(false);
		buffer.putBoolean(false);
		buffer.putBoolean(false);
		buffer.putBoolean(true);
		buffer.putBoolean(true);
		buffer.putBoolean(true);
		buffer.putBoolean(true);
		
		buffer.flip();
		
		assertTrue(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertFalse(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
		assertTrue(buffer.getBoolean());
	}
	
	public static void assertHex(int expected, int actual){
		if(expected != actual)
			throw new AssertionError("expected:<"+"0x" + Integer.toHexString(expected)
					+"> but was:<"+"0x" + Integer.toHexString(actual) +">");
	}
	
	public static void assertBits(byte expected, byte actual){
		if(expected != actual)
			throw new AssertionError("expected:<"+"0b" + ("0000000" + Integer.toBinaryString(0xFF & expected)).replaceAll(".*(.{8})$", "$1")
					+"> but was:<"+"0b" + ("0000000" + Integer.toBinaryString(0xFF & actual)).replaceAll(".*(.{8})$", "$1")+">");
	}
}
