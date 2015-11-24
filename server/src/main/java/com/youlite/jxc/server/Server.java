package com.youlite.jxc.server;

import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.youlite.jxc.server.manager.GoodsManager;
import com.youlite.jxc.server.manager.UserGroupManager;
import com.youlite.jxc.server.persistence.PersistenceManager;
import com.youlite.jxc.server.pojo.Goods;
import com.youlite.jxc.server.pojo.GoodsIn;
import com.youlite.jxc.server.pojo.GoodsOut;
import com.youlite.jxc.server.pojo.GoodsReturn;
import com.youlite.jxc.server.pojo.Group;
import com.youlite.jxc.server.pojo.User;

public class Server implements ApplicationContextAware {
	private static final Logger log = LoggerFactory.getLogger(Server.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private SystemInfo systemInfo;

	@Autowired
	private List<IPlugin> plugins;

	@Autowired
	private IRemoteEventManager eventManager;

	@Autowired
	private PersistenceManager persistenceManager;

	@Autowired
	private UserGroupManager userGroupManager;

	@Autowired
	private GoodsManager goodsManager;

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

		eventProcessor.setHandler(this);
		eventProcessor.init();
		if (eventProcessor.getThread() != null) {
			eventProcessor.getThread().setName("Server");
		}

		log.debug("PersistenceManager initialized");
		persistenceManager.init();

		Thread thread = new Thread(new Runnable() {

			public void run() {
				try {
					recover();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					System.exit(-1);
				}
			}

		});
		thread.start();
	}

	private void recover() {
		List<Group> groups = persistenceManager.recoverGroups();
		log.info("Group Definition loaded: " + groups.size());
		userGroupManager.injectGroups(groups);

		List<User> users = persistenceManager.recoverUsers();
		log.info("User Definition loaded: " + users.size());
		userGroupManager.injectUsers(users);

		List<Goods> goods = persistenceManager.recoverGoods();
		log.info("Goods Definition loaded: " + goods.size());
		goodsManager.injectGoods(goods);

		List<GoodsIn> goodsIns = persistenceManager.recoverGoodsIn();
		log.info("GoodsIn Definition loaded: " + goodsIns.size());
		goodsManager.injectGoodsIns(goodsIns);

		List<GoodsOut> goodsOuts = persistenceManager.recoverGoodsOut();
		log.info("GoodsOut Definition loaded: " + goodsOuts.size());
		goodsManager.injectGoodsOuts(goodsOuts);

		List<GoodsReturn> goodsReturns = persistenceManager
				.recoverGoodsReturn();
		log.info("GoodsReturn Definition loaded: " + goodsReturns.size());
		goodsManager.injectGoodsReturns(goodsReturns);
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
