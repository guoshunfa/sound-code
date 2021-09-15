package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.impl.naming.cosnaming.BindingIteratorImpl;
import java.util.Enumeration;
import java.util.Hashtable;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.PortableServer.POA;

public class PersistentBindingIterator extends BindingIteratorImpl {
   private POA biPOA;
   private int currentSize;
   private Hashtable theHashtable;
   private Enumeration theEnumeration;
   private ORB orb;

   public PersistentBindingIterator(ORB var1, Hashtable var2, POA var3) throws Exception {
      super(var1);
      this.orb = var1;
      this.theHashtable = var2;
      this.theEnumeration = this.theHashtable.keys();
      this.currentSize = this.theHashtable.size();
      this.biPOA = var3;
   }

   public final boolean NextOne(BindingHolder var1) {
      boolean var2 = this.theEnumeration.hasMoreElements();
      if (var2) {
         InternalBindingKey var3 = (InternalBindingKey)this.theEnumeration.nextElement();
         InternalBindingValue var4 = (InternalBindingValue)this.theHashtable.get(var3);
         NameComponent var5 = new NameComponent(var3.id, var3.kind);
         NameComponent[] var6 = new NameComponent[]{var5};
         BindingType var7 = var4.theBindingType;
         var1.value = new Binding(var6, var7);
      } else {
         var1.value = new Binding(new NameComponent[0], BindingType.nobject);
      }

      return var2;
   }

   public final void Destroy() {
      try {
         byte[] var1 = this.biPOA.servant_to_id(this);
         if (var1 != null) {
            this.biPOA.deactivate_object(var1);
         }

      } catch (Exception var2) {
         throw new INTERNAL("Exception in BindingIterator.Destroy " + var2);
      }
   }

   public final int RemainingElements() {
      return this.currentSize;
   }
}
