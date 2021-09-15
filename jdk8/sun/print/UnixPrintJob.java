package sun.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.security.AccessController;
import java.security.PrivilegedAction;
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
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobOriginatingUserName;
import javax.print.attribute.standard.JobSheets;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.NumberUp;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;

public class UnixPrintJob implements CancelablePrintJob {
   private static String debugPrefix = "UnixPrintJob>> ";
   private transient Vector jobListeners;
   private transient Vector attrListeners;
   private transient Vector listenedAttributeSets;
   private PrintService service;
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
   private MediaSizeName mediaName;
   private MediaSize mediaSize;
   private CustomMediaTray customTray;
   private OrientationRequested orient;
   private NumberUp nUp;
   private Sides sides;
   private static int DESTPRINTER = 1;
   private static int DESTFILE = 2;
   private int mDestType;
   private File spoolFile;
   private String mDestination;
   private String mOptions;
   private boolean mNoJobSheet;

   UnixPrintJob(PrintService var1) {
      this.mediaName = MediaSizeName.NA_LETTER;
      this.mediaSize = MediaSize.NA.LETTER;
      this.customTray = null;
      this.orient = OrientationRequested.PORTRAIT;
      this.nUp = null;
      this.sides = null;
      this.mDestType = DESTPRINTER;
      this.mOptions = "";
      this.mNoJobSheet = false;
      this.service = var1;
      this.mDestination = var1.getName();
      if (UnixPrintServiceLookup.isMac()) {
         this.mDestination = ((IPPPrintService)var1).getDest();
      }

      this.mDestType = DESTPRINTER;
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
      switch(var1) {
      case 101:
      case 102:
      case 103:
      case 105:
      case 106:
         this.closeDataStreams();
      case 104:
      default:
         synchronized(this) {
            if (this.jobListeners != null) {
               PrintJobEvent var4 = new PrintJobEvent(this, var1);

               for(int var5 = 0; var5 < this.jobListeners.size(); ++var5) {
                  PrintJobListener var3 = (PrintJobListener)((PrintJobListener)this.jobListeners.elementAt(var5));
                  switch(var1) {
                  case 101:
                     var3.printJobCanceled(var4);
                  case 102:
                  case 104:
                  default:
                     break;
                  case 103:
                     var3.printJobFailed(var4);
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

      if ((PrinterIsAcceptingJobs)this.service.getAttribute(PrinterIsAcceptingJobs.class) == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS) {
         throw new PrintException("Printer is not accepting job.");
      } else {
         this.doc = var1;
         DocFlavor var3 = var1.getDocFlavor();

         Object var4;
         try {
            var4 = var1.getPrintData();
         } catch (IOException var45) {
            this.notifyEvent(103);
            throw new PrintException("can't get print data: " + var45.toString());
         }

         if (var4 == null) {
            throw new PrintException("Null print data.");
         } else if (var3 != null && this.service.isDocFlavorSupported(var3)) {
            this.initializeAttributeSets(var1, var2);
            this.getAttributeValues(var3);
            String var55;
            if (this.service instanceof IPPPrintService && CUPSPrinter.isCupsRunning()) {
               IPPPrintService.debug_println(debugPrefix + "instanceof IPPPrintService");
               if (this.mediaName != null) {
                  CustomMediaSizeName var5 = ((IPPPrintService)this.service).findCustomMedia(this.mediaName);
                  if (var5 != null) {
                     this.mOptions = " media=" + var5.getChoiceName();
                  }
               }

               if (this.customTray != null && this.customTray instanceof CustomMediaTray) {
                  var55 = this.customTray.getChoiceName();
                  if (var55 != null) {
                     this.mOptions = this.mOptions + " media=" + var55;
                  }
               }

               if (this.nUp != null) {
                  this.mOptions = this.mOptions + " number-up=" + this.nUp.getValue();
               }

               if (this.orient != OrientationRequested.PORTRAIT && var3 != null && !var3.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) {
                  this.mOptions = this.mOptions + " orientation-requested=" + this.orient.getValue();
               }

               if (this.sides != null) {
                  this.mOptions = this.mOptions + " sides=" + this.sides;
               }
            }

            IPPPrintService.debug_println(debugPrefix + "mOptions " + this.mOptions);
            var55 = var3.getRepresentationClassName();
            String var6 = var3.getParameter("charset");
            String var7 = "us-ascii";
            if (var6 != null && !var6.equals("")) {
               var7 = var6;
            }

            if (!var3.equals(DocFlavor.INPUT_STREAM.GIF) && !var3.equals(DocFlavor.INPUT_STREAM.JPEG) && !var3.equals(DocFlavor.INPUT_STREAM.PNG) && !var3.equals(DocFlavor.BYTE_ARRAY.GIF) && !var3.equals(DocFlavor.BYTE_ARRAY.JPEG) && !var3.equals(DocFlavor.BYTE_ARRAY.PNG)) {
               URL var8;
               if (!var3.equals(DocFlavor.URL.GIF) && !var3.equals(DocFlavor.URL.JPEG) && !var3.equals(DocFlavor.URL.PNG)) {
                  if (!var3.equals(DocFlavor.CHAR_ARRAY.TEXT_PLAIN) && !var3.equals(DocFlavor.READER.TEXT_PLAIN) && !var3.equals(DocFlavor.STRING.TEXT_PLAIN)) {
                     if (!var55.equals("[B") && !var55.equals("java.io.InputStream")) {
                        if (!var55.equals("java.net.URL")) {
                           if (var55.equals("java.awt.print.Pageable")) {
                              try {
                                 this.pageableJob((Pageable)var1.getPrintData());
                                 if (this.service instanceof IPPPrintService) {
                                    ((IPPPrintService)this.service).wakeNotifier();
                                 } else {
                                    ((UnixPrintService)this.service).wakeNotifier();
                                 }

                                 return;
                              } catch (ClassCastException var38) {
                                 this.notifyEvent(103);
                                 throw new PrintException(var38);
                              } catch (IOException var39) {
                                 this.notifyEvent(103);
                                 throw new PrintException(var39);
                              }
                           }

                           if (var55.equals("java.awt.print.Printable")) {
                              try {
                                 this.printableJob((Printable)var1.getPrintData());
                                 if (this.service instanceof IPPPrintService) {
                                    ((IPPPrintService)this.service).wakeNotifier();
                                 } else {
                                    ((UnixPrintService)this.service).wakeNotifier();
                                 }

                                 return;
                              } catch (ClassCastException var40) {
                                 this.notifyEvent(103);
                                 throw new PrintException(var40);
                              } catch (IOException var41) {
                                 this.notifyEvent(103);
                                 throw new PrintException(var41);
                              }
                           }

                           this.notifyEvent(103);
                           throw new PrintException("unrecognized class: " + var55);
                        }

                        var8 = (URL)var4;

                        try {
                           this.instream = var8.openStream();
                        } catch (IOException var42) {
                           this.notifyEvent(103);
                           throw new PrintException(var42.toString());
                        }
                     } else {
                        try {
                           this.instream = var1.getStreamForBytes();
                           if (this.instream == null) {
                              this.notifyEvent(103);
                              throw new PrintException("No stream for data");
                           }
                        } catch (IOException var43) {
                           this.notifyEvent(103);
                           throw new PrintException(var43.toString());
                        }
                     }
                  } else {
                     try {
                        this.reader = var1.getReaderForText();
                        if (this.reader == null) {
                           this.notifyEvent(103);
                           throw new PrintException("No reader for data");
                        }
                     } catch (IOException var44) {
                        this.notifyEvent(103);
                        throw new PrintException(var44.toString());
                     }
                  }
               } else {
                  try {
                     var8 = (URL)var4;
                     if (!(this.service instanceof IPPPrintService) || !((IPPPrintService)this.service).isIPPSupportedImages(var3.getMimeType())) {
                        this.printableJob(new ImagePrinter(var8));
                        ((UnixPrintService)this.service).wakeNotifier();
                        return;
                     }

                     this.instream = var8.openStream();
                  } catch (ClassCastException var50) {
                     this.notifyEvent(103);
                     throw new PrintException(var50);
                  } catch (IOException var51) {
                     this.notifyEvent(103);
                     throw new PrintException(var51.toString());
                  }
               }
            } else {
               try {
                  this.instream = var1.getStreamForBytes();
                  if (this.instream == null) {
                     this.notifyEvent(103);
                     throw new PrintException("No stream for data");
                  }

                  if (!(this.service instanceof IPPPrintService) || !((IPPPrintService)this.service).isIPPSupportedImages(var3.getMimeType())) {
                     this.printableJob(new ImagePrinter(this.instream));
                     ((UnixPrintService)this.service).wakeNotifier();
                     return;
                  }
               } catch (ClassCastException var52) {
                  this.notifyEvent(103);
                  throw new PrintException(var52);
               } catch (IOException var53) {
                  this.notifyEvent(103);
                  throw new PrintException(var53);
               }
            }

            UnixPrintJob.PrinterOpener var56 = new UnixPrintJob.PrinterOpener();
            AccessController.doPrivileged((PrivilegedAction)var56);
            if (var56.pex != null) {
               throw var56.pex;
            } else {
               OutputStream var9 = var56.result;
               BufferedWriter var10 = null;
               int var64;
               if (this.instream == null && this.reader != null) {
                  BufferedReader var58 = new BufferedReader(this.reader);
                  OutputStreamWriter var61 = new OutputStreamWriter(var9);
                  var10 = new BufferedWriter(var61);
                  char[] var63 = new char[1024];

                  try {
                     while((var64 = var58.read(var63, 0, var63.length)) >= 0) {
                        var10.write((char[])var63, 0, var64);
                     }

                     var58.close();
                     var10.flush();
                     var10.close();
                  } catch (IOException var49) {
                     this.notifyEvent(103);
                     throw new PrintException(var49);
                  }
               } else if (this.instream != null && var3.getMediaType().equalsIgnoreCase("text")) {
                  try {
                     InputStreamReader var57 = new InputStreamReader(this.instream, var7);
                     BufferedReader var60 = new BufferedReader(var57);
                     OutputStreamWriter var62 = new OutputStreamWriter(var9);
                     var10 = new BufferedWriter(var62);
                     char[] var65 = new char[1024];

                     int var15;
                     while((var15 = var60.read(var65, 0, var65.length)) >= 0) {
                        var10.write((char[])var65, 0, var15);
                     }

                     var10.flush();
                  } catch (IOException var47) {
                     this.notifyEvent(103);
                     throw new PrintException(var47);
                  } finally {
                     try {
                        if (var10 != null) {
                           var10.close();
                        }
                     } catch (IOException var37) {
                     }

                  }
               } else if (this.instream != null) {
                  BufferedInputStream var11 = new BufferedInputStream(this.instream);
                  BufferedOutputStream var12 = new BufferedOutputStream(var9);
                  byte[] var13 = new byte[1024];
                  boolean var14 = false;

                  try {
                     while((var64 = var11.read(var13)) >= 0) {
                        var12.write(var13, 0, var64);
                     }

                     var11.close();
                     var12.flush();
                     var12.close();
                  } catch (IOException var46) {
                     this.notifyEvent(103);
                     throw new PrintException(var46);
                  }
               }

               this.notifyEvent(106);
               if (this.mDestType == DESTPRINTER) {
                  UnixPrintJob.PrinterSpooler var59 = new UnixPrintJob.PrinterSpooler();
                  AccessController.doPrivileged((PrivilegedAction)var59);
                  if (var59.pex != null) {
                     throw var59.pex;
                  }
               }

               this.notifyEvent(105);
               if (this.service instanceof IPPPrintService) {
                  ((IPPPrintService)this.service).wakeNotifier();
               } else {
                  ((UnixPrintService)this.service).wakeNotifier();
               }

            }
         } else {
            this.notifyEvent(103);
            throw new PrintJobFlavorException("invalid flavor", var3);
         }
      }
   }

   public void printableJob(Printable var1) throws PrintException {
      try {
         synchronized(this) {
            if (this.job != null) {
               throw new PrintException("already printing");
            }

            this.job = new PSPrinterJob();
         }

         this.job.setPrintService(this.getPrintService());
         this.job.setCopies(this.copies);
         this.job.setJobName(this.jobName);
         PageFormat var2 = new PageFormat();
         if (this.mediaSize != null) {
            Paper var3 = new Paper();
            var3.setSize((double)this.mediaSize.getX(25400) * 72.0D, (double)this.mediaSize.getY(25400) * 72.0D);
            var3.setImageableArea(72.0D, 72.0D, var3.getWidth() - 144.0D, var3.getHeight() - 144.0D);
            var2.setPaper(var3);
         }

         if (this.orient == OrientationRequested.REVERSE_LANDSCAPE) {
            var2.setOrientation(2);
         } else if (this.orient == OrientationRequested.LANDSCAPE) {
            var2.setOrientation(0);
         }

         this.job.setPrintable(var1, var2);
         this.job.print(this.reqAttrSet);
         this.notifyEvent(106);
      } catch (PrinterException var9) {
         this.notifyEvent(103);
         throw new PrintException(var9);
      } finally {
         this.printReturned = true;
         this.notifyEvent(105);
      }

   }

   public void pageableJob(Pageable var1) throws PrintException {
      try {
         synchronized(this) {
            if (this.job != null) {
               throw new PrintException("already printing");
            }

            this.job = new PSPrinterJob();
         }

         this.job.setPrintService(this.getPrintService());
         this.job.setCopies(this.copies);
         this.job.setJobName(this.jobName);
         this.job.setPageable(var1);
         this.job.print(this.reqAttrSet);
         this.notifyEvent(106);
      } catch (PrinterException var9) {
         this.notifyEvent(103);
         throw new PrintException(var9);
      } finally {
         this.printReturned = true;
         this.notifyEvent(105);
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

         if (var3 == Destination.class) {
            URI var6 = ((Destination)var2).getURI();
            if (!"file".equals(var6.getScheme())) {
               this.notifyEvent(103);
               throw new PrintException("Not a file: URI");
            }

            try {
               this.mDestType = DESTFILE;
               this.mDestination = (new File(var6)).getPath();
            } catch (Exception var10) {
               throw new PrintException(var10);
            }

            SecurityManager var7 = System.getSecurityManager();
            if (var7 != null) {
               try {
                  var7.checkWrite(this.mDestination);
               } catch (SecurityException var9) {
                  this.notifyEvent(103);
                  throw new PrintException(var9);
               }
            }
         } else if (var3 == JobSheets.class) {
            if ((JobSheets)var2 == JobSheets.NONE) {
               this.mNoJobSheet = true;
            }
         } else if (var3 == JobName.class) {
            this.jobName = ((JobName)var2).getValue();
         } else if (var3 == Copies.class) {
            this.copies = ((Copies)var2).getValue();
         } else if (var3 == Media.class) {
            if (var2 instanceof MediaSizeName) {
               this.mediaName = (MediaSizeName)var2;
               IPPPrintService.debug_println(debugPrefix + "mediaName " + this.mediaName);
               if (!this.service.isAttributeValueSupported(var2, (DocFlavor)null, (AttributeSet)null)) {
                  this.mediaSize = MediaSize.getMediaSizeForName(this.mediaName);
               }
            } else if (var2 instanceof CustomMediaTray) {
               this.customTray = (CustomMediaTray)var2;
            }
         } else if (var3 == OrientationRequested.class) {
            this.orient = (OrientationRequested)var2;
         } else if (var3 == NumberUp.class) {
            this.nUp = (NumberUp)var2;
         } else if (var3 == Sides.class) {
            this.sides = (Sides)var2;
         }
      }

   }

   private String[] printExecCmd(String var1, String var2, boolean var3, String var4, int var5, String var6) {
      boolean var7 = true;
      boolean var8 = true;
      boolean var9 = true;
      boolean var10 = true;
      boolean var11 = true;
      boolean var12 = false;
      int var14 = 2;
      byte var15 = 0;
      if (var1 != null && !var1.equals("") && !var1.equals("lp")) {
         var12 |= var7;
         ++var14;
      }

      if (var2 != null && !var2.equals("")) {
         var12 |= var8;
         ++var14;
      }

      if (var4 != null && !var4.equals("")) {
         var12 |= var9;
         ++var14;
      }

      if (var5 > 1) {
         var12 |= var10;
         ++var14;
      }

      if (var3) {
         var12 |= var11;
         ++var14;
      }

      String[] var13;
      int var17;
      if (UnixPrintServiceLookup.osname.equals("SunOS")) {
         ++var14;
         var13 = new String[var14];
         var17 = var15 + 1;
         var13[var15] = "/usr/bin/lp";
         var13[var17++] = "-c";
         if (var12 & var7) {
            var13[var17++] = "-d" + var1;
         }

         if (var12 & var9) {
            String var16 = "\"";
            var13[var17++] = "-t " + var16 + var4 + var16;
         }

         if (var12 & var10) {
            var13[var17++] = "-n " + var5;
         }

         if (var12 & var11) {
            var13[var17++] = "-o nobanner";
         }

         if (var12 & var8) {
            var13[var17++] = "-o " + var2;
         }
      } else {
         var13 = new String[var14];
         var17 = var15 + 1;
         var13[var15] = "/usr/bin/lpr";
         if (var12 & var7) {
            var13[var17++] = "-P" + var1;
         }

         if (var12 & var9) {
            var13[var17++] = "-J " + var4;
         }

         if (var12 & var10) {
            var13[var17++] = "-#" + var5;
         }

         if (var12 & var11) {
            var13[var17++] = "-h";
         }

         if (var12 & var8) {
            var13[var17++] = "-o" + var2;
         }
      }

      var13[var17++] = var6;
      if (IPPPrintService.debugPrint) {
         System.out.println("UnixPrintJob>> execCmd");

         for(int var18 = 0; var18 < var13.length; ++var18) {
            System.out.print(" " + var13[var18]);
         }

         System.out.println();
      }

      return var13;
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

   private class PrinterSpooler implements PrivilegedAction {
      PrintException pex;

      private PrinterSpooler() {
      }

      private void handleProcessFailure(Process var1, String[] var2, int var3) throws IOException {
         StringWriter var4 = new StringWriter();
         Throwable var5 = null;

         try {
            PrintWriter var6 = new PrintWriter(var4);
            Throwable var7 = null;

            try {
               var6.append("error=").append(Integer.toString(var3));
               var6.append(" running:");
               String[] var8 = var2;
               int var9 = var2.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  String var11 = var8[var10];
                  var6.append(" '").append(var11).append("'");
               }

               try {
                  InputStream var157 = var1.getErrorStream();
                  Throwable var158 = null;
                  boolean var102 = false;

                  try {
                     var102 = true;
                     InputStreamReader var159 = new InputStreamReader(var157);
                     Throwable var160 = null;

                     try {
                        BufferedReader var12 = new BufferedReader(var159);
                        Throwable var13 = null;

                        try {
                           while(var12.ready()) {
                              var6.println();
                              var6.append("\t\t").append(var12.readLine());
                           }
                        } catch (Throwable var146) {
                           var13 = var146;
                           throw var146;
                        } finally {
                           if (var12 != null) {
                              if (var13 != null) {
                                 try {
                                    var12.close();
                                 } catch (Throwable var144) {
                                    var13.addSuppressed(var144);
                                 }
                              } else {
                                 var12.close();
                              }
                           }

                        }
                     } catch (Throwable var148) {
                        var160 = var148;
                        throw var148;
                     } finally {
                        if (var159 != null) {
                           if (var160 != null) {
                              try {
                                 var159.close();
                              } catch (Throwable var143) {
                                 var160.addSuppressed(var143);
                              }
                           } else {
                              var159.close();
                           }
                        }

                     }

                     var102 = false;
                  } catch (Throwable var150) {
                     var158 = var150;
                     throw var150;
                  } finally {
                     if (var102) {
                        if (var157 != null) {
                           if (var158 != null) {
                              try {
                                 var157.close();
                              } catch (Throwable var142) {
                                 var158.addSuppressed(var142);
                              }
                           } else {
                              var157.close();
                           }
                        }

                     }
                  }

                  if (var157 != null) {
                     if (var158 != null) {
                        try {
                           var157.close();
                        } catch (Throwable var145) {
                           var158.addSuppressed(var145);
                        }
                     } else {
                        var157.close();
                     }
                  }
               } finally {
                  var6.flush();
                  throw new IOException(var4.toString());
               }
            } catch (Throwable var153) {
               var7 = var153;
               throw var153;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var141) {
                        var7.addSuppressed(var141);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         } catch (Throwable var155) {
            var5 = var155;
            throw var155;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var140) {
                     var5.addSuppressed(var140);
                  }
               } else {
                  var4.close();
               }
            }

         }
      }

      public Object run() {
         if (UnixPrintJob.this.spoolFile != null && UnixPrintJob.this.spoolFile.exists()) {
            try {
               String var1 = UnixPrintJob.this.spoolFile.getAbsolutePath();
               String[] var2 = UnixPrintJob.this.printExecCmd(UnixPrintJob.this.mDestination, UnixPrintJob.this.mOptions, UnixPrintJob.this.mNoJobSheet, UnixPrintJob.this.jobName, UnixPrintJob.this.copies, var1);
               Process var3 = Runtime.getRuntime().exec(var2);
               var3.waitFor();
               int var4 = var3.exitValue();
               if (0 != var4) {
                  this.handleProcessFailure(var3, var2, var4);
               }

               UnixPrintJob.this.notifyEvent(106);
            } catch (IOException var9) {
               UnixPrintJob.this.notifyEvent(103);
               this.pex = new PrintException(var9);
            } catch (InterruptedException var10) {
               UnixPrintJob.this.notifyEvent(103);
               this.pex = new PrintException(var10);
            } finally {
               UnixPrintJob.this.spoolFile.delete();
               UnixPrintJob.this.notifyEvent(105);
            }

            return null;
         } else {
            this.pex = new PrintException("No spool file");
            UnixPrintJob.this.notifyEvent(103);
            return null;
         }
      }

      // $FF: synthetic method
      PrinterSpooler(Object var2) {
         this();
      }
   }

   private class PrinterOpener implements PrivilegedAction {
      PrintException pex;
      OutputStream result;

      private PrinterOpener() {
      }

      public Object run() {
         try {
            if (UnixPrintJob.this.mDestType == UnixPrintJob.DESTFILE) {
               UnixPrintJob.this.spoolFile = new File(UnixPrintJob.this.mDestination);
            } else {
               UnixPrintJob.this.spoolFile = Files.createTempFile("javaprint", "").toFile();
               UnixPrintJob.this.spoolFile.deleteOnExit();
            }

            this.result = new FileOutputStream(UnixPrintJob.this.spoolFile);
            return this.result;
         } catch (IOException var2) {
            UnixPrintJob.this.notifyEvent(103);
            this.pex = new PrintException(var2);
            return null;
         }
      }

      // $FF: synthetic method
      PrinterOpener(Object var2) {
         this();
      }
   }
}
