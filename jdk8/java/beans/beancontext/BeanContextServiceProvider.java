package java.beans.beancontext;

import java.util.Iterator;

public interface BeanContextServiceProvider {
   Object getService(BeanContextServices var1, Object var2, Class var3, Object var4);

   void releaseService(BeanContextServices var1, Object var2, Object var3);

   Iterator getCurrentServiceSelectors(BeanContextServices var1, Class var2);
}
