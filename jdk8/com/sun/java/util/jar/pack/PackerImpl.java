package com.sun.java.util.jar.pack;

import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Pack200;

public class PackerImpl extends TLGlobals implements Pack200.Packer {
   public SortedMap<String, String> properties() {
      return this.props;
   }

   public synchronized void pack(JarFile var1, OutputStream var2) throws IOException {
      assert Utils.currentInstance.get() == null;

      boolean var3 = !this.props.getBoolean("com.sun.java.util.jar.pack.default.timezone");

      try {
         Utils.currentInstance.set(this);
         if (var3) {
            Utils.changeDefaultTimeZoneToUtc();
         }

         if ("0".equals(this.props.getProperty("pack.effort"))) {
            Utils.copyJarFile(var1, var2);
         } else {
            (new PackerImpl.DoPack()).run(var1, var2);
         }
      } finally {
         Utils.currentInstance.set((Object)null);
         if (var3) {
            Utils.restoreDefaultTimeZone();
         }

         var1.close();
      }

   }

   public synchronized void pack(JarInputStream var1, OutputStream var2) throws IOException {
      assert Utils.currentInstance.get() == null;

      boolean var3 = !this.props.getBoolean("com.sun.java.util.jar.pack.default.timezone");

      try {
         Utils.currentInstance.set(this);
         if (var3) {
            Utils.changeDefaultTimeZoneToUtc();
         }

         if ("0".equals(this.props.getProperty("pack.effort"))) {
            Utils.copyJarFile(var1, var2);
         } else {
            (new PackerImpl.DoPack()).run(var1, var2);
         }
      } finally {
         Utils.currentInstance.set((Object)null);
         if (var3) {
            Utils.restoreDefaultTimeZone();
         }

         var1.close();
      }

   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      this.props.addListener(var1);
   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
      this.props.removeListener(var1);
   }

   private class DoPack {
      final int verbose;
      final Package pkg;
      final String unknownAttrCommand;
      final String classFormatCommand;
      final Map<Attribute.Layout, Attribute> attrDefs;
      final Map<Attribute.Layout, String> attrCommands;
      final boolean keepFileOrder;
      final boolean keepClassOrder;
      final boolean keepModtime;
      final boolean latestModtime;
      final boolean keepDeflateHint;
      long totalOutputSize;
      int segmentCount;
      long segmentTotalSize;
      long segmentSize;
      final long segmentLimit;
      final List<String> passFiles;
      private int nread;

