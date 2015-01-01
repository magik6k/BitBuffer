package net.magik6k.bitbuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class BitBuffer {
	/**
	 * Puts boolean value(Single bit)
	 * @param b value to set
	 * @return This buffer
	 */
	public abstract BitBuffer putBoolean(boolean b);
	
	/**
	 * Puts single bit(boolean value) to this buffer
	 * @param bit value to set
	 * @return This buffer
	 */
	public BitBuffer putBit(boolean bit){
		return putBoolean(bit);
	}
	
	/**
	 * Puts byte value(8 bits)
	 * @param b value to set
	 * @return This buffer
	 */
	public abstract BitBuffer putByte(byte b);
	
	/**
	 * Puts byte value with specified bit count. Note that this
	 * method can only be used with only positive numbers or zero.
	 * @param b value to set
	 * @param bits Number of bits to use
	 * @return This buffer
	 */
	public abstract BitBuffer putByte(byte b, int bits);
	
	/**
	 * Puts integer value(32 bits)
	 * @param i value to set
	 * @return This buffer
	 */
	public BitBuffer putInt(int i){
		putByte((byte)((i&0xFF000000)>>>24));
		putByte((byte)((i&0x00FF0000)>>>16));
		putByte((byte)((i&0x0000FF00)>>>8));
		putByte((byte) (i&0x000000FF));
		return this;
	}
	
	/**
	 * Puts long value(64 bits)
	 * @param l value to set
	 * @return This buffer
	 */
	public BitBuffer putLong(long l){
		putByte((byte)((l&0xFF00000000000000L)>>>56L));
		putByte((byte)((l&0x00FF000000000000L)>>>48L));
		putByte((byte)((l&0x0000FF0000000000L)>>>40L));
		putByte((byte)((l&0x000000FF00000000L)>>>32L));
		putByte((byte)((l&0x00000000FF000000L)>>>24L));
		putByte((byte)((l&0x0000000000FF0000L)>>>16L));
		putByte((byte)((l&0x000000000000FF00L)>>>8L));
		putByte((byte) (l&0x00000000000000FFL));
		return this;
	}
	
	/**
	 * Puts integer value with specified bit count. Note that this
	 * method can only be used only with positive numbers or zero.
	 * @param i value to set
	 * @param bits Number of bits to use
	 * @return This buffer
	 */
	public BitBuffer putInt(int i, int bits){
		if(bits == 0)return this;
		do{
			if(bits > 7){
				putByte((byte) ((i&(0xFF << (bits - 8))) >>> (bits - 8) ));
				bits -= 8;
			}else{
				putByte((byte) (i & (0xFF >> -(bits - 8))), bits);
				bits = 0;
			}
		}while(bits > 0);
		return this;
	}
	
	/**
	 * Puts long value with specified bit count. Note that this
	 * method can only be used only with positive numbers or zero.
	 * @param l value to set
	 * @param bits Number of bits to use
	 * @return This buffer
	 */
	public BitBuffer putLong(long l, int bits){
		if(bits == 0)return this;
		do{
			if(bits > 31){
				putInt((int) ((l&(0xFFFFFFFFL << (bits - 32L))) >>> (bits - 32L) ));
				bits -= 32;
			}else{
				putInt((int) (l & (0xFFFFFFFFL >> -(bits - 32L))), bits);
				bits = 0;
			}
		}while(bits > 0);
		return this;
	}
	
	/**
	 * Puts floating point value(32 bits)
	 * @param f value to set
	 * @return This buffer
	 */
	public BitBuffer putFloat(float f){
		putInt(Float.floatToRawIntBits(f));
		return this;
	}
	
	/**
	 * Puts double floating point value(64 bits)
	 * @param d value to set
	 * @return This buffer
	 */
	public BitBuffer putDouble(double d){
		putLong(Double.doubleToLongBits(d));
		return this;
	}
	
	/**
	 * Puts {@link String} value(8 bits per char), using UTF-8
	 * @param s value to set
	 * @return This buffer
	 */
	public BitBuffer putString(String s){
		for(byte ch : s.getBytes(StandardCharsets.UTF_8)){
			putByte(ch);
		}
		return this;
	}
	
	/**
	 * Puts {@link String} value(specified amount bits per char), using UTF-8
	 * @param s value to set
	 * @param bitsPerChar amount of bits to use per character
	 * @return This buffer
	 */
	public BitBuffer putString(String s, int bitsPerChar){
		for(byte ch : s.getBytes(StandardCharsets.UTF_8)){
			putByte(ch, bitsPerChar);
		}
		return this;
	}
	
	/**
	 * @see #putBoolean(boolean)
	 * @param bit value to set
	 * @return This buffer
	 */
	public BitBuffer put(boolean bit){
		return putBoolean(bit);
	}
	
	/**
	 * @see #putByte(byte)
	 * @param number value to set
	 * @return This buffer
	 */
	public BitBuffer put(byte number){
		return putByte(number);
	}
	
	/**
	 * @see #putInt(int)
	 * @param number value to set
	 * @return This buffer
	 */
	public BitBuffer put(int number) {
		return putInt(number);
	}
	
	/**
	 * @see #putLong(long)
	 * @param number value to set
	 * @return This buffer
	 */
	public BitBuffer put(long number) {
		return putLong(number);
	}
	
	/**
	 * @see #putLong(long)
	 * @param string value to set
	 * @return This buffer
	 */
	public BitBuffer put(String string){
		return putString(string);
	}
	
	/**
	 * @return Binary value of current bit
	 */
	public abstract boolean getBoolean();
	
	/**
	 * @return 8 bit byte value
	 */
	public abstract byte getByte();
	
	/**
	 * @param bits length of value in bits
	 * @return Byte value of given bit width
	 */
	public abstract byte getByte(int bits);
	
	/**
	 * @return 32 bit integer value
	 */
	public int getInt(){
		return ((getByte()&0xFF) << 24) | ((getByte()&0xFF) << 16) | ((getByte()&0xFF) << 8) | (getByte()&0xFF);
	}
	
	/**
	 * @return 64 bit long value
	 */
	public long getLong(){
		return ((getByte()&0xFFL) << 56L) | ((getByte()&0xFFL) << 48L) | ((getByte()&0xFFL) << 40L) | ((getByte()&0xFFL) << 32L) 
				| ((getByte()&0xFFL) << 24L) | ((getByte()&0xFFL) << 16L) | ((getByte()&0xFFL) << 8L) | (getByte()&0xFFL);
	}
	
	/**
	 * @param bits Length of integer
	 * @return Integer value of given bit width
	 */
	public int getInt(int bits){
		if(bits == 0)return 0;
		int res = 0;
		do {
			if(bits > 7){
				res = (res << 8) | (getByte()&0xFF);
				bits -= 8;
			}else{
				res = (res << bits) + (getByte(bits)&0xFF);
				bits -= bits;
			}
		}while(bits > 0);
		return res;
	}
	
	/**
	 * @param bits Length of long integer
	 * @return Long value of given bit width
	 */
	public long getLong(int bits){
		if(bits == 0)return 0;
		long res = 0;
		do {
			if(bits > 31){
				res = (long)(res << 8L) | (long)(getInt()&0xFFFFFFFFL);
				bits -= 32;
			}else{
				res = (long)(res << bits) | (long)(getInt(bits)&0xFFFFFFFFL);
				bits -= bits;
			}
		}while(bits > 0);
		return res;
	}
	
	/**
	 * @return 32 bit floating point value
	 */
	public float getFloat(){
		return Float.intBitsToFloat(getInt());
	}
	
	/**
	 * @return 64 bit floating point value
	 */
	public double getDouble(){
		return Double.longBitsToDouble(getLong());
	}
	
	/**
	 * @param length Length of the string
	 * @return String of given length, with 8-bit wide characters
	 */
	public String getString(int length){
		byte[] bytes = new byte[length];
		for(int i = 0; i < length; ++i){
			bytes[i] = getByte();
		}
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	/**
	 * @param length Length of the string
	 * @param bitsPerChar amount of bits used per char
	 * @return String of given length
	 */
	public String getString(int length, int bitsPerChar){
		byte[] bytes = new byte[length];
		for(int i = 0; i < length; ++i){
			bytes[i] = getByte(bitsPerChar);
		}
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	/**
	 * Toggles the buffer betwen read/write modes. 
	 * Limit is set at current position, and cursor is set at position 0
	 */
	public abstract void flip();
	
	/**
	 * @return Boolean value telling if the buffer can output data
	 */
	public abstract boolean canRead();
	
	/**
	 * @return Boolean value telling if the buffer can accept data
	 */
	public abstract boolean canWrite();
	
	/**
	 * This method returns representation of this bufer as
	 * array of bytes. nota that not-full bits will be set to 0.
	 * This method shouldn't affect the position.
	 * @return This BitBuffer represented as byte array
	 */
	public byte[] asByteArray(){
		
		byte[] result = new byte[(int) limit()];
		long startPos = position();
		boolean reflip = false;
		if(!canRead()){
			flip();
			reflip = true;
		}
		setPosition(0);
		for(int i = 0; i*8 < limit(); ++i){
			result[i] = getByte();
		}
		if(reflip)
			flip();
		setPosition(startPos);
		
		return result;
	}
	
	/**
	 * This method returns representation of this bufer as
	 * ByteBuffer. nota that not-full bits will be set to 0.
	 * This method shouldn't affect the position.
	 * @return ByteBuffer version of this class
	 */
	public ByteBuffer asByteBuffer(){
		return ByteBuffer.wrap(asByteArray());
	}
	
	/**
	 * Puts this BitBuffer into ByteBuffer
	 * @param bb ByteBuffer to put data to
	 * @return This buffer
	 */
	public BitBuffer putToByteBuffer(ByteBuffer bb){
		bb.put(asByteArray());
		return this;
	}
	
	/**
	 * @return Size of this buffer, in bits
	 */
	public abstract long size();
	
	/**
	 * @return Virtual 'end' of this buffer, in bits
	 */
	public abstract long limit();
	
	/**
	 * @return Current position of cursor, in bits
	 */
	public abstract long position();
	
	/**
	 * Sets cursor position for this buffer
	 * @param newPosition position to set
	 * @return This buffer
	 */
	public abstract BitBuffer setPosition(long newPosition);
	
	/**
	 * Allocates new BitBuffer.
	 * First bit is MSB of byte 0.
	 * @param bits Amount of bits to allocate
	 * @return Newly created instance of BitBuffer
	 */
	public static BitBuffer allocate(long bits){
		return new ArrayBitBuffer(bits);
	}
	
	public static BitBuffer allocateDirect(long bits){
		return new DirectBitBuffer(bits);
	}
	
	/**
	 * Wraps bitbuffer around given array instance.
	 * Any operation on this bitBuffer will modify the array
	 * @param array A byte array to wrap this buffer around
	 * @return Newly created instance of BitBuffer wrapped around array
	 */
	public static BitBuffer wrap(byte[] array){
		return new ArrayBitBuffer(array);
	}

}
