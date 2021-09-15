package java.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.SocketPermission;
import java.net.URL;
import java.security.cert.CertPath;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import sun.misc.IOUtils;

public class CodeSource implements Serializable {
   private static final long serialVersionUID = 4977541819976013951L;
   private URL location;
   private transient CodeSigner[] signers = null;
   private transient java.security.cert.Certificate[] certs = null;
   private transient SocketPermission sp;
   private transient CertificateFactory factory = null;

   public CodeSource(URL var1, java.security.cert.Certificate[] var2) {
      this.location = var1;
      if (var2 != null) {
         this.certs = (java.security.cert.Certificate[])var2.clone();
      }

   }

   public CodeSource(URL var1, CodeSigner[] var2) {
      this.location = var1;
      if (var2 != null) {
         this.signers = (CodeSigner[])var2.clone();
      }

   }

   public int hashCode() {
      return this.location != null ? this.location.hashCode() : 0;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof CodeSource)) {
         return false;
      } else {
         CodeSource var2 = (CodeSource)var1;
         if (this.location == null) {
            if (var2.location != null) {
               return false;
            }
         } else if (!this.location.equals(var2.location)) {
            return false;
         }

         return this.matchCerts(var2, true);
      }
   }

   public final URL getLocation() {
      return this.location;
   }

   public final java.security.cert.Certificate[] getCertificates() {
      if (this.certs != null) {
         return (java.security.cert.Certificate[])this.certs.clone();
      } else if (this.signers == null) {
         return null;
      } else {
         ArrayList var1 = new ArrayList();

         for(int var2 = 0; var2 < this.signers.length; ++var2) {
            var1.addAll(this.signers[var2].getSignerCertPath().getCertificates());
         }

         this.certs = (java.security.cert.Certificate[])var1.toArray(new java.security.cert.Certificate[var1.size()]);
         return (java.security.cert.Certificate[])this.certs.clone();
      }
   }

   public final CodeSigner[] getCodeSigners() {
      if (this.signers != null) {
         return (CodeSigner[])this.signers.clone();
      } else if (this.certs != null) {
         this.signers = this.convertCertArrayToSignerArray(this.certs);
         return (CodeSigner[])this.signers.clone();
      } else {
         return null;
      }
   }

   public boolean implies(CodeSource var1) {
      if (var1 == null) {
         return false;
      } else {
         return this.matchCerts(var1, false) && this.matchLocation(var1);
      }
   }

   private boolean matchCerts(CodeSource var1, boolean var2) {
      if (this.certs == null && this.signers == null) {
         if (!var2) {
            return true;
         } else {
            return var1.certs == null && var1.signers == null;
         }
      } else {
         boolean var3;
         int var4;
         int var5;
         if (this.signers != null && var1.signers != null) {
            if (var2 && this.signers.length != var1.signers.length) {
               return false;
            } else {
               for(var4 = 0; var4 < this.signers.length; ++var4) {
                  var3 = false;

                  for(var5 = 0; var5 < var1.signers.length; ++var5) {
                     if (this.signers[var4].equals(var1.signers[var5])) {
                        var3 = true;
                        break;
                     }
                  }

                  if (!var3) {
                     return false;
                  }
               }

               return true;
            }
         } else if (this.certs != null && var1.certs != null) {
            if (var2 && this.certs.length != var1.certs.length) {
               return false;
            } else {
               for(var4 = 0; var4 < this.certs.length; ++var4) {
                  var3 = false;

                  for(var5 = 0; var5 < var1.certs.length; ++var5) {
                     if (this.certs[var4].equals(var1.certs[var5])) {
                        var3 = true;
                        break;
                     }
                  }

                  if (!var3) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   private boolean matchLocation(CodeSource var1) {
      if (this.location == null) {
         return true;
      } else if (var1 != null && var1.location != null) {
         if (this.location.equals(var1.location)) {
            return true;
         } else if (!this.location.getProtocol().equalsIgnoreCase(var1.location.getProtocol())) {
            return false;
         } else {
            int var2 = this.location.getPort();
            int var3;
            if (var2 != -1) {
               var3 = var1.location.getPort();
               int var4 = var3 != -1 ? var3 : var1.location.getDefaultPort();
               if (var2 != var4) {
                  return false;
               }
            }

            String var6;
            String var7;
            if (this.location.getFile().endsWith("/-")) {
               var6 = this.location.getFile().substring(0, this.location.getFile().length() - 1);
               if (!var1.location.getFile().startsWith(var6)) {
                  return false;
               }
            } else if (this.location.getFile().endsWith("/*")) {
               var3 = var1.location.getFile().lastIndexOf(47);
               if (var3 == -1) {
                  return false;
               }

               var7 = this.location.getFile().substring(0, this.location.getFile().length() - 1);
               String var5 = var1.location.getFile().substring(0, var3 + 1);
               if (!var5.equals(var7)) {
                  return false;
               }
            } else if (!var1.location.getFile().equals(this.location.getFile()) && !var1.location.getFile().equals(this.location.getFile() + "/")) {
               return false;
            }

            if (this.location.getRef() != null && !this.location.getRef().equals(var1.location.getRef())) {
               return false;
            } else {
               var6 = this.location.getHost();
               var7 = var1.location.getHost();
               if (var6 != null && (!"".equals(var6) && !"localhost".equals(var6) || !"".equals(var7) && !"localhost".equals(var7)) && !var6.equals(var7)) {
                  if (var7 == null) {
                     return false;
                  }

                  if (this.sp == null) {
                     this.sp = new SocketPermission(var6, "resolve");
                  }

                  if (var1.sp == null) {
                     var1.sp = new SocketPermission(var7, "resolve");
                  }

                  if (!this.sp.implies(var1.sp)) {
                     return false;
                  }
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("(");
      var1.append((Object)this.location);
      int var2;
      if (this.certs != null && this.certs.length > 0) {
         for(var2 = 0; var2 < this.certs.length; ++var2) {
            var1.append(" " + this.certs[var2]);
         }
      } else if (this.signers != null && this.signers.length > 0) {
         for(var2 = 0; var2 < this.signers.length; ++var2) {
            var1.append(" " + this.signers[var2]);
         }
      } else {
         var1.append(" <no signer certificates>");
      }

      var1.append(")");
      return var1.toString();
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

      if (this.signers != null && this.signers.length > 0) {
         var1.writeObject(this.signers);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      Hashtable var3 = null;
      ArrayList var4 = null;
      var1.defaultReadObject();
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
            } catch (CertificateException var13) {
               throw new ClassNotFoundException("Certificate factory for " + var7 + " not found");
            }

            var3.put(var7, var2);
         }

         byte[] var8 = IOUtils.readNBytes(var1, var1.readInt());
         ByteArrayInputStream var9 = new ByteArrayInputStream(var8);

         try {
            var4.add(var2.generateCertificate(var9));
         } catch (CertificateException var12) {
            throw new IOException(var12.getMessage());
         }

         var9.close();
      }

      if (var4 != null) {
         this.certs = (java.security.cert.Certificate[])var4.toArray(new java.security.cert.Certificate[var5]);
      }

      try {
         this.signers = (CodeSigner[])((CodeSigner[])((CodeSigner[])var1.readObject())).clone();
      } catch (IOException var11) {
      }

   }

   private CodeSigner[] convertCertArrayToSignerArray(java.security.cert.Certificate[] var1) {
      if (var1 == null) {
         return null;
      } else {
         try {
            if (this.factory == null) {
               this.factory = CertificateFactory.getInstance("X.509");
            }

            int var2 = 0;
            ArrayList var3 = new ArrayList();

            while(var2 < var1.length) {
               ArrayList var4 = new ArrayList();
               var4.add(var1[var2++]);

               int var5;
               for(var5 = var2; var5 < var1.length && var1[var5] instanceof X509Certificate && ((X509Certificate)var1[var5]).getBasicConstraints() != -1; ++var5) {
                  var4.add(var1[var5]);
               }

               var2 = var5;
               CertPath var6 = this.factory.generateCertPath((List)var4);
               var3.add(new CodeSigner(var6, (Timestamp)null));
            }

            if (var3.isEmpty()) {
               return null;
            } else {
               return (CodeSigner[])var3.toArray(new CodeSigner[var3.size()]);
            }
         } catch (CertificateException var7) {
            return null;
         }
      }
   }
}
