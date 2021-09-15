package sun.lwawt.macosx;

import java.awt.Frame;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

final class CPrinterPageDialog extends CPrinterDialog {
   private PageFormat fPage;
   private Printable fPainter;

   CPrinterPageDialog(Frame var1, CPrinterJob var2, PageFormat var3, Printable var4) {
      super(var1, var2);
      this.fPage = var3;
      this.fPainter = var4;
   }

   protected native boolean showDialog();
}
