package umontreal.ssj.networks.flow;

import java.io.IOException;
import java.util.Arrays;

public class TestSortIndex {

	public static void main(String[] args) throws IOException {
		Double[] countries = { 0.13, 4.5, 0.0,2.3 };
		ArrayIndexComparator comparator = new ArrayIndexComparator(countries);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		for (int i = 0; i < indexes.length; i++)
        {
			System.out.println(indexes[i]);
        }
		
	}
	
}
