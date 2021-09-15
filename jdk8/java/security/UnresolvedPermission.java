package java.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import sun.misc.IOUtils;
import sun.security.util.Debug;

public final class UnresolvedPermission extends Permission implements Serializable {
   private static final long serialVersionUID = -4821973115467008846L;
   private static final Debug debug = Debug.getInstance("policy,access", "UnresolvedPermission");
   private String type;
   private String name;
   private String actions;
   private transient java.security.cert.Certificate[] certs;
   private static final Class[] PARAMS0 = new Class[0];
   private static final Class[] PARAMS1 = new Class[]{String.class};
   private static final Class[] PARAMS2 = new Class[]{String.class, String.class};

   public UnresolvedPermission(String var1, String var2, String var3, java.security.cert.Certificate[] var4) {
      super(var1);
      if (var1 == null) {
         throw new NullPointerException("type can't be null");
      } else {
         this.type = var1;
         this.name = var2;
         this.actions = var3;
         if (var4 != null) {
            int var5;
            for(var5 = 0; var5 < var4.length; ++var5) {
               if (!(var4[var5] instanceof X509Certificate)) {
                  this.certs = (java.security.cert.Certificate[])var4.clone();
                  break;
               }
            }

            if (this.certs == null) {
               var5 = 0;

               int var6;
               for(var6 = 0; var5 < var4.length; ++var5) {
                  ++var6;

                  while(var5 + 1 < var4.length && ((X509Certificate)var4[var5]).getIssuerDN().equals(((X509Certificate)var4[var5 + 1]).getSubjectDN())) {
                     ++var5;
                  }
               }

               if (var6 == var4.length) {
                  this.certs = (java.security.cert.Certificate[])var4.clone();
               }

               if (this.certs == null) {
                  ArrayList var7 = new ArrayList();

                  for(var5 = 0; var5 < var4.length; ++var5) {
                     var7.add(var4[var5]);

                     while(var5 + 1 < var4.length && ((X509Certificate)var4[var5]).getIssuerDN().equals(((X509Certificate)var4[var5 + 1]).getSubjectDN())) {
                        ++var5;
                     }
                  }

                  this.certs = new java.security.cert.Certificate[var7.size()];
                  var7.toArray(this.certs);
               }
            }
         }

      }
   }

   Permission resolve(Permission var1, java.security.cert.Certificate[] var2) {
      if (this.certs != null) {
         if (var2 == null) {
            return null;
         }

         for(int var4 = 0; var4 < this.certs.length; ++var4) {
            boolean var3 = false;

            for(int var5 = 0; var5 < var2.length; ++var5) {
               if (this.certs[var4].equals(var2[var5])) {
                  var3 = true;
                  break;
               }
            }

            if (!var3) {
               return null;
            }
         }
      }

      try {
         Class var12 = var1.getClass();
         Constructor var13;
         Constructor var14;
         if (this.name == null && this.actions == null) {
            try {
               var13 = var12.getConstructor(PARAMS0);
               return (Permission)var13.newInstance();
            } catch (NoSuchMethodException var8) {
               try {
                  var14 = var12.getConstructor(PARAMS1);
                  return (Permission)var14.newInstance(this.name);
               } catch (NoSuchMethodException var7) {
                  Constructor var6 = var12.getConstructor(PARAMS2);
                  return (Permission)var6.newInstance(this.name, this.actions);
               }
            }
         } else if (this.name != null && this.actions == null) {
            try {
               var13 = var12.getConstructor(PARAMS1);
               return (Permission)var13.newInstance(this.name);
            } catch (NoSuchMethodException var9) {
               var14 = var12.getConstructor(PARAMS2);
               return (Permission)var14.newInstance(this.name, this.actions);
            }
         } else {
            var13 = var12.getConstructor(PARAMS2);
            return (Permission)var13.newInstance(this.name, this.actions);
         }
      } catch (NoSuchMethodException var10) {
         if (debug != null) {
            debug.println("NoSuchMethodException:\n  could not find proper constructor for " + this.type);
            var10.printStackTrace();
         }

         return null;
      } catch (Exception var11) {
         if (debug != null) {
            debug.println("unable to instantiate " + this.name);
            var11.printStackTrace();
         }

         return null;
      }
   }

