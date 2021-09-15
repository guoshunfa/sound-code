package sun.print;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.Finishings;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobSheets;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.NumberUp;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PDLOverrideSupported;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PagesPerMinute;
import javax.print.attribute.standard.PagesPerMinuteColor;
import javax.print.attribute.standard.PrinterInfo;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterLocation;
import javax.print.attribute.standard.PrinterMakeAndModel;
import javax.print.attribute.standard.PrinterMessageFromOperator;
import javax.print.attribute.standard.PrinterMoreInfo;
import javax.print.attribute.standard.PrinterMoreInfoManufacturer;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.PrinterURI;
import javax.print.attribute.standard.QueuedJobCount;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintServiceAttributeListener;
import sun.security.action.GetPropertyAction;

public class IPPPrintService implements PrintService, SunPrinterJobService {
   public static final boolean debugPrint;
   private static final String debugPrefix = "IPPPrintService>> ";
   private static final String FORCE_PIPE_PROP = "sun.print.ippdebug";
   private String printer;
   private URI myURI;
   private URL myURL;
   private transient ServiceNotifier notifier = null;
   private static int MAXCOPIES;
   private static short MAX_ATTRIBUTE_LENGTH;
   private CUPSPrinter cps;
   private HttpURLConnection urlConnection = null;
   private DocFlavor[] supportedDocFlavors;
   private Class[] supportedCats;
   private MediaTray[] mediaTrays;
   private MediaSizeName[] mediaSizeNames;
   private CustomMediaSizeName[] customMediaSizeNames;
   private int defaultMediaIndex;
   private boolean isCupsPrinter;
   private boolean init;
   private Boolean isPS;
   private HashMap getAttMap;
   private boolean pngImagesAdded = false;
   private boolean gifImagesAdded = false;
   private boolean jpgImagesAdded = false;
   private static final byte STATUSCODE_SUCCESS = 0;
   private static final byte GRPTAG_OP_ATTRIBUTES = 1;
   private static final byte GRPTAG_JOB_ATTRIBUTES = 2;
   private static final byte GRPTAG_PRINTER_ATTRIBUTES = 4;
   private static final byte GRPTAG_END_ATTRIBUTES = 3;
   public static final String OP_GET_ATTRIBUTES = "000B";
   public static final String OP_CUPS_GET_DEFAULT = "4001";
   public static final String OP_CUPS_GET_PRINTERS = "4002";
   private static Object[] printReqAttribDefault;
   private static Object[][] serviceAttributes;
   private static DocFlavor[] appPDF;
   private static DocFlavor[] appPostScript;
   private static DocFlavor[] appOctetStream;
   private static DocFlavor[] textPlain;
   private static DocFlavor[] textPlainHost;
   private static DocFlavor[] imageJPG;
   private static DocFlavor[] imageGIF;
   private static DocFlavor[] imagePNG;
   private static DocFlavor[] textHtml;
   private static DocFlavor[] textHtmlHost;
   private static DocFlavor[] appPCL;
   private static Object[] allDocFlavors;

   protected static void debug_println(String var0) {
      if (debugPrint) {
         System.out.println(var0);
      }

   }

   IPPPrintService(String var1, URL var2) {
      if (var1 != null && var2 != null) {
         this.printer = var1;
         this.supportedDocFlavors = null;
         this.supportedCats = null;
         this.mediaSizeNames = null;
         this.customMediaSizeNames = null;
         this.mediaTrays = null;
         this.myURL = var2;
         this.cps = null;
         this.isCupsPrinter = false;
         this.init = false;
         this.defaultMediaIndex = -1;
         String var3 = this.myURL.getHost();
         if (var3 != null && var3.equals(CUPSPrinter.getServer())) {
            this.isCupsPrinter = true;

            try {
               this.myURI = new URI("ipp://" + var3 + "/printers/" + this.printer);
               debug_println("IPPPrintService>> IPPPrintService myURI : " + this.myURI);
            } catch (URISyntaxException var5) {
               throw new IllegalArgumentException("invalid url");
            }
         }

      } else {
         throw new IllegalArgumentException("null uri or printer name");
      }
   }

   IPPPrintService(String var1, String var2, boolean var3) {
      if (var1 != null && var2 != null) {
         this.printer = var1;
         this.supportedDocFlavors = null;
         this.supportedCats = null;
         this.mediaSizeNames = null;
         this.customMediaSizeNames = null;
         this.mediaTrays = null;
         this.cps = null;
         this.init = false;
         this.defaultMediaIndex = -1;

         try {
            this.myURL = new URL(var2.replaceFirst("ipp", "http"));
         } catch (Exception var6) {
            debug_println("IPPPrintService>>  IPPPrintService, myURL=" + this.myURL + " Exception= " + var6);
            throw new IllegalArgumentException("invalid url");
         }

         this.isCupsPrinter = var3;

         try {
            this.myURI = new URI(var2);
            debug_println("IPPPrintService>> IPPPrintService myURI : " + this.myURI);
         } catch (URISyntaxException var5) {
            throw new IllegalArgumentException("invalid uri");
         }
      } else {
         throw new IllegalArgumentException("null uri or printer name");
      }
   }

   private void initAttributes() {
      if (!this.init) {
         this.customMediaSizeNames = new CustomMediaSizeName[0];
         if ((this.urlConnection = getIPPConnection(this.myURL)) == null) {
            this.mediaSizeNames = new MediaSizeName[0];
            this.mediaTrays = new MediaTray[0];
            debug_println("IPPPrintService>> initAttributes, NULL urlConnection ");
            this.init = true;
            return;
         }

         this.opGetAttributes();
         if (this.isCupsPrinter) {
            try {
               this.cps = new CUPSPrinter(this.printer);
               this.mediaSizeNames = this.cps.getMediaSizeNames();
               this.mediaTrays = this.cps.getMediaTrays();
               this.customMediaSizeNames = this.cps.getCustomMediaSizeNames();
               this.defaultMediaIndex = this.cps.getDefaultMediaIndex();
               this.urlConnection.disconnect();
               this.init = true;
               return;
            } catch (Exception var5) {
               debug_println("IPPPrintService>> initAttributes, error creating CUPSPrinter e=" + var5);
            }
         }

         Media[] var1 = (Media[])this.getSupportedMedia();
         ArrayList var2 = new ArrayList();
         ArrayList var3 = new ArrayList();

         for(int var4 = 0; var4 < var1.length; ++var4) {
            if (var1[var4] instanceof MediaSizeName) {
               var2.add(var1[var4]);
            } else if (var1[var4] instanceof MediaTray) {
               var3.add(var1[var4]);
            }
         }

         if (var2 != null) {
            this.mediaSizeNames = new MediaSizeName[var2.size()];
            this.mediaSizeNames = (MediaSizeName[])((MediaSizeName[])var2.toArray(this.mediaSizeNames));
         }

         if (var3 != null) {
            this.mediaTrays = new MediaTray[var3.size()];
            this.mediaTrays = (MediaTray[])((MediaTray[])var3.toArray(this.mediaTrays));
         }

         this.urlConnection.disconnect();
         this.init = true;
      }

   }

