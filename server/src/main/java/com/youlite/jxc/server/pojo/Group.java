package com.youlite.jxc.server.pojo;

import java.io.Serializable;
import java.util.Set;

public class Group implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private Set<User> users;

	public Group() {
	}

	public Group(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
}
