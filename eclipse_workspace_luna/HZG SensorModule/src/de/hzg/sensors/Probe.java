package de.hzg.sensors;

//import java.util.List;

public class Probe {
	private final String name;
	private final String device;
	//private final List<Sensor> sensors;

	public Probe(String name, String device) {
		this.name = name;
		this.device = device;

		this.name.length();
		this.device.length();
	}
}