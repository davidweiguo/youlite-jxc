package com.youlite.jxc.common.util;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.youlite.jxc.common.IClockListener;

public class Clock {
	public enum Mode { AUTO, MANUAL } 
	private static Clock instance;
	private Mode mode = Mode.AUTO;
	private Date manualClock = new Date(0);
	private Set<IClockListener> listeners = new HashSet<IClockListener>();
	
	private Clock() {
	}
	public static Clock getInstance() {
		if (null == instance) {
			instance = new Clock();
		}
		return instance;
	}
	public Mode getMode() {
		return mode;
	}
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	public void setManualClock(Date manualClock) {
		this.manualClock = new Date(manualClock.getTime());
		for(IClockListener listener: listeners) {
			listener.onTime(new Date(manualClock.getTime()));
		}
	}
	
	public Date now() {
		if (mode == Mode.MANUAL)
			return new Date(manualClock.getTime());
		
		return new Date();
	}
	
	public boolean addClockListener(IClockListener listener) {
		return listeners.add(listener);
	}
	
	public boolean removeClockListener(IClockListener listener) {
		return listeners.remove(listener);
	}
	
	public boolean isManual() {
		return mode.equals(Mode.MANUAL);
	}
}
