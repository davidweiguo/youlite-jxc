package com.youlite.jxc.common.event;

public interface IAsyncEventInbox {
	void addEvent(AsyncEvent event, IAsyncEventListener listener);
}
