package Project.IFS_System;
import java.util.Random;

/**
 * LoadedDice - The LoadedDice class is intended to allow a random 
 * selection of numbers between 1 and n but where the selection is 
 * biased in favour of some numbers rather than others.
 */
public class LoadedDice {
	private int sum[];
	Random generator = new Random();
	int[] probabilities;

	/**
	 * Constructor takes the list of relative probability weightings
	 * specified as integers in the input array and generates a continuous 
	 * sum of them in order that the intervals represent the likelihood of a 
	 * randomly generated integer falling between them. If the probability
	 * weight in prob[i] is 8 the difference between sum[i-1] and sum[i] will be 8. 
	 * @param prob an array containing the required probability weightings 
	 */
	public LoadedDice(int[] prob)
	{
		probabilities = prob;
		sum = new int[probabilities.length];		
		for(int i = 0; i < probabilities.length; i++)
		{
			if(i == 0)
			{
				sum[i] = probabilities[i];
			} else
			{
				sum[i] = sum[i-1] + probabilities[i];
			}
		}
	}
	
	/**
	 * returns a value determined by a random number  
	 * being compared with the continuous sum of the weighted
	 * probabilities. The value of i is returned if the random
	 * number falls in the interval between sum[i] and sum[i+1]. 
	 * Effectively a "roll" of a loaded dice
	 */
	public int roll()
	{
		int random = generator.nextInt(sum[sum.length - 1]) + 1;		
		for(int i = 0; i < probabilities.length; i++)
		{
			if(random <= sum[i])
				return i;
		}
		return -1;
	}
	
	/**
	 * Returns an array of int holding the 
	 * outcome of many weighted "rolls"
	 * @param size the number of required rolls
	 */
	public int[] rollMany(int size)
	{
		int[] many = new int[size];
		for(int i = 0; i < size; i++)
		{
			many[i] = roll();
		}
		return many;
	}

}
