package jana.lang.java.bcel;

import jana.lang.java.JJavaImplements;
import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.typesystem.JJavaBcelObjectType;

public class JJavaBcelImplements extends JJavaImplements
{
	public void addInterface(JJavaSignature aSignature)
	{
		super.addSuperType(new JJavaBcelObjectType(aSignature));
	}
}
