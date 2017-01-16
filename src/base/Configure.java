/*
 * 配置信息类，用于存放仿真中可能用到的参数，方便调试
 */
package base;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Configure {

	public static float INF = (float) 10000000000.0;	//定义十亿为无限大
	public static int IMPOSSIBLENODE = 1000000000;	//定义一亿为不可能节点
	public static int INTRA_EDGE = 0;	// 域内链路
	public static int INTER_EDGE = 1;	// 域间链路
	public static long RANDOM_INIT_SEED = 47;	//生成随机数的初始seed
	public static int FUNCTION_SEED = 4;	//随机生成的功能数
	public static Random random = new Random(RANDOM_INIT_SEED);
	public static int SFC_AMOUNT = 10000;	//产生SFC的条数
	public static int POISSON_LAMDA = 4;	//泊松分布的lamda参数
	
	/*
	 * 功能：产生泊松分布的随机数，用于指定SFC业务的到达，参数是上面定义的POISSON_LAMDA
	 * 参考的是：Knuth的算法思路。具体参看维基百科
	 * https://en.wikipedia.org/wiki/Poisson_distribution
	 */
	public static int createPoission() {
		int k = 0;
		double p = 1.0, L = Math.exp(-POISSON_LAMDA);
		double u;
		do {
			k++;
			u = random.nextDouble();
			p = p*u;
		} while(p > L);
		return k - 1;
	}
	
	public static void testPossion() {
		List<Integer> poissionList = new ArrayList<Integer>();
		for (int i = 0; i < 100; i++) {
			poissionList.add(Configure.createPoission());
		}
		int sum = 0;
		for (Integer key : poissionList) {
			System.out.println(key);
			sum += key;
		}
		System.out.println("泊松分布的均值为：");
		double average = (double)sum / poissionList.size();
		System.out.println("均值 = " + average);
		double variance = 0.0;
		for (Integer key : poissionList) {
			variance += (key - average) * (key - average);
		}
		System.out.println("方差 = " + (double)variance / poissionList.size());
	}
	
	public static boolean intToBool(int value) {
		if (value == 0) {
			return false;
		}
		return true;
	}
}
