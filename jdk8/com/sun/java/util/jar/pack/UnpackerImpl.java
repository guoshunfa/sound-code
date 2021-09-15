package com.sun.java.util.jar.pack;

import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

public class UnpackerImpl extends TLGlobals implements Pack200.Unpacker {
   Object _nunp;

   public void addPropertyChangeListener(PropertyChangeListener var1) {
      this.props.addListener(var1);
   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
      this.props.removeListener(var1);
   }

   public SortedMap<String, String> properties() {
      return this.props;
   }

   public String toString() {
      return Utils.getVersionString();
   }

   public synchronized void unpack(InputStream var1, JarOutputStream var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("null input");
      } else if (var2 == null) {
         throw new NullPointerException("null output");
      } else {
         assert Utils.currentInstance.get() == null;

         boolean var3 = !this.props.getBoolean("com.sun.java.util.jar.pack.default.timezone");

         try {
            Utils.currentInstance.set(this);
            if (var3) {
               Utils.changeDefaultTimeZoneToUtc();
            }

            int var4 = this.props.getInteger("com.sun.java.util.jar.pack.verbose");
            BufferedInputStream var5 = new BufferedInputStream(var1);
            if (Utils.isJarMagic(Utils.readMagic(var5))) {
               if (var4 > 0) {
                  Utils.log.info("Copying unpacked JAR file...");
               }

               Utils.copyJarFile(new JarInputStream(var5), var2);
            } else if (this.props.getBoolean("com.sun.java.util.jar.pack.disable.native")) {
               (new UnpackerImpl.DoUnpack()).run(var5, var2);
               var5.close();
               Utils.markJarFile(var2);
            } else {
               try {
                  (new NativeUnpack(this)).run((InputStream)var5, var2);
               } catch (NoClassDefFoundError | UnsatisfiedLinkError var10) {
                  (new UnpackerImpl.DoUnpack()).run(var5, var2);
               }

               var5.close();
               Utils.markJarFile(var2);
            }
         } finally {
            this._nunp = null;
            Utils.currentInstance.set((Object)null);
            if (var3) {
               Utils.restoreDefaultTimeZone();
            }

         }

      }
   }

   public synchronized void unpack(File var1, JarOutputStream var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("null input");
      } else if (var2 == null) {
         throw new NullPointerException("null output");
      } else {
         FileInputStream var3 = new FileInputStream(var1);
         Throwable var4 = null;

         try {
            this.unpack((InputStream)var3, var2);
         } catch (Throwable var13) {
            var4 = var13;
            throw var13;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var12) {
                     var4.addSuppressed(var12);
                  }
               } else {
                  var3.close();
               }
            }

         }

         if (this.props.getBoolean("com.sun.java.util.jar.pack.unpack.remove.packfile")) {
            var1.delete();
         }

      }
   }

   private class DoUnpack {
      final int verbose;
      final Package pkg;
      final boolean keepModtime;
      final boolean keepDeflateHint;
      final int modtime;
      final boolean deflateHint;
      final CRC32 crc;
      final ByteArrayOutputStream bufOut;
      final OutputStream crcOut;

      private DoUnpack() {
         this.verbose = UnpackerImpl.this.props.getInteger("com.sun.java.util.jar.pack.verbose");
         UnpackerImpl.this.props.setInteger("unpack.progress", 0);
         this.pkg = new Package();
         this.keepModtime = "keep".equals(UnpackerImpl.this.props.getProperty("com.sun.java.util.jar.pack.unpack.modification.time", "keep"));
         this.keepDeflateHint = "keep".equals(UnpackerImpl.this.props.getProperty("unpack.deflate.hint", "keep"));
         if (!this.keepModtime) {
            this.modtime = UnpackerImpl.this.props.getTime("com.sun.java.util.jar.pack.unpack.modification.time");
         } else {
            this.modtime = this.pkg.default_modtime;
         }

         this.deflateHint = this.keepDeflateHint ? false : UnpackerImpl.this.props.getBoolean("unpack.deflate.hint");
         this.crc = new CRC32();
         this.bufOut = new ByteArrayOutputStream();
         this.crcOut = new CheckedOutputStream(this.bufOut, this.crc);
      }

      public void run(BufferedInputStream var1, JarOutputStream var2) throws IOException {
         if (this.verbose > 0) {
            UnpackerImpl.this.props.list(System.out);
         }

         int var3 = 1;

         while(true) {
            this.unpackSegment(var1, var2);
            if (!Utils.isPackMagic(Utils.readMagic(var1))) {
               return;
            }

            if (this.verbose > 0) {
               Utils.log.info("Finished segment #" + var3);
            }

            ++var3;
         }
      }

      private void unpackSegment(InputStream var1, JarOutputStream var2) throws IOException {
         UnpackerImpl.this.props.setProperty("unpack.progress", "0");
         (new PackageReader(this.pkg, var1)).read();
         if (UnpackerImpl.this.props.getBoolean("unpack.strip.debug")) {
            this.pkg.stripAttributeKind("Debug");
         }

         if (UnpackerImpl.this.props.getBoolean("unpack.strip.compile")) {
            this.pkg.stripAttributeKind("Compile");
         }

         UnpackerImpl.this.props.setProperty("unpack.progress", "50");
         this.pkg.ensureAllClassFiles();
         HashSet var3 = new HashSet(this.pkg.getClasses());
         Iterator var4 = this.pkg.getFiles().iterator();

         while(var4.hasNext()) {
            Package.File var5 = (Package.File)var4.next();
            String var6 = var5.nameString;
            JarEntry var7 = new JarEntry(Utils.getJarEntryName(var6));
            boolean var8 = this.keepDeflateHint ? (var5.options & 1) != 0 || (this.pkg.default_options & 32) != 0 : this.deflateHint;
            boolean var9 = !var8;
            if (var9) {
               this.crc.reset();
            }

            this.bufOut.reset();
            if (var5.isClassStub()) {
               Package.Class var10 = var5.getStubClass();

               assert var10 != null;

               (new ClassWriter(var10, (OutputStream)(var9 ? this.crcOut : this.bufOut))).write();
               var3.remove(var10);
            } else {
               var5.writeTo((OutputStream)(var9 ? this.crcOut : this.bufOut));
            }

            var7.setMethod(var8 ? 8 : 0);
            if (var9) {
               if (this.verbose > 0) {
                  Utils.log.info("stored size=" + this.bufOut.size() + " and crc=" + this.crc.getValue());
               }

               var7.setMethod(0);
               var7.setSize((long)this.bufOut.size());
               var7.setCrc(this.crc.getValue());
            }

            if (this.keepModtime) {
               var7.setTime((long)var5.modtime);
               var7.setTime((long)var5.modtime * 1000L);
            } else {
               var7.setTime((long)this.modtime * 1000L);
            }

            var2.putNextEntry(var7);
            this.bufOut.writeTo(var2);
            var2.closeEntry();
            if (this.verbose > 0) {
               Utils.log.info("Writing " + Utils.zeString(var7));
            }
         }

         assert var3.isEmpty();

         UnpackerImpl.this.props.setProperty("unpack.progress", "100");
         this.pkg.reset();
      }

      // $FF: synthetic method
      DoUnpack(Object var2) {
         this();
      }
   }
}
