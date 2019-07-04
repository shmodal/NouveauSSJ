package umontreal.ssj.networks.flow.nouv;

import umontreal.ssj.networks.LinkWithCapacity;

import umontreal.ssj.probdist.ExponentialDist;
import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.rng.RandomStream;

public class LinkFlow extends LinkWithCapacity {

	// relfehcir a somme cumulee pour les probas(cdf, fonction repartiotn)
	// retenir juste loi de proba au lieu des 4 premiers tableaux
	
	
    private int b ; //le bi associ� au lien i 
	private int[] capacityValues;  // les ci,k   taille bi+1, de 0 � bi
	private double [] probabilityValues ; // les ri,k  taille bi+1,  de 0 � bi
	
	private double [] lambdaValues;  // les lambda i,k, taille bi, de 0 � bi-1
	public double sommeLambda; // somme des lambda i,k � i fix�(poour ne pas recalculer plus tard)
	public double [] tabY ; //les Yi,k
	private int [] S_jump ;  //tableau S
	public int numberJumps; // nombre de jumps (Si,k) =1
	public double [] lambdaTildeValues ; // les lambdaTilde
	
	
	
	// capacity: only used in flowProblem 
	private int capacity;
	
	/**
	 * (Previous)Partially Full constructor
	 * 
	 * @param indice
	 *           define the indice of the LinkWithCapacity
	 * @param source
	 *           define the number of the source's node
	 * @param target
	 *           define the number of the target's node
	 * @param r
	 *           define the reliability of the LinkWithCapacity
	 */
	public LinkFlow(int indice, int source, int target)
	{
		super(indice,source,target);
		//this.indice = indice;
	    //this.source = source;
	    //this.target = target;
	    //this.r=r;
		//temporary initialisation 
		//this.capacity=1;
	}
   
	/**
	 * Full Constructor
	 */
	public LinkFlow(int indice, int source, int target, int capacity)
	{
		super(indice,source,target);
		//this.indice = indice;
	    //this.source = source;
	    //this.target = target;
	    //this.r=r;
		this.capacity=capacity;
	}


	/**
	* Set the capacity of this LinkWithCapacity
	* 
	* @param r
	*/
	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}
	
	/**
    * Return the capacity of the LinkWithCapacity.
    * 
    * @return To obtain the capacity of the LinkWithCapacity
    */
	public int getCapacity()
	{
		return capacity;
	}

   /**
    * Clone a LinkWithCapacity
    * 
    * @return To obtain a copy of the LinkWithCapacity
    */
	@Override
	public LinkFlow clone()
	{
		LinkFlow image = null;
		
		//try {
		//	image = (LinkWithCapacity) super.clone();
		//} catch (CloneNotSupportedException e) {
			// No deberia suceder
		//}
		image = (LinkFlow) super.clone();
		image.setIndice(this.getIndice());
		image.setSource(this.getSource());
		image.setTarget(this.getTarget());
		image.capacity = this.capacity;
		return image;
   }
	
	
	  
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
	    
	   
	   // A mettre en arraylist une fois corrige
	   //le tableau tabLambT est peut etre trop volumineux pour rien(on copie tout)
	   
	   public void initJumpAndIndexes() {
		   // LISTE L, NOT DONE YET
		   //double[] tabY = sampleY(lambdaValues, stream);
		   S_jump = new int[tabY.length];
		   
		   // LE PREMIER JUMP EXISTE ?
		   S_jump[tabY.length-1] = 1;
		   
		   double [] tabLambT = new double[lambdaValues.length];
		   System.arraycopy(lambdaValues, 0, tabLambT, 0, lambdaValues.length);
		   //int nJumps = 0;
		   int nJumps = 1;
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


	public double getY(int indice) {
		return this.tabY[indice];
	}

	public int getMinCapacity() {
		return this.capacityValues[0];
	}
	   
	   
	   
	   
	
	

}
