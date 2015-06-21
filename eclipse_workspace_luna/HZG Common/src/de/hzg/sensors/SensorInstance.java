package de.hzg.sensors;

public class SensorInstance {
	private Long id;
	private Integer address;
	private Double calibration1;
	private Double calibration2;
	private Double calibration3;
	private Double calibration4;
	private Double calibration5;
	private Double calibration6;
	private SensorDescription description;

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

	public Double getCalibration1() {
		return calibration1;
	}

	public void setCalibration1(Double calibration1) {
		this.calibration1 = calibration1;
	}

	public Double getCalibration2() {
		return calibration2;
	}

	public void setCalibration2(Double calibration2) {
		this.calibration2 = calibration2;
	}

	public Double getCalibration3() {
		return calibration3;
	}

	public void setCalibration3(Double calibration3) {
		this.calibration3 = calibration3;
	}

	public Double getCalibration4() {
		return calibration4;
	}

	public void setCalibration4(Double calibration4) {
		this.calibration4 = calibration4;
	}

	public Double getCalibration5() {
		return calibration5;
	}

	public void setCalibration5(Double calibration5) {
		this.calibration5 = calibration5;
	}

	public Double getCalibration6() {
		return calibration6;
	}

	public void setCalibration6(Double calibration6) {
		this.calibration6 = calibration6;
	}

	public SensorDescription getDescription() {
		return description;
	}

	public void setDescription(SensorDescription description) {
		this.description = description;
	}

	public void getCalibrationParameters(double[] calibrationParameters) {
		calibrationParameters[0] = getCalibration1();
		calibrationParameters[1] = getCalibration2();
		calibrationParameters[2] = getCalibration3();
		calibrationParameters[3] = getCalibration4();
		calibrationParameters[4] = getCalibration5();
		calibrationParameters[5] = getCalibration6();
	}
}