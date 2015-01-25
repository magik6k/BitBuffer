package net.magik6k.bitbuffer;

/**
 * Classes implementing this interface can be directly inserted into {@link BitBuffer} via its {@link BitBuffer#put(IBufferInsert) put} method
 */
public interface IBufferInsert {
	/**
	 * This method is called when class with interface is being put into {@link BitBuffer}
	 * @param buffer Destination buffer+
	 */
	public void instert(BitBuffer buffer);
}
