package com.youlite.jxc.server.persistence;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.derby.drda.NetworkServerControl;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.youlite.jxc.server.pojo.Group;

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
		List<Group> groups = recoverGroups();
		System.out.println(groups.size());
	}
	
	private void startEmbeddedSQLServer() throws UnknownHostException,
			Exception {
		server = new NetworkServerControl(InetAddress.getByName(embeddedHost),
				embeddedPort);
		server.start(null);
		log.info("Embedded SQL server started");
	}

	@SuppressWarnings("unchecked")
	public List<Group> recoverGroups() {
		Session session = sessionFactory.openSession();
		List<Group> result = new ArrayList<Group>();
		try {
			result = (List<Group>) session.createCriteria(Group.class).list();
		} catch (HibernateException e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			session.close();
		}
		return result;
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

	public boolean isEmbeddedSQLServer() {
		return embeddedSQLServer;
	}

	public void setEmbeddedSQLServer(boolean embeddedSQLServer) {
		this.embeddedSQLServer = embeddedSQLServer;
	}

	public String getEmbeddedHost() {
		return embeddedHost;
	}

	public void setEmbeddedHost(String embeddedHost) {
		this.embeddedHost = embeddedHost;
	}

	public int getEmbeddedPort() {
		return embeddedPort;
	}

	public void setEmbeddedPort(int embeddedPort) {
		this.embeddedPort = embeddedPort;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
