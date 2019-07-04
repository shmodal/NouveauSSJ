package umontreal.ssj.networks.flow.nouv;

import java.io.IOException;


import umontreal.ssj.rng.LFSR113;

import umontreal.ssj.rng.*;
import umontreal.ssj.rng.RandomStream;

public class PMCFlowtest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		proc("1-diamond");
		//testTab();
		//initLambda();
		//initLambda2();
		//initLambda3();
	}

	
	 private static void proc(String GraphName) throws IOException {


		      String filename = TestParams.getGraphfile(GraphName); 
		      System.out.println("Peut etre");
		      GraphFlow graph = new GraphFlow(filename);
		      System.out.println("Oui");
		      //Graph graph = new Graph(filename);
		      
		      int m = graph.getNumLinks();
		      int[] tab = new int[m];
		      for (int i = 0; i<m;i++) {
		    	  tab[i] = 8;
		      }
		      
		      PMC p = new PMC(graph);
		      RandomStream stream = new LFSR113();
		      //RandomStream stream = new F2NL607();
		      
		      double prob = p.testRun(stream, 10, false, tab, 0.6, 0.0001);
		      System.out.println("Proba" + prob);
		      
		      
		      //p.run(5, stream, 10, false, tab, 0.6, 0.0001);
}
	 
	 
	 
	 private static void testTab() throws IOException {
		 int b = 8;
		 double[] tabRShah = new double[b+1];
		 double[] tabIdiot = new double[b+1];
		 double rho = 0.6;
		 double eps = 0.0001;
		 for (int i = 0;i<((tabIdiot.length)-1);i++) {
			 tabIdiot[i] = eps*Math.pow(rho, b-1-i);
		 }
		 double sum = 0;
		 for (int i = 0;i<((tabIdiot.length) -1);i++   ) {
			 sum += tabIdiot[i];
		 }
		 tabIdiot[b] = 1-sum;
		 //printTab(tabIdiot);
		 
		 
		 tabRShah[0] = eps*Math.pow(rho, b-1);
		 sum = tabRShah[0];
		   for (int k=1;k<b;k++) {
			   tabRShah[k] = tabRShah[k-1]/rho;
			   sum += tabRShah[k];
		   }
		   tabRShah[b] = 1.0-sum;
		   //printTab(tabRShah);
		   
		   printDeuxTab(tabRShah,tabIdiot);
		 
	 }

	   private static void printTab(double[] t) {
		   int m = t.length;
		   for (int i =0;i<m;i++) {
			   System.out.println(t[i]);
		   }
	   }
	   
	   
	   private static void printDeuxTab(double[] t1, double[] t2) {
		   int m = t1.length;
		   for (int i =0;i<m;i++) {
			   System.out.println(t1[i] + " ; " + t2[i]);
		   }
	   }

	   
	   
	   public static void initLambda() {
		   int b = 8;
		   double[] tabRShah = new double[b+1];
			 double rho = 0.6;
			 double eps = 0.0001;
			 tabRShah[0] = eps*Math.pow(rho, b-1);
			 double sum = tabRShah[0];
			   for (int k=1;k<b;k++) {
				   tabRShah[k] = tabRShah[k-1]/rho;
				   sum += tabRShah[k];
			   }
			   tabRShah[b] = 1.0-sum;
		   
		   
		   
		   sum = 0.0;
		   double [] lamb = new double[tabRShah.length -1];
		   System.arraycopy(tabRShah, 0, lamb, 0, lamb.length); // on ne copie pas ri,bi
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
		   //double[] tabY = new double[lambdaValues.length];
	   
		   //printTab(lamb);
		   }
	   
	   public static void initLambda2() {
		   int b = 8;
		   double[] tabRShah = new double[b+1];
			 double rho = 0.6;
			 double eps = 0.0001;
			 tabRShah[0] = eps*Math.pow(rho, b-1);
			 double sum = tabRShah[0];
			   for (int k=1;k<b;k++) {
				   tabRShah[k] = tabRShah[k-1]/rho;
				   sum += tabRShah[k];
			   }
			   tabRShah[b] = 1.0-sum; 
			   
			   
			   
			   sum = 0.0;
			   double [] lamb = new double[tabRShah.length -1];
			   System.arraycopy(tabRShah, 0, lamb, 0, lamb.length); // on ne copie pas ri,bi
			   // Somme cumulee des r puis ln sur le terme d'avant;
			   for (int k=1; k <(lamb.length) ; k++) {
				   lamb[k] = lamb[k] + lamb[k-1];
			   }
			   for (int k=0; k <(lamb.length) ; k++) {
				   lamb[k] = -Math.log(lamb[k]);
			   }
			   sum = lamb[lamb.length -1];
			   for (int k =(lamb.length) -2; k >=0; k--) {
				   lamb[k] = lamb[k] - sum;
				   sum += lamb[k];
			   }
			   printTab(lamb);
			   
	   }
	   
	   
	   // calcul avec l'expression explicite
	   public static void initLambda3() {
		   int b = 8;
		   double[] tabRShah = new double[b+1];
			 double rho = 0.6;
			 double eps = 0.0001;
			 tabRShah[0] = eps*Math.pow(rho, b-1);
			 double sum = tabRShah[0];
			   for (int k=1;k<b;k++) {
				   tabRShah[k] = tabRShah[k-1]/rho;
				   sum += tabRShah[k];
			   }
			   tabRShah[b] = 1.0-sum; 
			   
			   
			   
			   sum = 0.0;
			   double [] lamb = new double[tabRShah.length -1];
			   System.arraycopy(tabRShah, 0, lamb, 0, lamb.length); // on ne copie pas ri,bi
			   // Somme cumulee des r puis ln sur le terme d'avant;
			   for (int k=1; k <(lamb.length) ; k++) {
				   lamb[k] = lamb[k] + lamb[k-1];
			   }
			   for (int k=0; k <(lamb.length) ; k++) {
				   lamb[k] = -Math.log(lamb[k]);
			   }
			   double [] lambCopy = new double[lamb.length];
			   System.arraycopy(lamb, 0, lambCopy, 0, lamb.length);
			   
			   
			   for (int k=0;k<lamb.length-1;k++) {
				   lamb[k] = lamb[k] - lambCopy[k+1];
			   }
			   
			   printTab(lamb);
			   
	   }
	   
	   
	   
}
