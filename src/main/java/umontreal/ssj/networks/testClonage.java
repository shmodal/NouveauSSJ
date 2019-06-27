package umontreal.ssj.networks;

import java.io.IOException;
import java.util.ArrayList;

public class testClonage {

	public static void main(String[] args) {
		princ();

	}
	
	private static void princ() {
		GraphReliability g = new GraphReliability();
		g.addNode(new NodeBasic(0));
		g.addNode(new NodeBasic(1));
		g.addNode(new NodeBasic(1));
		g.addLink(new LinkReliability(0,0,1,0.9));
		
		NodeBasic n1 = g.getNode(0);
		System.out.println("Compteur :" +n1.getCounter() );
		System.out.println("Nombre :" +n1.getNumber());
		ArrayList<Integer> links1 = n1.getNodeLinks();
		System.out.println("Nombre :" + links1.get(0) );
		
		
		
		
		GraphReliability h = g.clone();
		NodeBasic n = h.getNode(0);
		//System.out.println("Compteur :" +n.getCounter() );
		//System.out.println("Nombre :" +n.getNumber());
		ArrayList<Integer> links = n.getNodeLinks();
		//System.out.println("Nombre :" +links.get(0));
		
		System.out.println(g.toString());
		System.out.println(h.toString());
		
		
		ArrayList<LinkReliability> tab =  h.getLinks();
		LinkReliability l = tab.get(0);
		System.out.println(l.getSource());
		System.out.println(l.getTarget());
		
	}

}
