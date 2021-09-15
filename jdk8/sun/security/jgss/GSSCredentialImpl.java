package sun.security.jgss;

import com.sun.security.jgss.ExtendedGSSCredential;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spnego.SpNegoCredElement;

public class GSSCredentialImpl implements ExtendedGSSCredential {
   private GSSManagerImpl gssManager;
   private boolean destroyed;
   private Hashtable<GSSCredentialImpl.SearchKey, GSSCredentialSpi> hashtable;
   private GSSCredentialSpi tempCred;

   GSSCredentialImpl(GSSManagerImpl var1, int var2) throws GSSException {
      this(var1, (GSSName)null, 0, (Oid[])((Oid[])null), var2);
   }

   GSSCredentialImpl(GSSManagerImpl var1, GSSName var2, int var3, Oid var4, int var5) throws GSSException {
      this.gssManager = null;
      this.destroyed = false;
      this.hashtable = null;
      this.tempCred = null;
      if (var4 == null) {
         var4 = ProviderList.DEFAULT_MECH_OID;
      }

      this.init(var1);
      this.add(var2, var3, var3, var4, var5);
   }

   GSSCredentialImpl(GSSManagerImpl var1, GSSName var2, int var3, Oid[] var4, int var5) throws GSSException {
      this.gssManager = null;
      this.destroyed = false;
      this.hashtable = null;
      this.tempCred = null;
      this.init(var1);
      boolean var6 = false;
      if (var4 == null) {
         var4 = var1.getMechs();
         var6 = true;
      }

      for(int var7 = 0; var7 < var4.length; ++var7) {
         try {
            this.add(var2, var3, var3, var4[var7], var5);
         } catch (GSSException var9) {
            if (!var6) {
               throw var9;
            }

            GSSUtil.debug("Ignore " + var9 + " while acquring cred for " + var4[var7]);
         }
      }

      if (this.hashtable.size() == 0 || var5 != this.getUsage()) {
         throw new GSSException(13);
      }
   }

   public GSSCredentialImpl(GSSManagerImpl var1, GSSCredentialSpi var2) throws GSSException {
      this.gssManager = null;
      this.destroyed = false;
      this.hashtable = null;
      this.tempCred = null;
      this.init(var1);
      byte var3 = 2;
      if (var2.isInitiatorCredential()) {
         if (var2.isAcceptorCredential()) {
            var3 = 0;
         } else {
            var3 = 1;
         }
      }

      GSSCredentialImpl.SearchKey var4 = new GSSCredentialImpl.SearchKey(var2.getMechanism(), var3);
      this.tempCred = var2;
      this.hashtable.put(var4, this.tempCred);
      if (!GSSUtil.isSpNegoMech(var2.getMechanism())) {
         var4 = new GSSCredentialImpl.SearchKey(GSSUtil.GSS_SPNEGO_MECH_OID, var3);
         this.hashtable.put(var4, new SpNegoCredElement(var2));
      }

   }

   void init(GSSManagerImpl var1) {
      this.gssManager = var1;
      this.hashtable = new Hashtable(var1.getMechs().length);
   }

   public void dispose() throws GSSException {
      if (!this.destroyed) {
         Enumeration var2 = this.hashtable.elements();

         while(var2.hasMoreElements()) {
            GSSCredentialSpi var1 = (GSSCredentialSpi)var2.nextElement();
            var1.dispose();
         }

         this.destroyed = true;
      }

   }

   public GSSCredential impersonate(GSSName var1) throws GSSException {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else {
         Oid var2 = this.tempCred.getMechanism();
         GSSNameSpi var3 = var1 == null ? null : ((GSSNameImpl)var1).getElement(var2);
         GSSCredentialSpi var4 = this.tempCred.impersonate(var3);
         return var4 == null ? null : new GSSCredentialImpl(this.gssManager, var4);
      }
   }

   public GSSName getName() throws GSSException {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else {
         return GSSNameImpl.wrapElement(this.gssManager, this.tempCred.getName());
      }
   }

