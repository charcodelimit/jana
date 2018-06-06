package example.java.modelchecking;

class Parameters {

  static final int Stage_size = 3;
  static final int BlockingQueue_size = 5;

}

class ksu_pipe {
  public static void main (String argv[]) {
    BlockingQueue first, in, out;
    Stage t1;
    Listener t2;

    first = new BlockingQueue();
    in = first;
    out = in;
    
    for (int i = 0; i < 2; i++) {   // Uli: changed the upper bound from 10 to 2
      out = new BlockingQueue();
      t1 = new Stage(in,out);
      t1.start();
      in = out;
    } 
    
    t2 = new Listener(out);
    t2.start();

    first.add(1);       
    first.add(2);       
    first.add(3);       
    first.add(0);     
  }
}


final class BlockingQueue  {
  int queue = -1;

  public final synchronized int take() {
    int value;

    while ( queue < 0 ) 
      try { wait(); } 
      catch ( InterruptedException ex) { }

    value = queue;
    queue = -1;
    return value;
  }

  public final synchronized void add(int o) {
    queue = o;
    notifyAll();
  }
}


final class Listener extends Thread {
  BlockingQueue input;

  public Listener(BlockingQueue in) { input = in; } 

  public void run() {
    int tmp = -1;

    while (tmp != 0) {
      tmp = input.take();
      //if (tmp != 0) Verify.print("tmp", tmp);  
    } 
  } 
}


final class Stage extends Thread {
  BlockingQueue input, output;

  public Stage(BlockingQueue in, BlockingQueue out) {
    input = in; output = out; 
  } 

  public void run() {
    int tmp = -1;

    while (tmp != 0) {
      tmp = input.take();
      if (tmp != 0) tmp = tmp + 1;
      output.add(tmp);
    } 
  } 
}

