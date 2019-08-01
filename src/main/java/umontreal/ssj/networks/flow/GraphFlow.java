package umontreal.ssj.networks.flow;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.io.*;

import java.util.LinkedList;
import java.util.Arrays;

import umontreal.ssj.util.PrintfFormat;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.networks.GraphOriented;
import umontreal.ssj.networks.LinkReliability;
import umontreal.ssj.networks.LinkWithCapacity;
import umontreal.ssj.networks.NodeBasic;
import umontreal.ssj.probdist.*;
import umontreal.ssj.randvar.*;
import umontreal.ssj.randvar.RandomVariateGen;
import umontreal.ssj.util.PrintfFormat;


public class GraphFlow extends GraphOriented<NodeBasic,LinkFlow> {
	
	
    /**
     * Source of flow
     */
	int source; 
    /**
     * Target of flow
     */
	int target;
	
    /**
     * Name of file containing the parameters of this graph
     */
    String filename = "";
	

	
	public GraphFlow() {
		this.numLinks=0;
		this.numNodes=0;
		this.links=new ArrayList<LinkFlow>();
		this.nodes=new ArrayList<NodeBasic>();
		this.source=-1;
		this.target=-1;
	}
	
	public GraphFlow(ArrayList<NodeBasic> nodes, ArrayList<LinkFlow> links) {
		this.numLinks=nodes.size();
		this.numNodes=links.size();
		this.links=links;
		this.nodes=nodes;
		this.source=-1;
		this.target=-1;
	}
	
	   /**
	    * Get the source of the graph
	    * 
	    * @param s
	    */
	   public int getSource() {
		   return this.source;
	   }

	   /**
	    * Get the target of the graph
	    * 
	    * @param t
	    */
	   public int getTarget() {
		  return this.target;
	   }
	   
	   /**
	    * Basic Clone, it doesn't copy capacity Values and probabilityValues of LinkFlow
	    * 
	    */
	   @Override
	   public GraphFlow clone() {
	      GraphFlow image = new GraphFlow();
	      //image = (GraphOrientedBasic) super.clone();

	      image.numLinks = numLinks;
	      image.numNodes = numNodes;


	      // Link
	      image.links = new ArrayList<LinkFlow>();
	      for (int i = 0; i < numLinks; i++) {
	         image.links.add(new LinkFlow(links.get(i).getIndice(), links.get(i).getSource(),
	        		 links.get(i).getTarget(),links.get(i).getCapacity()));
	      }

	      // nodes
	      image.nodes = new ArrayList<NodeBasic>();
	      for (int i = 0; i < numNodes; i++) {
	         if (nodes.get(i).getNodeLinks() != null) {
	            //int clonemylink[] = new int[nodes.get(i).getNodeLinks().size()];
	            int n = nodes.get(i).getNodeLinks().size();
	        	ArrayList<Integer> clonemylink = new ArrayList<Integer>();
	            for (int j = 0; j < n; j++) {
	               clonemylink.add(nodes.get(i).getNodeLink(j));
	               //= nodes.get(i).getNodeLink(j);
	            }
	            image.nodes.add( new NodeBasic(0, nodes.get(i).getNumber(), clonemylink));
	         } else {
	            image.nodes.add(new NodeBasic());
	         }
	      }

	      return image;
	   }
	
	   /**
	    * Sets the same Capacity for all links of the graph.
	    * 
	    * @param capacity
	    *           
	    */
	   public void setCapacity(int capacity) {
		   for (int i = 0; i < this.numLinks; i++)
			   links.get(i).setCapacity(capacity);
	   }

	   /**
	    * Sets the Capacity capacity for link i of the graph.
	    * 
	    * @param i link
	    *           
	    * @param capacity
	    *           
	    */
	   public void setCapacity(int i, int capacity) {
		   links.get(i).setCapacity(capacity);
		   //System.out.println("Maj cap de " +i +"  devient : " +links.get(i).getCapacity());
		   //System.out.println(links.get(i).getCapacity());   
	   }

	   /**
	    * Sets the Capacities for all links of the graph, with an array. 
	    * Capacities must have the same
	    * number of elements as the number of links.
	    * 
	    *   @param Capacities Array  
	    */
	   public void setCapacity(int[] Capacities) {
		   for (int i = 0; i < numLinks; i++)
			   links.get(i).setCapacity(Capacities[i]);
	   }

	   /**
	    * Set the source of the graph
	    * 
	    * @param s
	    */
	   public void setSource(int s) {
		   this.source=s;
	   }

	   /**
	    * Set the target of the graph
	    * 
	    * @param t
	    */
	   public void setTarget(int t) {
		   this.target=t;
	   }
	
	   
	   
