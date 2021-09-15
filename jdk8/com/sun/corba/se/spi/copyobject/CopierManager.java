package com.sun.corba.se.spi.copyobject;

public interface CopierManager {
   void setDefaultId(int var1);

   int getDefaultId();

   ObjectCopierFactory getObjectCopierFactory(int var1);

   ObjectCopierFactory getDefaultObjectCopierFactory();

   void registerObjectCopierFactory(ObjectCopierFactory var1, int var2);
}
