/**
 * SFC请求类
 */
package base.traffic;

import java.util.LinkedHashMap;
import java.util.Map;

public class SFC {
	private int sfcID;		//SFC编号
//	private int length;	//SFC长度（VNF个数）
	
	// 源和目的点，只是用来指点源域和目的域，这两个点，不用来表示功能
	private int sourceID;	//SFC起始节点、在多域中，应该是起始域中的某一个边界节点
	private int sinkID;	//SFC结束节点、在多域中，应该是起始域中的某一个边界节点
	private int deployTime;	//本条SFC部署时间
	private int continueTime;	//本条SFC持续时间
	
	public Map<Integer, Node> nodeMap = new LinkedHashMap<Integer, Node>();		// SFC上的节点需求，因为SFC的顺序性，故采用LinkedHashMap数据结构
	public Map<Integer, Link> linkMap = new LinkedHashMap<Integer, Link>();
	
	
	
	public int getDeployTime() {
		return deployTime;
	}
	public void setDeployTime(int deployTime) {
		this.deployTime = deployTime;
	}
	public int getContinueTime() {
		return continueTime;
	}
	public void setContinueTime(int continueTime) {
		this.continueTime = continueTime;
	}
	public int getLength() {
		return nodeMap.size();
	}
	public int getSfcID() {
		return sfcID;
	}
	public void setSfcID(int sfcID) {
		this.sfcID = sfcID;
	}
	public int getSourceID() {
		return sourceID;
	}
	public void setSourceID(int sourceID) {
		this.sourceID = sourceID;
	}
	public int getSinkID() {
		return sinkID;
	}
	public void setSinkID(int sinkID) {
		this.sinkID = sinkID;
	}
	
	
}
