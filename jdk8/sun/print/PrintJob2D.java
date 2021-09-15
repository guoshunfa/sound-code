package sun.print;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.JobAttributes;
import java.awt.PageAttributes;
import java.awt.PrintJob;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;

public class PrintJob2D extends PrintJob implements Printable, Runnable {
   private static final PageAttributes.MediaType[] SIZES;
   private static final MediaSizeName[] JAVAXSIZES;
   private static final int[] WIDTHS;
   private static final int[] LENGTHS;
   private Frame frame;
   private String docTitle = "";
   private JobAttributes jobAttributes;
   private PageAttributes pageAttributes;
   private PrintRequestAttributeSet attributes;
   private PrinterJob printerJob;
   private PageFormat pageFormat;
   private PrintJob2D.MessageQ graphicsToBeDrawn = new PrintJob2D.MessageQ("tobedrawn");
   private PrintJob2D.MessageQ graphicsDrawn = new PrintJob2D.MessageQ("drawn");
   private Graphics2D currentGraphics;
   private int pageIndex = -1;
   private static final String DEST_PROP = "awt.print.destination";
   private static final String PRINTER = "printer";
   private static final String FILE = "file";
   private static final String PRINTER_PROP = "awt.print.printer";
   private static final String FILENAME_PROP = "awt.print.fileName";
   private static final String NUMCOPIES_PROP = "awt.print.numCopies";
   private static final String OPTIONS_PROP = "awt.print.options";
   private static final String ORIENT_PROP = "awt.print.orientation";
   private static final String PORTRAIT = "portrait";
   private static final String LANDSCAPE = "landscape";
   private static final String PAPERSIZE_PROP = "awt.print.paperSize";
   private static final String LETTER = "letter";
   private static final String LEGAL = "legal";
   private static final String EXECUTIVE = "executive";
   private static final String A4 = "a4";
   private Properties props;
   private String options = "";
   private Thread printerJobThread;

   public PrintJob2D(Frame var1, String var2, Properties var3) {
      this.props = var3;
      this.jobAttributes = new JobAttributes();
      this.pageAttributes = new PageAttributes();
      this.translateInputProps();
      this.initPrintJob2D(var1, var2, this.jobAttributes, this.pageAttributes);
   }

   public PrintJob2D(Frame var1, String var2, JobAttributes var3, PageAttributes var4) {
      this.initPrintJob2D(var1, var2, var3, var4);
   }

   private void initPrintJob2D(Frame var1, String var2, JobAttributes var3, PageAttributes var4) {
      SecurityManager var5 = System.getSecurityManager();
      if (var5 != null) {
         var5.checkPrintJobAccess();
      }

      if (var1 != null || var3 != null && var3.getDialog() != JobAttributes.DialogType.NATIVE) {
         this.frame = var1;
         this.docTitle = var2 == null ? "" : var2;
         this.jobAttributes = var3 != null ? var3 : new JobAttributes();
         this.pageAttributes = var4 != null ? var4 : new PageAttributes();
         int[][] var6 = this.jobAttributes.getPageRanges();
         int var7 = var6[0][0];
         int var8 = var6[var6.length - 1][1];
         this.jobAttributes.setPageRanges(new int[][]{{var7, var8}});
         this.jobAttributes.setToPage(var8);
         this.jobAttributes.setFromPage(var7);
         int[] var9 = this.pageAttributes.getPrinterResolution();
         if (var9[0] != var9[1]) {
            throw new IllegalArgumentException("Differing cross feed and feed resolutions not supported.");
         } else {
            JobAttributes.DestinationType var10 = this.jobAttributes.getDestination();
            if (var10 == JobAttributes.DestinationType.FILE) {
               this.throwPrintToFile();
               String var11 = var3.getFileName();
               if (var11 != null && var3.getDialog() == JobAttributes.DialogType.NONE) {
                  File var12 = new File(var11);

                  try {
                     if (var12.createNewFile()) {
                        var12.delete();
                     }
                  } catch (IOException var14) {
                     throw new IllegalArgumentException("Cannot write to file:" + var11);
                  } catch (SecurityException var15) {
                  }

                  File var13 = var12.getParentFile();
                  if (var12.exists() && (!var12.isFile() || !var12.canWrite()) || var13 != null && (!var13.exists() || var13.exists() && !var13.canWrite())) {
                     throw new IllegalArgumentException("Cannot write to file:" + var11);
                  }
               }
            }

         }
      } else {
         throw new NullPointerException("Frame must not be null");
      }
   }

