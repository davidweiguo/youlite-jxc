package com.youlite.jxc.server.keeper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.youlite.jxc.server.pojo.Group;
import com.youlite.jxc.server.pojo.User;

public class UserGroupKeeper {

	// k=Group id; v=Group
	private Map<String, Group> groupMap = new ConcurrentHashMap<String, Group>();

	// k=User id; v=User
	private Map<String, User> userMap = new ConcurrentHashMap<String, User>();

	public void injectGroups(List<Group> groups) {
		for (Group group : groups) {
			groupMap.put(group.getId(), group);
		}
	}

	public void injectUsers(List<User> users) {
		for (User user : users) {
			userMap.put(user.getId(), user);
		}
	}
}
