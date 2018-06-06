package example.java.modelchecking;
//import java.io.*;

class Event{
  int count = 0;

  public synchronized void wait_for_event() {
    try {
      wait();
    } catch(InterruptedException e) { };
  }

  public synchronized void signal_event() {
    count = (count + 1) % 3;
    notifyAll();
  }
}

class FirstTask extends Thread{
  Event event1,event2;
  int count = 0;

  public FirstTask(Event e1, Event e2) {
    this.event1 = e1;
    this.event2 = e2;
  }

  public void run() {
    int i = 0;
    count = event1.count;
    while(i++ < 5) {   // Uli: changed the upper bound from 1000 to 5
      if (count == event1.count)  
	event1.wait_for_event(); 
      count = event1.count;
      event2.signal_event();
    }
    return;
  }
}

class SecondTask extends Thread {
  Event event1,event2;
  int count = 0;

  public SecondTask(Event e1, Event e2) {
    this.event1 = e1;
    this.event2 = e2;
  }

  public void run() {
    count = event2.count;
    while(true) {
      event1.signal_event();
      if (count == event2.count)
        event2.wait_for_event();
      count = event2.count;
    }
  }
}

class classic {
  public static void main(String[] args) {
    Event new_event1 = new Event();
    Event new_event2 = new Event();

    FirstTask  task1 = new FirstTask(new_event1, new_event2);
    SecondTask task2 = new SecondTask(new_event1, new_event2);

    task1.start();
    task2.start();
  }
}
