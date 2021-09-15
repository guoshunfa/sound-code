package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.IdentifiableFactory;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.orb.ORB;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class IdentifiableFactoryFinderBase implements IdentifiableFactoryFinder {
   private ORB orb;
   private Map map = new HashMap();
   protected IORSystemException wrapper;

   protected IdentifiableFactoryFinderBase(ORB var1) {
      this.orb = var1;
      this.wrapper = IORSystemException.get(var1, "oa.ior");
   }

   protected IdentifiableFactory getFactory(int var1) {
      Integer var2 = new Integer(var1);
      IdentifiableFactory var3 = (IdentifiableFactory)((IdentifiableFactory)this.map.get(var2));
      return var3;
   }

   public abstract Identifiable handleMissingFactory(int var1, InputStream var2);

   public Identifiable create(int var1, InputStream var2) {
      IdentifiableFactory var3 = this.getFactory(var1);
      return var3 != null ? var3.create(var2) : this.handleMissingFactory(var1, var2);
   }

   public void registerFactory(IdentifiableFactory var1) {
      Integer var2 = new Integer(var1.getId());
      this.map.put(var2, var1);
   }
}
