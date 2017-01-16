/**
 * SFC请求中的链路需求
 */
package base.traffic;

public class Link {
	private int linkID;	//链路编号
	private int sourceID;	//链路需求起始节点
	private int sinkID;	//链路需求终止节点
	private float bandwithResourceDemand;	//链路带宽资源需求
	
	
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
	public int getLinkID() {
		return linkID;
	}
	public void setLinkID(int linkID) {
		this.linkID = linkID;
	}
	public float getBandwithResourceDemand() {
		return bandwithResourceDemand;
	}
	public void setBandwithResourceDemand(float bandwithResourceDemand) {
		this.bandwithResourceDemand = bandwithResourceDemand;
	}
	
	
}
