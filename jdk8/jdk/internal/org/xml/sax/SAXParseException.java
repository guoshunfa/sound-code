package jdk.internal.org.xml.sax;

public class SAXParseException extends SAXException {
   private String publicId;
   private String systemId;
   private int lineNumber;
   private int columnNumber;
   static final long serialVersionUID = -5651165872476709336L;

   public SAXParseException(String var1, Locator var2) {
      super(var1);
      if (var2 != null) {
         this.init(var2.getPublicId(), var2.getSystemId(), var2.getLineNumber(), var2.getColumnNumber());
      } else {
         this.init((String)null, (String)null, -1, -1);
      }

   }

   public SAXParseException(String var1, Locator var2, Exception var3) {
      super(var1, var3);
      if (var2 != null) {
         this.init(var2.getPublicId(), var2.getSystemId(), var2.getLineNumber(), var2.getColumnNumber());
      } else {
         this.init((String)null, (String)null, -1, -1);
      }

   }

   public SAXParseException(String var1, String var2, String var3, int var4, int var5) {
      super(var1);
      this.init(var2, var3, var4, var5);
   }

   public SAXParseException(String var1, String var2, String var3, int var4, int var5, Exception var6) {
      super(var1, var6);
      this.init(var2, var3, var4, var5);
   }

   private void init(String var1, String var2, int var3, int var4) {
      this.publicId = var1;
      this.systemId = var2;
      this.lineNumber = var3;
      this.columnNumber = var4;
   }

   public String getPublicId() {
      return this.publicId;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   public int getColumnNumber() {
      return this.columnNumber;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(this.getClass().getName());
      String var2 = this.getLocalizedMessage();
      if (this.publicId != null) {
         var1.append("publicId: ").append(this.publicId);
      }

      if (this.systemId != null) {
         var1.append("; systemId: ").append(this.systemId);
      }

      if (this.lineNumber != -1) {
         var1.append("; lineNumber: ").append(this.lineNumber);
      }

      if (this.columnNumber != -1) {
         var1.append("; columnNumber: ").append(this.columnNumber);
      }

      if (var2 != null) {
         var1.append("; ").append(var2);
      }

      return var1.toString();
   }
}
