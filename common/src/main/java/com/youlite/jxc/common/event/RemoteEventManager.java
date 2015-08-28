package com.youlite.jxc.common.event;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.youlite.jxc.common.transport.IObjectListener;
import com.youlite.jxc.common.transport.IObjectTransportService;

public class RemoteEventManager extends AsyncEventManager implements
		IRemoteEventManager {
	private static final Logger log = LoggerFactory
			.getLogger(RemoteEventManager.class);
	private IObjectTransportService transport;
	private boolean embedBroker;
	private String channel;
	private String inbox;
	private ConcurrentHashMap<String, IAsyncEventBridge> bridgeMap = new ConcurrentHashMap<String, IAsyncEventBridge>();
	private List<IAsyncEventBridge> bridges;

	class RemoteListener implements IObjectListener {
		public void onMessage(Object obj) {
			if (obj instanceof RemoteAsyncEvent) {
				RemoteAsyncEvent event = (RemoteAsyncEvent) obj;
				if (inbox.equals(event.getSender())) // not interested in the
														// event sent by self
					return;
				RemoteEventManager.super.sendEvent(event);
			}

		}
	}

	protected RemoteEventManager() {

	}

	public RemoteEventManager(IObjectTransportService transport) {
		this.transport = transport;
	}

	public void init(String channel, String inbox) throws Exception {
		if (null != bridges) {
			for (IAsyncEventBridge bridge : bridges) {
				IAsyncEventBridge existing = bridgeMap.put(
						bridge.getBridgeId(), bridge);
				if (null != existing)
					throw new Exception("Duplicate IAsyncEventBridge id: "
							+ bridge.getBridgeId());
			}
		}

		if (null == transport)
			throw new Exception("Transport isn't instantiated");
		this.channel = channel;
		this.inbox = inbox;

		if (embedBroker)
			transport.startBroker();
		transport.startService();

		transport.createReceiver(inbox, new RemoteListener());
	}

	public void addEventInbox(String queue) throws Exception {
		transport.createReceiver(queue, new RemoteListener());
	}

	public void addEventChannel(String channel) throws Exception {
		transport.createSubscriber(channel, new RemoteListener());
	}

	public void uninit() {
		try {
			close();
		} catch (Exception e) {
		}
	}

	public void finalize() throws Throwable {
		close();
		super.finalize();
	}

	public void close() throws Exception {
		log.debug("Closing transport...");
		clearAllSubscriptions();
		transport.closeService();
		if (embedBroker)
			transport.closeBroker();
	}

	private void sendToAllBridges(RemoteAsyncEvent event) {
		for (IAsyncEventBridge bridge : bridgeMap.values()) {
			bridge.onBridgeEvent(event);
		}
	}

	public void sendLocalOrRemoteEvent(RemoteAsyncEvent event) throws Exception {
		if (event.getReceiver() == null)
			sendEvent(event);
		else
			sendRemoteEvent(event);
	}

	public void sendRemoteEvent(RemoteAsyncEvent event) throws Exception {
		if (event.getSender() == null)
			event.setSender(inbox);

		if (event.getReceiver() == null) {
			sendToAllBridges(event);
			transport.publishMessage(channel, event);
		} else {
			IAsyncEventBridge bridge = bridgeMap.get(event.getReceiver());
			if (bridge != null)
				bridge.onBridgeEvent(event);
			else
				transport.sendMessage(event.getReceiver(), event);
		}
	}

	public void publishRemoteEvent(String channel, RemoteAsyncEvent event)
			throws Exception {
		if (event.getSender() == null)
			event.setSender(inbox);

		sendToAllBridges(event);
		transport.publishMessage(channel, event);
	}

	public void sendGlobalEvent(RemoteAsyncEvent event) throws Exception {
		super.sendEvent(event);
		sendRemoteEvent(event);
	}

	public boolean isEmbedBroker() {
		return embedBroker;
	}

	public void setEmbedBroker(boolean embedBroker) {
		this.embedBroker = embedBroker;
	}

	public List<IAsyncEventBridge> getBridges() {
		return bridges;
	}

	public void setBridges(List<IAsyncEventBridge> bridges) {
		this.bridges = bridges;
	}

}
