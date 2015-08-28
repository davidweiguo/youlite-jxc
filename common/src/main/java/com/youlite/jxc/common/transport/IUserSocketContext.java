package com.youlite.jxc.common.transport;

public interface IUserSocketContext {
	String getId();

	String getUser();

	void setUser(String user);

	void send(Object obj);

	void close();

	boolean isOpen();
}
