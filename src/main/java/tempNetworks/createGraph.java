package tempNetworks;

import java.io.IOException;

public class createGraph {
   /** Home of Rare-Events software */
	// Fill according to your data
   public static final String RAREHOME = "/u/nervogui/Documents/Librairie rarev";

   private static final String DIRDAT = "/data/";

   public static void main(String[] args) throws IOException {
      int m = 7;
      int n = 3;
      
      LinkWithCapacity l=new LinkWithCapacity(0,0,1);
      System.out.println(l.getIndice());
    //  complete(n);
    //  square(n);
    //  lattice(m, n);
    //  stripe(m, n);
    //  series(n);
    //  parallel(m, n);
   }

   private static void complete(int n) throws IOException {
      String ns = Integer.toString(n);
      String name = RAREHOME + DIRDAT + ns + "-Complete" + ".txt";
      GraphCreator.createCompleteGraph(name, n);
   }

   private static void square(int n) throws IOException {
      String ns = Integer.toString(n);
      String name = RAREHOME + DIRDAT + ns + "-Square" + ".txt";
      GraphCreator.createSquareGraph(name, n);
   }

   private static void lattice(int m, int n) throws IOException {
      String ms = Integer.toString(m);
      String ns = Integer.toString(n);
      String name = RAREHOME + DIRDAT + ns + "-" + ms + "-Lattice" + ".txt";
      GraphCreator.createLatticeGraph(name, n, m);
   }

   private static void stripe(int m, int n) throws IOException {
      String ms = Integer.toString(m);
      String ns = Integer.toString(n);
      String name = RAREHOME + DIRDAT + ns + "-" + ms + "-Stripe" + ".txt";
      GraphCreator.createStripeGraph(name, n, m);
   }

   private static void series(int n) throws IOException {
      String ns = Integer.toString(n);
      String name = RAREHOME + DIRDAT + ns + "-Series" + ".txt";
      GraphCreator.createSeriesGraph(name, n);
   }

   private static void parallel(int m, int n) throws IOException {
      String ms = Integer.toString(m);
      String ns = Integer.toString(n);
      String name = RAREHOME + DIRDAT + ms + "-" + ns + "-Parallel" + ".txt";
      GraphCreator.createParallelGraph(name, m, n);
   }

}
