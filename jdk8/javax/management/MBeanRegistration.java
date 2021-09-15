package javax.management;

public interface MBeanRegistration {
   ObjectName preRegister(MBeanServer var1, ObjectName var2) throws Exception;

   void postRegister(Boolean var1);

   void preDeregister() throws Exception;

   void postDeregister();
}
