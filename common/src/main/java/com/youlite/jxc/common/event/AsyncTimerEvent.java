package com.youlite.jxc.common.event;

public final class AsyncTimerEvent extends AsyncEvent {

	private static final long serialVersionUID = 1L;

	public AsyncTimerEvent() {
		super();
		setPriority(EventPriority.HIGH);
	}

}
