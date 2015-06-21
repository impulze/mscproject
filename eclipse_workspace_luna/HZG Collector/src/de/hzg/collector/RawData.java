package de.hzg.collector;

public class RawData {
	private final int address;
	private final int value;
	private final long millisUTC = System.currentTimeMillis();

	public RawData(int address, int value) {
		this.address = address;
		this.value = value;
	}

	public int getAddress() {
		return address;
	}

	public int getValue() {
		return value;
	}

	public long getMillisUTC() {
		return millisUTC;
	}
}