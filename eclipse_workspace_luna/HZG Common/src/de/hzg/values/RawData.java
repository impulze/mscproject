package de.hzg.values;

import java.util.Date;

import de.hzg.measurement.ProcedureInstance;

public class RawData implements ValueData<Integer> {
	private Long id;
	private ProcedureInstance procedureInstance;
	private Date date = new Date();
	private Integer value;

	public RawData() {
	}

	public RawData(ProcedureInstance procedureInstance, Integer value) {
		this.procedureInstance = procedureInstance;
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

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}