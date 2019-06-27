package umontreal.ssj.splitting.tests;

import java.io.IOException;


import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.MarkovChainNetworkReliability;
import umontreal.ssj.networks.staticreliability.SamplerType;
import umontreal.ssj.rng.*;
import umontreal.ssj.util.Chrono;
import umontreal.ssj.util.Tools;
//import umontreal.ssj.networks.*;
//import umontreal.ssj.networks.old.GraphOld;
import umontreal.ssj.splitting.*;
//import umontreal.ssj.networks.staticreliability.*;
import umontreal.ssj.stat.*;

public class SquareSplittingTest {
	
	
	/* GS Simple bivariate uniform example
	 */
	
	
	public static void main(String[] args) throws IOException{
		int[] tau = {2};
		int[] split = { 2, 5, 10, 20 };
		proc(split[0], tau[0]);
//		for (int j = 1; j < 2; j ++) {
//			for (int i = 0; i < 3; i+=2) {
//				proc(split[i], tau[j]);
//			}
//		}
	}

	private static void proc(int split, int t) throws IOException  {
		
		
		System.out.println(
		"====================================================== Bivariate uniform Splitting");
		RandomStream stream = new LFSR113();
		
		//initialisation ? le faire dans un ordre spécifique ou il faut rendre aléatoire ?
		// ca donne toujours les memes nombres aléatoires, c bon ?
		double y1 = stream.nextDouble();  // besoin d'initialiser ?
		double y2 = stream.nextDouble();   //besoin d'initialiser
		
		//System.out.println("Y1:" + y1);
		//System.out.println(y2);
		
		stream.resetNextSubstream();
		MarkovChainSquareSplitting chain = new MarkovChainSquareSplitting(y1,y2);
		double [] gamma = returnGamma(split,t);
		
		int tau = gamma.length;
		double delta = 1-gamma[tau-1]; 
		System.out.println("Delta :" + delta);
		
		double area = delta*(2-delta);
		System.out.println("Sampled area : " + area + "\n");
		
		Chrono timer = new Chrono();
		System.out.println("rng     = " + stream.getClass().getSimpleName());
	    int nrun = 1000 * 1000 * 1;
	    int numchain = 1;
	    double cro = timer.getSeconds();
	    
	    
	    System.out.println("number of runs  = " + nrun + "\nnumber of chains = " +
	    numchain + "\n");
	    
	    
	    boolean histoFlag = false; boolean storeFlag = false;
		SplittingGS gsplit = new SplittingGS();
		gsplit.initHistogram(histoFlag, -25, -10, 30); gsplit.initStore(storeFlag,
				nrun); 

		gsplit.run(chain, nrun, gamma, split, stream); 
		if (histoFlag) {
		TallyHistogram hist = gsplit.getHistogram(); System.out.println("Histogram" +
		hist.toString()); 
		} 
		if (storeFlag) { 
			TallyStore store = gsplit.getStore();
			System.out.println("TallyStore\n" + store.toString()); 
		}
	    
		
	}


	   private static double [] returnGamma(int s, int tau) {
		   // vérifier que s>=2 et tau>=2
		   double [] gamma = new double[tau];
		   for (int i =0;i<tau;i++) {
			   double gamm = Math.pow(s, -(i+1));
			   gamma[i] = Math.sqrt(1-gamm);
		   }
		   return gamma;
	   }
	   
		   private static SplittingGSAdam monpilote(MarkovChainWithImportance chain, int numChainAdam,
		         int split, RandomStream stream) {
		      double firstGamma = 0; // chain.getGamma();
		      double lastGamma = 1.;
		      SplittingGSAdam adam = new SplittingGSAdam();
		      adam.run(chain, numChainAdam, split, stream, firstGamma, lastGamma);
		      return adam;
		   }

	   
	
	
	
	
	

}
