package Project.IFS_System;
import java.util.Random;


public class LoadedDice {
	private int sum[];
	Random generator = new Random();
	int[] probabilities;

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
