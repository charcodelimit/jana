package example.java.modelchecking;
//
// TurnDemo2 example from CS193k class notes
//

class Semaphore {
  private int count;

  public Semaphore(int value) {
    count = value;
  }

  public synchronized void decr() {
    count--;
    if (count < 0)
      try { wait(); } catch(InterruptedException ignored) { }
  }

  public synchronized void incr() {
    count++;
    if (count <= 0)
      notify();
  }
}

class TurnDemo {
  Semaphore aGo = new Semaphore(1);   // a gets to go first
  Semaphore bGo = new Semaphore(0);

  void a() {
    aGo.decr();
    synchronized(this) {
      System.out.println("A rules");
    }
    bGo.incr();
  }

  void b() {
    bGo.decr();
    synchronized(this) {
      System.out.println("B rules");
    }
    aGo.incr();
  }

  public void run() {

    new Thread() {
      public void run() {
        for (int i=0; i<3; i++) { b(); }
      }
    }.start();

    new Thread() {
      public void run() {
        for (int i=0; i<3; i++) { a(); }
      }
    }.start();

  }

  public static void main(String[] args) {
    new TurnDemo().run();
  }

}
