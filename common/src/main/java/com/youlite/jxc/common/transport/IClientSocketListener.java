package com.youlite.jxc.common.transport;

public interface IClientSocketListener {
	void onConnected(boolean connected);

	void onMessage(Object obj);
}
