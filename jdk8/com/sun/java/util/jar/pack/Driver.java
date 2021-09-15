package com.sun.java.util.jar.pack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

class Driver {
   private static final ResourceBundle RESOURCE = ResourceBundle.getBundle("com.sun.java.util.jar.pack.DriverResource");
   private static final String PACK200_OPTION_MAP = "--repack                 $ \n  -r +>- @--repack              $ \n--no-gzip                $ \n  -g +>- @--no-gzip             $ \n--strip-debug            $ \n  -G +>- @--strip-debug         $ \n--no-keep-file-order     $ \n  -O +>- @--no-keep-file-order  $ \n--segment-limit=      *> = \n  -S +>  @--segment-limit=      = \n--effort=             *> = \n  -E +>  @--effort=             = \n--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--modification-time=  *> = \n  -m +>  @--modification-time=  = \n--pass-file=        *> &\u0000 \n  -P +>  @--pass-file=        &\u0000 \n--unknown-attribute=  *> = \n  -U +>  @--unknown-attribute=  = \n--class-attribute=  *> &\u0000 \n  -C +>  @--class-attribute=  &\u0000 \n--field-attribute=  *> &\u0000 \n  -F +>  @--field-attribute=  &\u0000 \n--method-attribute= *> &\u0000 \n  -M +>  @--method-attribute= &\u0000 \n--code-attribute=   *> &\u0000 \n  -D +>  @--code-attribute=   &\u0000 \n--config-file=      *>   . \n  -f +>  @--config-file=        . \n--no-strip-debug  !--strip-debug         \n--gzip            !--no-gzip             \n--keep-file-order !--no-keep-file-order  \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n--           . \n-   +?    >- . \n";
   private static final String UNPACK200_OPTION_MAP = "--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--remove-pack-file       $ \n  -r +>- @--remove-pack-file    $ \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--config-file=        *> . \n  -f +>  @--config-file=        . \n--           . \n-   +?    >- . \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n";
   private static final String[] PACK200_PROPERTY_TO_OPTION = new String[]{"pack.segment.limit", "--segment-limit=", "pack.keep.file.order", "--no-keep-file-order", "pack.effort", "--effort=", "pack.deflate.hint", "--deflate-hint=", "pack.modification.time", "--modification-time=", "pack.pass.file.", "--pass-file=", "pack.unknown.attribute", "--unknown-attribute=", "pack.class.attribute.", "--class-attribute=", "pack.field.attribute.", "--field-attribute=", "pack.method.attribute.", "--method-attribute=", "pack.code.attribute.", "--code-attribute=", "com.sun.java.util.jar.pack.verbose", "--verbose", "com.sun.java.util.jar.pack.strip.debug", "--strip-debug"};
   private static final String[] UNPACK200_PROPERTY_TO_OPTION = new String[]{"unpack.deflate.hint", "--deflate-hint=", "com.sun.java.util.jar.pack.verbose", "--verbose", "com.sun.java.util.jar.pack.unpack.remove.packfile", "--remove-pack-file"};

