package de.hzg.measurement;

public interface Sensor {
	SensorInstance getSensorInstance();
	void setSensorInstance(SensorInstance sensorInstance);

	SensorDescription getSensorDescription();
	void setSensorDescription(SensorDescription sensorDescription);

	void updateCalibrationParameters();

	double calibrate(double rawValue);
}