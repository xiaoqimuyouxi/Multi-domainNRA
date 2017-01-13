/*
 * 边类
 */
package base.graph;

public class CEdge {
	private int edgeID;	//边编号
	private int sourceID;	//边起始点编号
	private int sinkID;	//边结束点编号
	private float totalBandwidthResource;	//边总带宽资源
	private float unitCost;	//边单位部分开销
	private float delay;	//边延迟
	private int type;		//用于表示这条边类型，即是域间链路，还是域内链路
	private int domainLocation;	//用于表示边所处域编号，域间链路，单独构成一个域
	
	public CEdge() {
		
	}

	public int getEdgeID() {
		return edgeID;
	}

	public void setEdgeID(int edgeID) {
		this.edgeID = edgeID;
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

	public float getTotalBandwidthResource() {
		return totalBandwidthResource;
	}

	public void setTotalBandwidthResource(float totalBandwidthResource) {
		this.totalBandwidthResource = totalBandwidthResource;
	}

	public float getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(float unitCost) {
		this.unitCost = unitCost;
	}

	public float getDelay() {
		return delay;
	}

	public void setDelay(float delay) {
		this.delay = delay;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getDomainLocation() {
		return domainLocation;
	}

	public void setDomainLocation(int domainLocation) {
		this.domainLocation = domainLocation;
	}
	
	
}
