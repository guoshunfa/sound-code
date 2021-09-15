package com.sun.jndi.rmi.registry;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.ConnectException;
import java.rmi.ConnectIOException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.StubNotFoundException;
import java.rmi.UnknownHostException;
import java.rmi.UnmarshalException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.SocketSecurityException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CommunicationException;
import javax.naming.CompositeName;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.naming.OperationNotSupportedException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.ServiceUnavailableException;
import javax.naming.StringRefAddr;
import javax.naming.spi.NamingManager;

public class RegistryContext implements Context, Referenceable {
   private Hashtable<String, Object> environment;
   private Registry registry;
   private String host;
   private int port;
   private static final NameParser nameParser = new AtomicNameParser();
   private static final String SOCKET_FACTORY = "com.sun.jndi.rmi.factory.socket";
   static final boolean trustURLCodebase;
   Reference reference = null;
   public static final String SECURITY_MGR = "java.naming.rmi.security.manager";

   public RegistryContext(String var1, int var2, Hashtable<?, ?> var3) throws NamingException {
      this.environment = var3 == null ? new Hashtable(5) : var3;
      if (this.environment.get("java.naming.rmi.security.manager") != null) {
         installSecurityMgr();
      }

      if (var1 != null && var1.charAt(0) == '[') {
         var1 = var1.substring(1, var1.length() - 1);
      }

      RMIClientSocketFactory var4 = (RMIClientSocketFactory)this.environment.get("com.sun.jndi.rmi.factory.socket");
      this.registry = getRegistry(var1, var2, var4);
      this.host = var1;
      this.port = var2;
   }

   RegistryContext(RegistryContext var1) {
      this.environment = (Hashtable)var1.environment.clone();
      this.registry = var1.registry;
      this.host = var1.host;
      this.port = var1.port;
      this.reference = var1.reference;
   }

   protected void finalize() {
      this.close();
   }

   public Object lookup(Name var1) throws NamingException {
      if (var1.isEmpty()) {
         return new RegistryContext(this);
      } else {
         Remote var2;
         try {
            var2 = this.registry.lookup(var1.get(0));
         } catch (NotBoundException var4) {
            throw new NameNotFoundException(var1.get(0));
         } catch (RemoteException var5) {
            throw (NamingException)wrapRemoteException(var5).fillInStackTrace();
         }

         return this.decodeObject(var2, var1.getPrefix(1));
      }
   }

   public Object lookup(String var1) throws NamingException {
      return this.lookup((Name)(new CompositeName(var1)));
   }

   public void bind(Name var1, Object var2) throws NamingException {
      if (var1.isEmpty()) {
         throw new InvalidNameException("RegistryContext: Cannot bind empty name");
      } else {
         try {
            this.registry.bind(var1.get(0), this.encodeObject(var2, var1.getPrefix(1)));
         } catch (AlreadyBoundException var5) {
            NameAlreadyBoundException var4 = new NameAlreadyBoundException(var1.get(0));
            var4.setRootCause(var5);
            throw var4;
         } catch (RemoteException var6) {
            throw (NamingException)wrapRemoteException(var6).fillInStackTrace();
         }
      }
   }

   public void bind(String var1, Object var2) throws NamingException {
      this.bind((Name)(new CompositeName(var1)), var2);
   }

   public void rebind(Name var1, Object var2) throws NamingException {
      if (var1.isEmpty()) {
         throw new InvalidNameException("RegistryContext: Cannot rebind empty name");
      } else {
         try {
            this.registry.rebind(var1.get(0), this.encodeObject(var2, var1.getPrefix(1)));
         } catch (RemoteException var4) {
            throw (NamingException)wrapRemoteException(var4).fillInStackTrace();
         }
      }
   }

   public void rebind(String var1, Object var2) throws NamingException {
      this.rebind((Name)(new CompositeName(var1)), var2);
   }

