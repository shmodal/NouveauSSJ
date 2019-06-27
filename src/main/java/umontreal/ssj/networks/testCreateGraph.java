package umontreal.ssj.networks;

import java.io.IOException;

public class testCreateGraph {

	public static void main(String[] args) throws IOException {
		princ();

	}
	
	private static void princ() throws IOException {
		GraphReliability g=new GraphReliability();
		g.addNode(new NodeBasic(0));
		g.addNode(new NodeBasic(1));
		g.addNode(new NodeBasic(2));
		g.addNode(new NodeBasic(3));
		
		g.addLink(new LinkReliability(0,0,1,0.9));
		g.addLink(new LinkReliability(1,0,2,0.9));
		g.addLink(new LinkReliability(2,1,3,0.9));
		g.addLink(new LinkReliability(3,2,3,0.9));
		System.out.println(g.toString());
		System.out.println(g.getLink(0).getR());
	}
}
