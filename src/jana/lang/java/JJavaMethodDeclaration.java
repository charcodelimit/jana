package jana.lang.java;

import java.util.List;

import jana.lang.java.typesystem.JJavaReferenceType;
import jana.metamodel.JRoutineDeclaration;

public abstract class JJavaMethodDeclaration extends JRoutineDeclaration
{
	protected JJavaClassifier owner;
	protected JJavaMethodModifiers modifiers;
	protected List<JJavaAnnotation> annotations;
	protected List<JJavaReferenceType> thrownExceptions;
}
