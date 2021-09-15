package sun.tools.jar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import sun.misc.JarIndex;

public class Main {
   String program;
   PrintStream out;
   PrintStream err;
   String fname;
   String mname;
   String ename;
   String zname = "";
   String[] files;
   String rootjar = null;
   Map<String, File> entryMap = new HashMap();
   Set<File> entries = new LinkedHashSet();
   Set<String> paths = new HashSet();
   boolean cflag;
   boolean uflag;
   boolean xflag;
   boolean tflag;
   boolean vflag;
   boolean flag0;
   boolean Mflag;
   boolean iflag;
   boolean nflag;
   boolean pflag;
   static final String MANIFEST_DIR = "META-INF/";
   static final String VERSION = "1.0";
   private static ResourceBundle rsrc;
   private static final boolean useExtractionTime = Boolean.getBoolean("sun.tools.jar.useExtractionTime");
   private boolean ok;
   private byte[] copyBuf = new byte[8192];
   private HashSet<String> jarPaths = new HashSet();

   private String getMsg(String var1) {
      try {
         return rsrc.getString(var1);
      } catch (MissingResourceException var3) {
         throw new Error("Error in message file");
      }
   }

   private String formatMsg(String var1, String var2) {
      String var3 = this.getMsg(var1);
      String[] var4 = new String[]{var2};
      return MessageFormat.format(var3, (Object[])var4);
   }

   private String formatMsg2(String var1, String var2, String var3) {
      String var4 = this.getMsg(var1);
      String[] var5 = new String[]{var2, var3};
      return MessageFormat.format(var4, (Object[])var5);
   }

   public Main(PrintStream var1, PrintStream var2, String var3) {
      this.out = var1;
      this.err = var2;
      this.program = var3;
   }

   private static File createTempFileInSameDirectoryAs(File var0) throws IOException {
      File var1 = var0.getParentFile();
      if (var1 == null) {
         var1 = new File(".");
      }

      return File.createTempFile("jartmp", (String)null, var1);
   }