   public GSSName getName(Oid var1) throws GSSException {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else {
         GSSCredentialImpl.SearchKey var2 = null;
         GSSCredentialSpi var3 = null;
         if (var1 == null) {
            var1 = ProviderList.DEFAULT_MECH_OID;
         }

         var2 = new GSSCredentialImpl.SearchKey(var1, 1);
         var3 = (GSSCredentialSpi)this.hashtable.get(var2);
         if (var3 == null) {
            var2 = new GSSCredentialImpl.SearchKey(var1, 2);
            var3 = (GSSCredentialSpi)this.hashtable.get(var2);
         }

         if (var3 == null) {
            var2 = new GSSCredentialImpl.SearchKey(var1, 0);
            var3 = (GSSCredentialSpi)this.hashtable.get(var2);
         }

         if (var3 == null) {
            throw new GSSExceptionImpl(2, var1);
         } else {
            return GSSNameImpl.wrapElement(this.gssManager, var3.getName());
         }
      }
   }

   public int getRemainingLifetime() throws GSSException {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else {
         boolean var3 = false;
         boolean var4 = false;
         boolean var5 = false;
         int var6 = Integer.MAX_VALUE;
         Enumeration var7 = this.hashtable.keys();

         while(var7.hasMoreElements()) {
            GSSCredentialImpl.SearchKey var1 = (GSSCredentialImpl.SearchKey)var7.nextElement();
            GSSCredentialSpi var2 = (GSSCredentialSpi)this.hashtable.get(var1);
            int var8;
            if (var1.getUsage() == 1) {
               var8 = var2.getInitLifetime();
            } else if (var1.getUsage() == 2) {
               var8 = var2.getAcceptLifetime();
            } else {
               int var9 = var2.getInitLifetime();
               int var10 = var2.getAcceptLifetime();
               var8 = var9 < var10 ? var9 : var10;
            }

            if (var6 > var8) {
               var6 = var8;
            }
         }

         return var6;
      }
   }

   public int getRemainingInitLifetime(Oid var1) throws GSSException {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else {
         GSSCredentialSpi var2 = null;
         GSSCredentialImpl.SearchKey var3 = null;
         boolean var4 = false;
         int var5 = 0;
         if (var1 == null) {
            var1 = ProviderList.DEFAULT_MECH_OID;
         }

         var3 = new GSSCredentialImpl.SearchKey(var1, 1);
         var2 = (GSSCredentialSpi)this.hashtable.get(var3);
         if (var2 != null) {
            var4 = true;
            if (var5 < var2.getInitLifetime()) {
               var5 = var2.getInitLifetime();
            }
         }

         var3 = new GSSCredentialImpl.SearchKey(var1, 0);
         var2 = (GSSCredentialSpi)this.hashtable.get(var3);
         if (var2 != null) {
            var4 = true;
            if (var5 < var2.getInitLifetime()) {
               var5 = var2.getInitLifetime();
            }
         }

         if (!var4) {
            throw new GSSExceptionImpl(2, var1);
         } else {
            return var5;
         }
      }
   }

   public int getRemainingAcceptLifetime(Oid var1) throws GSSException {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else {
         GSSCredentialSpi var2 = null;
         GSSCredentialImpl.SearchKey var3 = null;
         boolean var4 = false;
         int var5 = 0;
         if (var1 == null) {
            var1 = ProviderList.DEFAULT_MECH_OID;
         }

         var3 = new GSSCredentialImpl.SearchKey(var1, 2);
         var2 = (GSSCredentialSpi)this.hashtable.get(var3);
         if (var2 != null) {
            var4 = true;
            if (var5 < var2.getAcceptLifetime()) {
               var5 = var2.getAcceptLifetime();
            }
         }

         var3 = new GSSCredentialImpl.SearchKey(var1, 0);
         var2 = (GSSCredentialSpi)this.hashtable.get(var3);
         if (var2 != null) {
            var4 = true;
            if (var5 < var2.getAcceptLifetime()) {
               var5 = var2.getAcceptLifetime();
            }
         }

         if (!var4) {
            throw new GSSExceptionImpl(2, var1);
         } else {
            return var5;
         }
      }
   }

