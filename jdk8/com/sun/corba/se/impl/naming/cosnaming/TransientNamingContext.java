package com.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.BindingIteratorHelper;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.POA;

public class TransientNamingContext extends NamingContextImpl implements NamingContextDataStore {
   private Logger readLogger;
   private Logger updateLogger;
   private Logger lifecycleLogger;
   private NamingSystemException wrapper;
   private final Hashtable theHashtable = new Hashtable();
   public Object localRoot;

   public TransientNamingContext(ORB var1, Object var2, POA var3) throws Exception {
      super(var1, var3);
      this.wrapper = NamingSystemException.get(var1, "naming");
      this.localRoot = var2;
      this.readLogger = var1.getLogger("naming.read");
      this.updateLogger = var1.getLogger("naming.update");
      this.lifecycleLogger = var1.getLogger("naming.lifecycle");
      this.lifecycleLogger.fine("Root TransientNamingContext LIFECYCLE.CREATED");
   }

   public final void Bind(NameComponent var1, Object var2, BindingType var3) throws SystemException {
      InternalBindingKey var4 = new InternalBindingKey(var1);
      NameComponent[] var5 = new NameComponent[]{var1};
      Binding var6 = new Binding(var5, var3);
      InternalBindingValue var7 = new InternalBindingValue(var6, (String)null);
      var7.theObjectRef = var2;
      InternalBindingValue var8 = (InternalBindingValue)this.theHashtable.put(var4, var7);
      if (var8 != null) {
         this.updateLogger.warning("<<NAMING BIND>>Name " + this.getName(var1) + " Was Already Bound");
         throw this.wrapper.transNcBindAlreadyBound();
      } else {
         if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING BIND>><<SUCCESS>>Name Component: " + var1.id + "." + var1.kind);
         }

      }
   }

   public final Object Resolve(NameComponent var1, BindingTypeHolder var2) throws SystemException {
      if (var1.id.length() == 0 && var1.kind.length() == 0) {
         var2.value = BindingType.ncontext;
         return this.localRoot;
      } else {
         InternalBindingKey var3 = new InternalBindingKey(var1);
         InternalBindingValue var4 = (InternalBindingValue)this.theHashtable.get(var3);
         if (var4 == null) {
            return null;
         } else {
            if (this.readLogger.isLoggable(Level.FINE)) {
               this.readLogger.fine("<<NAMING RESOLVE>><<SUCCESS>>Namecomponent :" + this.getName(var1));
            }

            var2.value = var4.theBinding.binding_type;
            return var4.theObjectRef;
         }
      }
   }

   public final Object Unbind(NameComponent var1) throws SystemException {
      InternalBindingKey var2 = new InternalBindingKey(var1);
      InternalBindingValue var3 = (InternalBindingValue)this.theHashtable.remove(var2);
      if (var3 == null) {
         if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING UNBIND>><<FAILURE>> There was no binding with the name " + this.getName(var1) + " to Unbind ");
         }

         return null;
      } else {
         if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING UNBIND>><<SUCCESS>> NameComponent:  " + this.getName(var1));
         }

         return var3.theObjectRef;
      }
   }

   public final void List(int var1, BindingListHolder var2, BindingIteratorHolder var3) throws SystemException {
      try {
         TransientBindingIterator var4 = new TransientBindingIterator(this.orb, (Hashtable)this.theHashtable.clone(), this.nsPOA);
         var4.list(var1, var2);
         byte[] var5 = this.nsPOA.activate_object(var4);
         Object var6 = this.nsPOA.id_to_reference(var5);
         BindingIterator var7 = BindingIteratorHelper.narrow(var6);
         var3.value = var7;
      } catch (SystemException var8) {
         this.readLogger.warning("<<NAMING LIST>><<FAILURE>>" + var8);
         throw var8;
      } catch (Exception var9) {
         this.readLogger.severe("<<NAMING LIST>><<FAILURE>>" + var9);
         throw this.wrapper.transNcListGotExc((Throwable)var9);
      }
   }

   public final NamingContext NewContext() throws SystemException {
      try {
         TransientNamingContext var1 = new TransientNamingContext(this.orb, this.localRoot, this.nsPOA);
         byte[] var2 = this.nsPOA.activate_object(var1);
         Object var3 = this.nsPOA.id_to_reference(var2);
         this.lifecycleLogger.fine("TransientNamingContext LIFECYCLE.CREATE SUCCESSFUL");
         return NamingContextHelper.narrow(var3);
      } catch (SystemException var4) {
         this.lifecycleLogger.log(Level.WARNING, (String)"<<LIFECYCLE CREATE>><<FAILURE>>", (Throwable)var4);
         throw var4;
      } catch (Exception var5) {
         this.lifecycleLogger.log(Level.WARNING, (String)"<<LIFECYCLE CREATE>><<FAILURE>>", (Throwable)var5);
         throw this.wrapper.transNcNewctxGotExc((Throwable)var5);
      }
   }

   public final void Destroy() throws SystemException {
      try {
         byte[] var1 = this.nsPOA.servant_to_id(this);
         if (var1 != null) {
            this.nsPOA.deactivate_object(var1);
         }

         if (this.lifecycleLogger.isLoggable(Level.FINE)) {
            this.lifecycleLogger.fine("<<LIFECYCLE DESTROY>><<SUCCESS>>");
         }

      } catch (SystemException var2) {
         this.lifecycleLogger.log(Level.WARNING, (String)"<<LIFECYCLE DESTROY>><<FAILURE>>", (Throwable)var2);
         throw var2;
      } catch (Exception var3) {
         this.lifecycleLogger.log(Level.WARNING, (String)"<<LIFECYCLE DESTROY>><<FAILURE>>", (Throwable)var3);
         throw this.wrapper.transNcDestroyGotExc((Throwable)var3);
      }
   }

   private String getName(NameComponent var1) {
      return var1.id + "." + var1.kind;
   }

   public final boolean IsEmpty() {
      return this.theHashtable.isEmpty();
   }
}