   public static void main(String[] var0) throws IOException {
      ArrayList var1 = new ArrayList(Arrays.asList(var0));
      boolean var2 = true;
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = true;
      String var6 = null;
      String var7 = "com.sun.java.util.jar.pack.verbose";
      String var8 = var1.isEmpty() ? "" : (String)var1.get(0);
      byte var10 = -1;
      switch(var8.hashCode()) {
      case 1333303225:
         if (var8.equals("--pack")) {
            var10 = 0;
         }
         break;
      case 1559677394:
         if (var8.equals("--unpack")) {
            var10 = 1;
         }
      }

      switch(var10) {
      case 0:
         var1.remove(0);
         break;
      case 1:
         var1.remove(0);
         var2 = false;
         var3 = true;
      }

      HashMap var69 = new HashMap();
      var69.put(var7, System.getProperty(var7));
      String var9;
      String[] var70;
      if (var2) {
         var9 = "--repack                 $ \n  -r +>- @--repack              $ \n--no-gzip                $ \n  -g +>- @--no-gzip             $ \n--strip-debug            $ \n  -G +>- @--strip-debug         $ \n--no-keep-file-order     $ \n  -O +>- @--no-keep-file-order  $ \n--segment-limit=      *> = \n  -S +>  @--segment-limit=      = \n--effort=             *> = \n  -E +>  @--effort=             = \n--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--modification-time=  *> = \n  -m +>  @--modification-time=  = \n--pass-file=        *> &\u0000 \n  -P +>  @--pass-file=        &\u0000 \n--unknown-attribute=  *> = \n  -U +>  @--unknown-attribute=  = \n--class-attribute=  *> &\u0000 \n  -C +>  @--class-attribute=  &\u0000 \n--field-attribute=  *> &\u0000 \n  -F +>  @--field-attribute=  &\u0000 \n--method-attribute= *> &\u0000 \n  -M +>  @--method-attribute= &\u0000 \n--code-attribute=   *> &\u0000 \n  -D +>  @--code-attribute=   &\u0000 \n--config-file=      *>   . \n  -f +>  @--config-file=        . \n--no-strip-debug  !--strip-debug         \n--gzip            !--no-gzip             \n--keep-file-order !--no-keep-file-order  \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n--           . \n-   +?    >- . \n";
         var70 = PACK200_PROPERTY_TO_OPTION;
      } else {
         var9 = "--deflate-hint=       *> = \n  -H +>  @--deflate-hint=       = \n--verbose                $ \n  -v +>- @--verbose             $ \n--quiet        !--verbose  \n  -q +>- !--verbose               \n--remove-pack-file       $ \n  -r +>- @--remove-pack-file    $ \n--log-file=           *> = \n  -l +>  @--log-file=           = \n--config-file=        *> . \n  -f +>  @--config-file=        . \n--           . \n-   +?    >- . \n--version                . \n  -V +>  @--version             . \n--help               . \n  -? +> @--help . \n  -h +> @--help . \n";
         var70 = UNPACK200_PROPERTY_TO_OPTION;
      }

      HashMap var11 = new HashMap();

      String var14;
      String var15;
      String var22;
      String var74;
      String var78;
      String var88;
      try {
         label1624:
         while(true) {
            String var12 = parseCommandOptions(var1, var9, var11);
            Iterator var13 = var11.keySet().iterator();

            while(true) {
               while(true) {
                  do {
                     if (!var13.hasNext()) {
                        if (!"--config-file=".equals(var12)) {
                           if ("--version".equals(var12)) {
                              System.out.println(MessageFormat.format(RESOURCE.getString("VERSION"), Driver.class.getName(), "1.31, 07/05/05"));
                              return;
                           }

                           if ("--help".equals(var12)) {
                              printUsage(var2, true, System.out);
                              System.exit(1);
                              return;
                           }
                           break label1624;
                        }

                        var74 = (String)var1.remove(0);
                        Properties var75 = new Properties();
                        FileInputStream var76 = new FileInputStream(var74);
                        Throwable var79 = null;

                        try {
                           var75.load((InputStream)var76);
                        } catch (Throwable var64) {
                           var79 = var64;
                           throw var64;
                        } finally {
                           if (var76 != null) {
                              if (var79 != null) {
                                 try {
                                    var76.close();
                                 } catch (Throwable var62) {
                                    var79.addSuppressed(var62);
                                 }
                              } else {
                                 var76.close();
                              }
                           }

                        }

                        if (var69.get(var7) != null) {
                           var75.list(System.out);
                        }

                        Iterator var77 = var75.entrySet().iterator();

                        while(var77.hasNext()) {
                           Map.Entry var83 = (Map.Entry)var77.next();
                           var69.put((String)var83.getKey(), (String)var83.getValue());
                        }
                        continue label1624;
                     }

                     var14 = (String)var13.next();
                     var15 = null;

                     for(int var16 = 0; var16 < var70.length; var16 += 2) {
                        if (var14.equals(var70[1 + var16])) {
                           var15 = var70[0 + var16];
                           break;
                        }
                     }
                  } while(var15 == null);

                  var78 = (String)var11.get(var14);
                  var13.remove();
                  if (!var15.endsWith(".")) {
                     if (!var14.equals("--verbose") && !var14.endsWith("=")) {
                        boolean var81 = var78 != null;
                        if (var14.startsWith("--no-")) {
                           var81 = !var81;
                        }

                        var78 = var81 ? "true" : "false";
                     }

                     var69.put(var15, var78);
                  } else {
                     int var19;
                     if (var15.contains(".attribute.")) {
                        String[] var80 = var78.split("\u0000");
                        int var82 = var80.length;

                        for(var19 = 0; var19 < var82; ++var19) {
                           var88 = var80[var19];
                           String[] var89 = var88.split("=", 2);
                           var69.put(var15 + var89[0], var89[1]);
                        }
                     } else {
                        int var17 = 1;
                        String[] var18 = var78.split("\u0000");
                        var19 = var18.length;

                        for(int var20 = 0; var20 < var19; ++var20) {
                           String var21 = var18[var20];

                           do {
                              var22 = var15 + "cli." + var17++;
                           } while(var69.containsKey(var22));

                           var69.put(var22, var21);
                        }
                     }
                  }
               }
            }
         }
      } catch (IllegalArgumentException var68) {
         System.err.println(MessageFormat.format(RESOURCE.getString("BAD_ARGUMENT"), var68));
         printUsage(var2, false, System.err);
         System.exit(2);
         return;
      }

      Iterator var71 = var11.keySet().iterator();

      while(var71.hasNext()) {
         var74 = (String)var71.next();
         var14 = (String)var11.get(var74);
         byte var85 = -1;
         switch(var74.hashCode()) {
         case -845245370:
            if (var74.equals("--no-gzip")) {
               var85 = 1;
            }
            break;
         case 1339571416:
            if (var74.equals("--log-file=")) {
               var85 = 2;
            }
            break;
         case 1465478252:
            if (var74.equals("--repack")) {
               var85 = 0;
            }
         }

         switch(var85) {
         case 0:
            var4 = true;
            break;
         case 1:
            var5 = var14 == null;
            break;
         case 2:
            var6 = var14;
            break;
         default:
            throw new InternalError(MessageFormat.format(RESOURCE.getString("BAD_OPTION"), var74, var11.get(var74)));
         }
      }

      if (var6 != null && !var6.equals("")) {
         if (var6.equals("-")) {
            System.setErr(System.out);
         } else {
            FileOutputStream var72 = new FileOutputStream(var6);
            System.setErr(new PrintStream(var72));
         }
      }

      boolean var73 = var69.get(var7) != null;
      var74 = "";
      if (!var1.isEmpty()) {
         var74 = (String)var1.remove(0);
      }

      var14 = "";
      if (!var1.isEmpty()) {
         var14 = (String)var1.remove(0);
      }

      var15 = "";
      var78 = "";
      String var86 = "";
      if (var4) {
         if (var74.toLowerCase().endsWith(".pack") || var74.toLowerCase().endsWith(".pac") || var74.toLowerCase().endsWith(".gz")) {
            System.err.println(MessageFormat.format(RESOURCE.getString("BAD_REPACK_OUTPUT"), var74));
            printUsage(var2, false, System.err);
            System.exit(2);
         }

         var15 = var74;
         if (var14.equals("")) {
            var14 = var74;
         }

         var86 = createTempFile(var74, ".pack").getPath();
         var74 = var86;
         var5 = false;
      }

      if (!var1.isEmpty() || !var14.toLowerCase().endsWith(".jar") && !var14.toLowerCase().endsWith(".zip") && (!var14.equals("-") || var2)) {
         printUsage(var2, false, System.err);
         System.exit(2);
      } else {
         if (var4) {
            var3 = true;
            var2 = true;
         } else if (var2) {
            var3 = false;
         }

         Pack200.Packer var84 = Pack200.newPacker();
         Pack200.Unpacker var87 = Pack200.newUnpacker();
         var84.properties().putAll(var69);
         var87.properties().putAll(var69);
         if (var4 && var15.equals(var14)) {
            var88 = getZipComment(var14);
            if (var73 && var88.length() > 0) {
               System.out.println(MessageFormat.format(RESOURCE.getString("DETECTED_ZIP_COMMENT"), var88));
            }

            if (var88.indexOf("PACK200") >= 0) {
               System.out.println(MessageFormat.format(RESOURCE.getString("SKIP_FOR_REPACKED"), var14));
               var2 = false;
               var3 = false;
               var4 = false;
            }
         }

         boolean var41 = false;

         File var92;
         try {
            var41 = true;
            if (var2) {
               JarFile var90 = new JarFile(new File(var14));
               Object var91;
               if (var74.equals("-")) {
                  var91 = System.out;
                  System.setOut(System.err);
               } else {
                  FileOutputStream var93;
                  if (var5) {
                     if (!var74.endsWith(".gz")) {
                        System.err.println(MessageFormat.format(RESOURCE.getString("WRITE_PACK_FILE"), var74));
                        printUsage(var2, false, System.err);
                        System.exit(2);
                     }

                     var93 = new FileOutputStream(var74);
                     BufferedOutputStream var95 = new BufferedOutputStream(var93);
                     var91 = new GZIPOutputStream(var95);
                  } else {
                     if (!var74.toLowerCase().endsWith(".pack") && !var74.toLowerCase().endsWith(".pac")) {
                        System.err.println(MessageFormat.format(RESOURCE.getString("WRITE_PACKGZ_FILE"), var74));
                        printUsage(var2, false, System.err);
                        System.exit(2);
                     }

                     var93 = new FileOutputStream(var74);
                     var91 = new BufferedOutputStream(var93);
                  }
               }

               var84.pack((JarFile)var90, (OutputStream)var91);
               ((OutputStream)var91).close();
            }

            if (var4 && var15.equals(var14)) {
               var92 = createTempFile(var14, ".bak");
               var92.delete();
               boolean var96 = (new File(var14)).renameTo(var92);
               if (!var96) {
                  throw new Error(MessageFormat.format(RESOURCE.getString("SKIP_FOR_MOVE_FAILED"), var78));
               }

               var78 = var92.getPath();
            }

            if (var3) {
               Object var94;
               if (var74.equals("-")) {
                  var94 = System.in;
               } else {
                  var94 = new FileInputStream(new File(var74));
               }

               BufferedInputStream var98 = new BufferedInputStream((InputStream)var94);
               var94 = var98;
               if (Utils.isGZIPMagic(Utils.readMagic(var98))) {
                  var94 = new GZIPInputStream(var98);
               }

               var22 = var15.equals("") ? var14 : var15;
               Object var23;
               if (var22.equals("-")) {
                  var23 = System.out;
               } else {
                  var23 = new FileOutputStream(var22);
               }

               BufferedOutputStream var97 = new BufferedOutputStream((OutputStream)var23);
               JarOutputStream var24 = new JarOutputStream(var97);
               Throwable var25 = null;

               try {
                  var87.unpack((InputStream)var94, var24);
               } catch (Throwable var63) {
                  var25 = var63;
                  throw var63;
               } finally {
                  if (var24 != null) {
                     if (var25 != null) {
                        try {
                           var24.close();
                        } catch (Throwable var61) {
                           var25.addSuppressed(var61);
                        }
                     } else {
                        var24.close();
                     }
                  }

               }
            }

            if (!var78.equals("")) {
               (new File(var78)).delete();
               var78 = "";
               var41 = false;
            } else {
               var41 = false;
            }
         } finally {
            if (var41) {
               if (!var78.equals("")) {
                  File var30 = new File(var14);
                  var30.delete();
                  (new File(var78)).renameTo(var30);
               }

               if (!var86.equals("")) {
                  (new File(var86)).delete();
               }

            }
         }

         if (!var78.equals("")) {
            var92 = new File(var14);
            var92.delete();
            (new File(var78)).renameTo(var92);
         }

         if (!var86.equals("")) {
            (new File(var86)).delete();
         }

      }
   }

