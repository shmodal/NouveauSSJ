package umontreal.ssj.networks.flow.nouv;

/**
 * 
 */
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.randvar.UniformGen;
import umontreal.ssj.probdist.UniformDist;



/**
 * This class generates random samples for the length 
 * of an edge of a graph: the length represents time at which 
 * the edge becomes operational. Sampling is done with <i>uniform</i> random variables.
 * @author Richard Simard
 *
 */
public class UniformSampler extends Sampler {
   private double b;   // = 1/r
	  
	/**
	 * Constructor.
	 * @param stream a source of U[0,1) random numbers
	 * @param r reliability of edge
	 */   
	public UniformSampler (RandomStream stream, double r) {
		b = 1.0/r;
		gen = new UniformGen(stream, 0, b);
		m_method = SamplerType.UNIFORM;
	}
   
	
	/**
	 * Samples a length for the edge: a may be 0 or a threshold gamma
	 * @return a random length
	 */
	@Override
	public double sample(double a) {
		// a = 0
		if (a <= 0.0)
			return gen.nextDouble();
		// a = gamma > 0
	   RandomStream stream = gen.getStream();
		return UniformGen.nextDouble(stream, a, b);
	}
	  

	/**
	 * Samples a uniform random length over the interval [a, b].
	 * @param stream a source of U[0,1) random numbers
	 * @param a may be 0 or a threshold gamma
	 * @param b upper limit of range = 1/r
	 * @return a random length
	 */
	public static double sample(RandomStream stream, double a, double b) {
		return UniformDist.inverseF (a, b, stream.nextDouble());
	}

}
