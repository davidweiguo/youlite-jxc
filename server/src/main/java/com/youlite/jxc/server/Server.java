package com.youlite.jxc.server;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.Date;
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
import com.youlite.jxc.common.event.AsyncTimerEvent;
import com.youlite.jxc.common.event.IAsyncEventManager;
import com.youlite.jxc.common.event.IRemoteEventManager;
import com.youlite.jxc.common.event.ScheduleManager;
import com.youlite.jxc.common.event.system.DuplicateSystemIdEvent;
import com.youlite.jxc.common.event.system.NodeInfoEvent;
import com.youlite.jxc.common.event.system.ServerHeartBeatEvent;
import com.youlite.jxc.common.event.system.ServerReadyEvent;
import com.youlite.jxc.common.util.TimeUtil;
import com.youlite.jxc.server.manager.GoodsManager;
import com.youlite.jxc.server.manager.UserGroupManager;
import com.youlite.jxc.server.persistence.PersistenceManager;
import com.youlite.jxc.server.pojo.Goods;
import com.youlite.jxc.server.pojo.GoodsIn;
import com.youlite.jxc.server.pojo.GoodsOut;
import com.youlite.jxc.server.pojo.GoodsReturn;
import com.youlite.jxc.server.pojo.Group;
import com.youlite.jxc.server.pojo.User;
import com.youlite.jxc.server.util.IdGenerator;

public class Server implements ApplicationContextAware {
	private static final Logger log = LoggerFactory.getLogger(Server.class);

	private String inbox;
	private String uid;
	private String channel;
	private String nodeInfoChannel;
	private int heartBeatInterval = 3000; // 3000 miliseconds
	private String shutdownTime;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private SystemInfo systemInfo;

	@Autowired
	private List<IPlugin> plugins;

	@Autowired
	private IRemoteEventManager eventManager;

	@Autowired
	ScheduleManager scheduleManager;

	@Autowired
	private PersistenceManager persistenceManager;

	@Autowired
	private UserGroupManager userGroupManager;

	@Autowired
	private GoodsManager goodsManager;

	private AsyncTimerEvent timerEvent = new AsyncTimerEvent();
	private AsyncTimerEvent shutdownEvent = new AsyncTimerEvent();

	private ServerHeartBeatEvent heartBeat = new ServerHeartBeatEvent(null,
			null);

	private AsyncEventProcessor eventProcessor = new AsyncEventProcessor() {

		@Override
		public void subscribeToEvents() {
			subscribeToEvent(NodeInfoEvent.class, null);
		}

		@Override
		public IAsyncEventManager getEventManager() {
			return eventManager;
		}

	};

	public void init() throws Exception {
		IdGenerator.getInstance().setPrefix(systemInfo.getId() + "-");

		// create node.info subscriber and publisher
		log.info("SystemInfo: " + systemInfo);
		this.channel = systemInfo.getEnv() + "." + systemInfo.getCategory()
				+ "." + "channel";
		this.nodeInfoChannel = systemInfo.getEnv() + "."
				+ systemInfo.getCategory() + "." + "node";
		InetAddress addr = InetAddress.getLocalHost();
		String hostName = addr.getHostName();
		this.inbox = systemInfo.getEnv() + "." + systemInfo.getCategory() + "."
				+ systemInfo.getId();
		IdGenerator.getInstance().setSystemId(this.inbox);
		this.uid = hostName + "." + IdGenerator.getInstance().getNextID();

		eventManager.init(channel, inbox);
		eventManager.addEventChannel(nodeInfoChannel);

		eventProcessor.setHandler(this);
		eventProcessor.init();
		if (eventProcessor.getThread() != null) {
			eventProcessor.getThread().setName(Server.class.getName());
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

		NodeInfoEvent nodeInfo = new NodeInfoEvent(null, null, true, true,
				inbox, uid);
		nodeInfo.setSender(uid);

		eventManager.publishRemoteEvent(nodeInfoChannel, nodeInfo);
		log.info("Published my node info");

		// start heart beat
		scheduleManager.scheduleRepeatTimerEvent(heartBeatInterval,
				eventProcessor, timerEvent);
		registerShutdownTime();
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

	public void processNodeInfoEvent(NodeInfoEvent event) throws Exception {
		if (event.getFirstTime() && !event.getUid().equals(Server.this.uid)) {
			// check duplicate system id
			if (event.getServer() && event.getInbox().equals(Server.this.inbox)) {
				log.error("Duplicated system id detected: " + event.getSender());
				DuplicateSystemIdEvent de = new DuplicateSystemIdEvent(null,
						null, event.getUid());
				de.setSender(Server.this.uid);
				eventManager.publishRemoteEvent(nodeInfoChannel, de);
			} else {
				// publish my node info
				NodeInfoEvent myInfo = new NodeInfoEvent(null, null, true,
						false, Server.this.inbox, Server.this.uid);
				eventManager.publishRemoteEvent(nodeInfoChannel, myInfo);
				log.info("Replied my nodeInfo");
			}
			if (!event.getServer()) {
				try {
					eventManager.publishRemoteEvent(channel,
							new ServerReadyEvent(true));
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	public void processAsyncTimerEvent(AsyncTimerEvent event) throws Exception {
		if (event == timerEvent) {
			eventManager.publishRemoteEvent(nodeInfoChannel, heartBeat);
		} else if (event == shutdownEvent) {
			log.info("System hits end time, shutting down...");
			System.exit(0);
		}
	}

	private void registerShutdownTime() throws ParseException {
		if (null == shutdownTime)
			return;
		Date endTime = TimeUtil.parseTime("HH:mm:ss", shutdownTime);

		scheduleManager.scheduleTimerEvent(endTime, eventProcessor,
				shutdownEvent);
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
