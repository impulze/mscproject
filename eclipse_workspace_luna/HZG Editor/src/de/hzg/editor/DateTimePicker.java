package de.hzg.editor;

import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.awt.*;

/**
 * This is licensed under LGPL.  License can be found here:  http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * This is provided as is.  If you have questions please direct them to charlie.hubbard at gmail dot you know what.
 */
public class DateTimePicker extends JXDatePicker {
	private static final long serialVersionUID = 4880521566870770813L;
	private JSpinner timeSpinner;
	private JPanel timePanel;
	private DateFormat timeFormat;

	public DateTimePicker() {
		super();
			getMonthView().setSelectionModel(new SingleDaySelectionModel());
	}

	public DateTimePicker(Date date) {
		this();
		setDate(date);
	}

	public void commitEdit() throws ParseException {
		commitTime();
		super.commitEdit();
	}

	public void cancelEdit() {
		super.cancelEdit();
		setTimeSpinners();
	}

	@Override
	public JPanel getLinkPanel() {
		super.getLinkPanel();

		if (timePanel == null) {
			timePanel = createTimePanel();
		}

		setTimeSpinners();

		return timePanel;
	}

	private JPanel createTimePanel() {
		final JPanel newPanel = new JPanel();

		newPanel.setLayout(new FlowLayout());
		//newPanel.add(panelOriginal);

		final SpinnerDateModel dateModel = new SpinnerDateModel();

		timeSpinner = new JSpinner(dateModel);

		if (timeFormat == null) {
			timeFormat = DateFormat.getTimeInstance(DateFormat.LONG);
		}

		updateTextFieldFormat();

		newPanel.add(new JLabel("Time:"));
		newPanel.add(timeSpinner);
		newPanel.setBackground(Color.WHITE);

		return newPanel;
	}

	private void updateTextFieldFormat() {
		if (timeSpinner == null) {
			return;
		}

		final JFormattedTextField textField = ((JSpinner.DefaultEditor)timeSpinner.getEditor()).getTextField();
		final DefaultFormatterFactory factory = (DefaultFormatterFactory)textField.getFormatterFactory();
		final DateFormatter formatter = (DateFormatter)factory.getDefaultFormatter();

		// change the date format to only show the hours
		formatter.setFormat(timeFormat);
	}

	private void commitTime() {
		final Date date = getDate();

		if (date != null) {
			final Date time = (Date)timeSpinner.getValue();
			final GregorianCalendar timeCalendar = new GregorianCalendar();

			timeCalendar.setTime(time);

			final GregorianCalendar calendar = new GregorianCalendar();

			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			final Date newDate = calendar.getTime();

			setDate(newDate);
		}
	}

	private void setTimeSpinners() {
		final Date date = getDate();

		if (date != null) {
			timeSpinner.setValue(date);
		}
	}

	public DateFormat getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(DateFormat timeFormat) {
		this.timeFormat = timeFormat;

		updateTextFieldFormat();
	}
}