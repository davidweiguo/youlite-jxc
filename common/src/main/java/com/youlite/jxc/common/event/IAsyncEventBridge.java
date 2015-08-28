package com.youlite.jxc.common.event;

public interface IAsyncEventBridge {
	String getBridgeId();

	void onBridgeEvent(RemoteAsyncEvent event);
}
