package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.corba.CorbaUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Iterator;
import javax.naming.Binding;
import javax.naming.CannotProceedException;
import javax.naming.CommunicationException;
import javax.naming.CompositeName;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.NamingManager;
import javax.naming.spi.ResolveResult;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public class CNCtx implements Context {
   private static final boolean debug = false;
   private static ORB _defaultOrb;
   ORB _orb;
   public NamingContext _nc;
   private NameComponent[] _name = null;
   Hashtable<String, Object> _env;
   static final CNNameParser parser = new CNNameParser();
   private static final String FED_PROP = "com.sun.jndi.cosnaming.federation";
   boolean federation = false;
   public static final boolean trustURLCodebase;
   OrbReuseTracker orbTracker = null;
   int enumCount;
   boolean isCloseCalled = false;

   private static synchronized ORB getDefaultOrb() {
      if (_defaultOrb == null) {
         _defaultOrb = CorbaUtils.getOrb((String)null, -1, new Hashtable());
      }

      return _defaultOrb;
   }

   CNCtx(Hashtable<?, ?> var1) throws NamingException {
      if (var1 != null) {
         var1 = (Hashtable)var1.clone();
      }

      this._env = var1;
      this.federation = "true".equals(var1 != null ? var1.get("com.sun.jndi.cosnaming.federation") : null);
      this.initOrbAndRootContext(var1);
   }

   private CNCtx() {
   }

   public static ResolveResult createUsingURL(String var0, Hashtable<?, ?> var1) throws NamingException {
      CNCtx var2 = new CNCtx();
      if (var1 != null) {
         var1 = (Hashtable)var1.clone();
      }

      var2._env = var1;
      String var3 = var2.initUsingUrl(var1 != null ? (ORB)var1.get("java.naming.corba.orb") : null, var0, var1);
      return new ResolveResult(var2, parser.parse(var3));
   }

   CNCtx(ORB var1, OrbReuseTracker var2, NamingContext var3, Hashtable<String, Object> var4, NameComponent[] var5) throws NamingException {
      if (var1 != null && var3 != null) {
         if (var1 != null) {
            this._orb = var1;
         } else {
            this._orb = getDefaultOrb();
         }

         this._nc = var3;
         this._env = var4;
         this._name = var5;
         this.federation = "true".equals(var4 != null ? var4.get("com.sun.jndi.cosnaming.federation") : null);
      } else {
         throw new ConfigurationException("Must supply ORB or NamingContext");
      }
   }

   NameComponent[] makeFullName(NameComponent[] var1) {
      if (this._name != null && this._name.length != 0) {
         NameComponent[] var2 = new NameComponent[this._name.length + var1.length];
         System.arraycopy(this._name, 0, var2, 0, this._name.length);
         System.arraycopy(var1, 0, var2, this._name.length, var1.length);
         return var2;
      } else {
         return var1;
      }
   }

   public String getNameInNamespace() throws NamingException {
      return this._name != null && this._name.length != 0 ? CNNameParser.cosNameToInsString(this._name) : "";
   }

   private static boolean isCorbaUrl(String var0) {
      return var0.startsWith("iiop://") || var0.startsWith("iiopname://") || var0.startsWith("corbaname:");
   }

   private void initOrbAndRootContext(Hashtable<?, ?> var1) throws NamingException {
      ORB var2 = null;
      String var3 = null;
      if (var2 == null && var1 != null) {
         var2 = (ORB)var1.get("java.naming.corba.orb");
      }

      if (var2 == null) {
         var2 = getDefaultOrb();
      }

      String var4 = null;
      if (var1 != null) {
         var4 = (String)var1.get("java.naming.provider.url");
      }

      if (var4 != null && !isCorbaUrl(var4)) {
         var3 = this.getStringifiedIor(var4);
         this.setOrbAndRootContext(var2, var3);
      } else if (var4 != null) {
         String var5 = this.initUsingUrl(var2, var4, var1);
         if (var5.length() > 0) {
            this._name = CNNameParser.nameToCosName(parser.parse(var5));

            try {
               org.omg.CORBA.Object var6 = this._nc.resolve(this._name);
               this._nc = NamingContextHelper.narrow(var6);
               if (this._nc == null) {
                  throw new ConfigurationException(var5 + " does not name a NamingContext");
               }
            } catch (BAD_PARAM var7) {
               throw new ConfigurationException(var5 + " does not name a NamingContext");
            } catch (Exception var8) {
               throw ExceptionMapper.mapException(var8, this, this._name);
            }
         }
      } else {
         this.setOrbAndRootContext(var2, (String)null);
      }

   }

   private String initUsingUrl(ORB var1, String var2, Hashtable<?, ?> var3) throws NamingException {
      return !var2.startsWith("iiop://") && !var2.startsWith("iiopname://") ? this.initUsingCorbanameUrl(var1, var2, var3) : this.initUsingIiopUrl(var1, var2, var3);
   }

   private String initUsingIiopUrl(ORB var1, String var2, Hashtable<?, ?> var3) throws NamingException {
      if (var1 == null) {
         var1 = getDefaultOrb();
      }

      try {
         IiopUrl var4 = new IiopUrl(var2);
         NamingException var5 = null;
         Iterator var6 = var4.getAddresses().iterator();

         while(var6.hasNext()) {
            IiopUrl.Address var7 = (IiopUrl.Address)var6.next();

            try {
               try {
                  String var8 = "corbaloc:iiop:" + var7.host + ":" + var7.port + "/NameService";
                  org.omg.CORBA.Object var9 = var1.string_to_object(var8);
                  this.setOrbAndRootContext(var1, var9);
                  return var4.getStringName();
               } catch (Exception var10) {
                  this.setOrbAndRootContext(var1, (String)null);
                  return var4.getStringName();
               }
            } catch (NamingException var11) {
               var5 = var11;
            }
         }

         if (var5 != null) {
            throw var5;
         } else {
            throw new ConfigurationException("Problem with URL: " + var2);
         }
      } catch (MalformedURLException var12) {
         throw new ConfigurationException(var12.getMessage());
      }
   }

   private String initUsingCorbanameUrl(ORB var1, String var2, Hashtable<?, ?> var3) throws NamingException {
      if (var1 == null) {
         var1 = getDefaultOrb();
      }

      try {
         CorbanameUrl var4 = new CorbanameUrl(var2);
         String var5 = var4.getLocation();
         String var6 = var4.getStringName();
         this.setOrbAndRootContext(var1, var5);
         return var4.getStringName();
      } catch (MalformedURLException var7) {
         throw new ConfigurationException(var7.getMessage());
      }
   }

   private void setOrbAndRootContext(ORB var1, String var2) throws NamingException {
      this._orb = var1;

      ConfigurationException var4;
      try {
         org.omg.CORBA.Object var3;
         if (var2 != null) {
            var3 = this._orb.string_to_object(var2);
         } else {
            var3 = this._orb.resolve_initial_references("NameService");
         }

         this._nc = NamingContextHelper.narrow(var3);
         if (this._nc == null) {
            if (var2 != null) {
               throw new ConfigurationException("Cannot convert IOR to a NamingContext: " + var2);
            } else {
               throw new ConfigurationException("ORB.resolve_initial_references(\"NameService\") does not return a NamingContext");
            }
         }
      } catch (InvalidName var5) {
         var4 = new ConfigurationException("COS Name Service not registered with ORB under the name 'NameService'");
         var4.setRootCause(var5);
         throw var4;
      } catch (COMM_FAILURE var6) {
         CommunicationException var9 = new CommunicationException("Cannot connect to ORB");
         var9.setRootCause(var6);
         throw var9;
      } catch (BAD_PARAM var7) {
         var4 = new ConfigurationException("Invalid URL or IOR: " + var2);
         var4.setRootCause(var7);
         throw var4;
      } catch (INV_OBJREF var8) {
         var4 = new ConfigurationException("Invalid object reference: " + var2);
         var4.setRootCause(var8);
         throw var4;
      }
   }

   private void setOrbAndRootContext(ORB var1, org.omg.CORBA.Object var2) throws NamingException {
      this._orb = var1;

      try {
         this._nc = NamingContextHelper.narrow(var2);
         if (this._nc == null) {
            throw new ConfigurationException("Cannot convert object reference to NamingContext: " + var2);
         }
      } catch (COMM_FAILURE var5) {
         CommunicationException var4 = new CommunicationException("Cannot connect to ORB");
         var4.setRootCause(var5);
         throw var4;
      }
   }

   private String getStringifiedIor(String var1) throws NamingException {
      if (!var1.startsWith("IOR:") && !var1.startsWith("corbaloc:")) {
         InputStream var2 = null;
         boolean var17 = false;

         ConfigurationException var4;
         label129: {
            String var6;
            try {
               var17 = true;
               URL var3 = new URL(var1);
               var2 = var3.openStream();
               if (var2 == null) {
                  var17 = false;
                  break label129;
               }

               BufferedReader var23 = new BufferedReader(new InputStreamReader(var2, "8859_1"));

               while(true) {
                  String var5;
                  if ((var5 = var23.readLine()) == null) {
                     var17 = false;
                     break label129;
                  }

                  if (var5.startsWith("IOR:")) {
                     var6 = var5;
                     var17 = false;
                     break;
                  }
               }
            } catch (IOException var21) {
               var4 = new ConfigurationException("Invalid URL: " + var1);
               var4.setRootCause(var21);
               throw var4;
            } finally {
               if (var17) {
                  try {
                     if (var2 != null) {
                        var2.close();
                     }
                  } catch (IOException var19) {
                     ConfigurationException var11 = new ConfigurationException("Invalid URL: " + var1);
                     var11.setRootCause(var19);
                     throw var11;
                  }

               }
            }

            try {
               if (var2 != null) {
                  var2.close();
               }

               return var6;
            } catch (IOException var18) {
               ConfigurationException var8 = new ConfigurationException("Invalid URL: " + var1);
               var8.setRootCause(var18);
               throw var8;
            }
         }

         try {
            if (var2 != null) {
               var2.close();
            }
         } catch (IOException var20) {
            var4 = new ConfigurationException("Invalid URL: " + var1);
            var4.setRootCause(var20);
            throw var4;
         }

         throw new ConfigurationException(var1 + " does not contain an IOR");
      } else {
         return var1;
      }
   }

   Object callResolve(NameComponent[] var1) throws NamingException {
      try {
         org.omg.CORBA.Object var2 = this._nc.resolve(var1);

         try {
            NamingContext var3 = NamingContextHelper.narrow(var2);
            return var3 != null ? new CNCtx(this._orb, this.orbTracker, var3, this._env, this.makeFullName(var1)) : var2;
         } catch (SystemException var4) {
            return var2;
         }
      } catch (Exception var5) {
         throw ExceptionMapper.mapException(var5, this, var1);
      }
   }

   public Object lookup(String var1) throws NamingException {
      return this.lookup((Name)(new CompositeName(var1)));
   }

   public Object lookup(Name var1) throws NamingException {
      if (this._nc == null) {
         throw new ConfigurationException("Context does not have a corresponding NamingContext");
      } else if (var1.size() == 0) {
         return this;
      } else {
         NameComponent[] var2 = CNNameParser.nameToCosName(var1);
         Object var3 = null;

         try {
            var3 = this.callResolve(var2);

            try {
               if (CorbaUtils.isObjectFactoryTrusted(var3)) {
                  var3 = NamingManager.getObjectInstance(var3, var1, this, this._env);
               }

               return var3;
            } catch (NamingException var6) {
               throw var6;
            } catch (Exception var7) {
               NamingException var9 = new NamingException("problem generating object using object factory");
               var9.setRootCause(var7);
               throw var9;
            }
         } catch (CannotProceedException var8) {
            Context var5 = getContinuationContext(var8);
            return var5.lookup(var8.getRemainingName());
         }
      }
   }

   private void callBindOrRebind(NameComponent[] var1, Name var2, Object var3, boolean var4) throws NamingException {
      if (this._nc == null) {
         throw new ConfigurationException("Context does not have a corresponding NamingContext");
      } else {
         try {
            var3 = NamingManager.getStateToBind(var3, var2, this, this._env);
            if (var3 instanceof CNCtx) {
               var3 = ((CNCtx)var3)._nc;
            }

            if (var3 instanceof NamingContext) {
               NamingContext var5 = NamingContextHelper.narrow((org.omg.CORBA.Object)var3);
               if (var4) {
                  this._nc.rebind_context(var1, var5);
               } else {
                  this._nc.bind_context(var1, var5);
               }
            } else {
               if (!(var3 instanceof org.omg.CORBA.Object)) {
                  throw new IllegalArgumentException("Only instances of org.omg.CORBA.Object can be bound");
               }

               if (var4) {
                  this._nc.rebind(var1, (org.omg.CORBA.Object)var3);
               } else {
                  this._nc.bind(var1, (org.omg.CORBA.Object)var3);
               }
            }

         } catch (BAD_PARAM var7) {
            NotContextException var6 = new NotContextException(var2.toString());
            var6.setRootCause(var7);
            throw var6;
         } catch (Exception var8) {
            throw ExceptionMapper.mapException(var8, this, var1);
         }
      }
   }

   public void bind(Name var1, Object var2) throws NamingException {
      if (var1.size() == 0) {
         throw new InvalidNameException("Name is empty");
      } else {
         NameComponent[] var3 = CNNameParser.nameToCosName(var1);

         try {
            this.callBindOrRebind(var3, var1, var2, false);
         } catch (CannotProceedException var6) {
            Context var5 = getContinuationContext(var6);
            var5.bind(var6.getRemainingName(), var2);
         }

      }
   }

   private static Context getContinuationContext(CannotProceedException var0) throws NamingException {
      try {
         return NamingManager.getContinuationContext(var0);
      } catch (CannotProceedException var6) {
         Object var2 = var6.getResolvedObj();
         if (var2 instanceof Reference) {
            Reference var3 = (Reference)var2;
            RefAddr var4 = var3.get("nns");
            if (var4.getContent() instanceof Context) {
               NameNotFoundException var5 = new NameNotFoundException("No object reference bound for specified name");
               var5.setRootCause(var0.getRootCause());
               var5.setRemainingName(var0.getRemainingName());
               throw var5;
            }
         }

         throw var6;
      }
   }

   public void bind(String var1, Object var2) throws NamingException {
      this.bind((Name)(new CompositeName(var1)), var2);
   }

   public void rebind(Name var1, Object var2) throws NamingException {
      if (var1.size() == 0) {
         throw new InvalidNameException("Name is empty");
      } else {
         NameComponent[] var3 = CNNameParser.nameToCosName(var1);

         try {
            this.callBindOrRebind(var3, var1, var2, true);
         } catch (CannotProceedException var6) {
            Context var5 = getContinuationContext(var6);
            var5.rebind(var6.getRemainingName(), var2);
         }

      }
   }

   public void rebind(String var1, Object var2) throws NamingException {
      this.rebind((Name)(new CompositeName(var1)), var2);
   }

   private void callUnbind(NameComponent[] var1) throws NamingException {
      if (this._nc == null) {
         throw new ConfigurationException("Context does not have a corresponding NamingContext");
      } else {
         try {
            this._nc.unbind(var1);
         } catch (NotFound var3) {
            if (!this.leafNotFound(var3, var1[var1.length - 1])) {
               throw ExceptionMapper.mapException(var3, this, var1);
            }
         } catch (Exception var4) {
            throw ExceptionMapper.mapException(var4, this, var1);
         }

      }
   }

   private boolean leafNotFound(NotFound var1, NameComponent var2) {
      NameComponent var3;
      return var1.why.value() == 0 && var1.rest_of_name.length == 1 && (var3 = var1.rest_of_name[0]).id.equals(var2.id) && (var3.kind == var2.kind || var3.kind != null && var3.kind.equals(var2.kind));
   }

   public void unbind(String var1) throws NamingException {
      this.unbind((Name)(new CompositeName(var1)));
   }

   public void unbind(Name var1) throws NamingException {
      if (var1.size() == 0) {
         throw new InvalidNameException("Name is empty");
      } else {
         NameComponent[] var2 = CNNameParser.nameToCosName(var1);

         try {
            this.callUnbind(var2);
         } catch (CannotProceedException var5) {
            Context var4 = getContinuationContext(var5);
            var4.unbind(var5.getRemainingName());
         }

      }
   }

   public void rename(String var1, String var2) throws NamingException {
      this.rename((Name)(new CompositeName(var1)), (Name)(new CompositeName(var2)));
   }

   public void rename(Name var1, Name var2) throws NamingException {
      if (this._nc == null) {
         throw new ConfigurationException("Context does not have a corresponding NamingContext");
      } else if (var1.size() != 0 && var2.size() != 0) {
         Object var3 = this.lookup(var1);
         this.bind(var2, var3);
         this.unbind(var1);
      } else {
         throw new InvalidNameException("One or both names empty");
      }
   }

   public NamingEnumeration<NameClassPair> list(String var1) throws NamingException {
      return this.list((Name)(new CompositeName(var1)));
   }

   public NamingEnumeration<NameClassPair> list(Name var1) throws NamingException {
      return this.listBindings(var1);
   }

   public NamingEnumeration<Binding> listBindings(String var1) throws NamingException {
      return this.listBindings((Name)(new CompositeName(var1)));
   }

   public NamingEnumeration<Binding> listBindings(Name var1) throws NamingException {
      if (this._nc == null) {
         throw new ConfigurationException("Context does not have a corresponding NamingContext");
      } else if (var1.size() > 0) {
         try {
            Object var2 = this.lookup(var1);
            if (var2 instanceof CNCtx) {
               return new CNBindingEnumeration((CNCtx)var2, true, this._env);
            } else {
               throw new NotContextException(var1.toString());
            }
         } catch (NamingException var4) {
            throw var4;
         } catch (BAD_PARAM var5) {
            NotContextException var3 = new NotContextException(var1.toString());
            var3.setRootCause(var5);
            throw var3;
         }
      } else {
         return new CNBindingEnumeration(this, false, this._env);
      }
   }

   private void callDestroy(NamingContext var1) throws NamingException {
      if (this._nc == null) {
         throw new ConfigurationException("Context does not have a corresponding NamingContext");
      } else {
         try {
            var1.destroy();
         } catch (Exception var3) {
            throw ExceptionMapper.mapException(var3, this, (NameComponent[])null);
         }
      }
   }

   public void destroySubcontext(String var1) throws NamingException {
      this.destroySubcontext((Name)(new CompositeName(var1)));
   }

   public void destroySubcontext(Name var1) throws NamingException {
      if (this._nc == null) {
         throw new ConfigurationException("Context does not have a corresponding NamingContext");
      } else {
         NamingContext var2 = this._nc;
         NameComponent[] var3 = CNNameParser.nameToCosName(var1);
         if (var1.size() > 0) {
            try {
               Context var4 = (Context)this.callResolve(var3);
               CNCtx var10 = (CNCtx)var4;
               var2 = var10._nc;
               var10.close();
            } catch (ClassCastException var6) {
               throw new NotContextException(var1.toString());
            } catch (CannotProceedException var7) {
               Context var5 = getContinuationContext(var7);
               var5.destroySubcontext(var7.getRemainingName());
               return;
            } catch (NameNotFoundException var8) {
               if (var8.getRootCause() instanceof NotFound && this.leafNotFound((NotFound)var8.getRootCause(), var3[var3.length - 1])) {
                  return;
               }

               throw var8;
            } catch (NamingException var9) {
               throw var9;
            }
         }

         this.callDestroy(var2);
         this.callUnbind(var3);
      }
   }

   private Context callBindNewContext(NameComponent[] var1) throws NamingException {
      if (this._nc == null) {
         throw new ConfigurationException("Context does not have a corresponding NamingContext");
      } else {
         try {
            NamingContext var2 = this._nc.bind_new_context(var1);
            return new CNCtx(this._orb, this.orbTracker, var2, this._env, this.makeFullName(var1));
         } catch (Exception var3) {
            throw ExceptionMapper.mapException(var3, this, var1);
         }
      }
   }

   public Context createSubcontext(String var1) throws NamingException {
      return this.createSubcontext((Name)(new CompositeName(var1)));
   }

   public Context createSubcontext(Name var1) throws NamingException {
      if (var1.size() == 0) {
         throw new InvalidNameException("Name is empty");
      } else {
         NameComponent[] var2 = CNNameParser.nameToCosName(var1);

         try {
            return this.callBindNewContext(var2);
         } catch (CannotProceedException var5) {
            Context var4 = getContinuationContext(var5);
            return var4.createSubcontext(var5.getRemainingName());
         }
      }
   }

   public Object lookupLink(String var1) throws NamingException {
      return this.lookupLink((Name)(new CompositeName(var1)));
   }

   public Object lookupLink(Name var1) throws NamingException {
      return this.lookup(var1);
   }

   public NameParser getNameParser(String var1) throws NamingException {
      return parser;
   }

   public NameParser getNameParser(Name var1) throws NamingException {
      return parser;
   }

   public Hashtable<String, Object> getEnvironment() throws NamingException {
      return this._env == null ? new Hashtable(5, 0.75F) : (Hashtable)this._env.clone();
   }

   public String composeName(String var1, String var2) throws NamingException {
      return this.composeName((Name)(new CompositeName(var1)), (Name)(new CompositeName(var2))).toString();
   }

   public Name composeName(Name var1, Name var2) throws NamingException {
      Name var3 = (Name)var2.clone();
      return var3.addAll(var1);
   }

   public Object addToEnvironment(String var1, Object var2) throws NamingException {
      if (this._env == null) {
         this._env = new Hashtable(7, 0.75F);
      } else {
         this._env = (Hashtable)this._env.clone();
      }

      return this._env.put(var1, var2);
   }

   public Object removeFromEnvironment(String var1) throws NamingException {
      if (this._env != null && this._env.get(var1) != null) {
         this._env = (Hashtable)this._env.clone();
         return this._env.remove(var1);
      } else {
         return null;
      }
   }

   public synchronized void incEnumCount() {
      ++this.enumCount;
   }

   public synchronized void decEnumCount() throws NamingException {
      --this.enumCount;
      if (this.enumCount == 0 && this.isCloseCalled) {
         this.close();
      }

   }

   public synchronized void close() throws NamingException {
      if (this.enumCount > 0) {
         this.isCloseCalled = true;
      }
   }

   protected void finalize() {
      try {
         this.close();
      } catch (NamingException var2) {
      }

   }

   static {
      PrivilegedAction var0 = () -> {
         return System.getProperty("com.sun.jndi.cosnaming.object.trustURLCodebase", "false");
      };
      String var1 = (String)AccessController.doPrivileged(var0);
      trustURLCodebase = "true".equalsIgnoreCase(var1);
   }
}