	   /**
	    * Define graph given a .txt file
	    * 
	    * @param file
	    *           file name
	    * @throws java.io.IOException
	    */
	    public GraphFlow(String file) throws IOException {
		      // read in file
		      BufferedReader br = new BufferedReader(new FileReader(file));
		      String filename=splitFileName(file);
		      String f, l;
		      String[] ss;
		      f = "";
		      l = br.readLine();
		      int index = l.indexOf('#'); 
		      // if first line contains a comment #..., remove it
		      if (index >= 0)
		         l = l.substring(0, index);

		      do {
		         f += l + "\t";
		         l = br.readLine();
		      } while (l != null);

		      ss = f.split("[\t ]+");

		      numNodes = Integer.parseInt(ss[0]);
		      numLinks = Integer.parseInt(ss[1]);
		      source = Integer.parseInt(ss[2]);
		      target = Integer.parseInt(ss[3]);

		      links = new ArrayList<LinkFlow>();
		      nodes = new ArrayList<NodeBasic>();
		      for (int i = 0; i < numNodes; i++)
		         this.addNode(new NodeBasic(i));

		      int pos = 0;

		      for (int i = 0; i < numLinks; i++) {
		         int a, b;
		         int capacity;

		         pos = 3 * i + 4;

		         a = Integer.parseInt(ss[pos]);
		         b = Integer.parseInt(ss[pos + 1]);
		         nodes.get(a).incCounter();
		         capacity = Integer.parseInt(ss[pos + 2]);
		         this.addLink(new LinkFlow(i, a, b,capacity));
		      }

		      for (int i = 0; i < numNodes; i++) {
		         nodes.get(i).setNodeLinks(new ArrayList<Integer>());
		         // for the next step
		         nodes.get(i).setCounter(0);
		      }

		      for (int i = 0; i < numLinks; i++) {
		         int a = links.get(i).getSource();
		         int b = links.get(i).getTarget();
		         nodes.get(a).addNodeLink(links.get(i).getIndice());
		         nodes.get(a).incCounter();
		      }
	    }
	    
	   /**
	    * Generate the residual graph of the current grapWithCapacity.
	    */
      public GraphFlow residual() {
   	      GraphFlow image = null;
   	      
   	      image = (GraphFlow) super.clone();

   	      /*Storing the edges that are not present in both ways : source--»target and target--»source */
   	      LinkedList<Integer> Queue = new LinkedList<Integer>();
   	      for (int i = 0; i < numLinks; i++) {
   	    	  if(getLinkWithSourceAndSinkNodes(links.get(i).getTarget(),links.get(i).getSource())==-1) {
   			      Queue.add(i);
   		      }
   	      }
   	      
   	      image.numLinks = 0;
   	      image.numNodes = 0;

   	      // Link
   	      image.links = new ArrayList<LinkFlow>();
   	      for (int i = 0; i < numLinks; i++) {
   	         image.addLink(new LinkFlow(links.get(i).getIndice(), links.get(i).getSource(),
   	               links.get(i).getTarget(),links.get(i).getCapacity()));
   	      }
   	      
   	      /*counter to create the new links*/
   	      int counterIndiceLink=this.numLinks;
   	      while(!Queue.isEmpty()) {
   		     /*we pop the first element of the queue*/
   		     int duplicate=Queue.poll();
   		     image.addLink(new LinkFlow(counterIndiceLink, links.get(duplicate).getTarget(),
   		        		 links.get(duplicate).getSource(), 0));
   		     counterIndiceLink++;

   	      }
   	      
   	      // nodes
   	      /*change this part to add the edge in "connects nodes" (normally not important*/
   	      image.nodes = new ArrayList<NodeBasic>();
   	      for (int i = 0; i < numNodes; i++) {
   	         if (nodes.get(i).getNodeLinks() != null) {
   	            ArrayList<Integer> clonemylink = new ArrayList<Integer>();
   	            for (int j = 0; j < nodes.get(i).getNodeLinks().size(); j++) {
   	               clonemylink.add(nodes.get(i).getNodeLink(j));
   	            }
   	            image.addNode(new NodeBasic(0, nodes.get(i).getNumber(), clonemylink));
   	         } else {
   	            image.addNode(new NodeBasic());
   	         }
   	      }

   	      return image;
   	   }
   
	   
	   private String splitFileName(String file) {
	      int t = file.lastIndexOf('/');
	      int s = file.lastIndexOf('.');
	      if (s < 0)
	         return file.substring(1 + t);
	      else
	         return file.substring(1 + t, s);
	   }