   public boolean implies(Permission var1) {
      return false;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof UnresolvedPermission)) {
         return false;
      } else {
         UnresolvedPermission var2 = (UnresolvedPermission)var1;
         if (!this.type.equals(var2.type)) {
            return false;
         } else {
            if (this.name == null) {
               if (var2.name != null) {
                  return false;
               }
            } else if (!this.name.equals(var2.name)) {
               return false;
            }

            if (this.actions == null) {
               if (var2.actions != null) {
                  return false;
               }
            } else if (!this.actions.equals(var2.actions)) {
               return false;
            }

            if ((this.certs != null || var2.certs == null) && (this.certs == null || var2.certs != null) && (this.certs == null || var2.certs == null || this.certs.length == var2.certs.length)) {
               int var3;
               int var4;
               boolean var5;
               for(var3 = 0; this.certs != null && var3 < this.certs.length; ++var3) {
                  var5 = false;

                  for(var4 = 0; var4 < var2.certs.length; ++var4) {
                     if (this.certs[var3].equals(var2.certs[var4])) {
                        var5 = true;
                        break;
                     }
                  }

                  if (!var5) {
                     return false;
                  }
               }

               for(var3 = 0; var2.certs != null && var3 < var2.certs.length; ++var3) {
                  var5 = false;

                  for(var4 = 0; var4 < this.certs.length; ++var4) {
                     if (var2.certs[var3].equals(this.certs[var4])) {
                        var5 = true;
                        break;
                     }
                  }

                  if (!var5) {
                     return false;
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   public int hashCode() {
      int var1 = this.type.hashCode();
      if (this.name != null) {
         var1 ^= this.name.hashCode();
      }

      if (this.actions != null) {
         var1 ^= this.actions.hashCode();
      }

      return var1;
   }

   public String getActions() {
      return "";
   }

   public String getUnresolvedType() {
      return this.type;
   }

   public String getUnresolvedName() {
      return this.name;
   }

   public String getUnresolvedActions() {
      return this.actions;
   }

   public java.security.cert.Certificate[] getUnresolvedCerts() {
      return this.certs == null ? null : (java.security.cert.Certificate[])this.certs.clone();
   }

   public String toString() {
      return "(unresolved " + this.type + " " + this.name + " " + this.actions + ")";
   }

   public PermissionCollection newPermissionCollection() {
      return new UnresolvedPermissionCollection();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.certs != null && this.certs.length != 0) {
         var1.writeInt(this.certs.length);

         for(int var2 = 0; var2 < this.certs.length; ++var2) {
            java.security.cert.Certificate var3 = this.certs[var2];

            try {
               var1.writeUTF(var3.getType());
               byte[] var4 = var3.getEncoded();
               var1.writeInt(var4.length);
               var1.write(var4);
            } catch (CertificateEncodingException var5) {
               throw new IOException(var5.getMessage());
            }
         }
      } else {
         var1.writeInt(0);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      Hashtable var3 = null;
      ArrayList var4 = null;
      var1.defaultReadObject();
      if (this.type == null) {
         throw new NullPointerException("type can't be null");
      } else {
         int var5 = var1.readInt();
         if (var5 > 0) {
            var3 = new Hashtable(3);
            var4 = new ArrayList(var5 > 20 ? 20 : var5);
         } else if (var5 < 0) {
            throw new IOException("size cannot be negative");
         }

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var1.readUTF();
            CertificateFactory var2;
            if (var3.containsKey(var7)) {
               var2 = (CertificateFactory)var3.get(var7);
            } else {
               try {
                  var2 = CertificateFactory.getInstance(var7);
               } catch (CertificateException var12) {
                  throw new ClassNotFoundException("Certificate factory for " + var7 + " not found");
               }

               var3.put(var7, var2);
            }

            byte[] var8 = IOUtils.readNBytes(var1, var1.readInt());
            ByteArrayInputStream var9 = new ByteArrayInputStream(var8);

            try {
               var4.add(var2.generateCertificate(var9));
            } catch (CertificateException var11) {
               throw new IOException(var11.getMessage());
            }

            var9.close();
         }

         if (var4 != null) {
            this.certs = (java.security.cert.Certificate[])var4.toArray(new java.security.cert.Certificate[var5]);
         }

      }
   }
}
