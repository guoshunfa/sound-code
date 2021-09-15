package javax.management;

import java.io.Serializable;

public abstract class QueryEval implements Serializable {
   private static final long serialVersionUID = 2675899265640874796L;
   private static ThreadLocal<MBeanServer> server = new InheritableThreadLocal();

   public void setMBeanServer(MBeanServer var1) {
      server.set(var1);
   }

   public static MBeanServer getMBeanServer() {
      return (MBeanServer)server.get();
   }
}
