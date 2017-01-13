/**
 * SFC请求中的节点部分
 */
package base.traffic;

public class Node {
	private int nodeID;	// 节点编号
	private float computeResourceDemand;	// 节点计算资源需求
	private int functionDemand;	//该节点的功能需求
	
	public int getNodeID() {
		return nodeID;
	}
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}
	public float getComputeResourceDemand() {
		return computeResourceDemand;
	}
	public void setComputeResourceDemand(float computeResourceDemand) {
		this.computeResourceDemand = computeResourceDemand;
	}
	public int getFunctionDemand() {
		return functionDemand;
	}
	public void setFunctionDemand(int functionDemand) {
		this.functionDemand = functionDemand;
	}
	
	
}
