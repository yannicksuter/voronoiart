package ch.suterra.art.voronoi.assets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yannick on 09.12.16.
 */
class Combination {
	public static List<Integer[]> createCombinations(int toId, int tupleSize) {
		Integer s[] = new Integer[toId];
		for (int i = 0; i < toId; i++) { s[i] = i; }
		return getCombination(s, tupleSize);
	}

	static public List<Integer[]> getCombination(Integer[] input, int k) {
		List<Integer[]> subsets = new ArrayList<Integer[]>();
		Integer[] s = new Integer[k];                  // here we'll keep indices
		if (k <= input.length) {
			for (int i = 0; (s[i] = i) < k - 1; i++) ;
			subsets.add(getSubset(input, s));
			for (; ; ) {
				int i;
				// find position of item that can be incremented
				for (i = k - 1; i >= 0 && s[i] == input.length - k + i; i--) ;
				if (i < 0) {
					break;
				} else {
					s[i]++;                    // increment this item
					for (++i; i < k; i++) {    // fill up remaining items
						s[i] = s[i - 1] + 1;
					}
					subsets.add(getSubset(input, s));
				}
			}
		}
		return subsets;
	}

	// generate actual subset by index sequence
	static private Integer[] getSubset(Integer[] input, Integer[] subset) {
		Integer[] result = new Integer[subset.length];
		for (int i = 0; i < subset.length; i++)
			result[i] = input[subset[i]];
		return result;
	}

	public static void main(String[] args) {
//		List<Integer[]> tuples = createPermutations(5, 2);
//		System.out.println(tuples.size());

		Integer[] input = {10, 20, 30, 40, 50};
		for (Integer[] l : getCombination(input, 2)) {
			System.out.println(Arrays.toString(l));
		}
		System.out.println(getCombination(input, 2).size());

//		Set<Integer> mySet = new HashSet<Integer>(Arrays.asList(input));
//		for (Set<Integer> l : Permutation.calcPermutations(mySet)) {
//			System.out.printf(Arrays.toString(l.toArray())+"\n");
//		}
//		System.out.println(Permutation.calcPermutations(mySet).size());
	}
}