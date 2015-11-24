package com.youlite.jxc.common.event.system;

import com.youlite.jxc.common.event.RemoteAsyncEvent;

public class DuplicateSystemIdEvent extends RemoteAsyncEvent {
	private static final long serialVersionUID = 1L;
	String uid;

	public DuplicateSystemIdEvent(String key, String receiver, String uid) {
		super(key, receiver);
		this.uid = uid;
	}

	public String getUid() {
		return uid;
	}

}
