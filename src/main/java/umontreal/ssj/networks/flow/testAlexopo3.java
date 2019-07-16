package umontreal.ssj.networks.flow;

import java.io.IOException;

import umontreal.ssj.networks.NodeBasic;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testAlexopo3 {
	
	
	public static void main(String[] args) throws IOException {
		GraphFlow g = buildAlexo3();
		int demande = 20;
	    g.setSource(0);
	    g.setTarget(6);
	    
	    PMC p = new PMC(g);
	    RandomStream stream = new LFSR113();
	    p.trimCapacities(demande);
	    p.run(50000,stream,demande, true);
	    
	    
	    
		
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

}
