package umontreal.ssj.networks.flow;

import java.util.LinkedList;

import umontreal.ssj.networks.NodeBasic;

/**
 *  This class provides several pre builts graphs which can be used for flow reliability
 *  estimation. There are Lattice graphs(4 and 6), Dodecahedron, and networks from Daly and Alexopoulos (2006).
 *
 * WARNING : networks from Daly and Alexopoulos have pre set capacities/probabilities values (as in the
 * paper). Other networks do not have pre set capacity/probability values, and they must
 * be set by hand.
 */
public class ExamplesGraphs {
	
	
	   
		/**
		 * Transforms directed graph into an undirectGraph.
		 * If m is the initial number of links, for each link L = (i,a,b), we add the link
		 * (i+m,b,a) (symetric link). It also has same b,capacity values and probability values
		 * 
		 * @param g
		 *           GraphFlow to be transformed
		 */
	   
	   public static GraphFlow Undirect(GraphFlow g) {
			int numLinks = g.getNumLinks();
			//System.out.print("Nombre links" + numLinks);

			/*Storing the edges that are not present in both ways : source-->target and target-->source */
			LinkedList<Integer> Queue = new LinkedList<Integer>();
			
	  	      for (int i = 0; i < numLinks; i++) {
	   			      Queue.add(i);
	   	      }
	  	    int counterIndiceLink=g.getNumLinks();
	 	    while(!Queue.isEmpty()) {
	 		     /*we pop the first element of the queue*/
	 		     int duplicate=Queue.poll();
	 		     LinkFlow original = g.getLink(duplicate);
	 		     g.addLink(new LinkFlow(counterIndiceLink, original.getTarget(),
	 		        		 original.getSource(), original.getCapacity()));
	 		     g.getLink(counterIndiceLink).setCapacityValues(original.getCapacityValues());
	 		     g.getLink(counterIndiceLink).setProbabilityValues(original.getProbabilityValues());
	 		     g.getLink(counterIndiceLink).setB(original.getB());
	 		     counterIndiceLink++;
	 	      }
			
			return g; 
	   }
	
	
	// Attention, numérotation "mathématique" et non informatique. Le premier lien 
	// est le lien 1, le premier sommet est le sommet 1
	   //LE graphe doit avoir set up capacités et probas
	   public static void toString(GraphFlow g) {
		   int m = g.getNumLinks();
		   for (int i=0;i<m;i++) {
			   LinkFlow Edge = g.getLink(i);
			   int s = Edge.getSource();
			   int t = Edge.getTarget();
			   System.out.println("Lien " + (i+1) + ": " + (s+1) + " et " + (t+1));
			   int b= Edge.getB();
			   for (int j=0;j<(b+1);j++) {
				   System.out.print(Edge.getCapacityValue(j) + " (" + Edge.getProbabilityValue(j) + ") " );
			   }
			   System.out.println();
			   System.out.println();
		   }

	   }

	
	public static GraphFlow buildAlexo1Or() {
		GraphFlow g=new GraphFlow();
		//add Nodes
		for (int i =0;i<10;i++) {
			g.addNode(new NodeBasic(i));
		}
		
		
		// add Links
		g.addLink(new LinkFlow(0,0,1));  //1
		g.addLink(new LinkFlow(1,0,2)); //2
		g.addLink(new LinkFlow(2,0,3)); //3
		g.addLink(new LinkFlow(3,1,4));  //4
		g.addLink(new LinkFlow(4,1,5)); //5
		g.addLink(new LinkFlow(5,1,2)); //6
		g.addLink(new LinkFlow(6,2,6)); //7
		g.addLink(new LinkFlow(7,2,7)); //8
		g.addLink(new LinkFlow(8,2,3));  //9
		g.addLink(new LinkFlow(9,3,8));
		g.addLink(new LinkFlow(10,4,6));
		g.addLink(new LinkFlow(11,4,9	));
		g.addLink(new LinkFlow(12,2,5));
		g.addLink(new LinkFlow(13,4,5));
		g.addLink(new LinkFlow(14,5,6));
		g.addLink(new LinkFlow(15,6,7));
		g.addLink(new LinkFlow(16,6,9));
		g.addLink(new LinkFlow(17,3,7));
		g.addLink(new LinkFlow(18,7,8));
		g.addLink(new LinkFlow(19,6,8));
		g.addLink(new LinkFlow(20,8,9));
		

		//set les B
		int b = 2; // taille b+1
		for (int i = 0;i<21;i++) {
			g.setB(b);
		}
		
		//arc 0 
		double[] Pr0 = new double[b+1];
		Pr0[0] =0.02 ;Pr0[1] = 0.08; Pr0[2] = 0.90;
		g.setProbabilityValues(0, Pr0);
		int[] Cap0 = new int[b+1];
		Cap0[0] = 0; Cap0[1] = 8; Cap0[2] = 12;
		g.setCapacityValues(0, Cap0);
		
		
		//arc 1
		double[] Pr1 = new double[b+1];
		Pr1[0] =0.03 ;Pr1[1] = 0.12; Pr1[2] = 0.85;
		g.setProbabilityValues(1, Pr1);
		int[] Cap1 = new int[b+1];
		Cap1[0] = 0; Cap1[1] = 24; Cap1[2] = 35;
		g.setCapacityValues(1, Cap1);
		
		//arc 2
		double[] Pr2 = new double[b+1];
		Pr2[0] =0.02 ;Pr2[1] = 0.10; Pr2[2] = 0.88;
		g.setProbabilityValues(2, Pr2);
		int[] Cap2 = new int[b+1];
		Cap2[0] = 0; Cap2[1] = 18; Cap2[2] = 24;
		g.setCapacityValues(2, Cap2);
		
		//arc 3
		double[] Pr3 = new double[b+1];
		Pr3[0] =0.05 ;Pr3[1] = 0.20; Pr3[2] = 0.75;
		g.setProbabilityValues(3, Pr3);
		int[] Cap3 = new int[b+1];
		Cap3[0] = 0; Cap3[1] = 35; Cap3[2] = 50;
		g.setCapacityValues(3, Cap3);
		
		//arc 4
		double[] Pr4 = new double[b+1];
		Pr4[0] =0.07 ;Pr4[1] = 0.25; Pr4[2] = 0.68;
		g.setProbabilityValues(4, Pr4);
		int[] Cap4 = new int[b+1];
		Cap4[0] = 0; Cap4[1] = 7; Cap4[2] = 10;
		g.setCapacityValues(4, Cap4);
		
		//arc 5
		double[] Pr5 = new double[b+1];
		Pr5[0] =0.04 ;Pr5[1] = 0.11; Pr5[2] = 0.85;
		g.setProbabilityValues(5, Pr5);
		int[] Cap5 = new int[b+1];
		Cap5[0] = 0; Cap5[1] =22; Cap5[2] = 30;
		g.setCapacityValues(5, Cap5);
		
		//arc 6
		double[] Pr6 = new double[b+1];
		Pr6[0] =0.06 ;Pr6[1] = 0.14; Pr6[2] = 0.80;
		g.setProbabilityValues(6, Pr6);
		int[] Cap6 = new int[b+1];
		Cap6[0] = 0; Cap6[1] =19; Cap6[2] = 24;
		g.setCapacityValues(6, Cap6);
		
		//arc 7
		double[] Pr7 = new double[b+1];
		Pr7[0] =0.08 ;Pr7[1] = 0.17; Pr7[2] = 0.75;
		g.setProbabilityValues(7, Pr7);
		int[] Cap7 = new int[b+1];
		Cap7[0] = 0; Cap7[1] =6; Cap7[2] = 10;
		g.setCapacityValues(7, Cap7);

		//arc 8
		double[] Pr8 = new double[b+1];
		Pr8[0] =0.03 ;Pr8[1] = 0.13; Pr8[2] = 0.84;
		g.setProbabilityValues(8, Pr8);
		int[] Cap8 = new int[b+1];
		Cap8[0] = 0; Cap8[1] =7; Cap8[2] = 12;
		g.setCapacityValues(8, Cap8);
		
		//arc 9
		double[] Pr9 = new double[b+1];
		Pr9[0] =0.09 ;Pr9[1] = 0.06; Pr9[2] = 0.85;
		g.setProbabilityValues(9, Pr9);
		int[] Cap9 = new int[b+1];
		Cap9[0] = 0; Cap9[1] =33; Cap9[2] = 45;
		g.setCapacityValues(9, Cap9);
		
		//arc 10
		double[] Pr10 = new double[b+1];
		Pr10[0] =0.10 ;Pr10[1] = 0.13; Pr10[2] = 0.77;
		g.setProbabilityValues(10, Pr10);
		int[] Cap10 = new int[b+1];
		Cap10[0] = 0; Cap10[1] =14; Cap10[2] = 18;
		g.setCapacityValues(10, Cap10);
		
		//arc 11
		double[] Pr11 = new double[b+1];
		Pr11[0] =0.02 ;Pr11[1] = 0.22; Pr11[2] = 0.76;
		g.setProbabilityValues(11, Pr11);
		int[] Cap11 = new int[b+1];
		Cap11[0] = 0; Cap11[1] =21; Cap11[2] = 25;
		g.setCapacityValues(11, Cap11);
		
		//arc 12
		double[] Pr12 = new double[b+1];
		Pr12[0] =0.12 ;Pr12[1] = 0.43; Pr12[2] = 0.45;
		g.setProbabilityValues(12, Pr12);
		int[] Cap12 = new int[b+1];
		Cap12[0] = 0; Cap12[1] =10; Cap12[2] = 12;
		g.setCapacityValues(12, Cap12);
		
		//arc 13
		double[] Pr13 = new double[b+1];
		Pr13[0] =0.01 ;Pr13[1] = 0.05; Pr13[2] = 0.94;
		g.setProbabilityValues(13, Pr13);
		int[] Cap13 = new int[b+1];
		Cap13[0] = 0; Cap13[1] =27; Cap13[2] = 36;
		g.setCapacityValues(13, Cap13);
		

		//arc 14
		double[] Pr14 = new double[b+1];
		Pr14[0] =0.08 ;Pr14[1] = 0.12; Pr14[2] = 0.80;
		g.setProbabilityValues(14, Pr14);
		int[] Cap14 = new int[b+1];
		Cap14[0] = 0; Cap14[1] =38; Cap14[2] = 50;
		g.setCapacityValues(14, Cap14);
		
		//arc 15
		double[] Pr15 = new double[b+1];
		Pr15[0] =0.03 ;Pr15[1] = 0.15; Pr15[2] = 0.82;
		g.setProbabilityValues(15, Pr15);
		int[] Cap15 = new int[b+1];
		Cap15[0] = 0; Cap15[1] =17; Cap15[2] = 30;
		g.setCapacityValues(15, Cap15);
		
		//arc 16
		double[] Pr16 = new double[b+1];
		Pr16[0] =0.06 ;Pr16[1] = 0.15; Pr16[2] = 0.79;
		g.setProbabilityValues(16, Pr16);
		int[] Cap16 = new int[b+1];
		Cap16[0] = 0; Cap16[1] =13; Cap16[2] = 20;
		g.setCapacityValues(16, Cap16);
		
		//arc 17
		double[] Pr17 = new double[b+1];
		Pr17[0] =0.02 ;Pr17[1] = 0.03; Pr17[2] = 0.95;
		g.setProbabilityValues(17, Pr17);
		int[] Cap17 = new int[b+1];
		Cap17[0] = 0; Cap17[1] =28; Cap17[2] = 45;
		g.setCapacityValues(17, Cap17);
		
		//arc 18
		double[] Pr18 = new double[b+1];
		Pr18[0] =0.10 ;Pr18[1] = 0.05; Pr18[2] = 0.85;
		g.setProbabilityValues(18, Pr18);
		int[] Cap18 = new int[b+1];
		Cap18[0] = 0; Cap18[1] =14; Cap18[2] = 25;
		g.setCapacityValues(18, Cap18);
		
		
		//arc 19
		double[] Pr19 = new double[b+1];
		Pr19[0] =0.08 ;Pr19[1] = 0.42; Pr19[2] = 0.5;
		g.setProbabilityValues(19, Pr19);
		int[] Cap19 = new int[b+1];
		Cap19[0] = 0; Cap19[1] =11; Cap19[2] = 24;
		g.setCapacityValues(19, Cap19);
		
		//arc 20
		double[] Pr20 = new double[b+1];
		Pr20[0] =0.01 ;Pr20[1] = 0.04; Pr20[2] = 0.95;
		g.setProbabilityValues(20, Pr20);
		int[] Cap20 = new int[b+1];
		Cap20[0] = 0; Cap20[1] =23; Cap20[2] = 30;
		g.setCapacityValues(20, Cap20);
		
		
		return g;
	}
	
	
	// Graph 1 of Daly and Alexopoulos, with capacities/probabities all set
	
