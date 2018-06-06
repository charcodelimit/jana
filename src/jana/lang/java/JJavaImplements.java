package jana.lang.java;

import jana.metamodel.JGeneralization;
import jana.metamodel.typesystem.JType;

public class JJavaImplements extends JGeneralization 
{	
	public void toSExpression(StringBuffer aStringBuffer)
	{	
		aStringBuffer.append("java-implements");
		
		if(this.superTypes.size() > 0)
		{
			aStringBuffer.append(" (list");
			for( JType superType : this.superTypes )
			{
				aStringBuffer.append(" (list");
				aStringBuffer.append(" (");
				superType.toSExpression(aStringBuffer);
				aStringBuffer.append(")");
				aStringBuffer.append(")");
			}
			aStringBuffer.append(")");
		}
		else
		{
			aStringBuffer.append(" NIL");
		}
	}
}
