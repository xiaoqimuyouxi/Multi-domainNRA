/*
 * 图管理类
 */
package base.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import base.Configure;

public class CGraphManager {
	public CGraph graph = new CGraph();	//所管理的底层拓扑图
	private String vertexInfoPath;	//图中节点信息文件路径
	private String edgeInfoPath;	//图中边信息文件路径
	public CGraph abstractGraph = new CGraph();		//由底层拓扑抽象出的抽象网络图
	
	public CGraphManager(String vertexInfoPath, String edgeInfoPath) {
		this.vertexInfoPath = vertexInfoPath;
		this.edgeInfoPath = edgeInfoPath;
	}
	
	/*
	 * 生成底层拓扑图
	 */
	public void generateGraph() {
		generateVertex(vertexInfoPath);
		generateEdge(edgeInfoPath);
		generateTheOtherEdge();
		edgeDomain();
		vertexDomain();
		dijkstraAllBorderInEachDomain();
		generateAbstrateGraph();
	}
	
	public void testAbstractGraph() {
		System.out.println("测试抽象拓扑图");
		for (Integer vertexKey : abstractGraph.vertexMap.keySet()) {
			CVertex vertex = abstractGraph.vertexMap.get(vertexKey);
			System.out.println(vertex.getVertexID() + " " + vertex.getDomainLocation());
		}
		
		for (Integer edgeKey : abstractGraph.edgeMap.keySet()) {
			CEdge edge = abstractGraph.edgeMap.get(edgeKey);
			System.out.println(edge.getEdgeID() + " : " + edge.getSourceID() + " ----> " + edge.getSinkID() + " delay " + edge.getDelay() + " domain " + edge.getDomainLocation());
		}
	}
	
	public void testAllDomain() {
		for (Integer domainKey : graph.domainBorderVertexMap.keySet()) {
			System.out.println("doamin " + domainKey + " 边界节点之间的最短延时路径为：");
			testDijkstraInDomain(domainKey);
		}
	}
	
	public void testDijkstraInDomain(int domainIndex) {
		List<Integer> list = graph.domainBorderVertexMap.get(domainIndex);
		for (Integer firstKey : list) {
			for (Integer secondKey : list) {
				printPathBetweenTwoVertex(firstKey, secondKey, domainIndex);
			}
		}
	}
	
	public void printPathBetweenTwoVertex(int sourceID, int sinkID, int domainIndex) {
		int currentVertex = sinkID;
		CVertex vertex = graph.vertexMap.get(currentVertex);
		Integer previousVertex = vertex.previousVertexToBorderMap.get(sourceID);
		String string = "";
		while (previousVertex != Configure.IMPOSSIBLENODE) {
			string = " ----> " + currentVertex + " (totalDelay) " + vertex.minDelayToBorderMap.get(sourceID) + string;
			currentVertex = previousVertex;
			vertex = graph.vertexMap.get(currentVertex);
			previousVertex = vertex.previousVertexToBorderMap.get(sourceID);
		}
		string = "" + sourceID + string;
		System.out.println(string);
	}
	
	public void testVertexInfo() {
		for (Integer key : graph.vertexMap.keySet()) {
			System.out.println("key = " + key + " value = " + printVertex(graph.vertexMap.get(key)));
		}
	}
	
	public void testEdgeInfo() {
		for (Integer key : graph.edgeMap.keySet()) {
			System.out.println("key = " + key + " value = " + printEdge(graph.edgeMap.get(key)));
		}
	}
	
	public void testDomainVertex() {
		System.out.println("Test the vertex of domain");
		for (Integer key : graph.domainVertexMap.keySet()) {
			System.out.print("Doamin " + key + " ---> ");
			for (Integer vertexKey : graph.domainVertexMap.get(key)) {
				System.out.print(vertexKey + " ");
			}
			System.out.println();
		}
	}
	