   public synchronized boolean run(String[] var1) {
      this.ok = true;
      if (!this.parseArgs(var1)) {
         return false;
      } else {
         try {
            if ((this.cflag || this.uflag) && this.fname != null) {
               this.zname = this.fname.replace(File.separatorChar, '/');
               if (this.zname.startsWith("./")) {
                  this.zname = this.zname.substring(2);
               }
            }

            if (this.cflag) {
               java.util.jar.Manifest var2 = null;
               FileInputStream var3 = null;
               if (!this.Mflag) {
                  if (this.mname != null) {
                     var3 = new FileInputStream(this.mname);
                     var2 = new java.util.jar.Manifest(new BufferedInputStream(var3));
                  } else {
                     var2 = new java.util.jar.Manifest();
                  }

                  this.addVersion(var2);
                  this.addCreatedBy(var2);
                  if (this.isAmbiguousMainClass(var2)) {
                     if (var3 != null) {
                        var3.close();
                     }

                     return false;
                  }

                  if (this.ename != null) {
                     this.addMainClass(var2, this.ename);
                  }
               }

               this.expand((File)null, this.files, false);
               FileOutputStream var4;
               if (this.fname != null) {
                  var4 = new FileOutputStream(this.fname);
               } else {
                  var4 = new FileOutputStream(FileDescriptor.out);
                  if (this.vflag) {
                     this.vflag = false;
                  }
               }

               File var5 = null;
               FileOutputStream var6 = var4;
               String var7 = this.fname == null ? "tmpjar" : this.fname.substring(this.fname.indexOf(File.separatorChar) + 1);
               if (this.nflag) {
                  var5 = this.createTemporaryFile(var7, ".jar");
                  var4 = new FileOutputStream(var5);
               }

               this.create(new BufferedOutputStream(var4, 4096), var2);
               if (var3 != null) {
                  var3.close();
               }

               var4.close();
               if (this.nflag) {
                  JarFile var8 = null;
                  File var9 = null;
                  JarOutputStream var10 = null;

                  try {
                     Pack200.Packer var11 = Pack200.newPacker();
                     SortedMap var12 = var11.properties();
                     var12.put("pack.effort", "1");
                     var8 = new JarFile(var5.getCanonicalPath());
                     var9 = this.createTemporaryFile(var7, ".pack");
                     var4 = new FileOutputStream(var9);
                     var11.pack((JarFile)var8, var4);
                     var10 = new JarOutputStream(var6);
                     Pack200.Unpacker var13 = Pack200.newUnpacker();
                     var13.unpack(var9, var10);
                  } catch (IOException var40) {
                     this.fatalError((Exception)var40);
                  } finally {
                     if (var8 != null) {
                        var8.close();
                     }

                     if (var4 != null) {
                        var4.close();
                     }

                     if (var10 != null) {
                        var10.close();
                     }

                     if (var5 != null && var5.exists()) {
                        var5.delete();
                     }

                     if (var9 != null && var9.exists()) {
                        var9.delete();
                     }

                  }
               }
            } else if (this.uflag) {
               File var45 = null;
               File var46 = null;
               FileInputStream var48;
               FileOutputStream var49;
               if (this.fname != null) {
                  var45 = new File(this.fname);
                  var46 = createTempFileInSameDirectoryAs(var45);
                  var48 = new FileInputStream(var45);
                  var49 = new FileOutputStream(var46);
               } else {
                  var48 = new FileInputStream(FileDescriptor.in);
                  var49 = new FileOutputStream(FileDescriptor.out);
                  this.vflag = false;
               }

               FileInputStream var50 = !this.Mflag && this.mname != null ? new FileInputStream(this.mname) : null;
               this.expand((File)null, this.files, true);
               boolean var51 = this.update(var48, new BufferedOutputStream(var49), var50, (JarIndex)null);
               if (this.ok) {
                  this.ok = var51;
               }

               var48.close();
               var49.close();
               if (var50 != null) {
                  var50.close();
               }

               if (this.ok && this.fname != null) {
                  var45.delete();
                  if (!var46.renameTo(var45)) {
                     var46.delete();
                     throw new IOException(this.getMsg("error.write.file"));
                  }

                  var46.delete();
               }
            } else {
               FileInputStream var47;
               if (this.tflag) {
                  this.replaceFSC(this.files);
                  if (this.fname != null) {
                     this.list(this.fname, this.files);
                  } else {
                     var47 = new FileInputStream(FileDescriptor.in);

                     try {
                        this.list((InputStream)(new BufferedInputStream(var47)), this.files);
                     } finally {
                        var47.close();
                     }
                  }
               } else if (this.xflag) {
                  this.replaceFSC(this.files);
                  if (this.fname != null && this.files != null) {
                     this.extract(this.fname, this.files);
                  } else {
                     var47 = this.fname == null ? new FileInputStream(FileDescriptor.in) : new FileInputStream(this.fname);

                     try {
                        this.extract((InputStream)(new BufferedInputStream(var47)), this.files);
                     } finally {
                        var47.close();
                     }
                  }
               } else if (this.iflag) {
                  this.genIndex(this.rootjar, this.files);
               }
            }
         } catch (IOException var42) {
            this.fatalError((Exception)var42);
            this.ok = false;
         } catch (Error var43) {
            var43.printStackTrace();
            this.ok = false;
         } catch (Throwable var44) {
            var44.printStackTrace();
            this.ok = false;
         }

         this.out.flush();
         this.err.flush();
         return this.ok;
      }
   }

