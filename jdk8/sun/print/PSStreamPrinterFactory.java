package sun.print;

import java.io.OutputStream;
import javax.print.DocFlavor;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;

public class PSStreamPrinterFactory extends StreamPrintServiceFactory {
   static final String psMimeType = "application/postscript";
   static final DocFlavor[] supportedDocFlavors;

   public String getOutputFormat() {
      return "application/postscript";
   }

   public DocFlavor[] getSupportedDocFlavors() {
      return getFlavors();
   }

   static DocFlavor[] getFlavors() {
      DocFlavor[] var0 = new DocFlavor[supportedDocFlavors.length];
      System.arraycopy(supportedDocFlavors, 0, var0, 0, var0.length);
      return var0;
   }

   public StreamPrintService getPrintService(OutputStream var1) {
      return new PSStreamPrintService(var1);
   }

   static {
      supportedDocFlavors = new DocFlavor[]{DocFlavor.SERVICE_FORMATTED.PAGEABLE, DocFlavor.SERVICE_FORMATTED.PRINTABLE, DocFlavor.BYTE_ARRAY.GIF, DocFlavor.INPUT_STREAM.GIF, DocFlavor.URL.GIF, DocFlavor.BYTE_ARRAY.JPEG, DocFlavor.INPUT_STREAM.JPEG, DocFlavor.URL.JPEG, DocFlavor.BYTE_ARRAY.PNG, DocFlavor.INPUT_STREAM.PNG, DocFlavor.URL.PNG};
   }
}
