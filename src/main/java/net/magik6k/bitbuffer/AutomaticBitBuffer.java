package net.magik6k.bitbuffer;

import java.util.ArrayList;

class AutomaticBitBuffer extends SimpleBitBuffer{
	private ArrayList<Byte> bytes;//TODO: use some better method
	
	protected AutomaticBitBuffer() {
		bytes = new ArrayList<Byte>();
	}
	
	protected AutomaticBitBuffer(long initialCapacity){
		bytes = new ArrayList<Byte>((int) ((initialCapacity+(8-initialCapacity%8))/8));
		while(initialCapacity > bytes.size())
			bytes.add((byte) 0);
	}
	
	@Override
	protected byte rawGet(long index) {
		if(index >= bytes.size()){
			bytes.ensureCapacity((int) index+1);
			while(index >= bytes.size())
				bytes.add((byte) 0);
		}
		return bytes.get((int) index);
	}

	@Override
	protected void rawSet(long index, byte value) {
		if(index >= bytes.size()){
			bytes.ensureCapacity((int) index+1);
			while(index >= bytes.size())
				bytes.add((byte) 0);
		}
		bytes.set((int) index, value);
	}

	@Override
	protected long rawLength() {
		return bytes.size()*8;
	}
	
}
