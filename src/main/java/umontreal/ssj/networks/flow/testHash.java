package umontreal.ssj.networks.flow;

import java.io.IOException;
import java.util.HashMap;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;


public class testHash {
	
	
	public static void main(String[] args) throws IOException {
		
		
		   int [] t = new int[2];
		   t[0] = 1;
		   t[1] = 2;
		   
		   int [] p = new int[2];
		   p[0] = 3;
		   p[1] = 7;
		   
		   p[1]=6;
		
		
		 HashMap <Double,int[]> coordinates = new  HashMap <Double,int[]>();
		 coordinates.put(0.5,t);
		 coordinates.put(0.4,t);
		 
		 HashMap <Double,int[]> c = (HashMap<Double, int[]>) coordinates.clone();
		 
		 
		 RandomStream streamPermut =  new LFSR113();
		 
			String file= ExamplesGraphs.getGraphfile("alexo2");
			GraphFlow g = new GraphFlow(file);
			g.Undirect();
			g.resetCapacities();
		   MarkovChainRandomDiscreteCapacities image = new MarkovChainRandomDiscreteCapacities(g,streamPermut,
			   		10);
		   //
		   image.coordinates.put(0.5,t);
		   image.coordinates.put(0.4,t);
		   
		   int[] a = image.coordinates.get(0.5);
		   //System.out.print(a[0]);
		   //System.out.print(a);
		   
		   double [] valuesIm = new double[2];
		   valuesIm[0] = 4; valuesIm[1]=3;
		   image.valuesY = valuesIm;
		   
		   
		   MarkovChainRandomDiscreteCapacities b = image.clone();
		   
		   b.coordinates.put(0.5, a);
		   
		   //int m =b.coordinates.get(0.5)[0];
		   //System.out.print(m);
		   int[] h =b.coordinates.get(0.5);
		   //System.out.print(h);
		   System.out.println(h[0]);
		   System.out.println(h[1]);
		   
		   image.coordinates.remove(0.5);
		   System.out.println(h[0]);
		   System.out.println(h[1]);
		   
		   
		   
		   
		  //System.out.print(b.valuesY[0]);
		   
		   
		
	}

}
