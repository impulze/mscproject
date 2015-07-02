package de.hzg.measurement;

import java.util.List;

import org.hibernate.Hibernate;

public class SensorDescription {
	private Long id;
	private String name;
	private String className;
	private String unit;
	private String metadata;
	private List<SensorInstance> sensorInstances;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public List<SensorInstance> getSensorInstances() {
		return sensorInstances;
	}

	public void setSensorInstances(List<SensorInstance> sensorInstances) {
		this.sensorInstances = sensorInstances;
	}

	public void initSensorDescription() {
		Hibernate.initialize(getSensorInstances());

		for (final SensorInstance sensorInstance: getSensorInstances()) {
			Hibernate.initialize(sensorInstance.getProbe());
		}
	}
}