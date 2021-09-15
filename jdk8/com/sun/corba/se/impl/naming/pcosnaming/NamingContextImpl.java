package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.impl.naming.cosnaming.InterOperableNamingImpl;
import com.sun.corba.se.impl.naming.cosnaming.NamingContextDataStore;
import com.sun.corba.se.impl.naming.cosnaming.NamingUtils;
import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;
import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.BindingIteratorHelper;
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
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.ServantRetentionPolicyValue;

public class NamingContextImpl extends NamingContextExtPOA implements NamingContextDataStore, Serializable {
   private transient ORB orb;
   private final String objKey;
   private final Hashtable theHashtable = new Hashtable();
   private transient NameService theNameServiceHandle;
   private transient ServantManagerImpl theServantManagerImplHandle;
   private transient InterOperableNamingImpl insImpl;
   private transient NamingSystemException readWrapper;
   private transient NamingSystemException updateWrapper;
   private static POA biPOA = null;
   private static boolean debug;

   public NamingContextImpl(ORB var1, String var2, NameService var3, ServantManagerImpl var4) throws Exception {
      this.orb = var1;
      this.readWrapper = NamingSystemException.get(var1, "naming.read");
      this.updateWrapper = NamingSystemException.get(var1, "naming.update");
      debug = true;
      this.objKey = var2;
      this.theNameServiceHandle = var3;
      this.theServantManagerImplHandle = var4;
      this.insImpl = new InterOperableNamingImpl();
   }

   InterOperableNamingImpl getINSImpl() {
      if (this.insImpl == null) {
         this.insImpl = new InterOperableNamingImpl();
      }

      return this.insImpl;
   }

   public void setRootNameService(NameService var1) {
      this.theNameServiceHandle = var1;
   }

   public void setORB(ORB var1) {
      this.orb = var1;
   }

   public void setServantManagerImpl(ServantManagerImpl var1) {
      this.theServantManagerImplHandle = var1;
   }

   public POA getNSPOA() {
      return this.theNameServiceHandle.getNSPOA();
   }

