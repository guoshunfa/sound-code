package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.impl.orbutil.DenseIntMapImpl;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.orb.ORB;

public class CopierManagerImpl implements CopierManager {
   private int defaultId = 0;
   private DenseIntMapImpl map = new DenseIntMapImpl();
   private ORB orb;

   public CopierManagerImpl(ORB var1) {
      this.orb = var1;
   }

   public void setDefaultId(int var1) {
      this.defaultId = var1;
   }

   public int getDefaultId() {
      return this.defaultId;
   }

   public ObjectCopierFactory getObjectCopierFactory(int var1) {
      return (ObjectCopierFactory)((ObjectCopierFactory)this.map.get(var1));
   }

   public ObjectCopierFactory getDefaultObjectCopierFactory() {
      return (ObjectCopierFactory)((ObjectCopierFactory)this.map.get(this.defaultId));
   }

   public void registerObjectCopierFactory(ObjectCopierFactory var1, int var2) {
      this.map.set(var2, var1);
   }
}
