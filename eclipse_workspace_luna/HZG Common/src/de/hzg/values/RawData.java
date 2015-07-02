package de.hzg.values;

import java.util.Date;

import de.hzg.measurement.SensorInstance;

public class RawData implements ValueData<Integer> {
	private Long id;
	private SensorInstance sensorInstance;
	private Date date = new Date();
	private Integer value;

	public RawData() {
	}

	public RawData(SensorInstance sensorInstance, Integer value) {
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

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}