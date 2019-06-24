/**
 * 
 */
package umontreal.ssj.TempNetworks.staticreliability;

import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.randvar.*;


/**
 * This class generates random samples for the length 
 * of an edge of a graph: the length represents time at which 
 * the edge becomes operational. Sampling is done with <i>normal</i> random variables.
 * @author Richard Simard
 *
 */
public class NormalSampler extends Sampler {
   private double sigma;
   
	/**
	 * Constructor.
	 * @param stream stream of U[0,1) random numbers
	 * @param r reliability
	 */
	public NormalSampler(RandomStream stream, double r) {
		sigma = 1.0 / NormalDist.inverseF01(r);
		gen = new NormalGen(stream, 0, sigma);
		m_method = SamplerType.NORMAL;
	}
 
	
	/**
	 * Samples a length for the edge: a may be -inf or a threshold gamma
	 * @return a random length
	 */
	@Override
	public double sample(double a) {
		if (a <= -1.0e300)
			return gen.nextDouble();
		
		// Here threshold gamma > -inf
	   RandomStream stream = gen.getStream();
	   double c = NormalDist.cdf01(a * sigma);
	   double u = UniformGen.nextDouble (stream, c, 1);
		return sigma * NormalDist.inverseF01(u);
	}
	
	
	
	/**
	 * Samples a normal random length over the interval [a, inf].
	 * @param stream a source of U[0,1) random numbers
	 * @param a may be -inf or a threshold gamma
	 * @param r reliability of edge
	 * @return a random length
	 */
	public static double sampleSlow(RandomStream stream, double a, double r) {
		double sigma = 1.0 / NormalDist.inverseF01(r);
		if (a <= 0.0)
			return NormalGen.nextDouble(stream, 0, sigma);
		
		// Here threshold gamma > 0
	   double c = NormalDist.cdf01(a * sigma);
	   double u = UniformGen.nextDouble (stream, c, 1);
		return sigma * NormalDist.inverseF01(u);
	}

}
