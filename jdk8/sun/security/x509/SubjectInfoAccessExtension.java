package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class SubjectInfoAccessExtension extends Extension implements CertAttrSet<String> {
   public static final String IDENT = "x509.info.extensions.SubjectInfoAccess";
   public static final String NAME = "SubjectInfoAccess";
   public static final String DESCRIPTIONS = "descriptions";
   private List<AccessDescription> accessDescriptions;

   public SubjectInfoAccessExtension(List<AccessDescription> var1) throws IOException {
      this.extensionId = PKIXExtensions.SubjectInfoAccess_Id;
      this.critical = false;
      this.accessDescriptions = var1;
      this.encodeThis();
   }

   public SubjectInfoAccessExtension(Boolean var1, Object var2) throws IOException {
      this.extensionId = PKIXExtensions.SubjectInfoAccess_Id;
      this.critical = var1;
      if (!(var2 instanceof byte[])) {
         throw new IOException("Illegal argument type");
      } else {
         this.extensionValue = (byte[])((byte[])var2);
         DerValue var3 = new DerValue(this.extensionValue);
         if (var3.tag != 48) {
            throw new IOException("Invalid encoding for SubjectInfoAccessExtension.");
         } else {
            this.accessDescriptions = new ArrayList();

            while(var3.data.available() != 0) {
               DerValue var4 = var3.data.getDerValue();
               AccessDescription var5 = new AccessDescription(var4);
               this.accessDescriptions.add(var5);
            }

         }
      }
   }

   public List<AccessDescription> getAccessDescriptions() {
      return this.accessDescriptions;
   }

   public String getName() {
      return "SubjectInfoAccess";
   }

   public void encode(OutputStream var1) throws IOException {
      DerOutputStream var2 = new DerOutputStream();
      if (this.extensionValue == null) {
         this.extensionId = PKIXExtensions.SubjectInfoAccess_Id;
         this.critical = false;
         this.encodeThis();
      }

      super.encode(var2);
      var1.write(var2.toByteArray());
   }

   public void set(String var1, Object var2) throws IOException {
      if (var1.equalsIgnoreCase("descriptions")) {
         if (!(var2 instanceof List)) {
            throw new IOException("Attribute value should be of type List.");
         } else {
            this.accessDescriptions = (List)var2;
            this.encodeThis();
         }
      } else {
         throw new IOException("Attribute name [" + var1 + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
      }
   }

   public List<AccessDescription> get(String var1) throws IOException {
      if (var1.equalsIgnoreCase("descriptions")) {
         return this.accessDescriptions;
      } else {
         throw new IOException("Attribute name [" + var1 + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
      }
   }

   public void delete(String var1) throws IOException {
      if (var1.equalsIgnoreCase("descriptions")) {
         this.accessDescriptions = Collections.emptyList();
         this.encodeThis();
      } else {
         throw new IOException("Attribute name [" + var1 + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
      }
   }

   public Enumeration<String> getElements() {
      AttributeNameEnumeration var1 = new AttributeNameEnumeration();
      var1.addElement("descriptions");
      return var1.elements();
   }

   private void encodeThis() throws IOException {
      if (this.accessDescriptions.isEmpty()) {
         this.extensionValue = null;
      } else {
         DerOutputStream var1 = new DerOutputStream();
         Iterator var2 = this.accessDescriptions.iterator();

         while(var2.hasNext()) {
            AccessDescription var3 = (AccessDescription)var2.next();
            var3.encode(var1);
         }

         DerOutputStream var4 = new DerOutputStream();
         var4.write((byte)48, (DerOutputStream)var1);
         this.extensionValue = var4.toByteArray();
      }

   }

   public String toString() {
      return super.toString() + "SubjectInfoAccess [\n  " + this.accessDescriptions + "\n]\n";
   }
}
