package de.hzg.measurement;

import java.util.List;

import org.hibernate.Hibernate;

public class Sensor {
	private Long id;
	private String name;
	private String device;
	private boolean active;
	private List<ObservedPropertyInstance> observedPropertyInstances;

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

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<ObservedPropertyInstance> getObservedPropertyInstances() {
		return observedPropertyInstances;
	}

	public void setObservedPropertyInstances(List<ObservedPropertyInstance> observedPropertyInstances) {
		this.observedPropertyInstances = observedPropertyInstances;
	}

	public void initSensor() {
		Hibernate.initialize(getObservedPropertyInstances());

		for (final ObservedPropertyInstance observedPropertyInstance: getObservedPropertyInstances()) {
			observedPropertyInstance.initObservedPropertyInstance();
		}
	}
}