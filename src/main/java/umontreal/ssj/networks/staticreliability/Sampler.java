/**
 * 
 */
package umontreal.ssj.networks.staticreliability;
import umontreal.ssj.randvar.RandomVariateGen;


/**
 * Subclasses of Sampler generates random samples for the length 
 * of an edge of a graph: the length represents time at which 
 * the edge becomes operational.
 * @author Richard Simard
 *
 */
public abstract class Sampler {

	protected SamplerType m_method;
	
	protected RandomVariateGen gen;   // 
	
   /**
    * Sample the length of links using a specific sampler.

    * @param a 0 or threshold gamma
    * @return the length
    */
	public abstract double sample(double a);
	
	@Override
	public String toString () {
   	if (gen != null)
         return gen.toString();
   	return m_method.toString();
   }
}
