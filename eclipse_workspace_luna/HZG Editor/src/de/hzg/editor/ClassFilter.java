package de.hzg.editor;

import javax.swing.JTextArea;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class ClassFilter extends DocumentFilter {
	private final int startPosition;
	private int endPosition;

	ClassFilter(JTextArea textArea, int startPosition, int endPosition) {
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}

	public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr) throws BadLocationException {
		if (offset >= startPosition && offset <= this.endPosition) {
			this.endPosition += string.length();
			super.insertString(fb, offset, string, attr);
		}
	}

	public void remove(final FilterBypass fb, final int offset, final int length) throws BadLocationException {
		if (offset >= startPosition && offset + length <= this.endPosition) {
			this.endPosition -= length;
			super.remove(fb, offset, length);
		}
	}

	public void replace(final FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs) throws BadLocationException {
		if (offset >= startPosition && offset + length <= this.endPosition) {
			final int addedLength = text == null ? 0 : text.length();
			this.endPosition += addedLength - length;
			super.replace(fb, offset, length, text, attrs);
		}
	}
}