package umontreal.ssj.networks.flow.old;
import java.util.*;
import java.lang.System;

public class Tools {

	/**
	 * Writes all elements of A in a string
	 * @param name array name
	 * @param A array elements
	 * @return the string
	 */
	public static String toString(String name, int[] A) {
		return toString(name, A, A.length);
	}

	/**
	 * Writes the first n elements of A in a string
	 * @param name array name
	 * @param A array elements
	 * @param n number of elements to print
	 * @return the string
	 */
	public static String toString(String name, int[] A, int n) {
      if (n > A.length)
			n = A.length;
      StringBuffer sb = new StringBuffer(name + " = [");
 		for (int i = 0; i < n; i++) {
	      Formatter form = new Formatter();
			form.format(" %d ", A[i]);
			sb.append(form.toString());
		}
	//	if (n < A.length)
	//		sb.append(" ... ");
		sb.append("]\n");
		sb.append(name + " size = " + n + "\n");
      return sb.toString();
	}


   /**
    * Writes the first n elements of A in a string
    * @param name array name
    * @param A array elements
    * @return the string
    */
   public static String toString(String name, boolean[] A) {
      int n = A.length;
      StringBuffer sb = new StringBuffer(name + " = [");
      for (int i = 0; i < n; i++) {
         Formatter form = new Formatter();
         form.format(" %b ", A[i]);
         sb.append(form.toString());
      }
      sb.append("]\n");
      sb.append(name + ".length = " + A.length + "\n");
      return sb.toString();
   }

	
	/**
	 * Writes all elements of A in a string
	 * @param name array name
	 * @param A array elements
	 * @return the string
	 */
	public static String toString(String name, double[] A) {
		return toString(name, A, A.length);
	}

	/**
	 * Writes the first n elements of A in a string
	 * @param name array name
	 * @param A array elements
	 * @param n number of elements to print
	 * @return the string
	 */
	public static String toString(String name, double[] A, int n) {
		if (n > A.length)
			n = A.length;
      StringBuffer sb = new StringBuffer(name + " = [");
		for (int i = 0; i < n; i++) {
	      Formatter form = new Formatter();
			form.format(" %g ", A[i]);
			sb.append(form.toString());
		}
		if (n < A.length)
			sb.append(" ... ");
		sb.append("]\n");
		sb.append(name + ".length = " + A.length + "\n");
      return sb.toString();
	}

   /**
    * Writes all inverse elements of A in a string.
    * If the elements are A[i], then writes 1/A[i].
    * @param name array name
    * @param A array elements
    * @return the string
    */
   public static String toStringInv(String name, double[] A) {
      return toStringInv(name, A, A.length);
   }
   
   /**
    * Writes the first n inverse elements of A in a string. 
    * If the elements are A[i], then writes 1/A[i].
    * @param name array name
    * @param A array elements
    * @param n number of elements to print
    * @return the string
    */
   public static String toStringInv(String name, double[] A, int n) {
      if (n > A.length)
         n = A.length;
      StringBuffer sb = new StringBuffer(name + " = [");
      for (int i = 0; i < n; i++) {
         Formatter form = new Formatter();
         form.format(" %g ", 1.0/A[i]);
         sb.append(form.toString());
      }
      if (n < A.length)
         sb.append(" ... ");
      sb.append("]\n");
      sb.append(name + ".length = " + A.length + "\n");
      return sb.toString();
   }

	/**
	 * Writes the first n differences between elements of A in a string.
	 * Writes the diff: <tt>D[i] = A[i+s] - A[i]</tt>.
	 * @param name array name
	 * @param A array elements
	 * @param n number of elements to print
	 * @param s step between elements
	 * @return the string
	 */
	public static String toStringDiff(String name, double[] A, int n, int s) {
		if (n > A.length - s)
			n = A.length - s;
      StringBuffer sb = new StringBuffer(name + " = [\n");
		for (int i = 0; i < n; i++) {
	      Formatter form = new Formatter();
			form.format(" %g ", A[i+s] - A[i]);
			sb.append(form.toString());
		}
		if (n < A.length - s)
			sb.append(" ... ");
		sb.append("]\n");
		sb.append(name + ".length = " + A.length + "\n");
      return sb.toString();
	}
		

