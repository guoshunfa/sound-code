package com.sun.org.apache.xml.internal.serialize;

import java.io.OutputStream;
import java.io.Writer;

/** @deprecated */
public class XHTMLSerializer extends HTMLSerializer {
   public XHTMLSerializer() {
      super(true, new OutputFormat("xhtml", (String)null, false));
   }

   public XHTMLSerializer(OutputFormat format) {
      super(true, format != null ? format : new OutputFormat("xhtml", (String)null, false));
   }

   public XHTMLSerializer(Writer writer, OutputFormat format) {
      super(true, format != null ? format : new OutputFormat("xhtml", (String)null, false));
      this.setOutputCharStream(writer);
   }

   public XHTMLSerializer(OutputStream output, OutputFormat format) {
      super(true, format != null ? format : new OutputFormat("xhtml", (String)null, false));
      this.setOutputByteStream(output);
   }

   public void setOutputFormat(OutputFormat format) {
      super.setOutputFormat(format != null ? format : new OutputFormat("xhtml", (String)null, false));
   }
}
