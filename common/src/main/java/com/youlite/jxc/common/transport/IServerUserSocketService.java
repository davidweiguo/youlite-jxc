package com.youlite.jxc.common.transport;

import java.util.List;

import com.youlite.jxc.common.IPlugin;

public interface IServerUserSocketService extends IPlugin {
	IUserSocketContext getContext(String key);

	List<IUserSocketContext> getContextByUser(String user);

	void setUserContext(String user, IUserSocketContext ctx);

	boolean addListener(IServerSocketListener listener);

	boolean removeListener(IServerSocketListener listener);
}
