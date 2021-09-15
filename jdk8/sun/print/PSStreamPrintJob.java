package sun.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Locale;
import java.util.Vector;
import javax.print.CancelablePrintJob;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashPrintJobAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobOriginatingUserName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;

public class PSStreamPrintJob implements CancelablePrintJob {
   private transient Vector jobListeners;
   private transient Vector attrListeners;
   private transient Vector listenedAttributeSets;
   private PSStreamPrintService service;
   private boolean fidelity;
   private boolean printing = false;
   private boolean printReturned = false;
   private PrintRequestAttributeSet reqAttrSet = null;
   private PrintJobAttributeSet jobAttrSet = null;
   private PrinterJob job;
   private Doc doc;
   private InputStream instream = null;
   private Reader reader = null;
   private String jobName = "Java Printing";
   private int copies = 1;
   private MediaSize mediaSize;
   private OrientationRequested orient;

   PSStreamPrintJob(PSStreamPrintService var1) {
      this.mediaSize = MediaSize.NA.LETTER;
      this.orient = OrientationRequested.PORTRAIT;
      this.service = var1;
   }

   public PrintService getPrintService() {
      return this.service;
   }

   public PrintJobAttributeSet getAttributes() {
      synchronized(this) {
         if (this.jobAttrSet == null) {
            HashPrintJobAttributeSet var2 = new HashPrintJobAttributeSet();
            return AttributeSetUtilities.unmodifiableView((PrintJobAttributeSet)var2);
         } else {
            return this.jobAttrSet;
         }
      }
   }

   public void addPrintJobListener(PrintJobListener var1) {
      synchronized(this) {
         if (var1 != null) {
            if (this.jobListeners == null) {
               this.jobListeners = new Vector();
            }

            this.jobListeners.add(var1);
         }
      }
   }

   public void removePrintJobListener(PrintJobListener var1) {
      synchronized(this) {
         if (var1 != null && this.jobListeners != null) {
            this.jobListeners.remove(var1);
            if (this.jobListeners.isEmpty()) {
               this.jobListeners = null;
            }

         }
      }
   }

   private void closeDataStreams() {
      if (this.doc != null) {
         Object var1 = null;

         try {
            var1 = this.doc.getPrintData();
         } catch (IOException var25) {
            return;
         }

         if (this.instream != null) {
            try {
               this.instream.close();
            } catch (IOException var23) {
            } finally {
               this.instream = null;
            }
         } else if (this.reader != null) {
            try {
               this.reader.close();
            } catch (IOException var21) {
            } finally {
               this.reader = null;
            }
         } else if (var1 instanceof InputStream) {
            try {
               ((InputStream)var1).close();
            } catch (IOException var20) {
            }
         } else if (var1 instanceof Reader) {
            try {
               ((Reader)var1).close();
            } catch (IOException var19) {
            }
         }

      }
   }

   private void notifyEvent(int var1) {
      synchronized(this) {
         if (this.jobListeners != null) {
            PrintJobEvent var4 = new PrintJobEvent(this, var1);

            for(int var5 = 0; var5 < this.jobListeners.size(); ++var5) {
               PrintJobListener var3 = (PrintJobListener)((PrintJobListener)this.jobListeners.elementAt(var5));
               switch(var1) {
               case 101:
                  var3.printJobCanceled(var4);
                  break;
               case 102:
                  var3.printJobCompleted(var4);
                  break;
               case 103:
                  var3.printJobFailed(var4);
               case 104:
               default:
                  break;
               case 105:
                  var3.printJobNoMoreEvents(var4);
                  break;
               case 106:
                  var3.printDataTransferCompleted(var4);
               }
            }
         }

      }
   }

   public void addPrintJobAttributeListener(PrintJobAttributeListener var1, PrintJobAttributeSet var2) {
      synchronized(this) {
         if (var1 != null) {
            if (this.attrListeners == null) {
               this.attrListeners = new Vector();
               this.listenedAttributeSets = new Vector();
            }

            this.attrListeners.add(var1);
            if (var2 == null) {
               var2 = new HashPrintJobAttributeSet();
            }

            this.listenedAttributeSets.add(var2);
         }
      }
   }

   public void removePrintJobAttributeListener(PrintJobAttributeListener var1) {
      synchronized(this) {
         if (var1 != null && this.attrListeners != null) {
            int var3 = this.attrListeners.indexOf(var1);
            if (var3 != -1) {
               this.attrListeners.remove(var3);
               this.listenedAttributeSets.remove(var3);
               if (this.attrListeners.isEmpty()) {
                  this.attrListeners = null;
                  this.listenedAttributeSets = null;
               }

            }
         }
      }
   }