   public int getUsage() throws GSSException {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else {
         boolean var2 = false;
         boolean var3 = false;
         Enumeration var4 = this.hashtable.keys();

         while(var4.hasMoreElements()) {
            GSSCredentialImpl.SearchKey var1 = (GSSCredentialImpl.SearchKey)var4.nextElement();
            if (var1.getUsage() == 1) {
               var2 = true;
            } else {
               if (var1.getUsage() != 2) {
                  return 0;
               }

               var3 = true;
            }
         }

         if (var2) {
            if (var3) {
               return 0;
            } else {
               return 1;
            }
         } else {
            return 2;
         }
      }
   }

   public int getUsage(Oid var1) throws GSSException {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else {
         GSSCredentialSpi var2 = null;
         GSSCredentialImpl.SearchKey var3 = null;
         boolean var4 = false;
         boolean var5 = false;
         if (var1 == null) {
            var1 = ProviderList.DEFAULT_MECH_OID;
         }

         var3 = new GSSCredentialImpl.SearchKey(var1, 1);
         var2 = (GSSCredentialSpi)this.hashtable.get(var3);
         if (var2 != null) {
            var4 = true;
         }

         var3 = new GSSCredentialImpl.SearchKey(var1, 2);
         var2 = (GSSCredentialSpi)this.hashtable.get(var3);
         if (var2 != null) {
            var5 = true;
         }

         var3 = new GSSCredentialImpl.SearchKey(var1, 0);
         var2 = (GSSCredentialSpi)this.hashtable.get(var3);
         if (var2 != null) {
            var4 = true;
            var5 = true;
         }

         if (var4 && var5) {
            return 0;
         } else if (var4) {
            return 1;
         } else if (var5) {
            return 2;
         } else {
            throw new GSSExceptionImpl(2, var1);
         }
      }
   }

   public Oid[] getMechs() throws GSSException {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else {
         Vector var1 = new Vector(this.hashtable.size());
         Enumeration var2 = this.hashtable.keys();

         while(var2.hasMoreElements()) {
            GSSCredentialImpl.SearchKey var3 = (GSSCredentialImpl.SearchKey)var2.nextElement();
            var1.addElement(var3.getMech());
         }

         return (Oid[])var1.toArray(new Oid[0]);
      }
   }

   public void add(GSSName var1, int var2, int var3, Oid var4, int var5) throws GSSException {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else {
         if (var4 == null) {
            var4 = ProviderList.DEFAULT_MECH_OID;
         }

         GSSCredentialImpl.SearchKey var6 = new GSSCredentialImpl.SearchKey(var4, var5);
         if (this.hashtable.containsKey(var6)) {
            throw new GSSExceptionImpl(17, "Duplicate element found: " + getElementStr(var4, var5));
         } else {
            GSSNameSpi var7 = var1 == null ? null : ((GSSNameImpl)var1).getElement(var4);
            this.tempCred = this.gssManager.getCredentialElement(var7, var2, var3, var4, var5);
            if (this.tempCred != null) {
               if (var5 == 0 && (!this.tempCred.isAcceptorCredential() || !this.tempCred.isInitiatorCredential())) {
                  byte var8;
                  byte var9;
                  if (!this.tempCred.isInitiatorCredential()) {
                     var8 = 2;
                     var9 = 1;
                  } else {
                     var8 = 1;
                     var9 = 2;
                  }

                  var6 = new GSSCredentialImpl.SearchKey(var4, var8);
                  this.hashtable.put(var6, this.tempCred);
                  this.tempCred = this.gssManager.getCredentialElement(var7, var2, var3, var4, var9);
                  var6 = new GSSCredentialImpl.SearchKey(var4, var9);
                  this.hashtable.put(var6, this.tempCred);
               } else {
                  this.hashtable.put(var6, this.tempCred);
               }
            }

         }
      }
   }

