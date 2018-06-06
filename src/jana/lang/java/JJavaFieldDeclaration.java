package jana.lang.java;

import java.util.List;

import jana.metamodel.JVariableDeclaration;
import jana.metamodel.values.JConstantValue;

public abstract class JJavaFieldDeclaration extends JVariableDeclaration 
{
	protected JConstantValue constantValue;
	protected List<JJavaAnnotation> annotations;
	protected JJavaFieldModifiers modifiers;
}
