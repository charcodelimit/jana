package jana.lang.java;

public interface JNamedJavaElement
{
		public String getName();
		public JJavaSignature getSignature();
		public boolean equals(Object anObject);
		public int hashCode();
}
