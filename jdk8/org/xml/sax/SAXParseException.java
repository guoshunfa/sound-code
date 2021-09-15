package org.xml.sax;

public class SAXParseException extends SAXException {
   private String publicId;
   private String systemId;
   private int lineNumber;
   private int columnNumber;
   static final long serialVersionUID = -5651165872476709336L;

   public SAXParseException(String message, Locator locator) {
      super(message);
      if (locator != null) {
         this.init(locator.getPublicId(), locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
      } else {
         this.init((String)null, (String)null, -1, -1);
      }

   }

   public SAXParseException(String message, Locator locator, Exception e) {
      super(message, e);
      if (locator != null) {
         this.init(locator.getPublicId(), locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
      } else {
         this.init((String)null, (String)null, -1, -1);
      }

   }

   public SAXParseException(String message, String publicId, String systemId, int lineNumber, int columnNumber) {
      super(message);
      this.init(publicId, systemId, lineNumber, columnNumber);
   }

   public SAXParseException(String message, String publicId, String systemId, int lineNumber, int columnNumber, Exception e) {
      super(message, e);
      this.init(publicId, systemId, lineNumber, columnNumber);
   }

   private void init(String publicId, String systemId, int lineNumber, int columnNumber) {
      this.publicId = publicId;
      this.systemId = systemId;
      this.lineNumber = lineNumber;
      this.columnNumber = columnNumber;
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
      StringBuilder buf = new StringBuilder(this.getClass().getName());
      String message = this.getLocalizedMessage();
      if (this.publicId != null) {
         buf.append("publicId: ").append(this.publicId);
      }

      if (this.systemId != null) {
         buf.append("; systemId: ").append(this.systemId);
      }

      if (this.lineNumber != -1) {
         buf.append("; lineNumber: ").append(this.lineNumber);
      }

      if (this.columnNumber != -1) {
         buf.append("; columnNumber: ").append(this.columnNumber);
      }

      if (message != null) {
         buf.append("; ").append(message);
      }

      return buf.toString();
   }
}
