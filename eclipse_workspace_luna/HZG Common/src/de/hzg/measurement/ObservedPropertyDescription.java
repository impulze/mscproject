package de.hzg.measurement;

import java.util.List;

import org.hibernate.Hibernate;

public class ObservedPropertyDescription {
	private Long id;
	private String name;
	private String className;
	private String unit;
	private String metadata;
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

	public List<ObservedPropertyInstance> getObservedPropertyInstances() {
		return observedPropertyInstances;
	}

	public void setObservedPropertyInstances(List<ObservedPropertyInstance> observedPropertyInstances) {
		this.observedPropertyInstances = observedPropertyInstances;
	}

	public void initObservedPropertyDescription() {
		Hibernate.initialize(getObservedPropertyInstances());


		for (final ObservedPropertyInstance observedPropertyInstance: getObservedPropertyInstances()) {
			observedPropertyInstance.initObservedPropertyInstance();
		}
	}
}