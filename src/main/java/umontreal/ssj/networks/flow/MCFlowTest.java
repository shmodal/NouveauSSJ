package umontreal.ssj.networks.flow;

import umontreal.ssj.networks.NodeBasic;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class MCFlowTest {

	public static void main(String[] args) {
		
		
		
		// Calcul de la fiabilité : 1 si on satisfait la demande
		
		// Build graph 3 Daly and Alexopoulos
		
		
		//GraphFlow g3 = buildAlexo3();
		
		//int demande = 20;
	    //g3.setSource(0);
	    //g3.setTarget(6);
	    
	    //MonteCarloFlow mc3 = new MonteCarloFlow(g3);
	    
	    //RandomStream stream = new LFSR113();
	    //mc3.run(50000,stream,demande);
	    //System.out.println(mc3.doOneRun(stream,demande));
	    
	    
	    
		GraphFlow g1 = buildAlexo1NoOr();
		
		int demande = 60;
	    g1.setSource(0);
	    g1.setTarget(9);
	    
	    MonteCarloFlow mc1 = new MonteCarloFlow(g1);
	    
	    RandomStream stream = new LFSR113();
	    //mc1.doOneRun(stream, demande);
	    mc1.run(50000,stream,demande);

		
//		int m = g1.getNumLinks();
//		for (int i=0;i<m;i++) {
//			LinkFlow Edge = g1.getLink(i);
//			int a = Edge.getSource();
//			int b = Edge.getTarget();
//			System.out.println("Lien " + (i+1) + ": " + (a+1) + " et " + (b+1));
//			System.out.println("Capacités");
//			printTab(Edge.getProbabilityValues());
//		}
	    
	}

	
	
	
	public static GraphFlow buildAlexo3() {
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
		g.addLink(new LinkFlow(6,2,4));  // 7
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
	
		public static GraphFlow buildAlexo1() {
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
			Pr0[0] =0.02 ;Pr0[1] = 0.08; Pr0[2] = 0.9;
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
			Pr2[0] =0.02 ;Pr2[1] = 0.1; Pr2[2] = 0.88;
			g.setProbabilityValues(2, Pr2);
			int[] Cap2 = new int[b+1];
			Cap2[0] = 0; Cap2[1] = 18; Cap2[2] = 24;
			g.setCapacityValues(2, Cap2);
			
			//arc 3
			double[] Pr3 = new double[b+1];
			Pr3[0] =0.05 ;Pr3[1] = 0.2; Pr3[2] = 0.75;
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
			Pr10[0] =0.1 ;Pr10[1] = 0.13; Pr10[2] = 0.77;
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
		
		

		   
		public static GraphFlow buildAlexo1NoOr() {
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
			g.addLink(new LinkFlow(11,4,9));
			g.addLink(new LinkFlow(12,2,5));
			g.addLink(new LinkFlow(13,4,5));
			g.addLink(new LinkFlow(14,5,6));
			g.addLink(new LinkFlow(15,6,7));
			g.addLink(new LinkFlow(16,6,9));
			g.addLink(new LinkFlow(17,3,7));
			g.addLink(new LinkFlow(18,7,8));
			g.addLink(new LinkFlow(19,6,8));
			g.addLink(new LinkFlow(20,8,9));
			
			
			
			//Autre sens
			
			g.addLink(new LinkFlow(21,1,0));  //1
			g.addLink(new LinkFlow(22,2,0)); //2
			g.addLink(new LinkFlow(23,3,0)); //3
			g.addLink(new LinkFlow(24,4,1));  //4
			g.addLink(new LinkFlow(25,5,1)); //5
			g.addLink(new LinkFlow(26,2,1)); //6
			g.addLink(new LinkFlow(27,6,2)); //7
			g.addLink(new LinkFlow(28,7,2)); //8
			g.addLink(new LinkFlow(29,3,2));  //9
			g.addLink(new LinkFlow(30,8,3));
			g.addLink(new LinkFlow(31,6,4));
			g.addLink(new LinkFlow(32,9,4));
			g.addLink(new LinkFlow(33,5,2));
			g.addLink(new LinkFlow(34,5,4));
			g.addLink(new LinkFlow(35,6,5));
			g.addLink(new LinkFlow(36,7,6));
			g.addLink(new LinkFlow(37,9,6));
			g.addLink(new LinkFlow(38,7,3));
			g.addLink(new LinkFlow(39,8,7));
			g.addLink(new LinkFlow(40,8,6));
			g.addLink(new LinkFlow(41,9,8));
			
			
			
			
			
			
			
			
			
			
			
			
			

			//set les B
			int b = 2; // taille b+1
			for (int i = 0;i<42;i++) {
				g.setB(b);
			}
			
			//arc 0 
			double[] Pr0 = new double[b+1];
			Pr0[0] =0.02 ;Pr0[1] = 0.08; Pr0[2] = 0.9;
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
			Pr2[0] =0.02 ;Pr2[1] = 0.1; Pr2[2] = 0.88;
			g.setProbabilityValues(2, Pr2);
			int[] Cap2 = new int[b+1];
			Cap2[0] = 0; Cap2[1] = 18; Cap2[2] = 24;
			g.setCapacityValues(2, Cap2);
			
			//arc 3
			double[] Pr3 = new double[b+1];
			Pr3[0] =0.05 ;Pr3[1] = 0.2; Pr3[2] = 0.75;
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
			Pr10[0] =0.1 ;Pr10[1] = 0.13; Pr10[2] = 0.77;
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
			Pr18[0] =0.1 ;Pr18[1] = 0.05; Pr18[2] = 0.85;
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
			
			
			//arc 21 
			double[] Pr21 = new double[b+1];
			Pr21[0] =0.02 ;Pr21[1] = 0.08; Pr21[2] = 0.9;
			g.setProbabilityValues(21, Pr21);
			int[] Cap21 = new int[b+1];
			Cap21[0] = 0; Cap21[1] = 8; Cap21[2] = 12;
			g.setCapacityValues(21, Cap21);
			
			
			//arc 22
			double[] Pr22 = new double[b+1];
			Pr22[0] =0.03 ;Pr22[1] = 0.12; Pr22[2] = 0.85;
			g.setProbabilityValues(22, Pr22);
			int[] Cap22 = new int[b+1];
			Cap22[0] = 0; Cap22[1] = 24; Cap22[2] = 35;
			g.setCapacityValues(22, Cap22);
			
			//arc 23
			double[] Pr23 = new double[b+1];
			Pr23[0] =0.02 ;Pr23[1] = 0.1; Pr23[2] = 0.88;
			g.setProbabilityValues(23, Pr23);
			int[] Cap23 = new int[b+1];
			Cap23[0] = 0; Cap23[1] = 18; Cap23[2] = 24;
			g.setCapacityValues(23, Cap23);
			
			//arc 24
			double[] Pr24 = new double[b+1];
			Pr24[0] =0.05 ;Pr24[1] = 0.2; Pr24[2] = 0.75;
			g.setProbabilityValues(24, Pr24);
			int[] Cap24 = new int[b+1];
			Cap24[0] = 0; Cap24[1] = 35; Cap24[2] = 50;
			g.setCapacityValues(24, Cap24);
			
			//arc 25
			double[] Pr25 = new double[b+1];
			Pr25[0] =0.07 ;Pr25[1] = 0.25; Pr25[2] = 0.68;
			g.setProbabilityValues(25, Pr25);
			int[] Cap25 = new int[b+1];
			Cap25[0] = 0; Cap25[1] = 7; Cap25[2] = 10;
			g.setCapacityValues(25, Cap25);
			
			//arc 26
			double[] Pr26 = new double[b+1];
			Pr26[0] =0.04 ;Pr26[1] = 0.11; Pr26[2] = 0.85;
			g.setProbabilityValues(26, Pr26);
			int[] Cap26 = new int[b+1];
			Cap26[0] = 0; Cap26[1] =22; Cap26[2] = 30;
			g.setCapacityValues(26, Cap26);
			
			//arc 27
			double[] Pr27 = new double[b+1];
			Pr27[0] =0.06 ;Pr27[1] = 0.14; Pr27[2] = 0.80;
			g.setProbabilityValues(27, Pr27);
			int[] Cap27 = new int[b+1];
			Cap27[0] = 0; Cap27[1] =19; Cap27[2] = 24;
			g.setCapacityValues(27, Cap27);
			
			//arc 28
			double[] Pr28 = new double[b+1];
			Pr28[0] =0.08 ;Pr28[1] = 0.17; Pr28[2] = 0.75;
			g.setProbabilityValues(28, Pr28);
			int[] Cap28 = new int[b+1];
			Cap28[0] = 0; Cap28[1] =6; Cap28[2] = 10;
			g.setCapacityValues(28, Cap28);

			//arc 29
			double[] Pr29 = new double[b+1];
			Pr29[0] =0.03 ;Pr29[1] = 0.13; Pr29[2] = 0.84;
			g.setProbabilityValues(29, Pr29);
			int[] Cap29 = new int[b+1];
			Cap29[0] = 0; Cap29[1] =7; Cap29[2] = 12;
			g.setCapacityValues(29, Cap29);
			
			//arc 30
			double[] Pr30 = new double[b+1];
			Pr30[0] =0.09 ;Pr30[1] = 0.06; Pr30[2] = 0.85;
			g.setProbabilityValues(30, Pr30);
			int[] Cap30 = new int[b+1];
			Cap30[0] = 0; Cap30[1] =33; Cap30[2] = 45;
			g.setCapacityValues(30, Cap30);
			
			//arc 31
			double[] Pr31 = new double[b+1];
			Pr31[0] =0.1 ;Pr31[1] = 0.13; Pr31[2] = 0.77;
			g.setProbabilityValues(31, Pr31);
			int[] Cap31 = new int[b+1];
			Cap31[0] = 0; Cap31[1] =14; Cap31[2] = 18;
			g.setCapacityValues(31, Cap31);
			
			//arc 32
			double[] Pr32 = new double[b+1];
			Pr32[0] =0.02 ;Pr32[1] = 0.22; Pr32[2] = 0.76;
			g.setProbabilityValues(32, Pr32);
			int[] Cap32 = new int[b+1];
			Cap32[0] = 0; Cap32[1] =21; Cap32[2] = 25;
			g.setCapacityValues(32, Cap32);
			
			//arc 33
			double[] Pr33 = new double[b+1];
			Pr33[0] =0.12 ;Pr33[1] = 0.43; Pr33[2] = 0.45;
			g.setProbabilityValues(33, Pr33);
			int[] Cap33 = new int[b+1];
			Cap33[0] = 0; Cap33[1] =10; Cap33[2] = 12;
			g.setCapacityValues(33, Cap33);
			
			//arc 34
			double[] Pr34 = new double[b+1];
			Pr34[0] =0.01 ;Pr34[1] = 0.05; Pr34[2] = 0.94;
			g.setProbabilityValues(34, Pr34);
			int[] Cap34 = new int[b+1];
			Cap34[0] = 0; Cap34[1] =27; Cap34[2] = 36;
			g.setCapacityValues(34, Cap34);
			

			//arc 35
			double[] Pr35 = new double[b+1];
			Pr35[0] =0.08 ;Pr35[1] = 0.12; Pr35[2] = 0.80;
			g.setProbabilityValues(35, Pr35);
			int[] Cap35 = new int[b+1];
			Cap35[0] = 0; Cap35[1] =38; Cap35[2] = 50;
			g.setCapacityValues(35, Cap35);
			
			//arc 36
			double[] Pr36 = new double[b+1];
			Pr36[0] =0.03 ;Pr36[1] = 0.15; Pr36[2] = 0.82;
			g.setProbabilityValues(36, Pr36);
			int[] Cap36 = new int[b+1];
			Cap36[0] = 0; Cap36[1] =17; Cap36[2] = 30;
			g.setCapacityValues(36, Cap36);
			
			//arc 37
			double[] Pr37 = new double[b+1];
			Pr37[0] =0.06 ;Pr37[1] = 0.15; Pr37[2] = 0.79;
			g.setProbabilityValues(37, Pr37);
			int[] Cap37 = new int[b+1];
			Cap37[0] = 0; Cap37[1] =13; Cap37[2] = 20;
			g.setCapacityValues(37, Cap37);
			
			//arc 38
			double[] Pr38 = new double[b+1];
			Pr38[0] =0.02 ;Pr38[1] = 0.03; Pr38[2] = 0.95;
			g.setProbabilityValues(38, Pr38);
			int[] Cap38 = new int[b+1];
			Cap38[0] = 0; Cap38[1] =28; Cap38[2] = 45;
			g.setCapacityValues(38, Cap38);
			
			//arc 39
			double[] Pr39 = new double[b+1];
			Pr39[0] =0.1 ;Pr39[1] = 0.05; Pr39[2] = 0.85;
			g.setProbabilityValues(39, Pr39);
			int[] Cap39 = new int[b+1];
			Cap39[0] = 0; Cap39[1] =14; Cap39[2] = 25;
			g.setCapacityValues(39, Cap39);
			
			
			//arc 40
			double[] Pr40 = new double[b+1];
			Pr40[0] =0.08 ;Pr40[1] = 0.42; Pr40[2] = 0.5;
			g.setProbabilityValues(40, Pr40);
			int[] Cap40 = new int[b+1];
			Cap40[0] = 0; Cap40[1] =11; Cap40[2] = 24;
			g.setCapacityValues(40, Cap40);
			
			//arc 41
			double[] Pr41 = new double[b+1];
			Pr41[0] =0.01 ;Pr41[1] = 0.04; Pr41[2] = 0.95;
			g.setProbabilityValues(41, Pr41);
			int[] Cap41 = new int[b+1];
			Cap41[0] = 0; Cap41[1] =23; Cap41[2] = 30;
			g.setCapacityValues(41, Cap41);
			
			
			
			
			
			
			
			
			
			
			return g;
		}
}
