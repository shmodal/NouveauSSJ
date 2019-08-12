package umontreal.ssj.networks.flow;

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
	    * Define graph given a .txt file
	    * 
	    * @param file
	    *           file name
	    * @throws java.io.IOException
	    */
	    public GraphFlow(String file) throws IOException {
		      // read in file
	    	  BufferedReader br = new BufferedReader(new FileReader(file));
		      String l;
		      l = br.readLine();
		      int index = l.indexOf('#'); 
		      // if first line contains a comment #..., remove it
		      if (index >= 0)
		    	  l = l.substring(0, index);
		    
		      String[] graphParam=l.split("[\\t ]");
		      int numNodes = Integer.parseInt(graphParam[0]);
		      int numLinks = Integer.parseInt(graphParam[1]);
		      int source = Integer.parseInt(graphParam[2]);
		      int target = Integer.parseInt(graphParam[3]);
		      
		      this.source=source;
		      this.target=target;

		      links = new ArrayList<LinkFlow>();
		      nodes = new ArrayList<NodeBasic>();
		      
		      for (int i = 0; i < numNodes; i++) {
			         this.addNode(new NodeBasic(i));
		      }
		      
			  l = br.readLine();
			  int cntLink=0;
			  while(l!=null){ 
				 int a, b;
				 int capacity;
				 String[] linkParam=l.split("[\t ]+");
				 a = Integer.parseInt(linkParam[0]);
				 b = Integer.parseInt(linkParam[1]);
				 nodes.get(a).incCounter();
				 capacity = Integer.parseInt(linkParam[2]);
		        
				 String[] capL=br.readLine().split("[\t ]+");
				 String[] probL=br.readLine().split("[\t ]+");
				 if (capL.length!=probL.length) {
					 System.out.println("The graph cannot be built as capacities values has not the same length "
		        			+ "as the probabilities values.");
				 }
				 else {
					 int[] capacityVal=new int[capL.length];
					 int cntCap=0;
					 for(String str:capL){
						 capacityVal[cntCap]=Integer.parseInt(str.trim());
						 cntCap++;
					 }
					 double[] probabilityVal=new double[probL.length];
					 int cntProb=0;
					 double sumProb=0; 
					 for(String str:probL){
						 probabilityVal[cntProb]=Double.parseDouble(str.trim());
						 sumProb+=probabilityVal[cntProb];
						 cntProb++;
					 }
					 if (Math.abs(1-sumProb)>1e-6) {
						 System.out.println("The graph cannot be built as the probability values are not summing to one."+ sumProb);
					 }
					 else {
						 this.addLink(new LinkFlow(cntLink, a, b,capacity,capacityVal,probabilityVal));
					 }
				 }
		        
				 l=br.readLine();
				 cntLink++;
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
	    * Creates a graph define by a rectangular lattice
	    * graph. The x-sides of the graph have n nodes, and the y-sides have m
	    * nodes. The graph has edges between each adjacent nodes of capacity capacite. The graph contains
	    * n*m nodes and n(m-1) + m(n-1) edges. Here is an example with n = 3 and m =
	    * 4:<br />
	    *
	    * <pre>
	    *    o---o---o
	    *    |   |   |
	    *    o---o---o
	    *    |   |   |
	    *    o---o---o
	    *    |   |   |
	    *    o---o---o
	    * </pre>
	    *
	    * @param filename
	    *           output file name
	    * @param n
	    *           number of nodes on x-sides
	    * @param m
	    *           number of nodes on y-sides
	    * @param capacite
	    * 			max capacity for every edge
	    */
	    GraphFlow(String filename,int n, int m, int capacite) throws IOException{
		      int numNodes = n * m;
		      int numLinks = n * (m - 1) + m * (n - 1);
		      Writer output = null;
		      int target = numNodes - 1; // target of graph
		      double unifProb= 1.0/(capacite+1);
		      File file = new File(filename);
		      output = new BufferedWriter(new FileWriter(file));
		      output.write(numNodes + "\t" + numLinks + "\t" + 0 + "\t" +target + "\n");
		      
		      for (int i = 0; i < m - 1; i++) {
		         for (int j = 0; j < n - 1; j++) {
		            int k = i * n + j;
		            int targetleft = k + 1;
		            int targetdown = k + n;
		           
		            output.write(k + "\t" + targetleft + "\t" + capacite + "\n");
		            for (int cap=0;cap<capacite;cap++) {
		            	output.write(cap + "\t");
		            }
		            output.write(capacite + "\n");
		            for (int cap=0;cap<capacite;cap++) {
		            	output.write(unifProb + "\t");
		            }
		            output.write(unifProb + "\n");
		            
		            output.write(k + "\t" + targetdown + "\t" + capacite + "\n");
		            for (int cap=0;cap<capacite;cap++) {
		            	output.write(cap + "\t");
		            }
		            output.write(capacite + "\n");
		            for (int cap=0;cap<capacite;cap++) {
		            	output.write(unifProb + "\t");
		            }
		            output.write(unifProb + "\n");
		         }
		      }

		      for (int i = 0; i < m - 1; i++) {
		         // node1 and node2 of edge
		         int s = (i + 1) * n - 1;
		         int t = s + n;
		         output.write(s + "\t" + t + "\t" + capacite + "\n");
		         for (int cap=0;cap<capacite;cap++) {
		         	output.write(cap + "\t");
		         }
		         output.write(capacite + "\n");
		         for (int cap=0;cap<capacite;cap++) {
		         	output.write(unifProb + "\t");
		         }
		         output.write(unifProb + "\n");
		      }

		      for (int j = 0; j < n - 1; j++) {
		         int s = (m - 1) * n + j;
		         int t = s + 1;
		         output.write(s + "\t" + t + "\t" + capacite + "\n");
		         for (int cap=0;cap<capacite;cap++) {
		         	output.write(cap + "\t");
		         }
		         output.write(capacite + "\n");
		         for (int cap=0;cap<capacite;cap++) {
		         	output.write(unifProb + "\t");
		         }
		         output.write(unifProb + "\n");
		      }
		      output.close();
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
	        		 links.get(i).getTarget(),links.get(i).getCapacity(),links.get(i).getCapacityValues(),
	   	               links.get(i).getProbabilityValues()));
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
	    * Sets the Capacity r for link i of the graph.
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
	    * Sets the Capacities for all links of the graph. Capacity must have the same
	    * number of elements as the number of links.
	    * 
	    *           
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
   	               links.get(i).getTarget(),links.get(i).getCapacity(), new int[0],new double[0]));
   	      }
   	      
   	      /*counter to create the new links*/
   	      int counterIndiceLink=this.numLinks;
   	      while(!Queue.isEmpty()) {
   		     /*we pop the first element of the queue*/
   		     int duplicate=Queue.poll();
   		     image.addLink(new LinkFlow(counterIndiceLink, links.get(duplicate).getTarget(),
   		        		 links.get(duplicate).getSource(), 0, new int[0],new double[0]));
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
   
      
	 /**
	  * Multiply capacities probability values by eps, except the probability values of 
	  * last capacity (the maximum one) which is set to make the sum equal to 1.
	  * 
	  * @param eps
	  */
      public void MultiplyEdgeProbability(double eps) {
    	  
    	  double newProb;
    	  double sum;
    	  int numberSeuil;
    	  
    	  for (int i = 0; i < numLinks; i++) {
    		LinkFlow l=links.get(i);
    		numberSeuil=l.getProbabilityValues().length;
    		sum=0.0;
    		for(int seuil=0;seuil<numberSeuil-1;seuil++) {
    			newProb=l.getProbabilityValue(seuil)*eps;
    			l.setProbabilityValue(seuil, newProb);
    			sum+=newProb;
    		}
			newProb=1-sum;
			l.setProbabilityValue(numberSeuil-1, newProb);
    	  }
      }

      
 	 /**
 	  * Multiply capacities probability values by eps, except the probability values of 
 	  * last capacity (the maximum one) which is set to make the sum equal to 1.
 	  * 
 	  * @param eps
 	  */
       public void MultiplyEdgeProbability(double[] Eps) {
     	  
     	  double newProb;
     	  double sum;
     	  double eps;
     	  int numberSeuil;
     	  
     	  for (int i = 0; i < numLinks; i++) {
     		LinkFlow l=links.get(i);
     		numberSeuil=l.getProbabilityValues().length;
     		sum=0.0;
     		eps=Eps[i];
     		for(int seuil=0;seuil<numberSeuil-1;seuil++) {
     			newProb=l.getProbabilityValue(seuil)*eps;
     			l.setProbabilityValue(seuil, newProb);
     			sum+=newProb;
     		}
 			newProb=1-sum;
 			l.setProbabilityValue(numberSeuil-1, newProb);
     	  }
       }

	   
	   public void Undirect() {
			int numLinks = this.getNumLinks();
			//System.out.print("Nombre links" + numLinks);

			/*Storing the edges that are not present in both ways : source-->target and target-->source */
			LinkedList<Integer> Queue = new LinkedList<Integer>();
   	      	for (int i = 0; i < numLinks; i++) {
   	      		if(getLinkWithSourceAndSinkNodes(links.get(i).getTarget(),links.get(i).getSource())==-1) {
   	      			Queue.add(i);
   	      		}
   	      	}
	   	      
	  	    int counterIndiceLink=this.getNumLinks();
	 	    while(!Queue.isEmpty()) {
	 		     /*we pop the first element of the queue*/
	 		     int duplicate=Queue.poll();
	 		     LinkFlow original = this.getLink(duplicate);
	 		     this.addLink(new LinkFlow(counterIndiceLink, original.getTarget(),
	 		        		 original.getSource(), original.getCapacity()));
	 		     this.getLink(counterIndiceLink).setCapacityValues(original.getCapacityValues());
	 		     this.getLink(counterIndiceLink).setProbabilityValues(original.getProbabilityValues());
	 		     this.getLink(counterIndiceLink).setB(original.getB());
	 		     counterIndiceLink++;
	 	      }
	   }
       
       
       
       
       
       
       
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
	   
	   public void setJump(int i, int k, int value) {
		   links.get(i).setJump(k, value);
	   }
	   

	   // New
	   
	   public void setCapacityValues(int i, int[] tab) {
		   int [] copy = new int[tab.length];
		   System.arraycopy(tab, 0, copy, 0, tab.length);
		   links.get(i).setCapacityValues(copy);
	   }
	   
	   // Memes capacites, NEW
	   
	   public void setCapacityValues(int[] tab) {
		   for (int i = 0; i < numLinks; i++) {
			   links.get(i).setCapacityValues(tab);
			   }
	   }
	   
	   // NEW
	   
	   public void setB(int i, int k) {
		   links.get(i).setB(k);
	   }
	   
	   
	   // Meme B, NEW
	   
	   public void setB(int k) {
		   for (int i = 0; i < numLinks; i++) {
			   links.get(i).setB(k);
			   }
	   }
	   
	   
	   // New
	   
	   public void setProbabilityValues(int i, double[] tab) {
		   double [] copy = new double[tab.length];
		   System.arraycopy(tab, 0, copy, 0, tab.length);
		   links.get(i).setProbabilityValues(copy);
		   
		   //double [] lamb = new double[probabilityValues.length -1];
		   //System.arraycopy(probabilityValues, 0, lamb, 0, lamb.length);
		   
		   
		   
		   //links.get(i).setProbabilityValues(tab);
	   }
	   
	   // Memes capacites, NEW
	   
	   public void setProbabilityValues(double[] tab) {
		   for (int i = 0; i < numLinks; i++) {
			   links.get(i).setProbabilityValues(tab);
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
	   
	   

	   @Override
	   public String toString() {
	      StringBuffer sb = new StringBuffer(
	            "================================================= Graph"
	                  + PrintfFormat.NEWLINE);
	      
	      for (int i = 0; i < numLinks; i++) {
	         LinkFlow link = links.get(i);
	         sb.append("link  " + link.getIndice() + "  connects nodes  "
	               + link.getSource() + ", " + link.getTarget() + ", capacity  "
	               + link.getCapacity() + PrintfFormat.NEWLINE);
	         sb.append("Capacities: "+Arrays.toString(link.getCapacityValues())+ PrintfFormat.NEWLINE);
	         sb.append("Probabilities: "+Arrays.toString(link.getProbabilityValues())+ PrintfFormat.NEWLINE);
	      }
	      sb.append(PrintfFormat.NEWLINE + "----------------------------------"
	                + PrintfFormat.NEWLINE);
	      
	      for (int i = 0; i < numNodes; i++) {
	         NodeBasic node = nodes.get(i);
	         int r = node.getNodeLinks().size();
	         sb.append("node  " + node.getNumber());
	         sb.append("  has " + r + " links: ");
	         if(r>0) {
		         for (int j = 0; j < r - 1; j++) {
		            sb.append(node.getNodeLink(j) + ", ");
		         }
		         sb.append(node.getNodeLink(r - 1));
	         }
	         sb.append(PrintfFormat.NEWLINE);
	      }
	      
	      return sb.toString();
	   }
	   
	   
	    public void resetCapacities() {
	    	int m = this.getNumLinks();
	    	for (int i=0;i<m;i++) {
	    		this.getLink(i).setCapacity(this.getLink(i).getCapacityValue(0));
	    	}
	    }
}


	   
	

