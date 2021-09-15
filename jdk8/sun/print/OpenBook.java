package sun.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;

class OpenBook implements Pageable {
   private PageFormat mFormat;
   private Printable mPainter;

   OpenBook(PageFormat var1, Printable var2) {
      this.mFormat = var1;
      this.mPainter = var2;
   }

   public int getNumberOfPages() {
      return -1;
   }

   public PageFormat getPageFormat(int var1) {
      return this.mFormat;
   }

   public Printable getPrintable(int var1) throws IndexOutOfBoundsException {
      return this.mPainter;
   }
}
