package example.java.modelchecking;

class Assert {
  static void assert_(boolean b) {}
  static void error() {}
}

class Resource { public int x; }

public class SplitSync implements Runnable {
  static Resource resource  = new Resource();

  public static void main(String[] args) {
    new SplitSync();
    new SplitSync();
  }

  public SplitSync() {
    new Thread(this).start();
  }

  // increment resource.x
  public void run() {
    int y;
    synchronized (resource) {
      y = resource.x;
    }
    synchronized (resource) {
      //Assert.assert_(y == resource.x);
      Assert.error();
      resource.x = y + 1;
    }
  }
}

