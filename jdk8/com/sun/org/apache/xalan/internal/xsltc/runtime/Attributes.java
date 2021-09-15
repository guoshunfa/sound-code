package com.sun.org.apache.xalan.internal.xsltc.runtime;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import org.xml.sax.AttributeList;

public final class Attributes implements AttributeList {
   private int _element;
   private DOM _document;

   public Attributes(DOM document, int element) {
      this._element = element;
      this._document = document;
   }

   public int getLength() {
      return 0;
   }

   public String getName(int i) {
      return null;
   }

   public String getType(int i) {
      return null;
   }

   public String getType(String name) {
      return null;
   }

   public String getValue(int i) {
      return null;
   }

   public String getValue(String name) {
      return null;
   }
}
