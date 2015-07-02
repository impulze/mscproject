package de.hzg.values;

import java.util.Date;

import de.hzg.measurement.SensorInstance;

public interface ValueData<T> {
	SensorInstance getSensorInstance();
	void setSensorInstance(SensorInstance sensorInstance);

	Date getDate();
	void setDate(Date date);

	T getValue();
	void setValue(T value);
}