	/**
	 * Writes the differences between elements of A in a string.
	 * Writes the diff: <tt>D[i] = A[i+s] - A[i]</tt>.
	 * @param name array name
	 * @param A array elements
	 * @param s step between elements
	 * @return the string
	 */
	public static String toStringDiff(String name, double[] A, int s) {
	   return toStringDiff(name, A, A.length - s, s);
	}
	
	/**
	 * Writes all elements of A in a string. When successive elements are
	 * the same, they are written in the form 
	 * <em> element &lt;n times&gt;</em>.
	 * @param name array name
	 * @param A array elements
	 * @return the string
	 */
	public static String toStringSame(String name, double[] A) {
		int n = A.length;
      StringBuffer sb = new StringBuffer(name + " = [");
      Formatter form = new Formatter();
	   form.format(" %g ", A[0]);
	   sb.append(form.toString());
	   
      int co = 1;
		for (int i = 1; i < n; i++) {
			if (A[i] == A[i-1]) {
				co++;
			} else {
            if (co > 1) {
	            form = new Formatter();
			      form.format("<%d times> ", co);
			      sb.append(form.toString());
            }
	         form = new Formatter();
			   form.format(" %g ", A[i]);
			   sb.append(form.toString());
			   co = 1;
		   }
		}
      if (co > 1) {
	      form = new Formatter();
		   form.format("<%d times> ", co);
		   sb.append(form.toString());
      }
         
		sb.append("]\n");
		sb.append(name + ".length = " + A.length + "\n");
      return sb.toString();
	}
		
	/**
	 * Writes all non-zero elements of A in a string
	 * @param name array name
	 * @param A array elements
	 * @return the string
	 */
	public static String toStringNonZero(String name, int[] A) {
      StringBuffer sb = new StringBuffer("\n");
		int co = 0;
		for (int i = 0; i < A.length; i++) {
			if (A[i] != 0) {
				co += A[i];
				sb.append(name);
		      Formatter form = new Formatter();
				form.format(" [%d] = %d%n", i, A[i]);
				sb.append(form.toString());
			}
		}
		sb.append("\n" + name + ".length = " + A.length + "\n");
		sb.append("Sum_i " + name + "[i] = " + co + "\n\n");
      return sb.toString();
	}
	
	
	/**
	 * Sums all the elements of array A
	 * @param A array
	 * @return the sum
	 */
	public static long sum (int[] A) {
		long co = 0;
		for (int i = 0; i < A.length; i++)
		   co += A[i];
		return co;
	}
	
	
	/**
	 * Sums all the elements of array A
	 * @param A array
	 * @return the sum
	 */
	public static long sum (long[] A) {
		long co = 0;
		for (int i = 0; i < A.length; i++)
		   co += A[i];
		return co;
	}
	
		
	
	/**
	 * Sums all the elements of array A
	 * @param A array
	 * @return the sum
	 */
	public static double sum (double[] A) {
		return sum(A, A.length);
	}
	
		
	
	/**
	 * Sums the first n elements of array A
	 * @param A array
	 * @param n number of elements to sum
	 * @return the sum
	 */
	public static double sum (double[] A, int n) {
		double co = 0;
		for (int i = 0; i < n; i++)
		   co += A[i];
		return co;
	}
		
	
	/**
	 * Finds the minimum and the maximum of the elements in A.
	 * @param A array
	 * @return [min, max]
	 */
	public static double[] minmax (double[] A) {
		int n = A.length;
		double min = A[0];
		double max = A[0];
		for (int i = 1; i < n; i++) {
			double x = A[i];
			if (x < min)
				min = x;
			if (x > max)
				max = x;
		}
	   double[] res = new double[2];
	   res[0] = min;
	   res[1] = max;
		return res;
	}
		
	/**
	 * Returns the name of the host computer
	 * @return the name of the host computer
	 */
	public static String getHostName() {
		String host = System.getenv("HOST");
 		int j = host.indexOf('.');
 		String name;
 		if (j >= 0)
 		   name = host.substring(0, j);
 		else
 			name = host;
 		return name;
	}
}
