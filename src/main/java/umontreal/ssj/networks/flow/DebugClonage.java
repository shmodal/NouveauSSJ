package umontreal.ssj.networks.flow;

import java.util.HashMap;
import java.util.Map;

public class DebugClonage {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		
		
		HashMap <Double,int[]> coordinates  = new HashMap <Double,int[]>();
		
		int[] t = new int[2];
		t[0]=3;t[1]=4;
		coordinates.put(2.524, t);
//		
		t = new int[2];
		t[0]=6;t[1]=7;
//		
		coordinates.put(1.121, t);
		
		
        System.out.println("La table initiale:");
        for (Map.Entry mapentry : coordinates.entrySet()) {
           System.out.println("clé: "+mapentry.getKey() 
                              + " | valeur: " + mapentry.getValue());
        }

        //Dbut clonage
        
        HashMap <Double,int[]> clone = new HashMap <Double,int[]>();
        
        
        for (Map.Entry mapentry : coordinates.entrySet()) {
	           double key = (double) mapentry.getKey(); //est ce que ca marche ?
	           System.out.println(key);
	           int[] t0 = coordinates.get(key);
	           int i = t0[0];
	           int k = t0[1];
	           System.out.println(i);
	           System.out.println(k);
	           int[] tab = new int[2]; tab[0] =i; tab[1]=k;
	           clone.put(key, tab);
     		
     }

        System.out.println("Clone :");
        
        for (Map.Entry mapentry : clone.entrySet()) {
	           double key = (double) mapentry.getKey(); //est ce que ca marche ?
	           System.out.println(key);
	           int[] t0 = coordinates.get(key);
	           int i = t0[0];
	           int k = t0[1];
	           System.out.println(i);
	           System.out.println(k);
  		
  }
		
		
		
	}

}
