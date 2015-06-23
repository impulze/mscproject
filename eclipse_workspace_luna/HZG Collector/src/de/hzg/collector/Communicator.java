package de.hzg.collector;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import de.hzg.common.ExceptionUtil;

public class Communicator {
	private static final Logger logger = Logger.getLogger(Communicator.class.getName());
	private final InputStream inputStream;
	private final CommPort commPort;

	public Communicator(String portName) throws CommunicatorSetupException {
		final CommPortIdentifier identifier;
		final int timeout = 2000;

		try {
			identifier = CommPortIdentifier.getPortIdentifier(portName);
		} catch (NoSuchPortException exception) {
			logger.severe("Unable to find serial port at '" + portName + "'.");
			throw new CommunicatorSetupException();
		}

		try {
			commPort = identifier.open(getClass().getName(), timeout);
		} catch (PortInUseException exception) {
			logger.severe("Serial port at '" + portName + "' already in use.");
			throw new CommunicatorSetupException();
		}

		/*
		 * Now the serial port is owned by this class.
		 * It has to be closed() otherwise the port is locked after
		 * reloading the servlet.
		 */
		try {
			inputStream = commPort.getInputStream();
		} catch (IOException exception) {
			commPort.close();
			logger.severe("Unable to obtain input stream for serial port at '" + portName + "'.");
			throw new CommunicatorSetupException();
		}
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void end() {
		if (inputStream != null) {
			logger.info("Closing input stream for serial port at '" + commPort.getName() + "'");

			try {
				inputStream.close();
			} catch (IOException exception) {
				logger.severe("Error closing input stream for serial port at '" + commPort.getName() + "'");
				final String stackTrace = ExceptionUtil.stackTraceToString(exception);
				logger.severe(stackTrace);
			}
		}

		if (commPort != null) {
			logger.info("Closing serial port at '" + commPort.getName() + "'");
			commPort.close();
		}
	}
}
