package umontreal.ssj.networks.staticreliability;

import java.io.IOException;
import java.util.*;

import umontreal.ssj.networks.GraphReliability;


/**
 * @author simardr
 * 
 */
public class shocktest {

   /**
    * @param args
    */
   public static void main(String[] args) throws IOException {
      String filename = getGraphfile();
      GraphReliability graph = new GraphReliability(filename);
      filename = getShockfile();
      ShockList shocks = new ShockList(graph, filename);
      ShockList image = shocks.clone();
      
      Set<Integer> choc = shocks.getShock(2);
      choc.add(1);
      for (Integer link : choc)
         System.out.print(" " + link);
      System.out.println("");
                
      String chocs = shocks.toString();
      System.out.println("\n" + chocs);
      
      int m = graph.getNumLinks();
      System.out.println("Counters");
      for (int j = 0; j < -m; ++j)
         System.out.printf("%4d  %3d\n", j, shocks.getCount(j));
      
      String cha = image.toString();
      System.out.println("\n" + cha);
   }

   private static String getShockfile() {
      String filename = "choc1";
      //String file = "/u/simardr/java/rarev/data/chocs/" + filename + ".txt";
      //String file = "/u/hadjisof/Documents/Librairie rarev/data/chocs/" + filename + ".txt";
      String file = "C:\\Users\\Sofiane\\Documents\\Stage 3A\\Librairie compress\\Librairie rarev\\data\\chocs\\" + filename + ".txt";
      return file;
   }

   private static String getGraphfile() {
      String legraphe;
      // choose a network
      
      // legraphe = "4-8-Lattice";
      legraphe = "6-Complete";
      legraphe = "3-Square";
      // legraphe = "fuller";
      // legraphe = "dodac3paragain";
     // legraphe = "dodecahedron";
      // legraphe = "dodac3serie";
      
      //String file = "/u/hadjisof/Documents/Librairie rarev/data/" + legraphe + ".txt";
      String file = "C:\\Users\\Sofiane\\Documents\\Stage 3A\\Librairie compress\\Librairie rarev\\data\\" + legraphe + ".txt";
      return file;
   }

}
