package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import javax.xml.transform.Result;

public class TXWResult implements Result {
   private String systemId;
   private TypedXmlWriter writer;

   public TXWResult(TypedXmlWriter writer) {
      this.writer = writer;
   }

   public TypedXmlWriter getWriter() {
      return this.writer;
   }

   public void setWriter(TypedXmlWriter writer) {
      this.writer = writer;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public void setSystemId(String systemId) {
      this.systemId = systemId;
   }
}
