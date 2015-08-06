package pokerBot;

import java.util.ArrayList;

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

}
