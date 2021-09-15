package com.sun.jmx.mbeanserver;

import javax.management.ObjectName;
import javax.management.loading.ClassLoaderRepository;

public interface ModifiableClassLoaderRepository extends ClassLoaderRepository {
   void addClassLoader(ClassLoader var1);

   void removeClassLoader(ClassLoader var1);

   void addClassLoader(ObjectName var1, ClassLoader var2);

   void removeClassLoader(ObjectName var1);

   ClassLoader getClassLoader(ObjectName var1);
}
