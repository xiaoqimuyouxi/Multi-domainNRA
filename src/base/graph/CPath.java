/*
 * 图中路径类。该类具有的属性包括：节点、链路、总延时
 */
package base.graph;

import java.util.HashMap;
import java.util.Map;

public class CPath {
	private float pathDelay;
	public Map<Integer, Integer> vertexMap = new HashMap<Integer, Integer>();
	public Map<Integer, Integer> edgeMap = new HashMap<Integer, Integer>();
	
	public CPath() {
		// TODO Auto-generated constructor stub
	}
	
	public int getPathHop() {
		return vertexMap.size();
	}

	public float getPathDelay() {
		return pathDelay;
	}

	public void setPathDelay(float pathDelay) {
		this.pathDelay = pathDelay;
	}
	
	
}