   public void bind(NameComponent[] var1, Object var2) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
      if (var2 == null) {
         throw this.updateWrapper.objectIsNull();
      } else {
         if (debug) {
            dprint("bind " + nameToString(var1) + " to " + var2);
         }

         this.doBind(this, var1, var2, false, BindingType.nobject);
      }
   }

   public void bind_context(NameComponent[] var1, NamingContext var2) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
      if (var2 == null) {
         throw this.updateWrapper.objectIsNull();
      } else {
         this.doBind(this, var1, var2, false, BindingType.ncontext);
      }
   }

   public void rebind(NameComponent[] var1, Object var2) throws NotFound, CannotProceed, InvalidName {
      if (var2 == null) {
         throw this.updateWrapper.objectIsNull();
      } else {
         try {
            if (debug) {
               dprint("rebind " + nameToString(var1) + " to " + var2);
            }

            this.doBind(this, var1, var2, true, BindingType.nobject);
         } catch (AlreadyBound var4) {
            throw this.updateWrapper.namingCtxRebindAlreadyBound((Throwable)var4);
         }
      }
   }

   public void rebind_context(NameComponent[] var1, NamingContext var2) throws NotFound, CannotProceed, InvalidName {
      try {
         if (debug) {
            dprint("rebind_context " + nameToString(var1) + " to " + var2);
         }

         this.doBind(this, var1, var2, true, BindingType.ncontext);
      } catch (AlreadyBound var4) {
         throw this.updateWrapper.namingCtxRebindAlreadyBound((Throwable)var4);
      }
   }

   public Object resolve(NameComponent[] var1) throws NotFound, CannotProceed, InvalidName {
      if (debug) {
         dprint("resolve " + nameToString(var1));
      }

      return doResolve(this, var1);
   }

   public void unbind(NameComponent[] var1) throws NotFound, CannotProceed, InvalidName {
      if (debug) {
         dprint("unbind " + nameToString(var1));
      }

      doUnbind(this, var1);
   }

   public void list(int var1, BindingListHolder var2, BindingIteratorHolder var3) {
      if (debug) {
         dprint("list(" + var1 + ")");
      }

      NamingContextImpl var4 = this;
      synchronized(this) {
         var4.List(var1, var2, var3);
      }

      if (debug && var2.value != null) {
         dprint("list(" + var1 + ") -> bindings[" + var2.value.length + "] + iterator: " + var3.value);
      }

   }

   public synchronized NamingContext new_context() {
      if (debug) {
         dprint("new_context()");
      }

      NamingContextImpl var1 = this;
      synchronized(this) {
         return var1.NewContext();
      }
   }

   public NamingContext bind_new_context(NameComponent[] var1) throws NotFound, AlreadyBound, CannotProceed, InvalidName {
      NamingContext var2 = null;
      NamingContext var3 = null;

      try {
         if (debug) {
            dprint("bind_new_context " + nameToString(var1));
         }

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

      return var3;
   }

   public void destroy() throws NotEmpty {
      if (debug) {
         dprint("destroy ");
      }

      NamingContextImpl var1 = this;
      synchronized(this) {
         if (var1.IsEmpty()) {
            var1.Destroy();
         } else {
            throw new NotEmpty();
         }
      }
   }

   private void doBind(NamingContextDataStore var1, NameComponent[] var2, Object var3, boolean var4, BindingType var5) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
      if (var2.length < 1) {
         throw new InvalidName();
      } else {
         if (var2.length == 1) {
            if (var2[0].id.length() == 0 && var2[0].kind.length() == 0) {
               throw new InvalidName();
            }

            synchronized(var1) {
               BindingTypeHolder var7 = new BindingTypeHolder();
               if (var4) {
                  Object var8 = var1.Resolve(var2[0], var7);
                  if (var8 != null) {
                     if (var7.value.value() == BindingType.nobject.value()) {
                        if (var5.value() == BindingType.ncontext.value()) {
                           throw new NotFound(NotFoundReason.not_context, var2);
                        }
                     } else if (var5.value() == BindingType.nobject.value()) {
                        throw new NotFound(NotFoundReason.not_object, var2);
                     }

                     var1.Unbind(var2[0]);
                  }
               } else if (var1.Resolve(var2[0], var7) != null) {
                  throw new AlreadyBound();
               }

               var1.Bind(var2[0], var3, var5);
            }
         } else {
            NamingContext var6 = resolveFirstAsContext(var1, var2);
            NameComponent[] var11 = new NameComponent[var2.length - 1];
            System.arraycopy(var2, 1, var11, 0, var2.length - 1);
            switch(var5.value()) {
            case 0:
               if (var4) {
                  var6.rebind(var11, var3);
               } else {
                  var6.bind(var11, var3);
               }
               break;
            case 1:
               NamingContext var12 = (NamingContext)var3;
               if (var4) {
                  var6.rebind_context(var11, var12);
               } else {
                  var6.bind_context(var11, var12);
               }
               break;
            default:
               throw this.updateWrapper.namingCtxBadBindingtype();
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
         return var4.resolve(var5);
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

   public void Bind(NameComponent var1, Object var2, BindingType var3) {
      if (var2 != null) {
         InternalBindingKey var4 = new InternalBindingKey(var1);

         try {
            InternalBindingValue var5;
            if (var3.value() == 0) {
               var5 = new InternalBindingValue(var3, this.orb.object_to_string(var2));
               var5.setObjectRef(var2);
            } else {
               String var6 = this.theNameServiceHandle.getObjectKey(var2);
               var5 = new InternalBindingValue(var3, var6);
               var5.setObjectRef(var2);
            }

            InternalBindingValue var10 = (InternalBindingValue)this.theHashtable.put(var4, var5);
            if (var10 != null) {
               throw this.updateWrapper.namingCtxRebindAlreadyBound();
            } else {
               try {
                  this.theServantManagerImplHandle.updateContext(this.objKey, this);
               } catch (Exception var8) {
                  throw this.updateWrapper.bindUpdateContextFailed((Throwable)var8);
               }
            }
         } catch (Exception var9) {
            throw this.updateWrapper.bindFailure((Throwable)var9);
         }
      }
   }

   public Object Resolve(NameComponent var1, BindingTypeHolder var2) throws SystemException {
      if (var1.id.length() == 0 && var1.kind.length() == 0) {
         var2.value = BindingType.ncontext;
         return this.theNameServiceHandle.getObjectReferenceFromKey(this.objKey);
      } else {
         InternalBindingKey var3 = new InternalBindingKey(var1);
         InternalBindingValue var4 = (InternalBindingValue)this.theHashtable.get(var3);
         if (var4 == null) {
            return null;
         } else {
            Object var5 = null;
            var2.value = var4.theBindingType;

            try {
               if (var4.strObjectRef.startsWith("NC")) {
                  var2.value = BindingType.ncontext;
                  return this.theNameServiceHandle.getObjectReferenceFromKey(var4.strObjectRef);
               } else {
                  var5 = var4.getObjectRef();
                  if (var5 == null) {
                     try {
                        var5 = this.orb.string_to_object(var4.strObjectRef);
                        var4.setObjectRef(var5);
                     } catch (Exception var7) {
                        throw this.readWrapper.resolveConversionFailure(CompletionStatus.COMPLETED_MAYBE, var7);
                     }
                  }

                  return var5;
               }
            } catch (Exception var8) {
               throw this.readWrapper.resolveFailure(CompletionStatus.COMPLETED_MAYBE, var8);
            }
         }
      }
   }

   public Object Unbind(NameComponent var1) throws SystemException {
      try {
         InternalBindingKey var2 = new InternalBindingKey(var1);
         InternalBindingValue var3 = null;

         try {
            var3 = (InternalBindingValue)this.theHashtable.remove(var2);
         } catch (Exception var5) {
         }

         this.theServantManagerImplHandle.updateContext(this.objKey, this);
         if (var3 == null) {
            return null;
         } else {
            Object var4;
            if (var3.strObjectRef.startsWith("NC")) {
               this.theServantManagerImplHandle.readInContext(var3.strObjectRef);
               var4 = this.theNameServiceHandle.getObjectReferenceFromKey(var3.strObjectRef);
               return var4;
            } else {
               var4 = var3.getObjectRef();
               if (var4 == null) {
                  var4 = this.orb.string_to_object(var3.strObjectRef);
               }

               return var4;
            }
         }
      } catch (Exception var6) {
         throw this.updateWrapper.unbindFailure(CompletionStatus.COMPLETED_MAYBE, var6);
      }
   }

   public void List(int var1, BindingListHolder var2, BindingIteratorHolder var3) throws SystemException {
      if (biPOA == null) {
         this.createbiPOA();
      }

      try {
         PersistentBindingIterator var4 = new PersistentBindingIterator(this.orb, (Hashtable)this.theHashtable.clone(), biPOA);
         var4.list(var1, var2);
         byte[] var5 = biPOA.activate_object(var4);
         Object var6 = biPOA.id_to_reference(var5);
         BindingIterator var7 = BindingIteratorHelper.narrow(var6);
         var3.value = var7;
      } catch (SystemException var8) {
         throw var8;
      } catch (Exception var9) {
         throw this.readWrapper.transNcListGotExc((Throwable)var9);
      }
   }

   private synchronized void createbiPOA() {
      if (biPOA == null) {
         try {
            POA var1 = (POA)this.orb.resolve_initial_references("RootPOA");
            var1.the_POAManager().activate();
            byte var2 = 0;
            Policy[] var3 = new Policy[3];
            int var5 = var2 + 1;
            var3[var2] = var1.create_lifespan_policy(LifespanPolicyValue.TRANSIENT);
            var3[var5++] = var1.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID);
            var3[var5++] = var1.create_servant_retention_policy(ServantRetentionPolicyValue.RETAIN);
            biPOA = var1.create_POA("BindingIteratorPOA", (POAManager)null, var3);
            biPOA.the_POAManager().activate();
         } catch (Exception var4) {
            throw this.readWrapper.namingCtxBindingIteratorCreate((Throwable)var4);
         }
      }
   }

   public NamingContext NewContext() throws SystemException {
      try {
         return this.theNameServiceHandle.NewContext();
      } catch (SystemException var2) {
         throw var2;
      } catch (Exception var3) {
         throw this.updateWrapper.transNcNewctxGotExc((Throwable)var3);
      }
   }

   public void Destroy() throws SystemException {
   }

   public String to_string(NameComponent[] var1) throws InvalidName {
      if (var1 != null && var1.length != 0) {
         String var2 = this.getINSImpl().convertToString(var1);
         if (var2 == null) {
            throw new InvalidName();
         } else {
            return var2;
         }
      } else {
         throw new InvalidName();
      }
   }

   public NameComponent[] to_name(String var1) throws InvalidName {
      if (var1 != null && var1.length() != 0) {
         NameComponent[] var2 = this.getINSImpl().convertToNameComponent(var1);
         if (var2 != null && var2.length != 0) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               if ((var2[var3].id == null || var2[var3].id.length() == 0) && (var2[var3].kind == null || var2[var3].kind.length() == 0)) {
                  throw new InvalidName();
               }
            }

            return var2;
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
            String var3 = null;

            try {
               var3 = this.getINSImpl().createURLBasedAddress(var1, var2);
            } catch (Exception var6) {
               var3 = null;
            }

            try {
               INSURLHandler.getINSURLHandler().parseURL(var3);
               return var3;
            } catch (BAD_PARAM var5) {
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
         NameComponent[] var3 = this.getINSImpl().convertToNameComponent(var1);
         if (var3 != null && var3.length != 0) {
            var2 = this.resolve(var3);
            return var2;
         } else {
            throw new InvalidName();
         }
      } else {
         throw new InvalidName();
      }
   }

   public boolean IsEmpty() {
      return this.theHashtable.isEmpty();
   }

   public void printSize() {
      System.out.println("Hashtable Size = " + this.theHashtable.size());
      Enumeration var1 = this.theHashtable.keys();

      while(var1.hasMoreElements()) {
         InternalBindingValue var2 = (InternalBindingValue)this.theHashtable.get(var1.nextElement());
         if (var2 != null) {
            System.out.println("value = " + var2.strObjectRef);
         }
      }

   }
}