      private DoPack() {
         this.verbose = PackerImpl.this.props.getInteger("com.sun.java.util.jar.pack.verbose");
         PackerImpl.this.props.setInteger("pack.progress", 0);
         if (this.verbose > 0) {
            Utils.log.info(PackerImpl.this.props.toString());
         }

         this.pkg = new Package(Package.Version.makeVersion(PackerImpl.this.props, "min.class"), Package.Version.makeVersion(PackerImpl.this.props, "max.class"), Package.Version.makeVersion(PackerImpl.this.props, "package"));
         String var2 = PackerImpl.this.props.getProperty("pack.unknown.attribute", "pass");
         if (!"strip".equals(var2) && !"pass".equals(var2) && !"error".equals(var2)) {
            throw new RuntimeException("Bad option: pack.unknown.attribute = " + var2);
         } else {
            this.unknownAttrCommand = var2.intern();
            var2 = PackerImpl.this.props.getProperty("com.sun.java.util.jar.pack.class.format.error", "pass");
            if (!"pass".equals(var2) && !"error".equals(var2)) {
               throw new RuntimeException("Bad option: com.sun.java.util.jar.pack.class.format.error = " + var2);
            } else {
               this.classFormatCommand = var2.intern();
               HashMap var14 = new HashMap();
               HashMap var3 = new HashMap();
               String[] var4 = new String[]{"pack.class.attribute.", "pack.field.attribute.", "pack.method.attribute.", "pack.code.attribute."};
               int[] var5 = new int[]{0, 1, 2, 3};

               label118:
               for(int var6 = 0; var6 < var5.length; ++var6) {
                  String var7 = var4[var6];
                  SortedMap var8 = PackerImpl.this.props.prefixMap(var7);
                  Iterator var9 = var8.keySet().iterator();

                  Attribute.Layout var13;
                  do {
                     while(true) {
                        if (!var9.hasNext()) {
                           continue label118;
                        }

                        String var10 = (String)var9.next();

                        assert var10.startsWith(var7);

                        String var11 = var10.substring(var7.length());
                        String var12 = PackerImpl.this.props.getProperty(var10);
                        var13 = Attribute.keyForLookup(var5[var6], var11);
                        if (!"strip".equals(var12) && !"pass".equals(var12) && !"error".equals(var12)) {
                           Attribute.define(var14, var5[var6], var11, var12);
                           if (this.verbose > 1) {
                              Utils.log.fine("Added layout for " + Constants.ATTR_CONTEXT_NAME[var6] + " attribute " + var11 + " = " + var12);
                           }
                           break;
                        }

                        var3.put(var13, var12.intern());
                     }
                  } while($assertionsDisabled || var14.containsKey(var13));

                  throw new AssertionError();
               }

               this.attrDefs = var14.isEmpty() ? null : var14;
               this.attrCommands = var3.isEmpty() ? null : var3;
               this.keepFileOrder = PackerImpl.this.props.getBoolean("pack.keep.file.order");
               this.keepClassOrder = PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.keep.class.order");
               this.keepModtime = "keep".equals(PackerImpl.this.props.getProperty("pack.modification.time"));
               this.latestModtime = "latest".equals(PackerImpl.this.props.getProperty("pack.modification.time"));
               this.keepDeflateHint = "keep".equals(PackerImpl.this.props.getProperty("pack.deflate.hint"));
               int var15;
               if (!this.keepModtime && !this.latestModtime) {
                  var15 = PackerImpl.this.props.getTime("pack.modification.time");
                  if (var15 != 0) {
                     this.pkg.default_modtime = var15;
                  }
               }

               Package var10000;
               if (!this.keepDeflateHint) {
                  boolean var16 = PackerImpl.this.props.getBoolean("pack.deflate.hint");
                  if (var16) {
                     var10000 = this.pkg;
                     var10000.default_options |= 32;
                  }
               }

               this.totalOutputSize = 0L;
               this.segmentCount = 0;
               this.segmentTotalSize = 0L;
               this.segmentSize = 0L;
               long var18;
               if (PackerImpl.this.props.getProperty("pack.segment.limit", "").equals("")) {
                  var18 = -1L;
               } else {
                  var18 = PackerImpl.this.props.getLong("pack.segment.limit");
               }

               var18 = Math.min(2147483647L, var18);
               var18 = Math.max(-1L, var18);
               if (var18 == -1L) {
                  var18 = Long.MAX_VALUE;
               }

               this.segmentLimit = var18;
               this.passFiles = PackerImpl.this.props.getProperties("pack.pass.file.");
               ListIterator var19 = this.passFiles.listIterator();

               while(var19.hasNext()) {
                  String var17 = (String)var19.next();
                  if (var17 == null) {
                     var19.remove();
                  } else {
                     var17 = Utils.getJarEntryName(var17);
                     if (var17.endsWith("/")) {
                        var17 = var17.substring(0, var17.length() - 1);
                     }

                     var19.set(var17);
                  }
               }

               if (this.verbose > 0) {
                  Utils.log.info("passFiles = " + this.passFiles);
               }

               var15 = PackerImpl.this.props.getInteger("com.sun.java.util.jar.pack.archive.options");
               if (var15 != 0) {
                  var10000 = this.pkg;
                  var10000.default_options |= var15;
               }

               this.nread = 0;
            }
         }
      }

      boolean isClassFile(String var1) {
         if (!var1.endsWith(".class")) {
            return false;
         } else {
            int var3;
            for(String var2 = var1; !this.passFiles.contains(var2); var2 = var2.substring(0, var3)) {
               var3 = var2.lastIndexOf(47);
               if (var3 < 0) {
                  return true;
               }
            }

            return false;
         }
      }

      boolean isMetaInfFile(String var1) {
         return var1.startsWith("/META-INF") || var1.startsWith("META-INF");
      }

      private void makeNextPackage() {
         this.pkg.reset();
      }

      private void noteRead(PackerImpl.DoPack.InFile var1) {
         ++this.nread;
         if (this.verbose > 2) {
            Utils.log.fine("...read " + var1.name);
         }

         if (this.verbose > 0 && this.nread % 1000 == 0) {
            Utils.log.info("Have read " + this.nread + " files...");
         }

      }

      void run(JarInputStream var1, OutputStream var2) throws IOException {
         if (var1.getManifest() != null) {
            ByteArrayOutputStream var3 = new ByteArrayOutputStream();
            var1.getManifest().write(var3);
            ByteArrayInputStream var4 = new ByteArrayInputStream(var3.toByteArray());
            this.pkg.addFile(this.readFile("META-INF/MANIFEST.MF", var4));
         }

         JarEntry var11;
         while((var11 = var1.getNextJarEntry()) != null) {
            PackerImpl.DoPack.InFile var12 = new PackerImpl.DoPack.InFile(var11);
            String var5 = var12.name;
            Package.File var6 = this.readFile(var5, var1);
            Package.File var7 = null;
            long var8 = this.isMetaInfFile(var5) ? 0L : var12.getInputLength();
            if ((this.segmentSize += var8) > this.segmentLimit) {
               this.segmentSize -= var8;
               byte var10 = -1;
               this.flushPartial(var2, var10);
            }

            if (this.verbose > 1) {
               Utils.log.fine("Reading " + var5);
            }

            assert var11.isDirectory() == var5.endsWith("/");

            if (this.isClassFile(var5)) {
               var7 = this.readClass(var5, var6.getInputStream());
            }

            if (var7 == null) {
               var7 = var6;
               this.pkg.addFile(var6);
            }

            var12.copyTo(var7);
            this.noteRead(var12);
         }

         this.flushAll(var2);
      }