   public void unbind(Name var1) throws NamingException {
      if (var1.isEmpty()) {
         throw new InvalidNameException("RegistryContext: Cannot unbind empty name");
      } else {
         try {
            this.registry.unbind(var1.get(0));
         } catch (NotBoundException var3) {
         } catch (RemoteException var4) {
            throw (NamingException)wrapRemoteException(var4).fillInStackTrace();
         }

      }
   }

   public void unbind(String var1) throws NamingException {
      this.unbind((Name)(new CompositeName(var1)));
   }

   public void rename(Name var1, Name var2) throws NamingException {
      this.bind(var2, this.lookup(var1));
      this.unbind(var1);
   }

   public void rename(String var1, String var2) throws NamingException {
      this.rename((Name)(new CompositeName(var1)), (Name)(new CompositeName(var2)));
   }

   public NamingEnumeration<NameClassPair> list(Name var1) throws NamingException {
      if (!var1.isEmpty()) {
         throw new InvalidNameException("RegistryContext: can only list \"\"");
      } else {
         try {
            String[] var2 = this.registry.list();
            return new NameClassPairEnumeration(var2);
         } catch (RemoteException var3) {
            throw (NamingException)wrapRemoteException(var3).fillInStackTrace();
         }
      }
   }

   public NamingEnumeration<NameClassPair> list(String var1) throws NamingException {
      return this.list((Name)(new CompositeName(var1)));
   }

   public NamingEnumeration<Binding> listBindings(Name var1) throws NamingException {
      if (!var1.isEmpty()) {
         throw new InvalidNameException("RegistryContext: can only list \"\"");
      } else {
         try {
            String[] var2 = this.registry.list();
            return new BindingEnumeration(this, var2);
         } catch (RemoteException var3) {
            throw (NamingException)wrapRemoteException(var3).fillInStackTrace();
         }
      }
   }

   public NamingEnumeration<Binding> listBindings(String var1) throws NamingException {
      return this.listBindings((Name)(new CompositeName(var1)));
   }

   public void destroySubcontext(Name var1) throws NamingException {
      throw new OperationNotSupportedException();
   }

   public void destroySubcontext(String var1) throws NamingException {
      throw new OperationNotSupportedException();
   }

   public Context createSubcontext(Name var1) throws NamingException {
      throw new OperationNotSupportedException();
   }

   public Context createSubcontext(String var1) throws NamingException {
      throw new OperationNotSupportedException();
   }

   public Object lookupLink(Name var1) throws NamingException {
      return this.lookup(var1);
   }

   public Object lookupLink(String var1) throws NamingException {
      return this.lookup(var1);
   }

   public NameParser getNameParser(Name var1) throws NamingException {
      return nameParser;
   }

   public NameParser getNameParser(String var1) throws NamingException {
      return nameParser;
   }

   public Name composeName(Name var1, Name var2) throws NamingException {
      Name var3 = (Name)var2.clone();
      return var3.addAll(var1);
   }

   public String composeName(String var1, String var2) throws NamingException {
      return this.composeName((Name)(new CompositeName(var1)), (Name)(new CompositeName(var2))).toString();
   }

   public Object removeFromEnvironment(String var1) throws NamingException {
      return this.environment.remove(var1);
   }

   public Object addToEnvironment(String var1, Object var2) throws NamingException {
      if (var1.equals("java.naming.rmi.security.manager")) {
         installSecurityMgr();
      }

      return this.environment.put(var1, var2);
   }

   public Hashtable<String, Object> getEnvironment() throws NamingException {
      return (Hashtable)this.environment.clone();
   }

   public void close() {
      this.environment = null;
      this.registry = null;
   }

   public String getNameInNamespace() {
      return "";
   }

