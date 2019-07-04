/**
 * 
 */
package umontreal.ssj.networks.flow.old;

import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.randvar.ExponentialGen;
import umontreal.ssj.randvar.UniformGen;
import umontreal.ssj.probdist.ExponentialDist;


/**
 * This class generates random samples for the length 
 * of an edge of a graph: the length represents time at which 
 * the edge becomes operational. Sampling is done with
 * <i>exponential</i> random variables.
 * @author Richard Simard
 *
 */
public class ExponentialSampler extends Sampler {
   private boolean destructFlag;
   
   private void init (RandomStream stream, double r, boolean flag) {
      destructFlag = flag;
      m_method = SamplerType.EXPONENTIAL;      
      if (flag) {
         double lam = -Math.log(r);
         gen = new ExponentialGen(stream, lam);    
      } else {
         double lam = -Math.log(1.0 - r);
         gen = new ExponentialGen(stream, lam);
      }
  }
   
   
	/**
	 * Constructor for the constructive schema.
	 * @param stream stream of U[0,1) random numbers
	 * @param r reliability
	 */
	public ExponentialSampler(RandomStream stream, double r) {
	   init (stream, r, false); 
	}
   
	/**
	 * Initiates the sampler for the destructive schema.
	 * @param stream
	 * @param r
	 */
	public void setDestructiveSchema (RandomStream stream, double r) {
      init (stream, r, true); 
	}
	
	
	/**
	 * Samples a length for the edge: a may be 0 or a threshold gamma
	 * @return a random length
	 */
	@Override
	public double sample(double a) {
	   if (destructFlag)
	      throw new UnsupportedOperationException("  Destructive schema unfinished");
	   else
	 	   return a + gen.nextDouble();
	}

	
	/**
	 * Samples an exponential random length over the interval [a, inf].
	 * @param stream a source of U[0,1) random numbers
	 * @param a may be 0 or a threshold gamma
	 * @param lambda  rate of exponential = -log(1-r)
	 * @return a random length
	 */
	public static double sample(RandomStream stream, double a, double lambda) {
		return a + ExponentialDist.inverseF (lambda, stream.nextDouble());
	}
	
	  
   /**
    * Samples an exponential random length over the interval [0, a] or [0, inf].
    * @param stream source of U(0,1) random numbers
    * @param a may be 0 or a threshold gamma
    * @param lambda  rate of exponential = -log(r)
    * @return a random length
    */
   public static double sampleDestruct(RandomStream stream, double a, double lambda) {
      if (a <= 0.)     // if a <= 0, sample over [0, inf]
         return ExponentialDist.inverseF (lambda, stream.nextDouble());
      // sample over [0, b]
      double b = -Math.expm1(-lambda*a);
      double u = UniformGen.nextDouble(stream, 0, b);
      return -Math.log1p(-u)/lambda;
   }
}
