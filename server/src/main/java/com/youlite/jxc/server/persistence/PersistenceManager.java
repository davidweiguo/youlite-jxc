package com.youlite.jxc.server.persistence;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.derby.drda.NetworkServerControl;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class PersistenceManager {

	private static final Logger log = LoggerFactory
			.getLogger(PersistenceManager.class);
	private boolean embeddedSQLServer;
	protected boolean persistSignal;
	NetworkServerControl server;
	private String embeddedHost = "localhost";
	private int embeddedPort = 1527;

	@Autowired
	private SessionFactory sessionFactory;

	public void init() throws Exception {
		if (embeddedSQLServer) {
			startEmbeddedSQLServer();
		}
		createTableSchema();
	}

	private void startEmbeddedSQLServer() throws UnknownHostException,
			Exception {
		server = new NetworkServerControl(InetAddress.getByName(embeddedHost),
				embeddedPort);
		server.start(null);
		log.info("Embedded SQL server started");
	}

	public void uninit() {
		log.info("uninitialising");
		if (embeddedSQLServer)
			stopEmbeddedSQLServer();
	}

	private void stopEmbeddedSQLServer() {
		try {
			server.shutdown();
			log.info("Embedded SQL server stopped");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void createTableSchema() {
		Session session = sessionFactory.openSession();
		Transaction trans = session.beginTransaction();
		String hql = "CREATE TABLE ANYONE.GROUP_DEF (GROUP_ID VARCHAR(40) NOT NULL,GROUP_NAME VARCHAR(40) NOT NULL,PRIMARY KEY (GROUP_ID));";
		Query query = session.createQuery(hql);
		query.executeUpdate();
		trans.commit();
		session.close();
	}
}
