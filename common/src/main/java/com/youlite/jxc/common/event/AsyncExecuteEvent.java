package com.youlite.jxc.common.event;

public class AsyncExecuteEvent extends AsyncEvent {

	private static final long serialVersionUID = 1L;

	IAsyncEventListener innerListener;

	AsyncEvent innerEvent;

	public AsyncExecuteEvent(IAsyncEventListener listener, AsyncEvent event) {
		super();
		this.innerListener = listener;
		this.innerEvent = event;
	}

	public IAsyncEventListener getInnerListener() {
		return innerListener;
	}

	public AsyncEvent getInnerEvent() {
		return innerEvent;
	}
}
