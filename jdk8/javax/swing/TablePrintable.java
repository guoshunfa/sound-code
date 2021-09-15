package javax.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

class TablePrintable implements Printable {
   private JTable table;
   private JTableHeader header;
   private TableColumnModel colModel;
   private int totalColWidth;
   private JTable.PrintMode printMode;
   private MessageFormat headerFormat;
   private MessageFormat footerFormat;
   private int last = -1;
   private int row = 0;
   private int col = 0;
   private final Rectangle clip = new Rectangle(0, 0, 0, 0);
   private final Rectangle hclip = new Rectangle(0, 0, 0, 0);
   private final Rectangle tempRect = new Rectangle(0, 0, 0, 0);
   private static final int H_F_SPACE = 8;
   private static final float HEADER_FONT_SIZE = 18.0F;
   private static final float FOOTER_FONT_SIZE = 12.0F;
   private Font headerFont;
   private Font footerFont;

   public TablePrintable(JTable var1, JTable.PrintMode var2, MessageFormat var3, MessageFormat var4) {
      this.table = var1;
      this.header = var1.getTableHeader();
      this.colModel = var1.getColumnModel();
      this.totalColWidth = this.colModel.getTotalColumnWidth();
      if (this.header != null) {
         this.hclip.height = this.header.getHeight();
      }

      this.printMode = var2;
      this.headerFormat = var3;
      this.footerFormat = var4;
      this.headerFont = var1.getFont().deriveFont(1, 18.0F);
      this.footerFont = var1.getFont().deriveFont(0, 12.0F);
   }

   public int print(Graphics var1, PageFormat var2, int var3) throws PrinterException {
      int var4 = (int)var2.getImageableWidth();
      int var5 = (int)var2.getImageableHeight();
      if (var4 <= 0) {
         throw new PrinterException("Width of printable area is too small.");
      } else {
         Object[] var6 = new Object[]{var3 + 1};
         String var7 = null;
         if (this.headerFormat != null) {
            var7 = this.headerFormat.format(var6);
         }

         String var8 = null;
         if (this.footerFormat != null) {
            var8 = this.footerFormat.format(var6);
         }

         Rectangle2D var9 = null;
         Rectangle2D var10 = null;
         int var11 = 0;
         int var12 = 0;
         int var13 = var5;
         if (var7 != null) {
            var1.setFont(this.headerFont);
            var9 = var1.getFontMetrics().getStringBounds(var7, var1);
            var11 = (int)Math.ceil(var9.getHeight());
            var13 = var5 - (var11 + 8);
         }

         if (var8 != null) {
            var1.setFont(this.footerFont);
            var10 = var1.getFontMetrics().getStringBounds(var8, var1);
            var12 = (int)Math.ceil(var10.getHeight());
            var13 -= var12 + 8;
         }

         if (var13 <= 0) {
            throw new PrinterException("Height of printable area is too small.");
         } else {
            double var14 = 1.0D;
            if (this.printMode == JTable.PrintMode.FIT_WIDTH && this.totalColWidth > var4) {
               assert var4 > 0;

               assert this.totalColWidth > 1;

               var14 = (double)var4 / (double)this.totalColWidth;
            }

            assert var14 > 0.0D;

            while(this.last < var3) {
               if (this.row >= this.table.getRowCount() && this.col == 0) {
                  return 1;
               }

               int var16 = (int)((double)var4 / var14);
               int var17 = (int)((double)(var13 - this.hclip.height) / var14);
               this.findNextClip(var16, var17);
               ++this.last;
            }

            Graphics2D var19 = (Graphics2D)var1.create();
            var19.translate(var2.getImageableX(), var2.getImageableY());
            AffineTransform var20;
            if (var8 != null) {
               var20 = var19.getTransform();
               var19.translate(0, var5 - var12);
               this.printText(var19, var8, var10, this.footerFont, var4);
               var19.setTransform(var20);
            }

            if (var7 != null) {
               this.printText(var19, var7, var9, this.headerFont, var4);
               var19.translate(0, var11 + 8);
            }

            this.tempRect.x = 0;
            this.tempRect.y = 0;
            this.tempRect.width = var4;
            this.tempRect.height = var13;
            var19.clip(this.tempRect);
            if (var14 != 1.0D) {
               var19.scale(var14, var14);
            } else {
               int var18 = (var4 - this.clip.width) / 2;
               var19.translate(var18, 0);
            }

            var20 = var19.getTransform();
            Shape var21 = var19.getClip();
            if (this.header != null) {
               this.hclip.x = this.clip.x;
               this.hclip.width = this.clip.width;
               var19.translate(-this.hclip.x, 0);
               var19.clip(this.hclip);
               this.header.print(var19);
               var19.setTransform(var20);
               var19.setClip(var21);
               var19.translate(0, this.hclip.height);
            }

            var19.translate(-this.clip.x, -this.clip.y);
            var19.clip(this.clip);
            this.table.print(var19);
            var19.setTransform(var20);
            var19.setClip(var21);
            var19.setColor(Color.BLACK);
            var19.drawRect(0, 0, this.clip.width, this.hclip.height + this.clip.height);
            var19.dispose();
            return 0;
         }
      }
   }

   private void printText(Graphics2D var1, String var2, Rectangle2D var3, Font var4, int var5) {
      int var6;
      if (var3.getWidth() < (double)var5) {
         var6 = (int)(((double)var5 - var3.getWidth()) / 2.0D);
      } else if (this.table.getComponentOrientation().isLeftToRight()) {
         var6 = 0;
      } else {
         var6 = -((int)(Math.ceil(var3.getWidth()) - (double)var5));
      }

      int var7 = (int)Math.ceil(Math.abs(var3.getY()));
      var1.setColor(Color.BLACK);
      var1.setFont(var4);
      var1.drawString(var2, var6, var7);
   }

   private void findNextClip(int var1, int var2) {
      boolean var3 = this.table.getComponentOrientation().isLeftToRight();
      Rectangle var10000;
      int var4;
      int var5;
      if (this.col == 0) {
         if (var3) {
            this.clip.x = 0;
         } else {
            this.clip.x = this.totalColWidth;
         }

         var10000 = this.clip;
         var10000.y += this.clip.height;
         this.clip.width = 0;
         this.clip.height = 0;
         var4 = this.table.getRowCount();
         var5 = this.table.getRowHeight(this.row);

         do {
            var10000 = this.clip;
            var10000.height += var5;
            if (++this.row >= var4) {
               break;
            }

            var5 = this.table.getRowHeight(this.row);
         } while(this.clip.height + var5 <= var2);
      }

      if (this.printMode == JTable.PrintMode.FIT_WIDTH) {
         this.clip.x = 0;
         this.clip.width = this.totalColWidth;
      } else {
         if (var3) {
            var10000 = this.clip;
            var10000.x += this.clip.width;
         }

         this.clip.width = 0;
         var4 = this.table.getColumnCount();
         var5 = this.colModel.getColumn(this.col).getWidth();

         do {
            var10000 = this.clip;
            var10000.width += var5;
            if (!var3) {
               var10000 = this.clip;
               var10000.x -= var5;
            }

            if (++this.col >= var4) {
               this.col = 0;
               break;
            }

            var5 = this.colModel.getColumn(this.col).getWidth();
         } while(this.clip.width + var5 <= var1);

      }
   }
}
