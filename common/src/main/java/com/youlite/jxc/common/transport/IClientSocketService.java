package com.youlite.jxc.common.transport;

import com.youlite.jxc.common.IPlugin;

public interface IClientSocketService extends IPlugin {
	boolean sendMessage(Object obj);

	boolean addListener(IClientSocketListener listener);

	boolean removeListener(IClientSocketListener listener);
}