	public void testDomainBorderVertex() {
		System.out.println("Test the borderVertex of domain");
		for (Integer key : graph.domainBorderVertexMap.keySet()) {
			System.out.print("Doamin " + key + " ---> ");
			for (Integer vertexKey : graph.domainBorderVertexMap.get(key)) {
				System.out.print(vertexKey + " ");
			}
			System.out.println();
		}
	}
	
	public void testDomainEdge() {
		System.out.println("Test the edge of domain");
		for (Integer key :graph.domainEdgeMap.keySet()) {
			System.out.print("Doamin " + key + " ---> ");
			for (Integer edgeKey : graph.domainEdgeMap.get(key)) {
				System.out.print(edgeKey + " ");
			}
			System.out.println();
		}
	}
	
	public void testVertexAdj() {
		System.out.println("test the adjVertex of vertex");
		for (Integer key : graph.vertexMap.keySet()) {
			System.out.print("vertex " + key + " ---> ");
			for (Integer vertexKey : graph.vertexMap.get(key).adjVertexList) {
				System.out.print(vertexKey + " ");
			}
			System.out.println();
		}
	}
	
	public void testVertexAdjEdge() {
		System.out.println("Test the adjEdge of vertex");
		for (Integer key : graph.vertexMap.keySet()) {
			System.out.print("vertex " + key + " outside edge ---> ");
			for (Integer edgeKey : graph.vertexMap.get(key).outsideEdgeList) {
				System.out.print(edgeKey + " ");
			}
			System.out.println();
			System.out.print("vertex " + key + " entry edge ---> ");
			for (Integer edgeKey : graph.vertexMap.get(key).entryEdgeList) {
				System.out.print(edgeKey + " ");
			}
			System.out.println();
		}
	}
	
	public String printVertex(CVertex vertex) {
		String string = "vertex ";
		string += vertex.getVertexID() + " " + vertex.getUnitCost() + " " + vertex.getTotalComputeResource() + " ";
		string += vertex.getDomainLocation() + " " + vertex.getIsBorder();
		return string;
	}
	
	public String printEdge(CEdge edge) {
		String string = "edge ";
		string += edge.getEdgeID() + " " + edge.getSourceID() + " " + edge.getSinkID() + " " + edge.getUnitCost() + " ";
		string += edge.getTotalBandwidthResource() + " " + edge.getDelay() + " " + edge.getDomainLocation() + " " + edge.getType(); 
		return string;
	}
	
	/*
	 * 功能：生成抽象拓扑图
	 */
	public void generateAbstrateGraph() {
		for (Integer domainKey : graph.domainBorderVertexMap.keySet()) {
			for (Integer key : graph.domainBorderVertexMap.get(domainKey)) {
				CVertex vertex = graph.vertexMap.get(key);
				abstractGraph.vertexMap.put(vertex.getVertexID(), vertex);	//抽象边界节点
				//构造域内边界节点之间的抽象边
				for (Integer borderKey : vertex.minDelayToBorderMap.keySet()) {
					if (borderKey != key) {
						CEdge edge = new CEdge();
						edge.setEdgeID(abstractGraph.edgeMap.size());
						edge.setSinkID(key);
						edge.setSourceID(borderKey);
						edge.setType(Configure.INTRA_EDGE);
						edge.setDelay(vertex.minDelayToBorderMap.get(borderKey));
						edge.setDomainLocation(vertex.getDomainLocation());
						abstractGraph.edgeMap.put(edge.getEdgeID(), edge);
					}
				}
			}
		}
		
		//将域间边，加入到抽象图中
		for (Integer edgeKey : graph.edgeMap.keySet()) {
			CEdge edge = graph.edgeMap.get(edgeKey);
			if (edge.getType() == Configure.INTER_EDGE) {
				edge.setEdgeID(abstractGraph.edgeMap.size());	//根据抽象图中边集合大小来重新确定抽象图中边的编号
				abstractGraph.edgeMap.put(edge.getEdgeID(), edge);
			}
		}
	}
	