	public static GraphFlow buildAlexo1NoOr() {
		GraphFlow g=buildAlexo1Or();
		return Undirect(g);
	}
	
	
	
	// Basic build of network no3 of Daly and Alexopoulos, with orientation ("defined" by the paper
	// ")
	
	public static GraphFlow buildAlexo3Or() {
		GraphFlow g=new GraphFlow();
		//add Nodes
		for (int i =0;i<7;i++) {
			g.addNode(new NodeBasic(i));
		}
		
		// add Links
		g.addLink(new LinkFlow(0,0,1));  //1
		g.addLink(new LinkFlow(1,0,2));  //2
		g.addLink(new LinkFlow(2,0,3));  //3
		g.addLink(new LinkFlow(3,1,3));  //4
		g.addLink(new LinkFlow(4,1,4));   //5
		g.addLink(new LinkFlow(5,2,3));   //6
		g.addLink(new LinkFlow(6,2,5));  // 7
		g.addLink(new LinkFlow(7,3,4)); //8
		g.addLink(new LinkFlow(8,3,5)); 
		g.addLink(new LinkFlow(9,3,6)); 
		g.addLink(new LinkFlow(10,4,6)); 
		g.addLink(new LinkFlow(11,5,6)); 
		
		//set les B
		int b = 9; // taille b+1
		for (int i = 0;i<12;i++) {
			g.setB(b);
		}
		
		//set les Proba Values
		double[] tabProba = new double[b+1];
		for (int i = 0;i<b+1;i++) {
			tabProba[i] = 0.1;
		}
		for (int i = 0;i<12;i++) {
			g.setProbabilityValues(tabProba);
		}
		
		// set les capcValues
		//arc 0 
		int[] tab0 = new int[b+1];
		
		tab0[0] =0; tab0[1] =3;tab0[2]=6;tab0[3]=8;tab0[4]=9; tab0[5] =10;
		tab0[6] =12; tab0[7]=15;tab0[8] =16;tab0[9]=18;
		g.setCapacityValues(0, tab0);
		//arc 1
		int[] tab1 = new int[b+1];
		tab1[0] =0; tab1[1] =2;tab1[2]=8;tab1[3]=9;tab1[4]=10; tab1[5] =12;
		tab1[6] =14; tab1[7]=15;tab1[8] =16;tab1[9]=19;
		g.setCapacityValues(1, tab1);
		//arc 2
		int[] tab2 = new int[b+1];
		tab2[0] =0; tab2[1] =3; tab2[2]=8;tab2[3]=10;tab2[4]=11; tab2[5] =12;
		tab2[6] =15; tab2[7]=16;tab2[8] =20;tab2[9]=21;
		g.setCapacityValues(2, tab2);
		
		//arc 3

		int[] tab3 = new int[b+1];
		tab3[0] =0; tab3[1] =3; tab3[2]=5;tab3[3]=7;tab3[4]=9; tab3[5] =10;
		tab3[6] =11; tab3[7]=14;tab3[8] =16;tab3[9]=17;
		g.setCapacityValues(3, tab3);
		

		
		//arc 4
		int[] tab4 = new int[b+1];
		tab4[0] =0; tab4[1] =2; tab4[2]=3;tab4[3]=4;tab4[4]=7; tab4[5] =12;
		tab4[6] =14; tab4[7]=15;tab4[8] =16;tab4[9]=17;
		g.setCapacityValues(4, tab4);
		

		
		//arc 5
		int[] tab5 = new int[b+1];
		tab5[0] =0; tab5[1] =6; tab5[2]=9;tab5[3]=10;tab5[4]=13; tab5[5] =14;
		tab5[6] =16; tab5[7]=19;tab5[8] =22;tab5[9]=24;
		g.setCapacityValues(5, tab5);
		
		//arc 6
		int[] tab6 = new int[b+1];
		tab6[0] =0; tab6[1] =2; tab6[2]=3;tab6[3]=4;tab6[4]=5; tab6[5] =6;
		tab6[6] =7; tab6[7]=11;tab6[8] =19;tab6[9]=20;
		g.setCapacityValues(6, tab6);
		
		//arc 7
		int[] tab7 = new int[b+1];
		tab7[0] =0; tab7[1] =3; tab7[2]=6;tab7[3]=9;tab7[4]=10; tab7[5] =11;
		tab7[6] =12; tab7[7]=13;tab7[8] =14;tab7[9]=15;
		g.setCapacityValues(7, tab7);
		
		//arc 8
		int[] tab8 = new int[b+1];
		tab8[0] =0; tab8[1] =5; tab8[2]=9;tab8[3]=10;tab8[4]=11; tab8[5] =12;
		tab8[6] =13; tab8[7]=14;tab8[8] =16;tab8[9]=18;
		g.setCapacityValues(8, tab8);
		
		//arc 9
		int[] tab9 = new int[b+1];
		tab9[0] =0; tab9[1] =1; tab9[2]=3;tab9[3]=5;tab9[4]=7; tab9[5] =9;
		tab9[6] =11; tab9[7]=13;tab9[8] =15;tab9[9]=17;
		g.setCapacityValues(9, tab9);
		
		//arc 10

		int[] tab10 = new int[b+1];
		tab10[0] =0; tab10[1] =4; tab10[2]=5;tab10[3]=6;tab10[4]=8; tab10[5] =9;
		tab10[6] =10; tab10[7]=14;tab10[8] =18;tab10[9]=19;
		g.setCapacityValues(10, tab10);
		
        //arc 11
		int[] tab11 = new int[b+1];
		tab11[0] =0; tab11[1] =1; tab11[2]=2;tab11[3]=5;tab11[4]=9; tab11[5] =10;
		tab11[6] =13; tab11[7]=14;tab11[8] =15;tab11[9]=17;
		g.setCapacityValues(11, tab11);

	return g;
	}
	
