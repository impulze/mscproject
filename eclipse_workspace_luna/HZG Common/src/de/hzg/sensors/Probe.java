package de.hzg.sensors;

import java.util.List;

public class Probe {
	private Long id;
	private String name;
	private String device;
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

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public List<SensorInstance> getSensorInstances() {
		return sensorInstances;
	}

	public void setSensorInstances(List<SensorInstance> sensorInstances) {
		this.sensorInstances = sensorInstances;
	}
}