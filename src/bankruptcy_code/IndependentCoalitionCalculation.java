package bankruptcy_code;

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
		int s = (int) (Math.pow(2,  n) - 2);
		ArrayList<Boolean> checked = new ArrayList<Boolean>(Collections.nCopies(s + 1, false));
		ArrayList<Integer> ind_coal = new ArrayList<Integer>(Collections.nCopies(n-1, 0));
		ArrayList<ArrayList<Double>> arref = new ArrayList<ArrayList<Double>>
			(Collections.nCopies(n, new ArrayList<Double>(Collections.nCopies(n, 0.0))));

		ArrayList<Boolean> J = new ArrayList<Boolean>(Collections.nCopies(n, true));

		checked.set(s, true);
		J.set(0, false);
		ind_coal.set(0, s+1);
		int count = 1;
		int i = 0;
		ArrayList<Boolean> a = new ArrayList<Boolean>(Collections.nCopies(n, false));

		int card = 0;
		Random r = new Random();
		while(count < n - 1)
		{
			i = r.nextInt(s); // does not include s
			if(!checked.get(i))
			{
				Collections.fill(a,  false);
				card = de2bi_card(i, a, n);
				if(binrank(arref, J, a, n))
				{
					count++;
					if(card < n / 2)
					{
						for(int j = 0; j < n; j++)
						{
							a.set(j, !a.get(j));
						}
						ind_coal.set(count - 1, s - i - 1);
					}
					else
					{
						ind_coal.set(count - 1, i);
					}
					rowechform(arref, J, a, n, count);
				}
				checked.set(i, true);
				checked.set(s-i-1, true);
				
				if(isCheckedFull(checked)) // reset the whole thing if no good combinations were found
				{
					Collections.fill(checked, false);
					checked.set(s, true);
					Collections.fill(ind_coal, 0);
					Collections.fill(arref, new ArrayList<Double>(Collections.nCopies(n, 0.0)));
					Collections.fill(J, true);
					J.set(0, false);
					ind_coal.set(0, s+1);
					count = 1;
					i = 0;
					Collections.fill(a, false);
					card = 0;
				}
			}
		}
		return ind_coal;		
	}
	
	private static boolean isCheckedFull(ArrayList<Boolean> checked)
	{
		boolean full = true;
		for(Boolean b : checked)
		{
			if(!b)
			{
				full = false;
			}
		}
		return full;
	}
	
	private static void rowechform(ArrayList<ArrayList<Double>> arref, ArrayList<Boolean> J, ArrayList<Boolean> b, int n, int rank)
	{
		double prec = Math.pow(10, -10);
		ArrayList<ArrayList<Double>> rref = new ArrayList<ArrayList<Double>>
			(Collections.nCopies(rank + 1, new ArrayList<Double>(Collections.nCopies(n, 0.0))));

		rref.set(0, arref.get(0));
		
		for(int i = 1; i < rank + 1; i++)
		{
			for(int j = 0; j < n; j++)
			{
				if(i < rank)
				{
					if(arref.get(i).get(j) >prec || arref.get(i).get(j) < -prec)
					{
						rref.get(i).set(j, arref.get(i).get(j));
					}
				}
				else
				{
					if(b.get(j))
					{
						rref.get(i).set(j, 1.0);
					}
				}
			}
		}
		
		for(int i = 1; i < rank + 1; i++)
		{
			if(rref.get(i).get(0) > prec || rref.get(i).get(0) < -prec)
			{
				if(rref.get(i).get(0) < 1 + prec && rref.get(i).get(0) > 1 - prec)
				{
					vec_subtract(rref.get(i), rref.get(0), rref.get(i));
				}
				else
				{
					rowechform_piv2(rref, i, n);
				}
			}
		}
		
		int l = 1;
		int j = 1;
		while(l < rank + 1 && j < n)
		{
			int[] out = rowechform_loop(rref, J, l, j, rank, prec, n);
			l = out[0];
			j = out[1];
		}
		
		if(rank + 1 < n)
		{
			for(int x = 1; x < rank + 1; x++)
			{
				arref.set(x, rref.get(x));
			}
		}
		else
		{
			for(int x = 1; x < n; x++)
			{
				arref.set(x, rref.get(x));
			}
		}
	}
	
	private static int[] rowechform_loop(ArrayList<ArrayList<Double>> rref, ArrayList<Boolean> J, int l, int j, int rank, double prec, int n)
	{
		ArrayList<Integer> nonz = new ArrayList<Integer>(Collections.nCopies(0, 0));
		ArrayList<Integer> ones = new ArrayList<Integer>(Collections.nCopies(0, 0));
		
		for(int k = l; k < rank + 1; k++)
		{
			if(rref.get(k).get(j) > prec || rref.get(k).get(j) < -prec) {
				if(rref.get(k).get(j) < 1 + prec && rref.get(k).get(j) > 1 - prec)
				{
					ones.add(k);
				}
				else
				{
					nonz.add(k);
				}
			}
		}
		if(ones.size() == 0 && nonz.size() == 0)
		{
			j = j + 1;
		}
		else
		{
			if(ones.size() == 0)
			{
				if(nonz.get(0) != l)
				{
					swap_ith_and_firstnz(rref, nonz, l);
				}
				sc_vec_prod(rref.get(l), 1 / rref.get(l).get(j), rref.get(l));
			}
			else
			{
				if(ones.get(0) != l)
				{
					swap_ith_and_firstnz(rref, nonz, l);
				}
			}
			
			if(ones.size() > 0)
			{
				if(ones.get(0) == l)
				{
					for(int k = 1; k < ones.size(); k++)
					{
						vec_subtract(rref.get(ones.get(k)), rref.get(ones.get(k)), rref.get(l));
					}
				}
				else
				{
					for(int k = 0; k < ones.size(); k++)
					{
						vec_subtract(rref.get(ones.get(k)), rref.get(ones.get(k)), rref.get(l));
					}
				}
			}
			if(nonz.size() > 0)
			{
				if(nonz.get(0) == l)
				{
					for(int k = 1; k < nonz.size(); k++)
					{
						rowechform_piv(rref, nonz, l, j, k, n);
					}
				}
				else
				{
					for(int k = 0; k < nonz.size(); k++)
					{
						rowechform_piv(rref, nonz, l, j, k, n);
					}
				}
			}
			
			l = l + 1;
			J.set(j,  false);
			j = j + 1;
		}
		int[] output = new int[2];
		output[0] = l;
		output[1] =j;
		return output;
	}
	
	private static void rowechform_piv(ArrayList<ArrayList<Double>> rref, ArrayList<Integer> nonz, int i, int j, int k, int n)
	{
		ArrayList<Double> aux = new ArrayList<Double>(Collections.nCopies(n, 0.0));
		sc_vec_prod(aux, rref.get(nonz.get(k)).get(j), rref.get(i));
		vec_subtract(rref.get(nonz.get(k)), rref.get(nonz.get(k)), aux);
	}
	
	private static void rowechform_piv2(ArrayList<ArrayList<Double>> rref, int i, int n)
	{
		ArrayList<Double> aux = new ArrayList<Double>(Collections.nCopies(n, 0.0));
		sc_vec_prod(aux, rref.get(i).get(0), rref.get(0));
		vec_subtract(rref.get(i), aux, rref.get(i));
	}
	
	private static void swap_ith_and_firstnz(ArrayList<ArrayList<Double>> rref, ArrayList<Integer> nonz, int i)
	{
		ArrayList<Double> aux = rref.get(nonz.get(0));
		rref.set(nonz.get(0), rref.get(i));
		rref.set(i, aux);
		nonz.set(0, i);
	}
	 
	private void swap_ith_and_firstone(ArrayList<ArrayList<Double>> rref, ArrayList<Integer> ones, ArrayList<Integer> nonz, int i)
	{
		ArrayList<Double> aux = rref.get(ones.get(0));
		rref.set(i,  aux);
		if(nonz.size() > 0)
		{
			if(nonz.get(0) == i)
			{
				nonz.set(0, ones.get(0));
			}
		}
		ones.set(0, i);
	}
	
	private static boolean binrank(ArrayList<ArrayList<Double>> arref, ArrayList<Boolean> J, ArrayList<Boolean> b, int n)
	{
		double prec = Math.pow(10,  -10);
		ArrayList<Double> B = new ArrayList<Double>(Collections.nCopies(n, 0.0));
		for(int i = 0; i < n; i++)
		{
			if(b.get(i) == true)
			{
				B.set(i, 1.0);
			}
		}
		
		int m = 0;
		boolean size = true;
		while(size)
		{
			if(nonz_vec(arref.get(m), prec))
			{
				m++;
			}
			else
			{
				size = false;
			}
		}
		if(m >= n)
		{
			return false;
		}
		else
		{
			ArrayList<Boolean> pivot_col = new ArrayList<Boolean>(Collections.nCopies(n, false));
			for(int i = 0; i < n; i++)
			{
				if(J.get(i) == false)
				{
					pivot_col.set(i, true);
				}
			}
			int j = 0;
			ArrayList<Boolean> piv = new ArrayList<Boolean>(Collections.nCopies(n, false));
			ArrayList<Double> aux = new ArrayList<Double>(Collections.nCopies(n, 0.0));
			int k = 0;
			int I = 0;
			int ind = 0;
			int count = 0;
			while(j < n)
			{
				for(int i = 0; i < n; i++)
				{
					if(B.get(i) > prec || B.get(i) < -prec)
					{
						piv.set(i,  true);
					}
				}
				
				if(sum_vecb(piv)==0)
				{
					return false;
				}
				else
				{
					while(k==0)
					{
						if(piv.get(I) == true)
						{
							k = I + 1;
						}
						I++;
					}
					k--;
					I = 0;
					if(J.get(k) == true)
					{
						return true;
					}
					else
					{
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
						aux = sc_vec_prod(aux, B.get(k) / arref.get(ind).get(k), arref.get(ind));
						B = vec_subtract(B, B, aux);
						
						j++;
					}
				}
				for(int l = 0; l < n; l++)
				{
					piv.set(l, false);
				}
				k = 0;
				ind = 0;
			}
			return false;
		}
	}
	
	private static int sum_vecb(ArrayList<Boolean> x)
	{
		int s = 0;
		for(int i = 0; i< x.size(); i++)
		{
			s += x.get(i) ? 1:0;
		}
		return s;
	}
	
	private static boolean nonz_vec(ArrayList<Double> x, double prec)
	{
		for(int i = 0; i < x.size(); i++)
		{
			if(x.get(i) > prec || x.get(i) < -prec)
			{
				return true;
			}
		}
		return false;
	}
	
	private static ArrayList<Double> vec_subtract(ArrayList<Double> z, ArrayList<Double> x, ArrayList<Double> y)
	{
		for(int i = 0; i != x.size(); i++)
		{
			z.set(i, x.get(i) - y.get(i));
		}
		return z;
	}
	
	private static ArrayList<Double> sc_vec_prod(ArrayList<Double> y, double a, ArrayList<Double> x)
	{
		for(int i = 0; i < x.size(); i++)
		{
			y.set(i, a*x.get(i));
		}
		return y;
	}
	
	private static int de2bi_card(int i, ArrayList<Boolean> a, int n)
	{
		int k = 0;
		int x = 2;
		for(int c = 0; c < n-2; c++)
		{
			x += x;
		}
		int j = i + 1;
		int l = n - 1;
		while(j > 0)
		{
			if(j >= x)
			{
				a.set(l, true);
				k = k + 1;
				j -= x;
			}
			x /= 2;
			l--;
		}
		return k;
	}

}
