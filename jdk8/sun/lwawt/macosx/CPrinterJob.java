package sun.lwawt.macosx;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.StreamPrintService;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PageRanges;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.print.PeekGraphics;
import sun.print.RasterPrinterJob;
import sun.print.SunPageSelection;

public final class CPrinterJob extends RasterPrinterJob {
   private static String sShouldNotReachHere = "Should not reach here.";
   private volatile SecondaryLoop printingLoop;
   private boolean noDefaultPrinter = false;
   private static Font defaultFont;
   private long fNSPrintInfo = -1L;
   private Object fNSPrintInfoLock = new Object();
   volatile boolean onEventThread;

   public boolean printDialog() throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else if (this.noDefaultPrinter) {
         return false;
      } else {
         if (this.attributes == null) {
            this.attributes = new HashPrintRequestAttributeSet();
         }

         return this.getPrintService() instanceof StreamPrintService ? super.printDialog(this.attributes) : this.jobSetup(this.getPageable(), this.checkAllowedToPrintToFile());
      }
   }

   public PageFormat pageDialog(PageFormat var1) throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else if (this.noDefaultPrinter) {
         return var1;
      } else if (this.getPrintService() instanceof StreamPrintService) {
         return super.pageDialog(var1);
      } else {
         PageFormat var2 = (PageFormat)var1.clone();
         boolean var3 = this.pageSetup(var2, (Printable)null);
         return var3 ? var2 : var1;
      }
   }

   public PageFormat defaultPage(PageFormat var1) {
      PageFormat var2 = (PageFormat)var1.clone();
      this.getDefaultPage(var2);
      return var2;
   }

   protected void setAttributes(PrintRequestAttributeSet var1) throws PrinterException {
      super.setAttributes(var1);
      if (var1 != null) {
         PageRanges var2 = (PageRanges)var1.get(PageRanges.class);
         if (this.isSupportedValue(var2, var1)) {
            SunPageSelection var3 = (SunPageSelection)var1.get(SunPageSelection.class);
            if (var3 == null || var3 == SunPageSelection.RANGE) {
               int[][] var4 = var2.getMembers();
               this.setPageRange(var4[0][0] - 1, var4[0][1] - 1);
            }
         }

      }
   }

   protected void cancelDoc() throws PrinterAbortException {
      super.cancelDoc();
      if (this.printingLoop != null) {
         this.printingLoop.exit();
      }

   }

   private void completePrintLoop() {
      Runnable var1 = new Runnable() {
         public void run() {
            synchronized(this) {
               CPrinterJob.this.performingPrinting = false;
            }

            if (CPrinterJob.this.printingLoop != null) {
               CPrinterJob.this.printingLoop.exit();
            }

         }
      };
      if (this.onEventThread) {
         try {
            EventQueue.invokeAndWait(var1);
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      } else {
         var1.run();
      }

   }

   public void print(PrintRequestAttributeSet var1) throws PrinterException {
      PrintService var2 = this.getPrintService();
      if (var2 == null) {
         throw new PrinterException("No print service found.");
      } else if (var2 instanceof StreamPrintService) {
         this.spoolToService(var2, var1);
      } else {
         this.setAttributes(var1);
         if (this.destinationAttr != null) {
            this.validateDestination(this.destinationAttr);
         }

         int var3 = this.getFirstPage();
         int var4 = this.getLastPage();
         if (var4 == -1) {
            int var5 = this.mDocument.getNumberOfPages();
            if (var5 != -1) {
               var4 = this.mDocument.getNumberOfPages() - 1;
            }
         }

         boolean var19 = false;

         try {
            var19 = true;
            synchronized(this) {
               this.performingPrinting = true;
               this.userCancelled = false;
            }

            PageRanges var26 = var1 == null ? null : (PageRanges)var1.get(PageRanges.class);
            int[][] var6 = var26 == null ? new int[0][0] : var26.getMembers();
            int var7 = 0;

            while(true) {
               if (EventQueue.isDispatchThread()) {
                  this.onEventThread = true;
                  this.printingLoop = (SecondaryLoop)AccessController.doPrivileged(new PrivilegedAction<SecondaryLoop>() {
                     public SecondaryLoop run() {
                        return Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
                     }
                  });

                  try {
                     if (this.printLoop(false, var3, var4)) {
                        this.printingLoop.enter();
                     }
                  } catch (Exception var23) {
                     var23.printStackTrace();
                  }
               } else {
                  this.onEventThread = false;

                  try {
                     this.printLoop(true, var3, var4);
                  } catch (Exception var22) {
                     var22.printStackTrace();
                  }
               }

               ++var7;
               if (var7 < var6.length) {
                  var3 = var6[var7][0] - 1;
                  var4 = var6[var7][1] - 1;
               }

               if (var7 >= var6.length) {
                  var19 = false;
                  break;
               }
            }
         } finally {
            if (var19) {
               synchronized(this) {
                  this.performingPrinting = false;
                  this.notify();
               }

               if (this.printingLoop != null) {
                  this.printingLoop.exit();
               }

            }
         }

         synchronized(this) {
            this.performingPrinting = false;
            this.notify();
         }

         if (this.printingLoop != null) {
            this.printingLoop.exit();
         }

      }
   }

   protected double getXRes() {
      return 0.0D;
   }

   protected double getYRes() {
      return 0.0D;
   }

   protected double getPhysicalPrintableX(Paper var1) {
      return 0.0D;
   }

   protected double getPhysicalPrintableY(Paper var1) {
      return 0.0D;
   }

   protected double getPhysicalPrintableWidth(Paper var1) {
      return 0.0D;
   }

   protected double getPhysicalPrintableHeight(Paper var1) {
      return 0.0D;
   }

   protected double getPhysicalPageWidth(Paper var1) {
      return 0.0D;
   }

   protected double getPhysicalPageHeight(Paper var1) {
      return 0.0D;
   }

   protected void startPage(PageFormat var1, Printable var2, int var3) throws PrinterException {
      throw new PrinterException(sShouldNotReachHere);
   }

   protected void endPage(PageFormat var1, Printable var2, int var3) throws PrinterException {
      throw new PrinterException(sShouldNotReachHere);
   }

   protected void printBand(byte[] var1, int var2, int var3, int var4, int var5) throws PrinterException {
      throw new PrinterException(sShouldNotReachHere);
   }

   protected void startDoc() throws PrinterException {
      throw new PrinterException(sShouldNotReachHere);
   }

   protected void endDoc() throws PrinterException {
      throw new PrinterException(sShouldNotReachHere);
   }

   protected native void abortDoc();

   public boolean pageSetup(PageFormat var1, Printable var2) {
      CPrinterPageDialog var3 = new CPrinterPageDialog((Frame)null, this, var1, var2);
      var3.setVisible(true);
      boolean var4 = var3.getRetVal();
      var3.dispose();
      return var4;
   }

   private boolean jobSetup(Pageable var1, boolean var2) {
      CPrinterJobDialog var3 = new CPrinterJobDialog((Frame)null, this, var1, var2);
      var3.setVisible(true);
      boolean var4 = var3.getRetVal();
      var3.dispose();
      return var4;
   }

   private native void getDefaultPage(PageFormat var1);

   protected native void validatePaper(Paper var1, Paper var2);

   protected void finalize() {
      synchronized(this.fNSPrintInfoLock) {
         if (this.fNSPrintInfo != -1L) {
            this.dispose(this.fNSPrintInfo);
         }

         this.fNSPrintInfo = -1L;
      }
   }

   private native long createNSPrintInfo();

   private native void dispose(long var1);

   private long getNSPrintInfo() {
      synchronized(this.fNSPrintInfoLock) {
         if (this.fNSPrintInfo == -1L) {
            this.fNSPrintInfo = this.createNSPrintInfo();
         }

         return this.fNSPrintInfo;
      }
   }

   private native boolean printLoop(boolean var1, int var2, int var3) throws PrinterException;

   private PageFormat getPageFormat(int var1) {
      try {
         PageFormat var2 = this.getPageable().getPageFormat(var1);
         return var2;
      } catch (Exception var4) {
         return null;
      }
   }

   private Printable getPrintable(int var1) {
      try {
         Printable var2 = this.getPageable().getPrintable(var1);
         return var2;
      } catch (Exception var4) {
         return null;
      }
   }

   private String getPrinterName() {
      PrintService var1 = this.getPrintService();
      return var1 == null ? null : var1.getName();
   }

   private void setPrinterServiceFromNative(String var1) {
      PrintService[] var2 = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, (AttributeSet)null);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         PrintService var4 = var2[var3];
         if (var1.equals(var4.getName())) {
            try {
               this.setPrintService(var4);
            } catch (PrinterException var6) {
            }

            return;
         }
      }

   }

   private Rectangle2D getPageFormatArea(PageFormat var1) {
      Rectangle2D.Double var2 = new Rectangle2D.Double(var1.getImageableX(), var1.getImageableY(), var1.getImageableWidth(), var1.getImageableHeight());
      return var2;
   }

   private boolean cancelCheck() {
      boolean var1 = this.performingPrinting && this.userCancelled;
      if (var1) {
         try {
            LWCToolkit.invokeLater(new Runnable() {
               public void run() {
                  try {
                     CPrinterJob.this.cancelDoc();
                  } catch (PrinterAbortException var2) {
                  }

               }
            }, (Component)null);
         } catch (InvocationTargetException var3) {
         }
      }

      return var1;
   }

   private PeekGraphics createFirstPassGraphics(PrinterJob var1, PageFormat var2) {
      BufferedImage var3 = new BufferedImage((int)Math.round(var2.getWidth()), (int)Math.round(var2.getHeight()), 3);
      PeekGraphics var4 = this.createPeekGraphics(var3.createGraphics(), var1);
      Rectangle2D var5 = this.getPageFormatArea(var2);
      this.initPrinterGraphics(var4, var5);
      return var4;
   }

   private void printToPathGraphics(PeekGraphics var1, final PrinterJob var2, final Printable var3, final PageFormat var4, final int var5, final long var6) throws PrinterException {
      Runnable var8 = new Runnable() {
         public void run() {
            try {
               SurfaceData var1 = CPrinterSurfaceData.createData(var4, var6);
               if (CPrinterJob.defaultFont == null) {
                  CPrinterJob.defaultFont = new Font("Dialog", 0, 12);
               }

               SunGraphics2D var2x = new SunGraphics2D(var1, Color.black, Color.white, CPrinterJob.defaultFont);
               CPrinterGraphics var3x = new CPrinterGraphics(var2x, var2);
               Rectangle2D var4x = CPrinterJob.this.getPageFormatArea(var4);
               CPrinterJob.this.initPrinterGraphics(var3x, var4x);
               var3.print(var3x, var4, var5);
               var2x.dispose();
               var2x = null;
            } catch (PrinterException var5x) {
               throw new UndeclaredThrowableException(var5x);
            }
         }
      };
      if (this.onEventThread) {
         try {
            EventQueue.invokeAndWait(var8);
         } catch (InvocationTargetException var11) {
            Throwable var10 = var11.getTargetException();
            if (var10 instanceof PrinterException) {
               throw (PrinterException)var10;
            }

            var10.printStackTrace();
         } catch (Exception var12) {
            var12.printStackTrace();
         }
      } else {
         var8.run();
      }

   }

   private Object[] getPageformatPrintablePeekgraphics(final int var1) {
      final Object[] var2 = new Object[3];
      Runnable var4 = new Runnable() {
         public void run() {
            synchronized(var2) {
               try {
                  Pageable var2x = CPrinterJob.this.getPageable();
                  PageFormat var3 = var2x.getPageFormat(var1);
                  if (var3 != null) {
                     Printable var4 = var2x.getPrintable(var1);
                     if (var4 != null) {
                        BufferedImage var5 = new BufferedImage((int)Math.round(var3.getWidth()), (int)Math.round(var3.getHeight()), 3);
                        PeekGraphics var6 = CPrinterJob.this.createPeekGraphics(var5.createGraphics(), CPrinterJob.this);
                        Rectangle2D var7 = CPrinterJob.this.getPageFormatArea(var3);
                        CPrinterJob.this.initPrinterGraphics(var6, var7);
                        var2[0] = var3;
                        var2[1] = var4;
                        var2[2] = var6;
                     }
                  }
               } catch (Exception var9) {
               }

            }
         }
      };
      if (this.onEventThread) {
         try {
            EventQueue.invokeAndWait(var4);
         } catch (Exception var8) {
            var8.printStackTrace();
         }
      } else {
         var4.run();
      }

      synchronized(var2) {
         return var2[2] != null ? var2 : null;
      }
   }

   private Rectangle2D printAndGetPageFormatArea(final Printable var1, final Graphics var2, final PageFormat var3, final int var4) {
      final Rectangle2D[] var5 = new Rectangle2D[1];
      Runnable var6 = new Runnable() {
         public void run() {
            synchronized(var5) {
               try {
                  int var2x = var1.print(var2, var3, var4);
                  if (var2x != 1) {
                     var5[0] = CPrinterJob.this.getPageFormatArea(var3);
                  }
               } catch (Exception var4x) {
               }

            }
         }
      };
      if (this.onEventThread) {
         try {
            EventQueue.invokeAndWait(var6);
         } catch (Exception var10) {
            var10.printStackTrace();
         }
      } else {
         var6.run();
      }

      synchronized(var5) {
         return var5[0];
      }
   }

   private static void detachPrintLoop(final long var0, final long var2) {
      (new Thread() {
         public void run() {
            CPrinterJob._safePrintLoop(var0, var2);
         }
      }).start();
   }

   private static native void _safePrintLoop(long var0, long var2);

   protected void startPage(PageFormat var1, Printable var2, int var3, boolean var4) throws PrinterException {
   }

   protected MediaSize getMediaSize(Media var1, PrintService var2, PageFormat var3) {
      if (var1 != null && var1 instanceof MediaSizeName) {
         MediaSize var4 = MediaSize.getMediaSizeForName((MediaSizeName)var1);
         return var4 != null ? var4 : this.getDefaultMediaSize(var3);
      } else {
         return this.getDefaultMediaSize(var3);
      }
   }

   private MediaSize getDefaultMediaSize(PageFormat var1) {
      Paper var3 = var1.getPaper();
      float var4 = (float)(var3.getWidth() / 72.0D);
      float var5 = (float)(var3.getHeight() / 72.0D);
      return new MediaSize(var4, var5, 25400);
   }

   protected MediaPrintableArea getDefaultPrintableArea(PageFormat var1, double var2, double var4) {
      Paper var7 = var1.getPaper();
      return new MediaPrintableArea((float)(var7.getImageableX() / 72.0D), (float)(var7.getImageableY() / 72.0D), (float)(var7.getImageableWidth() / 72.0D), (float)(var7.getImageableHeight() / 72.0D), 25400);
   }

   static {
      Toolkit.getDefaultToolkit();
   }
}