   public DocPrintJob createPrintJob() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPrintJobAccess();
      }

      return new UnixPrintJob(this);
   }

   public synchronized Object getSupportedAttributeValues(Class<? extends Attribute> var1, DocFlavor var2, AttributeSet var3) {
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
         } else if (!this.isDestinationSupported(var2, var3)) {
            return null;
         } else {
            this.initAttributes();
            if (var1 != Copies.class && var1 != CopiesSupported.class) {
               if (var1 == Chromaticity.class) {
                  if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE) && this.isIPPSupportedImages(var2.getMimeType())) {
                     return null;
                  } else {
                     Chromaticity[] var37 = new Chromaticity[]{Chromaticity.COLOR};
                     return var37;
                  }
               } else if (var1 == Destination.class) {
                  if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                     return null;
                  } else {
                     try {
                        return new Destination((new File("out.ps")).toURI());
                     } catch (SecurityException var11) {
                        try {
                           return new Destination(new URI("file:out.ps"));
                        } catch (URISyntaxException var10) {
                           return null;
                        }
                     }
                  }
               } else if (var1 == Fidelity.class) {
                  Fidelity[] var36 = new Fidelity[]{Fidelity.FIDELITY_FALSE, Fidelity.FIDELITY_TRUE};
                  return var36;
               } else {
                  int var7;
                  AttributeClass var13;
                  int[] var14;
                  if (var1 == Finishings.class) {
                     var13 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get("finishings-supported") : null;
                     if (var13 != null) {
                        var14 = var13.getArrayOfIntValues();
                        if (var14 != null && var14.length > 0) {
                           Finishings[] var17 = new Finishings[var14.length];

                           for(var7 = 0; var7 < var14.length; ++var7) {
                              var17[var7] = Finishings.NONE;
                              Finishings[] var8 = (Finishings[])((Finishings[])(new IPPPrintService.ExtFinishing(100)).getAll());

                              for(int var9 = 0; var9 < var8.length; ++var9) {
                                 if (var14[var7] == var8[var9].getValue()) {
                                    var17[var7] = var8[var9];
                                    break;
                                 }
                              }
                           }

                           return var17;
                        }
                     }
                  } else {
                     if (var1 == JobName.class) {
                        return new JobName("Java Printing", (Locale)null);
                     }

                     if (var1 == JobSheets.class) {
                        JobSheets[] var35 = new JobSheets[]{JobSheets.NONE, JobSheets.STANDARD};
                        return var35;
                     }

                     int var25;
                     if (var1 == Media.class) {
                        Media[] var34 = new Media[this.mediaSizeNames.length + this.mediaTrays.length];

                        for(var25 = 0; var25 < this.mediaSizeNames.length; ++var25) {
                           var34[var25] = this.mediaSizeNames[var25];
                        }

                        for(var25 = 0; var25 < this.mediaTrays.length; ++var25) {
                           var34[var25 + this.mediaSizeNames.length] = this.mediaTrays[var25];
                        }

                        if (var34.length == 0) {
                           var34 = new Media[]{(Media)this.getDefaultAttributeValue(Media.class)};
                        }

                        return var34;
                     }

                     int var29;
                     if (var1 == MediaPrintableArea.class) {
                        MediaPrintableArea[] var27 = null;
                        if (this.cps != null) {
                           var27 = this.cps.getMediaPrintableArea();
                        }

                        if (var27 == null) {
                           var27 = new MediaPrintableArea[]{(MediaPrintableArea)this.getDefaultAttributeValue(MediaPrintableArea.class)};
                        }

                        if (var3 != null && var3.size() != 0) {
                           var25 = -1;
                           Media var28 = (Media)var3.get(Media.class);
                           if (var28 != null && var28 instanceof MediaSizeName) {
                              MediaSizeName var32 = (MediaSizeName)var28;
                              if (this.mediaSizeNames.length == 0 && var32.equals(this.getDefaultAttributeValue(Media.class))) {
                                 return var27;
                              }

                              for(var29 = 0; var29 < this.mediaSizeNames.length; ++var29) {
                                 if (var32.equals(this.mediaSizeNames[var29])) {
                                    var25 = var29;
                                 }
                              }
                           }

                           if (var25 == -1) {
                              return null;
                           }

                           MediaPrintableArea[] var33 = new MediaPrintableArea[]{var27[var25]};
                           return var33;
                        }

                        ArrayList var22 = new ArrayList();

                        for(int var26 = 0; var26 < var27.length; ++var26) {
                           if (var27[var26] != null) {
                              var22.add(var27[var26]);
                           }
                        }

                        if (var22.size() > 0) {
                           var27 = new MediaPrintableArea[var22.size()];
                           var22.toArray(var27);
                        }

                        return var27;
                     }

                     if (var1 == NumberUp.class) {
                        var13 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get("number-up-supported") : null;
                        if (var13 != null) {
                           var14 = var13.getArrayOfIntValues();
                           if (var14 == null) {
                              return null;
                           }

                           NumberUp[] var19 = new NumberUp[var14.length];

                           for(var7 = 0; var7 < var14.length; ++var7) {
                              var19[var7] = new NumberUp(var14[var7]);
                           }

                           return var19;
                        }
                     } else {
                        if (var1 == OrientationRequested.class) {
                           if (var2 != null && (var2.equals(DocFlavor.INPUT_STREAM.POSTSCRIPT) || var2.equals(DocFlavor.URL.POSTSCRIPT) || var2.equals(DocFlavor.BYTE_ARRAY.POSTSCRIPT))) {
                              return null;
                           }

                           boolean var23 = false;
                           OrientationRequested[] var18 = null;
                           AttributeClass var24 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get("orientation-requested-supported") : null;
                           if (var24 != null) {
                              int[] var30 = var24.getArrayOfIntValues();
                              if (var30 != null && var30.length > 0) {
                                 var18 = new OrientationRequested[var30.length];

                                 for(var29 = 0; var29 < var30.length; ++var29) {
                                    switch(var30[var29]) {
                                    case 3:
                                    default:
                                       var18[var29] = OrientationRequested.PORTRAIT;
                                       break;
                                    case 4:
                                       var18[var29] = OrientationRequested.LANDSCAPE;
                                       break;
                                    case 5:
                                       var18[var29] = OrientationRequested.REVERSE_LANDSCAPE;
                                       break;
                                    case 6:
                                       var18[var29] = OrientationRequested.REVERSE_PORTRAIT;
                                       var23 = true;
                                    }
                                 }
                              }
                           }

                           if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                              return var18;
                           }

                           OrientationRequested[] var31;
                           if (var23 && var2 == null) {
                              var31 = new OrientationRequested[]{OrientationRequested.PORTRAIT, OrientationRequested.LANDSCAPE, OrientationRequested.REVERSE_LANDSCAPE, OrientationRequested.REVERSE_PORTRAIT};
                              return var31;
                           }

                           var31 = new OrientationRequested[]{OrientationRequested.PORTRAIT, OrientationRequested.LANDSCAPE, OrientationRequested.REVERSE_LANDSCAPE};
                           return var31;
                        }

                        if (var1 == PageRanges.class) {
                           if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                              return null;
                           }

                           PageRanges[] var20 = new PageRanges[]{new PageRanges(1, Integer.MAX_VALUE)};
                           return var20;
                        }

                        if (var1 == RequestingUserName.class) {
                           String var15 = "";

                           try {
                              var15 = System.getProperty("user.name", "");
                           } catch (SecurityException var12) {
                           }

                           return new RequestingUserName(var15, (Locale)null);
                        }

                        if (var1 == Sides.class) {
                           var13 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get("sides-supported") : null;
                           if (var13 != null) {
                              String[] var16 = var13.getArrayOfStringValues();
                              if (var16 != null && var16.length > 0) {
                                 Sides[] var21 = new Sides[var16.length];

                                 for(var7 = 0; var7 < var16.length; ++var7) {
                                    if (var16[var7].endsWith("long-edge")) {
                                       var21[var7] = Sides.TWO_SIDED_LONG_EDGE;
                                    } else if (var16[var7].endsWith("short-edge")) {
                                       var21[var7] = Sides.TWO_SIDED_SHORT_EDGE;
                                    } else {
                                       var21[var7] = Sides.ONE_SIDED;
                                    }
                                 }

                                 return var21;
                              }
                           }
                        }
                     }
                  }

                  return null;
               }
            } else if (var2 != null && (var2.equals(DocFlavor.INPUT_STREAM.POSTSCRIPT) || var2.equals(DocFlavor.URL.POSTSCRIPT) || var2.equals(DocFlavor.BYTE_ARRAY.POSTSCRIPT))) {
               return null;
            } else {
               CopiesSupported var4 = new CopiesSupported(1, MAXCOPIES);
               AttributeClass var5 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get(var4.getName()) : null;
               if (var5 != null) {
                  int[] var6 = var5.getIntRangeValue();
                  var4 = new CopiesSupported(var6[0], var6[1]);
               }

               return var4;
            }
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

   public synchronized DocFlavor[] getSupportedDocFlavors() {
      if (this.supportedDocFlavors != null) {
         int var12 = this.supportedDocFlavors.length;
         DocFlavor[] var13 = new DocFlavor[var12];
         System.arraycopy(this.supportedDocFlavors, 0, var13, 0, var12);
         return var13;
      } else {
         this.initAttributes();
         if (this.getAttMap != null && this.getAttMap.containsKey("document-format-supported")) {
            AttributeClass var1 = (AttributeClass)this.getAttMap.get("document-format-supported");
            if (var1 != null) {
               boolean var3 = false;
               String[] var4 = var1.getArrayOfStringValues();
               HashSet var6 = new HashSet();
               String var8 = DocFlavor.hostEncoding.toLowerCase(Locale.ENGLISH);
               boolean var9 = !var8.equals("utf-8") && !var8.equals("utf-16") && !var8.equals("utf-16be") && !var8.equals("utf-16le") && !var8.equals("us-ascii");

               int var10;
               for(var10 = 0; var10 < var4.length; ++var10) {
                  int var7;
                  for(var7 = 0; var7 < allDocFlavors.length; ++var7) {
                     DocFlavor[] var5 = (DocFlavor[])((DocFlavor[])allDocFlavors[var7]);
                     String var2 = var5[0].getMimeType();
                     if (var2.startsWith(var4[var10])) {
                        var6.addAll(Arrays.asList(var5));
                        if (var2.equals("text/plain") && var9) {
                           var6.add(Arrays.asList(textPlainHost));
                           break;
                        }

                        if (var2.equals("text/html") && var9) {
                           var6.add(Arrays.asList(textHtmlHost));
                           break;
                        }

                        if (var2.equals("image/png")) {
                           this.pngImagesAdded = true;
                        } else if (var2.equals("image/gif")) {
                           this.gifImagesAdded = true;
                        } else if (var2.equals("image/jpeg")) {
                           this.jpgImagesAdded = true;
                        } else if (var2.indexOf("postscript") != -1) {
                           var3 = true;
                        }
                        break;
                     }
                  }

                  if (var7 == allDocFlavors.length) {
                     var6.add(new DocFlavor.BYTE_ARRAY(var4[var10]));
                     var6.add(new DocFlavor.INPUT_STREAM(var4[var10]));
                     var6.add(new DocFlavor.URL(var4[var10]));
                  }
               }

               if (var3 || this.isCupsPrinter) {
                  var6.add(DocFlavor.SERVICE_FORMATTED.PAGEABLE);
                  var6.add(DocFlavor.SERVICE_FORMATTED.PRINTABLE);
                  var6.addAll(Arrays.asList(imageJPG));
                  var6.addAll(Arrays.asList(imagePNG));
                  var6.addAll(Arrays.asList(imageGIF));
               }

               this.supportedDocFlavors = new DocFlavor[var6.size()];
               var6.toArray(this.supportedDocFlavors);
               var10 = this.supportedDocFlavors.length;
               DocFlavor[] var11 = new DocFlavor[var10];
               System.arraycopy(this.supportedDocFlavors, 0, var11, 0, var10);
               return var11;
            }
         }

         return null;
      }
   }

   public boolean isDocFlavorSupported(DocFlavor var1) {
      if (this.supportedDocFlavors == null) {
         this.getSupportedDocFlavors();
      }

      if (this.supportedDocFlavors != null) {
         for(int var2 = 0; var2 < this.supportedDocFlavors.length; ++var2) {
            if (var1.equals(this.supportedDocFlavors[var2])) {
               return true;
            }
         }
      }

      return false;
   }

   public CustomMediaSizeName findCustomMedia(MediaSizeName var1) {
      if (this.customMediaSizeNames == null) {
         return null;
      } else {
         for(int var2 = 0; var2 < this.customMediaSizeNames.length; ++var2) {
            CustomMediaSizeName var3 = this.customMediaSizeNames[var2];
            MediaSizeName var4 = var3.getStandardMedia();
            if (var1.equals(var4)) {
               return this.customMediaSizeNames[var2];
            }
         }

         return null;
      }
   }

   private Media getIPPMedia(String var1) {
      CustomMediaSizeName var2 = new CustomMediaSizeName("sample", "", 0.0F, 0.0F);
      Media[] var3 = var2.getSuperEnumTable();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var1.equals("" + var3[var4])) {
            return var3[var4];
         }
      }

      CustomMediaTray var7 = new CustomMediaTray("sample", "");
      Media[] var5 = var7.getSuperEnumTable();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         if (var1.equals("" + var5[var6])) {
            return var5[var6];
         }
      }

      return null;
   }

   private Media[] getSupportedMedia() {
      if (this.getAttMap != null && this.getAttMap.containsKey("media-supported")) {
         AttributeClass var1 = (AttributeClass)this.getAttMap.get("media-supported");
         if (var1 != null) {
            String[] var2 = var1.getArrayOfStringValues();
            Media[] var4 = new Media[var2.length];

            for(int var5 = 0; var5 < var2.length; ++var5) {
               Media var3 = this.getIPPMedia(var2[var5]);
               var4[var5] = var3;
            }

            return var4;
         }
      }

      return new Media[0];
   }

   public synchronized Class[] getSupportedAttributeCategories() {
      if (this.supportedCats != null) {
         Class[] var5 = new Class[this.supportedCats.length];
         System.arraycopy(this.supportedCats, 0, var5, 0, var5.length);
         return var5;
      } else {
         this.initAttributes();
         ArrayList var1 = new ArrayList();

         for(int var3 = 0; var3 < printReqAttribDefault.length; ++var3) {
            PrintRequestAttribute var4 = (PrintRequestAttribute)printReqAttribDefault[var3];
            if (this.getAttMap != null && this.getAttMap.containsKey(var4.getName() + "-supported")) {
               Class var2 = var4.getCategory();
               var1.add(var2);
            }
         }

         if (this.isCupsPrinter) {
            if (!var1.contains(Media.class)) {
               var1.add(Media.class);
            }

            var1.add(MediaPrintableArea.class);
            var1.add(Destination.class);
            if (!UnixPrintServiceLookup.isLinux()) {
               var1.add(SheetCollate.class);
            }
         }

         if (this.getAttMap != null && this.getAttMap.containsKey("color-supported")) {
            var1.add(Chromaticity.class);
         }

         this.supportedCats = new Class[var1.size()];
         var1.toArray(this.supportedCats);
         Class[] var6 = new Class[this.supportedCats.length];
         System.arraycopy(this.supportedCats, 0, var6, 0, var6.length);
         return var6;
      }
   }

   public boolean isAttributeCategorySupported(Class<? extends Attribute> var1) {
      if (var1 == null) {
         throw new NullPointerException("null category");
      } else if (!Attribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException(var1 + " is not an Attribute");
      } else {
         if (this.supportedCats == null) {
            this.getSupportedAttributeCategories();
         }

         if (var1 == OrientationRequested.class) {
            return true;
         } else {
            for(int var2 = 0; var2 < this.supportedCats.length; ++var2) {
               if (var1 == this.supportedCats[var2]) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public synchronized <T extends PrintServiceAttribute> T getAttribute(Class<T> var1) {
      if (var1 == null) {
         throw new NullPointerException("category");
      } else if (!PrintServiceAttribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException("Not a PrintServiceAttribute");
      } else {
         this.initAttributes();
         if (var1 == PrinterName.class) {
            return new PrinterName(this.printer, (Locale)null);
         } else {
            AttributeClass var3;
            if (var1 == PrinterInfo.class) {
               PrinterInfo var6 = new PrinterInfo(this.printer, (Locale)null);
               var3 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get(var6.getName()) : null;
               return var3 != null ? new PrinterInfo(var3.getStringValue(), (Locale)null) : var6;
            } else if (var1 == QueuedJobCount.class) {
               QueuedJobCount var5 = new QueuedJobCount(0);
               var3 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get(var5.getName()) : null;
               if (var3 != null) {
                  var5 = new QueuedJobCount(var3.getIntValue());
               }

               return var5;
            } else if (var1 == PrinterIsAcceptingJobs.class) {
               PrinterIsAcceptingJobs var4 = PrinterIsAcceptingJobs.ACCEPTING_JOBS;
               var3 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get(var4.getName()) : null;
               if (var3 != null && var3.getByteValue() == 0) {
                  var4 = PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS;
               }

               return var4;
            } else if (var1 == ColorSupported.class) {
               ColorSupported var2 = ColorSupported.SUPPORTED;
               var3 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get(var2.getName()) : null;
               if (var3 != null && var3.getByteValue() == 0) {
                  var2 = ColorSupported.NOT_SUPPORTED;
               }

               return var2;
            } else if (var1 == PDLOverrideSupported.class) {
               return this.isCupsPrinter ? PDLOverrideSupported.NOT_ATTEMPTED : PDLOverrideSupported.NOT_ATTEMPTED;
            } else {
               return var1 == PrinterURI.class ? new PrinterURI(this.myURI) : null;
            }
         }
      }
   }

   public synchronized PrintServiceAttributeSet getAttributes() {
      this.init = false;
      this.initAttributes();
      HashPrintServiceAttributeSet var1 = new HashPrintServiceAttributeSet();

      for(int var2 = 0; var2 < serviceAttributes.length; ++var2) {
         String var3 = (String)serviceAttributes[var2][1];
         if (this.getAttMap != null && this.getAttMap.containsKey(var3)) {
            Class var4 = (Class)serviceAttributes[var2][0];
            PrintServiceAttribute var5 = this.getAttribute(var4);
            if (var5 != null) {
               var1.add(var5);
            }
         }
      }

      return AttributeSetUtilities.unmodifiableView((PrintServiceAttributeSet)var1);
   }

   public boolean isIPPSupportedImages(String var1) {
      if (this.supportedDocFlavors == null) {
         this.getSupportedDocFlavors();
      }

      if (var1.equals("image/png") && this.pngImagesAdded) {
         return true;
      } else if (var1.equals("image/gif") && this.gifImagesAdded) {
         return true;
      } else {
         return var1.equals("image/jpeg") && this.jpgImagesAdded;
      }
   }

   private boolean isSupportedCopies(Copies var1) {
      CopiesSupported var2 = (CopiesSupported)this.getSupportedAttributeValues(Copies.class, (DocFlavor)null, (AttributeSet)null);
      int[][] var3 = var2.getMembers();
      int var4;
      int var5;
      if (var3.length > 0 && var3[0].length > 0) {
         var4 = var3[0][0];
         var5 = var3[0][1];
      } else {
         var4 = 1;
         var5 = MAXCOPIES;
      }

      int var6 = var1.getValue();
      return var6 >= var4 && var6 <= var5;
   }

   private boolean isAutoSense(DocFlavor var1) {
      return var1.equals(DocFlavor.BYTE_ARRAY.AUTOSENSE) || var1.equals(DocFlavor.INPUT_STREAM.AUTOSENSE) || var1.equals(DocFlavor.URL.AUTOSENSE);
   }

   private synchronized boolean isSupportedMediaTray(MediaTray var1) {
      this.initAttributes();
      if (this.mediaTrays != null) {
         for(int var2 = 0; var2 < this.mediaTrays.length; ++var2) {
            if (var1.equals(this.mediaTrays[var2])) {
               return true;
            }
         }
      }

      return false;
   }

   private synchronized boolean isSupportedMedia(MediaSizeName var1) {
      this.initAttributes();
      if (var1.equals((Media)this.getDefaultAttributeValue(Media.class))) {
         return true;
      } else {
         for(int var2 = 0; var2 < this.mediaSizeNames.length; ++var2) {
            debug_println("IPPPrintService>> isSupportedMedia, mediaSizeNames[i] " + this.mediaSizeNames[var2]);
            if (var1.equals(this.mediaSizeNames[var2])) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean isDestinationSupported(DocFlavor var1, AttributeSet var2) {
      return var2 == null || var2.get(Destination.class) == null || var1 == null || var1.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || var1.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE);
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
         } else if (!this.isDestinationSupported(var2, var3)) {
            return false;
         } else if (var1.getCategory() == Chromaticity.class) {
            if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE) && this.isIPPSupportedImages(var2.getMimeType())) {
               return false;
            } else {
               return var1 == Chromaticity.COLOR;
            }
         } else if (var1.getCategory() == Copies.class) {
            return (var2 == null || !var2.equals(DocFlavor.INPUT_STREAM.POSTSCRIPT) && !var2.equals(DocFlavor.URL.POSTSCRIPT) && !var2.equals(DocFlavor.BYTE_ARRAY.POSTSCRIPT)) && this.isSupportedCopies((Copies)var1);
         } else if (var1.getCategory() == Destination.class) {
            if (var2 == null || var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
               URI var8 = ((Destination)var1).getURI();
               if ("file".equals(var8.getScheme()) && !var8.getSchemeSpecificPart().equals("")) {
                  return true;
               }
            }

            return false;
         } else {
            if (var1.getCategory() == Media.class) {
               if (var1 instanceof MediaSizeName) {
                  return this.isSupportedMedia((MediaSizeName)var1);
               }

               if (var1 instanceof MediaTray) {
                  return this.isSupportedMediaTray((MediaTray)var1);
               }
            } else if (var1.getCategory() == PageRanges.class) {
               if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                  return false;
               }
            } else if (var1.getCategory() == SheetCollate.class) {
               if (var2 != null && !var2.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !var2.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
                  return false;
               }
            } else {
               int var6;
               if (var1.getCategory() == Sides.class) {
                  Sides[] var7 = (Sides[])((Sides[])this.getSupportedAttributeValues(Sides.class, var2, var3));
                  if (var7 != null) {
                     for(var6 = 0; var6 < var7.length; ++var6) {
                        if (var7[var6] == (Sides)var1) {
                           return true;
                        }
                     }
                  }

                  return false;
               }

               if (var1.getCategory() == OrientationRequested.class) {
                  OrientationRequested[] var5 = (OrientationRequested[])((OrientationRequested[])this.getSupportedAttributeValues(OrientationRequested.class, var2, var3));
                  if (var5 != null) {
                     for(var6 = 0; var6 < var5.length; ++var6) {
                        if (var5[var6] == (OrientationRequested)var1) {
                           return true;
                        }
                     }
                  }

                  return false;
               }
            }

            return true;
         }
      }
   }

   public synchronized Object getDefaultAttributeValue(Class<? extends Attribute> var1) {
      if (var1 == null) {
         throw new NullPointerException("null category");
      } else if (!Attribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException(var1 + " is not an Attribute");
      } else if (!this.isAttributeCategorySupported(var1)) {
         return null;
      } else {
         this.initAttributes();
         String var2 = null;

         for(int var3 = 0; var3 < printReqAttribDefault.length; ++var3) {
            PrintRequestAttribute var4 = (PrintRequestAttribute)printReqAttribDefault[var3];
            if (var4.getCategory() == var1) {
               var2 = var4.getName();
               break;
            }
         }

         String var12 = var2 + "-default";
         AttributeClass var13 = this.getAttMap != null ? (AttributeClass)this.getAttMap.get(var12) : null;
         if (var1 == Copies.class) {
            return var13 != null ? new Copies(var13.getIntValue()) : new Copies(1);
         } else if (var1 == Chromaticity.class) {
            return Chromaticity.COLOR;
         } else if (var1 == Destination.class) {
            try {
               return new Destination((new File("out.ps")).toURI());
            } catch (SecurityException var10) {
               try {
                  return new Destination(new URI("file:out.ps"));
               } catch (URISyntaxException var9) {
                  return null;
               }
            }
         } else if (var1 == Fidelity.class) {
            return Fidelity.FIDELITY_FALSE;
         } else if (var1 == Finishings.class) {
            return Finishings.NONE;
         } else if (var1 == JobName.class) {
            return new JobName("Java Printing", (Locale)null);
         } else if (var1 == JobSheets.class) {
            return var13 != null && var13.getStringValue().equals("none") ? JobSheets.NONE : JobSheets.STANDARD;
         } else {
            String var5;
            if (var1 == Media.class) {
               if (this.defaultMediaIndex == -1) {
                  this.defaultMediaIndex = 0;
               }

               if (this.mediaSizeNames.length == 0) {
                  var5 = Locale.getDefault().getCountry();
                  return var5 != null && (var5.equals("") || var5.equals(Locale.US.getCountry()) || var5.equals(Locale.CANADA.getCountry())) ? MediaSizeName.NA_LETTER : MediaSizeName.ISO_A4;
               } else {
                  if (var13 != null) {
                     var5 = var13.getStringValue();
                     if (this.isCupsPrinter) {
                        return this.mediaSizeNames[this.defaultMediaIndex];
                     }

                     for(int var16 = 0; var16 < this.mediaSizeNames.length; ++var16) {
                        if (this.mediaSizeNames[var16].toString().indexOf(var5) != -1) {
                           this.defaultMediaIndex = var16;
                           return this.mediaSizeNames[this.defaultMediaIndex];
                        }
                     }
                  }

                  return this.mediaSizeNames[this.defaultMediaIndex];
               }
            } else if (var1 == MediaPrintableArea.class) {
               MediaPrintableArea[] var15;
               if (this.cps != null && (var15 = this.cps.getMediaPrintableArea()) != null) {
                  if (this.defaultMediaIndex == -1) {
                     this.getDefaultAttributeValue(Media.class);
                  }

                  return var15[this.defaultMediaIndex];
               } else {
                  String var6 = Locale.getDefault().getCountry();
                  float var7;
                  float var8;
                  if (var6 == null || !var6.equals("") && !var6.equals(Locale.US.getCountry()) && !var6.equals(Locale.CANADA.getCountry())) {
                     var7 = MediaSize.ISO.A4.getX(25400) - 0.5F;
                     var8 = MediaSize.ISO.A4.getY(25400) - 0.5F;
                  } else {
                     var7 = MediaSize.NA.LETTER.getX(25400) - 0.5F;
                     var8 = MediaSize.NA.LETTER.getY(25400) - 0.5F;
                  }

                  return new MediaPrintableArea(0.25F, 0.25F, var7, var8, 25400);
               }
            } else if (var1 == NumberUp.class) {
               return new NumberUp(1);
            } else if (var1 == OrientationRequested.class) {
               if (var13 != null) {
                  switch(var13.getIntValue()) {
                  case 3:
                  default:
                     return OrientationRequested.PORTRAIT;
                  case 4:
                     return OrientationRequested.LANDSCAPE;
                  case 5:
                     return OrientationRequested.REVERSE_LANDSCAPE;
                  case 6:
                     return OrientationRequested.REVERSE_PORTRAIT;
                  }
               } else {
                  return OrientationRequested.PORTRAIT;
               }
            } else if (var1 == PageRanges.class) {
               if (var13 != null) {
                  int[] var14 = var13.getIntRangeValue();
                  return new PageRanges(var14[0], var14[1]);
               } else {
                  return new PageRanges(1, Integer.MAX_VALUE);
               }
            } else if (var1 == RequestingUserName.class) {
               var5 = "";

               try {
                  var5 = System.getProperty("user.name", "");
               } catch (SecurityException var11) {
               }

               return new RequestingUserName(var5, (Locale)null);
            } else if (var1 == SheetCollate.class) {
               return SheetCollate.UNCOLLATED;
            } else if (var1 == Sides.class) {
               if (var13 != null) {
                  if (var13.getStringValue().endsWith("long-edge")) {
                     return Sides.TWO_SIDED_LONG_EDGE;
                  }

                  if (var13.getStringValue().endsWith("short-edge")) {
                     return Sides.TWO_SIDED_SHORT_EDGE;
                  }
               }

               return Sides.ONE_SIDED;
            } else {
               return null;
            }
         }
      }
   }

   public ServiceUIFactory getServiceUIFactory() {
      return null;
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

   String getDest() {
      return this.printer;
   }

   public String getName() {
      if (UnixPrintServiceLookup.isMac()) {
         PrintServiceAttributeSet var1 = this.getAttributes();
         if (var1 != null) {
            PrinterInfo var2 = (PrinterInfo)var1.get(PrinterInfo.class);
            if (var2 != null) {
               return var2.toString();
            }
         }
      }

      return this.printer;
   }

   public boolean usesClass(Class var1) {
      return var1 == PSPrinterJob.class;
   }

   public static HttpURLConnection getIPPConnection(URL var0) {
      URLConnection var2;
      try {
         var2 = var0.openConnection();
      } catch (IOException var4) {
         return null;
      }

      if (!(var2 instanceof HttpURLConnection)) {
         return null;
      } else {
         HttpURLConnection var1 = (HttpURLConnection)var2;
         var1.setUseCaches(false);
         var1.setDefaultUseCaches(false);
         var1.setDoInput(true);
         var1.setDoOutput(true);
         var1.setRequestProperty("Content-type", "application/ipp");
         return var1;
      }
   }

   public synchronized boolean isPostscript() {
      if (this.isPS == null) {
         this.isPS = Boolean.TRUE;
         if (this.isCupsPrinter) {
            try {
               this.urlConnection = getIPPConnection(new URL(this.myURL + ".ppd"));
               InputStream var1 = this.urlConnection.getInputStream();
               if (var1 != null) {
                  BufferedReader var2 = new BufferedReader(new InputStreamReader(var1, Charset.forName("ISO-8859-1")));

                  String var3;
                  while((var3 = var2.readLine()) != null) {
                     if (var3.startsWith("*cupsFilter:")) {
                        this.isPS = Boolean.FALSE;
                        break;
                     }
                  }
               }
            } catch (IOException var4) {
               debug_println(" isPostscript, e= " + var4);
            }
         }
      }

      return this.isPS;
   }

   private void opGetAttributes() {
      try {
         debug_println("IPPPrintService>> opGetAttributes myURI " + this.myURI + " myURL " + this.myURL);
         AttributeClass[] var1 = new AttributeClass[]{AttributeClass.ATTRIBUTES_CHARSET, AttributeClass.ATTRIBUTES_NATURAL_LANGUAGE};
         AttributeClass[] var2 = new AttributeClass[]{AttributeClass.ATTRIBUTES_CHARSET, AttributeClass.ATTRIBUTES_NATURAL_LANGUAGE, new AttributeClass("printer-uri", 69, "" + this.myURI)};
         OutputStream var3 = (OutputStream)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               try {
                  return IPPPrintService.this.urlConnection.getOutputStream();
               } catch (Exception var2) {
                  return null;
               }
            }
         });
         if (var3 == null) {
            return;
         }

         boolean var4 = this.myURI == null ? writeIPPRequest(var3, "000B", var1) : writeIPPRequest(var3, "000B", var2);
         if (var4) {
            InputStream var5 = null;
            if ((var5 = this.urlConnection.getInputStream()) != null) {
               HashMap[] var6 = readIPPResponse(var5);
               if (var6 != null && var6.length > 0) {
                  this.getAttMap = var6[0];
                  if (var6.length > 1) {
                     for(int var7 = 1; var7 < var6.length; ++var7) {
                        Set var8 = var6[var7].entrySet();
                        Iterator var9 = var8.iterator();

                        while(var9.hasNext()) {
                           Map.Entry var10 = (Map.Entry)var9.next();
                           if (!this.getAttMap.containsKey(var10.getValue())) {
                              this.getAttMap.put(var10.getKey(), var10.getValue());
                           }
                        }
                     }
                  }
               }
            } else {
               debug_println("IPPPrintService>> opGetAttributes - null input stream");
            }

            var5.close();
         }

         var3.close();
      } catch (IOException var11) {
         debug_println("IPPPrintService>> opGetAttributes - input/output stream: " + var11);
      }

   }

   public static boolean writeIPPRequest(OutputStream var0, String var1, AttributeClass[] var2) {
      OutputStreamWriter var3;
      try {
         var3 = new OutputStreamWriter(var0, "UTF-8");
      } catch (UnsupportedEncodingException var11) {
         debug_println("IPPPrintService>> writeIPPRequest, UTF-8 not supported? Exception: " + var11);
         return false;
      }

      debug_println("IPPPrintService>> writeIPPRequest, op code= " + var1);
      char[] var4 = new char[]{(char)Byte.parseByte(var1.substring(0, 2), 16), (char)Byte.parseByte(var1.substring(2, 4), 16)};
      char[] var5 = new char[]{'\u0001', '\u0001', '\u0000', '\u0001'};

      try {
         var3.write((char[])var5, 0, 2);
         var3.write((char[])var4, 0, 2);
         var5[0] = 0;
         var5[1] = 0;
         var3.write((char[])var5, 0, 4);
         var5[0] = 1;
         var3.write(var5[0]);

         for(int var9 = 0; var9 < var2.length; ++var9) {
            AttributeClass var8 = var2[var9];
            var3.write(var8.getType());
            char[] var7 = var8.getLenChars();
            var3.write((char[])var7, 0, 2);
            var3.write((String)("" + var8), 0, var8.getName().length());
            if (var8.getType() >= 53 && var8.getType() <= 73) {
               String var6 = (String)var8.getObjectValue();
               var5[0] = 0;
               var5[1] = (char)var6.length();
               var3.write((char[])var5, 0, 2);
               var3.write((String)var6, 0, var6.length());
            }
         }

         var3.write(3);
         var3.flush();
         var3.close();
         return true;
      } catch (IOException var10) {
         debug_println("IPPPrintService>> writeIPPRequest, IPPPrintService Exception in writeIPPRequest: " + var10);
         return false;
      }
   }

   public static HashMap[] readIPPResponse(InputStream var0) {
      if (var0 == null) {
         return null;
      } else {
         byte[] var1 = new byte[MAX_ATTRIBUTE_LENGTH];

         try {
            DataInputStream var2 = new DataInputStream(var0);
            if (var2.read(var1, 0, 8) > -1 && var1[2] == 0) {
               boolean var4 = false;
               boolean var5 = false;
               String var6 = null;
               byte var7 = 68;
               ArrayList var8 = new ArrayList();
               HashMap var9 = new HashMap();
               var1[0] = var2.readByte();

               while(var1[0] >= 1 && var1[0] <= 4 && var1[0] != 3) {
                  debug_println("IPPPrintService>> readIPPResponse, checking group tag,  response[0]= " + var1[0]);
                  ByteArrayOutputStream var3 = new ByteArrayOutputStream();
                  int var13 = 0;
                  var6 = null;

                  byte[] var10;
                  AttributeClass var11;
                  for(var1[0] = var2.readByte(); var1[0] >= 16 && var1[0] <= 74; var1[0] = var2.readByte()) {
                     short var14 = var2.readShort();
                     if (var14 != 0 && var6 != null) {
                        var3.write(var13);
                        var3.flush();
                        var3.close();
                        var10 = var3.toByteArray();
                        if (var9.containsKey(var6)) {
                           var8.add(var9);
                           var9 = new HashMap();
                        }

                        if (var7 >= 33) {
                           var11 = new AttributeClass(var6, var7, var10);
                           var9.put(var11.getName(), var11);
                           debug_println("IPPPrintService>> readIPPResponse " + var11);
                        }

                        var3 = new ByteArrayOutputStream();
                        var13 = 0;
                     }

                     if (var13 == 0) {
                        var7 = var1[0];
                     }

                     if (var14 != 0) {
                        if (var14 > MAX_ATTRIBUTE_LENGTH) {
                           var1 = new byte[var14];
                        }

                        var2.read(var1, 0, var14);
                        var6 = new String(var1, 0, var14);
                     }

                     var14 = var2.readShort();
                     var3.write(var14);
                     if (var14 > MAX_ATTRIBUTE_LENGTH) {
                        var1 = new byte[var14];
                     }

                     var2.read(var1, 0, var14);
                     var3.write(var1, 0, var14);
                     ++var13;
                  }

                  if (var6 != null) {
                     var3.write(var13);
                     var3.flush();
                     var3.close();
                     if (var13 != 0 && var9.containsKey(var6)) {
                        var8.add(var9);
                        var9 = new HashMap();
                     }

                     var10 = var3.toByteArray();
                     var11 = new AttributeClass(var6, var7, var10);
                     var9.put(var11.getName(), var11);
                  }
               }

               var2.close();
               if (var9 != null && var9.size() > 0) {
                  var8.add(var9);
               }

               return (HashMap[])((HashMap[])var8.toArray(new HashMap[var8.size()]));
            } else {
               debug_println("IPPPrintService>> readIPPResponse client error, IPP status code: 0x" + toHex(var1[2]) + toHex(var1[3]));
               return null;
            }
         } catch (IOException var12) {
            debug_println("IPPPrintService>> readIPPResponse: " + var12);
            if (debugPrint) {
               var12.printStackTrace();
            }

            return null;
         }
      }
   }

   private static String toHex(byte var0) {
      String var1 = Integer.toHexString(var0 & 255);
      return var1.length() == 2 ? var1 : "0" + var1;
   }

   public String toString() {
      return "IPP Printer : " + this.getName();
   }

   public boolean equals(Object var1) {
      return var1 == this || var1 instanceof IPPPrintService && ((IPPPrintService)var1).getName().equals(this.getName());
   }

   public int hashCode() {
      return this.getClass().hashCode() + this.getName().hashCode();
   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.print.ippdebug")));
      debugPrint = "true".equalsIgnoreCase(var0);
      MAXCOPIES = 1000;
      MAX_ATTRIBUTE_LENGTH = 255;
      printReqAttribDefault = new Object[]{Chromaticity.COLOR, new Copies(1), Fidelity.FIDELITY_FALSE, Finishings.NONE, new JobName("", Locale.getDefault()), JobSheets.NONE, MediaSizeName.NA_LETTER, new NumberUp(1), OrientationRequested.PORTRAIT, new PageRanges(1), new RequestingUserName("", Locale.getDefault()), Sides.ONE_SIDED};
      serviceAttributes = new Object[][]{{ColorSupported.class, "color-supported"}, {PagesPerMinute.class, "pages-per-minute"}, {PagesPerMinuteColor.class, "pages-per-minute-color"}, {PDLOverrideSupported.class, "pdl-override-supported"}, {PrinterInfo.class, "printer-info"}, {PrinterIsAcceptingJobs.class, "printer-is-accepting-jobs"}, {PrinterLocation.class, "printer-location"}, {PrinterMakeAndModel.class, "printer-make-and-model"}, {PrinterMessageFromOperator.class, "printer-message-from-operator"}, {PrinterMoreInfo.class, "printer-more-info"}, {PrinterMoreInfoManufacturer.class, "printer-more-info-manufacturer"}, {PrinterName.class, "printer-name"}, {PrinterState.class, "printer-state"}, {PrinterStateReasons.class, "printer-state-reasons"}, {PrinterURI.class, "printer-uri"}, {QueuedJobCount.class, "queued-job-count"}};
      appPDF = new DocFlavor[]{DocFlavor.BYTE_ARRAY.PDF, DocFlavor.INPUT_STREAM.PDF, DocFlavor.URL.PDF};
      appPostScript = new DocFlavor[]{DocFlavor.BYTE_ARRAY.POSTSCRIPT, DocFlavor.INPUT_STREAM.POSTSCRIPT, DocFlavor.URL.POSTSCRIPT};
      appOctetStream = new DocFlavor[]{DocFlavor.BYTE_ARRAY.AUTOSENSE, DocFlavor.INPUT_STREAM.AUTOSENSE, DocFlavor.URL.AUTOSENSE};
      textPlain = new DocFlavor[]{DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_8, DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_16, DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_16BE, DocFlavor.BYTE_ARRAY.TEXT_PLAIN_UTF_16LE, DocFlavor.BYTE_ARRAY.TEXT_PLAIN_US_ASCII, DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_8, DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_16, DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_16BE, DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_16LE, DocFlavor.INPUT_STREAM.TEXT_PLAIN_US_ASCII, DocFlavor.URL.TEXT_PLAIN_UTF_8, DocFlavor.URL.TEXT_PLAIN_UTF_16, DocFlavor.URL.TEXT_PLAIN_UTF_16BE, DocFlavor.URL.TEXT_PLAIN_UTF_16LE, DocFlavor.URL.TEXT_PLAIN_US_ASCII, DocFlavor.CHAR_ARRAY.TEXT_PLAIN, DocFlavor.STRING.TEXT_PLAIN, DocFlavor.READER.TEXT_PLAIN};
      textPlainHost = new DocFlavor[]{DocFlavor.BYTE_ARRAY.TEXT_PLAIN_HOST, DocFlavor.INPUT_STREAM.TEXT_PLAIN_HOST, DocFlavor.URL.TEXT_PLAIN_HOST};
      imageJPG = new DocFlavor[]{DocFlavor.BYTE_ARRAY.JPEG, DocFlavor.INPUT_STREAM.JPEG, DocFlavor.URL.JPEG};
      imageGIF = new DocFlavor[]{DocFlavor.BYTE_ARRAY.GIF, DocFlavor.INPUT_STREAM.GIF, DocFlavor.URL.GIF};
      imagePNG = new DocFlavor[]{DocFlavor.BYTE_ARRAY.PNG, DocFlavor.INPUT_STREAM.PNG, DocFlavor.URL.PNG};
      textHtml = new DocFlavor[]{DocFlavor.BYTE_ARRAY.TEXT_HTML_UTF_8, DocFlavor.BYTE_ARRAY.TEXT_HTML_UTF_16, DocFlavor.BYTE_ARRAY.TEXT_HTML_UTF_16BE, DocFlavor.BYTE_ARRAY.TEXT_HTML_UTF_16LE, DocFlavor.BYTE_ARRAY.TEXT_HTML_US_ASCII, DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_8, DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_16, DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_16BE, DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_16LE, DocFlavor.INPUT_STREAM.TEXT_HTML_US_ASCII, DocFlavor.URL.TEXT_HTML_UTF_8, DocFlavor.URL.TEXT_HTML_UTF_16, DocFlavor.URL.TEXT_HTML_UTF_16BE, DocFlavor.URL.TEXT_HTML_UTF_16LE, DocFlavor.URL.TEXT_HTML_US_ASCII};
      textHtmlHost = new DocFlavor[]{DocFlavor.BYTE_ARRAY.TEXT_HTML_HOST, DocFlavor.INPUT_STREAM.TEXT_HTML_HOST, DocFlavor.URL.TEXT_HTML_HOST};
      appPCL = new DocFlavor[]{DocFlavor.BYTE_ARRAY.PCL, DocFlavor.INPUT_STREAM.PCL, DocFlavor.URL.PCL};
      allDocFlavors = new Object[]{appPDF, appPostScript, appOctetStream, textPlain, imageJPG, imageGIF, imagePNG, textHtml, appPCL};
   }

   private class ExtFinishing extends Finishings {
      ExtFinishing(int var2) {
         super(100);
      }

      EnumSyntax[] getAll() {
         EnumSyntax[] var1 = super.getEnumValueTable();
         return var1;
      }
   }
}
