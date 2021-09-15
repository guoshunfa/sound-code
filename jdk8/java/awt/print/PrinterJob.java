package java.awt.print;

import java.awt.AWTError;
import java.awt.HeadlessException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

public abstract class PrinterJob {
   public static PrinterJob getPrinterJob() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPrintJobAccess();
      }

      return (PrinterJob)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            String var1 = System.getProperty("java.awt.printerjob", (String)null);

            try {
               return (PrinterJob)Class.forName(var1).newInstance();
            } catch (ClassNotFoundException var3) {
               throw new AWTError("PrinterJob not found: " + var1);
            } catch (InstantiationException var4) {
               throw new AWTError("Could not instantiate PrinterJob: " + var1);
            } catch (IllegalAccessException var5) {
               throw new AWTError("Could not access PrinterJob: " + var1);
            }
         }
      });
   }

   public static PrintService[] lookupPrintServices() {
      return PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, (AttributeSet)null);
   }

   public static StreamPrintServiceFactory[] lookupStreamPrintServices(String var0) {
      return StreamPrintServiceFactory.lookupStreamPrintServiceFactories(DocFlavor.SERVICE_FORMATTED.PAGEABLE, var0);
   }

   public PrintService getPrintService() {
      return null;
   }

   public void setPrintService(PrintService var1) throws PrinterException {
      throw new PrinterException("Setting a service is not supported on this class");
   }

   public abstract void setPrintable(Printable var1);

   public abstract void setPrintable(Printable var1, PageFormat var2);

   public abstract void setPageable(Pageable var1) throws NullPointerException;

   public abstract boolean printDialog() throws HeadlessException;

   public boolean printDialog(PrintRequestAttributeSet var1) throws HeadlessException {
      if (var1 == null) {
         throw new NullPointerException("attributes");
      } else {
         return this.printDialog();
      }
   }

   public abstract PageFormat pageDialog(PageFormat var1) throws HeadlessException;

   public PageFormat pageDialog(PrintRequestAttributeSet var1) throws HeadlessException {
      if (var1 == null) {
         throw new NullPointerException("attributes");
      } else {
         return this.pageDialog(this.defaultPage());
      }
   }

   public abstract PageFormat defaultPage(PageFormat var1);

   public PageFormat defaultPage() {
      return this.defaultPage(new PageFormat());
   }

   public PageFormat getPageFormat(PrintRequestAttributeSet var1) {
      PrintService var2 = this.getPrintService();
      PageFormat var3 = this.defaultPage();
      if (var2 != null && var1 != null) {
         Media var4 = (Media)var1.get(Media.class);
         MediaPrintableArea var5 = (MediaPrintableArea)var1.get(MediaPrintableArea.class);
         OrientationRequested var6 = (OrientationRequested)var1.get(OrientationRequested.class);
         if (var4 == null && var5 == null && var6 == null) {
            return var3;
         } else {
            Paper var7 = var3.getPaper();
            if (var5 == null && var4 != null && var2.isAttributeCategorySupported(MediaPrintableArea.class)) {
               Object var8 = var2.getSupportedAttributeValues(MediaPrintableArea.class, (DocFlavor)null, var1);
               if (var8 instanceof MediaPrintableArea[] && ((MediaPrintableArea[])((MediaPrintableArea[])var8)).length > 0) {
                  var5 = ((MediaPrintableArea[])((MediaPrintableArea[])var8))[0];
               }
            }

            if (var4 != null && var2.isAttributeValueSupported(var4, (DocFlavor)null, var1) && var4 instanceof MediaSizeName) {
               MediaSizeName var16 = (MediaSizeName)var4;
               MediaSize var9 = MediaSize.getMediaSizeForName(var16);
               if (var9 != null) {
                  double var10 = 72.0D;
                  double var12 = (double)var9.getX(25400) * var10;
                  double var14 = (double)var9.getY(25400) * var10;
                  var7.setSize(var12, var14);
                  if (var5 == null) {
                     var7.setImageableArea(var10, var10, var12 - 2.0D * var10, var14 - 2.0D * var10);
                  }
               }
            }

            if (var5 != null && var2.isAttributeValueSupported(var5, (DocFlavor)null, var1)) {
               float[] var17 = var5.getPrintableArea(25400);

               for(int var19 = 0; var19 < var17.length; ++var19) {
                  var17[var19] *= 72.0F;
               }

               var7.setImageableArea((double)var17[0], (double)var17[1], (double)var17[2], (double)var17[3]);
            }

            if (var6 != null && var2.isAttributeValueSupported(var6, (DocFlavor)null, var1)) {
               byte var18;
               if (var6.equals(OrientationRequested.REVERSE_LANDSCAPE)) {
                  var18 = 2;
               } else if (var6.equals(OrientationRequested.LANDSCAPE)) {
                  var18 = 0;
               } else {
                  var18 = 1;
               }

               var3.setOrientation(var18);
            }

            var3.setPaper(var7);
            var3 = this.validatePage(var3);
            return var3;
         }
      } else {
         return var3;
      }
   }

   public abstract PageFormat validatePage(PageFormat var1);

   public abstract void print() throws PrinterException;

   public void print(PrintRequestAttributeSet var1) throws PrinterException {
      this.print();
   }

   public abstract void setCopies(int var1);

   public abstract int getCopies();

   public abstract String getUserName();

   public abstract void setJobName(String var1);

   public abstract String getJobName();

   public abstract void cancel();

   public abstract boolean isCancelled();
}
