package umontreal.ssj.networks;

import java.io.IOException;
import umontreal.ssj.networks.*;

public class testGraph {

	  public static void main(String[] args) throws IOException {
	      GraphWithCapacity g=new GraphWithCapacity();
	      
	      /*
	      g.addNode(new NodeBasic(0));
	      g.addNode(new NodeBasic(1));
	      g.addNode(new NodeBasic(2));
	      g.addNode(new NodeBasic(3));
	      g.addNode(new NodeBasic(4));
	      g.addNode(new NodeBasic(5));
	      
	      g.addLink(new LinkWithCapacity(0,0,1,15));
	      g.addLink(new LinkWithCapacity(1,0,2,10));
	      g.addLink(new LinkWithCapacity(2,1,3,4));
	      g.addLink(new LinkWithCapacity(3,1,4,8));
	      g.addLink(new LinkWithCapacity(4,1,2,2));
	      g.addLink(new LinkWithCapacity(5,2,4,9));
	      g.addLink(new LinkWithCapacity(6,4,3,6));
	      g.addLink(new LinkWithCapacity(7,4,5,10));
	      g.addLink(new LinkWithCapacity(8,3,5,10));
	      
	      
	      g.setSource(0);
	      g.setTarget(5);
	      */
	      
	      g.addNode(new NodeBasic(0));
	      g.addNode(new NodeBasic(1));
	      g.addNode(new NodeBasic(2));
	      g.addNode(new NodeBasic(3));
	      
	      g.addLink(new LinkWithCapacity(0,0,1,3));
	      g.addLink(new LinkWithCapacity(1,0,2,1));
	      g.addLink(new LinkWithCapacity(2,1,3,1));
	      g.addLink(new LinkWithCapacity(3,2,3,1));
	      g.addLink(new LinkWithCapacity(4,1,2,2));
	      g.addLink(new LinkWithCapacity(5,1,0,1));
	      g.addLink(new LinkWithCapacity(6,2,0,1));
	      g.addLink(new LinkWithCapacity(7,3,1,1));
	      g.addLink(new LinkWithCapacity(8,3,2,1));
	      g.addLink(new LinkWithCapacity(9,2,1,2));
	      g.setSource(0);
	      g.setTarget(3);
	      
	      
	      System.out.println(g.toString());
	      MaxFlowEdmondsKarp EK=new MaxFlowEdmondsKarp(g);
	      //System.out.println(g.residual().toString());
	      System.out.println(EK.EdmondsKarp());
	      EK.DecreaseLinkCapacity(0,1);
	      System.out.println(EK.maxFlowValue);
	   }
}
