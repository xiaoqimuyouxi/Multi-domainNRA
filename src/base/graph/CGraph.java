/*
 * 图类
 */
package base.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import base.Configure;

public class CGraph {
	private int vertexNum;		//图中节点数
	private int edgeNum;		//图中边数
	private boolean isDeleteEdge = false;	//用于表示，是否进行了删边操作，默认表示没有
	
	public Map<Integer, CVertex> vertexMap = new HashMap<Integer, CVertex>();		//图中节点集合，与节点编号构成一个map
	public Map<Integer, CEdge> edgeMap = new HashMap<Integer, CEdge>();		//图中边集合，与边编号构成一个map
	
	public Map<Integer, List<Integer>> domainVertexMap	= new HashMap<Integer, List<Integer>>();	//该图中，域编号以及该域中的节点集合构成的Map
	public Map<Integer, List<Integer>> domainBorderVertexMap	= new HashMap<Integer, List<Integer>>();	//该图中，域编号以及该域中的边界节点集合构成的Map
	public Map<Integer, List<Integer>> domainEdgeMap = new HashMap<Integer, List<Integer>>();	// 该图中，域编号以及该域中边编号集合构成的Map
	public Map<Integer, Set<Integer>> domainFunctionSetMap = new HashMap<Integer, Set<Integer>>();	// 该图中，域编号以及该域所能提供功能集合
	public List<Integer> allBorderVertexList = new ArrayList<Integer>();	//该图中所有边界节点的编号，这个list作为参数传入SFCManager类中的SFC生成函数，用于帮助指定SFC的源和目的
	
	public CGraph() {
	}
	
