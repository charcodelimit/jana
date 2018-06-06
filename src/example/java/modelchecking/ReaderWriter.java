package example.java.modelchecking;
//
// ReaderWriter example from CS193k class notes
//

class ReaderWriter {
  int len = 0;

  public synchronized void write() {
    len++;
    System.out.println("Write elem " + (len-1));
    notify();
  }

  public synchronized void read() {
    while (len == 0)
      try { wait(); } catch(InterruptedException ignored) { }
    System.out.println("Read elem " + (len-1));
    len--;
  }

  public void run() {

    // writer one
    new Thread() {
      public void run() {
        for (int i=0; i<1; i++) {
          write();
          yield();
        }
      }
    }.start();

    // writer two
    new Thread() {
      public void run() {
        for (int i=0; i<1; i++) {
          write();
          yield();
        }
      }
    }.start();

    // reader one
    new Thread() {
      public void run() {
        for (int i=0; i<1; i++) {
          read();
          yield();
        }
        System.out.println("reader one done");
      }
    }.start();

    // reader two
    new Thread() {
      public void run() {
        for (int i=0; i<1; i++) {
          read();
          yield();
        }
        System.out.println("reader two done");
      }
    }.start();

  }

  public static void main(String[] args) {
    new ReaderWriter().run();
  }
}
