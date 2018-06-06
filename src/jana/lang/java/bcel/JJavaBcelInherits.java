package jana.lang.java.bcel;

import jana.lang.java.JJavaInherits;
import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.typesystem.JJavaBcelObjectType;
import jana.metamodel.typesystem.JReferenceType;

public class JJavaBcelInherits extends JJavaInherits
{
	public JJavaBcelInherits()
	{
		super();
	}
	
	public JJavaBcelInherits(JReferenceType referenceType)
	{
		super(referenceType);
	}

	public void addSuperClass(JJavaSignature aSignature)
	{
		super.addSuperType(new JJavaBcelObjectType(aSignature));
	}
}
