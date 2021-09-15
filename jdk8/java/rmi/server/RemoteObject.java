package java.rmi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import sun.rmi.server.Util;
import sun.rmi.transport.ObjectTable;

public abstract class RemoteObject implements Remote, Serializable {
   protected transient RemoteRef ref;
   private static final long serialVersionUID = -3215090123894869218L;

   protected RemoteObject() {
      this.ref = null;
   }

   protected RemoteObject(RemoteRef var1) {
      this.ref = var1;
   }

   public RemoteRef getRef() {
      return this.ref;
   }

   public static Remote toStub(Remote var0) throws NoSuchObjectException {
      return !(var0 instanceof RemoteStub) && (var0 == null || !Proxy.isProxyClass(var0.getClass()) || !(Proxy.getInvocationHandler(var0) instanceof RemoteObjectInvocationHandler)) ? ObjectTable.getStub(var0) : var0;
   }

   public int hashCode() {
      return this.ref == null ? super.hashCode() : this.ref.remoteHashCode();
   }

   public boolean equals(Object var1) {
      if (var1 instanceof RemoteObject) {
         if (this.ref == null) {
            return var1 == this;
         } else {
            return this.ref.remoteEquals(((RemoteObject)var1).ref);
         }
      } else {
         return var1 != null ? var1.equals(this) : false;
      }
   }

   public String toString() {
      String var1 = Util.getUnqualifiedName(this.getClass());
      return this.ref == null ? var1 : var1 + "[" + this.ref.remoteToString() + "]";
   }

   private void writeObject(ObjectOutputStream var1) throws IOException, ClassNotFoundException {
      if (this.ref == null) {
         throw new MarshalException("Invalid remote object");
      } else {
         String var2 = this.ref.getRefClass(var1);
         if (var2 != null && var2.length() != 0) {
            var1.writeUTF(var2);
            this.ref.writeExternal(var1);
         } else {
            var1.writeUTF("");
            var1.writeObject(this.ref);
         }

      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      String var2 = var1.readUTF();
      if (var2 != null && var2.length() != 0) {
         String var3 = "sun.rmi.server." + var2;
         Class var4 = Class.forName(var3);

         try {
            this.ref = (RemoteRef)var4.newInstance();
         } catch (InstantiationException var6) {
            throw new ClassNotFoundException(var3, var6);
         } catch (IllegalAccessException var7) {
            throw new ClassNotFoundException(var3, var7);
         } catch (ClassCastException var8) {
            throw new ClassNotFoundException(var3, var8);
         }

         this.ref.readExternal(var1);
      } else {
         this.ref = (RemoteRef)var1.readObject();
      }

   }
}
