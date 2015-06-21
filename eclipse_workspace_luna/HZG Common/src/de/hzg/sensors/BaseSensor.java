package de.hzg.sensors;

//import java.util.logging.Logger;

public abstract class BaseSensor implements Sensor {
	//private static final Logger logger = Logger.getLogger(BaseSensor.class.getName());
	private SensorInstance sensorInstance;
	private SensorDescription sensorDescription;
	protected double[] parameters = new double[6];

	public SensorInstance getSensorInstance() {
		return sensorInstance;
	}

	public void setSensorInstance(SensorInstance sensorInstance) {
		this.sensorInstance = sensorInstance;
	}

	public SensorDescription getSensorDescription() {
		return sensorDescription;
	}

	public void setSensorDescription(SensorDescription sensorDescription) {
		this.sensorDescription = sensorDescription;
	}

	public void updateCalibrationParameters() {
		sensorInstance.getParameters(parameters);
	}
}