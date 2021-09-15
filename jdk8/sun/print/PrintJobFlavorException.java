package sun.print;

import javax.print.DocFlavor;
import javax.print.FlavorException;
import javax.print.PrintException;

class PrintJobFlavorException extends PrintException implements FlavorException {
   private DocFlavor flavor;

   PrintJobFlavorException(String var1, DocFlavor var2) {
      super(var1);
      this.flavor = var2;
   }

   public DocFlavor[] getUnsupportedFlavors() {
      DocFlavor[] var1 = new DocFlavor[]{this.flavor};
      return var1;
   }
}
