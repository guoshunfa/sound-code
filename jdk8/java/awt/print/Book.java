package java.awt.print;

import java.util.Vector;

public class Book implements Pageable {
   private Vector mPages = new Vector();

   public int getNumberOfPages() {
      return this.mPages.size();
   }

   public PageFormat getPageFormat(int var1) throws IndexOutOfBoundsException {
      return this.getPage(var1).getPageFormat();
   }

   public Printable getPrintable(int var1) throws IndexOutOfBoundsException {
      return this.getPage(var1).getPrintable();
   }

   public void setPage(int var1, Printable var2, PageFormat var3) throws IndexOutOfBoundsException {
      if (var2 == null) {
         throw new NullPointerException("painter is null");
      } else if (var3 == null) {
         throw new NullPointerException("page is null");
      } else {
         this.mPages.setElementAt(new Book.BookPage(var2, var3), var1);
      }
   }

   public void append(Printable var1, PageFormat var2) {
      this.mPages.addElement(new Book.BookPage(var1, var2));
   }

   public void append(Printable var1, PageFormat var2, int var3) {
      Book.BookPage var4 = new Book.BookPage(var1, var2);
      int var5 = this.mPages.size();
      int var6 = var5 + var3;
      this.mPages.setSize(var6);

      for(int var7 = var5; var7 < var6; ++var7) {
         this.mPages.setElementAt(var4, var7);
      }

   }

   private Book.BookPage getPage(int var1) throws ArrayIndexOutOfBoundsException {
      return (Book.BookPage)this.mPages.elementAt(var1);
   }

   private class BookPage {
      private PageFormat mFormat;
      private Printable mPainter;

      BookPage(Printable var2, PageFormat var3) {
         if (var2 != null && var3 != null) {
            this.mFormat = var3;
            this.mPainter = var2;
         } else {
            throw new NullPointerException();
         }
      }

      Printable getPrintable() {
         return this.mPainter;
      }

      PageFormat getPageFormat() {
         return this.mFormat;
      }
   }
}
