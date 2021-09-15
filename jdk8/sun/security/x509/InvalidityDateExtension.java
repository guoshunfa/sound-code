package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class InvalidityDateExtension extends Extension implements CertAttrSet<String> {
   public static final String NAME = "InvalidityDate";
   public static final String DATE = "date";
   private Date date;

   private void encodeThis() throws IOException {
      if (this.date == null) {
         this.extensionValue = null;
      } else {
         DerOutputStream var1 = new DerOutputStream();
         var1.putGeneralizedTime(this.date);
         this.extensionValue = var1.toByteArray();
      }
   }

   public InvalidityDateExtension(Date var1) throws IOException {
      this(false, var1);
   }

   public InvalidityDateExtension(boolean var1, Date var2) throws IOException {
      this.extensionId = PKIXExtensions.InvalidityDate_Id;
      this.critical = var1;
      this.date = var2;
      this.encodeThis();
   }

   public InvalidityDateExtension(Boolean var1, Object var2) throws IOException {
      this.extensionId = PKIXExtensions.InvalidityDate_Id;
      this.critical = var1;
      this.extensionValue = (byte[])((byte[])var2);
      DerValue var3 = new DerValue(this.extensionValue);
      this.date = var3.getGeneralizedTime();
   }

   public void set(String var1, Object var2) throws IOException {
      if (!(var2 instanceof Date)) {
         throw new IOException("Attribute must be of type Date.");
      } else if (var1.equalsIgnoreCase("date")) {
         this.date = (Date)var2;
         this.encodeThis();
      } else {
         throw new IOException("Name not supported by InvalidityDateExtension");
      }
   }

   public Date get(String var1) throws IOException {
      if (var1.equalsIgnoreCase("date")) {
         return this.date == null ? null : new Date(this.date.getTime());
      } else {
         throw new IOException("Name not supported by InvalidityDateExtension");
      }
   }

   public void delete(String var1) throws IOException {
      if (var1.equalsIgnoreCase("date")) {
         this.date = null;
         this.encodeThis();
      } else {
         throw new IOException("Name not supported by InvalidityDateExtension");
      }
   }

   public String toString() {
      return super.toString() + "    Invalidity Date: " + this.date;
   }

   public void encode(OutputStream var1) throws IOException {
      DerOutputStream var2 = new DerOutputStream();
      if (this.extensionValue == null) {
         this.extensionId = PKIXExtensions.InvalidityDate_Id;
         this.critical = false;
         this.encodeThis();
      }

      super.encode(var2);
      var1.write(var2.toByteArray());
   }

   public Enumeration<String> getElements() {
      AttributeNameEnumeration var1 = new AttributeNameEnumeration();
      var1.addElement("date");
      return var1.elements();
   }

   public String getName() {
      return "InvalidityDate";
   }

   public static InvalidityDateExtension toImpl(java.security.cert.Extension var0) throws IOException {
      return var0 instanceof InvalidityDateExtension ? (InvalidityDateExtension)var0 : new InvalidityDateExtension(var0.isCritical(), var0.getValue());
   }
}
