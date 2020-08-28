package calculations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class IndependentCoalitionCalculation 
{
	public IndependentCoalitionCalculation()
	{
		
	}
	
	public static ArrayList<Integer> findIndependentCoalitions(int n)
	{
		int s = (int) (Math.pow(2, n) - 2);
		ArrayList<Boolean> checked = new ArrayList<Boolean>(Collections.nCopies(s + 1, false));
		ArrayList<Integer> ind_coal = new ArrayList<Integer>(Collections.nCopies(n-1,  0));
		// selected independent vectors in reduced row echelon form
		ArrayList<ArrayList<Double>> arref = new ArrayList<ArrayList<Double>>(Collections.nCopies(n, new ArrayList<Double>(Collections.nCopies(n, 0.0))));
		ArrayList<Boolean> pivots = new ArrayList<Boolean>(Collections.nCopies(n, true)); // missing pivot element vector
		
		// grand coalition is always in it
		arref.set(0, new ArrayList<Double>(Collections.nCopies(n, 1.0)));		
		checked.set(s,  true);
		pivots.set(0, false);
		ind_coal.set(0, s + 1);
		int count = 1; // number of selected coalitions
		int coalition = 0;
		ArrayList<Boolean> a = new ArrayList<Boolean>(Collections.nCopies(n, false)); // coalition represented in binary
		int coalSize = 0; // size of coalition
		
		Random r = new Random();
		while(count < n - 1)
		{
			coalition = r.nextInt(s) + 1; // does not include s
			if(!checked.get(coalition)) // checks if we have already looked at that coalition
			{
				Collections.fill(a,  false);
				coalSize = de2bi_card(coalition, a, n);
				if(binrank(arref, pivots, a, n)) // true if with the addition of the new coalition they remain independent
				{
					count++; // increase number of found coalitions
					if(coalSize < n/2) // if coalition is small, we switch it for the opposite coalition
					{
						for(int i = 0; i < n; i++)
						{
							a.set(i, !a.get(i));
						}
						ind_coal.set(count - 1, s-coalition+1); // TODO: was s-coalition-1, but that doesn't make sense
					}
					else
					{
						ind_coal.set(count - 1, coalition);
					}
					// update reduced row echelon form with the new coalition
					arref = updateReducedRowEchelonForm(arref, pivots, a, n, count);
				}
				checked.set(coalition, true); // update checked with coalition
				checked.set(s-coalition+1, true); // update checked with opposite, TODO: again, s-coalition-1 is incorrect?
			}
		}
		return ind_coal;
	}
	
	private static ArrayList<ArrayList<Double>> updateReducedRowEchelonForm(ArrayList<ArrayList<Double>> arref, ArrayList<Boolean> pivots, ArrayList<Boolean> a, int n, int count)
	{
		double prec = Math.pow(10, -10);
		
		ArrayList<ArrayList<Double>> rref = new ArrayList<ArrayList<Double>>(Collections.nCopies(count + 1, new ArrayList<Double>(Collections.nCopies(n, 0.0))));
		rref.set(0, arref.get(0)); // first row is always the grand coalition
		
		// copy arref to rref and add vector a
		for(int i = 1; i < count + 1; i++)
		{
			for(int j = 0; j < n; j++)
			{
				if(i < count)
				{
					if(arref.get(i).get(j) > prec || arref.get(i).get(j) < (-1)*prec)
					{
						rref.get(i).set(j, arref.get(i).get(j));
					}
					else
					{
						if(a.get(j))
						{
							rref.get(i).set(j, 1.0);
						}
					}
				}
			}
		}
		
		// eliminate all non-zero elements in the first column from the 2nd row with the help of the first row
		for(int i = 1; i < count+1; i++)
		{
			if(rref.get(i).get(0) > prec || rref.get(i).get(0) < (-1)*prec)
			{
				if(rref.get(i).get(0) < 1 + prec && rref.get(i).get(0) > 1 - prec)
				{
					rref.set(i, vectorSubtract(rref.get(0), rref.get(i)));
				}
				else
				{
					rref.set(i, rowEchForm_piv2(rref, i, n));
				}
			}
		}
		
		int l = 1;
		int j = 1;
		while(l < count + 1 && j < n)
		{
			ArrayList<Integer> nonzero = new ArrayList<Integer>();
			ArrayList<Integer> ones = new ArrayList<Integer>();
			
			// find the non zero elements of column j.
			for(int k = l; k < count + 1; k++)
			{
				if(rref.get(k).get(j) > prec || rref.get(k).get(j) < (-1)*prec)
				{
					if(rref.get(k).get(j) < 1 + prec && rref.get(k).get(j) > 1 - prec)
					{
						ones.add(k);
					}
					else
					{
						nonzero.add(k);
					}
				}
			}
			if(ones.size() == 0 && nonzero.size() == 0)
			{
				j++; // if all elements are zero than we can move on to the next column
			}
			else
			{
				if(ones.size() == 0)
				{
					if(nonzero.get(0) != l)
					{
						// swap_ith_and_ first_nonzero, swap the l row with first non zero row
						ArrayList<Double> aux = rref.get(ones.get(0));
						rref.set(nonzero.get(0), rref.get(l));
						rref.set(l, aux);
						nonzero.set(0, l);
					}
					rref.set(l, sc_vectorProd(1/rref.get(l).get(j), rref.get(l)));
				}
				else
				{
					if(ones.get(0) != l)
					{
						ArrayList<Double>aux = rref.get(ones.get(0));
						rref.set(ones.get(0), rref.get(l));
						rref.set(l, aux);
						if(nonzero.size() > 0)
						{
							if(nonzero.get(0) == l)
							{
								nonzero.set(0, ones.get(0));
							}
						}
						ones.set(0, l);
					}
				}
				
				if(ones.size() > 0)
				{
					if(ones.get(0) == l)
					{
						for(int k = 1; k < ones.size(); k++)
						{
							rref.set(ones.get(k), vectorSubtract(rref.get(ones.get(k)), rref.get(l)));
						}
					}
					else
					{
						for(int k = 0; k < ones.size(); k++)
						{
							rref.set(ones.get(k), vectorSubtract(rref.get(ones.get(k)), rref.get(l)));
						}
					}
				}
				
				if(nonzero.size() > 0)
				{
					if(nonzero.get(0) == l)
					{
						for(int k = 1; k < nonzero.size(); k++)
						{
							rref.set(nonzero.get(k), rowEchForm_piv(rref, nonzero, l, j, k, n));
						}
					}
					else
					{
						for(int k = 0; k < nonzero.size(); k++)
						{
							rref.set(nonzero.get(k), rowEchForm_piv(rref, nonzero, l, j, k, n));
						}
					}
				}
				
				// finished with row l and column j 
				l++;
				pivots.set(j, false); // TODO: do we change the pivots array??
				j++;
			}			
		}
		
		// copy rref back to arref
		if(count + 1 < n)
		{
			for(int i = 1; i < count + 1; i++)
			{
				arref.set(i,  rref.get(i));
			}
		}
		else
		{
			for(int i = 1; i < n; i++)
			{
				arref.set(i,  rref.get(i));
			}
		}
		return arref;
	}
	
	private static ArrayList<Double> rowEchForm_piv(ArrayList<ArrayList<Double>> rref, ArrayList<Integer> nonzero, int i, int j, int k, int n)
	{
		ArrayList<Double> output = new ArrayList<Double>();
		ArrayList<Double> aux = new ArrayList<Double>(Collections.nCopies(n, 0.0));
		aux = sc_vectorProd(rref.get(nonzero.get(k)).get(j), rref.get(i));
		output = vectorSubtract(rref.get(nonzero.get(k)), aux);
		return output;
	}
	
	private static ArrayList<Double> rowEchForm_piv2(ArrayList<ArrayList<Double>> rref, int i, int n)
	{
		ArrayList<Double> output = new ArrayList<Double>();
		ArrayList<Double> aux = new ArrayList<Double>(Collections.nCopies(n, 0.0));
		aux = sc_vectorProd(rref.get(i).get(0), rref.get(0));
		output = vectorSubtract(aux, rref.get(i));
		return output;
	}
	
	private static int de2bi_card(int coalition, ArrayList<Boolean> a, int n)
	{
		int coalSize = 0;
		int x = 2;
		for(int c = 0; c < n-2; c++)
		{
			x += x;
		}
		int j = coalition;
		int l = n - 1;
		while(j > 0)
		{
			if(j >= x)
			{
				a.set(l, true);
				coalSize = coalSize + 1;
				j -= x;
			}
			x /= 2;
			l--;
		}
		return coalSize;
	}
	
	private static boolean binrank(ArrayList<ArrayList<Double>> arref, ArrayList<Boolean> pivots, ArrayList<Boolean> a, int n)
	{
		double prec = Math.pow(10, -10);
		
		// the binary vector as doubles (1 and 0)
		ArrayList<Double> aAsDoubles = new ArrayList<Double>(Collections.nCopies(n, 0.0));
		for(int i = 0; i < n; i++) 
		{
			if(a.get(i))
			{
				aAsDoubles.set(i, 1.0);
			}
			else
			{
				aAsDoubles.set(i, 0.0);
			}
		}
		
		// find the fist zero row of arref, store it in m
		int m = 0;
		boolean size = true;
		while(size)
		{
			if(nonZeroVector(arref.get(m), prec))
			{
				m = m + 1;
			}
			else
			{
				size = false;
			}
		}
		// if m is greater than n then the matrix is by default good?
		if(m >= n)
		{
			return false;
		}
		else
		{
			ArrayList<Boolean> pivot_col = new ArrayList<Boolean>(Collections.nCopies(n, false)); // columns with pivot elements (where pivots is false)
			for(int i = 0; i < n; i++)
			{
				if(!pivots.get(i))
				{
					pivot_col.set(i, true);
				}
			}
			int j = 0;
			ArrayList<Boolean> nonZero = new ArrayList<Boolean>(Collections.nCopies(n, false)); // non zero elements in aAsDoubles
			ArrayList<Double> aux = new ArrayList<Double>(Collections.nCopies(n, 0.0));
			int k = 0;
			int I = 0;
			int ind = 0;
			int count = 0;
			while(j < n)
			{
				for(int i = 0; i < n; i++)
				{
					if(aAsDoubles.get(i) > prec || aAsDoubles.get(i) < (-1)*prec)
					{
						nonZero.set(i, true);
					}
				}
				int s = sumOfVector(nonZero);
				if(s == 0)
				{
					return false; // if all elements are zero in aAsDoubles
				}
				else
				{
					// find the first non-zero element in aAsDoubles, store its position in k
					while(k == 0)
					{
						if(nonZero.get(I))
						{
							k = I + 1;
						}
						I++;
					}
					k--;
					I = 0;
					if(pivots.get(k)) // if there was no pivot element there before than it returns true
					{
						return true;
					}
					else // otherwise there is a pivot element, with which we can eliminate the last non-zero element of aAsDoubles
					{
						// find the corresponding pivot element, store its position in ind
						while(count < k + 1)
						{
							if(pivot_col.get(count))
							{
								ind++;
							}
							count++;
						}
						ind--;
						count = 0;
						// eliminate first non zero element of aAsDoubles
						aux = sc_vectorProd(aAsDoubles.get(k)/arref.get(ind).get(k), arref.get(ind));
						aAsDoubles = vectorSubtract(aAsDoubles, aux);
						j++;
					}
				}
				// reset
				for(int i = 0; i < n; i ++)
				{
					nonZero.set(i, false);
				}
				k = 0;
				ind = 0;
			}
			// iterated through all, no pivot elements, returns false
			return false;
		}
	}
	
	private static boolean nonZeroVector(ArrayList<Double> input, double prec)
	{
		for(int i = 0; i < input.size(); i++)
		{
			if(input.get(i) > prec || input.get(i) < (-1)*prec)
			{
				return true;
			}
		}
		return false;
	}
	
	private static int sumOfVector(ArrayList<Boolean> vec)
	{
		int s = 0;
		for(int i = 0; i < vec.size(); i++) 
		{
			if(vec.get(i))
			{
				s = s + 1; // adds one if it is true, none (0) if false
			}
		}
		return s;
	}
	
	private static ArrayList<Double> sc_vectorProd(double a, ArrayList<Double> input)
	{
		ArrayList<Double> output = new ArrayList<Double>();
		for(int i = 0; i < input.size(); i++)
		{
			output.add(a*input.get(i));
		}
		return output;
	}
	
	private static ArrayList<Double> vectorSubtract(ArrayList<Double> vec1, ArrayList<Double> vec2)
	{
		ArrayList<Double> output = new ArrayList<Double>();
		for(int i = 0; i != vec1.size(); i++)
		{
			output.add(vec1.get(i) - vec2.get(i));
		}
		return output;
	}
}