	   /**
	    * For PMC Algorithm. Inits lambda{i,k} for the link i
	    *  @param i indice of link
	    */
	   
	   public void initLinkLambda(int i) {
		   //if (i<3) {
		//	   System.out.println("Init lambda ar�te " +i);
		//	   links.get(i).initLambda();
		//	   printTab(links.get(i).getLambdaValues());
		 //  }
		   //System.out.println("Init lambda ar�te " +i);
		   //else {
			   links.get(i).initLambda();
			   //}  
	   }
	   
	   /**
	    * For PMC Algorithm. Inits Array of the S{i,k} for the link i
	    *  @param i indice of link
	    */
	   
	   public void initJumpAndIndexes(int i) {
		   //System.out.println("Init jump ar�te " +i);
		   links.get(i).initJumpAndIndexes();
	   }
	   
	   public void setLambdaValues(double [] tab,int i) {
		   links.get(i).setLambdaValues(tab);
	   }
	   
	   public double[] getLambdaValues(int i) {
		   return links.get(i).getLambdaValues();
	   }
	   
	   public void setValuesY(double[] tab, int i) {
		   links.get(i).setValuesY(tab);
	   }
	   
	   public double[] getValuesY(int i) {
		   return links.get(i).getValuesY();
	   }
	   
	   /**
	    * For PMC Algorithm. Sets the jump number k of link i to value.
	    *  @param i indice of link
	    *  @param value must be 0 or 1
	    *  @param k jump
	    */
	   
	   public void setJump(int i, int k, int value) {
		   links.get(i).setJump(k, value);
	   }
	   

	   /**
	    * Sets the capacity values for the link i
	    *  @param i indice of link
	    *  @param tab Array of the possible capacities
	    */
	   
	   public void setCapacityValues(int i, int[] tab) {
		   int [] copy = new int[tab.length];
		   System.arraycopy(tab, 0, copy, 0, tab.length);
		   links.get(i).setCapacityValues(copy);
	   }
	   
	   /**
	    * Sets the same capacity values for each link
	    *  @param tab Array of the possible capacities
	    */
	   
	   public void setCapacityValues(int[] tab) {
		   for (int i = 0; i < numLinks; i++) {
			   int [] copy = new int[tab.length];
			   System.arraycopy(tab, 0, copy, 0, tab.length);
			   links.get(i).setCapacityValues(copy);
			   }
	   }
	   
	   /**
	    * Sets the integer b for the link i
	    *  @param i indice of link
	    *  @param k integer b
	    */
	   
	   public void setB(int i, int k) {
		   links.get(i).setB(k);
	   }
	   
	   
	   /**
	    * Sets the same b for each link
	    *  @param k Integer b
	    */
	   
	   public void setB(int k) {
		   for (int i = 0; i < numLinks; i++) {
			   links.get(i).setB(k);
			   }
	   }
	   
	   
	   /**
	    * Sets the capacity values for the link i
	    *  @param i indice of link
	    *  @param tab Array of the possible capacities
	    */
	   
	   public void setProbabilityValues(int i, double[] tab) {
		   double [] copy = new double[tab.length];
		   System.arraycopy(tab, 0, copy, 0, tab.length);
		   links.get(i).setProbabilityValues(copy);
		   
		   //double [] lamb = new double[probabilityValues.length -1];
		   //System.arraycopy(probabilityValues, 0, lamb, 0, lamb.length);
		   //links.get(i).setProbabilityValues(tab);
	   }
	   
	   /**
	    * Sets the same probability values for each link
	    *  @param tab Array of the possible probabilities
	    */
	   
	   public void setProbabilityValues(double[] tab) {
		   for (int i = 0; i < numLinks; i++) {
			   double [] copy = new double[tab.length];
			   System.arraycopy(tab, 0, copy, 0, tab.length);
			   links.get(i).setProbabilityValues(copy);
			   }
	   }
	   
	   
	   public int getB(int i) {
		   return links.get(i).getB();
	   }
	   
	   public int[] getCapacityValues(int i) {
		   return links.get(i).getCapacityValues();
	   }
	   
	   public double[] getProbabilityValues(int i) {
		   return links.get(i).getProbabilityValues();
	   }

	   private void printTab(double[] t) {
		   int m = t.length;
		   for (int i =0;i<m;i++) {
			   System.out.println(t[i]);
		   }
	   }
	   private void printTab(int[] t) {
		   int m = t.length;
		   for (int i =0;i<m;i++) {
			   System.out.println(t[i]);
		   }
	   }
	   
	   

	   
	   
}


	   
	

