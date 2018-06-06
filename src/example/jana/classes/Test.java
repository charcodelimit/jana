package example.jana.classes;

import java.util.ArrayList;
import java.util.List;

public class Test
{
	static int MAX = 2;
	
	static int machma (StringBuffer sb, int nochZuTun)
	{
		int echtNochZuTun = Math.min(nochZuTun, MAX);
		sb.append("(list ");
		
		for(int i = 0; i < echtNochZuTun; i++)
		{
			if(i < echtNochZuTun - 1)
				sb.append((i+1) + " ");
			else
				sb.append((i+1));
		}
		
		sb.append(")");
		
		return echtNochZuTun;
	}
	
	static StringBuffer machmaLos(int nochZuTun)
	{	
		List<StringBuffer> stack1 = new ArrayList<StringBuffer>();
		List<StringBuffer> stack2 = new ArrayList<StringBuffer>();
		
		while(nochZuTun > 0)
		{
			StringBuffer sb1 = new StringBuffer();
			
			sb1.append("(nconc ");
			
			for( int i = 0; i < MAX && nochZuTun > 0; i++)
			{
				nochZuTun -= machma(sb1, nochZuTun);
			}
			
			sb1.append(")");
			
			stack1.add(sb1);
		}
		
		
		while(stack1.size() > 1)
		{
			while(stack1.size() > 0)
			{
				StringBuffer sb1 = new StringBuffer();
		
				sb1.append("(nconc ");
				
				for( int i = 0; i < MAX && stack1.size() > 0; i++)
				{
					sb1.append( (StringBuffer) stack1.remove(0) );
				}
			
				sb1.append(")");
			
				stack2.add(sb1);
			}

			stack1 = stack2;
			stack2 = new ArrayList<StringBuffer>();
		}
		
		if(stack1.size() > 0)
			return (StringBuffer) stack1.remove(0);
		else
			return new StringBuffer();
	}
	
	public static void main(String[] args)
	{
		StringBuffer str;
		str = machmaLos(16);
		System.out.println(str);
	}
}