	/*
	 * 功能：计算每个域中所有边界节点间的最短延时路径
	 */
	public boolean dijkstraAllBorderInEachDomain() {
		for (Integer domainKey : graph.domainBorderVertexMap.keySet()) {
			for (Integer vertexKey : graph.domainBorderVertexMap.get(domainKey)) {
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
		tempList.addAll(graph.domainVertexMap.get(domainID));
		
		for (Integer key : tempList) {
			graph.vertexMap.get(key).minDelayToBorderMap.put(sourceID, Configure.INF);
			graph.vertexMap.get(key).previousVertexToBorderMap.put(sourceID, Configure.IMPOSSIBLENODE);
		}
		
		graph.vertexMap.get(sourceID).minDelayToBorderMap.put(sourceID, (float)0);
		
		int it = sourceID;	//每次迭代的节点编号
		float minDelay;
		int minDelayVertex;
		while (!tempList.isEmpty()) {
			CVertex vertex = graph.vertexMap.get(it);
			for (Integer edgeKey : vertex.outsideEdgeList) {
				CEdge edge = graph.edgeMap.get(edgeKey);
				if (edge.getDomainLocation() == vertex.getDomainLocation()) {
					CVertex sinkVertex = graph.vertexMap.get(edge.getSinkID());
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
				if (minDelay > graph.vertexMap.get(vertexKey).getTotalDelayToSourceID(sourceID)) {
					minDelay = graph.vertexMap.get(vertexKey).getTotalDelayToSourceID(sourceID);
					minDelayVertex = graph.vertexMap.get(vertexKey).getVertexID();
				}
			}
			it = minDelayVertex;
			tempList.remove(tempList.indexOf(minDelayVertex));
		}
		return true;
	}
	
	/*
	 * 功能：将图中的边按域分开，需要注意的是，域间链路单独一个域。同时，在对边遍历的时候，
	 * 为节点处理邻接节点、入度边、出度边
	 */
	private void edgeDomain() {
		for (Integer key : graph.edgeMap.keySet()) {
			CEdge edge = graph.edgeMap.get(key);
			//将边分入对应的域中
			if (!graph.domainEdgeMap.containsKey(edge.getDomainLocation())) {
				List<Integer> domainEdgeList = new ArrayList<Integer>();
				domainEdgeList.add(edge.getEdgeID());
				graph.domainEdgeMap.put(edge.getDomainLocation(), domainEdgeList);
			} else {
				graph.domainEdgeMap.get(edge.getDomainLocation()).add(edge.getEdgeID());
			}
			//处理节点的邻接信息
			graph.vertexMap.get(edge.getSourceID()).outsideEdgeList.add(edge.getEdgeID());
			graph.vertexMap.get(edge.getSinkID()).entryEdgeList.add(edge.getEdgeID());
			graph.vertexMap.get(edge.getSourceID()).adjVertexList.add(edge.getSinkID());
		}
	}
	
	/*
	 * 功能：将每个域中的节点分域
	 */
	private void vertexDomain() {
		for (Integer key : graph.vertexMap.keySet()) {
			CVertex vertex = graph.vertexMap.get(key);
			if (!graph.domainVertexMap.containsKey(vertex.getDomainLocation())) {
				List<Integer> domainVertexList = new ArrayList<Integer>();
				domainVertexList.add(vertex.getVertexID());
				graph.domainVertexMap.put(vertex.getDomainLocation(), domainVertexList);
			} else {
				graph.domainVertexMap.get(vertex.getDomainLocation()).add(vertex.getVertexID());
			}
		}
		
		for (Integer key : graph.domainVertexMap.keySet()) {
			List<Integer> domainVertexList = graph.domainVertexMap.get(key);
			
			for (Integer vertexKey : domainVertexList) {
				CVertex vertex = graph.vertexMap.get(vertexKey);
				if (vertex.getIsBorder()) {
					if (!graph.domainBorderVertexMap.containsKey(vertex.getDomainLocation())) {
						List<Integer> domainBorderVertexList = new ArrayList<Integer>();
						domainBorderVertexList.add(vertex.getVertexID());
						graph.domainBorderVertexMap.put(vertex.getDomainLocation(), domainBorderVertexList);
					} else {
						graph.domainBorderVertexMap.get(vertex.getDomainLocation()).add(vertex.getVertexID());
					}
				}
			}
		}
	}
	
	/*
	 * 功能：因为做的是无向图，所以需要在原来边的基础上，增加一条反向边。这条边除了源和目的节点交换外，其他属性保持一致。
	 * 需要注意：反向边的编号是在原来边的编号基础上加上图中边的总条数
	 */
	
	private void generateTheOtherEdge() {
		int edgeNum = graph.edgeMap.size();
		Map<Integer, CEdge> tempEdgeMap = new HashMap<Integer, CEdge>();
		for (Integer key : graph.edgeMap.keySet()) {
			CEdge edgeIt = graph.edgeMap.get(key);
			CEdge edge = new CEdge();
			edge.setEdgeID(edgeIt.getEdgeID() + edgeNum);
			edge.setSourceID(edgeIt.getSinkID());
			edge.setSinkID(edgeIt.getSourceID());
			edge.setDelay(edgeIt.getDelay());
			edge.setDomainLocation(edgeIt.getDomainLocation());
			edge.setUnitCost(edgeIt.getUnitCost());
			edge.setTotalBandwidthResource(edgeIt.getTotalBandwidthResource());
			edge.setType(edgeIt.getType());
			tempEdgeMap.put(edge.getEdgeID(), edge);
		}
		graph.edgeMap.putAll(tempEdgeMap);
	}
	
	/*
	 * 功能：根据节点信息文件，读取节点信息数据
	 * 参数：节点信息文件路径
	 */
 	private void generateVertex(String vertexInfo) {
		File vertexFile = new File(vertexInfo);
		try {
			Scanner scanner = new Scanner(vertexFile);
			if (scanner.hasNextLine()) {
				scanner.nextLine();		//跳过表头信息行
			}
			
			while (scanner.hasNextLine()) {
				CVertex vertex = new CVertex();
				vertex.setVertexID(scanner.nextInt());
				scanner.nextInt();	//跳过文件中的IsFacility列
				vertex.setUnitCost(Float.valueOf(String.valueOf(scanner.nextInt())));
				vertex.setTotalComputeResource(Float.valueOf(String.valueOf(scanner.nextInt())));
				//vertex.setDelay(); 延时数据读取
				scanner.nextInt(); //跳过文件中location列
				vertex.setDomainLocation(scanner.nextInt());
				vertex.setIsBorder(Configure.intToBool(scanner.nextInt()));
				graph.vertexMap.put(vertex.getVertexID(), vertex);
			}
			graph.setVertexNum(graph.vertexMap.size());
			
			if (scanner != null) {
				scanner.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println("节点信息文件不存在");
			e.printStackTrace();
		}
	}
	
	/*
	 * 功能：根据边信息文件，读取边信息
	 * 参数：边信息文件路径
	 */
	private void generateEdge(String edgeInfo) {
		File edgeFile = new File(edgeInfo);
		try {
			Scanner scanner = new Scanner(edgeFile);
			if (scanner.hasNextLine()) {
				scanner.nextLine();		//跳过表头信息行
			}
			
			while (scanner.hasNextLine()) {
				CEdge edge = new CEdge();
				edge.setEdgeID(scanner.nextInt());
				edge.setSourceID(scanner.nextInt());
				edge.setSinkID(scanner.nextInt());
				edge.setUnitCost(Float.valueOf(String.valueOf(scanner.nextInt())));
				edge.setTotalBandwidthResource(Float.valueOf(String.valueOf(scanner.nextInt())));
				edge.setDelay(Float.valueOf(String.valueOf(scanner.nextInt())));
				edge.setDomainLocation(scanner.nextInt());
				edge.setType(scanner.nextInt());
				scanner.nextLine();	//跳过最后一列
				graph.edgeMap.put(edge.getEdgeID(), edge);
			}
			graph.setEdgeNum(graph.edgeMap.size());
			
			if (scanner != null) {
				scanner.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println("边信息文件不存在");
			e.printStackTrace();
		}
	}
}
