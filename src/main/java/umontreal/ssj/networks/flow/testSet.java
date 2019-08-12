package umontreal.ssj.networks.flow;

import umontreal.ssj.networks.NodeBasic;

public class testSet {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		GraphFlow g = new GraphFlow();
		g.addNode(new NodeBasic(0));
		g.addNode(new NodeBasic(1));
		g.addNode(new NodeBasic(2));
		g.addLink(new LinkFlow(0,0,1,0));
		g.addLink(new LinkFlow(1,2,1,0));
		int [] tab = new int[2]; tab[0]=3;tab[1]=2;
		g.setCapacityValues(tab);
		g.getLink(0).setCapacityValue(0, 7);
		System.out.println(g.toString());
	}

}
