/**
 * SFC管理类， 即是：SFC的操作类，比如SFC的生成
 */
package base.traffic;

import java.util.HashMap;
import java.util.Map;

import base.Configure;
import base.graph.CGraph;
import base.graph.CGraphManager;

public class SFCManager {
	private CGraphManager cGraphManager;
	public Map<Integer, SFC>	sfcMap = new HashMap<Integer, SFC>();
	
	public void testTheSFCMap() {
		System.out.println("testTheSFCMap");
		for (Integer sfcKey : sfcMap.keySet()) {
			printSFC(sfcMap.get(sfcKey));
		}
	}
	
	public void printSFC(SFC sfc) {
		System.out.println(sfc.getSFCID() + " (" + sfc.getSourceID() + " ---> " + sfc.getSinkID() + " SFC长度 = " + sfc.getLength());
		for (Integer nodeKey : sfc.nodeMap.keySet()) {
			Node node = sfc.nodeMap.get(nodeKey);
			System.out.print(node.getFunctionDemand() + " ---> ");
		}
		System.out.println();
	}
	
	/*
	 * 功能：预映射一条SFC，即：为这条SFC上的需求链路与需求节点，找寻能够部署的底层节点已经底层链路
	 * 预映射的思路介绍：
	 *  一、预判（此时的数据：一条SFC + 底层操作图（底层拓扑图的拷贝））
	 *  	1. 根据底层操作图的抽象图，根据SFC的源节点和目的节点，生成左右源节点到目的节点的可达路径
	 *  	2. 对所有可达路径，按延时从低到高排序
	 *  	3. 取出一条可达路径，判断这条路径经过的域，能够完成SFC所需要的功能。若能，进入下一步，
	 *  		若不能，挑下一条可达路径
	 *  二、部署（此时的数据：一条SFC + 一条源节点到目的节点的可达路径）
	 *  三、调整（此时的数据：一条能够成功部署SFC的路径）
	 */
	public boolean preDeployOneSFC(CGraph abstractGraph, SFC sfc) {
		return sfc.checkTheLinks() && sfc.checkTheNodes();
	}
	/*
	 * 功能：产生所需要条数的SFC，一般是一万条
	 */
	public void generateSFCs() {
		for (int i = 0; i < 10; i++) {
			SFC sfc = generateOneSFC(4);	//产生长度为4的一万条SFC
			sfcMap.put(sfc.getSFCID(), sfc);
		}
		
	}
	
	/*
	 * 功能：根据指定SFC长度来生成一条SFC
	 */
	public SFC generateOneSFC(int SFCLength) {
		SFC sfc = new SFC();
		sfc.setSFCID(sfcMap.size());
		sfc.setLength(SFCLength);
		//生成需求节点
		for (int i = 0; i < SFCLength; i++) {
			Node node = sfc.createNode();
			sfc.nodeMap.put(node.getNodeID(), node);
		}
		//生成链路需求，每两个节点之间就有一条需求链路，在生成的时候，需要特别注意保持节点的编号顺序。
		for (int i = 0; i < SFCLength - 1; i++) {
			Link link = sfc.createLink(i, i+1);
			sfc.linkMap.put(link.getLinkID(), link);
		}
		//SFC持续时间的处理
		int sfcHold = Configure.createPoission();	//该条服务链持续时间，在采用的方法表示，当SFC执行到本条SFC的持续时间时，就回收该SFC所占用的资源
		sfc.setContinueTime(sfc.getSFCID() + sfcHold);
		//SFC起始和结束的处理，
		int source = Configure.random.nextInt(cGraphManager.graph.allBorderVertexList.size());
		int sink = Configure.random.nextInt(cGraphManager.graph.allBorderVertexList.size());
		System.out.println(cGraphManager.graph.allBorderVertexList.size());
		while (source == sink) {
			sink = Configure.random.nextInt(cGraphManager.graph.allBorderVertexList.size());
		}
		sfc.setSourceID(cGraphManager.graph.allBorderVertexList.get(source));
		sfc.setSinkID(cGraphManager.graph.allBorderVertexList.get(sink));
		return sfc;
	}
	
	public SFCManager(CGraphManager cGraphManager) {
		this.cGraphManager = cGraphManager;
	}

}
