package com.zj.chart.chartfactory;
/**
 * 符号工厂类，生成符号对象
 * @author lmk
 *
 */
public class ChartFactory {
	public IChart createcChart(String chartID)
	{
		String className = new JClassName().getChartClassName(chartID);
		if(className == null)
		{
			System.out.println("ERROR CHARTID");
			return null;
		}
		else
			try {
				IChart chart = (IChart)Class.forName(className).newInstance();
				return chart;
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
	}
}
