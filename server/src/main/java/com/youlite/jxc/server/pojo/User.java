package com.youlite.jxc.server.pojo;

import java.io.Serializable;
import java.util.Date;

import com.youlite.jxc.server.util.Clock;

public class User implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String password;
	private String phone;
	private Date created;
	private Date lastLogin;
	private Boolean active = true;
	private Group group;
	private UserRole role;

	private User() {
		this.created = Clock.getInstance().now();
	}

	public User(String id, String password) {
		this();
		this.id = id;
		this.password = password;
	}

	public User(String id, String name, String password, String phone) {
		this();
		this.id = id;
		this.name = name;
		this.password = password;
		this.phone = phone;
	}

	public synchronized String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public synchronized String getPassword() {
		return password;
	}

	public synchronized void setPassword(String password) {
		this.password = password;
	}

	public synchronized String getId() {
		return id;
	}

	public synchronized void setId(String id) {
		this.id = id;
	}

	public synchronized Date getLastLogin() {
		return lastLogin;
	}

	public Date getCreated() {
		return created;
	}

	protected void setCreated(Date created) {
		this.created = created;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public synchronized boolean login(String id, String password) {
		boolean ok = this.id == id && this.password == password;
		if (ok)
			lastLogin = Clock.getInstance().now();
		return ok;
	}

	public synchronized Boolean getActive() {
		return active;
	}

	public synchronized void setActive(Boolean active) {
		this.active = active;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}
}
