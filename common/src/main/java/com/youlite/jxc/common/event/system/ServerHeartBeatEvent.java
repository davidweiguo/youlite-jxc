package com.youlite.jxc.common.event.system;

import com.youlite.jxc.common.event.RemoteAsyncEvent;

public class ServerHeartBeatEvent extends RemoteAsyncEvent {

	private static final long serialVersionUID = 1L;

	public ServerHeartBeatEvent(String key, String receiver) {
		super(key, receiver);
	}

}
