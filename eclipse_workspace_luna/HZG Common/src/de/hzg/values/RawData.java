package de.hzg.values;

import java.util.Date;

import de.hzg.measurement.ObservedPropertyInstance;

public class RawData implements ValueData<Integer> {
	private Long id;
	private ObservedPropertyInstance observedPropertyInstance;
	private Date date;
	private Integer value;
	private Double average;
	private Integer min;
	private Integer max;
	private Integer median;
	private Double stddev;

	public RawData() {
	}

	public RawData(Date date, ObservedPropertyInstance observedPropertyInstance) {
		this.date = date;
		this.observedPropertyInstance = observedPropertyInstance;
	}

	public RawData(ObservedPropertyInstance observedPropertyInstance) {
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

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Double getAverage() {
		return average;
	}

	public void setAverage(Double average) {
		this.average = average;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public Integer getMedian() {
		return median;
	}

	public void setMedian(Integer median) {
		this.median = median;
	}

	public Double getStddev() {
		return stddev;
	}

	public void setStddev(Double stddev) {
		this.stddev = stddev;
	}
}