/**
 * SFC请求中的链路需求
 */
package base.traffic;

public class Link {
	private int linkID;	//链路编号
	private float bandwithResourceDemand;	//链路带宽资源需求
	
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
