package com.youlite.jxc.common.event.system;

import com.youlite.jxc.common.event.RemoteAsyncEvent;

public class ServerReadyEvent extends RemoteAsyncEvent {
	private static final long serialVersionUID = 1L;

	boolean ready;

	public ServerReadyEvent(boolean ready) {
		super(null, null);
		this.ready = ready;
	}

	public boolean isReady() {
		return ready;
	}

}
