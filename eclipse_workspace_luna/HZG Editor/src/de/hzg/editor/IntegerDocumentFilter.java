package de.hzg.editor;

import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class IntegerDocumentFilter extends DocumentFilter {
	private final Window owner;

	IntegerDocumentFilter(Window owner) {
		this.owner = owner;
	}

	private boolean testInteger(StringBuilder stringBuilder) {
		try {
			Integer.parseInt(stringBuilder.toString());
			return true;
		} catch (NumberFormatException exception) {
			JOptionPane.showMessageDialog(owner, "Only integers accepted.", "Only integers accept", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
	}

	@Override
	public void insertString(FilterBypass filterBypass, int offset, String string, AttributeSet attributeSet) throws BadLocationException {
		final Document document = filterBypass.getDocument();
		final StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(document.getText(0, document.getLength()));
		stringBuilder.insert(offset, string);

		if (testInteger(stringBuilder)) {
			super.insertString(filterBypass, offset, string, attributeSet);
		}
	}

	@Override
	public void replace(FilterBypass filterBypass, int offset, int length, String text, AttributeSet attributeSet) throws BadLocationException {
		final Document document = filterBypass.getDocument();
		final StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(document.getText(0, document.getLength()));
		stringBuilder.replace(offset, offset + length, text);

		if (testInteger(stringBuilder)) {
			super.replace(filterBypass, offset, length, text, attributeSet);
		}
	}

	@Override
	public void remove(FilterBypass filterBypass, int offset, int length) throws BadLocationException {
		final Document document = filterBypass.getDocument();
		final StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(document.getText(0, document.getLength()));
		stringBuilder.delete(offset, offset + length);

		if (testInteger(stringBuilder)) {
			super.remove(filterBypass, offset, length);
		}
	}
}