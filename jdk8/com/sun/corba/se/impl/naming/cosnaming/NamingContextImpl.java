package com.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExtPOA;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundReason;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public abstract class NamingContextImpl extends NamingContextExtPOA implements NamingContextDataStore {
   protected POA nsPOA;
   private Logger readLogger;
   private Logger updateLogger;
   private Logger lifecycleLogger;
   private NamingSystemException wrapper;
   private static NamingSystemException staticWrapper = NamingSystemException.get("naming.update");
   private InterOperableNamingImpl insImpl;
   protected transient ORB orb;
   public static final boolean debug = false;

   public NamingContextImpl(ORB var1, POA var2) throws Exception {
      this.orb = var1;
      this.wrapper = NamingSystemException.get(var1, "naming.update");
      this.insImpl = new InterOperableNamingImpl();
      this.nsPOA = var2;
      this.readLogger = var1.getLogger("naming.read");
      this.updateLogger = var1.getLogger("naming.update");
      this.lifecycleLogger = var1.getLogger("naming.lifecycle");
   }

   public POA getNSPOA() {
      return this.nsPOA;
   }

   public void bind(NameComponent[] var1, Object var2) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
      if (var2 == null) {
         this.updateLogger.warning("<<NAMING BIND>> unsuccessful because NULL Object cannot be Bound ");
         throw this.wrapper.objectIsNull();
      } else {
         doBind(this, var1, var2, false, BindingType.nobject);
         if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING BIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(var1));
         }

      }
   }

   public void bind_context(NameComponent[] var1, NamingContext var2) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
      if (var2 == null) {
         this.updateLogger.warning("<<NAMING BIND>><<FAILURE>> NULL Context cannot be Bound ");
         throw new BAD_PARAM("Naming Context should not be null ");
      } else {
         doBind(this, var1, var2, false, BindingType.ncontext);
         if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING BIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(var1));
         }

      }
   }

   public void rebind(NameComponent[] var1, Object var2) throws NotFound, CannotProceed, InvalidName {
      if (var2 == null) {
         this.updateLogger.warning("<<NAMING REBIND>><<FAILURE>> NULL Object cannot be Bound ");
         throw this.wrapper.objectIsNull();
      } else {
         try {
            doBind(this, var1, var2, true, BindingType.nobject);
         } catch (AlreadyBound var4) {
            this.updateLogger.warning("<<NAMING REBIND>><<FAILURE>>" + NamingUtils.getDirectoryStructuredName(var1) + " is already bound to a Naming Context");
            throw this.wrapper.namingCtxRebindAlreadyBound((Throwable)var4);
         }

         if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING REBIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(var1));
         }

      }
   }

   public void rebind_context(NameComponent[] var1, NamingContext var2) throws NotFound, CannotProceed, InvalidName {
      if (var2 == null) {
         this.updateLogger.warning("<<NAMING REBIND>><<FAILURE>> NULL Context cannot be Bound ");
         throw this.wrapper.objectIsNull();
      } else {
         try {
            doBind(this, var1, var2, true, BindingType.ncontext);
         } catch (AlreadyBound var4) {
            this.updateLogger.warning("<<NAMING REBIND>><<FAILURE>>" + NamingUtils.getDirectoryStructuredName(var1) + " is already bound to a CORBA Object");
            throw this.wrapper.namingCtxRebindctxAlreadyBound((Throwable)var4);
         }

         if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING REBIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(var1));
         }

      }
   }

   public Object resolve(NameComponent[] var1) throws NotFound, CannotProceed, InvalidName {
      Object var3 = doResolve(this, var1);
      if (var3 != null) {
         if (this.readLogger.isLoggable(Level.FINE)) {
            this.readLogger.fine("<<NAMING RESOLVE>><<SUCCESS>> Name: " + NamingUtils.getDirectoryStructuredName(var1));
         }
      } else {
         this.readLogger.warning("<<NAMING RESOLVE>><<FAILURE>> Name: " + NamingUtils.getDirectoryStructuredName(var1));
      }

      return var3;
   }

   public void unbind(NameComponent[] var1) throws NotFound, CannotProceed, InvalidName {
      doUnbind(this, var1);
      if (this.updateLogger.isLoggable(Level.FINE)) {
         this.updateLogger.fine("<<NAMING UNBIND>><<SUCCESS>> Name: " + NamingUtils.getDirectoryStructuredName(var1));
      }

   }

   public void list(int var1, BindingListHolder var2, BindingIteratorHolder var3) {
      NamingContextImpl var4 = this;
      synchronized(this) {
         var4.List(var1, var2, var3);
      }

      if (this.readLogger.isLoggable(Level.FINE) && var2.value != null) {
         this.readLogger.fine("<<NAMING LIST>><<SUCCESS>>list(" + var1 + ") -> bindings[" + var2.value.length + "] + iterator: " + var3.value);
      }

   }

   public synchronized NamingContext new_context() {
      this.lifecycleLogger.fine("Creating New Naming Context ");
      NamingContextImpl var1 = this;
      synchronized(this) {
         NamingContext var3 = var1.NewContext();
         if (var3 != null) {
            this.lifecycleLogger.fine("<<LIFECYCLE CREATE>><<SUCCESS>>");
         } else {
            this.lifecycleLogger.severe("<<LIFECYCLE CREATE>><<FAILURE>>");
         }

         return var3;
      }
   }

   public NamingContext bind_new_context(NameComponent[] var1) throws NotFound, AlreadyBound, CannotProceed, InvalidName {
      NamingContext var2 = null;
      NamingContext var3 = null;

      try {
         var2 = this.new_context();
         this.bind_context(var1, var2);
         var3 = var2;
         var2 = null;
      } finally {
         try {
            if (var2 != null) {
               var2.destroy();
            }
         } catch (NotEmpty var10) {
         }

      }

      if (this.updateLogger.isLoggable(Level.FINE)) {
         this.updateLogger.fine("<<NAMING BIND>>New Context Bound To " + NamingUtils.getDirectoryStructuredName(var1));
      }

      return var3;
   }

   public void destroy() throws NotEmpty {
      this.lifecycleLogger.fine("Destroying Naming Context ");
      NamingContextImpl var1 = this;
      synchronized(this) {
         if (var1.IsEmpty()) {
            var1.Destroy();
            this.lifecycleLogger.fine("<<LIFECYCLE DESTROY>><<SUCCESS>>");
         } else {
            this.lifecycleLogger.warning("<<LIFECYCLE DESTROY>><<FAILURE>> NamingContext children are not destroyed still..");
            throw new NotEmpty();
         }
      }
   }

   public static void doBind(NamingContextDataStore var0, NameComponent[] var1, Object var2, boolean var3, BindingType var4) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
      if (var1.length < 1) {
         throw new InvalidName();
      } else {
         if (var1.length == 1) {
            if (var1[0].id.length() == 0 && var1[0].kind.length() == 0) {
               throw new InvalidName();
            }

            synchronized(var0) {
               BindingTypeHolder var6 = new BindingTypeHolder();
               if (var3) {
                  Object var7 = var0.Resolve(var1[0], var6);
                  if (var7 != null) {
                     if (var6.value.value() == BindingType.nobject.value()) {
                        if (var4.value() == BindingType.ncontext.value()) {
                           throw new NotFound(NotFoundReason.not_context, var1);
                        }
                     } else if (var4.value() == BindingType.nobject.value()) {
                        throw new NotFound(NotFoundReason.not_object, var1);
                     }

                     var0.Unbind(var1[0]);
                  }
               } else if (var0.Resolve(var1[0], var6) != null) {
                  throw new AlreadyBound();
               }

               var0.Bind(var1[0], var2, var4);
            }
         } else {
            NamingContext var5 = resolveFirstAsContext(var0, var1);
            NameComponent[] var10 = new NameComponent[var1.length - 1];
            System.arraycopy(var1, 1, var10, 0, var1.length - 1);
            switch(var4.value()) {
            case 0:
               if (var3) {
                  var5.rebind(var10, var2);
               } else {
                  var5.bind(var10, var2);
               }
               break;
            case 1:
               NamingContext var11 = (NamingContext)var2;
               if (var3) {
                  var5.rebind_context(var10, var11);
               } else {
                  var5.bind_context(var10, var11);
               }
               break;
            default:
               throw staticWrapper.namingCtxBadBindingtype();
            }
         }

      }
   }

   public static Object doResolve(NamingContextDataStore var0, NameComponent[] var1) throws NotFound, CannotProceed, InvalidName {
      Object var2 = null;
      BindingTypeHolder var3 = new BindingTypeHolder();
      if (var1.length < 1) {
         throw new InvalidName();
      } else if (var1.length == 1) {
         synchronized(var0) {
            var2 = var0.Resolve(var1[0], var3);
         }

         if (var2 == null) {
            throw new NotFound(NotFoundReason.missing_node, var1);
         } else {
            return var2;
         }
      } else if (var1[1].id.length() == 0 && var1[1].kind.length() == 0) {
         throw new InvalidName();
      } else {
         NamingContext var4 = resolveFirstAsContext(var0, var1);
         NameComponent[] var5 = new NameComponent[var1.length - 1];
         System.arraycopy(var1, 1, var5, 0, var1.length - 1);

         try {
            Servant var6 = var0.getNSPOA().reference_to_servant(var4);
            return doResolve((NamingContextDataStore)var6, var5);
         } catch (Exception var8) {
            return var4.resolve(var5);
         }
      }
   }

   public static void doUnbind(NamingContextDataStore var0, NameComponent[] var1) throws NotFound, CannotProceed, InvalidName {
      if (var1.length < 1) {
         throw new InvalidName();
      } else {
         NamingContext var2;
         if (var1.length == 1) {
            if (var1[0].id.length() == 0 && var1[0].kind.length() == 0) {
               throw new InvalidName();
            } else {
               var2 = null;
               Object var6;
               synchronized(var0) {
                  var6 = var0.Unbind(var1[0]);
               }

               if (var6 == null) {
                  throw new NotFound(NotFoundReason.missing_node, var1);
               }
            }
         } else {
            var2 = resolveFirstAsContext(var0, var1);
            NameComponent[] var3 = new NameComponent[var1.length - 1];
            System.arraycopy(var1, 1, var3, 0, var1.length - 1);
            var2.unbind(var3);
         }
      }
   }

   protected static NamingContext resolveFirstAsContext(NamingContextDataStore var0, NameComponent[] var1) throws NotFound {
      Object var2 = null;
      BindingTypeHolder var3 = new BindingTypeHolder();
      NamingContext var4 = null;
      synchronized(var0) {
         var2 = var0.Resolve(var1[0], var3);
         if (var2 == null) {
            throw new NotFound(NotFoundReason.missing_node, var1);
         }
      }

      if (var3.value != BindingType.ncontext) {
         throw new NotFound(NotFoundReason.not_context, var1);
      } else {
         try {
            var4 = NamingContextHelper.narrow(var2);
            return var4;
         } catch (BAD_PARAM var7) {
            throw new NotFound(NotFoundReason.not_context, var1);
         }
      }
   }

   public String to_string(NameComponent[] var1) throws InvalidName {
      if (var1 != null && var1.length != 0) {
         String var3 = this.insImpl.convertToString(var1);
         if (var3 == null) {
            throw new InvalidName();
         } else {
            return var3;
         }
      } else {
         throw new InvalidName();
      }
   }

   public NameComponent[] to_name(String var1) throws InvalidName {
      if (var1 != null && var1.length() != 0) {
         NameComponent[] var3 = this.insImpl.convertToNameComponent(var1);
         if (var3 != null && var3.length != 0) {
            for(int var4 = 0; var4 < var3.length; ++var4) {
               if ((var3[var4].id == null || var3[var4].id.length() == 0) && (var3[var4].kind == null || var3[var4].kind.length() == 0)) {
                  throw new InvalidName();
               }
            }

            return var3;
         } else {
            throw new InvalidName();
         }
      } else {
         throw new InvalidName();
      }
   }

   public String to_url(String var1, String var2) throws InvalidAddress, InvalidName {
      if (var2 != null && var2.length() != 0) {
         if (var1 == null) {
            throw new InvalidAddress();
         } else {
            String var4 = null;
            var4 = this.insImpl.createURLBasedAddress(var1, var2);

            try {
               INSURLHandler.getINSURLHandler().parseURL(var4);
               return var4;
            } catch (BAD_PARAM var6) {
               throw new InvalidAddress();
            }
         }
      } else {
         throw new InvalidName();
      }
   }

   public Object resolve_str(String var1) throws NotFound, CannotProceed, InvalidName {
      Object var2 = null;
      if (var1 != null && var1.length() != 0) {
         NameComponent[] var4 = this.insImpl.convertToNameComponent(var1);
         if (var4 != null && var4.length != 0) {
            var2 = this.resolve(var4);
            return var2;
         } else {
            throw new InvalidName();
         }
      } else {
         throw new InvalidName();
      }
   }

   public static String nameToString(NameComponent[] var0) {
      StringBuffer var1 = new StringBuffer("{");
      if (var0 != null || var0.length > 0) {
         for(int var2 = 0; var2 < var0.length; ++var2) {
            if (var2 > 0) {
               var1.append(",");
            }

            var1.append("[").append(var0[var2].id).append(",").append(var0[var2].kind).append("]");
         }
      }

      var1.append("}");
      return var1.toString();
   }

   private static void dprint(String var0) {
      NamingUtils.dprint("NamingContextImpl(" + Thread.currentThread().getName() + " at " + System.currentTimeMillis() + " ems): " + var0);
   }
}
