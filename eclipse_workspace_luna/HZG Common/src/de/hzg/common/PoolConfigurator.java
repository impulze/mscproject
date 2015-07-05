package de.hzg.common;

import org.apache.tomcat.jdbc.pool.PoolConfiguration;

public interface PoolConfigurator {
	void configure(PoolConfiguration poolConfiguration);
}