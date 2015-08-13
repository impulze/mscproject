package de.hzg.values;

import java.util.Date;

import de.hzg.measurement.ProcedureInstance;

public interface ValueData<T> {
	ProcedureInstance getProcedureInstance();
	void setProcedureInstance(ProcedureInstance procedureInstance);

	Date getDate();
	void setDate(Date date);

	T getValue();
	void setValue(T value);
}