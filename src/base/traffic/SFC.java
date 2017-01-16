/**
 * SFC请求类
 */
package base.traffic;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import base.Configure;
import base.graph.CEdge;
import base.graph.CGraph;
import base.graph.CVertex;

public class SFC {
	private int sfcID;		//SFC编号
	private int length;	//SFC长度（VNF个数）
	
	// 源和目的点，只是用来指点源域和目的域，这两个点，不用来表示功能
	private int sourceID;	//SFC起始节点、在多域中，应该是起始域中的某一个边界节点
	private int sinkID;	//SFC结束节点、在多域中，应该是目的域中的某一个边界节点
	private int continueTime;	//本条SFC持续时间，在本次方法中，在线率采用的是几条SFC在线的方式，而不是采用一条SFC的持续系统时间
	
	public Map<Integer, Node> nodeMap = new LinkedHashMap<Integer, Node>();		// SFC上的节点需求，因为SFC的顺序性，故采用LinkedHashMap数据结构
	public Map<Integer, Link> linkMap = new LinkedHashMap<Integer, Link>();
	
	public Map<Integer, Integer> nodeDeployVertexMap = new HashMap<Integer, Integer>();		//功能节点部署在底层节点上的对应Map
	public Map<Integer, List<Integer>> linkDeployEdgeMap = new HashMap<Integer, List<Integer>>();	//SFC链路在底层网络上的部署路径，（这其中包括作为转发节点的中间节点）
	
	/*
	 * 功能： 部署SFC，具体体现在，部署节点以及部署链路上资源的扣除
	 */
	public boolean deploySFC(CGraph graph) {
		if (checkTheLinks() & checkTheNodes()) {
			deployNode(graph);
			deployLink(graph);
			return true;
		}
		return false;
	}
	
	/*
	 * 功能：部署SFC中的节点
	 */
	public void deployNode(CGraph graph) {
		for (Integer nodeKey : nodeDeployVertexMap.keySet()) {
			CVertex vertex = graph.vertexMap.get(nodeDeployVertexMap.get(nodeKey));
			vertex.setTotalComputeResource(vertex.getTotalComputeResource() - nodeMap.get(nodeKey).getComputeResourceDemand());
		}
	}
	
	/*
	 * 功能： 部署SFC中的链路
	 */
	public void deployLink(CGraph graph) {
		for (Integer linkKey : linkDeployEdgeMap.keySet()) {
			Link link = linkMap.get(linkKey);
			for (Integer edgeKey : linkDeployEdgeMap.get(linkKey)) {
				CEdge edge = graph.edgeMap.get(edgeKey);
				edge.setTotalBandwidthResource(edge.getTotalBandwidthResource() - link.getBandwithResourceDemand());
			}
		}
	}
	
	
	/*
	 * 功能：判断SFC中的Node是否已经全部找到部署节点
	 */
	public boolean checkTheNodes() {
		for (Integer nodeKey : nodeMap.keySet()) {
			if (!nodeDeployVertexMap.containsKey(nodeKey)) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * 功能：判断SFC中的Link是否都已经找到部署边或者部署路径（路径就是：有的边可能需要中介节点）
	 */
	public boolean checkTheLinks() {
		for (Integer linkkey : linkMap.keySet()) {
			if (!linkDeployEdgeMap.containsKey(linkkey)) {
				return false;
			}
		}
		return true;
	}
	
	//SFC部署完成后，资源回收
	public void recycleResource(CGraph graph) {
		recycleVertexComputeResource(graph);
		recycleEdgeBandwithResource(graph);
	}
	//SFC部署完成后，节点计算资源回收
	private void recycleVertexComputeResource(CGraph graph) {
		for (Integer nodeKey : nodeDeployVertexMap.keySet()) {
			CVertex vertex = graph.vertexMap.get(nodeDeployVertexMap.get(nodeKey));
			vertex.setTotalComputeResource(vertex.getTotalComputeResource() + nodeMap.get(nodeKey).getComputeResourceDemand());
		}
	}
	//SFC部署完成后，链路带宽资源回收
	private void recycleEdgeBandwithResource(CGraph graph) {
		for (Integer linkKey : linkDeployEdgeMap.keySet()) {
			Link link = linkMap.get(linkKey);
			for (Integer edgeKey : linkDeployEdgeMap.get(linkKey)) {
				CEdge edge = graph.edgeMap.get(edgeKey);
				edge.setTotalBandwidthResource(edge.getTotalBandwidthResource() + link.getBandwithResourceDemand());
			}
		}
	}
	
	//SFC类中，提供产生需求Node以及需求Link的函数
	public Node createNode() {
		Node node = new Node();
		node.setNodeID(nodeMap.size());
		node.setComputeResourceDemand(Configure.random.nextInt(10)%2 + 1);	//节点开销为1和2，这个可以继续调整
		node.setFunctionDemand(Configure.random.nextInt(Configure.FUNCTION_SEED) + 1);	//节点所需求的功能
		return node;
	}
	
	public Link createLink(int source, int sink) {
		Link link = new Link();
		link.setLinkID(linkMap.size());
		link.setSourceID(source);
		link.setSinkID(sink);
		link.setBandwithResourceDemand((float)(Configure.random.nextInt(12)%4 + 1 ) * 5);	//链路开销5、10、15和20
		return link;
	}
	
	public SFC() {
		// TODO Auto-generated constructor stub
	}
	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getContinueTime() {
		return continueTime;
	}

	public void setContinueTime(int continueTime) {
		this.continueTime = continueTime;
	}


	public int getSFCID() {
		return sfcID;
	}
	public void setSFCID(int sfcID) {
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
