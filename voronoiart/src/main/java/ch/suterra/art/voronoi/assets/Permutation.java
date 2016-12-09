package ch.suterra.art.voronoi.assets;

import java.util.*;

/**
 * Created by yannick on 09.12.16.
 */
public class Permutation {
	public static List<Set<Integer>> createPermutations(int toId) {
		Set<Integer> s = new HashSet<Integer>();
		for (int id=0;id<toId;id++) {
			s.add(id);
		}
		return calcPermutations(s);
	}

	private static List<Set<Integer>> calcPermutations(Set<Integer> myset) {
		int n = myset.size();
		Integer[] myInts = new Integer[n];
		Iterator<Integer> iterator = myset.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			myInts[index] = iterator.next();
			++index;
		}

		List<Set<Integer>> myList = new ArrayList<Set<Integer>>();
		Set<Integer> subSet;
		for (int i = 0; i < n; i++) {
			for (int j = i; j < n; j++) {
				if (j != i) {
					subSet = new HashSet<Integer>();
					subSet.add(myInts[i]);
					subSet.add(myInts[j]);
					myList.add(subSet);
				}
			}
		}
		return myList;
	}
}
