package de.hzg.measurement;

//import java.util.logging.Logger;

public abstract class BaseProcedure implements Procedure {
	//private static final Logger logger = Logger.getLogger(BaseSensor.class.getName());
	private ProcedureInstance procedureInstance;
	private ProcedureDescription procedureDescription;
	protected double[] parameters = new double[6];

	public ProcedureInstance getProcedureInstance() {
		return procedureInstance;
	}

	public void setProcedureInstance(ProcedureInstance procedureInstance) {
		this.procedureInstance = procedureInstance;
	}

	public ProcedureDescription getProcedureDescription() {
		return procedureDescription;
	}

	public void setProcedureDescription(ProcedureDescription procedureDescription) {
		this.procedureDescription = procedureDescription;
	}

	public void updateCalibrationParameters() {
		procedureInstance.getParameters(parameters);
	}
}