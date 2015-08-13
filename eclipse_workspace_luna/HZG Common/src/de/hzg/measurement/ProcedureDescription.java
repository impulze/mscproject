package de.hzg.measurement;

import java.util.List;

import org.hibernate.Hibernate;

public class ProcedureDescription {
	private Long id;
	private String name;
	private String className;
	private String unit;
	private String metadata;
	private List<ProcedureInstance> procedureInstances;

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
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public List<ProcedureInstance> getProcedureInstances() {
		return procedureInstances;
	}

	public void setprocedureInstances(List<ProcedureInstance> procedureInstances) {
		this.procedureInstances = procedureInstances;
	}

	public void initProcedureDescription() {
		Hibernate.initialize(getProcedureInstances());

		for (final ProcedureInstance procedureInstance: getProcedureInstances()) {
			Hibernate.initialize(procedureInstance.getSensor());
		}
	}
}