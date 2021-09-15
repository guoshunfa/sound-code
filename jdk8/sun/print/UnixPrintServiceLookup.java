package sun.print;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Vector;
import javax.print.DocFlavor;
import javax.print.MultiDocPrintService;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.PrinterURI;
import sun.security.action.GetPropertyAction;

public class UnixPrintServiceLookup extends PrintServiceLookup implements BackgroundServiceLookup, Runnable {
   private String defaultPrinter;
   private PrintService defaultPrintService;
   private PrintService[] printServices;
   private Vector lookupListeners = null;
   private static String debugPrefix = "UnixPrintServiceLookup>> ";
   private static boolean pollServices = true;
   private static final int DEFAULT_MINREFRESH = 120;
   private static int minRefreshTime = 120;
   static String osname;
   String[] lpNameComAix = new String[]{"/usr/bin/lsallq", "/usr/bin/lpstat -W -p|/usr/bin/expand|/usr/bin/cut -f1 -d' '", "/usr/bin/lpstat -W -d|/usr/bin/expand|/usr/bin/cut -f1 -d' '", "/usr/bin/lpstat -W -v"};
   private static final int aix_lsallq = 0;
   private static final int aix_lpstat_p = 1;
   private static final int aix_lpstat_d = 2;
   private static final int aix_lpstat_v = 3;
   private static int aix_defaultPrinterEnumeration = 0;
   static final int UNINITIALIZED = -1;
   static final int BSD_LPD = 0;
   static final int BSD_LPD_NG = 1;
   static int cmdIndex;
   String[] lpcFirstCom = new String[]{"/usr/sbin/lpc status | grep : | sed -ne '1,1 s/://p'", "/usr/sbin/lpc status | grep -E '^[ 0-9a-zA-Z_-]*@' | awk -F'@' '{print $1}'"};
   String[] lpcAllCom = new String[]{"/usr/sbin/lpc status all | grep : | sed -e 's/://'", "/usr/sbin/lpc status all | grep -E '^[ 0-9a-zA-Z_-]*@' | awk -F'@' '{print $1}' | sort"};
   String[] lpcNameCom = new String[]{"| grep : | sed -ne 's/://p'", "| grep -E '^[ 0-9a-zA-Z_-]*@' | awk -F'@' '{print $1}'"};

   static boolean isMac() {
      return osname.startsWith("Mac");
   }

   static boolean isSysV() {
      return osname.equals("SunOS");
   }

   static boolean isLinux() {
      return osname.equals("Linux");
   }

   static boolean isBSD() {
      return osname.equals("Linux") || osname.contains("OS X");
   }

   static boolean isAIX() {
      return osname.equals("AIX");
   }

