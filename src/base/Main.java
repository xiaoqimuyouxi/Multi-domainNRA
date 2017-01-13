/*
 * 程序入口函数
 */
package base;

import base.graph.CGraphManager;

public class Main {

	public static void main(String[] args) {
		String nodeInfoPath = "/home/liyayu/code/eclipse_workplace/Multi-domainNRA/topology/NodeBaseInfo.txt";
		String edgeInfoPath = "/home/liyayu/code/eclipse_workplace/Multi-domainNRA/topology/LinkBaseInfo.txt";
		CGraphManager graphManager = new CGraphManager(nodeInfoPath, edgeInfoPath);
		graphManager.generateGraph();
//		graphManager.testVertexInfo();
//		graphManager.testEdgeInfo();
//		graphManager.testDomainVertex();
//		graphManager.testDomainBorderVertex();
//		graphManager.testDomainEdge();
//		graphManager.testVertexAdj();
//		graphManager.testVertexAdjEdge();
//		graphManager.testAllDomain();
//		graphManager.testAbstractGraph();
	}

}