   public boolean printDialog() {
      boolean var1 = false;
      this.printerJob = PrinterJob.getPrinterJob();
      if (this.printerJob == null) {
         return false;
      } else {
         JobAttributes.DialogType var2 = this.jobAttributes.getDialog();
         PrintService var3 = this.printerJob.getPrintService();
         if (var3 == null && var2 == JobAttributes.DialogType.NONE) {
            return false;
         } else {
            this.copyAttributes(var3);
            JobAttributes.DefaultSelectionType var4 = this.jobAttributes.getDefaultSelection();
            if (var4 == JobAttributes.DefaultSelectionType.RANGE) {
               this.attributes.add(SunPageSelection.RANGE);
            } else if (var4 == JobAttributes.DefaultSelectionType.SELECTION) {
               this.attributes.add(SunPageSelection.SELECTION);
            } else {
               this.attributes.add(SunPageSelection.ALL);
            }

            if (this.frame != null) {
               this.attributes.add(new DialogOwner(this.frame));
            }

            if (var2 == JobAttributes.DialogType.NONE) {
               var1 = true;
            } else {
               if (var2 == JobAttributes.DialogType.NATIVE) {
                  this.attributes.add(DialogTypeSelection.NATIVE);
               } else {
                  this.attributes.add(DialogTypeSelection.COMMON);
               }

               if (var1 = this.printerJob.printDialog(this.attributes)) {
                  if (var3 == null) {
                     var3 = this.printerJob.getPrintService();
                     if (var3 == null) {
                        return false;
                     }
                  }

                  this.updateAttributes();
                  this.translateOutputProps();
               }
            }

            if (var1) {
               JobName var5 = (JobName)this.attributes.get(JobName.class);
               if (var5 != null) {
                  this.printerJob.setJobName(var5.toString());
               }

               this.pageFormat = new PageFormat();
               Media var6 = (Media)this.attributes.get(Media.class);
               MediaSize var7 = null;
               if (var6 != null && var6 instanceof MediaSizeName) {
                  var7 = MediaSize.getMediaSizeForName((MediaSizeName)var6);
               }

               Paper var8 = this.pageFormat.getPaper();
               if (var7 != null) {
                  var8.setSize((double)var7.getX(25400) * 72.0D, (double)var7.getY(25400) * 72.0D);
               }

               if (this.pageAttributes.getOrigin() == PageAttributes.OriginType.PRINTABLE) {
                  var8.setImageableArea(18.0D, 18.0D, var8.getWidth() - 36.0D, var8.getHeight() - 36.0D);
               } else {
                  var8.setImageableArea(0.0D, 0.0D, var8.getWidth(), var8.getHeight());
               }

               this.pageFormat.setPaper(var8);
               OrientationRequested var9 = (OrientationRequested)this.attributes.get(OrientationRequested.class);
               if (var9 != null && var9 == OrientationRequested.REVERSE_LANDSCAPE) {
                  this.pageFormat.setOrientation(2);
               } else if (var9 == OrientationRequested.LANDSCAPE) {
                  this.pageFormat.setOrientation(0);
               } else {
                  this.pageFormat.setOrientation(1);
               }

               this.printerJob.setPrintable(this, this.pageFormat);
            }

            return var1;
         }
      }
   }

