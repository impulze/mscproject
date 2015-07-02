package de.hzg.values;

import java.util.Date;

import de.hzg.measurement.SensorInstance;

public class CalculatedData {
	private Long id;
	private SensorInstance sensorInstance;
	private Date date = new Date();
	private Double value;

	public CalculatedData() {
	}

	public CalculatedData(SensorInstance sensorInstance, Double value) {
		this.sensorInstance = sensorInstance;
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SensorInstance getSensorInstance() {
		return sensorInstance;
	}

	public void setSensorInstance(SensorInstance sensorInstance) {
		this.sensorInstance = sensorInstance;
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