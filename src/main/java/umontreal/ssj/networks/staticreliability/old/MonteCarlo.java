package umontreal.ssj.networks.staticreliability.old;

import umontreal.ssj.networks.*;
import umontreal.ssj.networks.old.GraphOld;
import umontreal.ssj.networks.old.LinkOld;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.rng.*;
import umontreal.ssj.stat.*;
import umontreal.ssj.util.*;

/**
 * This class implements the simple Monte Carlo method to estimate the
 * <b>unreliability</b> of a network. It uses the constructive schema,
 * i.e., it assumes that initially, all links are failed.
 * 
 */
public class MonteCarlo {
   private double m_variance; // variance
   private double m_ell; // network unreliability
   protected GraphWithForest forest;
   protected GraphOld father; // same father as in forest

   
   public MonteCarlo(GraphOld graph, GraphWithForest forest) {
      father = graph;
      this.forest = forest;
   }

   
   public double run(int n, RandomStream stream) {
      Chrono timer = new Chrono();
      timer.init();
      Tally values = new Tally(); // unreliability estimates
      for (int j = 0; j < n; j++) {
         double x = doOneRun(stream);
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

   
   protected double doOneRun(RandomStream stream) {
      forest.initForestNotWeights();
      drawConfig(stream);
      if (forest.isConnected())
         return 0.0;
      else
         return 1.0;   // unreliability is estimated
   }

   /**
    * Generate a random configuration of the network.
    * 
    * @param stream random stream
    */
   private void drawConfig(RandomStream stream) {
      int m = father.getNumLinks();
      LinkOld link;
      double r;
      for (int j = 0; j < m; j++) {
         link = father.getLink(j);
         r = link.getR();
         if (stream.nextDouble() < r) {
            forest.setOperational(j, true);
            forest.insertLink(link);
         }
      }
   }

  
   /**
    * Returns the variance of the network reliability.
    * 
    * @return the variance
    */
   public double getVariance() {
      return m_variance;
   }

   /**
    * Returns the estimate of the network reliability.
    * 
    * @return the estimate
    */
   public double getEstimate() {
      return m_ell;
   }

}