   private void updateAttributes() {
      Copies var1 = (Copies)this.attributes.get(Copies.class);
      this.jobAttributes.setCopies(var1.getValue());
      SunPageSelection var2 = (SunPageSelection)this.attributes.get(SunPageSelection.class);
      if (var2 == SunPageSelection.RANGE) {
         this.jobAttributes.setDefaultSelection(JobAttributes.DefaultSelectionType.RANGE);
      } else if (var2 == SunPageSelection.SELECTION) {
         this.jobAttributes.setDefaultSelection(JobAttributes.DefaultSelectionType.SELECTION);
      } else {
         this.jobAttributes.setDefaultSelection(JobAttributes.DefaultSelectionType.ALL);
      }

      Destination var3 = (Destination)this.attributes.get(Destination.class);
      if (var3 != null) {
         this.jobAttributes.setDestination(JobAttributes.DestinationType.FILE);
         this.jobAttributes.setFileName(var3.getURI().getPath());
      } else {
         this.jobAttributes.setDestination(JobAttributes.DestinationType.PRINTER);
      }

      PrintService var4 = this.printerJob.getPrintService();
      if (var4 != null) {
         this.jobAttributes.setPrinter(var4.getName());
      }

      PageRanges var5 = (PageRanges)this.attributes.get(PageRanges.class);
      int[][] var6 = var5.getMembers();
      this.jobAttributes.setPageRanges(var6);
      SheetCollate var7 = (SheetCollate)this.attributes.get(SheetCollate.class);
      if (var7 == SheetCollate.COLLATED) {
         this.jobAttributes.setMultipleDocumentHandling(JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_COLLATED_COPIES);
      } else {
         this.jobAttributes.setMultipleDocumentHandling(JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_UNCOLLATED_COPIES);
      }

      Sides var8 = (Sides)this.attributes.get(Sides.class);
      if (var8 == Sides.TWO_SIDED_LONG_EDGE) {
         this.jobAttributes.setSides(JobAttributes.SidesType.TWO_SIDED_LONG_EDGE);
      } else if (var8 == Sides.TWO_SIDED_SHORT_EDGE) {
         this.jobAttributes.setSides(JobAttributes.SidesType.TWO_SIDED_SHORT_EDGE);
      } else {
         this.jobAttributes.setSides(JobAttributes.SidesType.ONE_SIDED);
      }

      Chromaticity var9 = (Chromaticity)this.attributes.get(Chromaticity.class);
      if (var9 == Chromaticity.COLOR) {
         this.pageAttributes.setColor(PageAttributes.ColorType.COLOR);
      } else {
         this.pageAttributes.setColor(PageAttributes.ColorType.MONOCHROME);
      }

      OrientationRequested var10 = (OrientationRequested)this.attributes.get(OrientationRequested.class);
      if (var10 == OrientationRequested.LANDSCAPE) {
         this.pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.LANDSCAPE);
      } else {
         this.pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
      }

      PrintQuality var11 = (PrintQuality)this.attributes.get(PrintQuality.class);
      if (var11 == PrintQuality.DRAFT) {
         this.pageAttributes.setPrintQuality(PageAttributes.PrintQualityType.DRAFT);
      } else if (var11 == PrintQuality.HIGH) {
         this.pageAttributes.setPrintQuality(PageAttributes.PrintQualityType.HIGH);
      } else {
         this.pageAttributes.setPrintQuality(PageAttributes.PrintQualityType.NORMAL);
      }

      Media var12 = (Media)this.attributes.get(Media.class);
      if (var12 != null && var12 instanceof MediaSizeName) {
         PageAttributes.MediaType var13 = unMapMedia((MediaSizeName)var12);
         if (var13 != null) {
            this.pageAttributes.setMedia(var13);
         }
      }

