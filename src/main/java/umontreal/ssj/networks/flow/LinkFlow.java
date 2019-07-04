package umontreal.ssj.networks.flow;


import umontreal.ssj.networks.LinkBasic;
import umontreal.ssj.networks.staticreliability.SamplerType;
import umontreal.ssj.probdist.ExponentialDist;
import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.rng.RandomStream;


/**
 * A Link of a network. A link is defined by a length and 2 nodes
 * connected by this link.
 * 
 */
public class LinkFlow extends LinkBasic
{
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


   /**
    * 
    * @param indice link id
    * @param source first node of this link
    * @param target second node of this link
    */
   public LinkFlow(int indice, int source, int target,int [] capacityValues, double [] probabilityValues)
   {
      //this.indice = indice;
      //this.source = source;
      //this.target = target;
      super(indice,source,target);
      this.capacityValues = capacityValues;
      this.probabilityValues = probabilityValues;
      this.capacity = 0;
   }

   /**
    * Clone a link
    * 
    * @return To obtain a copy of the link
    */
   @Override
   public LinkFlow clone()
   {
      LinkFlow image = null;

      try {
         image = (LinkFlow) super.clone();
      } catch (CloneNotSupportedException e) {
         // No deberia suceder
      }
      image.indice = this.indice;
      image.source = this.source;
      image.target = this.target;
      return image;
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
		   sum += lamb[k];
	   }
	   lambdaValues = lamb;
	   sommeLambda = sum;
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

   
   public void initJumpAndIndexes() {
	   // LISTE L, NOT DONE YET
	   //double[] tabY = sampleY(lambdaValues, stream);
	   double [] tabLambT = new double[lambdaValues.length];
	   System.arraycopy(lambdaValues, 0, tabLambT, 0, lambdaValues.length);
	   int nJumps = 0;
	   int min = b;
	   tabLambT[b] = lambdaValues[b];
	   for (int k=(b-1); k>=0; k--) {
		   if (tabY[k] > tabY[min]) {
			   //remove k ///// NOT DONE YET
			   tabLambT[min] += lambdaValues[k];
			   S_jump[k] =0;
		   }
		   else {
			   min = k;
			   tabLambT[min] = lambdaValues[k];
			   S_jump[k] =1;
			   nJumps += 1; 
		   }   
	   }
	   numberJumps = nJumps;
	   lambdaTildeValues = tabLambT;
   }
   
}

   
   