   public void print(Doc var1, PrintRequestAttributeSet var2) throws PrintException {
      synchronized(this) {
         if (this.printing) {
            throw new PrintException("already printing");
         }

         this.printing = true;
      }

      this.doc = var1;
      DocFlavor var3 = var1.getDocFlavor();

      Object var4;
      try {
         var4 = var1.getPrintData();
      } catch (IOException var14) {
         this.notifyEvent(103);
         throw new PrintException("can't get print data: " + var14.toString());
      }

      if (var3 != null && this.service.isDocFlavorSupported(var3)) {
         this.initializeAttributeSets(var1, var2);
         this.getAttributeValues(var3);
         String var5 = var3.getRepresentationClassName();
         if (!var3.equals(DocFlavor.INPUT_STREAM.GIF) && !var3.equals(DocFlavor.INPUT_STREAM.JPEG) && !var3.equals(DocFlavor.INPUT_STREAM.PNG) && !var3.equals(DocFlavor.BYTE_ARRAY.GIF) && !var3.equals(DocFlavor.BYTE_ARRAY.JPEG) && !var3.equals(DocFlavor.BYTE_ARRAY.PNG)) {
            if (!var3.equals(DocFlavor.URL.GIF) && !var3.equals(DocFlavor.URL.JPEG) && !var3.equals(DocFlavor.URL.PNG)) {
               if (var5.equals("java.awt.print.Pageable")) {
                  try {
                     this.pageableJob((Pageable)var1.getPrintData(), this.reqAttrSet);
                  } catch (ClassCastException var7) {
                     this.notifyEvent(103);
                     throw new PrintException(var7);
                  } catch (IOException var8) {
                     this.notifyEvent(103);
                     throw new PrintException(var8);
                  }
               } else if (var5.equals("java.awt.print.Printable")) {
                  try {
                     this.printableJob((Printable)var1.getPrintData(), this.reqAttrSet);
                  } catch (ClassCastException var9) {
                     this.notifyEvent(103);
                     throw new PrintException(var9);
                  } catch (IOException var10) {
                     this.notifyEvent(103);
                     throw new PrintException(var10);
                  }
               } else {
                  this.notifyEvent(103);
                  throw new PrintException("unrecognized class: " + var5);
               }
            } else {
               try {
                  this.printableJob(new ImagePrinter((URL)var4), this.reqAttrSet);
               } catch (ClassCastException var11) {
                  this.notifyEvent(103);
                  throw new PrintException(var11);
               }
            }
         } else {
            try {
               this.instream = var1.getStreamForBytes();
               this.printableJob(new ImagePrinter(this.instream), this.reqAttrSet);
            } catch (ClassCastException var12) {
               this.notifyEvent(103);
               throw new PrintException(var12);
            } catch (IOException var13) {
               this.notifyEvent(103);
               throw new PrintException(var13);
            }
         }
      } else {
         this.notifyEvent(103);
         throw new PrintJobFlavorException("invalid flavor", var3);
      }
   }

   public void printableJob(Printable var1, PrintRequestAttributeSet var2) throws PrintException {
      try {
         synchronized(this) {
            if (this.job != null) {
               throw new PrintException("already printing");
            }

            this.job = new PSPrinterJob();
         }

         this.job.setPrintService(this.getPrintService());
         PageFormat var3 = new PageFormat();
         if (this.mediaSize != null) {
            Paper var4 = new Paper();
            var4.setSize((double)this.mediaSize.getX(25400) * 72.0D, (double)this.mediaSize.getY(25400) * 72.0D);
            var4.setImageableArea(72.0D, 72.0D, var4.getWidth() - 144.0D, var4.getHeight() - 144.0D);
            var3.setPaper(var4);
         }

         if (this.orient == OrientationRequested.REVERSE_LANDSCAPE) {
            var3.setOrientation(2);
         } else if (this.orient == OrientationRequested.LANDSCAPE) {
            var3.setOrientation(0);
         }

         this.job.setPrintable(var1, var3);
         this.job.print(var2);
         this.notifyEvent(102);
      } catch (PrinterException var10) {
         this.notifyEvent(103);
         throw new PrintException(var10);
      } finally {
         this.printReturned = true;
      }

   }

