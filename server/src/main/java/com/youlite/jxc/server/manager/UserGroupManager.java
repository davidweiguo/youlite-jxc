package com.youlite.jxc.server.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.youlite.jxc.common.IPlugin;
import com.youlite.jxc.common.event.AsyncEventProcessor;
import com.youlite.jxc.common.event.IAsyncEventManager;
import com.youlite.jxc.common.event.IRemoteEventManager;
import com.youlite.jxc.server.keeper.UserGroupKeeper;
import com.youlite.jxc.server.pojo.Group;
import com.youlite.jxc.server.pojo.User;

public class UserGroupManager implements IPlugin {

	private static final Logger log = LoggerFactory
			.getLogger(UserGroupManager.class);

	@Autowired
	private IRemoteEventManager eventManager;

	@Autowired
	private UserGroupKeeper userGroupKeeper;

	private AsyncEventProcessor eventProcessor = new AsyncEventProcessor() {

		@Override
		public void subscribeToEvents() {
			// subscribeToEvent(NodeInfoEvent.class, null);
		}

		@Override
		public IAsyncEventManager getEventManager() {
			return eventManager;
		}

	};

	public void init() throws Exception {
		eventProcessor.setHandler(this);
		eventProcessor.init();
	}

	public void uninit() {
		eventProcessor.uninit();
	}

	public void injectGroups(List<Group> groups) {
		userGroupKeeper.injectGroups(groups);
	}

	public void injectUsers(List<User> users) {
		userGroupKeeper.injectUsers(users);
	}
}
