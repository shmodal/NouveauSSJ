package umontreal.ssj.networks.flow;

import java.util.Comparator;

public class ArrayIndexComparator implements Comparator<Integer>{

    private final Double[] array;

    public ArrayIndexComparator(Double[] array)
    {
        this.array = array;
    }

    public Integer[] createIndexArray()
    {
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < array.length; i++)
        {
            indexes[i] = i; 
        }
        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2)
    {
        Double a = (Double) array[index1];
        Double b = (Double) array[index2];
        return a.compareTo(b);
    	//return array[index1].compareTo(array[index2]);
    }

}
