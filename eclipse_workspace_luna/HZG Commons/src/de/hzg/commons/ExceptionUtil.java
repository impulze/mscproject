package de.hzg.commons;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
	public static String stackTraceToString(Throwable throwable) {
		final StringWriter stringWriter = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(stringWriter);

		throwable.printStackTrace(printWriter);

		return stringWriter.toString();
	}
}