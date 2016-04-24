package pokerBot;

import java.util.ArrayList;
import java.util.Arrays;

public class Utilities {
	
	/**
	 * returns a list of all subsets of size k of the list of cards. may want to change to Object rather than Card for better abstraction
	 * @param list: a list of Cards
	 * @param k: an integer k
	 * @return
	 */
	public static ArrayList<ArrayList<Card>> subset(ArrayList<Card> list, int k){
		//base cases
		if ( k==0 || list.size() < k )
			return new ArrayList<ArrayList<Card>>(0);
		
		if( k==1 ){
			//return subsets of each element in list as individuals
			ArrayList<ArrayList<Card>> ans = new ArrayList<ArrayList<Card>>(list.size());
			for(Card card : list){
				ArrayList<Card> tmp = new ArrayList<Card>(1);
				tmp.add(card);
				ans.add(tmp);
			}
			return ans;
		}

		ArrayList<Card> tmp = new ArrayList<Card>(list);
		tmp.remove(0);
		
		//all cases where the first element is part of the subset
		ArrayList<ArrayList<Card>> tmp1 = subset( tmp , k-1 );
		//append first element to all results of the recursive call
		for(ArrayList<Card> set : tmp1){
			set.add(0,list.get(0));
		}
		
		//all cases where the first element is not part of the subset
		ArrayList<ArrayList<Card>> tmp2 = subset( tmp , k );
				
		//append both lists and return
		tmp1.addAll(tmp2);
		return tmp1;
	}
	
	/**
	 * checks if an element is contained in an array. linear time lookup. should avoid excessive use.
	 * @param haystack
	 * @param needle
	 * @return
	 */
	public static <T> boolean contains(T[] haystack, T needle){
		if(haystack == null)
			return false;
		if(needle == null){
			for(T e: haystack){
				if(e == null){
					return true;
				}
			}
		}else{
			for(T e: haystack){
				if(e == needle || e.equals(needle)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static <T extends Comparable> boolean sortedContains(T[] haystack, T needle){
		return ( Arrays.binarySearch(haystack, needle) < 0 );
	}
	
	public static <T> int[] getIndices(T[] a){
		if(a == null)
			return null;
		
		int[] toRet = new int[a.length];
		for(int i = 0; i < a.length; i++){
			toRet[i] = i;
		}
		return toRet;
	}
	
	public static <T> void printArray(T[] a){
		for(T e: a){
			System.out.print(e + ", ");
		}
		System.out.println();
	}

}
