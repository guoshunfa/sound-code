package com.sun.xml.internal.ws.developer;

import java.net.URL;
import javax.activation.DataSource;

public abstract class StreamingDataHandler extends com.sun.xml.internal.org.jvnet.staxex.StreamingDataHandler {
   private String hrefCid;

   public StreamingDataHandler(Object o, String s) {
      super(o, s);
   }

   public StreamingDataHandler(URL url) {
      super(url);
   }

   public StreamingDataHandler(DataSource dataSource) {
      super(dataSource);
   }

   public String getHrefCid() {
      return this.hrefCid;
   }

   public void setHrefCid(String cid) {
      this.hrefCid = cid;
   }
}