   public Reference getReference() throws NamingException {
      if (this.reference != null) {
         return (Reference)this.reference.clone();
      } else if (this.host != null && !this.host.equals("localhost")) {
         String var1 = "rmi://";
         var1 = this.host.indexOf(":") > -1 ? var1 + "[" + this.host + "]" : var1 + this.host;
         if (this.port > 0) {
            var1 = var1 + ":" + Integer.toString(this.port);
         }

         StringRefAddr var2 = new StringRefAddr("URL", var1);
         return new Reference(RegistryContext.class.getName(), var2, RegistryContextFactory.class.getName(), (String)null);
      } else {
         throw new ConfigurationException("Cannot create a reference for an RMI registry whose host was unspecified or specified as \"localhost\"");
      }
   }

   public static NamingException wrapRemoteException(RemoteException var0) {
      Object var1;
      if (var0 instanceof ConnectException) {
         var1 = new ServiceUnavailableException();
      } else if (var0 instanceof AccessException) {
         var1 = new NoPermissionException();
      } else if (!(var0 instanceof StubNotFoundException) && !(var0 instanceof UnknownHostException) && !(var0 instanceof SocketSecurityException)) {
         if (!(var0 instanceof ExportException) && !(var0 instanceof ConnectIOException) && !(var0 instanceof MarshalException) && !(var0 instanceof UnmarshalException) && !(var0 instanceof NoSuchObjectException)) {
            if (var0 instanceof ServerException && var0.detail instanceof RemoteException) {
               var1 = wrapRemoteException((RemoteException)var0.detail);
            } else {
               var1 = new NamingException();
            }
         } else {
            var1 = new CommunicationException();
         }
      } else {
         var1 = new ConfigurationException();
      }

      ((NamingException)var1).setRootCause(var0);
      return (NamingException)var1;
   }

   private static Registry getRegistry(String var0, int var1, RMIClientSocketFactory var2) throws NamingException {
      try {
         return var2 == null ? LocateRegistry.getRegistry(var0, var1) : LocateRegistry.getRegistry(var0, var1, var2);
      } catch (RemoteException var4) {
         throw (NamingException)wrapRemoteException(var4).fillInStackTrace();
      }
   }

   private static void installSecurityMgr() {
      try {
         System.setSecurityManager(new RMISecurityManager());
      } catch (Exception var1) {
      }

   }

   private Remote encodeObject(Object var1, Name var2) throws NamingException, RemoteException {
      var1 = NamingManager.getStateToBind(var1, var2, this, this.environment);
      if (var1 instanceof Remote) {
         return (Remote)var1;
      } else if (var1 instanceof Reference) {
         return new ReferenceWrapper((Reference)var1);
      } else if (var1 instanceof Referenceable) {
         return new ReferenceWrapper(((Referenceable)var1).getReference());
      } else {
         throw new IllegalArgumentException("RegistryContext: object to bind must be Remote, Reference, or Referenceable");
      }
   }

   private Object decodeObject(Remote var1, Name var2) throws NamingException {
      try {
         Object var3 = var1 instanceof RemoteReference ? ((RemoteReference)var1).getReference() : var1;
         Reference var8 = null;
         if (var3 instanceof Reference) {
            var8 = (Reference)var3;
         } else if (var3 instanceof Referenceable) {
            var8 = ((Referenceable)((Referenceable)var3)).getReference();
         }

         if (var8 != null && var8.getFactoryClassLocation() != null && !trustURLCodebase) {
            throw new ConfigurationException("The object factory is untrusted. Set the system property 'com.sun.jndi.rmi.object.trustURLCodebase' to 'true'.");
         } else {
            return NamingManager.getObjectInstance(var3, var2, this, this.environment);
         }
      } catch (NamingException var5) {
         throw var5;
      } catch (RemoteException var6) {
         throw (NamingException)wrapRemoteException(var6).fillInStackTrace();
      } catch (Exception var7) {
         NamingException var4 = new NamingException();
         var4.setRootCause(var7);
         throw var4;
      }
   }

   static {
      PrivilegedAction var0 = () -> {
         return System.getProperty("com.sun.jndi.rmi.object.trustURLCodebase", "false");
      };
      String var1 = (String)AccessController.doPrivileged(var0);
      trustURLCodebase = "true".equalsIgnoreCase(var1);
   }
}
