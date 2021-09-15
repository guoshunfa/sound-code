package com.sun.corba.se.impl.naming.cosnaming;

import java.util.Enumeration;
import java.util.Hashtable;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.PortableServer.POA;

public class TransientBindingIterator extends BindingIteratorImpl {
   private POA nsPOA;
   private int currentSize;
   private Hashtable theHashtable;
   private Enumeration theEnumeration;

   public TransientBindingIterator(ORB var1, Hashtable var2, POA var3) throws Exception {
      super(var1);
      this.theHashtable = var2;
      this.theEnumeration = this.theHashtable.elements();
      this.currentSize = this.theHashtable.size();
      this.nsPOA = var3;
   }

   public final boolean NextOne(BindingHolder var1) {
      boolean var2 = this.theEnumeration.hasMoreElements();
      if (var2) {
         var1.value = ((InternalBindingValue)this.theEnumeration.nextElement()).theBinding;
         --this.currentSize;
      } else {
         var1.value = new Binding(new NameComponent[0], BindingType.nobject);
      }

      return var2;
   }

   public final void Destroy() {
      try {
         byte[] var1 = this.nsPOA.servant_to_id(this);
         if (var1 != null) {
            this.nsPOA.deactivate_object(var1);
         }
      } catch (Exception var2) {
         NamingUtils.errprint("BindingIterator.Destroy():caught exception:");
         NamingUtils.printException(var2);
      }

   }

   public final int RemainingElements() {
      return this.currentSize;
   }
}
