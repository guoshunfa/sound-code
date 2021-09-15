package java.beans.beancontext;

import java.beans.DesignMode;
import java.beans.Visibility;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

public interface BeanContext extends BeanContextChild, Collection, DesignMode, Visibility {
   Object globalHierarchyLock = new Object();

   Object instantiateChild(String var1) throws IOException, ClassNotFoundException;

   InputStream getResourceAsStream(String var1, BeanContextChild var2) throws IllegalArgumentException;

   URL getResource(String var1, BeanContextChild var2) throws IllegalArgumentException;

   void addBeanContextMembershipListener(BeanContextMembershipListener var1);

   void removeBeanContextMembershipListener(BeanContextMembershipListener var1);
}
