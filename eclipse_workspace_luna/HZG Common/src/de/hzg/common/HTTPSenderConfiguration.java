package de.hzg.common;

import java.net.URL;

public class HTTPSenderConfiguration {
	private String basicAuth;
	private URL url;
	private String query;
	private Integer interval;

	public String getBasicAuth() {
		return basicAuth;
	}

	public void setBasicAuth(String basicAuth) {
		this.basicAuth = basicAuth;
	}

	public URL getURL() {
		return url;
	}

	public void setURL(URL url) {
		this.url = url;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}
};