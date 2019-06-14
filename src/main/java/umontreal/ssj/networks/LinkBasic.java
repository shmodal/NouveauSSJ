package umontreal.ssj.networks;

public class LinkBasic extends Link {
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
	   public LinkBasic(int indice, int source, int target, double r)
	   {
	      this.indice = indice;
	      this.source = source;
	      this.target = target;
	      this.r = r;
	   }

}
