package javax.xml.crypto;

import java.io.InputStream;

public class OctetStreamData implements Data {
   private InputStream octetStream;
   private String uri;
   private String mimeType;

   public OctetStreamData(InputStream var1) {
      if (var1 == null) {
         throw new NullPointerException("octetStream is null");
      } else {
         this.octetStream = var1;
      }
   }

   public OctetStreamData(InputStream var1, String var2, String var3) {
      if (var1 == null) {
         throw new NullPointerException("octetStream is null");
      } else {
         this.octetStream = var1;
         this.uri = var2;
         this.mimeType = var3;
      }
   }

   public InputStream getOctetStream() {
      return this.octetStream;
   }

   public String getURI() {
      return this.uri;
   }

   public String getMimeType() {
      return this.mimeType;
   }
}
