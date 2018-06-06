/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 * Copyright (C) 2004 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package jana.lang.java.soot.jimple.values.references;

import soot.Value;
import soot.jimple.internal.JInstanceFieldRef;

public class JJavaSootJimpleInstanceFieldRef extends JJavaSootJimpleFieldReference
{
	JJavaSootJimpleReferenceValueLocal localVariable;
	
	public JJavaSootJimpleInstanceFieldRef(Value aValue) throws Exception
	{
		this((JInstanceFieldRef) aValue);
	}
	
	public JJavaSootJimpleInstanceFieldRef(JInstanceFieldRef aReference) throws Exception
	{
		super(aReference.getFieldRef());
		this.valueType = "jimple-reference-value-instance-variable";
		this.localVariable = JJavaSootJimpleReferenceValueLocal.produce(aReference.getBase());
	}

	/**
	 * (jimple-reference-value-instance-variable local-variable declaring-class-signature type field-name)
	 */
	public String toSExpression()
	{
		StringBuffer sb = new StringBuffer();
		
		this.toSExpression(sb);
		
		return sb.toString();
	}
	
	/**
	 * (jimple-reference-value-instance-variable local-variable declaring-class-signature type field-name)
	 */
	public void toSExpression(StringBuffer aStringBuffer)
	{	
		aStringBuffer.append(this.valueType);
		
		aStringBuffer.append(" (");
		this.localVariable.toSExpression(aStringBuffer);
		aStringBuffer.append(")");
		
		super.toSExpression(aStringBuffer);
	}
	
	public static boolean modelsValue(Value aValue)
	{
		return (aValue instanceof JInstanceFieldRef);
	}
}
