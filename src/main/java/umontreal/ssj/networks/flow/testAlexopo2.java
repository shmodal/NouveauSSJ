package umontreal.ssj.networks.flow;

import java.io.IOException;

import umontreal.ssj.networks.NodeBasic;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;


//The graph is not finished.


public class testAlexopo2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		GraphFlow g = buildAlexo2();
		int demande = 20;
	    g.setSource(0);
	    g.setTarget(6);
	    
	    PMCFlow p = new PMCFlow(g);
	    RandomStream stream = new LFSR113();
	    p.trimCapacities(demande);
	    p.run(50000,stream,demande);
	    

	}
	
	public static GraphFlow buildAlexo2() {
		GraphFlow g=new GraphFlow();
		//add Nodes
		for (int i =0;i<15;i++) {
			g.addNode(new NodeBasic(i));
		}
		
		// add Links
		
		double[] Pr2 = new double[2];
		int[] Cap2 = new int[2];
		double[] Pr3 = new double[3];
		int[] Cap3 = new int[3];
		double[] Pr4 = new double[4];
		int[] Cap4 = new int[4];
		
		//arc 1
		g.addLink(new LinkFlow(0,0,1));
		int b=2;
		g.setB(0,b);
		
		Pr3[0] =0.1 ;Pr3[1] = 0.3; Pr3[2] = 0.6;
		g.setProbabilityValues(0, Pr3);
		
		Cap3[0] = 16; Cap3[1] = 25; Cap3[2] = 36;
		g.setCapacityValues(0, Cap3);
		// arc 2
		g.addLink(new LinkFlow(1,0,2));
		b=3;
		g.setB(2,b);
		Pr4[0] =0.1 ;Pr4[1] = 0.2; Pr4[2] = 0.2;Pr4[3] = 0.5;  // Erreur dans l'article
		g.setProbabilityValues(1,Pr4);
		Cap4[0] = 21;Cap4[1] = 24; Cap4[2]=25; Cap4[3]=39;
		g.setCapacityValues(1,Cap4);
		//arc 3
		g.addLink(new LinkFlow(2,0,3));
		b=2;
		g.setB(2,b);
		Pr3[0] =0.2 ;Pr3[1] = 0.4; Pr3[2] = 0.4;
		g.setProbabilityValues(2, Pr3);
		Cap3[0] = 11; Cap3[1] = 13; Cap3[2] = 26;
		g.setCapacityValues(2, Cap3);
		
		//arc 4
		g.addLink(new LinkFlow(3,1,10));
		b=2;
		g.setB(3,b);
		Pr3[0] = 0.2 ;Pr3[1] = 0.3; Pr3[2] = 0.5;
		g.setProbabilityValues(3, Pr3);
		Cap3[0] = 24; Cap3[1] = 28; Cap3[2] = 31;
		g.setCapacityValues(3, Cap3);
		
		//arc 5
		g.addLink(new LinkFlow(4,1,4));
		b=1;
		g.setB(4,b);
		Pr2[0]=0.3;Pr2[1]=0.7;
		g.setProbabilityValues(4, Pr2);
		Cap2[0]=11;Cap2[1]=30;
		g.setCapacityValues(4, Cap2);
		
		//arc 6
		g.addLink(new LinkFlow(5,1,5));
		b=2;
		g.setB(5, b);
		Pr3[0]=0.2;Pr3[1]=0.2;Pr3[2]=0.6;
		g.setProbabilityValues(5, Pr3);
		Cap3[0]=13;Cap3[1]=37;Cap3[2]=39;
		g.setCapacityValues(5, Cap3);
		
		//arc 7
		g.addLink(new LinkFlow(6,2,1));
		b=2;
		g.setB(6, b);
		Pr3[0]=0.1;Pr3[1]=0.3;Pr3[2]=0.6;
		g.setProbabilityValues(6, Pr3);
		Cap3[0]=11;Cap3[1]=20;Cap3[2]=24;
		g.setCapacityValues(6, Cap3);
		
		//arc 8
		g.addLink(new LinkFlow(7,2,6));
		b=2;
		g.setB(7, b);
		Pr3[0]=0.3;Pr3[1]=0.3;Pr3[2]=0.4;
		g.setProbabilityValues(7, Pr3);
		Cap3[0]=23;Cap3[1]=30;Cap3[2]=34;
		g.setCapacityValues(7, Cap3);
		
		//arc 9
		g.addLink(new LinkFlow(8,2,7));
		b=2;
		g.setB(8, b);
		Pr3[0]=0.1;Pr3[1]=0.4;Pr3[2]=0.5;
		g.setProbabilityValues(8, Pr3);
		Cap3[0]=14;Cap3[1]=23;Cap3[2]=34;
		g.setCapacityValues(8, Cap3);
		
		//arc 10
		g.addLink(new LinkFlow(9,3,2));
		b=1;
		g.setB(9,b);
		Pr2[0]=0.3;Pr2[1]=0.7;
		g.setProbabilityValues(9,Pr2);
		Cap2[0]=22;Cap2[1]=30;
		g.setCapacityValues(9,Cap2);
		
		
		
		
		
		
		
		
		g.addLink(new LinkFlow(0,0,1));  //1
		g.addLink(new LinkFlow(1,0,2));  //2
	
	
	return g;
	}
	
	

}