      this.debugPrintAttributes(false, false);
   }

   private void debugPrintAttributes(boolean var1, boolean var2) {
      if (var1) {
         System.out.println("new Attributes\ncopies = " + this.jobAttributes.getCopies() + "\nselection = " + this.jobAttributes.getDefaultSelection() + "\ndest " + this.jobAttributes.getDestination() + "\nfile " + this.jobAttributes.getFileName() + "\nfromPage " + this.jobAttributes.getFromPage() + "\ntoPage " + this.jobAttributes.getToPage() + "\ncollation " + this.jobAttributes.getMultipleDocumentHandling() + "\nPrinter " + this.jobAttributes.getPrinter() + "\nSides2 " + this.jobAttributes.getSides());
      }

      if (var2) {
         System.out.println("new Attributes\ncolor = " + this.pageAttributes.getColor() + "\norientation = " + this.pageAttributes.getOrientationRequested() + "\nquality " + this.pageAttributes.getPrintQuality() + "\nMedia2 " + this.pageAttributes.getMedia());
      }

   }

   private void copyAttributes(PrintService var1) {
      this.attributes = new HashPrintRequestAttributeSet();
      this.attributes.add(new JobName(this.docTitle, (Locale)null));
      PrintService var2 = var1;
      String var3 = this.jobAttributes.getPrinter();
      if (var3 != null && var3 != "" && !var3.equals(var1.getName())) {
         PrintService[] var4 = PrinterJob.lookupPrintServices();

         try {
            for(int var5 = 0; var5 < var4.length; ++var5) {
               if (var3.equals(var4[var5].getName())) {
                  this.printerJob.setPrintService(var4[var5]);
                  var2 = var4[var5];
                  break;
               }
            }
         } catch (PrinterException var12) {
         }
      }

      JobAttributes.DestinationType var13 = this.jobAttributes.getDestination();
      if (var13 == JobAttributes.DestinationType.FILE && var2.isAttributeCategorySupported(Destination.class)) {
         String var14 = this.jobAttributes.getFileName();
         Destination var6;
         if (var14 == null && (var6 = (Destination)var2.getDefaultAttributeValue(Destination.class)) != null) {
            this.attributes.add(var6);
         } else {
            URI var7 = null;

            try {
               if (var14 != null) {
                  if (var14.equals("")) {
                     var14 = ".";
                  }
               } else {
                  var14 = "out.prn";
               }

               var7 = (new File(var14)).toURI();
            } catch (SecurityException var11) {
               try {
                  var14 = var14.replace('\\', '/');
                  var7 = new URI("file:" + var14);
               } catch (URISyntaxException var10) {
               }
            }

            if (var7 != null) {
               this.attributes.add(new Destination(var7));
            }
         }
      }

      this.attributes.add(new SunMinMaxPage(this.jobAttributes.getMinPage(), this.jobAttributes.getMaxPage()));
      JobAttributes.SidesType var16 = this.jobAttributes.getSides();
      if (var16 == JobAttributes.SidesType.TWO_SIDED_LONG_EDGE) {
         this.attributes.add(Sides.TWO_SIDED_LONG_EDGE);
      } else if (var16 == JobAttributes.SidesType.TWO_SIDED_SHORT_EDGE) {
         this.attributes.add(Sides.TWO_SIDED_SHORT_EDGE);
      } else if (var16 == JobAttributes.SidesType.ONE_SIDED) {
         this.attributes.add(Sides.ONE_SIDED);
      }

      JobAttributes.MultipleDocumentHandlingType var15 = this.jobAttributes.getMultipleDocumentHandling();
      if (var15 == JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_COLLATED_COPIES) {
         this.attributes.add(SheetCollate.COLLATED);
      } else {
         this.attributes.add(SheetCollate.UNCOLLATED);
      }

      this.attributes.add(new Copies(this.jobAttributes.getCopies()));
      this.attributes.add(new PageRanges(this.jobAttributes.getFromPage(), this.jobAttributes.getToPage()));
      if (this.pageAttributes.getColor() == PageAttributes.ColorType.COLOR) {
         this.attributes.add(Chromaticity.COLOR);
      } else {
         this.attributes.add(Chromaticity.MONOCHROME);
      }

      this.pageFormat = this.printerJob.defaultPage();
      if (this.pageAttributes.getOrientationRequested() == PageAttributes.OrientationRequestedType.LANDSCAPE) {
         this.pageFormat.setOrientation(0);
         this.attributes.add(OrientationRequested.LANDSCAPE);
      } else {
         this.pageFormat.setOrientation(1);
         this.attributes.add(OrientationRequested.PORTRAIT);
      }

      PageAttributes.MediaType var17 = this.pageAttributes.getMedia();
      MediaSizeName var8 = mapMedia(var17);
      if (var8 != null) {
         this.attributes.add(var8);
      }

      PageAttributes.PrintQualityType var9 = this.pageAttributes.getPrintQuality();
      if (var9 == PageAttributes.PrintQualityType.DRAFT) {
         this.attributes.add(PrintQuality.DRAFT);
      } else if (var9 == PageAttributes.PrintQualityType.NORMAL) {
         this.attributes.add(PrintQuality.NORMAL);
      } else if (var9 == PageAttributes.PrintQualityType.HIGH) {
         this.attributes.add(PrintQuality.HIGH);
      }

   }

   public Graphics getGraphics() {
      ProxyPrintGraphics var1 = null;
      synchronized(this) {
         ++this.pageIndex;
         if (this.pageIndex == 0 && !this.graphicsToBeDrawn.isClosed()) {
            this.startPrinterJobThread();
         }

         this.notify();
      }

      if (this.currentGraphics != null) {
         this.graphicsDrawn.append(this.currentGraphics);
         this.currentGraphics = null;
      }

      this.currentGraphics = this.graphicsToBeDrawn.pop();
      if (this.currentGraphics instanceof PeekGraphics) {
         ((PeekGraphics)this.currentGraphics).setAWTDrawingOnly();
         this.graphicsDrawn.append(this.currentGraphics);
         this.currentGraphics = this.graphicsToBeDrawn.pop();
      }

      if (this.currentGraphics != null) {
         this.currentGraphics.translate(this.pageFormat.getImageableX(), this.pageFormat.getImageableY());
         double var2 = 72.0D / this.getPageResolutionInternal();
         this.currentGraphics.scale(var2, var2);
         var1 = new ProxyPrintGraphics(this.currentGraphics.create(), this);
      }

      return var1;
   }

   public Dimension getPageDimension() {
      double var1;
      double var3;
      if (this.pageAttributes != null && this.pageAttributes.getOrigin() == PageAttributes.OriginType.PRINTABLE) {
         var1 = this.pageFormat.getImageableWidth();
         var3 = this.pageFormat.getImageableHeight();
      } else {
         var1 = this.pageFormat.getWidth();
         var3 = this.pageFormat.getHeight();
      }

      double var5 = this.getPageResolutionInternal() / 72.0D;
      return new Dimension((int)(var1 * var5), (int)(var3 * var5));
   }

   private double getPageResolutionInternal() {
      if (this.pageAttributes != null) {
         int[] var1 = this.pageAttributes.getPrinterResolution();
         return var1[2] == 3 ? (double)var1[0] : (double)var1[0] * 2.54D;
      } else {
         return 72.0D;
      }
   }

   public int getPageResolution() {
      return (int)this.getPageResolutionInternal();
   }

   public boolean lastPageFirst() {
      return false;
   }

   public synchronized void end() {
      this.graphicsToBeDrawn.close();
      if (this.currentGraphics != null) {
         this.graphicsDrawn.append(this.currentGraphics);
      }

      this.graphicsDrawn.closeWhenEmpty();
      if (this.printerJobThread != null && this.printerJobThread.isAlive()) {
         try {
            this.printerJobThread.join();
         } catch (InterruptedException var2) {
         }
      }

   }

   public void finalize() {
      this.end();
   }

   public int print(Graphics var1, PageFormat var2, int var3) throws PrinterException {
      this.graphicsToBeDrawn.append((Graphics2D)var1);
      byte var4;
      if (this.graphicsDrawn.pop() != null) {
         var4 = 0;
      } else {
         var4 = 1;
      }

      return var4;
   }

   private void startPrinterJobThread() {
      this.printerJobThread = new Thread(this, "printerJobThread");
      this.printerJobThread.start();
   }

   public void run() {
      try {
         this.printerJob.print(this.attributes);
      } catch (PrinterException var2) {
      }

      this.graphicsToBeDrawn.closeWhenEmpty();
      this.graphicsDrawn.close();
   }

   private static int[] getSize(PageAttributes.MediaType var0) {
      int[] var1 = new int[]{612, 792};

      for(int var2 = 0; var2 < SIZES.length; ++var2) {
         if (SIZES[var2] == var0) {
            var1[0] = WIDTHS[var2];
            var1[1] = LENGTHS[var2];
            break;
         }
      }

      return var1;
   }

   public static MediaSizeName mapMedia(PageAttributes.MediaType var0) {
      Object var1 = null;
      int var2 = Math.min(SIZES.length, JAVAXSIZES.length);

      for(int var3 = 0; var3 < var2; ++var3) {
         if (SIZES[var3] == var0) {
            if (JAVAXSIZES[var3] != null && MediaSize.getMediaSizeForName(JAVAXSIZES[var3]) != null) {
               var1 = JAVAXSIZES[var3];
            } else {
               var1 = new CustomMediaSizeName(SIZES[var3].toString());
               float var4 = (float)Math.rint((double)WIDTHS[var3] / 72.0D);
               float var5 = (float)Math.rint((double)LENGTHS[var3] / 72.0D);
               if ((double)var4 > 0.0D && (double)var5 > 0.0D) {
                  new MediaSize(var4, var5, 25400, (MediaSizeName)var1);
               }
            }
            break;
         }
      }

      return (MediaSizeName)var1;
   }

   public static PageAttributes.MediaType unMapMedia(MediaSizeName var0) {
      PageAttributes.MediaType var1 = null;
      int var2 = Math.min(SIZES.length, JAVAXSIZES.length);

      for(int var3 = 0; var3 < var2; ++var3) {
         if (JAVAXSIZES[var3] == var0 && SIZES[var3] != null) {
            var1 = SIZES[var3];
            break;
         }
      }

      return var1;
   }

   private void translateInputProps() {
      if (this.props != null) {
         String var1 = this.props.getProperty("awt.print.destination");
         if (var1 != null) {
            if (var1.equals("printer")) {
               this.jobAttributes.setDestination(JobAttributes.DestinationType.PRINTER);
            } else if (var1.equals("file")) {
               this.jobAttributes.setDestination(JobAttributes.DestinationType.FILE);
            }
         }

         var1 = this.props.getProperty("awt.print.printer");
         if (var1 != null) {
            this.jobAttributes.setPrinter(var1);
         }

         var1 = this.props.getProperty("awt.print.fileName");
         if (var1 != null) {
            this.jobAttributes.setFileName(var1);
         }

         var1 = this.props.getProperty("awt.print.numCopies");
         if (var1 != null) {
            this.jobAttributes.setCopies(Integer.parseInt(var1));
         }

         this.options = this.props.getProperty("awt.print.options", "");
         var1 = this.props.getProperty("awt.print.orientation");
         if (var1 != null) {
            if (var1.equals("portrait")) {
               this.pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
            } else if (var1.equals("landscape")) {
               this.pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.LANDSCAPE);
            }
         }

         var1 = this.props.getProperty("awt.print.paperSize");
         if (var1 != null) {
            if (var1.equals("letter")) {
               this.pageAttributes.setMedia(SIZES[PageAttributes.MediaType.LETTER.hashCode()]);
            } else if (var1.equals("legal")) {
               this.pageAttributes.setMedia(SIZES[PageAttributes.MediaType.LEGAL.hashCode()]);
            } else if (var1.equals("executive")) {
               this.pageAttributes.setMedia(SIZES[PageAttributes.MediaType.EXECUTIVE.hashCode()]);
            } else if (var1.equals("a4")) {
               this.pageAttributes.setMedia(SIZES[PageAttributes.MediaType.A4.hashCode()]);
            }
         }

      }
   }

   private void translateOutputProps() {
      if (this.props != null) {
         this.props.setProperty("awt.print.destination", this.jobAttributes.getDestination() == JobAttributes.DestinationType.PRINTER ? "printer" : "file");
         String var1 = this.jobAttributes.getPrinter();
         if (var1 != null && !var1.equals("")) {
            this.props.setProperty("awt.print.printer", var1);
         }

         var1 = this.jobAttributes.getFileName();
         if (var1 != null && !var1.equals("")) {
            this.props.setProperty("awt.print.fileName", var1);
         }

         int var2 = this.jobAttributes.getCopies();
         if (var2 > 0) {
            this.props.setProperty("awt.print.numCopies", "" + var2);
         }

         var1 = this.options;
         if (var1 != null && !var1.equals("")) {
            this.props.setProperty("awt.print.options", var1);
         }

         this.props.setProperty("awt.print.orientation", this.pageAttributes.getOrientationRequested() == PageAttributes.OrientationRequestedType.PORTRAIT ? "portrait" : "landscape");
         PageAttributes.MediaType var3 = SIZES[this.pageAttributes.getMedia().hashCode()];
         if (var3 == PageAttributes.MediaType.LETTER) {
            var1 = "letter";
         } else if (var3 == PageAttributes.MediaType.LEGAL) {
            var1 = "legal";
         } else if (var3 == PageAttributes.MediaType.EXECUTIVE) {
            var1 = "executive";
         } else if (var3 == PageAttributes.MediaType.A4) {
            var1 = "a4";
         } else {
            var1 = var3.toString();
         }

         this.props.setProperty("awt.print.paperSize", var1);
      }
   }

   private void throwPrintToFile() {
      SecurityManager var1 = System.getSecurityManager();
      FilePermission var2 = null;
      if (var1 != null) {
         if (var2 == null) {
            var2 = new FilePermission("<<ALL FILES>>", "read,write");
         }

         var1.checkPermission(var2);
      }

   }

   static {
      SIZES = new PageAttributes.MediaType[]{PageAttributes.MediaType.ISO_4A0, PageAttributes.MediaType.ISO_2A0, PageAttributes.MediaType.ISO_A0, PageAttributes.MediaType.ISO_A1, PageAttributes.MediaType.ISO_A2, PageAttributes.MediaType.ISO_A3, PageAttributes.MediaType.ISO_A4, PageAttributes.MediaType.ISO_A5, PageAttributes.MediaType.ISO_A6, PageAttributes.MediaType.ISO_A7, PageAttributes.MediaType.ISO_A8, PageAttributes.MediaType.ISO_A9, PageAttributes.MediaType.ISO_A10, PageAttributes.MediaType.ISO_B0, PageAttributes.MediaType.ISO_B1, PageAttributes.MediaType.ISO_B2, PageAttributes.MediaType.ISO_B3, PageAttributes.MediaType.ISO_B4, PageAttributes.MediaType.ISO_B5, PageAttributes.MediaType.ISO_B6, PageAttributes.MediaType.ISO_B7, PageAttributes.MediaType.ISO_B8, PageAttributes.MediaType.ISO_B9, PageAttributes.MediaType.ISO_B10, PageAttributes.MediaType.JIS_B0, PageAttributes.MediaType.JIS_B1, PageAttributes.MediaType.JIS_B2, PageAttributes.MediaType.JIS_B3, PageAttributes.MediaType.JIS_B4, PageAttributes.MediaType.JIS_B5, PageAttributes.MediaType.JIS_B6, PageAttributes.MediaType.JIS_B7, PageAttributes.MediaType.JIS_B8, PageAttributes.MediaType.JIS_B9, PageAttributes.MediaType.JIS_B10, PageAttributes.MediaType.ISO_C0, PageAttributes.MediaType.ISO_C1, PageAttributes.MediaType.ISO_C2, PageAttributes.MediaType.ISO_C3, PageAttributes.MediaType.ISO_C4, PageAttributes.MediaType.ISO_C5, PageAttributes.MediaType.ISO_C6, PageAttributes.MediaType.ISO_C7, PageAttributes.MediaType.ISO_C8, PageAttributes.MediaType.ISO_C9, PageAttributes.MediaType.ISO_C10, PageAttributes.MediaType.ISO_DESIGNATED_LONG, PageAttributes.MediaType.EXECUTIVE, PageAttributes.MediaType.FOLIO, PageAttributes.MediaType.INVOICE, PageAttributes.MediaType.LEDGER, PageAttributes.MediaType.NA_LETTER, PageAttributes.MediaType.NA_LEGAL, PageAttributes.MediaType.QUARTO, PageAttributes.MediaType.A, PageAttributes.MediaType.B, PageAttributes.MediaType.C, PageAttributes.MediaType.D, PageAttributes.MediaType.E, PageAttributes.MediaType.NA_10X15_ENVELOPE, PageAttributes.MediaType.NA_10X14_ENVELOPE, PageAttributes.MediaType.NA_10X13_ENVELOPE, PageAttributes.MediaType.NA_9X12_ENVELOPE, PageAttributes.MediaType.NA_9X11_ENVELOPE, PageAttributes.MediaType.NA_7X9_ENVELOPE, PageAttributes.MediaType.NA_6X9_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_9_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_10_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_11_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_12_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_14_ENVELOPE, PageAttributes.MediaType.INVITE_ENVELOPE, PageAttributes.MediaType.ITALY_ENVELOPE, PageAttributes.MediaType.MONARCH_ENVELOPE, PageAttributes.MediaType.PERSONAL_ENVELOPE};
      JAVAXSIZES = new MediaSizeName[]{null, null, MediaSizeName.ISO_A0, MediaSizeName.ISO_A1, MediaSizeName.ISO_A2, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.ISO_A6, MediaSizeName.ISO_A7, MediaSizeName.ISO_A8, MediaSizeName.ISO_A9, MediaSizeName.ISO_A10, MediaSizeName.ISO_B0, MediaSizeName.ISO_B1, MediaSizeName.ISO_B2, MediaSizeName.ISO_B3, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5, MediaSizeName.ISO_B6, MediaSizeName.ISO_B7, MediaSizeName.ISO_B8, MediaSizeName.ISO_B9, MediaSizeName.ISO_B10, MediaSizeName.JIS_B0, MediaSizeName.JIS_B1, MediaSizeName.JIS_B2, MediaSizeName.JIS_B3, MediaSizeName.JIS_B4, MediaSizeName.JIS_B5, MediaSizeName.JIS_B6, MediaSizeName.JIS_B7, MediaSizeName.JIS_B8, MediaSizeName.JIS_B9, MediaSizeName.JIS_B10, MediaSizeName.ISO_C0, MediaSizeName.ISO_C1, MediaSizeName.ISO_C2, MediaSizeName.ISO_C3, MediaSizeName.ISO_C4, MediaSizeName.ISO_C5, MediaSizeName.ISO_C6, null, null, null, null, MediaSizeName.ISO_DESIGNATED_LONG, MediaSizeName.EXECUTIVE, MediaSizeName.FOLIO, MediaSizeName.INVOICE, MediaSizeName.LEDGER, MediaSizeName.NA_LETTER, MediaSizeName.NA_LEGAL, MediaSizeName.QUARTO, MediaSizeName.A, MediaSizeName.B, MediaSizeName.C, MediaSizeName.D, MediaSizeName.E, MediaSizeName.NA_10X15_ENVELOPE, MediaSizeName.NA_10X14_ENVELOPE, MediaSizeName.NA_10X13_ENVELOPE, MediaSizeName.NA_9X12_ENVELOPE, MediaSizeName.NA_9X11_ENVELOPE, MediaSizeName.NA_7X9_ENVELOPE, MediaSizeName.NA_6X9_ENVELOPE, MediaSizeName.NA_NUMBER_9_ENVELOPE, MediaSizeName.NA_NUMBER_10_ENVELOPE, MediaSizeName.NA_NUMBER_11_ENVELOPE, MediaSizeName.NA_NUMBER_12_ENVELOPE, MediaSizeName.NA_NUMBER_14_ENVELOPE, null, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.MONARCH_ENVELOPE, MediaSizeName.PERSONAL_ENVELOPE};
      WIDTHS = new int[]{4768, 3370, 2384, 1684, 1191, 842, 595, 420, 298, 210, 147, 105, 74, 2835, 2004, 1417, 1001, 709, 499, 354, 249, 176, 125, 88, 2920, 2064, 1460, 1032, 729, 516, 363, 258, 181, 128, 91, 2599, 1837, 1298, 918, 649, 459, 323, 230, 162, 113, 79, 312, 522, 612, 396, 792, 612, 612, 609, 612, 792, 1224, 1584, 2448, 720, 720, 720, 648, 648, 504, 432, 279, 297, 324, 342, 360, 624, 312, 279, 261};
      LENGTHS = new int[]{6741, 4768, 3370, 2384, 1684, 1191, 842, 595, 420, 298, 210, 147, 105, 4008, 2835, 2004, 1417, 1001, 729, 499, 354, 249, 176, 125, 4127, 2920, 2064, 1460, 1032, 729, 516, 363, 258, 181, 128, 3677, 2599, 1837, 1298, 918, 649, 459, 323, 230, 162, 113, 624, 756, 936, 612, 1224, 792, 1008, 780, 792, 1224, 1584, 2448, 3168, 1080, 1008, 936, 864, 792, 648, 648, 639, 684, 747, 792, 828, 624, 652, 540, 468};
   }

   private class MessageQ {
      private String qid = "noname";
      private ArrayList queue = new ArrayList();

      MessageQ(String var2) {
         this.qid = var2;
      }

      synchronized void closeWhenEmpty() {
         while(this.queue != null && this.queue.size() > 0) {
            try {
               this.wait(1000L);
            } catch (InterruptedException var2) {
            }
         }

         this.queue = null;
         this.notifyAll();
      }

      synchronized void close() {
         this.queue = null;
         this.notifyAll();
      }

      synchronized boolean append(Graphics2D var1) {
         boolean var2 = false;
         if (this.queue != null) {
            this.queue.add(var1);
            var2 = true;
            this.notify();
         }

         return var2;
      }

      synchronized Graphics2D pop() {
         Graphics2D var1 = null;

         while(var1 == null && this.queue != null) {
            if (this.queue.size() > 0) {
               var1 = (Graphics2D)this.queue.remove(0);
               this.notify();
            } else {
               try {
                  this.wait(2000L);
               } catch (InterruptedException var3) {
               }
            }
         }

         return var1;
      }

      synchronized boolean isClosed() {
         return this.queue == null;
      }
   }
}
