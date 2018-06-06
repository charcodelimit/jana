package test.jana;

public interface JTestInterface
{
	public void setUp() throws Exception;
	public void runTest(String[] args);
	public abstract void unitTest() throws Exception;
}
