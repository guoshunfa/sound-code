package sun.print;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;

public class CUPSPrinter {
   private static final String debugPrefix = "CUPSPrinter>> ";
   private static final double PRINTER_DPI = 72.0D;
   private boolean initialized;
   private MediaPrintableArea[] cupsMediaPrintables;
   private MediaSizeName[] cupsMediaSNames;
   private CustomMediaSizeName[] cupsCustomMediaSNames;
   private MediaTray[] cupsMediaTrays;
   public int nPageSizes = 0;
   public int nTrays = 0;
   private String[] media;
   private float[] pageSizes;
   private String printer;
   private static boolean libFound;
   private static String cupsServer = null;
   private static int cupsPort = 0;

   private static native String getCupsServer();

   private static native int getCupsPort();

   private static native String getCupsDefaultPrinter();

   private static native boolean canConnect(String var0, int var1);

   private static native boolean initIDs();

   private static synchronized native String[] getMedia(String var0);

   private static synchronized native float[] getPageSizes(String var0);

   CUPSPrinter(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null printer name");
      } else {
         this.printer = var1;
         this.cupsMediaSNames = null;
         this.cupsMediaPrintables = null;
         this.cupsMediaTrays = null;
         this.initialized = false;
         if (!libFound) {
            throw new RuntimeException("cups lib not found");
         } else {
            this.media = getMedia(this.printer);
            if (this.media == null) {
               throw new RuntimeException("error getting PPD");
            } else {
               this.pageSizes = getPageSizes(this.printer);
               if (this.pageSizes != null) {
                  this.nPageSizes = this.pageSizes.length / 6;
                  this.nTrays = this.media.length / 2 - this.nPageSizes;

                  assert this.nTrays >= 0;
               }

            }
         }
      }
   }

   MediaSizeName[] getMediaSizeNames() {
      this.initMedia();
      return this.cupsMediaSNames;
   }

   CustomMediaSizeName[] getCustomMediaSizeNames() {
      this.initMedia();
      return this.cupsCustomMediaSNames;
   }

   public int getDefaultMediaIndex() {
      return this.pageSizes.length > 1 ? (int)this.pageSizes[this.pageSizes.length - 1] : 0;
   }

   MediaPrintableArea[] getMediaPrintableArea() {
      this.initMedia();
      return this.cupsMediaPrintables;
   }

   MediaTray[] getMediaTrays() {
      this.initMedia();
      return this.cupsMediaTrays;
   }

   private synchronized void initMedia() {
      if (!this.initialized) {
         this.initialized = true;
         if (this.pageSizes != null) {
            this.cupsMediaPrintables = new MediaPrintableArea[this.nPageSizes];
            this.cupsMediaSNames = new MediaSizeName[this.nPageSizes];
            this.cupsCustomMediaSNames = new CustomMediaSizeName[this.nPageSizes];

            for(int var9 = 0; var9 < this.nPageSizes; ++var9) {
               float var4 = (float)((double)this.pageSizes[var9 * 6] / 72.0D);
               float var3 = (float)((double)this.pageSizes[var9 * 6 + 1] / 72.0D);
               float var5 = (float)((double)this.pageSizes[var9 * 6 + 2] / 72.0D);
               float var8 = (float)((double)this.pageSizes[var9 * 6 + 3] / 72.0D);
               float var7 = (float)((double)this.pageSizes[var9 * 6 + 4] / 72.0D);
               float var6 = (float)((double)this.pageSizes[var9 * 6 + 5] / 72.0D);
               CustomMediaSizeName var1 = new CustomMediaSizeName(this.media[var9 * 2], this.media[var9 * 2 + 1], var4, var3);
               if ((this.cupsMediaSNames[var9] = var1.getStandardMedia()) == null) {
                  this.cupsMediaSNames[var9] = var1;
                  if ((double)var4 > 0.0D && (double)var3 > 0.0D) {
                     try {
                        new MediaSize(var4, var3, 25400, var1);
                     } catch (IllegalArgumentException var11) {
                        new MediaSize(var3, var4, 25400, var1);
                     }
                  }
               }

               this.cupsCustomMediaSNames[var9] = var1;
               MediaPrintableArea var2 = null;

               try {
                  var2 = new MediaPrintableArea(var5, var6, var7, var8, 25400);
               } catch (IllegalArgumentException var12) {
                  if (var4 > 0.0F && var3 > 0.0F) {
                     var2 = new MediaPrintableArea(0.0F, 0.0F, var4, var3, 25400);
                  }
               }

               this.cupsMediaPrintables[var9] = var2;
            }

            this.cupsMediaTrays = new MediaTray[this.nTrays];

            for(int var10 = 0; var10 < this.nTrays; ++var10) {
               CustomMediaTray var13 = new CustomMediaTray(this.media[(this.nPageSizes + var10) * 2], this.media[(this.nPageSizes + var10) * 2 + 1]);
               this.cupsMediaTrays[var10] = var13;
            }

         }
      }
   }

   static String[] getDefaultPrinter() {
      String[] var0 = new String[]{getCupsDefaultPrinter(), null};
      if (var0[0] != null) {
         var0[1] = null;
         return (String[])var0.clone();
      } else {
         try {
            URL var1 = new URL("http", getServer(), getPort(), "");
            final HttpURLConnection var2 = IPPPrintService.getIPPConnection(var1);
            if (var2 != null) {
               OutputStream var3 = (OutputStream)AccessController.doPrivileged(new PrivilegedAction() {
                  public Object run() {
                     try {
                        return var2.getOutputStream();
                     } catch (Exception var2x) {
                        IPPPrintService.debug_println("CUPSPrinter>> " + var2x);
                        return null;
                     }
                  }
               });
               if (var3 == null) {
                  return null;
               }

               AttributeClass[] var4 = new AttributeClass[]{AttributeClass.ATTRIBUTES_CHARSET, AttributeClass.ATTRIBUTES_NATURAL_LANGUAGE, new AttributeClass("requested-attributes", 69, "printer-uri")};
               if (IPPPrintService.writeIPPRequest(var3, "4001", var4)) {
                  HashMap var5 = null;
                  InputStream var6 = var2.getInputStream();
                  HashMap[] var7 = IPPPrintService.readIPPResponse(var6);
                  var6.close();
                  if (var7 != null && var7.length > 0) {
                     var5 = var7[0];
                  } else {
                     IPPPrintService.debug_println("CUPSPrinter>>  empty response map for GET_DEFAULT.");
                  }

                  if (var5 == null) {
                     var3.close();
                     var2.disconnect();
                     if (UnixPrintServiceLookup.isMac()) {
                        var0[0] = UnixPrintServiceLookup.getDefaultPrinterNameSysV();
                        var0[1] = null;
                        return (String[])((String[])var0.clone());
                     }

                     return null;
                  }

                  AttributeClass var8 = (AttributeClass)var5.get("printer-name");
                  if (var8 != null) {
                     var0[0] = var8.getStringValue();
                     var8 = (AttributeClass)var5.get("printer-uri-supported");
                     IPPPrintService.debug_println("CUPSPrinter>> printer-uri-supported=" + var8);
                     if (var8 != null) {
                        var0[1] = var8.getStringValue();
                     } else {
                        var0[1] = null;
                     }

                     var3.close();
                     var2.disconnect();
                     return (String[])((String[])var0.clone());
                  }
               }

               var3.close();
               var2.disconnect();
            }
         } catch (Exception var9) {
         }

         return null;
      }
   }

   static String[] getAllPrinters() {
      try {
         URL var0 = new URL("http", getServer(), getPort(), "");
         final HttpURLConnection var1 = IPPPrintService.getIPPConnection(var0);
         if (var1 != null) {
            OutputStream var2 = (OutputStream)AccessController.doPrivileged(new PrivilegedAction() {
               public Object run() {
                  try {
                     return var1.getOutputStream();
                  } catch (Exception var2) {
                     return null;
                  }
               }
            });
            if (var2 == null) {
               return null;
            }

            AttributeClass[] var3 = new AttributeClass[]{AttributeClass.ATTRIBUTES_CHARSET, AttributeClass.ATTRIBUTES_NATURAL_LANGUAGE, new AttributeClass("requested-attributes", 68, "printer-uri-supported")};
            if (IPPPrintService.writeIPPRequest(var2, "4002", var3)) {
               InputStream var4 = var1.getInputStream();
               HashMap[] var5 = IPPPrintService.readIPPResponse(var4);
               var4.close();
               var2.close();
               var1.disconnect();
               if (var5 != null && var5.length != 0) {
                  ArrayList var6 = new ArrayList();

                  for(int var7 = 0; var7 < var5.length; ++var7) {
                     AttributeClass var8 = (AttributeClass)var5[var7].get("printer-uri-supported");
                     if (var8 != null) {
                        String var9 = var8.getStringValue();
                        var6.add(var9);
                     }
                  }

                  return (String[])((String[])var6.toArray(new String[0]));
               }

               return null;
            }

            var2.close();
            var1.disconnect();
         }
      } catch (Exception var10) {
      }

      return null;
   }

   public static String getServer() {
      return cupsServer;
   }

   public static int getPort() {
      return cupsPort;
   }

   public static boolean isCupsRunning() {
      IPPPrintService.debug_println("CUPSPrinter>> libFound " + libFound);
      if (libFound) {
         IPPPrintService.debug_println("CUPSPrinter>> CUPS server " + getServer() + " port " + getPort());
         return canConnect(getServer(), getPort());
      } else {
         return false;
      }
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("awt");
            return null;
         }
      });
      libFound = initIDs();
      if (libFound) {
         cupsServer = getCupsServer();
         cupsPort = getCupsPort();
      }

   }
}
