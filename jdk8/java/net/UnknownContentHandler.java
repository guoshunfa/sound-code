package java.net;

import java.io.IOException;

class UnknownContentHandler extends ContentHandler {
   static final ContentHandler INSTANCE = new UnknownContentHandler();

   public Object getContent(URLConnection var1) throws IOException {
      return var1.getInputStream();
   }
}
