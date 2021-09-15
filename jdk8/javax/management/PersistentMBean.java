package javax.management;

public interface PersistentMBean {
   void load() throws MBeanException, RuntimeOperationsException, InstanceNotFoundException;

   void store() throws MBeanException, RuntimeOperationsException, InstanceNotFoundException;
}
