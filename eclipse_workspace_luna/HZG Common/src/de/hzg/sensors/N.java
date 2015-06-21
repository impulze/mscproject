package de.hzg.sensors;

public class N extends BaseSensor {
	@Override
	public double calibrate(double rawValue) {
            return (((((calibration[5]
                    * rawValue + calibration[4])
                    * rawValue + calibration[3])
                    * rawValue + calibration[2])
                    * rawValue + calibration[1])
                    * rawValue + calibration[0]);
	}
}