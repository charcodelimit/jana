package jana.lang.java;

import jana.lang.java.typesystem.JJavaReferenceType;
import jana.metamodel.JClosure;

import java.util.List;

/**
 * "A syntactic closure. Unlike in pure lambda calculus,
 * 	return type and local variables are made explicit."
 * @author chr
 *
 */
public abstract class JJavaClosure extends JClosure {
	protected List<JJavaReferenceType> thrownExceptions;
}
