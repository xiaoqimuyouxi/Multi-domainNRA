/*
 * 节点类
 */
package base.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CVertex {
	private int vertexID;	//节点编号
	private float totalComputeResource;	//节点计算资源总容量
	private float unitCost;	//启动该节点所需要的开销
	private float delay;		//节点处理时延
	private int domainLocation;	//节点所处域的编号
	private boolean isBorder;		//该节点是否是边界节点
	
	public List<Integer> adjVertexList = new ArrayList<Integer>();		//该节点的所有邻接节点编号
	
	public List<Integer> entryEdgeList = new ArrayList<Integer>();		//该节点所有入度边编号
	public List<Integer> outsideEdgeList = new ArrayList<Integer>();	//该节点所有出度边编号
	
	public Map<Integer, Integer> previousVertexToBorderMap = new HashMap<Integer, Integer>();		//该节点到该域中边界节点的上一个节点
	public Map<Integer, Float> minDelayToBorderMap = new HashMap<Integer, Float>();		//该节点到该域中边界节点的最短时延
	public Set<Integer> functionSet = new HashSet<Integer>();	//改点能够提供的功能集合
	
	public CVertex() {
		
	}
	public float getTotalDelayToSourceID(Integer sourceID) {
		return minDelayToBorderMap.get(sourceID);
	}
	public float getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(float unitCost) {
		this.unitCost = unitCost;
	}

	public int getVertexID() {
		return vertexID;
	}
	public void setVertexID(int vertexID) {
		this.vertexID = vertexID;
	}
	public float getTotalComputeResource() {
		return totalComputeResource;
	}
	public void setTotalComputeResource(float totalComputeResource) {
		this.totalComputeResource = totalComputeResource;
	}
	public float getDelay() {
		return delay;
	}
	public void setDelay(float delay) {
		this.delay = delay;
	}
	public int getDomainLocation() {
		return domainLocation;
	}
	public void setDomainLocation(int domainLocation) {
		this.domainLocation = domainLocation;
	}
	public boolean getIsBorder() {
		return isBorder;
	}
	public void setIsBorder(boolean isBorder) {
		this.isBorder = isBorder;
	}
	
	
	
}
