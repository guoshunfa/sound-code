package sun.print;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Locale;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobSheets;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import sun.awt.image.ByteInterleavedRaster;
import sun.security.action.GetPropertyAction;

public abstract class RasterPrinterJob extends PrinterJob {
   protected static final int PRINTER = 0;
   protected static final int FILE = 1;
   protected static final int STREAM = 2;
   protected static final int MAX_UNKNOWN_PAGES = 9999;
   protected static final int PD_ALLPAGES = 0;
   protected static final int PD_SELECTION = 1;
   protected static final int PD_PAGENUMS = 2;
   protected static final int PD_NOSELECTION = 4;
   private static final int MAX_BAND_SIZE = 4194304;
   private static final float DPI = 72.0F;
   private static final String FORCE_PIPE_PROP = "sun.java2d.print.pipeline";
   private static final String FORCE_RASTER = "raster";
   private static final String FORCE_PDL = "pdl";
   private static final String SHAPE_TEXT_PROP = "sun.java2d.print.shapetext";
   public static boolean forcePDL = false;
   public static boolean forceRaster = false;
   public static boolean shapeTextProp = false;
   private int cachedBandWidth = 0;
   private int cachedBandHeight = 0;
   private BufferedImage cachedBand = null;
   private int mNumCopies = 1;
   private boolean mCollate = false;
   private int mFirstPage = -1;
   private int mLastPage = -1;
   private Paper previousPaper;
   protected Pageable mDocument = new Book();
   private String mDocName = "Java Printing";
   protected boolean performingPrinting = false;
   protected boolean userCancelled = false;
   private FilePermission printToFilePermission;
   private ArrayList redrawList = new ArrayList();
   private int copiesAttr;
   private String jobNameAttr;
   private String userNameAttr;
   private PageRanges pageRangesAttr;
   protected Sides sidesAttr;
   protected String destinationAttr;
   protected boolean noJobSheet = false;
   protected int mDestType = 1;
   protected String mDestination = "";
   protected boolean collateAttReq = false;
   protected boolean landscapeRotates270 = false;
   protected PrintRequestAttributeSet attributes = null;
   protected PrintService myService;
   public static boolean debugPrint;
   private int deviceWidth;
   private int deviceHeight;
   private AffineTransform defaultDeviceTransform;
   private PrinterGraphicsConfig pgConfig;
   private DialogOnTop onTop = null;
   private long parentWindowID = 0L;

   protected abstract double getXRes();

   protected abstract double getYRes();

   protected abstract double getPhysicalPrintableX(Paper var1);

   protected abstract double getPhysicalPrintableY(Paper var1);

   protected abstract double getPhysicalPrintableWidth(Paper var1);

   protected abstract double getPhysicalPrintableHeight(Paper var1);

   protected abstract double getPhysicalPageWidth(Paper var1);

   protected abstract double getPhysicalPageHeight(Paper var1);

   protected abstract void startPage(PageFormat var1, Printable var2, int var3, boolean var4) throws PrinterException;

   protected abstract void endPage(PageFormat var1, Printable var2, int var3) throws PrinterException;

   protected abstract void printBand(byte[] var1, int var2, int var3, int var4, int var5) throws PrinterException;

   public void saveState(AffineTransform var1, Shape var2, Rectangle2D var3, double var4, double var6) {
      RasterPrinterJob.GraphicsState var8 = new RasterPrinterJob.GraphicsState();
      var8.theTransform = var1;
      var8.theClip = var2;
      var8.region = var3;
      var8.sx = var4;
      var8.sy = var6;
      this.redrawList.add(var8);
   }

