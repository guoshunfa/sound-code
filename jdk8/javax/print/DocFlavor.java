package javax.print;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

public class DocFlavor implements Serializable, Cloneable {
   private static final long serialVersionUID = -4512080796965449721L;
   public static final String hostEncoding = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("file.encoding")));
   private transient MimeType myMimeType;
   private String myClassName;
   private transient String myStringValue = null;

   public DocFlavor(String var1, String var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         this.myMimeType = new MimeType(var1);
         this.myClassName = var2;
      }
   }

   public String getMimeType() {
      return this.myMimeType.getMimeType();
   }

   public String getMediaType() {
      return this.myMimeType.getMediaType();
   }

   public String getMediaSubtype() {
      return this.myMimeType.getMediaSubtype();
   }

   public String getParameter(String var1) {
      return (String)this.myMimeType.getParameterMap().get(var1.toLowerCase());
   }

   public String getRepresentationClassName() {
      return this.myClassName;
   }

   public String toString() {
      return this.getStringValue();
   }

   public int hashCode() {
      return this.getStringValue().hashCode();
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof DocFlavor && this.getStringValue().equals(((DocFlavor)var1).getStringValue());
   }

   private String getStringValue() {
      if (this.myStringValue == null) {
         this.myStringValue = this.myMimeType + "; class=\"" + this.myClassName + "\"";
      }

      return this.myStringValue;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.myMimeType.getMimeType());
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();
      this.myMimeType = new MimeType((String)var1.readObject());
   }

   public static class SERVICE_FORMATTED extends DocFlavor {
      private static final long serialVersionUID = 6181337766266637256L;
      public static final DocFlavor.SERVICE_FORMATTED RENDERABLE_IMAGE = new DocFlavor.SERVICE_FORMATTED("java.awt.image.renderable.RenderableImage");
      public static final DocFlavor.SERVICE_FORMATTED PRINTABLE = new DocFlavor.SERVICE_FORMATTED("java.awt.print.Printable");
      public static final DocFlavor.SERVICE_FORMATTED PAGEABLE = new DocFlavor.SERVICE_FORMATTED("java.awt.print.Pageable");

      public SERVICE_FORMATTED(String var1) {
         super("application/x-java-jvm-local-objectref", var1);
      }
   }

   public static class READER extends DocFlavor {
      private static final long serialVersionUID = 7100295812579351567L;
      public static final DocFlavor.READER TEXT_PLAIN = new DocFlavor.READER("text/plain; charset=utf-16");
      public static final DocFlavor.READER TEXT_HTML = new DocFlavor.READER("text/html; charset=utf-16");

      public READER(String var1) {
         super(var1, "java.io.Reader");
      }
   }

   public static class STRING extends DocFlavor {
      private static final long serialVersionUID = 4414407504887034035L;
      public static final DocFlavor.STRING TEXT_PLAIN = new DocFlavor.STRING("text/plain; charset=utf-16");
      public static final DocFlavor.STRING TEXT_HTML = new DocFlavor.STRING("text/html; charset=utf-16");

      public STRING(String var1) {
         super(var1, "java.lang.String");
      }
   }

   public static class CHAR_ARRAY extends DocFlavor {
      private static final long serialVersionUID = -8720590903724405128L;
      public static final DocFlavor.CHAR_ARRAY TEXT_PLAIN = new DocFlavor.CHAR_ARRAY("text/plain; charset=utf-16");
      public static final DocFlavor.CHAR_ARRAY TEXT_HTML = new DocFlavor.CHAR_ARRAY("text/html; charset=utf-16");

      public CHAR_ARRAY(String var1) {
         super(var1, "[C");
      }
   }

   public static class URL extends DocFlavor {
      public static final DocFlavor.URL TEXT_PLAIN_HOST;
      public static final DocFlavor.URL TEXT_PLAIN_UTF_8;
      public static final DocFlavor.URL TEXT_PLAIN_UTF_16;
      public static final DocFlavor.URL TEXT_PLAIN_UTF_16BE;
      public static final DocFlavor.URL TEXT_PLAIN_UTF_16LE;
      public static final DocFlavor.URL TEXT_PLAIN_US_ASCII;
      public static final DocFlavor.URL TEXT_HTML_HOST;
      public static final DocFlavor.URL TEXT_HTML_UTF_8;
      public static final DocFlavor.URL TEXT_HTML_UTF_16;
      public static final DocFlavor.URL TEXT_HTML_UTF_16BE;
      public static final DocFlavor.URL TEXT_HTML_UTF_16LE;
      public static final DocFlavor.URL TEXT_HTML_US_ASCII;
      public static final DocFlavor.URL PDF;
      public static final DocFlavor.URL POSTSCRIPT;
      public static final DocFlavor.URL PCL;
      public static final DocFlavor.URL GIF;
      public static final DocFlavor.URL JPEG;
      public static final DocFlavor.URL PNG;
      public static final DocFlavor.URL AUTOSENSE;

      public URL(String var1) {
         super(var1, "java.net.URL");
      }

      static {
         TEXT_PLAIN_HOST = new DocFlavor.URL("text/plain; charset=" + hostEncoding);
         TEXT_PLAIN_UTF_8 = new DocFlavor.URL("text/plain; charset=utf-8");
         TEXT_PLAIN_UTF_16 = new DocFlavor.URL("text/plain; charset=utf-16");
         TEXT_PLAIN_UTF_16BE = new DocFlavor.URL("text/plain; charset=utf-16be");
         TEXT_PLAIN_UTF_16LE = new DocFlavor.URL("text/plain; charset=utf-16le");
         TEXT_PLAIN_US_ASCII = new DocFlavor.URL("text/plain; charset=us-ascii");
         TEXT_HTML_HOST = new DocFlavor.URL("text/html; charset=" + hostEncoding);
         TEXT_HTML_UTF_8 = new DocFlavor.URL("text/html; charset=utf-8");
         TEXT_HTML_UTF_16 = new DocFlavor.URL("text/html; charset=utf-16");
         TEXT_HTML_UTF_16BE = new DocFlavor.URL("text/html; charset=utf-16be");
         TEXT_HTML_UTF_16LE = new DocFlavor.URL("text/html; charset=utf-16le");
         TEXT_HTML_US_ASCII = new DocFlavor.URL("text/html; charset=us-ascii");
         PDF = new DocFlavor.URL("application/pdf");
         POSTSCRIPT = new DocFlavor.URL("application/postscript");
         PCL = new DocFlavor.URL("application/vnd.hp-PCL");
         GIF = new DocFlavor.URL("image/gif");
         JPEG = new DocFlavor.URL("image/jpeg");
         PNG = new DocFlavor.URL("image/png");
         AUTOSENSE = new DocFlavor.URL("application/octet-stream");
      }
   }

   public static class INPUT_STREAM extends DocFlavor {
      private static final long serialVersionUID = -7045842700749194127L;
      public static final DocFlavor.INPUT_STREAM TEXT_PLAIN_HOST;
      public static final DocFlavor.INPUT_STREAM TEXT_PLAIN_UTF_8;
      public static final DocFlavor.INPUT_STREAM TEXT_PLAIN_UTF_16;
      public static final DocFlavor.INPUT_STREAM TEXT_PLAIN_UTF_16BE;
      public static final DocFlavor.INPUT_STREAM TEXT_PLAIN_UTF_16LE;
      public static final DocFlavor.INPUT_STREAM TEXT_PLAIN_US_ASCII;
      public static final DocFlavor.INPUT_STREAM TEXT_HTML_HOST;
      public static final DocFlavor.INPUT_STREAM TEXT_HTML_UTF_8;
      public static final DocFlavor.INPUT_STREAM TEXT_HTML_UTF_16;
      public static final DocFlavor.INPUT_STREAM TEXT_HTML_UTF_16BE;
      public static final DocFlavor.INPUT_STREAM TEXT_HTML_UTF_16LE;
      public static final DocFlavor.INPUT_STREAM TEXT_HTML_US_ASCII;
      public static final DocFlavor.INPUT_STREAM PDF;
      public static final DocFlavor.INPUT_STREAM POSTSCRIPT;
      public static final DocFlavor.INPUT_STREAM PCL;
      public static final DocFlavor.INPUT_STREAM GIF;
      public static final DocFlavor.INPUT_STREAM JPEG;
      public static final DocFlavor.INPUT_STREAM PNG;
      public static final DocFlavor.INPUT_STREAM AUTOSENSE;

      public INPUT_STREAM(String var1) {
         super(var1, "java.io.InputStream");
      }

      static {
         TEXT_PLAIN_HOST = new DocFlavor.INPUT_STREAM("text/plain; charset=" + hostEncoding);
         TEXT_PLAIN_UTF_8 = new DocFlavor.INPUT_STREAM("text/plain; charset=utf-8");
         TEXT_PLAIN_UTF_16 = new DocFlavor.INPUT_STREAM("text/plain; charset=utf-16");
         TEXT_PLAIN_UTF_16BE = new DocFlavor.INPUT_STREAM("text/plain; charset=utf-16be");
         TEXT_PLAIN_UTF_16LE = new DocFlavor.INPUT_STREAM("text/plain; charset=utf-16le");
         TEXT_PLAIN_US_ASCII = new DocFlavor.INPUT_STREAM("text/plain; charset=us-ascii");
         TEXT_HTML_HOST = new DocFlavor.INPUT_STREAM("text/html; charset=" + hostEncoding);
         TEXT_HTML_UTF_8 = new DocFlavor.INPUT_STREAM("text/html; charset=utf-8");
         TEXT_HTML_UTF_16 = new DocFlavor.INPUT_STREAM("text/html; charset=utf-16");
         TEXT_HTML_UTF_16BE = new DocFlavor.INPUT_STREAM("text/html; charset=utf-16be");
         TEXT_HTML_UTF_16LE = new DocFlavor.INPUT_STREAM("text/html; charset=utf-16le");
         TEXT_HTML_US_ASCII = new DocFlavor.INPUT_STREAM("text/html; charset=us-ascii");
         PDF = new DocFlavor.INPUT_STREAM("application/pdf");
         POSTSCRIPT = new DocFlavor.INPUT_STREAM("application/postscript");
         PCL = new DocFlavor.INPUT_STREAM("application/vnd.hp-PCL");
         GIF = new DocFlavor.INPUT_STREAM("image/gif");
         JPEG = new DocFlavor.INPUT_STREAM("image/jpeg");
         PNG = new DocFlavor.INPUT_STREAM("image/png");
         AUTOSENSE = new DocFlavor.INPUT_STREAM("application/octet-stream");
      }
   }

   public static class BYTE_ARRAY extends DocFlavor {
      private static final long serialVersionUID = -9065578006593857475L;
      public static final DocFlavor.BYTE_ARRAY TEXT_PLAIN_HOST;
      public static final DocFlavor.BYTE_ARRAY TEXT_PLAIN_UTF_8;
      public static final DocFlavor.BYTE_ARRAY TEXT_PLAIN_UTF_16;
      public static final DocFlavor.BYTE_ARRAY TEXT_PLAIN_UTF_16BE;
      public static final DocFlavor.BYTE_ARRAY TEXT_PLAIN_UTF_16LE;
      public static final DocFlavor.BYTE_ARRAY TEXT_PLAIN_US_ASCII;
      public static final DocFlavor.BYTE_ARRAY TEXT_HTML_HOST;
      public static final DocFlavor.BYTE_ARRAY TEXT_HTML_UTF_8;
      public static final DocFlavor.BYTE_ARRAY TEXT_HTML_UTF_16;
      public static final DocFlavor.BYTE_ARRAY TEXT_HTML_UTF_16BE;
      public static final DocFlavor.BYTE_ARRAY TEXT_HTML_UTF_16LE;
      public static final DocFlavor.BYTE_ARRAY TEXT_HTML_US_ASCII;
      public static final DocFlavor.BYTE_ARRAY PDF;
      public static final DocFlavor.BYTE_ARRAY POSTSCRIPT;
      public static final DocFlavor.BYTE_ARRAY PCL;
      public static final DocFlavor.BYTE_ARRAY GIF;
      public static final DocFlavor.BYTE_ARRAY JPEG;
      public static final DocFlavor.BYTE_ARRAY PNG;
      public static final DocFlavor.BYTE_ARRAY AUTOSENSE;

      public BYTE_ARRAY(String var1) {
         super(var1, "[B");
      }

      static {
         TEXT_PLAIN_HOST = new DocFlavor.BYTE_ARRAY("text/plain; charset=" + hostEncoding);
         TEXT_PLAIN_UTF_8 = new DocFlavor.BYTE_ARRAY("text/plain; charset=utf-8");
         TEXT_PLAIN_UTF_16 = new DocFlavor.BYTE_ARRAY("text/plain; charset=utf-16");
         TEXT_PLAIN_UTF_16BE = new DocFlavor.BYTE_ARRAY("text/plain; charset=utf-16be");
         TEXT_PLAIN_UTF_16LE = new DocFlavor.BYTE_ARRAY("text/plain; charset=utf-16le");
         TEXT_PLAIN_US_ASCII = new DocFlavor.BYTE_ARRAY("text/plain; charset=us-ascii");
         TEXT_HTML_HOST = new DocFlavor.BYTE_ARRAY("text/html; charset=" + hostEncoding);
         TEXT_HTML_UTF_8 = new DocFlavor.BYTE_ARRAY("text/html; charset=utf-8");
         TEXT_HTML_UTF_16 = new DocFlavor.BYTE_ARRAY("text/html; charset=utf-16");
         TEXT_HTML_UTF_16BE = new DocFlavor.BYTE_ARRAY("text/html; charset=utf-16be");
         TEXT_HTML_UTF_16LE = new DocFlavor.BYTE_ARRAY("text/html; charset=utf-16le");
         TEXT_HTML_US_ASCII = new DocFlavor.BYTE_ARRAY("text/html; charset=us-ascii");
         PDF = new DocFlavor.BYTE_ARRAY("application/pdf");
         POSTSCRIPT = new DocFlavor.BYTE_ARRAY("application/postscript");
         PCL = new DocFlavor.BYTE_ARRAY("application/vnd.hp-PCL");
         GIF = new DocFlavor.BYTE_ARRAY("image/gif");
         JPEG = new DocFlavor.BYTE_ARRAY("image/jpeg");
         PNG = new DocFlavor.BYTE_ARRAY("image/png");
         AUTOSENSE = new DocFlavor.BYTE_ARRAY("application/octet-stream");
      }
   }
}