	// Graph 3 of Daly and Alexopoulos, with capacities/probabities all set
	public static GraphFlow buildAlexo3NoOr() {
		GraphFlow g = buildAlexo3Or();
		return Undirect(g);
	}
	
		//Lattice Graph 4*4 with pseudo orientation (left -> right and up -> down)
	   public static GraphFlow buildLatt4() {
		   GraphFlow g=new GraphFlow();
		   for (int i = 0;i<16;i++) {
			   g.addNode(new NodeBasic(i));
		   }
		   
		   //horizontales
		   int m = 0;
		   for (int i = 0; i<3;i++) {
			   g.addLink(new LinkFlow(m,i,i+1));
			   m++;
		   }
		   for (int i = 4; i<7;i++) {
			   g.addLink(new LinkFlow(m,i,i+1));
			   m++;
		   }
		   for (int i = 8; i<11;i++) {
			   g.addLink(new LinkFlow(m,i,i+1));
			   m++;
		   }
		   for (int i = 12; i<15;i++) {
			   g.addLink(new LinkFlow(m,i,i+1));
			   m++;
		   }
		   
		   //verticales
		   for (int i = 0; i<12;i+=4) {
			   g.addLink(new LinkFlow(m,i,i+4));
			   m++;
		   }
		   for (int i = 1; i<13;i+=4) {
			   g.addLink(new LinkFlow(m,i,i+4));
			   m++;
		   }
		   for (int i = 2; i<14;i+=4) {
			   g.addLink(new LinkFlow(m,i,i+4));
			   m++;
		   }
		   for (int i = 3; i<15;i+=4) {
			   g.addLink(new LinkFlow(m,i,i+4));
			   m++;
		   }
		   return g;
	   }
	   
