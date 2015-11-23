package com.youlite.jxc.server;

import org.springframework.beans.factory.annotation.Autowired;

import com.youlite.jxc.common.IPlugin;
import com.youlite.jxc.server.keeper.UserGroupKeeper;

public class UserGroupManager implements IPlugin {

	@Autowired
	private UserGroupKeeper userGroupKeeper;
	
	

	public void init() throws Exception {

	}

	public void uninit() {

	}
	
	
}
