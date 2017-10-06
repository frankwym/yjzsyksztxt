package com.zj.util;

public class ModelPrim {

	/**
	 * 界限等分模型
	 * 
	 * @param a
	 * @param num
	 * @return
	 */
	public static double[] modelA1(double[] a, int num) {
		boolean flag = JUtil.IsIntegerOnly(a);
		double[] b = new double[num + 1];
		b[0] = JStatistics.MinNoneNegative(a);
		b[num] = JStatistics.Max(a);
		double dif = (b[num] - b[0]) / num;
		for (int i = 1; i < b.length - 1; i++)
			b[i] = flag ? (int) (b[0] + i * dif + 0.5) : JUtil
					.GetDecimalTipValue(b[0] + i * dif);
		return b;
	}

	/**
	 * 间隔等分模型
	 * 
	 * @param a
	 * @param num
	 * @return
	 */
	public static double[] modelB1(double[] a, int num) {
		boolean flag = JUtil.IsIntegerOnly(a);
		double[] b = new double[num + 1];
		b[0] = JStatistics.MinNoneNegative(a);
		b[num] = JStatistics.Max(a);
		double dif = (b[num] - b[0]) / (num * (num + 1));
		for (int i = 1; i < b.length - 1; i++)
			b[i] = flag ? (int) (b[0] + i * (i + 1) * dif + 0.5) : JUtil
					.GetDecimalTipValue(b[0] + i * (i + 1) * dif);
		return b;
	}

	/**
	 * 界限等比模型
	 * 
	 * @param a
	 * @param num
	 * @return
	 */
	public static double[] modelC1(double[] a, int num) {
		boolean flag = JUtil.IsIntegerOnly(a);
		double[] b = new double[num + 1];
		b[0] = JStatistics.MinPositive(a);
		b[num] = JStatistics.Max(a);
		double ratn = b[num] / b[0];
		double rat = Math.pow(ratn, 1d / num);
		for (int i = 1; i < b.length - 1; i++)
			b[i] = flag ? (int) (b[0] * Math.pow(rat, i) + 0.5) : JUtil
					.GetDecimalTipValue(b[0] * Math.pow(rat, i));
		return b;
	}

	/**
	 * 间隔等比模型
	 * 
	 * @param a
	 * @param num
	 * @return
	 */
	public static double[] modelD1(double[] a, int num) {
		boolean flag = JUtil.IsIntegerOnly(a);
		double[] b = new double[num + 1];
		b[0] = JStatistics.MinPositive(a);
		b[num] = JStatistics.Max(a);
		double ratn = b[num] / b[0];
		double rat = JFormula.EquationR(num, ratn);
		for (int i = 1; i < b.length - 1; i++)
			b[i] = flag ? (int) (b[0] + JFormula.PolynomialsR(i, rat) + 0.5)
					: JUtil.GetDecimalTipValue(b[0]
							+ JFormula.PolynomialsR(i, rat));
		return b;
	}
}