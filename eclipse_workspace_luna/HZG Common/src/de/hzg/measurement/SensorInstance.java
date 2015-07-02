package de.hzg.measurement;

public class SensorInstance {
	private Long id;
	private Integer address;
	private Double parameter1;
	private Double parameter2;
	private Double parameter3;
	private Double parameter4;
	private Double parameter5;
	private Double parameter6;
	private Probe probe;
	private SensorDescription sensorDescription;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getAddress() {
		return address;
	}

	public void setAddress(Integer address) {
		this.address = address;
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

	public Probe getProbe() {
		return probe;
	}

	public void setProbe(Probe probe) {
		this.probe = probe;
	}

	public SensorDescription getSensorDescription() {
		return sensorDescription;
	}

	public void setSensorDescription(SensorDescription sensorDescription) {
		this.sensorDescription = sensorDescription;
	}

	public void getParameters(double[] parameters) {
		parameters[0] = getParameter1();
		parameters[1] = getParameter2();
		parameters[2] = getParameter3();
		parameters[3] = getParameter4();
		parameters[4] = getParameter5();
		parameters[5] = getParameter6();
	}
}