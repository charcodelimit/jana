package example.jana.classes;

public class BooleanExample
{
	public boolean[] halfAdder(boolean bit1, boolean bit2)
	{
		boolean[] result;
		boolean sum, carry;
		
		sum = bit1 ^ bit2;
		carry = bit1 && bit2;
		
		result = new boolean[2];
		result[0] = sum;
		result[1] = carry;
		
		System.out.println("\nSum:   " +  bitToString(bit1) + " + " + bitToString(bit2) + " = " + bitToString(sum));
		System.out.println("Carry: " + bitToString(bit1) + " + " + bitToString(bit2) + " = " + bitToString(carry));
		return result;
	}
	
	public String bitToString(boolean bit)
	{
		if(bit)
			return "H";
		else
			return "L";
	}
	
	public boolean[] fullAdder(boolean bit1, boolean bit2, boolean carryBit)
	{
		boolean sum, carry;
		
		boolean[] finalResult;
		boolean[] firstResult;
		boolean[] secondResult;
		
		firstResult = halfAdder(bit1, bit2);
		secondResult = halfAdder(firstResult[0], carryBit);
		
		carry = firstResult[1] || secondResult[1];
		sum = secondResult[0];
		
		finalResult = new boolean[2];
		finalResult[0] = sum;
		finalResult[1] = carry;
		
		System.out.println("\nSum:   " + bitToString(carryBit) + " " + bitToString(bit1) + " + " + bitToString(bit2) + " = " + bitToString(sum));
		System.out.println("Carry: " + bitToString(carryBit) + " " + bitToString(bit1) + " + " + bitToString(bit2) + " = " + bitToString(carry));
		return finalResult;
	}
	
	public static void main(String[] args)
	{
		boolean[] result;
		BooleanExample be = new BooleanExample();
		
		result = be.fullAdder(false, false, false);	
		assert (result[0] == false) : "s L,L,L";
		assert (result[1] == false) : "c L,L,L";

		result = be.fullAdder(true, false, false);	
		assert (result[0] == true) : "s H,L,L";
		assert (result[1] == false) : "c H,L,L";

		result = be.fullAdder(false, true, false);	
		assert (result[0] == true) : "s L,H,L";
		assert (result[1] == false) : "c L,H,L";


		result = be.fullAdder(false, false, true);	
		assert (result[0] == true) : "s L,L,H";
		assert (result[1] == false) : "c L,L,H";
		
		result = be.fullAdder(true, false, true);	
		assert (result[0] == false) : "s H,L,H";
		assert (result[1] == true) : "c H,L,H";
		
		result = be.fullAdder(false, true, true);	
		assert (result[0] == false) : "s L,H,H";
		assert (result[1] == true) : "c L,H,H";
		
		result = be.fullAdder(true, true, true);	
		assert (result[0] == true) : "s H,H,H";
		assert (result[1] == true) : "c H,H,H";
	}
}