   static int getBSDCommandIndex() {
      String var0 = "/usr/sbin/lpc status all";
      String[] var1 = execCmd(var0);
      if (var1 != null && var1.length != 0) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2].indexOf(64) != -1) {
               return 1;
            }
         }

         return 0;
      } else {
         return 1;
      }
   }

   public UnixPrintServiceLookup() {
      if (pollServices) {
         UnixPrintServiceLookup.PrinterChangeListener var1 = new UnixPrintServiceLookup.PrinterChangeListener();
         var1.setDaemon(true);
         var1.start();
         IPPPrintService.debug_println(debugPrefix + "polling turned on");
      }

   }

   public synchronized PrintService[] getPrintServices() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPrintJobAccess();
      }

      if (this.printServices == null || !pollServices) {
         this.refreshServices();
      }

      return this.printServices == null ? new PrintService[0] : (PrintService[])((PrintService[])this.printServices.clone());
   }

   private int addPrintServiceToList(ArrayList var1, PrintService var2) {
      int var3 = var1.indexOf(var2);
      if (CUPSPrinter.isCupsRunning() && var3 != -1) {
         PrinterURI var4 = (PrinterURI)var2.getAttribute(PrinterURI.class);
         if (var4.getURI().getHost().equals("localhost")) {
            IPPPrintService.debug_println(debugPrefix + "duplicate PrintService, ignoring the new local printer: " + var2);
            return var3;
         }

         PrintService var5 = (PrintService)((PrintService)var1.get(var3));
         var4 = (PrinterURI)var5.getAttribute(PrinterURI.class);
         if (!var4.getURI().getHost().equals("localhost")) {
            return var3;
         }

         IPPPrintService.debug_println(debugPrefix + "duplicate PrintService, removing existing local printer: " + var5);
         var1.remove(var5);
      }

      var1.add(var2);
      return var1.size() - 1;
   }

   public synchronized void refreshServices() {
      String[] var1 = null;
      String[] var2 = null;

      try {
         this.getDefaultPrintService();
      } catch (Throwable var10) {
         IPPPrintService.debug_println(debugPrefix + "Exception getting default printer : " + var10);
      }

      int var4;
      if (CUPSPrinter.isCupsRunning()) {
         int var3;
         try {
            var2 = CUPSPrinter.getAllPrinters();
            IPPPrintService.debug_println("CUPS URIs = " + var2);
            if (var2 != null) {
               for(var3 = 0; var3 < var2.length; ++var3) {
                  IPPPrintService.debug_println("URI=" + var2[var3]);
               }
            }
         } catch (Throwable var11) {
            IPPPrintService.debug_println(debugPrefix + "Exception getting all CUPS printers : " + var11);
         }

         if (var2 != null && var2.length > 0) {
            var1 = new String[var2.length];

            for(var3 = 0; var3 < var2.length; ++var3) {
               var4 = var2[var3].lastIndexOf("/");
               var1[var3] = var2[var3].substring(var4 + 1);
            }
         }
      } else if (!isMac() && !isSysV()) {
         if (isAIX()) {
            var1 = this.getAllPrinterNamesAIX();
         } else {
            var1 = this.getAllPrinterNamesBSD();
         }
      } else {
         var1 = this.getAllPrinterNamesSysV();
      }

      if (var1 == null) {
         if (this.defaultPrintService != null) {
            this.printServices = new PrintService[1];
            this.printServices[0] = this.defaultPrintService;
         } else {
            this.printServices = null;
         }

      } else {
         ArrayList var12 = new ArrayList();
         var4 = -1;

         int var5;
         for(var5 = 0; var5 < var1.length; ++var5) {
            if (var1[var5] != null) {
               if (this.defaultPrintService != null && var1[var5].equals(this.getPrinterDestName(this.defaultPrintService))) {
                  var4 = this.addPrintServiceToList(var12, this.defaultPrintService);
               } else if (this.printServices == null) {
                  IPPPrintService.debug_println(debugPrefix + "total# of printers = " + var1.length);
                  if (CUPSPrinter.isCupsRunning()) {
                     try {
                        this.addPrintServiceToList(var12, new IPPPrintService(var1[var5], var2[var5], true));
                     } catch (Exception var9) {
                        IPPPrintService.debug_println(debugPrefix + " getAllPrinters Exception " + var9);
                     }
                  } else {
                     var12.add(new UnixPrintService(var1[var5]));
                  }
               } else {
                  int var6;
                  for(var6 = 0; var6 < this.printServices.length; ++var6) {
                     if (this.printServices[var6] != null && var1[var5].equals(this.getPrinterDestName(this.printServices[var6]))) {
                        var12.add(this.printServices[var6]);
                        this.printServices[var6] = null;
                        break;
                     }
                  }

                  if (var6 == this.printServices.length) {
                     if (CUPSPrinter.isCupsRunning()) {
                        try {
                           this.addPrintServiceToList(var12, new IPPPrintService(var1[var5], var2[var5], true));
                        } catch (Exception var8) {
                           IPPPrintService.debug_println(debugPrefix + " getAllPrinters Exception " + var8);
                        }
                     } else {
                        var12.add(new UnixPrintService(var1[var5]));
                     }
                  }
               }
            }
         }

         if (this.printServices != null) {
            for(var5 = 0; var5 < this.printServices.length; ++var5) {
               if (this.printServices[var5] instanceof UnixPrintService && !this.printServices[var5].equals(this.defaultPrintService)) {
                  ((UnixPrintService)this.printServices[var5]).invalidateService();
               }
            }
         }

         if (var4 == -1 && this.defaultPrintService != null) {
            var4 = this.addPrintServiceToList(var12, this.defaultPrintService);
         }

         this.printServices = (PrintService[])((PrintService[])var12.toArray(new PrintService[0]));
         if (var4 > 0) {
            PrintService var13 = this.printServices[0];
            this.printServices[0] = this.printServices[var4];
            this.printServices[var4] = var13;
         }

      }
   }

   private boolean matchesAttributes(PrintService var1, PrintServiceAttributeSet var2) {
      Attribute[] var3 = var2.toArray();

      for(int var5 = 0; var5 < var3.length; ++var5) {
         PrintServiceAttribute var4 = var1.getAttribute(var3[var5].getCategory());
         if (var4 == null || !var4.equals(var3[var5])) {
            return false;
         }
      }

      return true;
   }

   private boolean checkPrinterName(String var1) {
      for(int var3 = 0; var3 < var1.length(); ++var3) {
         char var2 = var1.charAt(var3);
         if (!Character.isLetterOrDigit(var2) && var2 != '-' && var2 != '_' && var2 != '.' && var2 != '/') {
            return false;
         }
      }

      return true;
   }

   private String getPrinterDestName(PrintService var1) {
      return isMac() ? ((IPPPrintService)var1).getDest() : var1.getName();
   }

   private PrintService getServiceByName(PrinterName var1) {
      String var2 = var1.getValue();
      if (var2 != null && !var2.equals("") && this.checkPrinterName(var2)) {
         PrintService[] var3;
         if (this.printServices != null) {
            var3 = this.printServices;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               PrintService var6 = var3[var5];
               PrinterName var7 = (PrinterName)var6.getAttribute(PrinterName.class);
               if (var7.getValue().equals(var2)) {
                  return var6;
               }
            }
         }

         if (CUPSPrinter.isCupsRunning()) {
            try {
               return new IPPPrintService(var2, new URL("http://" + CUPSPrinter.getServer() + ":" + CUPSPrinter.getPort() + "/" + var2));
            } catch (Exception var8) {
               IPPPrintService.debug_println(debugPrefix + " getServiceByName Exception " + var8);
            }
         }

         var3 = null;
         PrintService var9;
         if (!isMac() && !isSysV()) {
            if (isAIX()) {
               var9 = this.getNamedPrinterNameAIX(var2);
            } else {
               var9 = this.getNamedPrinterNameBSD(var2);
            }
         } else {
            var9 = this.getNamedPrinterNameSysV(var2);
         }

         return var9;
      } else {
         return null;
      }
   }

   private PrintService[] getPrintServices(PrintServiceAttributeSet var1) {
      if (var1 != null && !var1.isEmpty()) {
         PrinterName var3 = (PrinterName)var1.get(PrinterName.class);
         PrintService[] var2;
         PrintService var4;
         if (var3 != null && (var4 = this.getDefaultPrintService()) != null) {
            PrinterName var7 = (PrinterName)var4.getAttribute(PrinterName.class);
            if (var7 != null && var3.equals(var7)) {
               if (this.matchesAttributes(var4, var1)) {
                  var2 = new PrintService[]{var4};
                  return var2;
               } else {
                  return new PrintService[0];
               }
            } else {
               PrintService var8 = this.getServiceByName(var3);
               if (var8 != null && this.matchesAttributes(var8, var1)) {
                  var2 = new PrintService[]{var8};
                  return var2;
               } else {
                  return new PrintService[0];
               }
            }
         } else {
            Vector var5 = new Vector();
            var2 = this.getPrintServices();

            int var6;
            for(var6 = 0; var6 < var2.length; ++var6) {
               if (this.matchesAttributes(var2[var6], var1)) {
                  var5.add(var2[var6]);
               }
            }

            var2 = new PrintService[var5.size()];

            for(var6 = 0; var6 < var2.length; ++var6) {
               var2[var6] = (PrintService)var5.elementAt(var6);
            }

            return var2;
         }
      } else {
         return this.getPrintServices();
      }
   }

   public PrintService[] getPrintServices(DocFlavor var1, AttributeSet var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkPrintJobAccess();
      }

      HashPrintRequestAttributeSet var4 = null;
      HashPrintServiceAttributeSet var5 = null;
      if (var2 != null && !var2.isEmpty()) {
         var4 = new HashPrintRequestAttributeSet();
         var5 = new HashPrintServiceAttributeSet();
         Attribute[] var6 = var2.toArray();

         for(int var7 = 0; var7 < var6.length; ++var7) {
            if (var6[var7] instanceof PrintRequestAttribute) {
               var4.add(var6[var7]);
            } else if (var6[var7] instanceof PrintServiceAttribute) {
               var5.add(var6[var7]);
            }
         }
      }

      PrintService[] var11 = this.getPrintServices(var5);
      if (var11.length == 0) {
         return var11;
      } else if (CUPSPrinter.isCupsRunning()) {
         ArrayList var13 = new ArrayList();

         for(int var8 = 0; var8 < var11.length; ++var8) {
            try {
               if (var11[var8].getUnsupportedAttributes(var1, var4) == null) {
                  var13.add(var11[var8]);
               }
            } catch (IllegalArgumentException var10) {
            }
         }

         var11 = new PrintService[var13.size()];
         return (PrintService[])((PrintService[])var13.toArray(var11));
      } else {
         PrintService var12 = var11[0];
         return (var1 == null || var12.isDocFlavorSupported(var1)) && var12.getUnsupportedAttributes(var1, var4) == null ? var11 : new PrintService[0];
      }
   }

   public MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] var1, AttributeSet var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkPrintJobAccess();
      }

      return new MultiDocPrintService[0];
   }

   public synchronized PrintService getDefaultPrintService() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPrintJobAccess();
      }

      this.defaultPrintService = null;
      String var2 = null;
      IPPPrintService.debug_println("isRunning ? " + CUPSPrinter.isCupsRunning());
      if (CUPSPrinter.isCupsRunning()) {
         String[] var3 = CUPSPrinter.getDefaultPrinter();
         if (var3 != null && var3.length >= 2) {
            this.defaultPrinter = var3[0];
            var2 = var3[1];
         }
      } else if (!isMac() && !isSysV()) {
         if (isAIX()) {
            this.defaultPrinter = this.getDefaultPrinterNameAIX();
         } else {
            this.defaultPrinter = this.getDefaultPrinterNameBSD();
         }
      } else {
         this.defaultPrinter = getDefaultPrinterNameSysV();
      }

      if (this.defaultPrinter == null) {
         return null;
      } else {
         this.defaultPrintService = null;
         if (this.printServices != null) {
            for(int var5 = 0; var5 < this.printServices.length; ++var5) {
               if (this.defaultPrinter.equals(this.getPrinterDestName(this.printServices[var5]))) {
                  this.defaultPrintService = this.printServices[var5];
                  break;
               }
            }
         }

         if (this.defaultPrintService == null) {
            if (CUPSPrinter.isCupsRunning()) {
               try {
                  IPPPrintService var6;
                  if (var2 != null && !var2.startsWith("file")) {
                     var6 = new IPPPrintService(this.defaultPrinter, var2, true);
                  } else {
                     var6 = new IPPPrintService(this.defaultPrinter, new URL("http://" + CUPSPrinter.getServer() + ":" + CUPSPrinter.getPort() + "/" + this.defaultPrinter));
                  }

                  this.defaultPrintService = var6;
               } catch (Exception var4) {
               }
            } else {
               this.defaultPrintService = new UnixPrintService(this.defaultPrinter);
            }
         }

         return this.defaultPrintService;
      }
   }

   public synchronized void getServicesInbackground(BackgroundLookupListener var1) {
      if (this.printServices != null) {
         var1.notifyServices(this.printServices);
      } else if (this.lookupListeners == null) {
         this.lookupListeners = new Vector();
         this.lookupListeners.add(var1);
         Thread var2 = new Thread(this);
         var2.start();
      } else {
         this.lookupListeners.add(var1);
      }

   }

   private PrintService[] copyOf(PrintService[] var1) {
      if (var1 != null && var1.length != 0) {
         PrintService[] var2 = new PrintService[var1.length];
         System.arraycopy(var1, 0, var2, 0, var1.length);
         return var2;
      } else {
         return var1;
      }
   }

   public void run() {
      PrintService[] var1 = this.getPrintServices();
      synchronized(this) {
         for(int var4 = 0; var4 < this.lookupListeners.size(); ++var4) {
            BackgroundLookupListener var3 = (BackgroundLookupListener)this.lookupListeners.elementAt(var4);
            var3.notifyServices(this.copyOf(var1));
         }

         this.lookupListeners = null;
      }
   }

   private String getDefaultPrinterNameBSD() {
      if (cmdIndex == -1) {
         cmdIndex = getBSDCommandIndex();
      }

      String[] var1 = execCmd(this.lpcFirstCom[cmdIndex]);
      if (var1 != null && var1.length != 0) {
         return cmdIndex == 1 && var1[0].startsWith("missingprinter") ? null : var1[0];
      } else {
         return null;
      }
   }

   private PrintService getNamedPrinterNameBSD(String var1) {
      if (cmdIndex == -1) {
         cmdIndex = getBSDCommandIndex();
      }

      String var2 = "/usr/sbin/lpc status " + var1 + this.lpcNameCom[cmdIndex];
      String[] var3 = execCmd(var2);
      return var3 != null && var3[0].equals(var1) ? new UnixPrintService(var1) : null;
   }

   private String[] getAllPrinterNamesBSD() {
      if (cmdIndex == -1) {
         cmdIndex = getBSDCommandIndex();
      }

      String[] var1 = execCmd(this.lpcAllCom[cmdIndex]);
      return var1 != null && var1.length != 0 ? var1 : null;
   }

   static String getDefaultPrinterNameSysV() {
      String var0 = "lp";
      String var1 = "/usr/bin/lpstat -d";
      String[] var2 = execCmd(var1);
      if (var2 != null && var2.length != 0) {
         int var3 = var2[0].indexOf(":");
         if (var3 != -1 && var2[0].length() > var3 + 1) {
            String var4 = var2[0].substring(var3 + 1).trim();
            return var4.length() == 0 ? null : var4;
         } else {
            return null;
         }
      } else {
         return var0;
      }
   }

   private PrintService getNamedPrinterNameSysV(String var1) {
      String var2 = "/usr/bin/lpstat -v " + var1;
      String[] var3 = execCmd(var2);
      return var3 != null && var3[0].indexOf("unknown printer") <= 0 ? new UnixPrintService(var1) : null;
   }

   private String[] getAllPrinterNamesSysV() {
      String var1 = "lp";
      String var2 = "/usr/bin/lpstat -v|/usr/bin/expand|/usr/bin/cut -f3 -d' ' |/usr/bin/cut -f1 -d':' | /usr/bin/sort";
      String[] var3 = execCmd(var2);
      ArrayList var4 = new ArrayList();

      for(int var5 = 0; var5 < var3.length; ++var5) {
         if (!var3[var5].equals("_default") && !var3[var5].equals(var1) && !var3[var5].equals("")) {
            var4.add(var3[var5]);
         }
      }

      return (String[])((String[])var4.toArray(new String[var4.size()]));
   }

   private String getDefaultPrinterNameAIX() {
      String[] var1 = execCmd(this.lpNameComAix[2]);
      var1 = UnixPrintService.filterPrinterNamesAIX(var1);
      return var1 != null && var1.length == 1 ? var1[0] : null;
   }

   private PrintService getNamedPrinterNameAIX(String var1) {
      String[] var2 = execCmd(this.lpNameComAix[3] + var1);
      var2 = UnixPrintService.filterPrinterNamesAIX(var2);
      return var2 != null && var2.length == 1 ? new UnixPrintService(var1) : null;
   }

   private String[] getAllPrinterNamesAIX() {
      String[] var1 = execCmd(this.lpNameComAix[aix_defaultPrinterEnumeration]);
      var1 = UnixPrintService.filterPrinterNamesAIX(var1);
      ArrayList var2 = new ArrayList();

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2.add(var1[var3]);
      }

      return (String[])((String[])var2.toArray(new String[var2.size()]));
   }

   static String[] execCmd(String var0) {
      ArrayList var1 = null;

      try {
         final String[] var2 = new String[3];
         if (!isSysV() && !isAIX()) {
            var2[0] = "/bin/sh";
            var2[1] = "-c";
            var2[2] = "LC_ALL=C " + var0;
         } else {
            var2[0] = "/usr/bin/sh";
            var2[1] = "-c";
            var2[2] = "env LC_ALL=C " + var0;
         }

         var1 = (ArrayList)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws IOException {
               BufferedReader var2x = null;
               File var3 = Files.createTempFile("prn", "xc").toFile();
               var2[2] = var2[2] + ">" + var3.getAbsolutePath();
               Process var1 = Runtime.getRuntime().exec(var2);

               try {
                  boolean var4 = false;

                  while(!var4) {
                     try {
                        var1.waitFor();
                        var4 = true;
                     } catch (InterruptedException var12) {
                     }
                  }

                  if (var1.exitValue() == 0) {
                     FileReader var5 = new FileReader(var3);
                     var2x = new BufferedReader(var5);
                     ArrayList var7 = new ArrayList();

                     String var6;
                     while((var6 = var2x.readLine()) != null) {
                        var7.add(var6);
                     }

                     ArrayList var8 = var7;
                     return var8;
                  }
               } finally {
                  var3.delete();
                  if (var2x != null) {
                     var2x.close();
                  }

                  var1.getInputStream().close();
                  var1.getErrorStream().close();
                  var1.getOutputStream().close();
               }

               return null;
            }
         });
      } catch (PrivilegedActionException var3) {
      }

      return var1 == null ? new String[0] : (String[])((String[])var1.toArray(new String[var1.size()]));
   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.print.polling")));
      if (var0 != null) {
         if (var0.equalsIgnoreCase("true")) {
            pollServices = true;
         } else if (var0.equalsIgnoreCase("false")) {
            pollServices = false;
         }
      }

      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.print.minRefreshTime")));
      if (var1 != null) {
         try {
            minRefreshTime = new Integer(var1);
         } catch (NumberFormatException var3) {
         }

         if (minRefreshTime < 120) {
            minRefreshTime = 120;
         }
      }

      osname = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("os.name")));
      if (isAIX()) {
         String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.print.aix.lpstat")));
         if (var2 != null) {
            if (var2.equalsIgnoreCase("lpstat")) {
               aix_defaultPrinterEnumeration = 1;
            } else if (var2.equalsIgnoreCase("lsallq")) {
               aix_defaultPrinterEnumeration = 0;
            }
         }
      }

      cmdIndex = -1;
   }

   private class PrinterChangeListener extends Thread {
      private PrinterChangeListener() {
      }

      public void run() {
         while(true) {
            try {
               UnixPrintServiceLookup.this.refreshServices();
            } catch (Exception var4) {
               IPPPrintService.debug_println(UnixPrintServiceLookup.debugPrefix + "Exception in refresh thread.");
               break;
            }

            int var1;
            if (UnixPrintServiceLookup.this.printServices != null && UnixPrintServiceLookup.this.printServices.length > UnixPrintServiceLookup.minRefreshTime) {
               var1 = UnixPrintServiceLookup.this.printServices.length;
            } else {
               var1 = UnixPrintServiceLookup.minRefreshTime;
            }

            try {
               sleep((long)(var1 * 1000));
            } catch (InterruptedException var3) {
               break;
            }
         }

      }

      // $FF: synthetic method
      PrinterChangeListener(Object var2) {
         this();
      }
   }
}