   protected static PrintService lookupDefaultPrintService() {
      PrintService var0 = PrintServiceLookup.lookupDefaultPrintService();
      if (var0 != null && var0.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && var0.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
         return var0;
      } else {
         PrintService[] var1 = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, (AttributeSet)null);
         return var1.length > 0 ? var1[0] : null;
      }
   }

   public PrintService getPrintService() {
      if (this.myService == null) {
         PrintService var1 = PrintServiceLookup.lookupDefaultPrintService();
         if (var1 != null && var1.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) {
            try {
               this.setPrintService(var1);
               this.myService = var1;
            } catch (PrinterException var5) {
            }
         }

         if (this.myService == null) {
            PrintService[] var2 = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, (AttributeSet)null);
            if (var2.length > 0) {
               try {
                  this.setPrintService(var2[0]);
                  this.myService = var2[0];
               } catch (PrinterException var4) {
               }
            }
         }
      }

      return this.myService;
   }

   public void setPrintService(PrintService var1) throws PrinterException {
      if (var1 == null) {
         throw new PrinterException("Service cannot be null");
      } else if (!(var1 instanceof StreamPrintService) && var1.getName() == null) {
         throw new PrinterException("Null PrintService name.");
      } else {
         PrinterState var2 = (PrinterState)var1.getAttribute(PrinterState.class);
         if (var2 == PrinterState.STOPPED) {
            PrinterStateReasons var3 = (PrinterStateReasons)var1.getAttribute(PrinterStateReasons.class);
            if (var3 != null && var3.containsKey(PrinterStateReason.SHUTDOWN)) {
               throw new PrinterException("PrintService is no longer available.");
            }
         }

         if (var1.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && var1.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
            this.myService = var1;
         } else {
            throw new PrinterException("Not a 2D print service: " + var1);
         }
      }
   }

   private PageFormat attributeToPageFormat(PrintService var1, PrintRequestAttributeSet var2) {
      PageFormat var3 = this.defaultPage();
      if (var1 == null) {
         return var3;
      } else {
         OrientationRequested var4 = (OrientationRequested)var2.get(OrientationRequested.class);
         if (var4 == null) {
            var4 = (OrientationRequested)var1.getDefaultAttributeValue(OrientationRequested.class);
         }

         if (var4 == OrientationRequested.REVERSE_LANDSCAPE) {
            var3.setOrientation(2);
         } else if (var4 == OrientationRequested.LANDSCAPE) {
            var3.setOrientation(0);
         } else {
            var3.setOrientation(1);
         }

         Media var5 = (Media)var2.get(Media.class);
         MediaSize var6 = this.getMediaSize(var5, var1, var3);
         Paper var7 = new Paper();
         float[] var8 = var6.getSize(1);
         double var9 = Math.rint((double)var8[0] * 72.0D / 25400.0D);
         double var11 = Math.rint((double)var8[1] * 72.0D / 25400.0D);
         var7.setSize(var9, var11);
         MediaPrintableArea var13 = (MediaPrintableArea)var2.get(MediaPrintableArea.class);
         if (var13 == null) {
            var13 = this.getDefaultPrintableArea(var3, var9, var11);
         }

         double var14 = Math.rint((double)(var13.getX(25400) * 72.0F));
         double var18 = Math.rint((double)(var13.getY(25400) * 72.0F));
         double var16 = Math.rint((double)(var13.getWidth(25400) * 72.0F));
         double var20 = Math.rint((double)(var13.getHeight(25400) * 72.0F));
         var7.setImageableArea(var14, var18, var16, var20);
         var3.setPaper(var7);
         return var3;
      }
   }

   protected MediaSize getMediaSize(Media var1, PrintService var2, PageFormat var3) {
      if (var1 == null) {
         var1 = (Media)var2.getDefaultAttributeValue(Media.class);
      }

      if (!(var1 instanceof MediaSizeName)) {
         var1 = MediaSizeName.NA_LETTER;
      }

      MediaSize var4 = MediaSize.getMediaSizeForName((MediaSizeName)var1);
      return var4 != null ? var4 : MediaSize.NA.LETTER;
   }

   protected MediaPrintableArea getDefaultPrintableArea(PageFormat var1, double var2, double var4) {
      double var6;
      double var8;
      if (var2 >= 432.0D) {
         var6 = 72.0D;
         var8 = var2 - 144.0D;
      } else {
         var6 = var2 / 6.0D;
         var8 = var2 * 0.75D;
      }

      double var10;
      double var12;
      if (var4 >= 432.0D) {
         var10 = 72.0D;
         var12 = var4 - 144.0D;
      } else {
         var10 = var4 / 6.0D;
         var12 = var4 * 0.75D;
      }

      return new MediaPrintableArea((float)(var6 / 72.0D), (float)(var10 / 72.0D), (float)(var8 / 72.0D), (float)(var12 / 72.0D), 25400);
   }

   protected void updatePageAttributes(PrintService var1, PageFormat var2) {
      if (this.attributes == null) {
         this.attributes = new HashPrintRequestAttributeSet();
      }

      this.updateAttributesWithPageFormat(var1, var2, this.attributes);
   }

   protected void updateAttributesWithPageFormat(PrintService var1, PageFormat var2, PrintRequestAttributeSet var3) {
      if (var1 != null && var2 != null && var3 != null) {
         float var4 = (float)Math.rint(var2.getPaper().getWidth() * 25400.0D / 72.0D) / 25400.0F;
         float var5 = (float)Math.rint(var2.getPaper().getHeight() * 25400.0D / 72.0D) / 25400.0F;
         Media[] var6 = (Media[])((Media[])var1.getSupportedAttributeValues(Media.class, (DocFlavor)null, (AttributeSet)null));
         Object var7 = null;

         try {
            var7 = CustomMediaSizeName.findMedia(var6, var4, var5, 25400);
         } catch (IllegalArgumentException var15) {
         }

         if (var7 == null || !var1.isAttributeValueSupported((Attribute)var7, (DocFlavor)null, (AttributeSet)null)) {
            var7 = (Media)var1.getDefaultAttributeValue(Media.class);
         }

         OrientationRequested var8;
         switch(var2.getOrientation()) {
         case 0:
            var8 = OrientationRequested.LANDSCAPE;
            break;
         case 2:
            var8 = OrientationRequested.REVERSE_LANDSCAPE;
            break;
         default:
            var8 = OrientationRequested.PORTRAIT;
         }

         if (var7 != null) {
            var3.add((Attribute)var7);
         }

         var3.add(var8);
         float var9 = (float)(var2.getPaper().getImageableX() / 72.0D);
         float var10 = (float)(var2.getPaper().getImageableWidth() / 72.0D);
         float var11 = (float)(var2.getPaper().getImageableY() / 72.0D);
         float var12 = (float)(var2.getPaper().getImageableHeight() / 72.0D);
         if (var9 < 0.0F) {
            var9 = 0.0F;
         }

         if (var11 < 0.0F) {
            var11 = 0.0F;
         }

         try {
            var3.add(new MediaPrintableArea(var9, var11, var10, var12, 25400));
         } catch (IllegalArgumentException var14) {
         }

      }
   }

   public PageFormat pageDialog(PageFormat var1) throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         final GraphicsConfiguration var2 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
         PrintService var3 = (PrintService)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               PrintService var1 = RasterPrinterJob.this.getPrintService();
               if (var1 == null) {
                  ServiceDialog.showNoPrintService(var2);
                  return null;
               } else {
                  return var1;
               }
            }
         });
         if (var3 == null) {
            return var1;
         } else {
            this.updatePageAttributes(var3, var1);
            PageFormat var4 = null;
            DialogTypeSelection var5 = (DialogTypeSelection)this.attributes.get(DialogTypeSelection.class);
            if (var5 == DialogTypeSelection.NATIVE) {
               this.attributes.remove(DialogTypeSelection.class);
               var4 = this.pageDialog(this.attributes);
               this.attributes.add(DialogTypeSelection.NATIVE);
            } else {
               var4 = this.pageDialog(this.attributes);
            }

            return var4 == null ? var1 : var4;
         }
      }
   }

   public PageFormat pageDialog(PrintRequestAttributeSet var1) throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         DialogTypeSelection var2 = (DialogTypeSelection)var1.get(DialogTypeSelection.class);
         if (var2 == DialogTypeSelection.NATIVE) {
            PrintService var11 = this.getPrintService();
            PageFormat var12 = this.attributeToPageFormat(var11, var1);
            this.setParentWindowID(var1);
            PageFormat var13 = this.pageDialog(var12);
            this.clearParentWindowID();
            if (var13 == var12) {
               return null;
            } else {
               this.updateAttributesWithPageFormat(var11, var13, var1);
               return var13;
            }
         } else {
            final GraphicsConfiguration var3 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            Rectangle var4 = var3.getBounds();
            int var5 = var4.x + var4.width / 3;
            int var6 = var4.y + var4.height / 3;
            PrintService var7 = (PrintService)AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  PrintService var1 = RasterPrinterJob.this.getPrintService();
                  if (var1 == null) {
                     ServiceDialog.showNoPrintService(var3);
                     return null;
                  } else {
                     return var1;
                  }
               }
            });
            if (var7 == null) {
               return null;
            } else {
               if (this.onTop != null) {
                  var1.add(this.onTop);
               }

               ServiceDialog var8 = new ServiceDialog(var3, var5, var6, var7, DocFlavor.SERVICE_FORMATTED.PAGEABLE, var1, (Frame)null);
               var8.show();
               if (var8.getStatus() == 1) {
                  PrintRequestAttributeSet var9 = var8.getAttributes();
                  Class var10 = SunAlternateMedia.class;
                  if (var1.containsKey(var10) && !var9.containsKey(var10)) {
                     var1.remove(var10);
                  }

                  var1.addAll(var9);
                  return this.attributeToPageFormat(var7, var1);
               } else {
                  return null;
               }
            }
         }
      }
   }

   protected PageFormat getPageFormatFromAttributes() {
      if (this.attributes != null && !this.attributes.isEmpty()) {
         PageFormat var1 = this.attributeToPageFormat(this.getPrintService(), this.attributes);
         PageFormat var2 = null;
         Pageable var3 = this.getPageable();
         if (var3 != null && var3 instanceof OpenBook && (var2 = var3.getPageFormat(0)) != null) {
            if (this.attributes.get(OrientationRequested.class) == null) {
               var1.setOrientation(var2.getOrientation());
            }

            Paper var4 = var1.getPaper();
            Paper var5 = var2.getPaper();
            boolean var6 = false;
            if (this.attributes.get(MediaSizeName.class) == null) {
               var4.setSize(var5.getWidth(), var5.getHeight());
               var6 = true;
            }

            if (this.attributes.get(MediaPrintableArea.class) == null) {
               var4.setImageableArea(var5.getImageableX(), var5.getImageableY(), var5.getImageableWidth(), var5.getImageableHeight());
               var6 = true;
            }

            if (var6) {
               var1.setPaper(var4);
            }
         }

         return var1;
      } else {
         return null;
      }
   }

   public boolean printDialog(PrintRequestAttributeSet var1) throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         DialogTypeSelection var2 = (DialogTypeSelection)var1.get(DialogTypeSelection.class);
         if (var2 == DialogTypeSelection.NATIVE) {
            this.attributes = var1;

            try {
               this.debug_println("calling setAttributes in printDialog");
               this.setAttributes(var1);
            } catch (PrinterException var13) {
            }

            this.setParentWindowID(var1);
            boolean var16 = this.printDialog();
            this.clearParentWindowID();
            this.attributes = var1;
            return var16;
         } else {
            final GraphicsConfiguration var3 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            PrintService var4 = (PrintService)AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  PrintService var1 = RasterPrinterJob.this.getPrintService();
                  if (var1 == null) {
                     ServiceDialog.showNoPrintService(var3);
                     return null;
                  } else {
                     return var1;
                  }
               }
            });
            if (var4 == null) {
               return false;
            } else {
               StreamPrintServiceFactory[] var6 = null;
               Object var5;
               if (var4 instanceof StreamPrintService) {
                  var6 = lookupStreamPrintServices((String)null);
                  var5 = new StreamPrintService[var6.length];

                  for(int var7 = 0; var7 < var6.length; ++var7) {
                     ((Object[])var5)[var7] = var6[var7].getPrintService((OutputStream)null);
                  }
               } else {
                  var5 = (PrintService[])((PrintService[])AccessController.doPrivileged(new PrivilegedAction() {
                     public Object run() {
                        PrintService[] var1 = PrinterJob.lookupPrintServices();
                        return var1;
                     }
                  }));
                  if (var5 == null || ((Object[])var5).length == 0) {
                     var5 = new PrintService[]{var4};
                  }
               }

               Rectangle var17 = var3.getBounds();
               int var8 = var17.x + var17.width / 3;
               int var9 = var17.y + var17.height / 3;
               PrinterJobWrapper var11 = new PrinterJobWrapper(this);
               var1.add(var11);

               PrintService var10;
               try {
                  var10 = ServiceUI.printDialog(var3, var8, var9, (PrintService[])var5, var4, DocFlavor.SERVICE_FORMATTED.PAGEABLE, var1);
               } catch (IllegalArgumentException var15) {
                  var10 = ServiceUI.printDialog(var3, var8, var9, (PrintService[])var5, (PrintService)((Object[])var5)[0], DocFlavor.SERVICE_FORMATTED.PAGEABLE, var1);
               }

               var1.remove(PrinterJobWrapper.class);
               if (var10 == null) {
                  return false;
               } else {
                  if (!var4.equals(var10)) {
                     try {
                        this.setPrintService(var10);
                     } catch (PrinterException var14) {
                        this.myService = var10;
                     }
                  }

                  return true;
               }
            }
         }
      }
   }

   public boolean printDialog() throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         HashPrintRequestAttributeSet var1 = new HashPrintRequestAttributeSet();
         var1.add(new Copies(this.getCopies()));
         var1.add(new JobName(this.getJobName(), (Locale)null));
         boolean var2 = this.printDialog(var1);
         if (var2) {
            JobName var3 = (JobName)var1.get(JobName.class);
            if (var3 != null) {
               this.setJobName(var3.getValue());
            }

            Copies var4 = (Copies)var1.get(Copies.class);
            if (var4 != null) {
               this.setCopies(var4.getValue());
            }

            Destination var5 = (Destination)var1.get(Destination.class);
            if (var5 != null) {
               try {
                  this.mDestType = 1;
                  this.mDestination = (new File(var5.getURI())).getPath();
               } catch (Exception var9) {
                  this.mDestination = "out.prn";
                  PrintService var7 = this.getPrintService();
                  if (var7 != null) {
                     Destination var8 = (Destination)var7.getDefaultAttributeValue(Destination.class);
                     if (var8 != null) {
                        this.mDestination = (new File(var8.getURI())).getPath();
                     }
                  }
               }
            } else {
               this.mDestType = 0;
               PrintService var6 = this.getPrintService();
               if (var6 != null) {
                  this.mDestination = var6.getName();
               }
            }
         }

         return var2;
      }
   }

   public void setPrintable(Printable var1) {
      this.setPageable(new OpenBook(this.defaultPage(new PageFormat()), var1));
   }

   public void setPrintable(Printable var1, PageFormat var2) {
      this.setPageable(new OpenBook(var2, var1));
      this.updatePageAttributes(this.getPrintService(), var2);
   }

   public void setPageable(Pageable var1) throws NullPointerException {
      if (var1 != null) {
         this.mDocument = var1;
      } else {
         throw new NullPointerException();
      }
   }

   protected void initPrinter() {
   }

   protected boolean isSupportedValue(Attribute var1, PrintRequestAttributeSet var2) {
      PrintService var3 = this.getPrintService();
      return var1 != null && var3 != null && var3.isAttributeValueSupported(var1, DocFlavor.SERVICE_FORMATTED.PAGEABLE, var2);
   }

   protected void setAttributes(PrintRequestAttributeSet var1) throws PrinterException {
      this.setCollated(false);
      this.sidesAttr = null;
      this.pageRangesAttr = null;
      this.copiesAttr = 0;
      this.jobNameAttr = null;
      this.userNameAttr = null;
      this.destinationAttr = null;
      this.collateAttReq = false;
      PrintService var2 = this.getPrintService();
      if (var1 != null && var2 != null) {
         boolean var3 = false;
         Fidelity var4 = (Fidelity)var1.get(Fidelity.class);
         if (var4 != null && var4 == Fidelity.FIDELITY_TRUE) {
            var3 = true;
         }

         if (var3) {
            AttributeSet var5 = var2.getUnsupportedAttributes(DocFlavor.SERVICE_FORMATTED.PAGEABLE, var1);
            if (var5 != null) {
               throw new PrinterException("Fidelity cannot be satisfied");
            }
         }

         SheetCollate var24 = (SheetCollate)var1.get(SheetCollate.class);
         if (this.isSupportedValue(var24, var1)) {
            this.setCollated(var24 == SheetCollate.COLLATED);
         }

         this.sidesAttr = (Sides)var1.get(Sides.class);
         if (!this.isSupportedValue(this.sidesAttr, var1)) {
            this.sidesAttr = Sides.ONE_SIDED;
         }

         this.pageRangesAttr = (PageRanges)var1.get(PageRanges.class);
         if (!this.isSupportedValue(this.pageRangesAttr, var1)) {
            this.pageRangesAttr = null;
         } else if ((SunPageSelection)var1.get(SunPageSelection.class) == SunPageSelection.RANGE) {
            int[][] var6 = this.pageRangesAttr.getMembers();
            this.setPageRange(var6[0][0] - 1, var6[0][1] - 1);
         } else {
            this.setPageRange(-1, -1);
         }

         Copies var25 = (Copies)var1.get(Copies.class);
         if (!this.isSupportedValue(var25, var1) && (var3 || var25 == null)) {
            this.copiesAttr = this.getCopies();
         } else {
            this.copiesAttr = var25.getValue();
            this.setCopies(this.copiesAttr);
         }

         Destination var7 = (Destination)var1.get(Destination.class);
         if (this.isSupportedValue(var7, var1)) {
            try {
               this.destinationAttr = "" + new File(var7.getURI().getSchemeSpecificPart());
            } catch (Exception var23) {
               Destination var9 = (Destination)var2.getDefaultAttributeValue(Destination.class);
               if (var9 != null) {
                  this.destinationAttr = "" + new File(var9.getURI().getSchemeSpecificPart());
               }
            }
         }

         JobSheets var8 = (JobSheets)var1.get(JobSheets.class);
         if (var8 != null) {
            this.noJobSheet = var8 == JobSheets.NONE;
         }

         JobName var26 = (JobName)var1.get(JobName.class);
         if (!this.isSupportedValue(var26, var1) && (var3 || var26 == null)) {
            this.jobNameAttr = this.getJobName();
         } else {
            this.jobNameAttr = var26.getValue();
            this.setJobName(this.jobNameAttr);
         }

         RequestingUserName var10 = (RequestingUserName)var1.get(RequestingUserName.class);
         if (this.isSupportedValue(var10, var1) || !var3 && var10 != null) {
            this.userNameAttr = var10.getValue();
         } else {
            try {
               this.userNameAttr = this.getUserName();
            } catch (SecurityException var22) {
               this.userNameAttr = "";
            }
         }

         Media var11 = (Media)var1.get(Media.class);
         OrientationRequested var12 = (OrientationRequested)var1.get(OrientationRequested.class);
         MediaPrintableArea var13 = (MediaPrintableArea)var1.get(MediaPrintableArea.class);
         if ((var12 != null || var11 != null || var13 != null) && this.getPageable() instanceof OpenBook) {
            Pageable var14 = this.getPageable();
            Printable var15 = var14.getPrintable(0);
            PageFormat var16 = (PageFormat)var14.getPageFormat(0).clone();
            Paper var17 = var16.getPaper();
            if (var13 == null && var11 != null && var2.isAttributeCategorySupported(MediaPrintableArea.class)) {
               Object var18 = var2.getSupportedAttributeValues(MediaPrintableArea.class, (DocFlavor)null, var1);
               if (var18 instanceof MediaPrintableArea[] && ((MediaPrintableArea[])((MediaPrintableArea[])var18)).length > 0) {
                  var13 = ((MediaPrintableArea[])((MediaPrintableArea[])var18))[0];
               }
            }

            if (this.isSupportedValue(var12, var1) || !var3 && var12 != null) {
               byte var27;
               if (var12.equals(OrientationRequested.REVERSE_LANDSCAPE)) {
                  var27 = 2;
               } else if (var12.equals(OrientationRequested.LANDSCAPE)) {
                  var27 = 0;
               } else {
                  var27 = 1;
               }

               var16.setOrientation(var27);
            }

            if ((this.isSupportedValue(var11, var1) || !var3 && var11 != null) && var11 instanceof MediaSizeName) {
               MediaSizeName var29 = (MediaSizeName)var11;
               MediaSize var19 = MediaSize.getMediaSizeForName(var29);
               if (var19 != null) {
                  float var20 = var19.getX(25400) * 72.0F;
                  float var21 = var19.getY(25400) * 72.0F;
                  var17.setSize((double)var20, (double)var21);
                  if (var13 == null) {
                     var17.setImageableArea(72.0D, 72.0D, (double)var20 - 144.0D, (double)var21 - 144.0D);
                  }
               }
            }

            if (this.isSupportedValue(var13, var1) || !var3 && var13 != null) {
               float[] var30 = var13.getPrintableArea(25400);

               for(int var28 = 0; var28 < var30.length; ++var28) {
                  var30[var28] *= 72.0F;
               }

               var17.setImageableArea((double)var30[0], (double)var30[1], (double)var30[2], (double)var30[3]);
            }

            var16.setPaper(var17);
            var16 = this.validatePage(var16);
            this.setPrintable(var15, var16);
         } else {
            this.attributes = var1;
         }

      }
   }

   protected void spoolToService(PrintService var1, PrintRequestAttributeSet var2) throws PrinterException {
      if (var1 == null) {
         throw new PrinterException("No print service found.");
      } else {
         DocPrintJob var3 = var1.createPrintJob();
         PageableDoc var4 = new PageableDoc(this.getPageable());
         if (var2 == null) {
            var2 = new HashPrintRequestAttributeSet();
         }

         try {
            var3.print(var4, (PrintRequestAttributeSet)var2);
         } catch (PrintException var6) {
            throw new PrinterException(var6.toString());
         }
      }
   }

   public void print() throws PrinterException {
      this.print(this.attributes);
   }

   protected void debug_println(String var1) {
      if (debugPrint) {
         System.out.println("RasterPrinterJob " + var1 + " " + this);
      }

   }

   public void print(PrintRequestAttributeSet var1) throws PrinterException {
      PrintService var2 = this.getPrintService();
      this.debug_println("psvc = " + var2);
      if (var2 == null) {
         throw new PrinterException("No print service found.");
      } else {
         PrinterState var3 = (PrinterState)var2.getAttribute(PrinterState.class);
         if (var3 == PrinterState.STOPPED) {
            PrinterStateReasons var4 = (PrinterStateReasons)var2.getAttribute(PrinterStateReasons.class);
            if (var4 != null && var4.containsKey(PrinterStateReason.SHUTDOWN)) {
               throw new PrinterException("PrintService is no longer available.");
            }
         }

         if ((PrinterIsAcceptingJobs)var2.getAttribute(PrinterIsAcceptingJobs.class) == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS) {
            throw new PrinterException("Printer is not accepting job.");
         } else if (var2 instanceof SunPrinterJobService && ((SunPrinterJobService)var2).usesClass(this.getClass())) {
            this.setAttributes(var1);
            if (this.destinationAttr != null) {
               this.validateDestination(this.destinationAttr);
            }

            this.initPrinter();
            int var27 = this.getCollatedCopies();
            int var5 = this.getNoncollatedCopies();
            this.debug_println("getCollatedCopies()  " + var27 + " getNoncollatedCopies() " + var5);
            int var6 = this.mDocument.getNumberOfPages();
            if (var6 != 0) {
               int var7 = this.getFirstPage();
               int var8 = this.getLastPage();
               if (var8 == -1) {
                  int var9 = this.mDocument.getNumberOfPages();
                  if (var9 != -1) {
                     var8 = this.mDocument.getNumberOfPages() - 1;
                  }
               }

               boolean var22 = false;

               try {
                  var22 = true;
                  synchronized(this) {
                     this.performingPrinting = true;
                     this.userCancelled = false;
                  }

                  this.startDoc();
                  if (this.isCancelled()) {
                     this.cancelDoc();
                  }

                  boolean var28 = true;
                  if (var1 != null) {
                     SunPageSelection var10 = (SunPageSelection)var1.get(SunPageSelection.class);
                     if (var10 != null && var10 != SunPageSelection.RANGE) {
                        var28 = false;
                     }
                  }

                  this.debug_println("after startDoc rangeSelected? " + var28 + " numNonCollatedCopies " + var5);
                  int var29 = 0;

                  while(true) {
                     if (var29 >= var27) {
                        if (this.isCancelled()) {
                           this.cancelDoc();
                           var22 = false;
                        } else {
                           var22 = false;
                        }
                        break;
                     }

                     int var11 = var7;

                     for(int var12 = 0; (var11 <= var8 || var8 == -1) && var12 == 0; ++var11) {
                        int var13;
                        if (this.pageRangesAttr != null && var28) {
                           var13 = this.pageRangesAttr.next(var11);
                           if (var13 == -1) {
                              break;
                           }

                           if (var13 != var11 + 1) {
                              continue;
                           }
                        }

                        for(var13 = 0; var13 < var5 && var12 == 0; ++var13) {
                           if (this.isCancelled()) {
                              this.cancelDoc();
                           }

                           this.debug_println("printPage " + var11);
                           var12 = this.printPage(this.mDocument, var11);
                        }
                     }

                     ++var29;
                  }
               } finally {
                  if (var22) {
                     this.previousPaper = null;
                     synchronized(this) {
                        if (this.performingPrinting) {
                           this.endDoc();
                        }

                        this.performingPrinting = false;
                        this.notify();
                     }
                  }
               }

               this.previousPaper = null;
               synchronized(this) {
                  if (this.performingPrinting) {
                     this.endDoc();
                  }

                  this.performingPrinting = false;
                  this.notify();
               }
            }
         } else {
            this.spoolToService(var2, var1);
         }
      }
   }

   protected void validateDestination(String var1) throws PrinterException {
      if (var1 != null) {
         File var2 = new File(var1);

         try {
            if (var2.createNewFile()) {
               var2.delete();
            }
         } catch (IOException var4) {
            throw new PrinterException("Cannot write to file:" + var1);
         } catch (SecurityException var5) {
         }

         File var3 = var2.getParentFile();
         if (var2.exists() && (!var2.isFile() || !var2.canWrite()) || var3 != null && (!var3.exists() || var3.exists() && !var3.canWrite())) {
            throw new PrinterException("Cannot write to file:" + var1);
         }
      }
   }

   protected void validatePaper(Paper var1, Paper var2) {
      if (var1 != null && var2 != null) {
         double var3 = var1.getWidth();
         double var5 = var1.getHeight();
         double var7 = var1.getImageableX();
         double var9 = var1.getImageableY();
         double var11 = var1.getImageableWidth();
         double var13 = var1.getImageableHeight();
         Paper var15 = new Paper();
         var3 = var3 > 0.0D ? var3 : var15.getWidth();
         var5 = var5 > 0.0D ? var5 : var15.getHeight();
         var7 = var7 > 0.0D ? var7 : var15.getImageableX();
         var9 = var9 > 0.0D ? var9 : var15.getImageableY();
         var11 = var11 > 0.0D ? var11 : var15.getImageableWidth();
         var13 = var13 > 0.0D ? var13 : var15.getImageableHeight();
         if (var11 > var3) {
            var11 = var3;
         }

         if (var13 > var5) {
            var13 = var5;
         }

         if (var7 + var11 > var3) {
            var7 = var3 - var11;
         }

         if (var9 + var13 > var5) {
            var9 = var5 - var13;
         }

         var2.setSize(var3, var5);
         var2.setImageableArea(var7, var9, var11, var13);
      }
   }

   public PageFormat defaultPage(PageFormat var1) {
      PageFormat var2 = (PageFormat)var1.clone();
      var2.setOrientation(1);
      Paper var3 = new Paper();
      double var4 = 72.0D;
      Media var10 = null;
      PrintService var11 = this.getPrintService();
      double var6;
      double var8;
      if (var11 != null) {
         var10 = (Media)var11.getDefaultAttributeValue(Media.class);
         MediaSize var12;
         if (var10 instanceof MediaSizeName && (var12 = MediaSize.getMediaSizeForName((MediaSizeName)var10)) != null) {
            var6 = (double)var12.getX(25400) * var4;
            var8 = (double)var12.getY(25400) * var4;
            var3.setSize(var6, var8);
            var3.setImageableArea(var4, var4, var6 - 2.0D * var4, var8 - 2.0D * var4);
            var2.setPaper(var3);
            return var2;
         }
      }

      String var15 = Locale.getDefault().getCountry();
      if (!Locale.getDefault().equals(Locale.ENGLISH) && var15 != null && !var15.equals(Locale.US.getCountry()) && !var15.equals(Locale.CANADA.getCountry())) {
         double var13 = 25.4D;
         var6 = Math.rint(210.0D * var4 / var13);
         var8 = Math.rint(297.0D * var4 / var13);
         var3.setSize(var6, var8);
         var3.setImageableArea(var4, var4, var6 - 2.0D * var4, var8 - 2.0D * var4);
      }

      var2.setPaper(var3);
      return var2;
   }

   public PageFormat validatePage(PageFormat var1) {
      PageFormat var2 = (PageFormat)var1.clone();
      Paper var3 = new Paper();
      this.validatePaper(var2.getPaper(), var3);
      var2.setPaper(var3);
      return var2;
   }

   public void setCopies(int var1) {
      this.mNumCopies = var1;
   }

   public int getCopies() {
      return this.mNumCopies;
   }

   protected int getCopiesInt() {
      return this.copiesAttr > 0 ? this.copiesAttr : this.getCopies();
   }

   public String getUserName() {
      return System.getProperty("user.name");
   }

   protected String getUserNameInt() {
      if (this.userNameAttr != null) {
         return this.userNameAttr;
      } else {
         try {
            return this.getUserName();
         } catch (SecurityException var2) {
            return "";
         }
      }
   }

   public void setJobName(String var1) {
      if (var1 != null) {
         this.mDocName = var1;
      } else {
         throw new NullPointerException();
      }
   }

   public String getJobName() {
      return this.mDocName;
   }

   protected String getJobNameInt() {
      return this.jobNameAttr != null ? this.jobNameAttr : this.getJobName();
   }

   protected void setPageRange(int var1, int var2) {
      if (var1 >= 0 && var2 >= 0) {
         this.mFirstPage = var1;
         this.mLastPage = var2;
         if (this.mLastPage < this.mFirstPage) {
            this.mLastPage = this.mFirstPage;
         }
      } else {
         this.mFirstPage = -1;
         this.mLastPage = -1;
      }

   }

   protected int getFirstPage() {
      return this.mFirstPage == -1 ? 0 : this.mFirstPage;
   }

   protected int getLastPage() {
      return this.mLastPage;
   }

   protected void setCollated(boolean var1) {
      this.mCollate = var1;
      this.collateAttReq = true;
   }

   protected boolean isCollated() {
      return this.mCollate;
   }

   protected final int getSelectAttrib() {
      if (this.attributes != null) {
         SunPageSelection var1 = (SunPageSelection)this.attributes.get(SunPageSelection.class);
         if (var1 == SunPageSelection.RANGE) {
            return 2;
         }

         if (var1 == SunPageSelection.SELECTION) {
            return 1;
         }

         if (var1 == SunPageSelection.ALL) {
            return 0;
         }
      }

      return 4;
   }

   protected final int getFromPageAttrib() {
      if (this.attributes != null) {
         PageRanges var1 = (PageRanges)this.attributes.get(PageRanges.class);
         if (var1 != null) {
            int[][] var2 = var1.getMembers();
            return var2[0][0];
         }
      }

      return this.getMinPageAttrib();
   }

   protected final int getToPageAttrib() {
      if (this.attributes != null) {
         PageRanges var1 = (PageRanges)this.attributes.get(PageRanges.class);
         if (var1 != null) {
            int[][] var2 = var1.getMembers();
            return var2[var2.length - 1][1];
         }
      }

      return this.getMaxPageAttrib();
   }

   protected final int getMinPageAttrib() {
      if (this.attributes != null) {
         SunMinMaxPage var1 = (SunMinMaxPage)this.attributes.get(SunMinMaxPage.class);
         if (var1 != null) {
            return var1.getMin();
         }
      }

      return 1;
   }

   protected final int getMaxPageAttrib() {
      if (this.attributes != null) {
         SunMinMaxPage var1 = (SunMinMaxPage)this.attributes.get(SunMinMaxPage.class);
         if (var1 != null) {
            return var1.getMax();
         }
      }

      Pageable var3 = this.getPageable();
      if (var3 != null) {
         int var2 = var3.getNumberOfPages();
         if (var2 <= -1) {
            var2 = 9999;
         }

         return var2 == 0 ? 1 : var2;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   protected abstract void startDoc() throws PrinterException;

   protected abstract void endDoc() throws PrinterException;

   protected abstract void abortDoc();

   protected void cancelDoc() throws PrinterAbortException {
      this.abortDoc();
      synchronized(this) {
         this.userCancelled = false;
         this.performingPrinting = false;
         this.notify();
      }

      throw new PrinterAbortException();
   }

   protected int getCollatedCopies() {
      return this.isCollated() ? this.getCopiesInt() : 1;
   }

   protected int getNoncollatedCopies() {
      return this.isCollated() ? 1 : this.getCopiesInt();
   }

   synchronized void setGraphicsConfigInfo(AffineTransform var1, double var2, double var4) {
      Point2D.Double var6 = new Point2D.Double(var2, var4);
      var1.transform(var6, var6);
      if (this.pgConfig == null || this.defaultDeviceTransform == null || !var1.equals(this.defaultDeviceTransform) || this.deviceWidth != (int)var6.getX() || this.deviceHeight != (int)var6.getY()) {
         this.deviceWidth = (int)var6.getX();
         this.deviceHeight = (int)var6.getY();
         this.defaultDeviceTransform = var1;
         this.pgConfig = null;
      }

   }

   synchronized PrinterGraphicsConfig getPrinterGraphicsConfig() {
      if (this.pgConfig != null) {
         return this.pgConfig;
      } else {
         String var1 = "Printer Device";
         PrintService var2 = this.getPrintService();
         if (var2 != null) {
            var1 = var2.toString();
         }

         this.pgConfig = new PrinterGraphicsConfig(var1, this.defaultDeviceTransform, this.deviceWidth, this.deviceHeight);
         return this.pgConfig;
      }
   }

   protected int printPage(Pageable var1, int var2) throws PrinterException {
      PageFormat var3;
      PageFormat var4;
      Printable var5;
      try {
         var4 = var1.getPageFormat(var2);
         var3 = (PageFormat)var4.clone();
         var5 = var1.getPrintable(var2);
      } catch (Exception var42) {
         PrinterException var7 = new PrinterException("Error getting page or printable.[ " + var42 + " ]");
         var7.initCause(var42);
         throw var7;
      }

      Paper var6 = var3.getPaper();
      double var9;
      double var43;
      if (var3.getOrientation() != 1 && this.landscapeRotates270) {
         var43 = var6.getImageableX();
         var9 = var6.getImageableY();
         double var11 = var6.getImageableWidth();
         double var13 = var6.getImageableHeight();
         var6.setImageableArea(var6.getWidth() - var43 - var11, var6.getHeight() - var9 - var13, var11, var13);
         var3.setPaper(var6);
         if (var3.getOrientation() == 0) {
            var3.setOrientation(2);
         } else {
            var3.setOrientation(0);
         }
      }

      var43 = this.getXRes() / 72.0D;
      var9 = this.getYRes() / 72.0D;
      Rectangle2D.Double var44 = new Rectangle2D.Double(var6.getImageableX() * var43, var6.getImageableY() * var9, var6.getImageableWidth() * var43, var6.getImageableHeight() * var9);
      AffineTransform var12 = new AffineTransform();
      AffineTransform var45 = new AffineTransform();
      var45.scale(var43, var9);
      int var14 = (int)var44.getWidth();
      if (var14 % 4 != 0) {
         var14 += 4 - var14 % 4;
      }

      if (var14 <= 0) {
         throw new PrinterException("Paper's imageable width is too small.");
      } else {
         int var15 = (int)var44.getHeight();
         if (var15 <= 0) {
            throw new PrinterException("Paper's imageable height is too small.");
         } else {
            int var16 = 4194304 / var14 / 3;
            int var17 = (int)Math.rint(var6.getImageableX() * var43);
            int var18 = (int)Math.rint(var6.getImageableY() * var9);
            AffineTransform var19 = new AffineTransform();
            var19.translate((double)(-var17), (double)var18);
            var19.translate(0.0D, (double)var16);
            var19.scale(1.0D, -1.0D);
            BufferedImage var20 = new BufferedImage(1, 1, 5);
            PeekGraphics var21 = this.createPeekGraphics(var20.createGraphics(), this);
            Rectangle2D.Double var22 = new Rectangle2D.Double(var3.getImageableX(), var3.getImageableY(), var3.getImageableWidth(), var3.getImageableHeight());
            var21.transform(var45);
            var21.translate(-this.getPhysicalPrintableX(var6) / var43, -this.getPhysicalPrintableY(var6) / var9);
            var21.transform(new AffineTransform(var3.getMatrix()));
            this.initPrinterGraphics(var21, var22);
            AffineTransform var23 = var21.getTransform();
            this.setGraphicsConfigInfo(var45, var6.getWidth(), var6.getHeight());
            int var24 = var5.print(var21, var4, var2);
            this.debug_println("pageResult " + var24);
            if (var24 == 0) {
               this.debug_println("startPage " + var2);
               Paper var25 = var3.getPaper();
               boolean var26 = this.previousPaper == null || var25.getWidth() != this.previousPaper.getWidth() || var25.getHeight() != this.previousPaper.getHeight();
               this.previousPaper = var25;
               this.startPage(var3, var5, var2, var26);
               Graphics2D var27 = this.createPathGraphics(var21, this, var5, var3, var2);
               if (var27 != null) {
                  var27.transform(var45);
                  var27.translate(-this.getPhysicalPrintableX(var6) / var43, -this.getPhysicalPrintableY(var6) / var9);
                  var27.transform(new AffineTransform(var3.getMatrix()));
                  this.initPrinterGraphics(var27, var22);
                  this.redrawList.clear();
                  AffineTransform var46 = var27.getTransform();
                  var5.print(var27, var4, var2);

                  for(int var47 = 0; var47 < this.redrawList.size(); ++var47) {
                     RasterPrinterJob.GraphicsState var48 = (RasterPrinterJob.GraphicsState)this.redrawList.get(var47);
                     var27.setTransform(var46);
                     ((PathGraphics)var27).redrawRegion(var48.region, var48.sx, var48.sy, var48.theClip, var48.theTransform);
                  }
               } else {
                  BufferedImage var28 = this.cachedBand;
                  if (this.cachedBand == null || var14 != this.cachedBandWidth || var16 != this.cachedBandHeight) {
                     var28 = new BufferedImage(var14, var16, 5);
                     this.cachedBand = var28;
                     this.cachedBandWidth = var14;
                     this.cachedBandHeight = var16;
                  }

                  Graphics2D var29 = var28.createGraphics();
                  Rectangle2D.Double var30 = new Rectangle2D.Double(0.0D, 0.0D, (double)var14, (double)var16);
                  this.initPrinterGraphics(var29, var30);
                  ProxyGraphics2D var31 = new ProxyGraphics2D(var29, this);
                  Graphics2D var32 = var28.createGraphics();
                  var32.setColor(Color.white);
                  ByteInterleavedRaster var33 = (ByteInterleavedRaster)var28.getRaster();
                  byte[] var34 = var33.getDataStorage();
                  int var10000 = var18 + var15;
                  int var36 = (int)this.getPhysicalPrintableX(var6);
                  int var37 = (int)this.getPhysicalPrintableY(var6);
                  int var38 = 0;

                  while(true) {
                     if (var38 > var15) {
                        var32.dispose();
                        var29.dispose();
                        break;
                     }

                     var32.fillRect(0, 0, var14, var16);
                     var29.setTransform(var12);
                     var29.transform(var19);
                     var19.translate(0.0D, (double)(-var16));
                     var29.transform(var45);
                     var29.transform(new AffineTransform(var3.getMatrix()));
                     Rectangle var39 = var29.getClipBounds();
                     var39 = var23.createTransformedShape(var39).getBounds();
                     if (var39 == null || var21.hitsDrawingArea(var39) && var14 > 0 && var16 > 0) {
                        int var40 = var17 - var36;
                        if (var40 < 0) {
                           var29.translate((double)var40 / var43, 0.0D);
                           var40 = 0;
                        }

                        int var41 = var18 + var38 - var37;
                        if (var41 < 0) {
                           var29.translate(0.0D, (double)var41 / var9);
                           var41 = 0;
                        }

                        var31.setDelegate((Graphics2D)var29.create());
                        var5.print(var31, var4, var2);
                        var31.dispose();
                        this.printBand(var34, var40, var41, var14, var16);
                     }

                     var38 += var16;
                  }
               }

               this.debug_println("calling endPage " + var2);
               this.endPage(var3, var5, var2);
            }

            return var24;
         }
      }
   }

   public void cancel() {
      synchronized(this) {
         if (this.performingPrinting) {
            this.userCancelled = true;
         }

         this.notify();
      }
   }

   public boolean isCancelled() {
      boolean var1 = false;
      synchronized(this) {
         var1 = this.performingPrinting && this.userCancelled;
         this.notify();
         return var1;
      }
   }

   protected Pageable getPageable() {
      return this.mDocument;
   }

   protected Graphics2D createPathGraphics(PeekGraphics var1, PrinterJob var2, Printable var3, PageFormat var4, int var5) {
      return null;
   }

   protected PeekGraphics createPeekGraphics(Graphics2D var1, PrinterJob var2) {
      return new PeekGraphics(var1, var2);
   }

   protected void initPrinterGraphics(Graphics2D var1, Rectangle2D var2) {
      var1.setClip(var2);
      var1.setPaint(Color.black);
   }

   public boolean checkAllowedToPrintToFile() {
      try {
         this.throwPrintToFile();
         return true;
      } catch (SecurityException var2) {
         return false;
      }
   }

   private void throwPrintToFile() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         if (this.printToFilePermission == null) {
            this.printToFilePermission = new FilePermission("<<ALL FILES>>", "read,write");
         }

         var1.checkPermission(this.printToFilePermission);
      }

   }

   protected String removeControlChars(String var1) {
      char[] var2 = var1.toCharArray();
      int var3 = var2.length;
      char[] var4 = new char[var3];
      int var5 = 0;

      for(int var6 = 0; var6 < var3; ++var6) {
         char var7 = var2[var6];
         if (var7 > '\r' || var7 < '\t' || var7 == 11 || var7 == '\f') {
            var4[var5++] = var7;
         }
      }

      if (var5 == var3) {
         return var1;
      } else {
         return new String(var4, 0, var5);
      }
   }

   private long getParentWindowID() {
      return this.parentWindowID;
   }

   private void clearParentWindowID() {
      this.parentWindowID = 0L;
      this.onTop = null;
   }

   private void setParentWindowID(PrintRequestAttributeSet var1) {
      this.parentWindowID = 0L;
      this.onTop = (DialogOnTop)var1.get(DialogOnTop.class);
      if (this.onTop != null) {
         this.parentWindowID = this.onTop.getID();
      }

   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.print.pipeline")));
      if (var0 != null) {
         if (var0.equalsIgnoreCase("pdl")) {
            forcePDL = true;
         } else if (var0.equalsIgnoreCase("raster")) {
            forceRaster = true;
         }
      }

      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.print.shapetext")));
      if (var1 != null) {
         shapeTextProp = true;
      }

      debugPrint = false;
   }

   private class GraphicsState {
      Rectangle2D region;
      Shape theClip;
      AffineTransform theTransform;
      double sx;
      double sy;

      private GraphicsState() {
      }

      // $FF: synthetic method
      GraphicsState(Object var2) {
         this();
      }
   }
}
