package com.youlite.jxc.server;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.youlite.jxc.common.IPlugin;
import com.youlite.jxc.common.SystemInfo;

public class Server implements ApplicationContextAware {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private SystemInfo systemInfo;

	private List<IPlugin> plugins;

	public void init() {
		System.out.println("********");
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
		if (args.length == 1) {
			configFile = args[0];
		} else if (args.length == 2) {
			configFile = args[0];
		}
		ApplicationContext context = new FileSystemXmlApplicationContext(
				configFile);

		// start server
		Server server = (Server) context.getBean("server");
		server.init();
	}
}
