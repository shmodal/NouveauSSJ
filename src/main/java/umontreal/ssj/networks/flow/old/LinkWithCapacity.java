package umontreal.ssj.networks.flow.old;


import umontreal.ssj.probdist.ExponentialDist;
import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.rng.RandomStream;


/**
 * A Link of a network. A link is defined by a length and 2 nodes
 * connected by this link.
 * 
 */
public class LinkWithCapacity
{
	// relfehcir a somme cumulee pour les probas(cdf, fonction repartiotn)
	// retenir juste loi de proba au lieu des 4 premiers tableaux
	
	
    private int b ; //le bi associé au lien i 
	private int[] capacityValues;  // les ci,k   taille bi+1, de 0 à bi
	private double [] probabilityValues ; // les ri,k  taille bi+1,  de 0 à bi
	
	private double [] lambdaValues;  // les lambda i,k, taille bi, de 0 à bi-1
	public double sommeLambda; // somme des lambda i,k à i fixé(poour ne pas recalculer plus tard)
	public double [] tabY ; //les Yi,k
	private int [] S_jump ;  //tableau S
	public int numberJumps; // nombre de jumps (Si,k) =1
	public double [] lambdaTildeValues ; // les lambdaTilde
	
	
	private int capacity ;   // VOIR AVEC NERVO COMMENT IL TRAITE CA plus tard
	
	// son indice ou identificateur dans le tableau Links
	private int indice;

   // indice du noeud source
   private int source;

   // indice du noeud target
   private int target;

   // reliability: probability that it is operational
   private double r;

   // parameter of probability distribution associated with the link; 
   // it could be lambda for the exponential distribution or sigma
   // for the normal distribution. This is used only for speed
   // so as not to recompute a log or the inverse normal at each step.
   private double param;
   
   private SamplerType m_sam = SamplerType.NON_INIT;

   /**
    * 
    * @param indice link id
    * @param source first node of this link
    * @param target second node of this link
    */
   public LinkWithCapacity(int indice, int source, int target)
   {
      this.indice = indice;
      this.source = source;
      this.target = target;
      this.capacity = 1;
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
   public LinkWithCapacity(int indice, int source, int target, double r)
   {
      this.indice = indice;
      this.source = source;
      this.target = target;
      this.r = r;
   }

   public LinkWithCapacity(int indice2, int source2, int target2, double r2, int capacity2) {
	// TODO Auto-generated constructor stub
	   this.indice = indice2;
	   this.source = source2;
	   this.target = target2;
	   this.r = r2;
	   this.capacity = capacity2;
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
   public LinkWithCapacity clone()
   {
      LinkWithCapacity image = null;

      try {
         image = (LinkWithCapacity) super.clone();
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
   
   // NEW
   
   public void setB(int k)
   {
     this.b = k;
   }

   public int getB()
   {
      return b;
   }
   
   public void setCapacityValues(int[] tab)
   {
     this.capacityValues = tab;
   }
   
   public int getCapacity(int k)
   {
      return capacityValues[k];
   }
   

   public int[] getCapacityValues()
   {
      return capacityValues;
   }
   
   public void setProbabilityValues(double[] tab)
   {
     this.probabilityValues = tab;
   }

   public double[] getProbabilityValues()
   {
      return probabilityValues;
   }
   
   public double[] getLambdaValues()
   {
      return lambdaValues;
   }
   public void setLambdaValues(double[] tab)
   {
      lambdaValues = tab;
   }
   
   public void setValuesY(double[] tab)
   {
      tabY = tab;
   }
   
   public double[] getValuesY()
   {
      return tabY;
   }
   
   
   public int getJump(int k) {
	   return S_jump[k];
   }
   
   public int getJumpLength() {
	   return S_jump.length;
   }
   
   public void setJump(int k, int value) {
	   S_jump[k] = value;
   }
   
   public double getLambdaTilde(int k)
   {
      return lambdaTildeValues[k];
   }
   
   
   // Les mettre dans PMC ou Forest plutot ?
   
   /// IL FAUT TAILLE Bi +1
   /** Computes the values of <tt>lambda_i,k</tt> and the sum of <tt>lambda_i,k</tt>
    *  for the link i and sets them in the fields lambdaValues and sommeLambda.
    */
   
   public void initLambda() {
	   double sum = 0.0;
	   double [] lamb = new double[probabilityValues.length -1];
	   System.arraycopy(probabilityValues, 0, lamb, 0, lamb.length); // on ne copie pas ri,bi
	   // Somme cumulee des r puis ln sur le terme d'avant;
	   for (int k=0; k <(lamb.length-1) ; k++) {
		   lamb[k+1] += lamb[k];
		   lamb[k] = -Math.log(lamb[k]); // les lambda sont avec des -ln(sum)
	   }
	   lamb[lamb.length -1] = -Math.log(lamb[lamb.length -1]);
	   sum = lamb[lamb.length -1];
	   for (int k=(lamb.length-2) ; k>=0 ; k--) {
		   lamb[k] = lamb[k] -sum;   //- lamb[k+1];
		   //System.out.println("Lambda k " + lamb[k] );
		   sum += lamb[k];
	   }
	   lambdaValues = lamb;
	   sommeLambda = sum;
	   //double[] tabY = new double[lambdaValues.length];
   }
   

   // SOON DEPRECATED(DONE EN PMC)
   public double [] sampleY(double [] lamb,RandomStream stream) {
	   double [] tabY = new double[lamb.length];
	   for (int i=0;i< tabY.length;i++) {
		   double lambda = lamb[i];
		   tabY[i] = ExponentialDist.inverseF(lambda, stream.nextDouble());
	   }
	   return tabY;
   }  

   
   // A mettre en arraylist une fois corrige
   //le tableau tabLambT est peut etre trop volumineux pour rien(on copie tout)
   
   public void initJumpAndIndexes() {
	   // LISTE L, NOT DONE YET
	   //double[] tabY = sampleY(lambdaValues, stream);
	   S_jump = new int[tabY.length];
	   double [] tabLambT = new double[lambdaValues.length];
	   System.arraycopy(lambdaValues, 0, tabLambT, 0, lambdaValues.length);
	   int nJumps = 0;
	   //System.out.println("b" + b);
	   //System.out.println(lambdaValues.length);
	   int min = b-1;
	   tabLambT[b-1] = lambdaValues[b-1];
	   
	   for (int i=0;i<tabY.length;i++) {
		   //System.out.println("Element numero "+ i +" "+ tabY[i]);
	   }
	   
	   
	   for (int k=(b-2); k>=0; k--) {
		   //System.out.println("passage boucle");
		   if (tabY[k] > tabY[min]) {
			   //remove k ///// NOT DONE YET
			   tabLambT[min] += lambdaValues[k];
			   S_jump[k] =0;
		   }
		   else {
			   //System.out.println("On a un jump");
			   min = k;
			   tabLambT[min] = lambdaValues[k];
			   S_jump[k] =1;
			   nJumps += 1; 
		   }   
	   }
	   numberJumps = nJumps;
	   lambdaTildeValues = tabLambT;
   }

public void setCapacity(int cap) {
	this.capacity = cap;
}

public int getCapacity() {
	return this.capacity;
}
 
public double getY(int indice) {
	return this.tabY[indice];
}

public int getMinCapacity() {
	return this.capacityValues[0];
}
   
   
   
   
}

   
   

