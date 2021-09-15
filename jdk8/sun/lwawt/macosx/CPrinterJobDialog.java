package sun.lwawt.macosx;

import java.awt.Frame;
import java.awt.print.Pageable;

final class CPrinterJobDialog extends CPrinterDialog {
   private Pageable fPageable;
   private boolean fAllowPrintToFile;

   CPrinterJobDialog(Frame var1, CPrinterJob var2, Pageable var3, boolean var4) {
      super(var1, var2);
      this.fPageable = var3;
      this.fAllowPrintToFile = var4;
   }

   protected native boolean showDialog();
}
