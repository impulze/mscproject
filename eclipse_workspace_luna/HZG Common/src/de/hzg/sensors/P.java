package de.hzg.sensors;

public class P extends BaseSensor {
	@Override
	public double calibrate(double rawValue) {
		// TODO: parameters[4] == field calibration offset?
		return (((parameters[3]
		          * rawValue + parameters[2])
		          * rawValue + parameters[1])
		          * rawValue + parameters[0]);
	}
}