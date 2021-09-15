package sun.usagetracker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import jdk.internal.util.EnvUtils;

public final class UsageTrackerClient {
   private static final Object LOCK = new Object();
   private static final String ORCL_UT_CONFIG_FILE_NAME = "usagetracker.properties";
   private static final String ORCL_UT_USAGE_DIR = ".oracle_jre_usage";
   private static final String ORCL_UT_PROPERTY_NAME = "com.oracle.usagetracker.";
   private static final String ORCL_UT_PROPERTY_RUN_SYNCHRONOUSLY = "com.oracle.usagetracker.run.synchronous";
   private static final String ORCL_UT_PROPERTY_CONFIG_FILE_NAME = "com.oracle.usagetracker.config.file";
   private static final String ORCL_UT_LOGTOFILE = "com.oracle.usagetracker.logToFile";
   private static final String ORCL_UT_LOGFILEMAXSIZE = "com.oracle.usagetracker.logFileMaxSize";
   private static final String ORCL_UT_LOGTOUDP = "com.oracle.usagetracker.logToUDP";
   private static final String ORCL_UT_RECORD_MAXSIZE = "com.oracle.usagetracker.maxSize";
   private static final String ORCL_UT_RECORD_MAXFIELDSIZE = "com.oracle.usagetracker.maxFieldSize";
   private static final String ORCL_UT_SEND_TRUNCATED = "com.oracle.usagetracker.sendTruncatedRecords";
   private static final String ORCL_UT_TRACK_LAST_USAGE = "com.oracle.usagetracker.track.last.usage";
   private static final String ORCL_UT_VERBOSE = "com.oracle.usagetracker.verbose";
   private static final String ORCL_UT_DEBUG = "com.oracle.usagetracker.debug";
   private static final String ORCL_UT_ADDITIONALPROPERTIES = "com.oracle.usagetracker.additionalProperties";
   private static final String ORCL_UT_SEPARATOR = "com.oracle.usagetracker.separator";
   private static final String ORCL_UT_QUOTE = "com.oracle.usagetracker.quote";
   private static final String ORCL_UT_QUOTE_INNER = "com.oracle.usagetracker.innerQuote";
   private static final String DISABLE_LAST_USAGE_PROP_NAME = "jdk.disableLastUsageTracking";
   private static final String DEFAULT_SEP = ",";
   private static final String DEFAULT_QUOTE = "\"";
   private static final String DEFAULT_QUOTE_INNER = "'";
   private static final AtomicBoolean isFirstRun = new AtomicBoolean(true);
   private static final String javaHome = getPropertyPrivileged("java.home");
   private static final String userHomeKeyword = "${user.home}";
   private static String separator;
   private static String quote;
   private static String innerQuote;
   private static boolean enabled;
   private static boolean verbose;
   private static boolean debug;
   private static boolean trackTime = initTrackTime();
   private static String[] additionalProperties;
   private static String fullLogFilename;
   private static long logFileMaxSize;
   private static int maxSize;
   private static int maxFieldSize;
   private static boolean sendTruncated;
   private static String datagramHost;
   private static int datagramPort;
   private static String staticMessage;
   private static boolean staticMessageIsTruncated;

   private static String getPropertyPrivileged(String var0) {
      return getPropertyPrivileged(var0, (String)null);
   }

