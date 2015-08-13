package de.hzg.measurement;

import java.sql.Timestamp;

import org.hibernate.Hibernate;

public class CalibrationSet {
	private Long id;
	private ObservedPropertyInstance observedPropertyInstance;
	private Double parameter1;
	private Double parameter2;
	private Double parameter3;
	private Double parameter4;
	private Double parameter5;
	private Double parameter6;
	private Timestamp validStart;
	private Timestamp validEnd;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ObservedPropertyInstance getObservedPropertyInstance() {
		return observedPropertyInstance;
	}

	public void setObservedPropertyInstance(ObservedPropertyInstance observedPropertyInstance) {
		this.observedPropertyInstance = observedPropertyInstance;
	}

	public Double getParameter1() {
		return parameter1;
	}

	public void setParameter1(Double parameter1) {
		this.parameter1 = parameter1;
	}

	public Double getParameter2() {
		return parameter2;
	}

	public void setParameter2(Double parameter2) {
		this.parameter2 = parameter2;
	}

	public Double getParameter3() {
		return parameter3;
	}

	public void setParameter3(Double parameter3) {
		this.parameter3 = parameter3;
	}

	public Double getParameter4() {
		return parameter4;
	}

	public void setParameter4(Double parameter4) {
		this.parameter4 = parameter4;
	}

	public Double getParameter5() {
		return parameter5;
	}

	public void setParameter5(Double parameter5) {
		this.parameter5 = parameter5;
	}

	public Double getParameter6() {
		return parameter6;
	}

	public void setParameter6(Double parameter6) {
		this.parameter6 = parameter6;
	}

	public Timestamp getValidStart() {
		return validStart;
	}

	public void setValidStart(Timestamp validStart) {
		this.validStart = validStart;
	}

	public Timestamp getValidEnd() {
		return validEnd;
	}

	public void setValidEnd(Timestamp validEnd) {
		this.validEnd = validEnd;
	}

	public void initCalibrationSet() {
		Hibernate.initialize(getObservedPropertyInstance());

		getObservedPropertyInstance().initObservedPropertyInstance();
	}
}