   public boolean equals(Object var1) {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof GSSCredentialImpl) ? false : false;
      }
   }

   public int hashCode() {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else {
         return 1;
      }
   }

   public GSSCredentialSpi getElement(Oid var1, boolean var2) throws GSSException {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else {
         GSSCredentialImpl.SearchKey var3;
         GSSCredentialSpi var4;
         if (var1 == null) {
            var1 = ProviderList.DEFAULT_MECH_OID;
            var3 = new GSSCredentialImpl.SearchKey(var1, var2 ? 1 : 2);
            var4 = (GSSCredentialSpi)this.hashtable.get(var3);
            if (var4 == null) {
               var3 = new GSSCredentialImpl.SearchKey(var1, 0);
               var4 = (GSSCredentialSpi)this.hashtable.get(var3);
               if (var4 == null) {
                  Object[] var5 = this.hashtable.entrySet().toArray();

                  for(int var6 = 0; var6 < var5.length; ++var6) {
                     var4 = (GSSCredentialSpi)((Map.Entry)var5[var6]).getValue();
                     if (var4.isInitiatorCredential() == var2) {
                        break;
                     }
                  }
               }
            }
         } else {
            if (var2) {
               var3 = new GSSCredentialImpl.SearchKey(var1, 1);
            } else {
               var3 = new GSSCredentialImpl.SearchKey(var1, 2);
            }

            var4 = (GSSCredentialSpi)this.hashtable.get(var3);
            if (var4 == null) {
               var3 = new GSSCredentialImpl.SearchKey(var1, 0);
               var4 = (GSSCredentialSpi)this.hashtable.get(var3);
            }
         }

         if (var4 == null) {
            throw new GSSExceptionImpl(13, "No credential found for: " + getElementStr(var1, var2 ? 1 : 2));
         } else {
            return var4;
         }
      }
   }

   Set<GSSCredentialSpi> getElements() {
      HashSet var1 = new HashSet(this.hashtable.size());
      Enumeration var2 = this.hashtable.elements();

      while(var2.hasMoreElements()) {
         GSSCredentialSpi var3 = (GSSCredentialSpi)var2.nextElement();
         var1.add(var3);
      }

      return var1;
   }

   private static String getElementStr(Oid var0, int var1) {
      String var2 = var0.toString();
      if (var1 == 1) {
         var2 = var2.concat(" usage: Initiate");
      } else if (var1 == 2) {
         var2 = var2.concat(" usage: Accept");
      } else {
         var2 = var2.concat(" usage: Initiate and Accept");
      }

      return var2;
   }

   public String toString() {
      if (this.destroyed) {
         throw new IllegalStateException("This credential is no longer valid");
      } else {
         GSSCredentialSpi var1 = null;
         StringBuffer var2 = new StringBuffer("[GSSCredential: ");
         Object[] var3 = this.hashtable.entrySet().toArray();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            try {
               var2.append('\n');
               var1 = (GSSCredentialSpi)((Map.Entry)var3[var4]).getValue();
               var2.append((Object)var1.getName());
               var2.append(' ');
               var2.append((Object)var1.getMechanism());
               var2.append(var1.isInitiatorCredential() ? " Initiate" : "");
               var2.append(var1.isAcceptorCredential() ? " Accept" : "");
               var2.append(" [");
               var2.append((Object)var1.getClass());
               var2.append(']');
            } catch (GSSException var6) {
            }
         }

         var2.append(']');
         return var2.toString();
      }
   }

   static class SearchKey {
      private Oid mechOid = null;
      private int usage = 0;

      public SearchKey(Oid var1, int var2) {
         this.mechOid = var1;
         this.usage = var2;
      }

      public Oid getMech() {
         return this.mechOid;
      }

      public int getUsage() {
         return this.usage;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof GSSCredentialImpl.SearchKey)) {
            return false;
         } else {
            GSSCredentialImpl.SearchKey var2 = (GSSCredentialImpl.SearchKey)var1;
            return this.mechOid.equals(var2.mechOid) && this.usage == var2.usage;
         }
      }

      public int hashCode() {
         return this.mechOid.hashCode();
      }
   }
}
