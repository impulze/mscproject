package de.hzg.measurement;

public interface Procedure {
	ProcedureInstance getProcedureInstance();
	void setProcedureInstance(ProcedureInstance procedureInstance);

	ProcedureDescription getProcedureDescription();
	void setProcedureDescription(ProcedureDescription procedureDescription);

	void updateCalibrationParameters();

	double calibrate(double rawValue);
}