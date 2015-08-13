package de.hzg.measurement;

//import java.util.logging.Logger;

public abstract class BaseObservedProperty implements ObservedProperty {
	//private static final Logger logger = Logger.getLogger(BaseSensor.class.getName());
	private ObservedPropertyInstance observedPropertyInstance;
	protected double[] parameters = new double[6];

	public ObservedPropertyInstance getObservedPropertyInstance() {
		return observedPropertyInstance;
	}

	public void setObservedPropertyInstance(ObservedPropertyInstance observedPropertyInstance) {
		this.observedPropertyInstance = observedPropertyInstance;
	}

	private CalibrationSet getActiveCalibrationSet() {
		return observedPropertyInstance.getActiveCalibrationSet();
	}

	public void updateCalibrationParameters() {
		parameters[0] = getActiveCalibrationSet().getParameter1();
		parameters[1] = getActiveCalibrationSet().getParameter2();
		parameters[2] = getActiveCalibrationSet().getParameter3();
		parameters[3] = getActiveCalibrationSet().getParameter4();
		parameters[4] = getActiveCalibrationSet().getParameter5();
		parameters[5] = getActiveCalibrationSet().getParameter6();
	}

	public int getRawValue(int binaryValue) {
		return binaryValue;
	}
}