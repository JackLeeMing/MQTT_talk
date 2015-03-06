package com.ljk.mytest.http;
/**
 * 各数据库更新检测
 */
public class InspectConfig {

	// 定检
	/** 桥梁构件 */
	private long part;
	/** 桥梁病害 */
	private long disease;
	private long bridgeModel;
	// 巡检
	/** 检测项 */
	private long checkitems;
	private long bridgeInfo;
	public long getBridgeInfo() {
		return bridgeInfo;
	}

	public void setBridgeInfo(long bridgeInfo) {
		this.bridgeInfo = bridgeInfo;
	}

	public long getBridgeModel() {
		return bridgeModel;
	}

	public void setBridgeModel(long bridgeModel) {
		this.bridgeModel = bridgeModel;
	}

	public void setPart(long part) {
		this.part = part;
	}

	public long getPart() {
		return part;
	}

	public void setDisease(long disease) {
		this.disease = disease;
	}

	public long getDisease() {
		return disease;
	}

	public void setCheckItems(long checkitems) {
		this.checkitems = checkitems;
	}

	public long getCheckItem() {
		return checkitems;
	}

}
