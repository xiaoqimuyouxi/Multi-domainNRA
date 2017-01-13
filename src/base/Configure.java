/*
 * 配置信息类，用于存放仿真中可能用到的参数，方便调试
 */
package base;

public class Configure {

	public static float INF = (float) 10000000000.0;	//定义十亿为无限大
	public static int IMPOSSIBLENODE = 1000000000;	//定义一亿为不可能节点
	public static int INTRA_EDGE = 0;	// 域内链路
	public static int INTER_EDGE = 1;	// 域间链路
	
	public static boolean intToBool(int value) {
		if (value == 0) {
			return false;
		}
		return true;
	}
}
