package sun.print;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Locale;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Destination;
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
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.QueuedJobCount;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.Severity;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintServiceAttributeListener;
import sun.security.action.GetPropertyAction;

public class UnixPrintService implements PrintService, AttributeUpdater, SunPrinterJobService {
   private static String encoding = "ISO8859_1";
   private static DocFlavor textByteFlavor;
   private static DocFlavor[] supportedDocFlavors = null;
   private static final DocFlavor[] supportedDocFlavorsInit;
   private static final DocFlavor[] supportedHostDocFlavors;
   String[] lpcStatusCom = new String[]{"", "| grep -E '^[ 0-9a-zA-Z_-]*@' | awk '{print $2, $3}'"};
   String[] lpcQueueCom = new String[]{"", "| grep -E '^[ 0-9a-zA-Z_-]*@' | awk '{print $4}'"};
   private static final Class[] serviceAttrCats;
   private static final Class[] otherAttrCats;
   private static int MAXCOPIES;
   private static final MediaSizeName[] mediaSizes;
   private String printer;
   private PrinterName name;
   private boolean isInvalid;
   private transient PrintServiceAttributeSet lastSet;
   private transient ServiceNotifier notifier = null;
   private static MediaPrintableArea[] mpas;

   UnixPrintService(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null printer name");
      } else {
         this.printer = var1;
         this.isInvalid = false;
      }
   }

   public void invalidateService() {
      this.isInvalid = true;
   }

   public String getName() {
      return this.printer;
   }

   private PrinterName getPrinterName() {
      if (this.name == null) {
         this.name = new PrinterName(this.printer, (Locale)null);
      }

      return this.name;
   }

   private PrinterIsAcceptingJobs getPrinterIsAcceptingJobsSysV() {
      String var1 = "/usr/bin/lpstat -a " + this.printer;
      String[] var2 = UnixPrintServiceLookup.execCmd(var1);
      if (var2 != null && var2.length > 0) {
         if (var2[0].startsWith(this.printer + " accepting requests")) {
            return PrinterIsAcceptingJobs.ACCEPTING_JOBS;
         }

         if (var2[0].startsWith(this.printer)) {
            int var3 = this.printer.length();
            String var4 = var2[0];
            if (var4.length() > var3 && var4.charAt(var3) == '@' && var4.indexOf(" accepting requests", var3) > 0 && var4.indexOf(" not accepting requests", var3) == -1) {
               return PrinterIsAcceptingJobs.ACCEPTING_JOBS;
            }
         }
      }

      return PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS;
   }

   private PrinterIsAcceptingJobs getPrinterIsAcceptingJobsBSD() {
      if (UnixPrintServiceLookup.cmdIndex == -1) {
         UnixPrintServiceLookup.cmdIndex = UnixPrintServiceLookup.getBSDCommandIndex();
      }

      String var1 = "/usr/sbin/lpc status " + this.printer + this.lpcStatusCom[UnixPrintServiceLookup.cmdIndex];
      String[] var2 = UnixPrintServiceLookup.execCmd(var1);
      if (var2 != null && var2.length > 0) {
         if (UnixPrintServiceLookup.cmdIndex == 1) {
            if (var2[0].startsWith("enabled enabled")) {
               return PrinterIsAcceptingJobs.ACCEPTING_JOBS;
            }
         } else if (var2[1].trim().startsWith("queuing is enabled") && var2[2].trim().startsWith("printing is enabled") || var2.length >= 4 && var2[2].trim().startsWith("queuing is enabled") && var2[3].trim().startsWith("printing is enabled")) {
            return PrinterIsAcceptingJobs.ACCEPTING_JOBS;
         }
      }

      return PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS;
   }

   protected static String[] filterPrinterNamesAIX(String[] var0) {
      ArrayList var1 = new ArrayList();

      for(int var3 = 0; var3 < var0.length; ++var3) {
         if (!var0[var3].startsWith("---") && !var0[var3].startsWith("Queue") && !var0[var3].equals("")) {
            String[] var2 = var0[var3].split(" ");
            if (var2.length >= 1 && !var2[0].trim().endsWith(":")) {
               var1.add(var0[var3]);
            }
         }
      }

      return (String[])((String[])var1.toArray(new String[var1.size()]));
   }

   private PrinterIsAcceptingJobs getPrinterIsAcceptingJobsAIX() {
      String var1 = "/usr/bin/lpstat -a" + this.printer;
      String[] var2 = UnixPrintServiceLookup.execCmd(var1);
      var2 = filterPrinterNamesAIX(var2);
      if (var2 != null && var2.length > 0) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3].contains("READY") || var2[var3].contains("RUNNING")) {
               return PrinterIsAcceptingJobs.ACCEPTING_JOBS;
            }
         }
      }

      return PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS;
   }

   private PrinterIsAcceptingJobs getPrinterIsAcceptingJobs() {
      if (UnixPrintServiceLookup.isSysV()) {
         return this.getPrinterIsAcceptingJobsSysV();
      } else if (UnixPrintServiceLookup.isBSD()) {
         return this.getPrinterIsAcceptingJobsBSD();
      } else {
         return UnixPrintServiceLookup.isAIX() ? this.getPrinterIsAcceptingJobsAIX() : PrinterIsAcceptingJobs.ACCEPTING_JOBS;
      }
   }

   private PrinterState getPrinterState() {
      return this.isInvalid ? PrinterState.STOPPED : null;
   }

   private PrinterStateReasons getPrinterStateReasons() {
      if (this.isInvalid) {
         PrinterStateReasons var1 = new PrinterStateReasons();
         var1.put(PrinterStateReason.SHUTDOWN, Severity.ERROR);
         return var1;
      } else {
         return null;
      }
   }

   private QueuedJobCount getQueuedJobCountSysV() {
      String var1 = "/usr/bin/lpstat -R " + this.printer;
      String[] var2 = UnixPrintServiceLookup.execCmd(var1);
      int var3 = var2 == null ? 0 : var2.length;
      return new QueuedJobCount(var3);
   }

   private QueuedJobCount getQueuedJobCountBSD() {
      if (UnixPrintServiceLookup.cmdIndex == -1) {
         UnixPrintServiceLookup.cmdIndex = UnixPrintServiceLookup.getBSDCommandIndex();
      }

      int var1 = 0;
      String var2 = "/usr/sbin/lpc status " + this.printer + this.lpcQueueCom[UnixPrintServiceLookup.cmdIndex];
      String[] var3 = UnixPrintServiceLookup.execCmd(var2);
      if (var3 != null && var3.length > 0) {
         String var4;
         if (UnixPrintServiceLookup.cmdIndex == 1) {
            var4 = var3[0];
         } else {
            var4 = var3[3].trim();
            if (var4.startsWith("no")) {
               return new QueuedJobCount(0);
            }

            var4 = var4.substring(0, var4.indexOf(32));
         }

         try {
            var1 = Integer.parseInt(var4);
         } catch (NumberFormatException var6) {
         }
      }

      return new QueuedJobCount(var1);
   }

   private QueuedJobCount getQueuedJobCountAIX() {
      String var1 = "/usr/bin/lpstat -a" + this.printer;
      String[] var2 = UnixPrintServiceLookup.execCmd(var1);
      var2 = filterPrinterNamesAIX(var2);
      int var3 = 0;
      if (var2 != null && var2.length > 0) {
         for(int var4 = 0; var4 < var2.length; ++var4) {
            if (var2[var4].contains("QUEUED")) {
               ++var3;
            }
         }
      }

      return new QueuedJobCount(var3);
   }

   private QueuedJobCount getQueuedJobCount() {
      if (UnixPrintServiceLookup.isSysV()) {
         return this.getQueuedJobCountSysV();
      } else if (UnixPrintServiceLookup.isBSD()) {
         return this.getQueuedJobCountBSD();
      } else {
         return UnixPrintServiceLookup.isAIX() ? this.getQueuedJobCountAIX() : new QueuedJobCount(0);
      }
   }

   private PrintServiceAttributeSet getSysVServiceAttributes() {
      HashPrintServiceAttributeSet var1 = new HashPrintServiceAttributeSet();
      var1.add(this.getQueuedJobCountSysV());
      var1.add(this.getPrinterIsAcceptingJobsSysV());
      return var1;
   }

   private PrintServiceAttributeSet getBSDServiceAttributes() {
      HashPrintServiceAttributeSet var1 = new HashPrintServiceAttributeSet();
      var1.add(this.getQueuedJobCountBSD());
      var1.add(this.getPrinterIsAcceptingJobsBSD());
      return var1;
   }

   private PrintServiceAttributeSet getAIXServiceAttributes() {
      HashPrintServiceAttributeSet var1 = new HashPrintServiceAttributeSet();
      var1.add(this.getQueuedJobCountAIX());
      var1.add(this.getPrinterIsAcceptingJobsAIX());
      return var1;
   }

   private boolean isSupportedCopies(Copies var1) {
      int var2 = var1.getValue();
      return var2 > 0 && var2 < MAXCOPIES;
   }

   private boolean isSupportedMedia(MediaSizeName var1) {
      for(int var2 = 0; var2 < mediaSizes.length; ++var2) {
         if (var1.equals(mediaSizes[var2])) {
            return true;
         }
      }

      return false;
   }

   public DocPrintJob createPrintJob() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPrintJobAccess();
      }

      return new UnixPrintJob(this);
   }

   private PrintServiceAttributeSet getDynamicAttributes() {
      if (UnixPrintServiceLookup.isSysV()) {
         return this.getSysVServiceAttributes();
      } else {
         return UnixPrintServiceLookup.isAIX() ? this.getAIXServiceAttributes() : this.getBSDServiceAttributes();
      }
   }

   public PrintServiceAttributeSet getUpdatedAttributes() {
      PrintServiceAttributeSet var1 = this.getDynamicAttributes();
      if (this.lastSet == null) {
         this.lastSet = var1;
         return AttributeSetUtilities.unmodifiableView(var1);
      } else {
         HashPrintServiceAttributeSet var2 = new HashPrintServiceAttributeSet();
         Attribute[] var3 = var1.toArray();

         for(int var5 = 0; var5 < var3.length; ++var5) {
            Attribute var4 = var3[var5];
            if (!this.lastSet.containsValue(var4)) {
               var2.add(var4);
            }
         }

         this.lastSet = var1;
         return AttributeSetUtilities.unmodifiableView((PrintServiceAttributeSet)var2);
      }
   }

   public void wakeNotifier() {
      synchronized(this) {
         if (this.notifier != null) {
            this.notifier.wake();
         }

      }
   }

   public void addPrintServiceAttributeListener(PrintServiceAttributeListener var1) {
      synchronized(this) {
         if (var1 != null) {
            if (this.notifier == null) {
               this.notifier = new ServiceNotifier(this);
            }

            this.notifier.addListener(var1);
         }
      }
   }

   public void removePrintServiceAttributeListener(PrintServiceAttributeListener var1) {
      synchronized(this) {
         if (var1 != null && this.notifier != null) {
            this.notifier.removeListener(var1);
            if (this.notifier.isEmpty()) {
               this.notifier.stopNotifier();
               this.notifier = null;
            }

         }
      }
   }

   public <T extends PrintServiceAttribute> T getAttribute(Class<T> var1) {
      if (var1 == null) {
         throw new NullPointerException("category");
      } else if (!PrintServiceAttribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException("Not a PrintServiceAttribute");
      } else if (var1 == PrinterName.class) {
         return this.getPrinterName();
      } else if (var1 == PrinterState.class) {
         return this.getPrinterState();
      } else if (var1 == PrinterStateReasons.class) {
         return this.getPrinterStateReasons();
      } else if (var1 == QueuedJobCount.class) {
         return this.getQueuedJobCount();
      } else {
         return var1 == PrinterIsAcceptingJobs.class ? this.getPrinterIsAcceptingJobs() : null;
      }
   }

   public PrintServiceAttributeSet getAttributes() {
      HashPrintServiceAttributeSet var1 = new HashPrintServiceAttributeSet();
      var1.add(this.getPrinterName());
      var1.add(this.getPrinterIsAcceptingJobs());
      PrinterState var2 = this.getPrinterState();
      if (var2 != null) {
         var1.add(var2);
      }

      PrinterStateReasons var3 = this.getPrinterStateReasons();
      if (var3 != null) {
         var1.add(var3);
      }

      var1.add(this.getQueuedJobCount());
      return AttributeSetUtilities.unmodifiableView((PrintServiceAttributeSet)var1);
   }

   private void initSupportedDocFlavors() {
      String var1 = DocFlavor.hostEncoding.toLowerCase(Locale.ENGLISH);
      if (!var1.equals("utf-8") && !var1.equals("utf-16") && !var1.equals("utf-16be") && !var1.equals("utf-16le") && !var1.equals("us-ascii")) {
         int var2 = supportedDocFlavorsInit.length;
         DocFlavor[] var3 = new DocFlavor[var2 + supportedHostDocFlavors.length];
         System.arraycopy(supportedHostDocFlavors, 0, var3, var2, supportedHostDocFlavors.length);
         System.arraycopy(supportedDocFlavorsInit, 0, var3, 0, var2);
         supportedDocFlavors = var3;
      } else {
         supportedDocFlavors = supportedDocFlavorsInit;
      }

   }

   public DocFlavor[] getSupportedDocFlavors() {
      if (supportedDocFlavors == null) {
         this.initSupportedDocFlavors();
      }

      int var1 = supportedDocFlavors.length;
      DocFlavor[] var2 = new DocFlavor[var1];
      System.arraycopy(supportedDocFlavors, 0, var2, 0, var1);
      return var2;
   }

   public boolean isDocFlavorSupported(DocFlavor var1) {
      if (supportedDocFlavors == null) {
         this.initSupportedDocFlavors();
      }

      for(int var2 = 0; var2 < supportedDocFlavors.length; ++var2) {
         if (var1.equals(supportedDocFlavors[var2])) {
            return true;
         }
      }

      return false;
   }

   public Class[] getSupportedAttributeCategories() {
      int var1 = otherAttrCats.length;
      Class[] var2 = new Class[var1];
      System.arraycopy(otherAttrCats, 0, var2, 0, otherAttrCats.length);
      return var2;
   }

   public boolean isAttributeCategorySupported(Class<? extends Attribute> var1) {
      if (var1 == null) {
         throw new NullPointerException("null category");
      } else if (!Attribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException(var1 + " is not an Attribute");
      } else {
         for(int var2 = 0; var2 < otherAttrCats.length; ++var2) {
            if (var1 == otherAttrCats[var2]) {
               return true;
            }
         }

         return false;
      }
   }

   public Object getDefaultAttributeValue(Class<? extends Attribute> var1) {
      if (var1 == null) {
         throw new NullPointerException("null category");
      } else if (!Attribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException(var1 + " is not an Attribute");
      } else if (!this.isAttributeCategorySupported(var1)) {
         return null;
      } else if (var1 == Copies.class) {
         return new Copies(1);
      } else if (var1 == Chromaticity.class) {
         return Chromaticity.COLOR;
      } else if (var1 == Destination.class) {
         try {
            return new Destination((new File("out.ps")).toURI());
         } catch (SecurityException var6) {
            try {
               return new Destination(new URI("file:out.ps"));
            } catch (URISyntaxException var5) {
               return null;
            }
         }
      } else if (var1 == Fidelity.class) {
         return Fidelity.FIDELITY_FALSE;
      } else if (var1 == JobName.class) {
         return new JobName("Java Printing", (Locale)null);
      } else if (var1 == JobSheets.class) {
         return JobSheets.STANDARD;
      } else {
         String var2;
         if (var1 == Media.class) {
            var2 = Locale.getDefault().getCountry();
            return var2 == null || !var2.equals("") && !var2.equals(Locale.US.getCountry()) && !var2.equals(Locale.CANADA.getCountry()) ? MediaSizeName.ISO_A4 : MediaSizeName.NA_LETTER;
         } else if (var1 != MediaPrintableArea.class) {
            if (var1 == OrientationRequested.class) {
               return OrientationRequested.PORTRAIT;
            } else if (var1 == PageRanges.class) {
               return new PageRanges(1, Integer.MAX_VALUE);
            } else if (var1 == RequestingUserName.class) {
               var2 = "";

               try {
                  var2 = System.getProperty("user.name", "");
               } catch (SecurityException var7) {
               }

               return new RequestingUserName(var2, (Locale)null);
            } else if (var1 == SheetCollate.class) {
               return SheetCollate.UNCOLLATED;
            } else {
               return var1 == Sides.class ? Sides.ONE_SIDED : null;
            }
         } else {
            var2 = Locale.getDefault().getCountry();
            float var3;
            float var4;
            if (var2 == null || !var2.equals("") && !var2.equals(Locale.US.getCountry()) && !var2.equals(Locale.CANADA.getCountry())) {
               var3 = MediaSize.ISO.A4.getX(25400) - 0.5F;
               var4 = MediaSize.ISO.A4.getY(25400) - 0.5F;
            } else {
               var3 = MediaSize.NA.LETTER.getX(25400) - 0.5F;
               var4 = MediaSize.NA.LETTER.getY(25400) - 0.5F;
            }

            return new MediaPrintableArea(0.25F, 0.25F, var3, var4, 25400);
         }
      }
   }

   private boolean isAutoSense(DocFlavor var1) {
      return var1.equals(DocFlavor.BYTE_ARRAY.AUTOSENSE) || var1.equals(DocFlavor.INPUT_STREAM.AUTOSENSE) || var1.equals(DocFlavor.URL.AUTOSENSE);
   }

   public Object getSupportedAttributeValues(Class<? extends Attribute> var1, DocFlavor var2, AttributeSet var3) {
      if (var1 == null) {
         throw new NullPointerException("null category");
      } else if (!Attribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException(var1 + " does not implement Attribute");
      } else {
         if (var2 != null) {
            if (!this.isDocFlavorSupported(var2)) {
               throw new IllegalArgumentException(var2 + " is an unsupported flavor");
            }

            if (this.isAutoSense(var2)) {
               return null;
            }
         }

         if (!this.isAttributeCategorySupported(var1)) {
            return null;
         } else if (var1 == Chromaticity.class) {
            if (var2 != null && !this.isServiceFormattedFlavor(var2)) {
               return null;
            } else {
               Chromaticity[] var19 = new Chromaticity[]{Chromaticity.COLOR};
               return var19;
            }
         } else if (var1 == Destination.class) {
            try {
               return new Destination((new File("out.ps")).toURI());
            } catch (SecurityException var9) {
               try {
                  return new Destination(new URI("file:out.ps"));
               } catch (URISyntaxException var8) {
                  return null;
               }
            }
         } else if (var1 == JobName.class) {
            return new JobName("Java Printing", (Locale)null);
         } else if (var1 == JobSheets.class) {
            JobSheets[] var18 = new JobSheets[]{JobSheets.NONE, JobSheets.STANDARD};
            return var18;
         } else if (var1 == RequestingUserName.class) {
            String var17 = "";

            try {
               var17 = System.getProperty("user.name", "");
            } catch (SecurityException var10) {
            }

            return new RequestingUserName(var17, (Locale)null);
         } else if (var1 == OrientationRequested.class) {
            if (var2 != null && !this.isServiceFormattedFlavor(var2)) {
               return null;
            } else {
               OrientationRequested[] var16 = new OrientationRequested[]{OrientationRequested.PORTRAIT, OrientationRequested.LANDSCAPE, OrientationRequested.REVERSE_LANDSCAPE};
               return var16;
            }
         } else if (var1 != Copies.class && var1 != CopiesSupported.class) {
            if (var1 == Media.class) {
               Media[] var15 = new Media[mediaSizes.length];
               System.arraycopy(mediaSizes, 0, var15, 0, mediaSizes.length);
               return var15;
            } else if (var1 == Fidelity.class) {
               Fidelity[] var14 = new Fidelity[]{Fidelity.FIDELITY_FALSE, Fidelity.FIDELITY_TRUE};
               return var14;
            } else if (var1 == MediaPrintableArea.class) {
               if (var3 == null) {
                  return this.getAllPrintableAreas();
               } else {
                  MediaSize var13 = (MediaSize)var3.get(MediaSize.class);
                  Media var5 = (Media)var3.get(Media.class);
                  MediaPrintableArea[] var6 = new MediaPrintableArea[1];
                  if (var13 == null) {
                     if (!(var5 instanceof MediaSizeName)) {
                        return this.getAllPrintableAreas();
                     }

                     MediaSizeName var7 = (MediaSizeName)var5;
                     var13 = MediaSize.getMediaSizeForName(var7);
                     if (var13 == null) {
                        var5 = (Media)this.getDefaultAttributeValue(Media.class);
                        if (var5 instanceof MediaSizeName) {
                           var7 = (MediaSizeName)var5;
                           var13 = MediaSize.getMediaSizeForName(var7);
                        }

                        if (var13 == null) {
                           var6[0] = new MediaPrintableArea(0.25F, 0.25F, 8.0F, 10.5F, 25400);
                           return var6;
                        }
                     }
                  }

                  assert var13 != null;

                  var6[0] = new MediaPrintableArea(0.25F, 0.25F, var13.getX(25400) - 0.5F, var13.getY(25400) - 0.5F, 25400);
                  return var6;
               }
            } else if (var1 == PageRanges.class) {
               if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                  return null;
               } else {
                  PageRanges[] var12 = new PageRanges[]{new PageRanges(1, Integer.MAX_VALUE)};
                  return var12;
               }
            } else if (var1 == SheetCollate.class) {
               if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                  return null;
               } else {
                  SheetCollate[] var11 = new SheetCollate[]{SheetCollate.UNCOLLATED, SheetCollate.COLLATED};
                  return var11;
               }
            } else if (var1 == Sides.class) {
               if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                  return null;
               } else {
                  Sides[] var4 = new Sides[]{Sides.ONE_SIDED, Sides.TWO_SIDED_LONG_EDGE, Sides.TWO_SIDED_SHORT_EDGE};
                  return var4;
               }
            } else {
               return null;
            }
         } else {
            return var2 != null && (var2.equals(DocFlavor.INPUT_STREAM.POSTSCRIPT) || var2.equals(DocFlavor.URL.POSTSCRIPT) || var2.equals(DocFlavor.BYTE_ARRAY.POSTSCRIPT)) ? null : new CopiesSupported(1, MAXCOPIES);
         }
      }
   }

   private MediaPrintableArea[] getAllPrintableAreas() {
      if (mpas == null) {
         Media[] var1 = (Media[])((Media[])this.getSupportedAttributeValues(Media.class, (DocFlavor)null, (AttributeSet)null));
         mpas = new MediaPrintableArea[var1.length];

         for(int var2 = 0; var2 < mpas.length; ++var2) {
            if (var1[var2] instanceof MediaSizeName) {
               MediaSizeName var3 = (MediaSizeName)var1[var2];
               MediaSize var4 = MediaSize.getMediaSizeForName(var3);
               if (var4 == null) {
                  mpas[var2] = (MediaPrintableArea)this.getDefaultAttributeValue(MediaPrintableArea.class);
               } else {
                  mpas[var2] = new MediaPrintableArea(0.25F, 0.25F, var4.getX(25400) - 0.5F, var4.getY(25400) - 0.5F, 25400);
               }
            }
         }
      }

      MediaPrintableArea[] var5 = new MediaPrintableArea[mpas.length];
      System.arraycopy(mpas, 0, var5, 0, mpas.length);
      return var5;
   }

   private boolean isServiceFormattedFlavor(DocFlavor var1) {
      return var1.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || var1.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE) || var1.equals(DocFlavor.BYTE_ARRAY.GIF) || var1.equals(DocFlavor.INPUT_STREAM.GIF) || var1.equals(DocFlavor.URL.GIF) || var1.equals(DocFlavor.BYTE_ARRAY.JPEG) || var1.equals(DocFlavor.INPUT_STREAM.JPEG) || var1.equals(DocFlavor.URL.JPEG) || var1.equals(DocFlavor.BYTE_ARRAY.PNG) || var1.equals(DocFlavor.INPUT_STREAM.PNG) || var1.equals(DocFlavor.URL.PNG);
   }

   public boolean isAttributeValueSupported(Attribute var1, DocFlavor var2, AttributeSet var3) {
      if (var1 == null) {
         throw new NullPointerException("null attribute");
      } else {
         if (var2 != null) {
            if (!this.isDocFlavorSupported(var2)) {
               throw new IllegalArgumentException(var2 + " is an unsupported flavor");
            }

            if (this.isAutoSense(var2)) {
               return false;
            }
         }

         Class var4 = var1.getCategory();
         if (!this.isAttributeCategorySupported(var4)) {
            return false;
         } else if (var1.getCategory() == Chromaticity.class) {
            if (var2 != null && !this.isServiceFormattedFlavor(var2)) {
               return false;
            } else {
               return var1 == Chromaticity.COLOR;
            }
         } else if (var1.getCategory() == Copies.class) {
            return (var2 == null || !var2.equals(DocFlavor.INPUT_STREAM.POSTSCRIPT) && !var2.equals(DocFlavor.URL.POSTSCRIPT) && !var2.equals(DocFlavor.BYTE_ARRAY.POSTSCRIPT)) && this.isSupportedCopies((Copies)var1);
         } else if (var1.getCategory() == Destination.class) {
            URI var5 = ((Destination)var1).getURI();
            return "file".equals(var5.getScheme()) && !var5.getSchemeSpecificPart().equals("");
         } else if (var1.getCategory() == Media.class) {
            return var1 instanceof MediaSizeName ? this.isSupportedMedia((MediaSizeName)var1) : false;
         } else {
            if (var1.getCategory() == OrientationRequested.class) {
               if (var1 == OrientationRequested.REVERSE_PORTRAIT || var2 != null && !this.isServiceFormattedFlavor(var2)) {
                  return false;
               }
            } else if (var1.getCategory() == PageRanges.class) {
               if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                  return false;
               }
            } else if (var1.getCategory() == SheetCollate.class) {
               if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                  return false;
               }
            } else if (var1.getCategory() == Sides.class && var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
               return false;
            }

            return true;
         }
      }
   }

   public AttributeSet getUnsupportedAttributes(DocFlavor var1, AttributeSet var2) {
      if (var1 != null && !this.isDocFlavorSupported(var1)) {
         throw new IllegalArgumentException("flavor " + var1 + "is not supported");
      } else if (var2 == null) {
         return null;
      } else {
         HashAttributeSet var4 = new HashAttributeSet();
         Attribute[] var5 = var2.toArray();

         for(int var6 = 0; var6 < var5.length; ++var6) {
            try {
               Attribute var3 = var5[var6];
               if (!this.isAttributeCategorySupported(var3.getCategory())) {
                  var4.add(var3);
               } else if (!this.isAttributeValueSupported(var3, var1, var2)) {
                  var4.add(var3);
               }
            } catch (ClassCastException var8) {
            }
         }

         if (var4.isEmpty()) {
            return null;
         } else {
            return var4;
         }
      }
   }

   public ServiceUIFactory getServiceUIFactory() {
      return null;
   }

   public String toString() {
      return "Unix Printer : " + this.getName();
   }

   public boolean equals(Object var1) {
      return var1 == this || var1 instanceof UnixPrintService && ((UnixPrintService)var1).getName().equals(this.getName());
   }

   public int hashCode() {
      return this.getClass().hashCode() + this.getName().hashCode();
   }

   public boolean usesClass(Class var1) {
      return var1 == PSPrinterJob.class;
   }

   static {
      supportedDocFlavorsInit = new DocFlavor[]{DocFlavor.BYTE_ARRAY.POSTSCRIPT, DocFlavor.INPUT_STREAM.POSTSCRIPT, DocFlavor.URL.POSTSCRIPT, DocFlavor.BYTE_ARRAY.GIF, DocFlavor.INPUT_STREAM.GIF, DocFlavor.URL.GIF, DocFlavor.BYTE_ARRAY.JPEG, DocFlavor.INPUT_STREAM.JPEG, DocFlavor.URL.JPEG, DocFlavor.BYTE_ARRAY.PNG, DocFlavor.INPUT_STREAM.PNG, DocFlavor.URL.PNG, DocFlavor.CHAR_ARRAY.TEXT_PLAIN, DocFlavor.READER.TEXT_PLAIN, DocFlavor.STRING.TEXT_PLAIN, DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_8, DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_16, DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_16BE, DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_16LE, DocFlavor.BYTE_ARRAY.TEXT_PLAIN_US_ASCII, DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_8, DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_16, DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_16BE, DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_16LE, DocFlavor.INPUT_STREAM.TEXT_PLAIN_US_ASCII, DocFlavor.URL.TEXT_PLAIN_UTF_8, DocFlavor.URL.TEXT_PLAIN_UTF_16, DocFlavor.URL.TEXT_PLAIN_UTF_16BE, DocFlavor.URL.TEXT_PLAIN_UTF_16LE, DocFlavor.URL.TEXT_PLAIN_US_ASCII, DocFlavor.SERVICE_FORMATTED.PAGEABLE, DocFlavor.SERVICE_FORMATTED.PRINTABLE, DocFlavor.BYTE_ARRAY.AUTOSENSE, DocFlavor.URL.AUTOSENSE, DocFlavor.INPUT_STREAM.AUTOSENSE};
      supportedHostDocFlavors = new DocFlavor[]{DocFlavor.BYTE_ARRAY.TEXT_PLAIN_HOST, DocFlavor.INPUT_STREAM.TEXT_PLAIN_HOST, DocFlavor.URL.TEXT_PLAIN_HOST};
      encoding = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("file.encoding")));
      serviceAttrCats = new Class[]{PrinterName.class, PrinterIsAcceptingJobs.class, QueuedJobCount.class};
      otherAttrCats = new Class[]{Chromaticity.class, Copies.class, Destination.class, Fidelity.class, JobName.class, JobSheets.class, Media.class, MediaPrintableArea.class, OrientationRequested.class, PageRanges.class, RequestingUserName.class, SheetCollate.class, Sides.class};
      MAXCOPIES = 1000;
      mediaSizes = new MediaSizeName[]{MediaSizeName.NA_LETTER, MediaSizeName.TABLOID, MediaSizeName.LEDGER, MediaSizeName.NA_LEGAL, MediaSizeName.EXECUTIVE, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5};
      mpas = null;
   }
}
