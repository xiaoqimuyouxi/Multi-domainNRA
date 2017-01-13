/*
 * 图类
 */
package base.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CGraph {
	private int vertexNum;		//图中节点数
	private int edgeNum;		//图中边数
	
	Map<Integer, CVertex> vertexMap = new HashMap<Integer, CVertex>();		//图中节点集合，与节点编号构成一个map
	Map<Integer, CEdge> edgeMap = new HashMap<Integer, CEdge>();		//图中边集合，与边编号构成一个map
	
	Map<Integer, List<Integer>> domainVertexMap	= new HashMap<Integer, List<Integer>>();	//该图中，域编号以及该域中的节点集合构成的Map
	Map<Integer, List<Integer>> domainBorderVertexMap	= new HashMap<Integer, List<Integer>>();	//该图中，域编号以及该域中的边界节点集合构成的Map
	Map<Integer, List<Integer>> domainEdgeMap = new HashMap<Integer, List<Integer>>();	// 该图中，域编号以及该域中边编号集合构成的Map
	Map<Integer, Set<Integer>> domainFunctionSetMap = new HashMap<Integer, Set<Integer>>();	// 该图中，域编号以及该域所能提供功能集合
	
	public CGraph() {
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
	
}
