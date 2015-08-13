package de.hzg.measurement;

public interface ObservedProperty {
	ObservedPropertyInstance getObservedPropertyInstance();
	void setObservedPropertyInstance(ObservedPropertyInstance observedPropertyInstance);

	void updateCalibrationParameters();

	int getRawValue(int binaryValue);
	double getCalculationValue(int binaryValue);
}