      void run(JarFile var1, OutputStream var2) throws IOException {
         List var3 = this.scanJar(var1);
         if (this.verbose > 0) {
            Utils.log.info("Reading " + var3.size() + " files...");
         }

         int var4 = 0;

         for(Iterator var5 = var3.iterator(); var5.hasNext(); ++var4) {
            PackerImpl.DoPack.InFile var6 = (PackerImpl.DoPack.InFile)var5.next();
            String var7 = var6.name;
            long var8 = this.isMetaInfFile(var7) ? 0L : var6.getInputLength();
            if ((this.segmentSize += var8) > this.segmentLimit) {
               this.segmentSize -= var8;
               float var10 = (float)(var4 + 1);
               float var11 = (float)(this.segmentCount + 1);
               float var12 = (float)var3.size() - var10;
               float var13 = var12 * (var11 / var10);
               if (this.verbose > 1) {
                  Utils.log.fine("Estimated segments to do: " + var13);
               }

               this.flushPartial(var2, (int)Math.ceil((double)var13));
            }

            InputStream var14 = var6.getInputStream();
            if (this.verbose > 1) {
               Utils.log.fine("Reading " + var7);
            }

            Package.File var15 = null;
            if (this.isClassFile(var7)) {
               var15 = this.readClass(var7, var14);
               if (var15 == null) {
                  var14.close();
                  var14 = var6.getInputStream();
               }
            }

            if (var15 == null) {
               var15 = this.readFile(var7, var14);
               this.pkg.addFile(var15);
            }

            var6.copyTo(var15);
            var14.close();
            this.noteRead(var6);
         }

         this.flushAll(var2);
      }

      Package.File readClass(String var1, InputStream var2) throws IOException {
         Package.Class var3 = this.pkg.new Class(var1);
         BufferedInputStream var9 = new BufferedInputStream(var2);
         ClassReader var4 = new ClassReader(var3, var9);
         var4.setAttrDefs(this.attrDefs);
         var4.setAttrCommands(this.attrCommands);
         var4.unknownAttrCommand = this.unknownAttrCommand;

         try {
            var4.read();
         } catch (IOException var8) {
            String var6 = "Passing class file uncompressed due to";
            if (var8 instanceof Attribute.FormatException) {
               Attribute.FormatException var10 = (Attribute.FormatException)var8;
               if (var10.layout.equals("pass")) {
                  Utils.log.info(var10.toString());
                  Utils.log.warning(var6 + " unrecognized attribute: " + var1);
                  return null;
               }
            } else if (var8 instanceof ClassReader.ClassFormatException) {
               ClassReader.ClassFormatException var7 = (ClassReader.ClassFormatException)var8;
               if (this.classFormatCommand.equals("pass")) {
                  Utils.log.info(var7.toString());
                  Utils.log.warning(var6 + " unknown class format: " + var1);
                  return null;
               }
            }

            throw var8;
         }

         this.pkg.addClass(var3);
         return var3.file;
      }

      Package.File readFile(String var1, InputStream var2) throws IOException {
         Package.File var3 = this.pkg.new File(var1);
         var3.readFrom(var2);
         if (var3.isDirectory() && var3.getFileLength() != 0L) {
            throw new IllegalArgumentException("Non-empty directory: " + var3.getFileName());
         } else {
            return var3;
         }
      }

      void flushPartial(OutputStream var1, int var2) throws IOException {
         if (!this.pkg.files.isEmpty() || !this.pkg.classes.isEmpty()) {
            this.flushPackage(var1, Math.max(1, var2));
            PackerImpl.this.props.setInteger("pack.progress", 25);
            this.makeNextPackage();
            ++this.segmentCount;
            this.segmentTotalSize += this.segmentSize;
            this.segmentSize = 0L;
         }
      }

      void flushAll(OutputStream var1) throws IOException {
         PackerImpl.this.props.setInteger("pack.progress", 50);
         this.flushPackage(var1, 0);
         var1.flush();
         PackerImpl.this.props.setInteger("pack.progress", 100);
         ++this.segmentCount;
         this.segmentTotalSize += this.segmentSize;
         this.segmentSize = 0L;
         if (this.verbose > 0 && this.segmentCount > 1) {
            Utils.log.info("Transmitted " + this.segmentTotalSize + " input bytes in " + this.segmentCount + " segments totaling " + this.totalOutputSize + " bytes");
         }

      }

