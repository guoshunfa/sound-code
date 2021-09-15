package org.w3c.dom.xpath;

public class XPathException extends RuntimeException {
   public short code;
   public static final short INVALID_EXPRESSION_ERR = 1;
   public static final short TYPE_ERR = 2;

   public XPathException(short code, String message) {
      super(message);
      this.code = code;
   }
}
