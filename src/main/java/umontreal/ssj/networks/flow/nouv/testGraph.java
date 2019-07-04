package umontreal.ssj.networks.flow.nouv;

import java.io.IOException;
import umontreal.ssj.networks.*;

public class testGraph {

	  public static void main(String[] args) throws IOException {
	      GraphFlow g=new GraphFlow();
	      
	      g.addNode(new NodeBasic(0));
	      g.addNode(new NodeBasic(1));
	      g.addNode(new NodeBasic(2));
	      g.addNode(new NodeBasic(3));
	      g.addNode(new NodeBasic(4));
	      g.addNode(new NodeBasic(5));
	      
	      g.addLink(new LinkFlow(0,0,1,10));
	      g.addLink(new LinkFlow(1,0,2,8));
	      g.addLink(new LinkFlow(2,1,3,8));
	      g.addLink(new LinkFlow(3,1,2,2));
	      g.addLink(new LinkFlow(4,2,4,7));
	      g.addLink(new LinkFlow(5,3,5,10));
	      g.addLink(new LinkFlow(6,4,5,10));
	      g.addLink(new LinkFlow(7,2,3,6));
	      
	      g.setSource(0);
	      g.setTarget(5);
	      
	      System.out.println(g.toString());
	      MaxFlowEdmondsKarp EK=new MaxFlowEdmondsKarp(g);
	      //EK.EdmondsKarp();
	      //System.out.println(EK.maxFlowValue);
	      //System.out.println(g.residual().toString());
	      System.out.println(EK.EdmondsKarp());
	   }
}
