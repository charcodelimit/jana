package jana.metamodel;

import jana.metamodel.typesystem.JReferenceType;

import java.util.List;

/**
 * Declares a user-defined type. 
 */
public abstract class JReferenceTypeDefinition extends JReferenceType 
{
	protected JGeneralization generalizationRelation;
	protected List<JVariableDeclaration> attributes;
	protected List<JRoutineDeclaration> routines;
}
