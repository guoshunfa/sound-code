package java.awt.datatransfer;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Locale;

class MimeType implements Externalizable, Cloneable {
   static final long serialVersionUID = -6568722458793895906L;
   private String primaryType;
   private String subType;
   private MimeTypeParameterList parameters;
   private static final String TSPECIALS = "()<>@,;:\\\"/[]?=";

   public MimeType() {
   }

   public MimeType(String var1) throws MimeTypeParseException {
      this.parse(var1);
   }

   public MimeType(String var1, String var2) throws MimeTypeParseException {
      this(var1, var2, new MimeTypeParameterList());
   }

   public MimeType(String var1, String var2, MimeTypeParameterList var3) throws MimeTypeParseException {
      if (this.isValidToken(var1)) {
         this.primaryType = var1.toLowerCase(Locale.ENGLISH);
         if (this.isValidToken(var2)) {
            this.subType = var2.toLowerCase(Locale.ENGLISH);
            this.parameters = (MimeTypeParameterList)var3.clone();
         } else {
            throw new MimeTypeParseException("Sub type is invalid.");
         }
      } else {
         throw new MimeTypeParseException("Primary type is invalid.");
      }
   }

   public int hashCode() {
      byte var1 = 0;
      int var2 = var1 + this.primaryType.hashCode();
      var2 += this.subType.hashCode();
      var2 += this.parameters.hashCode();
      return var2;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof MimeType)) {
         return false;
      } else {
         MimeType var2 = (MimeType)var1;
         boolean var3 = this.primaryType.equals(var2.primaryType) && this.subType.equals(var2.subType) && this.parameters.equals(var2.parameters);
         return var3;
      }
   }

   private void parse(String var1) throws MimeTypeParseException {
      int var2 = var1.indexOf(47);
      int var3 = var1.indexOf(59);
      if (var2 < 0 && var3 < 0) {
         throw new MimeTypeParseException("Unable to find a sub type.");
      } else if (var2 < 0 && var3 >= 0) {
         throw new MimeTypeParseException("Unable to find a sub type.");
      } else {
         if (var2 >= 0 && var3 < 0) {
            this.primaryType = var1.substring(0, var2).trim().toLowerCase(Locale.ENGLISH);
            this.subType = var1.substring(var2 + 1).trim().toLowerCase(Locale.ENGLISH);
            this.parameters = new MimeTypeParameterList();
         } else {
            if (var2 >= var3) {
               throw new MimeTypeParseException("Unable to find a sub type.");
            }

            this.primaryType = var1.substring(0, var2).trim().toLowerCase(Locale.ENGLISH);
            this.subType = var1.substring(var2 + 1, var3).trim().toLowerCase(Locale.ENGLISH);
            this.parameters = new MimeTypeParameterList(var1.substring(var3));
         }

         if (!this.isValidToken(this.primaryType)) {
            throw new MimeTypeParseException("Primary type is invalid.");
         } else if (!this.isValidToken(this.subType)) {
            throw new MimeTypeParseException("Sub type is invalid.");
         }
      }
   }

   public String getPrimaryType() {
      return this.primaryType;
   }

   public String getSubType() {
      return this.subType;
   }

   public MimeTypeParameterList getParameters() {
      return (MimeTypeParameterList)this.parameters.clone();
   }

   public String getParameter(String var1) {
      return this.parameters.get(var1);
   }

   public void setParameter(String var1, String var2) {
      this.parameters.set(var1, var2);
   }

   public void removeParameter(String var1) {
      this.parameters.remove(var1);
   }

   public String toString() {
      return this.getBaseType() + this.parameters.toString();
   }

   public String getBaseType() {
      return this.primaryType + "/" + this.subType;
   }

   public boolean match(MimeType var1) {
      if (var1 == null) {
         return false;
      } else {
         return this.primaryType.equals(var1.getPrimaryType()) && (this.subType.equals("*") || var1.getSubType().equals("*") || this.subType.equals(var1.getSubType()));
      }
   }

   public boolean match(String var1) throws MimeTypeParseException {
      return var1 == null ? false : this.match(new MimeType(var1));
   }

   public void writeExternal(ObjectOutput var1) throws IOException {
      String var2 = this.toString();
      if (var2.length() <= 65535) {
         var1.writeUTF(var2);
      } else {
         var1.writeByte(0);
         var1.writeByte(0);
         var1.writeInt(var2.length());
         var1.write(var2.getBytes());
      }

   }

   public void readExternal(ObjectInput var1) throws IOException, ClassNotFoundException {
      String var2 = var1.readUTF();
      if (var2 == null || var2.length() == 0) {
         byte[] var3 = new byte[var1.readInt()];
         var1.readFully(var3);
         var2 = new String(var3);
      }

      try {
         this.parse(var2);
      } catch (MimeTypeParseException var4) {
         throw new IOException(var4.toString());
      }
   }

   public Object clone() {
      MimeType var1 = null;

      try {
         var1 = (MimeType)super.clone();
      } catch (CloneNotSupportedException var3) {
      }

      var1.parameters = (MimeTypeParameterList)this.parameters.clone();
      return var1;
   }

   private static boolean isTokenChar(char var0) {
      return var0 > ' ' && var0 < 127 && "()<>@,;:\\\"/[]?=".indexOf(var0) < 0;
   }

   private boolean isValidToken(String var1) {
      int var2 = var1.length();
      if (var2 > 0) {
         for(int var3 = 0; var3 < var2; ++var3) {
            char var4 = var1.charAt(var3);
            if (!isTokenChar(var4)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
