package umontreal.ssj.networks.flow;

import umontreal.ssj.networks.GraphOriented;
import umontreal.ssj.networks.LinkWithCapacity;
import umontreal.ssj.networks.NodeBasic;

public class GraphWithCapacity extends GraphOriented<NodeBasic,LinkFlow> {
	
	
	
	 
	   public void initLinkLambda(int i) {
		   links[i].initLambda();
	   }
	   public void initJumpAndIndexes(int i) {
		   links[i].initJumpAndIndexes();
	   }
	   
	   public void setLambdaValues(double [] tab,int i) {
		   links[i].setLambdaValues(tab);
	   }
	   
	   public double[] getLambdaValues(int i) {
		   return links[i].getLambdaValues();
	   }
	   
	   public void setValuesY(double[] tab, int i) {
		   links[i].setValuesY(tab);
	   }
	   
	   public double[] getValuesY(int i) {
		   return links[i].getValuesY();
	   }
	   
	   public void setJump(int i, int k, int value) {
		   links[i].setJump(k, value);
	   }

}
