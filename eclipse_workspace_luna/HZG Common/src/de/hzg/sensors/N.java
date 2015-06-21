package de.hzg.sensors;

public class N extends BaseSensor {
	@Override
	public double calibrate(double rawValue) {
            return (((((parameters[5]
                    * rawValue + parameters[4])
                    * rawValue + parameters[3])
                    * rawValue + parameters[2])
                    * rawValue + parameters[1])
                    * rawValue + parameters[0]);
	}
}