/*
 * 图管理类
 */
package base.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import base.Configure;

public class CGraphManager {
	public CGraph graph = new CGraph();	//所管理的底层拓扑图，在整个部署过程中，graph只有节点资源、链路资源的变化
	private String vertexInfoPath;	//图中节点信息文件路径
	private String edgeInfoPath;	//图中边信息文件路径
	public CGraph operateGraph = new CGraph();	//在程序中，
	public CGraph initAbstractGraph = new CGraph();		//由底层拓扑初次抽象出的抽象网络图
	
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
		functionDomain();
		generateOperateGraph();
		generateInitAbstrateGraph();
	}
	
	public void testDomainFunctionSet() {
		System.out.println("测试各个域所能提供的功能集合：");
		for (Integer domainKey : graph.domainFunctionSetMap.keySet()) {
			System.out.print("域 " + domainKey + " 所能提供的功能集合为：");
			for (Integer functionKey : graph.domainFunctionSetMap.get(domainKey)) {
				System.out.print(functionKey + " ");
			}
			System.out.println();
		}
	}
	
	public void testNodeFunctionSet() {
		System.out.println("测试每个节点的功能集合：");
		for (Integer vertexKey : graph.vertexMap.keySet()) {
			System.out.print("节点 " + vertexKey + " 的功能集合为：");
			for (Integer functionKey : graph.vertexMap.get(vertexKey).functionSet) {
				System.out.print(functionKey + " ");
			}
			System.out.println();
		}
	}
	
	public void testAbstractGraph() {
		System.out.println("测试抽象拓扑图");
		for (Integer vertexKey : initAbstractGraph.vertexMap.keySet()) {
			CVertex vertex = initAbstractGraph.vertexMap.get(vertexKey);
			System.out.println(vertex.getVertexID() + " " + vertex.getDomainLocation());
		}
		
		for (Integer edgeKey : initAbstractGraph.edgeMap.keySet()) {
			CEdge edge = initAbstractGraph.edgeMap.get(edgeKey);
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
	 * 功能：根据底层拓扑图，生成实际操作用的图（两张图所有数据一致），只是操作图在找路过程中，可能会出现删边的情况
	 */
	public void generateOperateGraph() {
		operateGraph.setVertexNum(graph.getVertexNum());
		operateGraph.setEdgeNum(graph.getVertexNum());
		operateGraph.setIsDeleteEdge(graph.getIsDeleteEdge());
		operateGraph.vertexMap.putAll(graph.vertexMap);
		operateGraph.edgeMap.putAll(graph.edgeMap);
		operateGraph.domainVertexMap.putAll(graph.domainVertexMap);
		operateGraph.domainBorderVertexMap.putAll(graph.domainBorderVertexMap);
		operateGraph.domainEdgeMap.putAll(graph.domainEdgeMap);
		operateGraph.domainFunctionSetMap.putAll(graph.domainFunctionSetMap);
		operateGraph.allBorderVertexList.addAll(graph.allBorderVertexList);
	}
	
	/*
	 * 功能：在删边后，原来的抽象图边不能在使用（因为删边后，边界节点到其他边界节点的延时也会改变）
	 * 需要更新抽象拓扑。如果不删边，原来的抽象图在后面的SFC部署中，也能继续使用
	 */
	public void updateAbstrateGraph() {
		if (operateGraph.getIsDeleteEdge()) {
			generateInitAbstrateGraph();
		}
	}
	
	/*
	 * 功能：生成抽象拓扑图
	 */
	public void generateInitAbstrateGraph() {
		for (Integer domainKey : graph.domainBorderVertexMap.keySet()) {
			for (Integer key : graph.domainBorderVertexMap.get(domainKey)) {
				CVertex vertex = graph.vertexMap.get(key);
				initAbstractGraph.vertexMap.put(vertex.getVertexID(), vertex);	//抽象边界节点
				//构造域内边界节点之间的抽象边
				for (Integer borderKey : vertex.minDelayToBorderMap.keySet()) {
					if (borderKey != key) {
						CEdge edge = new CEdge();
						edge.setEdgeID(initAbstractGraph.edgeMap.size());
						edge.setSinkID(key);
						edge.setSourceID(borderKey);
						edge.setType(Configure.INTRA_EDGE);
						edge.setDelay(vertex.minDelayToBorderMap.get(borderKey));
						edge.setDomainLocation(vertex.getDomainLocation());
						initAbstractGraph.edgeMap.put(edge.getEdgeID(), edge);
					}
				}
			}
		}
		
		//将域间边，加入到抽象图中
		for (Integer edgeKey : graph.edgeMap.keySet()) {
			CEdge edge = graph.edgeMap.get(edgeKey);
			if (edge.getType() == Configure.INTER_EDGE) {
				edge.setEdgeID(initAbstractGraph.edgeMap.size());	//根据抽象图中边集合大小来重新确定抽象图中边的编号
				initAbstractGraph.edgeMap.put(edge.getEdgeID(), edge);
			}
		}
		
		//抽象图中，各个域所能提供功能
		initAbstractGraph.domainFunctionSetMap.putAll(graph.domainFunctionSetMap);
	}
	
	/*
	 * 功能：计算每个域中所有边界节点间的最短延时路径
	 */
	public boolean dijkstraAllBorderInEachDomain() {
		return graph.dijkstraAllBorderInEachDomain();
	}
	
	/*
	 * 功能：将每个域中的所有节点所能提供功能汇总，这个信息可以暴露给第三方
	 */
	private void functionDomain() {
		graph.functionDomain();
	}
	
	/*
	 * 功能：将图中的边按域分开，需要注意的是，域间链路单独一个域。同时，在对边遍历的时候，
	 * 为节点处理邻接节点、入度边、出度边
	 */
	private void edgeDomain() {
		graph.edgeDomain();
	}
	
	/*
	 * 功能：将每个域中的节点分域
	 */
	private void vertexDomain() {
		graph.vertexDomain();
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
				//为节点附加所能提供功能集合，为节点随机生成多少个功能集合
				for (int i = 0; i < Configure.random.nextInt(Configure.FUNCTION_SEED) + 1; i++) {
					Integer function = Configure.random.nextInt(Configure.FUNCTION_SEED) + 1;
					// while循环保证了，为节点提供的功能集合，不能重复
					while (vertex.functionSet.contains(function)) {
						function = Configure.random.nextInt(Configure.FUNCTION_SEED) + 1;
					}
					vertex.functionSet.add(function);
				}
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