      void flushPackage(OutputStream var1, int var2) throws IOException {
         int var3 = this.pkg.files.size();
         if (!this.keepFileOrder) {
            if (this.verbose > 1) {
               Utils.log.fine("Reordering files.");
            }

            boolean var4 = true;
            this.pkg.reorderFiles(this.keepClassOrder, var4);
         } else {
            assert this.pkg.files.containsAll(this.pkg.getClassStubs());

            ArrayList var9 = this.pkg.files;
            if (!$assertionsDisabled && !(var9 = new ArrayList(this.pkg.files)).retainAll(this.pkg.getClassStubs())) {
            }

            assert var9.equals(this.pkg.getClassStubs());
         }

         this.pkg.trimStubs();
         if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.debug")) {
            this.pkg.stripAttributeKind("Debug");
         }

         if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.compile")) {
            this.pkg.stripAttributeKind("Compile");
         }

         if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.constants")) {
            this.pkg.stripAttributeKind("Constant");
         }

         if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.exceptions")) {
            this.pkg.stripAttributeKind("Exceptions");
         }

         if (PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.strip.innerclasses")) {
            this.pkg.stripAttributeKind("InnerClasses");
         }

         PackageWriter var10 = new PackageWriter(this.pkg, var1);
         var10.archiveNextCount = var2;
         var10.write();
         var1.flush();
         if (this.verbose > 0) {
            long var5 = var10.archiveSize0 + var10.archiveSize1;
            this.totalOutputSize += var5;
            long var7 = this.segmentSize;
            Utils.log.info("Transmitted " + var3 + " files of " + var7 + " input bytes in a segment of " + var5 + " bytes");
         }

      }

      List<PackerImpl.DoPack.InFile> scanJar(JarFile var1) throws IOException {
         ArrayList var2 = new ArrayList();

         try {
            Iterator var3 = Collections.list(var1.entries()).iterator();

            while(var3.hasNext()) {
               JarEntry var4 = (JarEntry)var3.next();
               PackerImpl.DoPack.InFile var5 = new PackerImpl.DoPack.InFile(var1, var4);

               assert var4.isDirectory() == var5.name.endsWith("/");

               var2.add(var5);
            }

            return var2;
         } catch (IllegalStateException var6) {
            throw new IOException(var6.getLocalizedMessage(), var6);
         }
      }

      // $FF: synthetic method
      DoPack(Object var2) {
         this();
      }

      final class InFile {
         final String name;
         final JarFile jf;
         final JarEntry je;
         final File f;
         int modtime;
         int options;

         InFile(String var2) {
            this.modtime = 0;
            this.name = Utils.getJarEntryName(var2);
            this.f = new File(var2);
            this.jf = null;
            this.je = null;
            int var3 = this.getModtime(this.f.lastModified());
            if (DoPack.this.keepModtime && var3 != 0) {
               this.modtime = var3;
            } else if (DoPack.this.latestModtime && var3 > DoPack.this.pkg.default_modtime) {
               DoPack.this.pkg.default_modtime = var3;
            }

         }

         InFile(JarFile var2, JarEntry var3) {
            this.modtime = 0;
            this.name = Utils.getJarEntryName(var3.getName());
            this.f = null;
            this.jf = var2;
            this.je = var3;
            int var4 = this.getModtime(var3.getTime());
            if (DoPack.this.keepModtime && var4 != 0) {
               this.modtime = var4;
            } else if (DoPack.this.latestModtime && var4 > DoPack.this.pkg.default_modtime) {
               DoPack.this.pkg.default_modtime = var4;
            }

            if (DoPack.this.keepDeflateHint && var3.getMethod() == 8) {
               this.options |= 1;
            }

         }

         InFile(JarEntry var2) {
            this((JarFile)null, var2);
         }

         long getInputLength() {
            long var1 = this.je != null ? this.je.getSize() : this.f.length();

            assert var1 >= 0L : this + ".len=" + var1;

            return Math.max(0L, var1) + (long)this.name.length() + 5L;
         }

         int getModtime(long var1) {
            long var3 = (var1 + 500L) / 1000L;
            if ((long)((int)var3) == var3) {
               return (int)var3;
            } else {
               Utils.log.warning("overflow in modtime for " + this.f);
               return 0;
            }
         }

         void copyTo(Package.File var1) {
            if (this.modtime != 0) {
               var1.modtime = this.modtime;
            }

            var1.options |= this.options;
         }

         InputStream getInputStream() throws IOException {
            return (InputStream)(this.jf != null ? this.jf.getInputStream(this.je) : new FileInputStream(this.f));
         }

         public String toString() {
            return this.name;
         }
      }
   }
}
