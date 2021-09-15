package com.sun.xml.internal.ws.util.xml;

public final class CDATA {
   private String _text;

   public CDATA(String text) {
      this._text = text;
   }

   public String getText() {
      return this._text;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (!(obj instanceof CDATA)) {
         return false;
      } else {
         CDATA cdata = (CDATA)obj;
         return this._text.equals(cdata._text);
      }
   }

   public int hashCode() {
      return this._text.hashCode();
   }
}