   boolean parseArgs(String[] var1) {
      try {
         var1 = CommandLine.parse(var1);
      } catch (FileNotFoundException var8) {
         this.fatalError(this.formatMsg("error.cant.open", var8.getMessage()));
         return false;
      } catch (IOException var9) {
         this.fatalError((Exception)var9);
         return false;
      }

      int var2 = 1;

      int var4;
      try {
         String var3 = var1[0];
         if (var3.startsWith("-")) {
            var3 = var3.substring(1);
         }

         for(var4 = 0; var4 < var3.length(); ++var4) {
            switch(var3.charAt(var4)) {
            case '0':
               this.flag0 = true;
               break;
            case 'M':
               this.Mflag = true;
               break;
            case 'P':
               this.pflag = true;
               break;
            case 'c':
               if (!this.xflag && !this.tflag && !this.uflag && !this.iflag) {
                  this.cflag = true;
                  break;
               }

               this.usageError();
               return false;
            case 'e':
               this.ename = var1[var2++];
               break;
            case 'f':
               this.fname = var1[var2++];
               break;
            case 'i':
               if (this.cflag || this.uflag || this.xflag || this.tflag) {
                  this.usageError();
                  return false;
               }

               this.rootjar = var1[var2++];
               this.iflag = true;
               break;
            case 'm':
               this.mname = var1[var2++];
               break;
            case 'n':
               this.nflag = true;
               break;
            case 't':
               if (this.cflag || this.uflag || this.xflag || this.iflag) {
                  this.usageError();
                  return false;
               }

               this.tflag = true;
               break;
            case 'u':
               if (this.cflag || this.xflag || this.tflag || this.iflag) {
                  this.usageError();
                  return false;
               }

               this.uflag = true;
               break;
            case 'v':
               this.vflag = true;
               break;
            case 'x':
               if (!this.cflag && !this.uflag && !this.tflag && !this.iflag) {
                  this.xflag = true;
                  break;
               }

               this.usageError();
               return false;
            default:
               this.error(this.formatMsg("error.illegal.option", String.valueOf(var3.charAt(var4))));
               this.usageError();
               return false;
            }
         }
      } catch (ArrayIndexOutOfBoundsException var11) {
         this.usageError();
         return false;
      }

      if (!this.cflag && !this.tflag && !this.xflag && !this.uflag && !this.iflag) {
         this.error(this.getMsg("error.bad.option"));
         this.usageError();
         return false;
      } else {
         int var12 = var1.length - var2;
         if (var12 > 0) {
            var4 = 0;
            String[] var5 = new String[var12];

            try {
               for(int var6 = var2; var6 < var1.length; ++var6) {
                  if (!var1[var6].equals("-C")) {
                     var5[var4++] = var1[var6];
                  } else {
                     ++var6;
                     String var7 = var1[var6];
                     var7 = var7.endsWith(File.separator) ? var7 : var7 + File.separator;

                     for(var7 = var7.replace(File.separatorChar, '/'); var7.indexOf("//") > -1; var7 = var7.replace("//", "/")) {
                     }

                     this.paths.add(var7.replace(File.separatorChar, '/'));
                     int var10001 = var4++;
                     StringBuilder var13 = (new StringBuilder()).append(var7);
                     ++var6;
                     var5[var10001] = var13.append(var1[var6]).toString();
                  }
               }
            } catch (ArrayIndexOutOfBoundsException var10) {
               this.usageError();
               return false;
            }

            this.files = new String[var4];
            System.arraycopy(var5, 0, this.files, 0, var4);
         } else {
            if (this.cflag && this.mname == null) {
               this.error(this.getMsg("error.bad.cflag"));
               this.usageError();
               return false;
            }

            if (this.uflag) {
               if (this.mname == null && this.ename == null) {
                  this.error(this.getMsg("error.bad.uflag"));
                  this.usageError();
                  return false;
               }

               return true;
            }
         }

         return true;
      }
   }

   void expand(File var1, String[] var2, boolean var3) {
      if (var2 != null) {
         for(int var4 = 0; var4 < var2.length; ++var4) {
            File var5;
            if (var1 == null) {
               var5 = new File(var2[var4]);
            } else {
               var5 = new File(var1, var2[var4]);
            }

            if (var5.isFile()) {
               if (this.entries.add(var5) && var3) {
                  this.entryMap.put(this.entryName(var5.getPath()), var5);
               }
            } else if (var5.isDirectory()) {
               if (this.entries.add(var5)) {
                  if (var3) {
                     String var6 = var5.getPath();
                     var6 = var6.endsWith(File.separator) ? var6 : var6 + File.separator;
                     this.entryMap.put(this.entryName(var6), var5);
                  }

                  this.expand(var5, var5.list(), var3);
               }
            } else {
               this.error(this.formatMsg("error.nosuch.fileordir", String.valueOf((Object)var5)));
               this.ok = false;
            }
         }

      }
   }

