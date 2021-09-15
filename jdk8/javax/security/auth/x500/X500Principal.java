package javax.security.auth.x500;

import java.io.IOException;
import java.io.InputStream;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import sun.security.util.DerValue;
import sun.security.util.ResourcesMgr;
import sun.security.x509.X500Name;

public final class X500Principal implements Principal, Serializable {
   private static final long serialVersionUID = -500463348111345721L;
   public static final String RFC1779 = "RFC1779";
   public static final String RFC2253 = "RFC2253";
   public static final String CANONICAL = "CANONICAL";
   private transient X500Name thisX500Name;

   X500Principal(X500Name var1) {
      this.thisX500Name = var1;
   }

   public X500Principal(String var1) {
      this(var1, Collections.emptyMap());
   }

   public X500Principal(String var1, Map<String, String> var2) {
      if (var1 == null) {
         throw new NullPointerException(ResourcesMgr.getString("provided.null.name"));
      } else if (var2 == null) {
         throw new NullPointerException(ResourcesMgr.getString("provided.null.keyword.map"));
      } else {
         try {
            this.thisX500Name = new X500Name(var1, var2);
         } catch (Exception var5) {
            IllegalArgumentException var4 = new IllegalArgumentException("improperly specified input name: " + var1);
            var4.initCause(var5);
            throw var4;
         }
      }
   }

   public X500Principal(byte[] var1) {
      try {
         this.thisX500Name = new X500Name(var1);
      } catch (Exception var4) {
         IllegalArgumentException var3 = new IllegalArgumentException("improperly specified input name");
         var3.initCause(var4);
         throw var3;
      }
   }

   public X500Principal(InputStream var1) {
      if (var1 == null) {
         throw new NullPointerException("provided null input stream");
      } else {
         try {
            if (var1.markSupported()) {
               var1.mark(var1.available() + 1);
            }

            DerValue var2 = new DerValue(var1);
            this.thisX500Name = new X500Name(var2.data);
         } catch (Exception var6) {
            if (var1.markSupported()) {
               try {
                  var1.reset();
               } catch (IOException var5) {
                  IllegalArgumentException var4 = new IllegalArgumentException("improperly specified input stream and unable to reset input stream");
                  var4.initCause(var6);
                  throw var4;
               }
            }

            IllegalArgumentException var3 = new IllegalArgumentException("improperly specified input stream");
            var3.initCause(var6);
            throw var3;
         }
      }
   }

   public String getName() {
      return this.getName("RFC2253");
   }

   public String getName(String var1) {
      if (var1 != null) {
         if (var1.equalsIgnoreCase("RFC1779")) {
            return this.thisX500Name.getRFC1779Name();
         }

         if (var1.equalsIgnoreCase("RFC2253")) {
            return this.thisX500Name.getRFC2253Name();
         }

         if (var1.equalsIgnoreCase("CANONICAL")) {
            return this.thisX500Name.getRFC2253CanonicalName();
         }
      }

      throw new IllegalArgumentException("invalid format specified");
   }

   public String getName(String var1, Map<String, String> var2) {
      if (var2 == null) {
         throw new NullPointerException(ResourcesMgr.getString("provided.null.OID.map"));
      } else {
         if (var1 != null) {
            if (var1.equalsIgnoreCase("RFC1779")) {
               return this.thisX500Name.getRFC1779Name(var2);
            }

            if (var1.equalsIgnoreCase("RFC2253")) {
               return this.thisX500Name.getRFC2253Name(var2);
            }
         }

         throw new IllegalArgumentException("invalid format specified");
      }
   }

   public byte[] getEncoded() {
      try {
         return this.thisX500Name.getEncoded();
      } catch (IOException var2) {
         throw new RuntimeException("unable to get encoding", var2);
      }
   }

   public String toString() {
      return this.thisX500Name.toString();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof X500Principal)) {
         return false;
      } else {
         X500Principal var2 = (X500Principal)var1;
         return this.thisX500Name.equals(var2.thisX500Name);
      }
   }

   public int hashCode() {
      return this.thisX500Name.hashCode();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeObject(this.thisX500Name.getEncodedInternal());
   }

   private void readObject(ObjectInputStream var1) throws IOException, NotActiveException, ClassNotFoundException {
      this.thisX500Name = new X500Name((byte[])((byte[])var1.readObject()));
   }
}
