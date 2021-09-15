package sun.security.jgss;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spi.MechanismFactory;

public class GSSManagerImpl extends GSSManager {
   private static final String USE_NATIVE_PROP = "sun.security.jgss.native";
   private static final Boolean USE_NATIVE = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         String var1 = System.getProperty("os.name");
         return !var1.startsWith("SunOS") && !var1.contains("OS X") && !var1.startsWith("Linux") ? Boolean.FALSE : new Boolean(System.getProperty("sun.security.jgss.native"));
      }
   });
   private ProviderList list;

   public GSSManagerImpl(GSSCaller var1, boolean var2) {
      this.list = new ProviderList(var1, var2);
   }

   public GSSManagerImpl(GSSCaller var1) {
      this.list = new ProviderList(var1, USE_NATIVE);
   }

   public GSSManagerImpl() {
      this.list = new ProviderList(GSSCaller.CALLER_UNKNOWN, USE_NATIVE);
   }

   public Oid[] getMechs() {
      return this.list.getMechs();
   }

   public Oid[] getNamesForMech(Oid var1) throws GSSException {
      MechanismFactory var2 = this.list.getMechFactory(var1);
      return (Oid[])var2.getNameTypes().clone();
   }

   public Oid[] getMechsForName(Oid var1) {
      Oid[] var2 = this.list.getMechs();
      Oid[] var3 = new Oid[var2.length];
      int var4 = 0;
      if (var1.equals(GSSNameImpl.oldHostbasedServiceName)) {
         var1 = GSSName.NT_HOSTBASED_SERVICE;
      }

      for(int var5 = 0; var5 < var2.length; ++var5) {
         Oid var6 = var2[var5];

         try {
            Oid[] var7 = this.getNamesForMech(var6);
            if (var1.containedIn(var7)) {
               var3[var4++] = var6;
            }
         } catch (GSSException var8) {
            GSSUtil.debug("Skip " + var6 + ": error retrieving supported name types");
         }
      }

      if (var4 < var3.length) {
         Oid[] var9 = new Oid[var4];

         for(int var10 = 0; var10 < var4; ++var10) {
            var9[var10] = var3[var10];
         }

         var3 = var9;
      }

      return var3;
   }

   public GSSName createName(String var1, Oid var2) throws GSSException {
      return new GSSNameImpl(this, var1, var2);
   }

   public GSSName createName(byte[] var1, Oid var2) throws GSSException {
      return new GSSNameImpl(this, var1, var2);
   }

   public GSSName createName(String var1, Oid var2, Oid var3) throws GSSException {
      return new GSSNameImpl(this, var1, var2, var3);
   }

   public GSSName createName(byte[] var1, Oid var2, Oid var3) throws GSSException {
      return new GSSNameImpl(this, var1, var2, var3);
   }

   public GSSCredential createCredential(int var1) throws GSSException {
      return new GSSCredentialImpl(this, var1);
   }

   public GSSCredential createCredential(GSSName var1, int var2, Oid var3, int var4) throws GSSException {
      return new GSSCredentialImpl(this, var1, var2, var3, var4);
   }

   public GSSCredential createCredential(GSSName var1, int var2, Oid[] var3, int var4) throws GSSException {
      return new GSSCredentialImpl(this, var1, var2, var3, var4);
   }

   public GSSContext createContext(GSSName var1, Oid var2, GSSCredential var3, int var4) throws GSSException {
      return new GSSContextImpl(this, var1, var2, var3, var4);
   }

   public GSSContext createContext(GSSCredential var1) throws GSSException {
      return new GSSContextImpl(this, var1);
   }

   public GSSContext createContext(byte[] var1) throws GSSException {
      return new GSSContextImpl(this, var1);
   }

   public void addProviderAtFront(Provider var1, Oid var2) throws GSSException {
      this.list.addProviderAtFront(var1, var2);
   }

   public void addProviderAtEnd(Provider var1, Oid var2) throws GSSException {
      this.list.addProviderAtEnd(var1, var2);
   }

   public GSSCredentialSpi getCredentialElement(GSSNameSpi var1, int var2, int var3, Oid var4, int var5) throws GSSException {
      MechanismFactory var6 = this.list.getMechFactory(var4);
      return var6.getCredentialElement(var1, var2, var3, var5);
   }

   public GSSNameSpi getNameElement(String var1, Oid var2, Oid var3) throws GSSException {
      MechanismFactory var4 = this.list.getMechFactory(var3);
      return var4.getNameElement(var1, var2);
   }

   public GSSNameSpi getNameElement(byte[] var1, Oid var2, Oid var3) throws GSSException {
      MechanismFactory var4 = this.list.getMechFactory(var3);
      return var4.getNameElement(var1, var2);
   }

   GSSContextSpi getMechanismContext(GSSNameSpi var1, GSSCredentialSpi var2, int var3, Oid var4) throws GSSException {
      Provider var5 = null;
      if (var2 != null) {
         var5 = var2.getProvider();
      }

      MechanismFactory var6 = this.list.getMechFactory(var4, var5);
      return var6.getMechanismContext(var1, var2, var3);
   }

   GSSContextSpi getMechanismContext(GSSCredentialSpi var1, Oid var2) throws GSSException {
      Provider var3 = null;
      if (var1 != null) {
         var3 = var1.getProvider();
      }

      MechanismFactory var4 = this.list.getMechFactory(var2, var3);
      return var4.getMechanismContext(var1);
   }

   GSSContextSpi getMechanismContext(byte[] var1) throws GSSException {
      if (var1 != null && var1.length != 0) {
         GSSContextSpi var2 = null;
         Oid[] var3 = this.list.getMechs();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            MechanismFactory var5 = this.list.getMechFactory(var3[var4]);
            if (var5.getProvider().getName().equals("SunNativeGSS")) {
               var2 = var5.getMechanismContext(var1);
               if (var2 != null) {
                  break;
               }
            }
         }

         if (var2 == null) {
            throw new GSSException(16);
         } else {
            return var2;
         }
      } else {
         throw new GSSException(12);
      }
   }
}
