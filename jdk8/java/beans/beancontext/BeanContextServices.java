package java.beans.beancontext;

import java.util.Iterator;
import java.util.TooManyListenersException;

public interface BeanContextServices extends BeanContext, BeanContextServicesListener {
   boolean addService(Class var1, BeanContextServiceProvider var2);

   void revokeService(Class var1, BeanContextServiceProvider var2, boolean var3);

   boolean hasService(Class var1);

   Object getService(BeanContextChild var1, Object var2, Class var3, Object var4, BeanContextServiceRevokedListener var5) throws TooManyListenersException;

   void releaseService(BeanContextChild var1, Object var2, Object var3);

   Iterator getCurrentServiceClasses();

   Iterator getCurrentServiceSelectors(Class var1);

   void addBeanContextServicesListener(BeanContextServicesListener var1);

   void removeBeanContextServicesListener(BeanContextServicesListener var1);
}
