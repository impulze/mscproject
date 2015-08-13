package de.hzg.values;

import java.util.Date;

import de.hzg.measurement.ObservedPropertyInstance;

public interface ValueData<T> {
	ObservedPropertyInstance getObservedPropertyInstance();
	void setObservedPropertyInstance(ObservedPropertyInstance observedPropertyInstance);

	Date getDate();
	void setDate(Date date);

	T getValue();
	void setValue(T value);
}