package umontreal.ssj.networks;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.io.*;

import java.util.LinkedList;
import java.util.Arrays;

import umontreal.ssj.util.PrintfFormat;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.probdist.*;
import umontreal.ssj.randvar.*;
import umontreal.ssj.randvar.RandomVariateGen;
import umontreal.ssj.util.PrintfFormat;


public class GraphWithCapacity extends GraphOriented<NodeBasic,LinkWithCapacity> {
	
	int source;
	int target;
	
	public GraphWithCapacity() {
		this.numLinks=0;
		this.numNodes=0;
		this.links=new ArrayList<LinkWithCapacity>();
		this.nodes=new ArrayList<NodeBasic>();
		this.source=-1;
		this.target=-1;
	}
	
	public GraphWithCapacity(ArrayList<NodeBasic> nodes, ArrayList<LinkWithCapacity> links) {
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
	
	   @Override
	   public GraphWithCapacity clone() {
	      GraphWithCapacity image = new GraphWithCapacity();
	      //image = (GraphOrientedBasic) super.clone();

	      image.numLinks = numLinks;
	      image.numNodes = numNodes;


	      // Link
	      image.links = new ArrayList<LinkWithCapacity>();
	      for (int i = 0; i < numLinks; i++) {
	         image.links.add(new LinkWithCapacity(links.get(i).getIndice(), links.get(i).getSource(),
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
	    * @param r
	    *           Capacity
	    */
	   public void setCapacity(int capacity) {
		   for (int i = 0; i < this.numLinks; i++)
			   links.get(i).setCapacity(capacity);
	   }

	   /**
	    * Sets the Capacity r for link i of the graph.
	    * 
	    * @param i
	    *           link
	    * @param r
	    *           Capacity
	    */
	   public void setCapacity(int i, int capacity) {
		   links.get(i).setCapacity(capacity);
	   }

	   /**
	    * Sets the Capacities for all links of the graph. Capacity must have the same
	    * number of elements as the number of links.
	    * 
	    * @param R
	    *           Capacities
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
	    public GraphWithCapacity(String file) throws IOException {
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

		      links = new ArrayList<LinkWithCapacity>();
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
		         this.addLink(new LinkWithCapacity(i, a, b,capacity));
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
      public GraphWithCapacity residual() {
   	      GraphWithCapacity image = null;
   	      
   	      image = (GraphWithCapacity) super.clone();

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
   	      image.links = new ArrayList<LinkWithCapacity>();
   	      for (int i = 0; i < numLinks; i++) {
   	         image.addLink(new LinkWithCapacity(links.get(i).getIndice(), links.get(i).getSource(),
   	               links.get(i).getTarget(),links.get(i).getCapacity()));
   	      }
   	      
   	      /*counter to create the new links*/
   	      int counterIndiceLink=this.numLinks;
   	      while(!Queue.isEmpty()) {
   		     /*we pop the first element of the queue*/
   		     int duplicate=Queue.poll();
   		     image.addLink(new LinkWithCapacity(counterIndiceLink, links.get(duplicate).getTarget(),
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

	
	   
	   @Override
	   public String toString() {
	      StringBuffer sb = new StringBuffer(
	            "================================================= Graph"
	                  + PrintfFormat.NEWLINE);
	      
	      for (int i = 0; i < numLinks; i++) {
	         LinkWithCapacity link = links.get(i);
	         sb.append("link  " + link.getIndice() + "  connects nodes  "
	               + link.getSource() + ", " + link.getTarget() + ", capacity  "
	               + link.getCapacity() + PrintfFormat.NEWLINE);
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
}
