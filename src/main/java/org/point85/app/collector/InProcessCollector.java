package org.point85.app.collector;

import org.apache.log4j.PropertyConfigurator;
import org.point85.domain.collector.CollectorService;
import org.point85.domain.persistence.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for Java Service Wrapper's entry point to the {@link CollectorService}
 *
 */
public class InProcessCollector {
	// logger
	private static final Logger logger = LoggerFactory.getLogger(InProcessCollector.class);

	private static final int IDX_JDBC = 0;
	private static final int IDX_USER = 1;
	private static final int IDX_PASSWORD = 2;

	// main method is executed by the Java Service Wrapper
	public static void main(String[] args) {
		// configure log4j
		PropertyConfigurator.configure("../../../config/logging/log4j.properties");

		if (args.length < IDX_USER) {
			logger.error("The application, jdbc connection string and user name must be specified.");
			return;
		}

		String password = args.length > IDX_PASSWORD ? args[IDX_PASSWORD] : null;

		// create the EMF
		if (logger.isInfoEnabled()) {
			logger.info("Initializing persistence service with args: " + args[IDX_JDBC] + ", " + args[IDX_USER] + ", "
					+ password);
		}
		PersistenceService.instance().initialize(args[IDX_JDBC], args[IDX_USER], password);

		// create the collector service
		CollectorService collectorServer = new CollectorService();

		try {
			// start server
			collectorServer.startup();
		} catch (Exception e) {
			collectorServer.onException("Startup failed. ", e);
			collectorServer.shutdown();
		}

		if (logger.isInfoEnabled()) {
			logger.info("Startup finished successfully.");
		}
	}
}
