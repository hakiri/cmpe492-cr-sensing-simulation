package SimulationRunner;

import cern.jet.random.Uniform;
import cern.jet.random.engine.RandomEngine;

public class ParetoDistribution {
	private Uniform uniform;
	private double shapeParameter;
	private double minValue;

	/**
	 * Creates a Pareto distribution
	 * @param shapeParameter	Shape parameter of the distribution
	 * @param minValue			Minimum value of the distribution
	 * @param randomEngine		Random number generator
	 */
	public ParetoDistribution(double shapeParameter, double minValue, RandomEngine randomEngine) {
		this.uniform = new Uniform(randomEngine);
		this.shapeParameter = shapeParameter;
		this.minValue = minValue;
	}
	
	public ParetoDistribution(double meanValue, RandomEngine randomEngine) {
		this.uniform = new Uniform(randomEngine);
		this.shapeParameter = meanValue / (meanValue - 1);
		this.minValue = 1.0;
	}
	
	/**
	 * Returns the cumulative distribution function
	 * @param x		Point to evaluate the value of the function
	 * @return		Cumulative distribution function
	 */
	public double cdf(double x)
	{
		return 1.0 - Math.pow(minValue / x, shapeParameter);
	}
	
	/**
	 * Returns a random number from the distribution
	 * @return	A random number form the distribution
	 */
	public double nextDouble()
	{
		return Math.pow(1 - uniform.nextDoubleFromTo(0, 1), -1.0/shapeParameter)*minValue;
	}
	
	/**
	 * Returns the probability density function
	 * @param x		Point to evaluate the value of the function
	 * @return		Probability density function
	 */
	public double pdf(double x)
	{
		return (shapeParameter / x) * Math.pow(minValue / x, shapeParameter);
	}

	/**
	 * Sets the minimum value of the distribution.
	 * @param minValue	New minimum value
	 */
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	/**
	 * Sets the shape parameter value of the distribution.
	 * @param shapeParameter	New shape parameter value
	 */
	public void setShapeParameter(double shapeParameter) {
		this.shapeParameter = shapeParameter;
	}
}
