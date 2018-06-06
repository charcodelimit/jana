package example.jana.classes;

public class ConstantTypes 
{
	static String stringConstant = new String();
	
	static final char charConstant = 'c';
	static final byte byteConstant = 125;
	static final short shortConstant = 214; 
	static final int integerConstant = Integer.MIN_VALUE + 2;
	static final long longConstant = Long.MAX_VALUE - 2;
	static final float floatConstant = 0.1f;
	static final double doubleConstant = 0.1d;
	
	static char charClassVar = 'c';
	static byte byteClassVar = 125;
	static short shortClassVar = 214; 
	static int integerClassVar = Integer.MIN_VALUE + 2;
	static long longClassVar = Long.MAX_VALUE - 2;
	static float floatClassVar = 0.1f;
	static double doubleClassVar = 0.1d;
	
	char charInstanceVar = 'c';
	byte byteInstanceVar = 125;
	short shortInstanceVar = 214; 
	int integerInstanceVar = Integer.MIN_VALUE + 2;
	long longInstanceVar = Long.MAX_VALUE - 2;
	float floatInstanceVar = 0.1f;
	double doubleInstanceVar = 0.1d;
	
	final static int integerRefConstant = integerConstant; 
	static int integerRefClassVar = integerConstant;
	int integerRefInstanceVar = integerConstant;
	
	final static int integerRecConstant = integerRefConstant;
	static int integerRecClassVar = integerRefClassVar;
	int integerRecInstanceVar = integerRefInstanceVar;
	
	final static int integerAsgConstant = defconstant(); 
	final int integerAsgClassVar = defconstant();
	int integerAsgInstanceVar = defconstant();
	
	final static Integer integerObjectConstant = new Integer(2443);
	static Integer integerObjectClassVar = new Integer(2442);
	Integer integerObjectInstanceVar = new Integer(2441);
	
	final static StringBuffer stringBufferAsgConstant = defconstantObject();
	static StringBuffer stringBufferAsgClassVar = defconstantObject();
	StringBuffer stringBufferAsgInstanceVar = defconstantObject();
	
	public static int defconstant()
	{
		return -1;
	}
	
	public static StringBuffer defconstantObject()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Hello");
		sb.append("World");
		
		return sb;
	}
}