   void create(OutputStream var1, java.util.jar.Manifest var2) throws IOException {
      JarOutputStream var3 = new JarOutputStream(var1);
      if (this.flag0) {
         var3.setMethod(0);
      }

      if (var2 != null) {
         if (this.vflag) {
            this.output(this.getMsg("out.added.manifest"));
         }

         ZipEntry var4 = new ZipEntry("META-INF/");
         var4.setTime(System.currentTimeMillis());
         var4.setSize(0L);
         var4.setCrc(0L);
         var3.putNextEntry(var4);
         var4 = new ZipEntry("META-INF/MANIFEST.MF");
         var4.setTime(System.currentTimeMillis());
         if (this.flag0) {
            this.crc32Manifest(var4, var2);
         }

         var3.putNextEntry(var4);
         var2.write(var3);
         var3.closeEntry();
      }

      Iterator var6 = this.entries.iterator();

      while(var6.hasNext()) {
         File var5 = (File)var6.next();
         this.addFile(var3, var5);
      }

      var3.close();
   }

   private char toUpperCaseASCII(char var1) {
      return var1 >= 'a' && var1 <= 'z' ? (char)(var1 + 65 - 97) : var1;
   }

   private boolean equalsIgnoreCase(String var1, String var2) {
      assert var2.toUpperCase(Locale.ENGLISH).equals(var2);

      int var3;
      if ((var3 = var1.length()) != var2.length()) {
         return false;
      } else {
         for(int var4 = 0; var4 < var3; ++var4) {
            char var5 = var1.charAt(var4);
            char var6 = var2.charAt(var4);
            if (var5 != var6 && this.toUpperCaseASCII(var5) != var6) {
               return false;
            }
         }

         return true;
      }
   }

   boolean update(InputStream var1, OutputStream var2, InputStream var3, JarIndex var4) throws IOException {
      ZipInputStream var5 = new ZipInputStream(var1);
      JarOutputStream var6 = new JarOutputStream(var2);
      ZipEntry var7 = null;
      boolean var8 = false;
      boolean var9 = true;
      if (var4 != null) {
         this.addIndex(var4, var6);
      }

      java.util.jar.Manifest var19;
      do {
         while(true) {
            String var10;
            boolean var11;
            do {
               do {
                  if ((var7 = var5.getNextEntry()) == null) {
                     Iterator var14 = this.entries.iterator();

                     while(var14.hasNext()) {
                        File var16 = (File)var14.next();
                        this.addFile(var6, var16);
                     }

                     if (!var8) {
                        if (var3 != null) {
                           java.util.jar.Manifest var15 = new java.util.jar.Manifest(var3);
                           var9 = !this.isAmbiguousMainClass(var15);
                           if (var9 && !this.updateManifest(var15, var6)) {
                              var9 = false;
                           }
                        } else if (this.ename != null && !this.updateManifest(new java.util.jar.Manifest(), var6)) {
                           var9 = false;
                        }
                     }

                     var5.close();
                     var6.close();
                     return var9;
                  }

                  var10 = var7.getName();
                  var11 = this.equalsIgnoreCase(var10, "META-INF/MANIFEST.MF");
               } while(var4 != null && this.equalsIgnoreCase(var10, "META-INF/INDEX.LIST"));
            } while(this.Mflag && var11);

            if (var11 && (var3 != null || this.ename != null)) {
               var8 = true;
               if (var3 != null) {
                  FileInputStream var18 = new FileInputStream(this.mname);
                  boolean var13 = this.isAmbiguousMainClass(new java.util.jar.Manifest(var18));
                  var18.close();
                  if (var13) {
                     return false;
                  }
               }

               var19 = new java.util.jar.Manifest(var5);
               if (var3 != null) {
                  var19.read(var3);
               }
               break;
            }

            if (!this.entryMap.containsKey(var10)) {
               ZipEntry var12 = new ZipEntry(var10);
               var12.setMethod(var7.getMethod());
               var12.setTime(var7.getTime());
               var12.setComment(var7.getComment());
               var12.setExtra(var7.getExtra());
               if (var7.getMethod() == 0) {
                  var12.setSize(var7.getSize());
                  var12.setCrc(var7.getCrc());
               }

               var6.putNextEntry(var12);
               this.copy((InputStream)var5, (OutputStream)var6);
            } else {
               File var17 = (File)this.entryMap.get(var10);
               this.addFile(var6, var17);
               this.entryMap.remove(var10);
               this.entries.remove(var17);
            }
         }
      } while(this.updateManifest(var19, var6));

      return false;
   }

