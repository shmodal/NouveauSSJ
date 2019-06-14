package umontreal.ssj.networks;


public class LinkCapacity extends Link {

	// capacity: only used in flowProblem 
	private int capacity;
	
	/**
	 * 
	 * @param indice LinkWithCapacity id
	 * @param source first node of this LinkWithCapacity
	 * @param target second node of this LinkWithCapacity
	 */
	public LinkCapacity(int indice, int source, int target)
	{
		this.indice = indice;
	    this.source = source;
	    this.target = target;
		this.capacity=1;
	}

	/**
	 * (Previous)Partially Full constructor
	 * 
	 * @param indice
	 *           define the indice of the LinkWithCapacity
	 * @param source
	 *           define the number of the source's node
	 * @param target
	 *           define the number of the target's node
	 * @param r
	 *           define the reliability of the LinkWithCapacity
	 */
	public LinkCapacity(int indice, int source, int target, double r)
	{
		this.indice = indice;
	    this.source = source;
	    this.target = target;
	    this.r=r;
		//temporary initialisation 
		this.capacity=1;
	}
   
	/**
	 * Full Constructor
	 */
	public LinkCapacity(int indice, int source, int target, double r, int capacity)
	{
		this.indice = indice;
	    this.source = source;
	    this.target = target;
	    this.r=r;
		this.capacity=capacity;
	}


	/**
	* Set the capacity of this LinkWithCapacity
	* 
	* @param r
	*/
	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}
	
	/**
    * Return the capacity of the LinkWithCapacity.
    * 
    * @return To obtain the capacity of the LinkWithCapacity
    */
	public int getCapacity()
	{
		return capacity;
	}

   /**
    * Clone a LinkWithCapacity
    * 
    * @return To obtain a copy of the LinkWithCapacity
    */
	@Override
	public LinkCapacity clone()
	{
		LinkCapacity image = null;
		
		try {
			image = (LinkCapacity) super.clone();
		} catch (CloneNotSupportedException e) {
			// No deberia suceder
		}
		image.setIndice(this.getIndice());
		image.setSource(this.getSource());
		image.setTarget(this.getTarget());
		image.setR(this.getR());
		image.setParam(this.getParam());
		image.capacity = this.capacity;
		return image;
   }

}
