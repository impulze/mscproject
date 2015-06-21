package de.hzg.collector;

import java.util.logging.Formatter;
import java.util.logging.StreamHandler;

public class WebLogHandler extends StreamHandler {
	private final WebLogStream webLogStream;

	public WebLogHandler(WebLogStream webLogStream, Formatter formatter) {
		super(webLogStream, formatter);

		this.webLogStream = webLogStream;
	}

	public WebLogStream getWebLogStream() {
		return webLogStream;
	}
}