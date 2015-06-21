package de.hzg.values;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Logger;

public class BinaryDataInputStream extends BufferedInputStream {
	private byte[] currentBuffer = new byte[4096];
	private final byte[] currentSlice = new byte[3];
	private int stored = 0;
	private int currentPosition = 0;
	private static final Logger logger = Logger.getLogger(BinaryDataInputStream.class.getName());

	public BinaryDataInputStream(InputStream inputStream) {
		super(inputStream);
	}

	private int sliceFormat() {
		int sliceFormat = 0;

		sliceFormat |= (currentSlice[0] & 0b1) << 2;
		sliceFormat |= (currentSlice[1] & 0b1) << 1;
		sliceFormat |= (currentSlice[2] & 0b1) << 0;

		return sliceFormat;
	}

	private BinaryData createBinaryData() {
		int value = 0;

		value |= ((currentSlice[0] & 0xFE) >> 1) << 0;
		value |= ((currentSlice[1] & 0xFE) >> 1) << 7;
		value |= ((currentSlice[2] & 0x06) >> 1) << 14;

		final int address = (currentSlice[2] & 0xF8) >> 3;

		return new BinaryData(address, value);
	}

	public BinaryData readBinaryData() throws IOException {
		// while not enough bytes were read yet to form a slice
		while (currentPosition + 3 > stored) {
			final int left = currentBuffer.length - stored;
			if (left == 0) {
				// buffer is full, need to shift
				currentBuffer = Arrays.copyOfRange(currentBuffer,  currentPosition,  currentBuffer.length + currentPosition);
				stored = stored - currentPosition;
				currentPosition = 0;
			}

			final int result = this.read(currentBuffer, stored, left);

			if (result == -1) {
				throw new EOFException("Reached EOF while reading new raw data.");
			}

			stored += result;
		}

		// now there are at least 3 valid bytes from currentPosition
		while (currentPosition + 3 <= stored) {
			currentSlice[0] = currentBuffer[currentPosition + 0];
			currentSlice[1] = currentBuffer[currentPosition + 1];
			currentSlice[2] = currentBuffer[currentPosition + 2];

			// TODO: For now: HIHILOW
			if (sliceFormat() == 0b110) {
				// those 3 bytes are valid data, go to possible new one
				currentPosition += 3;

				return createBinaryData();
			} else {
				logger.info("BinaryData bytestream is not yet aligned, waiting for next iteration.");
				// maybe the valid data starts with the next byte
				currentPosition += 1;
			}
		}

		/*
		 * There are no 3 bytes left in the current buffer and
		 * now BinaryData was returned yet.
		 */
		return null;
	}
}