   private void addIndex(JarIndex var1, ZipOutputStream var2) throws IOException {
      ZipEntry var3 = new ZipEntry("META-INF/INDEX.LIST");
      var3.setTime(System.currentTimeMillis());
      if (this.flag0) {
         Main.CRC32OutputStream var4 = new Main.CRC32OutputStream();
         var1.write(var4);
         var4.updateEntry(var3);
      }

      var2.putNextEntry(var3);
      var1.write(var2);
      var2.closeEntry();
   }

   private boolean updateManifest(java.util.jar.Manifest var1, ZipOutputStream var2) throws IOException {
      this.addVersion(var1);
      this.addCreatedBy(var1);
      if (this.ename != null) {
         this.addMainClass(var1, this.ename);
      }

      ZipEntry var3 = new ZipEntry("META-INF/MANIFEST.MF");
      var3.setTime(System.currentTimeMillis());
      if (this.flag0) {
         this.crc32Manifest(var3, var1);
      }

      var2.putNextEntry(var3);
      var1.write(var2);
      if (this.vflag) {
         this.output(this.getMsg("out.update.manifest"));
      }

      return true;
   }

   private static final boolean isWinDriveLetter(char var0) {
      return var0 >= 'a' && var0 <= 'z' || var0 >= 'A' && var0 <= 'Z';
   }

   private String safeName(String var1) {
      if (!this.pflag) {
         int var2 = var1.length();
         int var3 = var1.lastIndexOf("../");
         if (var3 == -1) {
            var3 = 0;
         } else {
            var3 += 3;
         }

         if (File.separatorChar != '\\') {
            while(var3 < var2 && var1.charAt(var3) == '/') {
               ++var3;
            }
         } else {
            while(var3 < var2) {
               int var4 = var3;
               if (var3 + 1 < var2 && var1.charAt(var3 + 1) == ':' && isWinDriveLetter(var1.charAt(var3))) {
                  var3 += 2;
               }

               while(var3 < var2 && var1.charAt(var3) == '/') {
                  ++var3;
               }

               if (var3 == var4) {
                  break;
               }
            }
         }

         if (var3 != 0) {
            var1 = var1.substring(var3);
         }
      }

      return var1;
   }

   private String entryName(String var1) {
      var1 = var1.replace(File.separatorChar, '/');
      String var2 = "";
      Iterator var3 = this.paths.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         if (var1.startsWith(var4) && var4.length() > var2.length()) {
            var2 = var4;
         }
      }

      var1 = var1.substring(var2.length());
      var1 = this.safeName(var1);
      if (var1.startsWith("./")) {
         var1 = var1.substring(2);
      }

