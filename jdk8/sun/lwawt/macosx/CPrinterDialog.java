package sun.lwawt.macosx;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.LayoutManager;

public abstract class CPrinterDialog extends Dialog {
   private final CPrinterJob fPrinterJob;
   private boolean retval = false;

   CPrinterDialog(Frame var1, CPrinterJob var2) {
      super(var1, true);
      this.fPrinterJob = var2;
      this.setLayout((LayoutManager)null);
   }

   public void setRetVal(boolean var1) {
      this.retval = var1;
   }

   public boolean getRetVal() {
      return this.retval;
   }

   protected abstract boolean showDialog();
}
