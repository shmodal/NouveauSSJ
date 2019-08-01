package umontreal.ssj.networks.flow;

import java.util.HashMap;


import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.Tally;
import umontreal.ssj.stat.TallyHistogram;
import umontreal.ssj.stat.TallyStore;
import umontreal.ssj.util.Chrono;

/** Crude Monte Carlo Algorithm for flow reliability estimation. Graphs are supposed to be directed.
 * DrawCapacity assumes that the capacity of a link follows a discrete distribution.
 * 
 * To estimate unreliability instead of reliability, just switch 1.0 and 0.0 in doOneRun.
 *  
 */

public class MonteCarloFlow {
	
	
	private double m_variance; // variance
	private double m_ell; // network reliability
	private TallyStore store; // store the value for each run
	private boolean storeFlag; // true: store value for each run
	private TallyHistogram hist; // store counters of value for each run
	private boolean histFlag; // true: store values for each run in bin counters


   protected GraphFlow father; // same father as in forest

   protected boolean antiScanFlag;  // true for reverse scan, false otherwise
   protected boolean shockFlag;
   
   public MonteCarloFlow(GraphFlow graph) {
	      father = graph;
	   }
   
   
   protected double doOneRun(RandomStream stream,int demande) {
	   int m = father.getNumLinks();
	   for (int i=0;i<m;i++) {
		   father.setCapacity(i, drawCapacity(stream,i));
		   //System.out.println("nouvelle capa");
		   //System.out.println(father.getLink(i).getCapacity());
	   }
	   MaxFlowEdmondsKarp Ek= new MaxFlowEdmondsKarp(father);
	   int maxFlow = Ek.EdmondsKarp();
	   //System.out.println(maxFlow);
	   if (maxFlow >= demande){
		   return 1.0;
		   }
	   else {return 0.0;}
	   
   }
	
   protected int drawCapacity(RandomStream stream, int i) {
	   //System.out.println("Arete " + (i+1));
	   //System.out.println("Réel tiré");
	   double u= stream.nextDouble();
	   //System.out.println(u);
	   double [] prob = father.getLink(i).getProbabilityValues();
	   //System.out.println("Tableau probas");
	   //printTab(prob);
	   
	   int n = prob.length;
	   int compt =0;
	   double sum = prob[0];
	   while (sum<u && compt<(n-1)) {
		   compt += 1;
		   sum += prob[compt];
	   }
	   //System.out.println("Capacite de l'arete" + father.getLink(i).getCapacityValue(compt) );
	   return father.getLink(i).getCapacityValue(compt);
   }
   
   
   public double run(int n, RandomStream stream, int demande) {
      Chrono timer = new Chrono();
      timer.init();
      Tally values = new Tally(); // unreliability estimates
      for (int j = 0; j < n; j++) {
         double x = doOneRun(stream,demande);
         values.add(x);
      }

      m_ell = values.average();
      m_variance = values.variance();
      double sig = Math.sqrt(m_variance);
      double relvar = m_variance / (m_ell * m_ell); // relative variance
      double relerr = sig / (m_ell * Math.sqrt(n)); // relative error

      System.out.printf("barW_n      = %g%n", m_ell);
      System.out.printf("S_n         = %g%n", sig);
      System.out.printf("var = S_n^2 = %g%n", m_variance);
      System.out.printf("var/n       = %g%n", m_variance / n);
      System.out.printf("rel var(W)  = %g%n%n", relvar);
      System.out.println(values.formatCIStudent(0.95, 4));
      System.out.printf("rel err(barW_n) = %g%n", relerr);

      double cro = timer.getSeconds();
      double tem = cro * m_variance / n;
      System.out.printf("time*var/n      = %.3g%n", tem);
      System.out.printf("time*var/(n*barW_n^2) = %.3g%n%n", tem
            / (m_ell * m_ell));
      System.out.printf("CPU time:   %.1f  sec%n%n%n", cro);
      return relerr;
   }
   
   private static void printTab(double[] t) {
	   int m = t.length;
	   for (int i =0;i<m;i++) {
		   System.out.println(t[i]);
	   }
   }
   private static void printTab(int[] t) {
	   int m = t.length;
	   for (int i =0;i<m;i++) {
		   System.out.println(t[i]);
	   }
   }

}