   private static String getPropertyPrivileged(final String var0, final String var1) {
      return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty(var0, var1);
         }
      });
   }

   private static String getEnvPrivileged(final String var0) {
      return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return EnvUtils.getEnvVar(var0);
         }
      });
   }

   private static boolean initTrackTime() {
      String var0 = getPropertyPrivileged("jdk.disableLastUsageTracking");
      if (var0 == null) {
         return true;
      } else {
         return !var0.isEmpty() && !var0.equalsIgnoreCase("true");
      }
   }

   private static File getConfigFilePrivileged() {
      File var0 = null;
      String[] var1 = new String[]{getPropertyPrivileged("com.oracle.usagetracker.config.file"), getOSSpecificConfigFilePath(), javaHome + File.separator + "lib" + File.separator + "management" + File.separator + "usagetracker.properties"};
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         final String var5 = var2[var4];
         if (var5 != null) {
            var0 = (File)AccessController.doPrivileged(new PrivilegedAction<File>() {
               public File run() {
                  File var1 = new File(var5);
                  return var1.exists() ? var1 : null;
               }
            });
            if (var0 != null) {
               break;
            }
         }
      }

      return var0;
   }

   private static String getOSSpecificConfigFilePath() {
      String var0 = getPropertyPrivileged("os.name");
      if (var0 != null) {
         if (var0.toLowerCase().startsWith("sunos")) {
            return "/etc/oracle/java/usagetracker.properties";
         }

         if (var0.toLowerCase().startsWith("mac")) {
            return "/Library/Application Support/Oracle/Java/usagetracker.properties";
         }

         if (var0.toLowerCase().startsWith("win")) {
            String var1 = getEnvPrivileged("ProgramData");
            return var1 == null ? null : var1 + "\\Oracle\\Java\\" + "usagetracker.properties";
         }

         if (var0.toLowerCase().startsWith("linux")) {
            return "/etc/oracle/java/usagetracker.properties";
         }
      }

      return null;
   }

   private String getFullLogFilename(Properties var1) {
      String var2 = var1.getProperty("com.oracle.usagetracker.logToFile", "");
      if (var2.isEmpty()) {
         return null;
      } else {
         if (var2.startsWith("${user.home}")) {
            if (var2.length() <= "${user.home}".length()) {
               this.printVerbose("UsageTracker: blank filename after user.home.");
               return null;
            }

            var2 = getPropertyPrivileged("user.home") + var2.substring("${user.home}".length());
         } else if (!(new File(var2)).isAbsolute()) {
            this.printVerbose("UsageTracker: relative path disallowed.");
            return null;
         }

         return var2;
      }
   }

   private long getPropertyValueLong(Properties var1, String var2) {
      String var3 = var1.getProperty(var2, "");
      if (!var3.isEmpty()) {
         try {
            return Long.parseLong(var3);
         } catch (NumberFormatException var5) {
            this.printVerbose("UsageTracker: bad value: " + var2);
         }
      }

      return -1L;
   }

   private boolean getPropertyValueBoolean(Properties var1, String var2, boolean var3) {
      String var4 = var1.getProperty(var2, "");
      return !var4.isEmpty() ? Boolean.parseBoolean(var4) : var3;
   }

   private String[] getAdditionalProperties(Properties var1) {
      String var2 = var1.getProperty("com.oracle.usagetracker.additionalProperties", "");
      return var2.isEmpty() ? new String[0] : var2.split(",");
   }

   private String parseDatagramHost(String var1) {
      if (var1 != null) {
         int var2 = var1.indexOf(58);
         if (var2 > 0 && var2 < var1.length() - 1) {
            return var1.substring(0, var2);
         }

         this.printVerbose("UsageTracker: bad UDP details.");
      }

      return null;
   }

   private int parseDatagramPort(String var1) {
      if (var1 != null) {
         int var2 = var1.indexOf(58);

         try {
            return Integer.parseInt(var1.substring(var2 + 1));
         } catch (Exception var4) {
            this.printVerbose("UsageTracker: bad UDP port.");
         }
      }

      return 0;
   }

   private void printVerbose(String var1) {
      if (verbose) {
         System.err.println(var1);
      }

   }

   private void printDebug(String var1) {
      if (debug) {
         System.err.println(var1);
      }

   }

   private void printDebugStackTrace(Throwable var1) {
      if (debug) {
         var1.printStackTrace();
      }

   }

   private void setupAndTimestamp(long var1) {
      if (isFirstRun.compareAndSet(true, false)) {
         File var3 = getConfigFilePrivileged();
         if (var3 != null) {
            this.setup(var3);
         }

         if (trackTime) {
            this.registerUsage(var1);
         }
      }

   }

   public void run(final String var1, final String var2) {
      this.printDebug("UsageTracker.run: " + var1 + ", javaCommand: " + var2);

      try {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               long var1x = System.currentTimeMillis();
               boolean var3 = Boolean.parseBoolean(System.getProperty("com.oracle.usagetracker.run.synchronous", "true"));
               if (var3) {
                  UsageTrackerClient.this.setupAndTimestamp(var1x);
                  UsageTrackerClient.this.printVerbose("UsageTracker: running synchronous.");
               }

               if (UsageTrackerClient.enabled || !var3) {
                  UsageTrackerClient.UsageTrackerRunnable var4 = UsageTrackerClient.this.new UsageTrackerRunnable(var1, var2, var1x, !var3);

                  ThreadGroup var5;
                  for(var5 = Thread.currentThread().getThreadGroup(); var5.getParent() != null; var5 = var5.getParent()) {
                  }

                  Thread var6 = new Thread(var5, var4, "UsageTracker");
                  var6.setDaemon(true);
                  var6.start();
               }

               return null;
            }
         });
      } catch (Throwable var4) {
         this.printVerbose("UsageTracker: error in starting thread.");
         this.printDebugStackTrace(var4);
      }

   }

   private void setup(File var1) {
      Properties var2 = new Properties();
      if (var1 != null) {
         try {
            FileInputStream var3 = new FileInputStream(var1);
            Throwable var4 = null;

            try {
               BufferedInputStream var5 = new BufferedInputStream(var3);
               Throwable var6 = null;

               try {
                  var2.load((InputStream)var5);
               } catch (Throwable var31) {
                  var6 = var31;
                  throw var31;
               } finally {
                  if (var5 != null) {
                     if (var6 != null) {
                        try {
                           var5.close();
                        } catch (Throwable var30) {
                           var6.addSuppressed(var30);
                        }
                     } else {
                        var5.close();
                     }
                  }

               }
            } catch (Throwable var33) {
               var4 = var33;
               throw var33;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var29) {
                        var4.addSuppressed(var29);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         } catch (Exception var35) {
            var2.clear();
         }
      }

      verbose = this.getPropertyValueBoolean(var2, "com.oracle.usagetracker.verbose", false);
      debug = this.getPropertyValueBoolean(var2, "com.oracle.usagetracker.debug", false);
      separator = var2.getProperty("com.oracle.usagetracker.separator", ",");
      quote = var2.getProperty("com.oracle.usagetracker.quote", "\"");
      innerQuote = var2.getProperty("com.oracle.usagetracker.innerQuote", "'");
      fullLogFilename = this.getFullLogFilename(var2);
      logFileMaxSize = this.getPropertyValueLong(var2, "com.oracle.usagetracker.logFileMaxSize");
      maxSize = (int)this.getPropertyValueLong(var2, "com.oracle.usagetracker.maxSize");
      maxFieldSize = (int)this.getPropertyValueLong(var2, "com.oracle.usagetracker.maxFieldSize");
      sendTruncated = this.getPropertyValueBoolean(var2, "com.oracle.usagetracker.sendTruncatedRecords", true);
      additionalProperties = this.getAdditionalProperties(var2);
      String var36 = var2.getProperty("com.oracle.usagetracker.logToUDP");
      datagramHost = this.parseDatagramHost(var36);
      datagramPort = this.parseDatagramPort(var36);
      enabled = fullLogFilename != null || datagramHost != null && datagramPort > 0;
      if (trackTime) {
         trackTime = this.getPropertyValueBoolean(var2, "com.oracle.usagetracker.track.last.usage", true);
      }

   }

   private void registerUsage(long var1) {
      try {
         String var3 = (new File(System.getProperty("java.home"))).getCanonicalPath();
         String var4 = getPropertyPrivileged("os.name");
         File var5 = null;
         String var6;
         if (var4.toLowerCase().startsWith("win")) {
            var6 = getEnvPrivileged("ProgramData");
            if (var6 != null) {
               var5 = new File(var6 + File.separator + "Oracle" + File.separator + "Java" + File.separator + ".oracle_jre_usage", this.getPathHash(var3) + ".timestamp");
               if (!var5.exists()) {
                  if (!var5.getParentFile().exists()) {
                     if (var5.getParentFile().mkdirs()) {
                        String var7 = getEnvPrivileged("SYSTEMROOT");
                        File var8 = new File(var7 + File.separator + "system32" + File.separator + "icacls.exe");
                        if (var8.exists()) {
                           Runtime.getRuntime().exec(var8 + " " + var5.getParentFile() + " /grant \"everyone\":(OI)(CI)M");
                        }
                     } else {
                        var5 = null;
                     }
                  }

                  if (var5 != null) {
                     var5.createNewFile();
                  }
               }
            }
         } else {
            var6 = System.getProperty("user.home");
            if (var6 != null) {
               var5 = new File(var6 + File.separator + ".oracle_jre_usage", this.getPathHash(var3) + ".timestamp");
               if (!var5.exists()) {
                  if (!var5.getParentFile().exists()) {
                     var5.getParentFile().mkdirs();
                  }

                  var5.createNewFile();
               }
            }
         }

         if (var5 != null) {
            try {
               FileOutputStream var22 = new FileOutputStream(var5);
               Throwable var23 = null;

               try {
                  String var24 = var3 + System.lineSeparator() + var1 + System.lineSeparator();
                  var22.write(var24.getBytes("UTF-8"));
               } catch (Throwable var18) {
                  var23 = var18;
                  throw var18;
               } finally {
                  if (var22 != null) {
                     if (var23 != null) {
                        try {
                           var22.close();
                        } catch (Throwable var17) {
                           var23.addSuppressed(var17);
                        }
                     } else {
                        var22.close();
                     }
                  }

               }
            } catch (IOException var20) {
               this.printDebugStackTrace(var20);
            }
         }
      } catch (IOException var21) {
         this.printDebugStackTrace(var21);
      }

   }

   private String getPathHash(String var1) {
      long var2 = 0L;

      for(int var4 = 0; var4 < var1.length(); ++var4) {
         var2 = 31L * var2 + (long)var1.charAt(var4);
      }

      return Long.toHexString(var2);
   }

   class UsageTrackerRunnable implements Runnable {
      private String callerName;
      private String javaCommand;
      private long timestamp;
      private boolean runAsync;
      private boolean truncated;

      UsageTrackerRunnable(String var2, String var3, long var4, boolean var6) {
         this.callerName = var2;
         this.javaCommand = var3 != null ? var3 : "";
         this.timestamp = var4;
         this.runAsync = var6;
      }

      private String limitString(String var1, int var2) {
         if (var2 > 0 && var1.length() >= var2) {
            UsageTrackerClient.this.printDebug("UsgeTracker: limitString truncating: max=" + var2 + " length=" + var1.length() + " String: " + var1);
            this.truncated = true;
            var1 = var1.substring(0, var2);
         }

         return var1;
      }

      private String buildMessage(String var1, String var2, long var3) {
         var2 = this.limitString(var2, UsageTrackerClient.maxFieldSize);
         if (this.truncated && !UsageTrackerClient.sendTruncated) {
            return null;
         } else {
            StringBuilder var5 = new StringBuilder();
            this.appendWithQuotes(var5, var1);
            var5.append(UsageTrackerClient.separator);
            Date var6 = new Date(var3);
            this.appendWithQuotes(var5, var6.toString());
            var5.append(UsageTrackerClient.separator);
            String var7 = "0";

            try {
               InetAddress var8 = InetAddress.getLocalHost();
               var7 = var8.toString();
            } catch (Throwable var9) {
            }

            this.appendWithQuotes(var5, var7);
            var5.append(UsageTrackerClient.separator);
            this.appendWithQuotes(var5, var2);
            var5.append(UsageTrackerClient.separator);
            var5.append(this.getRuntimeDetails());
            var5.append("\n");
            String var10 = this.limitString(var5.toString(), UsageTrackerClient.maxSize);
            if (this.truncated && !UsageTrackerClient.sendTruncated) {
               UsageTrackerClient.this.printVerbose("UsageTracker: length limit exceeded.");
               return null;
            } else {
               return var10;
            }
         }
      }

      private String getRuntimeDetails() {
         synchronized(UsageTrackerClient.LOCK) {
            if (UsageTrackerClient.staticMessage == null) {
               StringBuilder var2 = new StringBuilder();
               boolean var3 = this.truncated;
               this.truncated = false;
               this.appendWithQuotes(var2, UsageTrackerClient.javaHome);
               var2.append(UsageTrackerClient.separator);
               this.appendWithQuotes(var2, UsageTrackerClient.getPropertyPrivileged("java.version"));
               var2.append(UsageTrackerClient.separator);
               this.appendWithQuotes(var2, UsageTrackerClient.getPropertyPrivileged("java.vm.version"));
               var2.append(UsageTrackerClient.separator);
               this.appendWithQuotes(var2, UsageTrackerClient.getPropertyPrivileged("java.vendor"));
               var2.append(UsageTrackerClient.separator);
               this.appendWithQuotes(var2, UsageTrackerClient.getPropertyPrivileged("java.vm.vendor"));
               var2.append(UsageTrackerClient.separator);
               this.appendWithQuotes(var2, UsageTrackerClient.getPropertyPrivileged("os.name"));
               var2.append(UsageTrackerClient.separator);
               this.appendWithQuotes(var2, UsageTrackerClient.getPropertyPrivileged("os.arch"));
               var2.append(UsageTrackerClient.separator);
               this.appendWithQuotes(var2, UsageTrackerClient.getPropertyPrivileged("os.version"));
               var2.append(UsageTrackerClient.separator);
               List var4 = this.getInputArguments();
               StringBuilder var5 = new StringBuilder();
               Iterator var6 = var4.iterator();

               while(var6.hasNext()) {
                  String var7 = (String)var6.next();
                  var5.append(this.addQuotesFor(var7, " ", UsageTrackerClient.innerQuote));
                  var5.append(' ');
               }

               this.appendWithQuotes(var2, var5.toString());
               var2.append(UsageTrackerClient.separator);
               this.appendWithQuotes(var2, UsageTrackerClient.getPropertyPrivileged("java.class.path"));
               var2.append(UsageTrackerClient.separator);
               StringBuilder var13 = new StringBuilder();
               String[] var14 = UsageTrackerClient.additionalProperties;
               int var8 = var14.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  String var10 = var14[var9];
                  var13.append(var10.trim());
                  var13.append("=");
                  var13.append(this.addQuotesFor(UsageTrackerClient.getPropertyPrivileged(var10.trim()), " ", UsageTrackerClient.innerQuote));
                  var13.append(" ");
               }

               this.appendWithQuotes(var2, var13.toString());
               UsageTrackerClient.staticMessage = var2.toString();
               UsageTrackerClient.staticMessageIsTruncated = this.truncated;
               this.truncated = var3 | UsageTrackerClient.staticMessageIsTruncated;
            } else {
               this.truncated |= UsageTrackerClient.staticMessageIsTruncated;
            }

            return UsageTrackerClient.staticMessage;
         }
      }

      private void appendWithQuotes(StringBuilder var1, String var2) {
         var1.append(UsageTrackerClient.quote);
         var2 = this.limitString(var2, UsageTrackerClient.maxFieldSize);
         var2 = var2.replace(UsageTrackerClient.quote, UsageTrackerClient.quote + UsageTrackerClient.quote);
         if (!var2.isEmpty()) {
            var1.append(var2);
         } else {
            var1.append(" ");
         }

         var1.append(UsageTrackerClient.quote);
      }

      private String addQuotesFor(String var1, String var2, String var3) {
         if (var1 == null) {
            return var1;
         } else {
            var1 = var1.replace(var3, var3 + var3);
            if (var1.indexOf(var2) >= 0) {
               var1 = var3 + var1 + var3;
            }

            return var1;
         }
      }

      private List<String> getInputArguments() {
         return (List)AccessController.doPrivileged(new PrivilegedAction<List<String>>() {
            public List<String> run() {
               try {
                  Class var1 = Class.forName("java.lang.management.ManagementFactory", true, (ClassLoader)null);
                  Method var2 = var1.getMethod("getRuntimeMXBean", (Class[])null);
                  Object var3 = var2.invoke((Object)null, (Object[])null);
                  var1 = Class.forName("java.lang.management.RuntimeMXBean", true, (ClassLoader)null);
                  var2 = var1.getMethod("getInputArguments", (Class[])null);
                  List var4 = (List)var2.invoke(var3, (Object[])null);
                  return var4;
               } catch (ClassNotFoundException var5) {
                  return Collections.singletonList("n/a");
               } catch (NoSuchMethodException var6) {
                  throw new AssertionError(var6);
               } catch (IllegalAccessException var7) {
                  throw new AssertionError(var7);
               } catch (InvocationTargetException var8) {
                  throw new AssertionError(var8.getCause());
               }
            }
         });
      }

      private void sendDatagram(String var1) {
         UsageTrackerClient.this.printDebug("UsageTracker: sendDatagram");

         try {
            DatagramSocket var2 = new DatagramSocket();
            Throwable var3 = null;

            try {
               byte[] var4 = var1.getBytes("UTF-8");
               if (var4.length > var2.getSendBufferSize()) {
                  UsageTrackerClient.this.printVerbose("UsageTracker: message truncated for Datagram.");
               }

               UsageTrackerClient.this.printDebug("UsageTracker: host=" + UsageTrackerClient.datagramHost + ", port=" + UsageTrackerClient.datagramPort);
               UsageTrackerClient.this.printDebug("UsageTracker: SendBufferSize = " + var2.getSendBufferSize());
               UsageTrackerClient.this.printDebug("UsageTracker: packet length  = " + var4.length);
               InetAddress var5 = InetAddress.getByName(UsageTrackerClient.datagramHost);
               DatagramPacket var6 = new DatagramPacket(var4, var4.length > var2.getSendBufferSize() ? var2.getSendBufferSize() : var4.length, var5, UsageTrackerClient.datagramPort);
               var2.send(var6);
               UsageTrackerClient.this.printVerbose("UsageTracker: done sending to UDP.");
               UsageTrackerClient.this.printDebug("UsageTracker: sent size = " + var6.getLength());
            } catch (Throwable var15) {
               var3 = var15;
               throw var15;
            } finally {
               if (var2 != null) {
                  if (var3 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var14) {
                        var3.addSuppressed(var14);
                     }
                  } else {
                     var2.close();
                  }
               }

            }
         } catch (Throwable var17) {
            UsageTrackerClient.this.printVerbose("UsageTracker: error in sendDatagram: " + var17);
            UsageTrackerClient.this.printDebugStackTrace(var17);
         }

      }

      private void sendToFile(String var1) {
         UsageTrackerClient.this.printDebug("UsageTracker: sendToFile");
         File var2 = new File(UsageTrackerClient.fullLogFilename);
         if (UsageTrackerClient.logFileMaxSize >= 0L && var2.length() >= UsageTrackerClient.logFileMaxSize) {
            UsageTrackerClient.this.printVerbose("UsageTracker: log file size exceeds maximum.");
         } else {
            synchronized(UsageTrackerClient.LOCK) {
               try {
                  FileOutputStream var4 = new FileOutputStream(var2, true);
                  Throwable var5 = null;

                  try {
                     OutputStreamWriter var6 = new OutputStreamWriter(var4, "UTF-8");
                     Throwable var7 = null;

                     try {
                        var6.write((String)var1, 0, var1.length());
                        UsageTrackerClient.this.printVerbose("UsageTracker: done sending to file.");
                        UsageTrackerClient.this.printDebug("UsageTracker: " + UsageTrackerClient.fullLogFilename);
                     } catch (Throwable var35) {
                        var7 = var35;
                        throw var35;
                     } finally {
                        if (var6 != null) {
                           if (var7 != null) {
                              try {
                                 var6.close();
                              } catch (Throwable var34) {
                                 var7.addSuppressed(var34);
                              }
                           } else {
                              var6.close();
                           }
                        }

                     }
                  } catch (Throwable var37) {
                     var5 = var37;
                     throw var37;
                  } finally {
                     if (var4 != null) {
                        if (var5 != null) {
                           try {
                              var4.close();
                           } catch (Throwable var33) {
                              var5.addSuppressed(var33);
                           }
                        } else {
                           var4.close();
                        }
                     }

                  }
               } catch (Throwable var39) {
                  UsageTrackerClient.this.printVerbose("UsageTracker: error in sending to file.");
                  UsageTrackerClient.this.printDebugStackTrace(var39);
               }

            }
         }
      }

      public void run() {
         if (this.runAsync) {
            UsageTrackerClient.this.setupAndTimestamp(this.timestamp);
            UsageTrackerClient.this.printVerbose("UsageTracker: running asynchronous.");
         }

         if (UsageTrackerClient.enabled) {
            UsageTrackerClient.this.printDebug("UsageTrackerRunnable.run: " + this.callerName + ", javaCommand: " + this.javaCommand);
            String var1 = this.buildMessage(this.callerName, this.javaCommand, this.timestamp);
            if (var1 != null) {
               if (UsageTrackerClient.datagramHost != null && UsageTrackerClient.datagramPort > 0) {
                  this.sendDatagram(var1);
               }

               if (UsageTrackerClient.fullLogFilename != null) {
                  this.sendToFile(var1);
               }
            } else {
               UsageTrackerClient.this.printVerbose("UsageTracker: length limit exceeded.");
            }
         }

      }
   }
}