	/*
	 * 功能：计算每个域中所有边界节点间的最短延时路径
	 */
	public boolean dijkstraAllBorderInEachDomain() {
		for (Integer domainKey : domainBorderVertexMap.keySet()) {
			for (Integer vertexKey : domainBorderVertexMap.get(domainKey)) {
				if (!dijkstraAllInDomain(vertexKey, domainKey)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/*
	 * 功能：计算给定域中域中指定点到其他点的最短路径及最短延时
	 */
	public boolean dijkstraAllInDomain(int sourceID, int domainID) {
		List<Integer> tempList = new ArrayList<Integer>();
		tempList.addAll(domainVertexMap.get(domainID));
		
		for (Integer key : tempList) {
			vertexMap.get(key).minDelayToBorderMap.put(sourceID, Configure.INF);
			vertexMap.get(key).previousVertexToBorderMap.put(sourceID, Configure.IMPOSSIBLENODE);
		}
		
		vertexMap.get(sourceID).minDelayToBorderMap.put(sourceID, (float)0);
		
		int it = sourceID;	//每次迭代的节点编号
		float minDelay;
		int minDelayVertex;
		while (!tempList.isEmpty()) {
			CVertex vertex = vertexMap.get(it);
			for (Integer edgeKey : vertex.outsideEdgeList) {
				CEdge edge = edgeMap.get(edgeKey);
				if (edge.getDomainLocation() == vertex.getDomainLocation()) {
					CVertex sinkVertex = vertexMap.get(edge.getSinkID());
					if (vertex.getTotalDelayToSourceID(sourceID) + edge.getDelay() < sinkVertex.getTotalDelayToSourceID(sourceID)) {
						sinkVertex.minDelayToBorderMap.put(sourceID, vertex.getTotalDelayToSourceID(sourceID) + edge.getDelay());
						sinkVertex.previousVertexToBorderMap.put(sourceID, it);
					}
				}
			}
			// 从剩余节点中，找出延迟总量最小的节点
			minDelay = Configure.INF;
			minDelayVertex = Configure.IMPOSSIBLENODE;
			for (Integer vertexKey : tempList) {
				if (minDelay > vertexMap.get(vertexKey).getTotalDelayToSourceID(sourceID)) {
					minDelay = vertexMap.get(vertexKey).getTotalDelayToSourceID(sourceID);
					minDelayVertex = vertexMap.get(vertexKey).getVertexID();
				}
			}
			it = minDelayVertex;
			tempList.remove(tempList.indexOf(minDelayVertex));
		}
		return true;
	}
	
	/*
	 * 功能：将每个域中的所有节点所能提供功能汇总，这个信息可以暴露给第三方
	 */
	public void functionDomain() {
		for (Integer domainKey : domainVertexMap.keySet()) {
			Set<Integer> function = new HashSet<Integer>();
			for (Integer vertexKey : domainVertexMap.get(domainKey)) {
				function.addAll(vertexMap.get(vertexKey).functionSet);
			}
			domainFunctionSetMap.put(domainKey, function);
		}
	}
	
	/*
	 * 功能：将图中的边按域分开，需要注意的是，域间链路单独一个域。同时，在对边遍历的时候，
	 * 为节点处理邻接节点、入度边、出度边
	 */
	public void edgeDomain() {
		for (Integer key : edgeMap.keySet()) {
			CEdge edge = edgeMap.get(key);
			//将边分入对应的域中
			if (!domainEdgeMap.containsKey(edge.getDomainLocation())) {
				List<Integer> domainEdgeList = new ArrayList<Integer>();
				domainEdgeList.add(edge.getEdgeID());
				domainEdgeMap.put(edge.getDomainLocation(), domainEdgeList);
			} else {
				domainEdgeMap.get(edge.getDomainLocation()).add(edge.getEdgeID());
			}
			//处理节点的邻接信息
			vertexMap.get(edge.getSourceID()).outsideEdgeList.add(edge.getEdgeID());
			vertexMap.get(edge.getSinkID()).entryEdgeList.add(edge.getEdgeID());
			vertexMap.get(edge.getSourceID()).adjVertexList.add(edge.getSinkID());
		}
	}
	
	/*
	 * 功能：将每个域中的节点分域
	 */
	public void vertexDomain() {
		for (Integer key : vertexMap.keySet()) {
			CVertex vertex = vertexMap.get(key);
			if (!domainVertexMap.containsKey(vertex.getDomainLocation())) {
				List<Integer> domainVertexList = new ArrayList<Integer>();
				domainVertexList.add(vertex.getVertexID());
				domainVertexMap.put(vertex.getDomainLocation(), domainVertexList);
			} else {
				domainVertexMap.get(vertex.getDomainLocation()).add(vertex.getVertexID());
			}
		}
		
		for (Integer key : domainVertexMap.keySet()) {
			List<Integer> domainVertexList = domainVertexMap.get(key);
			
			for (Integer vertexKey : domainVertexList) {
				CVertex vertex = vertexMap.get(vertexKey);
				if (vertex.getIsBorder()) {
					if (!domainBorderVertexMap.containsKey(vertex.getDomainLocation())) {
						List<Integer> domainBorderVertexList = new ArrayList<Integer>();
						domainBorderVertexList.add(vertex.getVertexID());
						domainBorderVertexMap.put(vertex.getDomainLocation(), domainBorderVertexList);
					} else {
						domainBorderVertexMap.get(vertex.getDomainLocation()).add(vertex.getVertexID());
					}
				}
			}
		}
		
		//将每个域中的边界节点放在一个list中，作为参数传递给SFCManager类
		for (Integer domainKey : domainBorderVertexMap.keySet()) {
			allBorderVertexList.addAll(domainBorderVertexMap.get(domainKey));
		}
	}
	public int getVertexNum() {
		return vertexNum;
	}
	public void setVertexNum(int vertexNum) {
		this.vertexNum = vertexNum;
	}
	public int getEdgeNum() {
		return edgeNum;
	}
	public void setEdgeNum(int edgeNum) {
		this.edgeNum = edgeNum;
	}

	public boolean getIsDeleteEdge() {
		return isDeleteEdge;
	}

	public void setIsDeleteEdge(boolean isDeleteEdge) {
		this.isDeleteEdge = isDeleteEdge;
	}
	
}
