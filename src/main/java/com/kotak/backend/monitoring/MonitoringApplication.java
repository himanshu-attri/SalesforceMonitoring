package com.kotak.backend.monitoring;

import com.kotak.backend.monitoring.connector.EmpConnector;
import com.kotak.backend.monitoring.connector.TopicSubscription;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MonitoringApplication {

	@Value("${salesforce.sourceEvent.list}")
	private String[] salesforceEventList;

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private ApplicationContext context;

	private static Logger logger = LoggerFactory.getLogger(MonitoringApplication.class);


	/**
	 * A main method to start this application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(MonitoringApplication.class, args);
	}

	@PostConstruct
	public void startPlatformEventSubsciptions() {

		EmpConnector emp = context.getBean(EmpConnector.class);

		// start platform event subscription
		logger.info("starting salesforce platform event subscription");
		for (int i = 0; i < salesforceEventList.length; i++) {
			String event = salesforceEventList[i];
			beanFactory.getBean(TopicSubscription.class, emp, event);
			logger.info("created new salesforce platform event subscription " + event);
		}
	}

}
