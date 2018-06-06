package jana.util.exps;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jana.metamodel.SExpression;
import jana.util.exceptions.JParseException;

/**
 * Association Lists are used for a rather small number of pairs, 
 * therefore they either trade-off speed [O(n) for all operations]  
 * or storage-space [O(1) for all operations]
 */
public abstract class AssociationList<T1,T2> extends JSExpressionList implements SExpression
{
	// 4 KBytes Buffer, as association lists are usually small because they store no duplicate elements
	protected static final int READ_BUFFER = 1 << 12; 
	
	public abstract void addPair(T1 aKey, T2 aValue);
	public abstract boolean containsKey(T1 aKey);
	public abstract boolean remove(T1 aKey);
	public abstract T2 getValue(T1 aKey);
	
	public void write(OutputStream out) throws IOException
	{
		DataOutputStream dos = new DataOutputStream(out);
		this.write(dos);
	}
	
	/***
	 * This method may not be safe for concurrent file access.
	 * 
	 * @param dos - a DataOutputStream used to write the data.
	 * @throws IOException
	 */
	public void write(DataOutputStream dos) throws IOException
	{
		dos.writeBytes( this.toSExpression() );
	}
	
	/**
	 * This method may not be safe for concurrent file access.
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static AssociationList<String,String> read(InputStream in) throws IOException
	{
		throw new RuntimeException("Subclass Responsibility!");
	}
	
	protected void initializeFromStream(InputStream in) throws IOException
	{
		DataInputStream dis = new DataInputStream(in);
		byte[] byteBuffer = new byte[READ_BUFFER];
		StringBuffer sb = new StringBuffer();
		int bytesRead = 0;
		
		try
		{
			while(bytesRead != -1)
			{
				if(bytesRead > 0)
					sb.append( new String(byteBuffer,0, bytesRead) );
			
				bytesRead = dis.read(byteBuffer);
			}
		}
		finally
		{
			dis.close();
		}
		
		initialize(sb.toString());
	}

	
	@SuppressWarnings("unchecked")
	protected void initialize(String aString) throws IOException
	{
		String list;
		String pair;
		Pair<String,String> pairObject;
		int index, start, end;
		int size;
		
		if(aString.length() == 0)
			return;
		
		list = aString.trim();
		
		start = list.indexOf('(');
		end = list.lastIndexOf(')');
		
		if(start < 0 || end < 0)
			throw new JParseException("Error while parsing the Association List " + aString);
		
		list =  aString.substring(start + 1, end);
		
		size = list.length();
		index = 0;
		while( index < size )
		{
			start = list.indexOf('(', index);
			end = list.indexOf(')', start);
			
			if(start < 0 || end < 0)
				throw new JParseException("Error while parsing the Association List " + aString);
			
			pair = list.substring(start, end + 1);
			pairObject = Pair.parsePair(pair);
			this.addPair((T1) pairObject.key(),(T2) pairObject.value());
			
			index = end + 1;
		}
		
		list = null;
	}
}
