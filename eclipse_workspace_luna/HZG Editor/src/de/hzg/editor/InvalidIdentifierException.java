package de.hzg.editor;

public class InvalidIdentifierException extends Exception {
	private static final long serialVersionUID = 5756619161122471986L;

	InvalidIdentifierException(String string) {
		super("The string '" + string + "' is not a valid identifier.");
	}
}