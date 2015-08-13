package de.hzg.values;

import java.util.Date;

import de.hzg.measurement.ProcedureInstance;

public class CalculatedData implements ValueData<Double> {
	private Long id;
	private ProcedureInstance procedureInstance;
	private Date date = new Date();
	private Double value;

	public CalculatedData() {
	}

	public CalculatedData(ProcedureInstance sensorInstance, Double value) {
		this.procedureInstance = sensorInstance;
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProcedureInstance getProcedureInstance() {
		return procedureInstance;
	}

	public void setProcedureInstance(ProcedureInstance procedureInstance) {
		this.procedureInstance = procedureInstance;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}