	 //Lattice Graph 4*4 with no orientation. No capacities or probabilities set
	   public static GraphFlow buildLatt4NoOr() {
		   GraphFlow g = buildLatt4();
		   return Undirect(g);
	   }
	   
	   
	 //Lattice Graph 6*6 with pseudo orientation (left -> right and up -> down)
	   public static GraphFlow buildLatt6Or() {
		      GraphFlow g=new GraphFlow();
		      
		      int count = 0; //number of links
		      for (int i = 0;i<36;i++) {
		    	  g.addNode(new NodeBasic(i));
		      }
		      // aretes horizontales
		      
		      for (int i=0; i<6;i++) { //ligne i
		    	  for (int j=0;j<5;j++) {
		    	  g.addLink(new LinkFlow(count,6*i +j, 6*i +j+1));
		    	  count++;
		    	  }
		      }
		      //aretes verticales
		      
		      for (int j=0; j<6;j++) { //ligne i
		    	  for (int i=0;i<5;i++) {
		    	  g.addLink(new LinkFlow(count, 6*i +j, 6*i +j + 6));
		    	  count++;
		    	  }
		      }
		      return g;
	   }
	   
	   // Lattice Graph 6*6 with no orientation. No capacities or probabilities set
	   public static GraphFlow buildLatt6NoOr() {
		   GraphFlow g = buildLatt6Or();
		   return Undirect(g);
	   } 
	   


	   
	//Dodecahedron Graph with pseudo orientation (left -> right and inside -> outside)
	   public static GraphFlow buildDodecaOr() {
		   GraphFlow g=new GraphFlow();
		      
		      
		      for (int i = 0;i<20;i++) {
		    	  g.addNode(new NodeBasic(i));
		      }
		      
		      g.addLink(new LinkFlow(0,0,1));
		      g.addLink(new LinkFlow(1,1,4));
		      g.addLink(new LinkFlow(2,4,9));
		      g.addLink(new LinkFlow(3,9,3));
		      g.addLink(new LinkFlow(4,3,0));
		      
		      
		      g.addLink(new LinkFlow(5,2,6));
		      g.addLink(new LinkFlow(6,6,5));
		      g.addLink(new LinkFlow(7,5,11));
		      g.addLink(new LinkFlow(8,11,10));
		      g.addLink(new LinkFlow(9,10,16));
		      g.addLink(new LinkFlow(10,16,15));
		      g.addLink(new LinkFlow(11,15,14));
		      g.addLink(new LinkFlow(12,14,8));
		      g.addLink(new LinkFlow(13,8,7));
		      g.addLink(new LinkFlow(14,7,2));
		      
		      g.addLink(new LinkFlow(15,13,12));
		      g.addLink(new LinkFlow(16,12,17));
		      g.addLink(new LinkFlow(17,17,19));
		      g.addLink(new LinkFlow(18,19,18));
		      g.addLink(new LinkFlow(19,18,13));
		      
		      
		      
		      
		      g.addLink(new LinkFlow(20,7,13));
		      g.addLink(new LinkFlow(21,6,12));
		      g.addLink(new LinkFlow(22,11,17));
		      g.addLink(new LinkFlow(23,16,19));
		      g.addLink(new LinkFlow(24,14,18));
		      
		      g.addLink(new LinkFlow(25,0,2));
		      g.addLink(new LinkFlow(26,1,5));
		      g.addLink(new LinkFlow(27,4,10));
		      g.addLink(new LinkFlow(28,9,15));
		      g.addLink(new LinkFlow(29,3,8));
		      
	
		      return g;
	   }

	// Dodecahedron Graph with no orientation. No capacities or probabilities set
	   public static GraphFlow buildDodecaNoOr() {
		   GraphFlow g = buildDodecaOr();
		   return Undirect(g);
	   }

	   

}