   private static File createTempFile(String var0, String var1) throws IOException {
      File var2 = new File(var0);
      String var3 = var2.getName();
      if (var3.length() < 3) {
         var3 = var3 + "tmp";
      }

      File var4 = var2.getParentFile() == null && var1.equals(".bak") ? (new File(".")).getAbsoluteFile() : var2.getParentFile();
      Path var5 = var4 == null ? Files.createTempFile(var3, var1) : Files.createTempFile(var4.toPath(), var3, var1);
      return var5.toFile();
   }

   private static void printUsage(boolean var0, boolean var1, PrintStream var2) {
      String var3 = var0 ? "pack200" : "unpack200";
      String[] var4 = (String[])((String[])RESOURCE.getObject("PACK_HELP"));
      String[] var5 = (String[])((String[])RESOURCE.getObject("UNPACK_HELP"));
      String[] var6 = var0 ? var4 : var5;

      for(int var7 = 0; var7 < var6.length; ++var7) {
         var2.println(var6[var7]);
         if (!var1) {
            var2.println(MessageFormat.format(RESOURCE.getString("MORE_INFO"), var3));
            break;
         }
      }

   }

   private static String getZipComment(String var0) throws IOException {
      byte[] var1 = new byte[1000];
      long var2 = (new File(var0)).length();
      if (var2 <= 0L) {
         return "";
      } else {
         long var4 = Math.max(0L, var2 - (long)var1.length);
         FileInputStream var6 = new FileInputStream(new File(var0));
         Throwable var7 = null;

         try {
            var6.skip(var4);
            var6.read(var1);

            for(int var8 = var1.length - 4; var8 >= 0; --var8) {
               if (var1[var8 + 0] == 80 && var1[var8 + 1] == 75 && var1[var8 + 2] == 5 && var1[var8 + 3] == 6) {
                  var8 += 22;
                  String var9;
                  if (var8 >= var1.length) {
                     var9 = "";
                     return var9;
                  }

                  var9 = new String(var1, var8, var1.length - var8, "UTF8");
                  return var9;
               }
            }

            String var22 = "";
            return var22;
         } catch (Throwable var20) {
            var7 = var20;
            throw var20;
         } finally {
            if (var6 != null) {
               if (var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var19) {
                     var7.addSuppressed(var19);
                  }
               } else {
                  var6.close();
               }
            }

         }
      }
   }

   private static String parseCommandOptions(List<String> var0, String var1, Map<String, String> var2) {
      String var3 = null;
      TreeMap var4 = new TreeMap();
      String[] var5 = var1.split("\n");
      int var6 = var5.length;

      String var10;
      for(int var7 = 0; var7 < var6; ++var7) {
         String var8 = var5[var7];
         String[] var9 = var8.split("\\p{Space}+");
         if (var9.length != 0) {
            var10 = var9[0];
            var9[0] = "";
            if (var10.length() == 0 && var9.length >= 1) {
               var10 = var9[1];
               var9[1] = "";
            }

            if (var10.length() != 0) {
               String[] var11 = (String[])var4.put(var10, var9);
               if (var11 != null) {
                  throw new RuntimeException(MessageFormat.format(RESOURCE.getString("DUPLICATE_OPTION"), var8.trim()));
               }
            }
         }
      }

      ListIterator var29 = var0.listIterator();
      ListIterator var30 = (new ArrayList()).listIterator();

      label196:
      while(true) {
         String var31;
         if (var30.hasPrevious()) {
            var31 = (String)var30.previous();
            var30.remove();
         } else {
            if (!var29.hasNext()) {
               break;
            }

            var31 = (String)var29.next();
         }

         int var32 = var31.length();

         while(true) {
            String var33 = var31.substring(0, var32);
            if (!var4.containsKey(var33)) {
               if (var32 != 0) {
                  SortedMap var35 = var4.headMap(var33);
                  int var36 = var35.isEmpty() ? 0 : ((String)var35.lastKey()).length();
                  var32 = Math.min(var36, var32 - 1);
                  var31.substring(0, var32);
                  continue;
               }
               break;
            } else {
               var33 = var33.intern();

               assert var31.startsWith(var33);

               assert var33.length() == var32;

               var10 = var31.substring(var32);
               boolean var34 = false;
               boolean var12 = false;
               int var13 = var30.nextIndex();
               String[] var14 = (String[])var4.get(var33);
               String[] var15 = var14;
               int var16 = var14.length;

               label190:
               for(int var17 = 0; var17 < var16; ++var17) {
                  String var18 = var15[var17];
                  if (var18.length() != 0) {
                     if (var18.startsWith("#")) {
                        break;
                     }

                     byte var19 = 0;
                     int var38 = var19 + 1;
                     char var20 = var18.charAt(var19);
                     boolean var21;
                     switch(var20) {
                     case '*':
                        var21 = true;
                        var20 = var18.charAt(var38++);
                        break;
                     case '+':
                        var21 = var10.length() != 0;
                        var20 = var18.charAt(var38++);
                        break;
                     default:
                        var21 = var10.length() == 0;
                     }

                     if (var21) {
                        String var22 = var18.substring(var38);
                        switch(var20) {
                        case '!':
                           String var23 = var22.length() != 0 ? var22.intern() : var33;
                           var2.remove(var23);
                           var2.put(var23, (Object)null);
                           var34 = true;
                           break;
                        case '$':
                           String var24;
                           if (var22.length() != 0) {
                              var24 = var22;
                           } else {
                              String var39 = (String)var2.get(var33);
                              if (var39 != null && var39.length() != 0) {
                                 var24 = "" + (1 + Integer.parseInt(var39));
                              } else {
                                 var24 = "1";
                              }
                           }

                           var2.put(var33, var24);
                           var34 = true;
                           break;
                        case '&':
                        case '=':
                           boolean var25 = var20 == '&';
                           String var26;
                           if (var30.hasPrevious()) {
                              var26 = (String)var30.previous();
                              var30.remove();
                           } else {
                              if (!var29.hasNext()) {
                                 var3 = var31 + " ?";
                                 var12 = true;
                                 break label190;
                              }

                              var26 = (String)var29.next();
                           }

                           if (var25) {
                              String var27 = (String)var2.get(var33);
                              if (var27 != null) {
                                 if (var22.length() == 0) {
                                    String var28 = " ";
                                 }

                                 var26 = var27 + var22 + var26;
                              }
                           }

                           var2.put(var33, var26);
                           var34 = true;
                           break;
                        case '.':
                           var3 = var22.length() != 0 ? var22.intern() : var33;
                           break label196;
                        case '>':
                           var30.add(var22 + var10);
                           var10 = "";
                           break;
                        case '?':
                           var3 = var22.length() != 0 ? var22.intern() : var31;
                           var12 = true;
                           break label190;
                        case '@':
                           var33 = var22.intern();
                           break;
                        default:
                           throw new RuntimeException(MessageFormat.format(RESOURCE.getString("BAD_SPEC"), var33, var18));
                        }
                     }
                  }
               }

               if (var34 && !var12) {
                  continue label196;
               }

               while(var30.nextIndex() > var13) {
                  var30.previous();
                  var30.remove();
               }

               if (var12) {
                  throw new IllegalArgumentException(var3);
               }

               if (var32 == 0) {
                  break;
               }

               --var32;
            }
         }

         var30.add(var31);
         break;
      }

      var0.subList(0, var29.nextIndex()).clear();

      while(var30.hasPrevious()) {
         var0.add(0, var30.previous());
      }

      return var3;
   }
}
