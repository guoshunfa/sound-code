package javax.rmi.CORBA;

import com.sun.corba.se.impl.javax.rmi.CORBA.StubDelegateImpl;
import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public abstract class Stub extends ObjectImpl implements Serializable {
   private static final long serialVersionUID = 1087775603798577179L;
   private transient StubDelegate stubDelegate = null;
   private static Class stubDelegateClass = null;
   private static final String StubClassKey = "javax.rmi.CORBA.StubClass";

   public int hashCode() {
      if (this.stubDelegate == null) {
         this.setDefaultDelegate();
      }

      return this.stubDelegate != null ? this.stubDelegate.hashCode(this) : 0;
   }

   public boolean equals(Object var1) {
      if (this.stubDelegate == null) {
         this.setDefaultDelegate();
      }

      return this.stubDelegate != null ? this.stubDelegate.equals(this, var1) : false;
   }

   public String toString() {
      if (this.stubDelegate == null) {
         this.setDefaultDelegate();
      }

      if (this.stubDelegate != null) {
         String var1 = this.stubDelegate.toString(this);
         return var1 == null ? super.toString() : var1;
      } else {
         return super.toString();
      }
   }

   public void connect(ORB var1) throws RemoteException {
      if (this.stubDelegate == null) {
         this.setDefaultDelegate();
      }

      if (this.stubDelegate != null) {
         this.stubDelegate.connect(this, var1);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      if (this.stubDelegate == null) {
         this.setDefaultDelegate();
      }

      if (this.stubDelegate != null) {
         this.stubDelegate.readObject(this, var1);
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.stubDelegate == null) {
         this.setDefaultDelegate();
      }

      if (this.stubDelegate != null) {
         this.stubDelegate.writeObject(this, var1);
      }

   }

   private void setDefaultDelegate() {
      if (stubDelegateClass != null) {
         try {
            this.stubDelegate = (StubDelegate)stubDelegateClass.newInstance();
         } catch (Exception var2) {
         }
      }

   }

   private static Object createDelegate(String var0) {
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(var0)));
      if (var1 == null) {
         Properties var2 = getORBPropertiesFile();
         if (var2 != null) {
            var1 = var2.getProperty(var0);
         }
      }

      if (var1 == null) {
         return new StubDelegateImpl();
      } else {
         INITIALIZE var3;
         try {
            return loadDelegateClass(var1).newInstance();
         } catch (ClassNotFoundException var4) {
            var3 = new INITIALIZE("Cannot instantiate " + var1);
            var3.initCause(var4);
            throw var3;
         } catch (Exception var5) {
            var3 = new INITIALIZE("Error while instantiating" + var1);
            var3.initCause(var5);
            throw var3;
         }
      }
   }

   private static Class loadDelegateClass(String var0) throws ClassNotFoundException {
      try {
         ClassLoader var1 = Thread.currentThread().getContextClassLoader();
         return Class.forName(var0, false, var1);
      } catch (ClassNotFoundException var5) {
         try {
            return RMIClassLoader.loadClass(var0);
         } catch (MalformedURLException var4) {
            String var2 = "Could not load " + var0 + ": " + var4.toString();
            ClassNotFoundException var3 = new ClassNotFoundException(var2);
            throw var3;
         }
      }
   }

   private static Properties getORBPropertiesFile() {
      return (Properties)AccessController.doPrivileged((PrivilegedAction)(new GetORBPropertiesFileAction()));
   }

   static {
      Object var0 = createDelegate("javax.rmi.CORBA.StubClass");
      if (var0 != null) {
         stubDelegateClass = var0.getClass();
      }

   }
}
