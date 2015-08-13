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

	Double getAverage();
	void setAverage(Double average);

	T getMin();
	void setMin(T min);

	T getMax();
	void setMax(T max);

	T getMedian();
	void setMedian(T median);

	Double getStddev();
	void setStddev(Double stddev);
}