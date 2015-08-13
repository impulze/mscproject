package de.hzg.measurement;

import java.util.List;

import org.hibernate.Hibernate;

public class ObservedPropertyInstance {
	private Long id;
	private String name;
	private Integer address;
	private Sensor sensor;
	private ObservedPropertyDescription observedPropertyDescription;
	private CalibrationSet activeCalibrationSet;
	private List<CalibrationSet> calibrationSets;
	private Boolean isRaw;
	private Boolean useInterval;

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

	public Integer getAddress() {
		return address;
	}

	public void setAddress(Integer address) {
		this.address = address;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public ObservedPropertyDescription getObservedPropertyDescription() {
		return observedPropertyDescription;
	}

	public void setObservedPropertyDescription(ObservedPropertyDescription observedPropertyDescription) {
		this.observedPropertyDescription = observedPropertyDescription;
	}

	public CalibrationSet getActiveCalibrationSet() {
		return activeCalibrationSet;
	}

	public void setActiveCalibrationSet(CalibrationSet activeCalibrationSet) {
		this.activeCalibrationSet = activeCalibrationSet;
	}

	public List<CalibrationSet> getCalibrationSets() {
		return calibrationSets;
	}

	public void setCalibrationSets(List<CalibrationSet> calibrationSets) {
		this.calibrationSets = calibrationSets;
	}

	public Boolean getIsRaw() {
		return isRaw;
	}

	public void setIsRaw(Boolean isRaw) {
		this.isRaw = isRaw;
	}

	public Boolean getUseInterval() {
		return useInterval;
	}

	public void setUseInterval(Boolean useInterval) {
		this.useInterval = useInterval;
	}

	public void initObservedPropertyInstance() {
		Hibernate.initialize(getSensor());
		Hibernate.initialize(getCalibrationSets());
		Hibernate.initialize(getObservedPropertyDescription());
	}
}