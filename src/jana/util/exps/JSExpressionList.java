package jana.util.exps;

import jana.metamodel.SExpression;

public abstract class JSExpressionList implements SExpression
{
	public static final SExpression NIL = new JSExpressionVectorList();
	public abstract SExpression nth(int index);
	public abstract int length();
	
	public Object first()
	{
		return nth(0);
	}
	
	public Object second()
	{
		return nth(1);
	}
}
