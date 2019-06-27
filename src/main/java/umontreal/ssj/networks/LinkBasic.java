package umontreal.ssj.networks;

public class LinkBasic {
	   // son indice ou identificateur dans le tableau Links
		protected int indice;

	   // indice du noeud source
		protected int source;

	   // indice du noeud target
		protected int target;
	
	
	   /**
	    * 
	    * @param indice link id
	    * @param source first node of this link
	    * @param target second node of this link
	    */
	   public LinkBasic(int indice, int source, int target)
	   {
	      this.indice = indice;
	      this.source = source;
	      this.target = target;
	   }

	   /**
	    * (Previous)Full constructor
	    * 
	    * @param indice
	    *           define the indice of the link
	    * @param source
	    *           define the number of the source's node
	    * @param target
	    *           define the number of the target's node
	    * @param r
	    *           define the reliability of the link
	    */
//	   public LinkBasic(int indice, int source, int target, double r)
//	   {
//	      this.indice = indice;
//	      this.source = source;
//	      this.target = target;
//	      this.r = r;
//	   }
	   
	   
	   /**
	    * Set the link indice
	    * 
	    * @param indice
	    */
	   public void setIndice(int indice)
	   {
	      this.indice = indice;
	   }

	   /**
	    * Sets node with index id as link source.
	    * 
	    * @param id
	    */
	   public void setSource(int id)
	   {
	      this.source = id;
	   }
	   
	   
	   /**
	    * Sets node with index id as link target.
	    * 
	    * @param id
	    */
	   public void setTarget(int id)
	   {
	      this.target = id;
	   }
	   
	   /**
	    * Return the link number.
	    * 
	    * @return the link number.
	    */
	   public int getIndice()
	   {
	      return indice;
	   }
	   
	   /**
	    * Return the id of one of the connected node
	    * 
	    * @return the id of one of the connected node
	    */
	   public int getSource()
	   {
	      return source;
	   }

	   /**
	    * Return the id of the other node
	    * 
	    * @return the id of the other node
	    */
	   public int getTarget()
	   {
	      return target;
	   }
	   
	   
	   /**
	    * Clone a link
	    * 
	    * @return To obtain a copy of the link
	    */
	   @Override
	   public LinkBasic clone()
	   {
	      LinkBasic image = null;

	      try {
	         image = (LinkBasic) super.clone();
	      } catch (CloneNotSupportedException e) {
	         // No deberia suceder
	      }
	      image.indice = this.indice;
	      image.source = this.source;
	      image.target = this.target;
	      //image.param = this.param;
	      //image.r = this.r;
	      //image.m_sam = this.m_sam;
	      return image;
	   }
	   
	   

}
