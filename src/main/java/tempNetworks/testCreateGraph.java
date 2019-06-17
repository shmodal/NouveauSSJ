package tempNetworks;

import java.io.IOException;

public class testCreateGraph {

	public static void main(String[] args) throws IOException {
		princ();

	}
	
	private static void princ() throws IOException {
		GraphNonOriented g=new GraphNonOriented();
		g.addNode(new NodeBasic(0));
		g.addNode(new NodeBasic(1));
		g.addNode(new NodeBasic(2));
		g.addNode(new NodeBasic(3));
		
		g.addLink(new LinkReliability(0,0,1,0.9));
		g.addLink(new LinkReliability(1,0,2,0.9));
		g.addLink(new LinkReliability(2,1,3,0.9));
		g.addLink(new LinkReliability(3,2,3,0.9));
		System.out.println(g.toString());
	}
}
