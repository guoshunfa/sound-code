package sun.applet;

public class AppletThreadGroup extends ThreadGroup {
   public AppletThreadGroup(String var1) {
      this(Thread.currentThread().getThreadGroup(), var1);
   }

   public AppletThreadGroup(ThreadGroup var1, String var2) {
      super(var1, var2);
      this.setMaxPriority(4);
   }
}