   public void pageableJob(Pageable var1, PrintRequestAttributeSet var2) throws PrintException {
      try {
         synchronized(this) {
            if (this.job != null) {
               throw new PrintException("already printing");
            }

            this.job = new PSPrinterJob();
         }

         this.job.setPrintService(this.getPrintService());
         this.job.setPageable(var1);
         this.job.print(var2);
         this.notifyEvent(102);
      } catch (PrinterException var10) {
         this.notifyEvent(103);
         throw new PrintException(var10);
      } finally {
         this.printReturned = true;
      }

   }

   private synchronized void initializeAttributeSets(Doc var1, PrintRequestAttributeSet var2) {
      this.reqAttrSet = new HashPrintRequestAttributeSet();
      this.jobAttrSet = new HashPrintJobAttributeSet();
      Attribute[] var3;
      if (var2 != null) {
         this.reqAttrSet.addAll(var2);
         var3 = var2.toArray();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var3[var4] instanceof PrintJobAttribute) {
               this.jobAttrSet.add(var3[var4]);
            }
         }
      }

      DocAttributeSet var11 = var1.getAttributes();
      if (var11 != null) {
         var3 = var11.toArray();

         for(int var5 = 0; var5 < var3.length; ++var5) {
            if (var3[var5] instanceof PrintRequestAttribute) {
               this.reqAttrSet.add(var3[var5]);
            }

            if (var3[var5] instanceof PrintJobAttribute) {
               this.jobAttrSet.add(var3[var5]);
            }
         }
      }

      String var12 = "";

      try {
         var12 = System.getProperty("user.name");
      } catch (SecurityException var10) {
      }

      if (var12 != null && !var12.equals("")) {
         this.jobAttrSet.add(new JobOriginatingUserName(var12, (Locale)null));
      } else {
         RequestingUserName var6 = (RequestingUserName)var2.get(RequestingUserName.class);
         if (var6 != null) {
            this.jobAttrSet.add(new JobOriginatingUserName(var6.getValue(), var6.getLocale()));
         } else {
            this.jobAttrSet.add(new JobOriginatingUserName("", (Locale)null));
         }
      }

      if (this.jobAttrSet.get(JobName.class) == null) {
         JobName var13;
         if (var11 != null && var11.get(DocumentName.class) != null) {
            DocumentName var14 = (DocumentName)var11.get(DocumentName.class);
            var13 = new JobName(var14.getValue(), var14.getLocale());
            this.jobAttrSet.add(var13);
         } else {
            String var7 = "JPS Job:" + var1;

            try {
               Object var8 = var1.getPrintData();
               if (var8 instanceof URL) {
                  var7 = ((URL)((URL)var1.getPrintData())).toString();
               }
            } catch (IOException var9) {
            }

            var13 = new JobName(var7, (Locale)null);
            this.jobAttrSet.add(var13);
         }
      }

      this.jobAttrSet = AttributeSetUtilities.unmodifiableView(this.jobAttrSet);
   }

   private void getAttributeValues(DocFlavor var1) throws PrintException {
      if (this.reqAttrSet.get(Fidelity.class) == Fidelity.FIDELITY_TRUE) {
         this.fidelity = true;
      } else {
         this.fidelity = false;
      }

      Attribute[] var4 = this.reqAttrSet.toArray();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         Attribute var2 = var4[var5];
         Class var3 = var2.getCategory();
         if (this.fidelity) {
            if (!this.service.isAttributeCategorySupported(var3)) {
               this.notifyEvent(103);
               throw new PrintJobAttributeException("unsupported category: " + var3, var3, (Attribute)null);
            }

            if (!this.service.isAttributeValueSupported(var2, var1, (AttributeSet)null)) {
               this.notifyEvent(103);
               throw new PrintJobAttributeException("unsupported attribute: " + var2, (Class)null, var2);
            }
         }

         if (var3 == JobName.class) {
            this.jobName = ((JobName)var2).getValue();
         } else if (var3 == Copies.class) {
            this.copies = ((Copies)var2).getValue();
         } else if (var3 == Media.class) {
            if (var2 instanceof MediaSizeName && this.service.isAttributeValueSupported(var2, (DocFlavor)null, (AttributeSet)null)) {
               this.mediaSize = MediaSize.getMediaSizeForName((MediaSizeName)var2);
            }
         } else if (var3 == OrientationRequested.class) {
            this.orient = (OrientationRequested)var2;
         }
      }

   }

   public void cancel() throws PrintException {
      synchronized(this) {
         if (!this.printing) {
            throw new PrintException("Job is not yet submitted.");
         } else if (this.job != null && !this.printReturned) {
            this.job.cancel();
            this.notifyEvent(101);
         } else {
            throw new PrintException("Job could not be cancelled.");
         }
      }
   }
}
