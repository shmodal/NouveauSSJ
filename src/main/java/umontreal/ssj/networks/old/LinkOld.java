package umontreal.ssj.networks.old;

import umontreal.ssj.networks.staticreliability.ExponentialSampler;
import umontreal.ssj.networks.staticreliability.SamplerType;
import umontreal.ssj.networks.staticreliability.UniformSampler;
//import sampling.*;
import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.rng.RandomStream;
//import umontreal.ssj.staticreliability.*;


/**
 * A Link of a network. A link is defined by an id (also stored in the array links in Graph)
 * and 2 nodes connected by this link. It also maintains the reliability and
 * the parameter of probability distribution associated with the link.
 * 
 * 
 */
public class LinkOld
{
   // son indice ou identificateur dans le tableau Links
	   /**
	    * id of the Link, also stored in the array Links(Graph)
	    */
	private int indice; 

   // indice du noeud source
	   /**
	    * id of the source node
	    */
   private int source;
   
   /**
    * id of the target node
    */
   // indice du noeud target
   private int target;

   // reliability: probability that it is operational
   /**
    * reliability: probability that it is operational
    */
   private double r;

   // parameter of probability distribution associated with the link; 
   // it could be lambda for the exponential distribution or sigma
   // for the normal distribution. This is used only for speed
   // so as not to recompute a log or the inverse normal at each step.
   /**
    * parameter of probability distribution associated with the link; 
    * it could be lambda for the exponential distribution or sigma
    * for the normal distribution. This is used only for speed
    * so as not to recompute a log or the inverse normal at each step.
    */ 
   private double param;
   
   /**
    * A REMPLIR
    */
   
   private SamplerType m_sam = SamplerType.NON_INIT;

   /** 
    * Basic Constructor
    * 
    * @param indice link id
    * @param source first node of this link
    * @param target second node of this link
    */
   public LinkOld(int indice, int source, int target)
   {
      this.indice = indice;
      this.source = source;
      this.target = target;
   }

   /**
    * Full constructor
    * 
    * @param indice
    *           define the indice of the link
    * @param source
    *           define the number of the source's node
    * @param target
    *           define the number of the target's node
    * @param r
    *           define the reliability of the link
    */
   public LinkOld(int indice, int source, int target, double r)
   {
      this.indice = indice;
      this.source = source;
      this.target = target;
      this.r = r;
   }

   /**
    * Set the link indice
    * 
    * @param indice
    */
   public void setIndice(int indice)
   {
      this.indice = indice;
   }

   /**
    * Sets node with index id as link source.
    * 
    * @param id
    */
   public void setSource(int id)
   {
      this.source = id;
   }

   /**
    * Sets node with index id as link target.
    * 
    * @param id
    */
   public void setTarget(int id)
   {
      this.target = id;
   }

   /**
    * Set the reliability of this link
    * 
    * @param r
    */
   public void setR(double r)
   {
      this.r = r;
   }

   /**
    * Set the distribution parameter of the link. Lambda for the exponential,
    * sigma for the normal.
    * @param par
    */
   public void setParam(double par)
   {
     this.param = par;
   }

   /**
    * Return the link number.
    * 
    * @return the link number.
    */
   public int getIndice()
   {
      return indice;
   }

   /**
    * Return the distribution parameter of the link
    * 
    * @return the distribution parameter of the link
    */
   public double getParam()
   {
      return param;
   }

   /**
    * Return the reliabillity of the link.
    * 
    * @return To obtain the reliability of the link
    */
   public double getR()
   {
      return r;
   }

   /**
    * Return the id of one of the connected node
    * 
    * @return the id of one of the connected node
    */
   public int getSource()
   {
      return source;
   }

   /**
    * Return the id of the other node
    * 
    * @return the id of the other node
    */
   public int getTarget()
   {
      return target;
   }

   /**
    * Clone a link
    * 
    * @return To obtain a copy of the link
    */
   @Override
   public LinkOld clone()
   {
      LinkOld image = null;

      try {
         image = (LinkOld) super.clone();
      } catch (CloneNotSupportedException e) {
         // No deberia suceder
      }
      image.indice = this.indice;
      image.source = this.source;
      image.target = this.target;
      image.param = this.param;
      image.r = this.r;
      image.m_sam = this.m_sam;
      return image;
   }
   

