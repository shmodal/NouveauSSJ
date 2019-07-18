package umontreal.ssj.networks;

import java.io.IOException;
import umontreal.ssj.networks.*;

public class testGraph {

	  public static void main(String[] args) throws IOException {
	      GraphWithCapacity g=new GraphWithCapacity();
	      
	      g.addNode(new NodeBasic(0));
	      g.addNode(new NodeBasic(1));
	      g.addNode(new NodeBasic(2));
	      g.addNode(new NodeBasic(3));
	      g.addNode(new NodeBasic(4));
	      g.addNode(new NodeBasic(5));
	      g.addNode(new NodeBasic(6));
	      g.addNode(new NodeBasic(7));
	      
	      g.addLink(new LinkWithCapacity(0,0,1,4));
	      g.addLink(new LinkWithCapacity(1,0,2,18));
	      g.addLink(new LinkWithCapacity(2,0,3,5));
	      g.addLink(new LinkWithCapacity(3,1,5,6));
	      g.addLink(new LinkWithCapacity(4,1,4,2));
	      g.addLink(new LinkWithCapacity(5,3,2,8));
	      g.addLink(new LinkWithCapacity(6,2,4,10));
	      g.addLink(new LinkWithCapacity(7,2,5,3));
	      g.addLink(new LinkWithCapacity(8,2,6,2));
	      g.addLink(new LinkWithCapacity(9,3,6,8));
	      g.addLink(new LinkWithCapacity(10,4,7,9));
	      g.addLink(new LinkWithCapacity(11,5,7,7));
	      g.addLink(new LinkWithCapacity(12,6,7,10));
	      
	      
	      g.setSource(0);
	      g.setTarget(7);
	      
	      System.out.println(g.toString());
	      MaxFlowEdmondsKarp EK=new MaxFlowEdmondsKarp(g);
	      //System.out.println(g.residual().toString());
	      System.out.println(EK.EdmondsKarp());
	      //EK.IncreaseLinkCapacity(0,5);
	      //System.out.println(EK.EdmondsKarp());
	   }
}
