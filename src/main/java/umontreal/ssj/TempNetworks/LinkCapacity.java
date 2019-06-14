package umontreal.ssj.TempNetworks;


public class LinkCapacity extends LinkBasic {

	// capacity: only used in flowProblem 
	private int capacity;
	
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
	public LinkCapacity(int indice, int source, int target)
	{
		super(indice,source,target);
		//this.indice = indice;
	    //this.source = source;
	    //this.target = target;
	    //this.r=r;
		//temporary initialisation 
		this.capacity=1;
	}
   
	/**
	 * Full Constructor
	 */
	public LinkCapacity(int indice, int source, int target, double r, int capacity)
	{
		super(indice,source,target);
		//this.indice = indice;
	    //this.source = source;
	    //this.target = target;
	    //this.r=r;
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
		image = (LinkCapacity) super.clone();
		image.setIndice(this.getIndice());
		image.setSource(this.getSource());
		image.setTarget(this.getTarget());
		image.capacity = this.capacity;
		return image;
   }

}