      return var1;
   }

   private void addVersion(java.util.jar.Manifest var1) {
      Attributes var2 = var1.getMainAttributes();
      if (var2.getValue(Attributes.Name.MANIFEST_VERSION) == null) {
         var2.put(Attributes.Name.MANIFEST_VERSION, "1.0");
      }

   }

   private void addCreatedBy(java.util.jar.Manifest var1) {
      Attributes var2 = var1.getMainAttributes();
      if (var2.getValue(new Attributes.Name("Created-By")) == null) {
         String var3 = System.getProperty("java.vendor");
         String var4 = System.getProperty("java.version");
         var2.put(new Attributes.Name("Created-By"), var4 + " (" + var3 + ")");
      }

   }

   private void addMainClass(java.util.jar.Manifest var1, String var2) {
      Attributes var3 = var1.getMainAttributes();
      var3.put(Attributes.Name.MAIN_CLASS, var2);
   }

   private boolean isAmbiguousMainClass(java.util.jar.Manifest var1) {
      if (this.ename != null) {
         Attributes var2 = var1.getMainAttributes();
         if (var2.get(Attributes.Name.MAIN_CLASS) != null) {
            this.error(this.getMsg("error.bad.eflag"));
            this.usageError();
            return true;
         }
      }

      return false;
   }

   void addFile(ZipOutputStream var1, File var2) throws IOException {
      String var3 = var2.getPath();
      boolean var4 = var2.isDirectory();
      if (var4) {
         var3 = var3.endsWith(File.separator) ? var3 : var3 + File.separator;
      }

      var3 = this.entryName(var3);
      if (!var3.equals("") && !var3.equals(".") && !var3.equals(this.zname)) {
         if ((var3.equals("META-INF/") || var3.equals("META-INF/MANIFEST.MF")) && !this.Mflag) {
            if (this.vflag) {
               this.output(this.formatMsg("out.ignore.entry", var3));
            }

         } else {
            long var5 = var4 ? 0L : var2.length();
            if (this.vflag) {
               this.out.print(this.formatMsg("out.adding", var3));
            }

            ZipEntry var7 = new ZipEntry(var3);
            var7.setTime(var2.lastModified());
            if (var5 == 0L) {
               var7.setMethod(0);
               var7.setSize(0L);
               var7.setCrc(0L);
            } else if (this.flag0) {
               this.crc32File(var7, var2);
            }

            var1.putNextEntry(var7);
            if (!var4) {
               this.copy((File)var2, (OutputStream)var1);
            }

            var1.closeEntry();
            if (this.vflag) {
               var5 = var7.getSize();
               long var8 = var7.getCompressedSize();
               this.out.print(this.formatMsg2("out.size", String.valueOf(var5), String.valueOf(var8)));
               if (var7.getMethod() == 8) {
                  long var10 = 0L;
                  if (var5 != 0L) {
                     var10 = (var5 - var8) * 100L / var5;
                  }

                  this.output(this.formatMsg("out.deflated", String.valueOf(var10)));
               } else {
                  this.output(this.getMsg("out.stored"));
               }
            }

         }
      }
   }

   private void copy(InputStream var1, OutputStream var2) throws IOException {
      int var3;
      while((var3 = var1.read(this.copyBuf)) != -1) {
         var2.write(this.copyBuf, 0, var3);
      }

   }

   private void copy(File var1, OutputStream var2) throws IOException {
      FileInputStream var3 = new FileInputStream(var1);

      try {
         this.copy((InputStream)var3, (OutputStream)var2);
      } finally {
         var3.close();
      }

   }

   private void copy(InputStream var1, File var2) throws IOException {
      FileOutputStream var3 = new FileOutputStream(var2);

      try {
         this.copy((InputStream)var1, (OutputStream)var3);
      } finally {
         var3.close();
      }

   }

   private void crc32Manifest(ZipEntry var1, java.util.jar.Manifest var2) throws IOException {
      Main.CRC32OutputStream var3 = new Main.CRC32OutputStream();
      var2.write(var3);
      var3.updateEntry(var1);
   }

   private void crc32File(ZipEntry var1, File var2) throws IOException {
      Main.CRC32OutputStream var3 = new Main.CRC32OutputStream();
      this.copy((File)var2, (OutputStream)var3);
      if (var3.n != var2.length()) {
         throw new JarException(this.formatMsg("error.incorrect.length", var2.getPath()));
      } else {
         var3.updateEntry(var1);
      }
   }

   void replaceFSC(String[] var1) {
      if (var1 != null) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2] = var1[var2].replace(File.separatorChar, '/');
         }
      }

   }

   Set<ZipEntry> newDirSet() {
      return new HashSet<ZipEntry>() {
         public boolean add(ZipEntry var1) {
            return var1 != null && !Main.useExtractionTime ? super.add(var1) : false;
         }
      };
   }

   void updateLastModifiedTime(Set<ZipEntry> var1) throws IOException {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ZipEntry var3 = (ZipEntry)var2.next();
         long var4 = var3.getTime();
         if (var4 != -1L) {
            String var6 = this.safeName(var3.getName().replace(File.separatorChar, '/'));
            if (var6.length() != 0) {
               File var7 = new File(var6.replace('/', File.separatorChar));
               var7.setLastModified(var4);
            }
         }
      }

   }

   void extract(InputStream var1, String[] var2) throws IOException {
      ZipInputStream var3 = new ZipInputStream(var1);
      Set var5 = this.newDirSet();

      while(true) {
         ZipEntry var4;
         while((var4 = var3.getNextEntry()) != null) {
            if (var2 == null) {
               var5.add(this.extractFile(var3, var4));
            } else {
               String var6 = var4.getName();
               String[] var7 = var2;
               int var8 = var2.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  String var10 = var7[var9];
                  if (var6.startsWith(var10)) {
                     var5.add(this.extractFile(var3, var4));
                     break;
                  }
               }
            }
         }

         this.updateLastModifiedTime(var5);
         return;
      }
   }

   void extract(String var1, String[] var2) throws IOException {
      ZipFile var3 = new ZipFile(var1);
      Set var4 = this.newDirSet();
      Enumeration var5 = var3.entries();

      while(true) {
         while(var5.hasMoreElements()) {
            ZipEntry var6 = (ZipEntry)var5.nextElement();
            if (var2 == null) {
               var4.add(this.extractFile(var3.getInputStream(var6), var6));
            } else {
               String var7 = var6.getName();
               String[] var8 = var2;
               int var9 = var2.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  String var11 = var8[var10];
                  if (var7.startsWith(var11)) {
                     var4.add(this.extractFile(var3.getInputStream(var6), var6));
                     break;
                  }
               }
            }
         }

         var3.close();
         this.updateLastModifiedTime(var4);
         return;
      }
   }

   ZipEntry extractFile(InputStream var1, ZipEntry var2) throws IOException {
      ZipEntry var3 = null;
      String var4 = this.safeName(var2.getName().replace(File.separatorChar, '/'));
      if (var4.length() == 0) {
         return var3;
      } else {
         File var5 = new File(var4.replace('/', File.separatorChar));
         if (var2.isDirectory()) {
            if (var5.exists()) {
               if (!var5.isDirectory()) {
                  throw new IOException(this.formatMsg("error.create.dir", var5.getPath()));
               }
            } else {
               if (!var5.mkdirs()) {
                  throw new IOException(this.formatMsg("error.create.dir", var5.getPath()));
               }

               var3 = var2;
            }

            if (this.vflag) {
               this.output(this.formatMsg("out.create", var4));
            }
         } else {
            if (var5.getParent() != null) {
               File var6 = new File(var5.getParent());
               if (!var6.exists() && !var6.mkdirs() || !var6.isDirectory()) {
                  throw new IOException(this.formatMsg("error.create.dir", var6.getPath()));
               }
            }

            try {
               this.copy(var1, var5);
            } finally {
               if (var1 instanceof ZipInputStream) {
                  ((ZipInputStream)var1).closeEntry();
               } else {
                  var1.close();
               }

            }

            if (this.vflag) {
               if (var2.getMethod() == 8) {
                  this.output(this.formatMsg("out.inflated", var4));
               } else {
                  this.output(this.formatMsg("out.extracted", var4));
               }
            }
         }

         if (!useExtractionTime) {
            long var10 = var2.getTime();
            if (var10 != -1L) {
               var5.setLastModified(var10);
            }
         }

         return var3;
      }
   }

   void list(InputStream var1, String[] var2) throws IOException {
      ZipInputStream var3 = new ZipInputStream(var1);

      ZipEntry var4;
      while((var4 = var3.getNextEntry()) != null) {
         var3.closeEntry();
         this.printEntry(var4, var2);
      }

   }

   void list(String var1, String[] var2) throws IOException {
      ZipFile var3 = new ZipFile(var1);
      Enumeration var4 = var3.entries();

      while(var4.hasMoreElements()) {
         this.printEntry((ZipEntry)var4.nextElement(), var2);
      }

      var3.close();
   }

   void dumpIndex(String var1, JarIndex var2) throws IOException {
      File var3 = new File(var1);
      Path var4 = var3.toPath();
      Path var5 = createTempFileInSameDirectoryAs(var3).toPath();

      try {
         if (this.update(Files.newInputStream(var4), Files.newOutputStream(var5), (InputStream)null, var2)) {
            try {
               Files.move(var5, var4, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException var10) {
               throw new IOException(this.getMsg("error.write.file"), var10);
            }
         }
      } finally {
         Files.deleteIfExists(var5);
      }

   }

   List<String> getJarPath(String var1) throws IOException {
      ArrayList var2 = new ArrayList();
      var2.add(var1);
      this.jarPaths.add(var1);
      String var3 = var1.substring(0, Math.max(0, var1.lastIndexOf(47) + 1));
      JarFile var4 = new JarFile(var1.replace('/', File.separatorChar));
      if (var4 != null) {
         java.util.jar.Manifest var5 = var4.getManifest();
         if (var5 != null) {
            Attributes var6 = var5.getMainAttributes();
            if (var6 != null) {
               String var7 = var6.getValue(Attributes.Name.CLASS_PATH);
               if (var7 != null) {
                  StringTokenizer var8 = new StringTokenizer(var7);

                  while(var8.hasMoreTokens()) {
                     String var9 = var8.nextToken();
                     if (!var9.endsWith("/")) {
                        var9 = var3.concat(var9);
                        if (!this.jarPaths.contains(var9)) {
                           var2.addAll(this.getJarPath(var9));
                        }
                     }
                  }
               }
            }
         }
      }

      var4.close();
      return var2;
   }

   void genIndex(String var1, String[] var2) throws IOException {
      List var3 = this.getJarPath(var1);
      int var4 = var3.size();
      if (var4 == 1 && var2 != null) {
         for(int var6 = 0; var6 < var2.length; ++var6) {
            var3.addAll(this.getJarPath(var2[var6]));
         }

         var4 = var3.size();
      }

      String[] var5 = (String[])var3.toArray(new String[var4]);
      JarIndex var7 = new JarIndex(var5);
      this.dumpIndex(var1, var7);
   }

   void printEntry(ZipEntry var1, String[] var2) throws IOException {
      if (var2 == null) {
         this.printEntry(var1);
      } else {
         String var3 = var1.getName();
         String[] var4 = var2;
         int var5 = var2.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            if (var3.startsWith(var7)) {
               this.printEntry(var1);
               return;
            }
         }
      }

   }

   void printEntry(ZipEntry var1) throws IOException {
      if (this.vflag) {
         StringBuilder var2 = new StringBuilder();
         String var3 = Long.toString(var1.getSize());

         for(int var4 = 6 - var3.length(); var4 > 0; --var4) {
            var2.append(' ');
         }

         var2.append(var3).append(' ').append((new Date(var1.getTime())).toString());
         var2.append(' ').append(var1.getName());
         this.output(var2.toString());
      } else {
         this.output(var1.getName());
      }

   }

   void usageError() {
      this.error(this.getMsg("usage"));
   }

   void fatalError(Exception var1) {
      var1.printStackTrace();
   }

   void fatalError(String var1) {
      this.error(this.program + ": " + var1);
   }

   protected void output(String var1) {
      this.out.println(var1);
   }

   protected void error(String var1) {
      this.err.println(var1);
   }

   public static void main(String[] var0) {
      Main var1 = new Main(System.out, System.err, "jar");
      System.exit(var1.run(var0) ? 0 : 1);
   }

   private File createTemporaryFile(String var1, String var2) {
      File var3 = null;

      try {
         var3 = File.createTempFile(var1, var2);
      } catch (SecurityException | IOException var6) {
      }

      if (var3 == null) {
         if (this.fname != null) {
            try {
               File var4 = (new File(this.fname)).getAbsoluteFile().getParentFile();
               var3 = File.createTempFile(this.fname, ".tmp" + var2, var4);
            } catch (IOException var5) {
               this.fatalError((Exception)var5);
            }
         } else {
            this.fatalError((Exception)(new IOException(this.getMsg("error.create.tempfile"))));
         }
      }

      return var3;
   }

   static {
      try {
         rsrc = ResourceBundle.getBundle("sun.tools.jar.resources.jar");
      } catch (MissingResourceException var1) {
         throw new Error("Fatal: Resource for jar is missing");
      }
   }

   private static class CRC32OutputStream extends OutputStream {
      final CRC32 crc = new CRC32();
      long n = 0L;

      CRC32OutputStream() {
      }

      public void write(int var1) throws IOException {
         this.crc.update(var1);
         ++this.n;
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.crc.update(var1, var2, var3);
         this.n += (long)var3;
      }

      public void updateEntry(ZipEntry var1) {
         var1.setMethod(0);
         var1.setSize(this.n);
         var1.setCrc(this.crc.getValue());
      }
   }
}
