package com.youlite.jxc.server;

import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.youlite.jxc.common.IPlugin;
import com.youlite.jxc.common.SystemInfo;
import com.youlite.jxc.common.event.AsyncEventProcessor;
import com.youlite.jxc.common.event.IAsyncEventManager;
import com.youlite.jxc.common.event.IRemoteEventManager;
import com.youlite.jxc.server.persistence.PersistenceManager;

public class Server implements ApplicationContextAware {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private IRemoteEventManager eventManager;

	@Autowired
	private PersistenceManager persistenceManager;

	@Autowired
	private SystemInfo systemInfo;

	private List<IPlugin> plugins;

	private AsyncEventProcessor eventProcessor = new AsyncEventProcessor() {

		@Override
		public void subscribeToEvents() {
			// subscribeToEvent(NodeInfoEvent.class, null);
		}

		@Override
		public IAsyncEventManager getEventManager() {
			return eventManager;
		}

	};

	public void init() throws Exception {
		persistenceManager.init();

		eventProcessor.setHandler(this);
		eventProcessor.init();
		if (eventProcessor.getThread() != null)
			eventProcessor.getThread().setName("Server");
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	public List<IPlugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<IPlugin> plugins) {
		this.plugins = plugins;
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		String configFile = "conf/server.xml";
		String logConfigFile = "conf/log4j.xml";
		DOMConfigurator.configure(logConfigFile);
		ApplicationContext context = new FileSystemXmlApplicationContext(
				configFile);

		// start server
		Server server = (Server) context.getBean("server");
		try {
			server.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
