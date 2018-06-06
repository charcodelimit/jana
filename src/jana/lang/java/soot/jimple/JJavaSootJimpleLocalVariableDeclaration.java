package jana.lang.java.soot.jimple;

import soot.Local;
import soot.jimple.internal.JimpleLocal;

import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.lang.java.typesystem.JJavaType;
import jana.metamodel.JVariableDeclaration;

public class JJavaSootJimpleLocalVariableDeclaration extends JVariableDeclaration
{
	public JJavaSootJimpleLocalVariableDeclaration(Local aLocalVariable) throws Exception
	{
		this((JimpleLocal) aLocalVariable);
	}

	
	public JJavaSootJimpleLocalVariableDeclaration(JimpleLocal aJimpleLocal) throws Exception
	{	
		this.name = aJimpleLocal.getName();
		this.type = JJavaSootType.produce(aJimpleLocal.getType());
	}
	
	public String toString()
	{
		return ((JJavaType) this.type).toString() + " " + this.name;
	}
	
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (jimple-local-variable-declaration name type)
	 * 
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{
		aStringBuffer.append("jimple-local-variable-declaration");
		
		aStringBuffer.append(' ');
		aStringBuffer.append('\"');
		aStringBuffer.append(this.name);
		aStringBuffer.append('\"');
		
		aStringBuffer.append(' ');
		aStringBuffer.append('(');
		this.type.toSExpression(aStringBuffer);
		aStringBuffer.append(')');
	}
}
