package de.hzg.values;

import java.util.Date;

import de.hzg.measurement.ObservedPropertyInstance;

public class CalculatedData implements ValueData<Double> {
	private Long id;
	private ObservedPropertyInstance observedPropertyInstance;
	private Date date;
	private Double value;
	private Double average;
	private Double min;
	private Double max;
	private Double median;
	private Double stddev;

	public CalculatedData() {
	}

	public CalculatedData(Date date, ObservedPropertyInstance observedPropertyInstance) {
		this.date = date;
		this.observedPropertyInstance = observedPropertyInstance;
	}

	public CalculatedData(ObservedPropertyInstance observedPropertyInstance) {
		this(new Date(), observedPropertyInstance);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ObservedPropertyInstance getObservedPropertyInstance() {
		return observedPropertyInstance;
	}

	public void setObservedPropertyInstance(ObservedPropertyInstance observedPropertyInstance) {
		this.observedPropertyInstance = observedPropertyInstance;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double getAverage() {
		return average;
	}

	public void setAverage(Double average) {
		this.average = average;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Double getMedian() {
		return median;
	}

	public void setMedian(Double median) {
		this.median = median;
	}

	public Double getStddev() {
		return stddev;
	}

	public void setStddev(Double stddev) {
		this.stddev = stddev;
	}
}