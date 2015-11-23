package com.youlite.jxc.server.pojo;

import java.util.Date;

public class GoodsReturn {
	private String id;

	private String goodsOutId;

	private double qty;

	private String reason;

	private Date created;

	private String expressNum;

	private double fee;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGoodsOutId() {
		return goodsOutId;
	}

	public void setGoodsOutId(String goodsOutId) {
		this.goodsOutId = goodsOutId;
	}

	public double getQty() {
		return qty;
	}

	public void setQty(double qty) {
		this.qty = qty;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getExpressNum() {
		return expressNum;
	}

	public void setExpressNum(String expressNum) {
		this.expressNum = expressNum;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}
}