   /**
    * Given the reliability r of the link, computes and sets the parameter
    * of the distribution to generate random variables, and set the param
    * in this object. So that we don't have to compute the same log
    * or inverse normal repeatedly for each random number generated.
    * This is done for speed.
    * <ul>
    * <li> For uniform sampling, param = 1/r is the upper limit of the
    *  range U[0, param]. </li>
    * <li> For exponential sampling, param = -log(1-r) is the rate of the
    *  exponential distribution. </li>
    *  <li> For normal sampling, param = 1/NormalDist.inverseF01(r) is the 
    *  standard deviation of the standard normal distribution. </li>
    * </ul>
    *
    * @param sam which kind of random sampler is used
    */
	private void calcParam (SamplerType sam) {
      switch (sam) {
      case UNIFORM:
         param = 1.0/r;
         m_sam = SamplerType.UNIFORM;
         break;
      case EXPONENTIAL:
         param = -Math.log(1.0 - r);
         m_sam = SamplerType.EXPONENTIAL;
         break;
      case NORMAL:
      	param = 1.0 / NormalDist.inverseF01(r);
         m_sam = SamplerType.NORMAL;
         break;
      case NON_INIT:
      	throw new IllegalArgumentException ("SamplerType not initialized");
      }
	}
	
	   /**
	    * A FAIRE
	    */
	
	  private void calcParamDestruct (SamplerType sam) {
	      switch (sam) {
	      case UNIFORM:
	         param = 1.0/(1.0 - r);
	         m_sam = SamplerType.UNIFORM;
	         break;
	      case EXPONENTIAL:
	         param = -Math.log(r);
	         m_sam = SamplerType.EXPONENTIAL;
	         break;
	      case NORMAL:
	         throw new IllegalArgumentException ("SamplerType NORMAL not used");
	       //  break;
	     // case NON_INIT:
	         default:
	         throw new IllegalArgumentException ("SamplerType not initialized");
	      }
	   }
	   
	 
   /**
    * Sample the weight of this link. 
    * a is either gamma_0 or a threshold gamma. gamma_0 is 0 for uniform
    * and exponential sampling, -inf for normal sampling.
    * @return a random weight
    */
   public double sample(RandomStream stream, double a)
   {
      double len = 0;
     
      switch (m_sam) {
      case UNIFORM:
         len = UniformSampler.sample(stream, a, param);
         break;
      case EXPONENTIAL:
         len = ExponentialSampler.sample(stream, a, param);
         break;
      case NORMAL:
        	throw new IllegalArgumentException ("Normal Sampler unfinished");
         // len = NormalSampler.sample(stream, a, param);
         // break;
      case NON_INIT:
      	throw new IllegalArgumentException ("SamplerType non initialized");
      }
      
      return len;
   }

   
   /**
    * Sample the weight of this link for the destructive schema. 
    * a is either 0 or a threshold gamma.
    * @return a random weight
    */
   public double sampleDestruct(RandomStream stream, double a)
   {
      double len = 0;
     
      switch (m_sam) {
      case UNIFORM:
         throw new IllegalArgumentException ("UNIFORM Sampler unfinished");
       /*  if (a <= 0.)
            len = UniformSampler.sample(stream, 0, param);
         else
            len = UniformSampler.sample(stream, 0, a);
         break;*/
      case EXPONENTIAL:
         len = ExponentialSampler.sampleDestruct(stream, a, param);
         break;
      case NORMAL:
         throw new IllegalArgumentException ("Normal Sampler unfinished");
         // len = NormalSampler.sample(stream, a, param);
         // break;
      case NON_INIT:
         throw new IllegalArgumentException ("SamplerType non initialized");
      }
      
      return len;
   }


   /**
    * Choose the sampler type to sample the weight of the links.
    * Given the reliability r of the link, computes and sets the parameter
    * of the distribution to generate random variables, and set the param
    * of this object.
    * 
    * @param sam which kind of random sampler to use
    */   
   public void setSampler(SamplerType sam, boolean destruct)
   {
     m_sam = sam;
     if (destruct)
        calcParamDestruct (sam);
     else 
        calcParam (sam);
   }
   
}
