package com.sun.java.util.jar.pack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

class Package {
   int verbose;
   final int magic;
   int default_modtime;
   int default_options;
   Package.Version defaultClassVersion;
   final Package.Version minClassVersion;
   final Package.Version maxClassVersion;
   final Package.Version packageVersion;
   Package.Version observedHighestClassVersion;
   ConstantPool.IndexGroup cp;
   public static final Attribute.Layout attrCodeEmpty;
   public static final Attribute.Layout attrBootstrapMethodsEmpty;
   public static final Attribute.Layout attrInnerClassesEmpty;
   public static final Attribute.Layout attrSourceFileSpecial;
   public static final Map<Attribute.Layout, Attribute> attrDefs;
   ArrayList<Package.Class> classes;
   ArrayList<Package.File> files;
   List<Package.InnerClass> allInnerClasses;
   Map<ConstantPool.ClassEntry, Package.InnerClass> allInnerClassesByThis;
   private static final int SLASH_MIN = 46;
   private static final int SLASH_MAX = 47;
   private static final int DOLLAR_MIN = 0;
   private static final int DOLLAR_MAX = 45;
   static final List<Object> noObjects;
   static final List<Package.Class.Field> noFields;
   static final List<Package.Class.Method> noMethods;
   static final List<Package.InnerClass> noInnerClasses;

   public Package() {
      PropMap var1 = Utils.currentPropMap();
      if (var1 != null) {
         this.verbose = var1.getInteger("com.sun.java.util.jar.pack.verbose");
      }

      this.magic = -889270259;
      this.default_modtime = 0;
      this.default_options = 0;
      this.defaultClassVersion = null;
      this.observedHighestClassVersion = null;
      this.cp = new ConstantPool.IndexGroup();
      this.classes = new ArrayList();
      this.files = new ArrayList();
      this.allInnerClasses = new ArrayList();
      this.minClassVersion = Constants.JAVA_MIN_CLASS_VERSION;
      this.maxClassVersion = Constants.JAVA_MAX_CLASS_VERSION;
      this.packageVersion = null;
   }

   public Package(Package.Version var1, Package.Version var2, Package.Version var3) {
      PropMap var4 = Utils.currentPropMap();
      if (var4 != null) {
         this.verbose = var4.getInteger("com.sun.java.util.jar.pack.verbose");
      }

      this.magic = -889270259;
      this.default_modtime = 0;
      this.default_options = 0;
      this.defaultClassVersion = null;
      this.observedHighestClassVersion = null;
      this.cp = new ConstantPool.IndexGroup();
      this.classes = new ArrayList();
      this.files = new ArrayList();
      this.allInnerClasses = new ArrayList();
      this.minClassVersion = var1 == null ? Constants.JAVA_MIN_CLASS_VERSION : var1;
      this.maxClassVersion = var2 == null ? Constants.JAVA_MAX_CLASS_VERSION : var2;
      this.packageVersion = var3;
   }

   public void reset() {
      this.cp = new ConstantPool.IndexGroup();
      this.classes.clear();
      this.files.clear();
      BandStructure.nextSeqForDebug = 0;
      this.observedHighestClassVersion = null;
   }

   Package.Version getDefaultClassVersion() {
      return this.defaultClassVersion;
   }

   private void setHighestClassVersion() {
      if (this.observedHighestClassVersion == null) {
         Package.Version var1 = Constants.JAVA_MIN_CLASS_VERSION;
         Iterator var2 = this.classes.iterator();

         while(var2.hasNext()) {
            Package.Class var3 = (Package.Class)var2.next();
            Package.Version var4 = var3.getVersion();
            if (var1.lessThan(var4)) {
               var1 = var4;
            }
         }

         this.observedHighestClassVersion = var1;
      }
   }

   Package.Version getHighestClassVersion() {
      this.setHighestClassVersion();
      return this.observedHighestClassVersion;
   }

   public List<Package.Class> getClasses() {
      return this.classes;
   }

   void addClass(Package.Class var1) {
      assert var1.getPackage() == this;

      boolean var2 = this.classes.add(var1);

      assert var2;

      if (var1.file == null) {
         var1.initFile((Package.File)null);
      }

      this.addFile(var1.file);
   }

   public List<Package.File> getFiles() {
      return this.files;
   }

   public List<Package.File> getClassStubs() {
      ArrayList var1 = new ArrayList(this.classes.size());
      Iterator var2 = this.classes.iterator();

      while(var2.hasNext()) {
         Package.Class var3 = (Package.Class)var2.next();

         assert var3.file.isClassStub();

         var1.add(var3.file);
      }

      return var1;
   }

   Package.File newStub(String var1) {
      Package.File var2 = new Package.File(var1);
      var2.options |= 2;
      var2.prepend = null;
      var2.append = null;
      return var2;
   }

   private static String fixupFileName(String var0) {
      String var1 = var0.replace(java.io.File.separatorChar, '/');
      if (var1.startsWith("/")) {
         throw new IllegalArgumentException("absolute file name " + var1);
      } else {
         return var1;
      }
   }

   void addFile(Package.File var1) {
      boolean var2 = this.files.add(var1);

      assert var2;

   }

   public List<Package.InnerClass> getAllInnerClasses() {
      return this.allInnerClasses;
   }

   public void setAllInnerClasses(Collection<Package.InnerClass> var1) {
      assert var1 != this.allInnerClasses;

      this.allInnerClasses.clear();
      this.allInnerClasses.addAll(var1);
      this.allInnerClassesByThis = new HashMap(this.allInnerClasses.size());
      Iterator var2 = this.allInnerClasses.iterator();

      Object var4;
      do {
         if (!var2.hasNext()) {
            return;
         }

         Package.InnerClass var3 = (Package.InnerClass)var2.next();
         var4 = this.allInnerClassesByThis.put(var3.thisClass, var3);
      } while($assertionsDisabled || var4 == null);

      throw new AssertionError();
   }

   public Package.InnerClass getGlobalInnerClass(ConstantPool.Entry var1) {
      assert var1 instanceof ConstantPool.ClassEntry;

      return (Package.InnerClass)this.allInnerClassesByThis.get(var1);
   }

   private static void visitInnerClassRefs(Collection<Package.InnerClass> var0, int var1, Collection<ConstantPool.Entry> var2) {
      if (var0 != null) {
         if (var1 == 0) {
            var2.add(getRefString("InnerClasses"));
         }

         if (var0.size() > 0) {
            Iterator var3 = var0.iterator();

            while(var3.hasNext()) {
               Package.InnerClass var4 = (Package.InnerClass)var3.next();
               var4.visitRefs(var1, var2);
            }
         }

      }
   }

   static String[] parseInnerClassName(String var0) {
      int var6 = var0.length();
      int var7 = lastIndexOf(46, 47, var0, var0.length()) + 1;
      int var5 = lastIndexOf(0, 45, var0, var0.length());
      if (var5 < var7) {
         return null;
      } else {
         String var2;
         String var3;
         int var4;
         if (isDigitString(var0, var5 + 1, var6)) {
            var2 = var0.substring(var5 + 1, var6);
            var3 = null;
            var4 = var5;
         } else if ((var4 = lastIndexOf(0, 45, var0, var5 - 1)) > var7 && isDigitString(var0, var4 + 1, var5)) {
            var2 = var0.substring(var4 + 1, var5);
            var3 = var0.substring(var5 + 1, var6).intern();
         } else {
            var4 = var5;
            var2 = null;
            var3 = var0.substring(var5 + 1, var6).intern();
         }

         String var1;
         if (var2 == null) {
            var1 = var0.substring(0, var4).intern();
         } else {
            var1 = null;
         }

         return new String[]{var1, var2, var3};
      }
   }

   private static int lastIndexOf(int var0, int var1, String var2, int var3) {
      int var4 = var3;

      char var5;
      do {
         --var4;
         if (var4 < 0) {
            return -1;
         }

         var5 = var2.charAt(var4);
      } while(var5 < var0 || var5 > var1);

      return var4;
   }

   private static boolean isDigitString(String var0, int var1, int var2) {
      if (var1 == var2) {
         return false;
      } else {
         for(int var3 = var1; var3 < var2; ++var3) {
            char var4 = var0.charAt(var3);
            if (var4 < '0' || var4 > '9') {
               return false;
            }
         }

         return true;
      }
   }

   static String getObviousSourceFile(String var0) {
      int var2 = lastIndexOf(46, 47, var0, var0.length()) + 1;
      String var1 = var0.substring(var2);
      int var3 = var1.length();

      int var4;
      do {
         var4 = lastIndexOf(0, 45, var1, var3 - 1);
         if (var4 < 0) {
            break;
         }

         var3 = var4;
      } while(var4 != 0);

      String var5 = var1.substring(0, var3) + ".java";
      return var5;
   }

   static ConstantPool.Utf8Entry getRefString(String var0) {
      return ConstantPool.getUtf8Entry(var0);
   }

   static ConstantPool.LiteralEntry getRefLiteral(Comparable<?> var0) {
      return ConstantPool.getLiteralEntry(var0);
   }

   void stripAttributeKind(String var1) {
      if (this.verbose > 0) {
         Utils.log.info("Stripping " + var1.toLowerCase() + " data and attributes...");
      }

      byte var3 = -1;
      switch(var1.hashCode()) {
      case -1679822317:
         if (var1.equals("Compile")) {
            var3 = 1;
         }
         break;
      case -503167036:
         if (var1.equals("Constant")) {
            var3 = 3;
         }
         break;
      case 65906227:
         if (var1.equals("Debug")) {
            var3 = 0;
         }
         break;
      case 679220772:
         if (var1.equals("Exceptions")) {
            var3 = 2;
         }
      }

      switch(var3) {
      case 0:
         this.strip("SourceFile");
         this.strip("LineNumberTable");
         this.strip("LocalVariableTable");
         this.strip("LocalVariableTypeTable");
         break;
      case 1:
         this.strip("Deprecated");
         this.strip("Synthetic");
         break;
      case 2:
         this.strip("Exceptions");
         break;
      case 3:
         this.stripConstantFields();
      }

   }

   public void trimToSize() {
      this.classes.trimToSize();
      Iterator var1 = this.classes.iterator();

      while(var1.hasNext()) {
         Package.Class var2 = (Package.Class)var1.next();
         var2.trimToSize();
      }

      this.files.trimToSize();
   }

   public void strip(String var1) {
      Iterator var2 = this.classes.iterator();

      while(var2.hasNext()) {
         Package.Class var3 = (Package.Class)var2.next();
         var3.strip(var1);
      }

   }

   public void stripConstantFields() {
      Iterator var1 = this.classes.iterator();

      while(var1.hasNext()) {
         Package.Class var2 = (Package.Class)var1.next();
         Iterator var3 = var2.fields.iterator();

         while(var3.hasNext()) {
            Package.Class.Field var4 = (Package.Class.Field)var3.next();
            if (Modifier.isFinal(var4.flags) && Modifier.isStatic(var4.flags) && var4.getAttribute("ConstantValue") != null && !var4.getName().startsWith("serial") && this.verbose > 2) {
               Utils.log.fine(">> Strip " + this + " ConstantValue");
               var3.remove();
            }
         }
      }

   }

   protected void visitRefs(int var1, Collection<ConstantPool.Entry> var2) {
      Iterator var3 = this.classes.iterator();

      while(var3.hasNext()) {
         Package.Class var4 = (Package.Class)var3.next();
         var4.visitRefs(var1, var2);
      }

      if (var1 != 0) {
         var3 = this.files.iterator();

         while(var3.hasNext()) {
            Package.File var5 = (Package.File)var3.next();
            var5.visitRefs(var1, var2);
         }

         visitInnerClassRefs(this.allInnerClasses, var1, var2);
      }

   }

   void reorderFiles(boolean var1, boolean var2) {
      if (!var1) {
         Collections.sort(this.classes);
      }

      List var3 = this.getClassStubs();
      Iterator var4 = this.files.iterator();

      while(true) {
         Package.File var5;
         do {
            if (!var4.hasNext()) {
               Collections.sort(this.files, new Comparator<Package.File>() {
                  public int compare(Package.File var1, Package.File var2) {
                     String var3 = var1.nameString;
                     String var4 = var2.nameString;
                     if (var3.equals(var4)) {
                        return 0;
                     } else if ("META-INF/MANIFEST.MF".equals(var3)) {
                        return -1;
                     } else if ("META-INF/MANIFEST.MF".equals(var4)) {
                        return 1;
                     } else {
                        String var5 = var3.substring(1 + var3.lastIndexOf(47));
                        String var6 = var4.substring(1 + var4.lastIndexOf(47));
                        String var7 = var5.substring(1 + var5.lastIndexOf(46));
                        String var8 = var6.substring(1 + var6.lastIndexOf(46));
                        int var9 = var7.compareTo(var8);
                        if (var9 != 0) {
                           return var9;
                        } else {
                           var9 = var3.compareTo(var4);
                           return var9;
                        }
                     }
                  }
               });
               this.files.addAll(var3);
               return;
            }

            var5 = (Package.File)var4.next();
         } while(!var5.isClassStub() && (!var2 || !var5.isDirectory()));

         var4.remove();
      }
   }

   void trimStubs() {
      for(ListIterator var1 = this.files.listIterator(this.files.size()); var1.hasPrevious(); var1.remove()) {
         Package.File var2 = (Package.File)var1.previous();
         if (!var2.isTrivialClassStub()) {
            if (this.verbose > 1) {
               Utils.log.fine("Keeping last non-trivial " + var2);
            }
            break;
         }

         if (this.verbose > 2) {
            Utils.log.fine("Removing trivial " + var2);
         }
      }

      if (this.verbose > 0) {
         Utils.log.info("Transmitting " + this.files.size() + " files, including per-file data for " + this.getClassStubs().size() + " classes out of " + this.classes.size());
      }

   }

   void buildGlobalConstantPool(Set<ConstantPool.Entry> var1) {
      if (this.verbose > 1) {
         Utils.log.fine("Checking for unused CP entries");
      }

      var1.add(getRefString(""));
      this.visitRefs(1, var1);
      ConstantPool.completeReferencesIn(var1, false);
      if (this.verbose > 1) {
         Utils.log.fine("Sorting CP entries");
      }

      ConstantPool.Index var2 = ConstantPool.makeIndex("unsorted", (Collection)var1);
      ConstantPool.Index[] var3 = ConstantPool.partitionByTag(var2);

      int var4;
      byte var5;
      ConstantPool.Index var6;
      for(var4 = 0; var4 < ConstantPool.TAGS_IN_ORDER.length; ++var4) {
         var5 = ConstantPool.TAGS_IN_ORDER[var4];
         var6 = var3[var5];
         if (var6 != null) {
            ConstantPool.sort(var6);
            this.cp.initIndexByTag(var5, var6);
            var3[var5] = null;
         }
      }

      for(var4 = 0; var4 < var3.length; ++var4) {
         ConstantPool.Index var7 = var3[var4];

         assert var7 == null;
      }

      for(var4 = 0; var4 < ConstantPool.TAGS_IN_ORDER.length; ++var4) {
         var5 = ConstantPool.TAGS_IN_ORDER[var4];
         var6 = this.cp.getIndexByTag(var5);

         assert var6.assertIsSorted();

         if (this.verbose > 2) {
            Utils.log.fine(var6.dumpString());
         }
      }

   }

   void ensureAllClassFiles() {
      HashSet var1 = new HashSet(this.files);
      Iterator var2 = this.classes.iterator();

      while(var2.hasNext()) {
         Package.Class var3 = (Package.Class)var2.next();
         if (!var1.contains(var3.file)) {
            this.files.add(var3.file);
         }
      }

   }

   static {
      HashMap var0 = new HashMap(3);
      attrCodeEmpty = Attribute.define(var0, 2, "Code", "").layout();
      attrBootstrapMethodsEmpty = Attribute.define(var0, 0, "BootstrapMethods", "").layout();
      attrInnerClassesEmpty = Attribute.define(var0, 0, "InnerClasses", "").layout();
      attrSourceFileSpecial = Attribute.define(var0, 0, "SourceFile", "RUNH").layout();
      attrDefs = Collections.unmodifiableMap(var0);

      assert lastIndexOf(0, 45, "x$$y$", 4) == 2;

      assert lastIndexOf(46, 47, "x//y/", 4) == 2;

      noObjects = Arrays.asList();
      noFields = Arrays.asList();
      noMethods = Arrays.asList();
      noInnerClasses = Arrays.asList();
   }

   protected static final class Version {
      public final short major;
      public final short minor;

      private Version(short var1, short var2) {
         this.major = var1;
         this.minor = var2;
      }

      public String toString() {
         return this.major + "." + this.minor;
      }

      public boolean equals(Object var1) {
         return var1 instanceof Package.Version && this.major == ((Package.Version)var1).major && this.minor == ((Package.Version)var1).minor;
      }

      public int intValue() {
         return (this.major << 16) + this.minor;
      }

      public int hashCode() {
         return (this.major << 16) + 7 + this.minor;
      }

      public static Package.Version of(int var0, int var1) {
         return new Package.Version((short)var0, (short)var1);
      }

      public static Package.Version of(byte[] var0) {
         int var1 = (var0[0] & 255) << 8 | var0[1] & 255;
         int var2 = (var0[2] & 255) << 8 | var0[3] & 255;
         return new Package.Version((short)var2, (short)var1);
      }

      public static Package.Version of(int var0) {
         short var1 = (short)var0;
         short var2 = (short)(var0 >>> 16);
         return new Package.Version(var2, var1);
      }

      public static Package.Version makeVersion(PropMap var0, String var1) {
         int var2 = var0.getInteger("com.sun.java.util.jar.pack." + var1 + ".minver", -1);
         int var3 = var0.getInteger("com.sun.java.util.jar.pack." + var1 + ".majver", -1);
         return var2 >= 0 && var3 >= 0 ? of(var3, var2) : null;
      }

      public byte[] asBytes() {
         byte[] var1 = new byte[]{(byte)(this.minor >> 8), (byte)this.minor, (byte)(this.major >> 8), (byte)this.major};
         return var1;
      }

      public int compareTo(Package.Version var1) {
         return this.intValue() - var1.intValue();
      }

      public boolean lessThan(Package.Version var1) {
         return this.compareTo(var1) < 0;
      }

      public boolean greaterThan(Package.Version var1) {
         return this.compareTo(var1) > 0;
      }
   }

   static class InnerClass implements Comparable<Package.InnerClass> {
      final ConstantPool.ClassEntry thisClass;
      final ConstantPool.ClassEntry outerClass;
      final ConstantPool.Utf8Entry name;
      final int flags;
      final boolean predictable;

      InnerClass(ConstantPool.ClassEntry var1, ConstantPool.ClassEntry var2, ConstantPool.Utf8Entry var3, int var4) {
         this.thisClass = var1;
         this.outerClass = var2;
         this.name = var3;
         this.flags = var4;
         this.predictable = this.computePredictable();
      }

      private boolean computePredictable() {
         String[] var1 = Package.parseInnerClassName(this.thisClass.stringValue());
         if (var1 == null) {
            return false;
         } else {
            String var2 = var1[0];
            String var3 = var1[2];
            String var4 = this.name == null ? null : this.name.stringValue();
            String var5 = this.outerClass == null ? null : this.outerClass.stringValue();
            boolean var6 = var3 == var4 && var2 == var5;
            return var6;
         }
      }

      public boolean equals(Object var1) {
         if (var1 != null && var1.getClass() == Package.InnerClass.class) {
            Package.InnerClass var2 = (Package.InnerClass)var1;
            return eq(this.thisClass, var2.thisClass) && eq(this.outerClass, var2.outerClass) && eq(this.name, var2.name) && this.flags == var2.flags;
         } else {
            return false;
         }
      }

      private static boolean eq(Object var0, Object var1) {
         return var0 == null ? var1 == null : var0.equals(var1);
      }

      public int hashCode() {
         return this.thisClass.hashCode();
      }

      public int compareTo(Package.InnerClass var1) {
         return this.thisClass.compareTo(var1.thisClass);
      }

      protected void visitRefs(int var1, Collection<ConstantPool.Entry> var2) {
         var2.add(this.thisClass);
         if (var1 == 0 || !this.predictable) {
            var2.add(this.outerClass);
            var2.add(this.name);
         }

      }

      public String toString() {
         return this.thisClass.stringValue();
      }
   }

   public final class File implements Comparable<Package.File> {
      String nameString;
      ConstantPool.Utf8Entry name;
      int modtime = 0;
      int options = 0;
      Package.Class stubClass;
      ArrayList<byte[]> prepend = new ArrayList();
      ByteArrayOutputStream append = new ByteArrayOutputStream();

      File(ConstantPool.Utf8Entry var2) {
         this.name = var2;
         this.nameString = var2.stringValue();
      }

      File(String var2) {
         var2 = Package.fixupFileName(var2);
         this.name = Package.getRefString(var2);
         this.nameString = this.name.stringValue();
      }

      public boolean isDirectory() {
         return this.nameString.endsWith("/");
      }

      public boolean isClassStub() {
         return (this.options & 2) != 0;
      }

      public Package.Class getStubClass() {
         assert this.isClassStub();

         assert this.stubClass != null;

         return this.stubClass;
      }

      public boolean isTrivialClassStub() {
         return this.isClassStub() && this.name.stringValue().equals("") && (this.modtime == 0 || this.modtime == Package.this.default_modtime) && (this.options & -3) == 0;
      }

      public boolean equals(Object var1) {
         if (var1 != null && var1.getClass() == Package.File.class) {
            Package.File var2 = (Package.File)var1;
            return var2.nameString.equals(this.nameString);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.nameString.hashCode();
      }

      public int compareTo(Package.File var1) {
         return this.nameString.compareTo(var1.nameString);
      }

      public String toString() {
         return this.nameString + "{" + (this.isClassStub() ? "*" : "") + (BandStructure.testBit(this.options, 1) ? "@" : "") + (this.modtime == 0 ? "" : "M" + this.modtime) + (this.getFileLength() == 0L ? "" : "[" + this.getFileLength() + "]") + "}";
      }

      public java.io.File getFileName() {
         return this.getFileName((java.io.File)null);
      }

      public java.io.File getFileName(java.io.File var1) {
         String var2 = this.nameString;
         String var3 = var2.replace('/', java.io.File.separatorChar);
         return new java.io.File(var1, var3);
      }

      public void addBytes(byte[] var1) {
         this.addBytes(var1, 0, var1.length);
      }

      public void addBytes(byte[] var1, int var2, int var3) {
         if ((this.append.size() | var3) << 2 < 0) {
            this.prepend.add(this.append.toByteArray());
            this.append.reset();
         }

         this.append.write(var1, var2, var3);
      }

      public long getFileLength() {
         long var1 = 0L;
         if (this.prepend != null && this.append != null) {
            byte[] var4;
            for(Iterator var3 = this.prepend.iterator(); var3.hasNext(); var1 += (long)var4.length) {
               var4 = (byte[])var3.next();
            }

            var1 += (long)this.append.size();
            return var1;
         } else {
            return 0L;
         }
      }

      public void writeTo(OutputStream var1) throws IOException {
         if (this.prepend != null && this.append != null) {
            Iterator var2 = this.prepend.iterator();

            while(var2.hasNext()) {
               byte[] var3 = (byte[])var2.next();
               var1.write(var3);
            }

            this.append.writeTo(var1);
         }
      }

      public void readFrom(InputStream var1) throws IOException {
         byte[] var2 = new byte[65536];

         int var3;
         while((var3 = var1.read(var2)) > 0) {
            this.addBytes(var2, 0, var3);
         }

      }

      public InputStream getInputStream() {
         ByteArrayInputStream var1 = new ByteArrayInputStream(this.append.toByteArray());
         if (this.prepend.isEmpty()) {
            return var1;
         } else {
            ArrayList var2 = new ArrayList(this.prepend.size() + 1);
            Iterator var3 = this.prepend.iterator();

            while(var3.hasNext()) {
               byte[] var4 = (byte[])var3.next();
               var2.add(new ByteArrayInputStream(var4));
            }

            var2.add(var1);
            return new SequenceInputStream(Collections.enumeration(var2));
         }
      }

      protected void visitRefs(int var1, Collection<ConstantPool.Entry> var2) {
         assert this.name != null;

         var2.add(this.name);
      }
   }

   public final class Class extends Attribute.Holder implements Comparable<Package.Class> {
      Package.File file;
      int magic;
      Package.Version version;
      ConstantPool.Entry[] cpMap;
      ConstantPool.ClassEntry thisClass;
      ConstantPool.ClassEntry superClass;
      ConstantPool.ClassEntry[] interfaces;
      ArrayList<Package.Class.Field> fields;
      ArrayList<Package.Class.Method> methods;
      ArrayList<Package.InnerClass> innerClasses;
      ArrayList<ConstantPool.BootstrapMethodEntry> bootstrapMethods;

      public Package getPackage() {
         return Package.this;
      }

      Class(int var2, ConstantPool.ClassEntry var3, ConstantPool.ClassEntry var4, ConstantPool.ClassEntry[] var5) {
         this.magic = -889275714;
         this.version = Package.this.defaultClassVersion;
         this.flags = var2;
         this.thisClass = var3;
         this.superClass = var4;
         this.interfaces = var5;
         boolean var6 = Package.this.classes.add(this);

         assert var6;

      }

      Class(String var2) {
         this.initFile(Package.this.newStub(var2));
      }

      List<Package.Class.Field> getFields() {
         return (List)(this.fields == null ? Package.noFields : this.fields);
      }

      List<Package.Class.Method> getMethods() {
         return (List)(this.methods == null ? Package.noMethods : this.methods);
      }

      public String getName() {
         return this.thisClass.stringValue();
      }

      Package.Version getVersion() {
         return this.version;
      }

      public int compareTo(Package.Class var1) {
         String var2 = this.getName();
         String var3 = var1.getName();
         return var2.compareTo(var3);
      }

      String getObviousSourceFile() {
         return Package.getObviousSourceFile(this.getName());
      }

      private void transformSourceFile(boolean var1) {
         Attribute var2 = this.getAttribute(Package.attrSourceFileSpecial);
         if (var2 != null) {
            String var3 = this.getObviousSourceFile();
            ArrayList var4 = new ArrayList(1);
            var2.visitRefs(this, 1, var4);
            ConstantPool.Utf8Entry var5 = (ConstantPool.Utf8Entry)var4.get(0);
            Attribute var6 = var2;
            if (var5 == null) {
               if (var1) {
                  var6 = Attribute.find(0, "SourceFile", "H");
                  var6 = var6.addContent(new byte[2]);
               } else {
                  byte[] var7 = new byte[2];
                  var5 = Package.getRefString(var3);
                  Object var8 = null;
                  var8 = Fixups.addRefWithBytes(var8, var7, var5);
                  var6 = Package.attrSourceFileSpecial.addContent(var7, var8);
               }
            } else if (var3.equals(var5.stringValue())) {
               if (var1) {
                  var6 = Package.attrSourceFileSpecial.addContent(new byte[2]);
               } else {
                  assert false;
               }
            }

            if (var6 != var2) {
               if (Package.this.verbose > 2) {
                  Utils.log.fine("recoding obvious SourceFile=" + var3);
               }

               ArrayList var9 = new ArrayList(this.getAttributes());
               int var10 = var9.indexOf(var2);
               var9.set(var10, var6);
               this.setAttributes(var9);
            }

         }
      }

      void minimizeSourceFile() {
         this.transformSourceFile(true);
      }

      void expandSourceFile() {
         this.transformSourceFile(false);
      }

      protected ConstantPool.Entry[] getCPMap() {
         return this.cpMap;
      }

      protected void setCPMap(ConstantPool.Entry[] var1) {
         this.cpMap = var1;
      }

      boolean hasBootstrapMethods() {
         return this.bootstrapMethods != null && !this.bootstrapMethods.isEmpty();
      }

      List<ConstantPool.BootstrapMethodEntry> getBootstrapMethods() {
         return this.bootstrapMethods;
      }

      ConstantPool.BootstrapMethodEntry[] getBootstrapMethodMap() {
         return this.hasBootstrapMethods() ? (ConstantPool.BootstrapMethodEntry[])this.bootstrapMethods.toArray(new ConstantPool.BootstrapMethodEntry[this.bootstrapMethods.size()]) : null;
      }

      void setBootstrapMethods(Collection<ConstantPool.BootstrapMethodEntry> var1) {
         assert this.bootstrapMethods == null;

         this.bootstrapMethods = new ArrayList(var1);
      }

      boolean hasInnerClasses() {
         return this.innerClasses != null;
      }

      List<Package.InnerClass> getInnerClasses() {
         return this.innerClasses;
      }

      public void setInnerClasses(Collection<Package.InnerClass> var1) {
         this.innerClasses = var1 == null ? null : new ArrayList(var1);
         Attribute var2 = this.getAttribute(Package.attrInnerClassesEmpty);
         if (this.innerClasses != null && var2 == null) {
            this.addAttribute(Package.attrInnerClassesEmpty.canonicalInstance());
         } else if (this.innerClasses == null && var2 != null) {
            this.removeAttribute(var2);
         }

      }

      public List<Package.InnerClass> computeGloballyImpliedICs() {
         HashSet var1 = new HashSet();
         ArrayList var2 = this.innerClasses;
         this.innerClasses = null;
         this.visitRefs(0, var1);
         this.innerClasses = var2;
         ConstantPool.completeReferencesIn(var1, true);
         HashSet var6 = new HashSet();
         Iterator var3 = var1.iterator();

         while(true) {
            Object var4;
            Package.InnerClass var5;
            do {
               if (!var3.hasNext()) {
                  ArrayList var7 = new ArrayList();
                  Iterator var8 = Package.this.allInnerClasses.iterator();

                  while(true) {
                     do {
                        if (!var8.hasNext()) {
                           return var7;
                        }

                        var5 = (Package.InnerClass)var8.next();
                     } while(!var6.contains(var5.thisClass) && var5.outerClass != this.thisClass);

                     if (Package.this.verbose > 1) {
                        Utils.log.fine("Relevant IC: " + var5);
                     }

                     var7.add(var5);
                  }
               }

               var4 = (ConstantPool.Entry)var3.next();
            } while(!(var4 instanceof ConstantPool.ClassEntry));

            while(var4 != null) {
               var5 = Package.this.getGlobalInnerClass((ConstantPool.Entry)var4);
               if (var5 == null || !var6.add(var4)) {
                  break;
               }

               var4 = var5.outerClass;
            }
         }
      }

      private List<Package.InnerClass> computeICdiff() {
         List var1 = this.computeGloballyImpliedICs();
         List var2 = this.getInnerClasses();
         if (var2 == null) {
            var2 = Collections.emptyList();
         }

         if (var2.isEmpty()) {
            return var1;
         } else if (var1.isEmpty()) {
            return var2;
         } else {
            HashSet var3 = new HashSet(var2);
            var3.retainAll(new HashSet(var1));
            var1.addAll(var2);
            var1.removeAll(var3);
            return var1;
         }
      }

      void minimizeLocalICs() {
         List var1 = this.computeICdiff();
         ArrayList var2 = this.innerClasses;
         List var3;
         if (var1.isEmpty()) {
            var3 = null;
            if (var2 != null && var2.isEmpty() && Package.this.verbose > 0) {
               Utils.log.info("Warning: Dropping empty InnerClasses attribute from " + this);
            }
         } else if (var2 == null) {
            var3 = Collections.emptyList();
         } else {
            var3 = var1;
         }

         this.setInnerClasses(var3);
         if (Package.this.verbose > 1 && var3 != null) {
            Utils.log.fine("keeping local ICs in " + this + ": " + var3);
         }

      }

      int expandLocalICs() {
         ArrayList var1 = this.innerClasses;
         List var2;
         int var3;
         if (var1 == null) {
            List var4 = this.computeGloballyImpliedICs();
            if (var4.isEmpty()) {
               var2 = null;
               var3 = 0;
            } else {
               var2 = var4;
               var3 = 1;
            }
         } else if (var1.isEmpty()) {
            var2 = null;
            var3 = 0;
         } else {
            var2 = this.computeICdiff();
            var3 = var2.containsAll(var1) ? 1 : -1;
         }

         this.setInnerClasses(var2);
         return var3;
      }

      public void trimToSize() {
         super.trimToSize();

         for(int var1 = 0; var1 <= 1; ++var1) {
            ArrayList var2 = var1 == 0 ? this.fields : this.methods;
            if (var2 != null) {
               var2.trimToSize();
               Iterator var3 = var2.iterator();

               while(var3.hasNext()) {
                  Package.Class.Member var4 = (Package.Class.Member)var3.next();
                  var4.trimToSize();
               }
            }
         }

         if (this.innerClasses != null) {
            this.innerClasses.trimToSize();
         }

      }

      public void strip(String var1) {
         if ("InnerClass".equals(var1)) {
            this.innerClasses = null;
         }

         for(int var2 = 0; var2 <= 1; ++var2) {
            ArrayList var3 = var2 == 0 ? this.fields : this.methods;
            if (var3 != null) {
               Iterator var4 = var3.iterator();

               while(var4.hasNext()) {
                  Package.Class.Member var5 = (Package.Class.Member)var4.next();
                  var5.strip(var1);
               }
            }
         }

         super.strip(var1);
      }

      protected void visitRefs(int var1, Collection<ConstantPool.Entry> var2) {
         if (Package.this.verbose > 2) {
            Utils.log.fine("visitRefs " + this);
         }

         var2.add(this.thisClass);
         var2.add(this.superClass);
         var2.addAll(Arrays.asList(this.interfaces));

         for(int var3 = 0; var3 <= 1; ++var3) {
            ArrayList var4 = var3 == 0 ? this.fields : this.methods;
            if (var4 != null) {
               Iterator var5 = var4.iterator();

               while(var5.hasNext()) {
                  Package.Class.Member var6 = (Package.Class.Member)var5.next();
                  boolean var7 = false;

                  try {
                     var6.visitRefs(var1, var2);
                     var7 = true;
                  } finally {
                     if (!var7) {
                        Utils.log.warning("Error scanning " + var6);
                     }

                  }
               }
            }
         }

         this.visitInnerClassRefs(var1, var2);
         super.visitRefs(var1, var2);
      }

      protected void visitInnerClassRefs(int var1, Collection<ConstantPool.Entry> var2) {
         Package.visitInnerClassRefs(this.innerClasses, var1, var2);
      }

      void finishReading() {
         this.trimToSize();
         this.maybeChooseFileName();
      }

      public void initFile(Package.File var1) {
         assert this.file == null;

         if (var1 == null) {
            var1 = Package.this.newStub(this.canonicalFileName());
         }

         this.file = var1;

         assert var1.isClassStub();

         var1.stubClass = this;
         this.maybeChooseFileName();
      }

      public void maybeChooseFileName() {
         if (this.thisClass != null) {
            String var1 = this.canonicalFileName();
            if (this.file.nameString.equals("")) {
               this.file.nameString = var1;
            }

            if (this.file.nameString.equals(var1)) {
               this.file.name = Package.getRefString("");
            } else {
               if (this.file.name == null) {
                  this.file.name = Package.getRefString(this.file.nameString);
               }

            }
         }
      }

      public String canonicalFileName() {
         return this.thisClass == null ? null : this.thisClass.stringValue() + ".class";
      }

      public java.io.File getFileName(java.io.File var1) {
         String var2 = this.file.name.stringValue();
         if (var2.equals("")) {
            var2 = this.canonicalFileName();
         }

         String var3 = var2.replace('/', java.io.File.separatorChar);
         return new java.io.File(var1, var3);
      }

      public java.io.File getFileName() {
         return this.getFileName((java.io.File)null);
      }

      public String toString() {
         return this.thisClass.stringValue();
      }

      public class Method extends Package.Class.Member {
         Code code;

         public Method(int var2, ConstantPool.DescriptorEntry var3) {
            super(var2, var3);

            assert var3.isMethod();

            if (Class.this.methods == null) {
               Class.this.methods = new ArrayList();
            }

            boolean var4 = Class.this.methods.add(this);

            assert var4;

         }

         public void trimToSize() {
            super.trimToSize();
            if (this.code != null) {
               this.code.trimToSize();
            }

         }

         public int getArgumentSize() {
            int var1 = this.descriptor.typeRef.computeSize(true);
            int var2 = Modifier.isStatic(this.flags) ? 0 : 1;
            return var2 + var1;
         }

         public int compareTo(Package.Class.Member var1) {
            Package.Class.Method var2 = (Package.Class.Method)var1;
            return this.getDescriptor().compareTo(var2.getDescriptor());
         }

         public void strip(String var1) {
            if ("Code".equals(var1)) {
               this.code = null;
            }

            if (this.code != null) {
               this.code.strip(var1);
            }

            super.strip(var1);
         }

         protected void visitRefs(int var1, Collection<ConstantPool.Entry> var2) {
            super.visitRefs(var1, var2);
            if (this.code != null) {
               if (var1 == 0) {
                  var2.add(Package.getRefString("Code"));
               }

               this.code.visitRefs(var1, var2);
            }

         }
      }

      public class Field extends Package.Class.Member {
         int order;

         public Field(int var2, ConstantPool.DescriptorEntry var3) {
            super(var2, var3);

            assert !var3.isMethod();

            if (Class.this.fields == null) {
               Class.this.fields = new ArrayList();
            }

            boolean var4 = Class.this.fields.add(this);

            assert var4;

            this.order = Class.this.fields.size();
         }

         public byte getLiteralTag() {
            return this.descriptor.getLiteralTag();
         }

         public int compareTo(Package.Class.Member var1) {
            Package.Class.Field var2 = (Package.Class.Field)var1;
            return this.order - var2.order;
         }
      }

      public abstract class Member extends Attribute.Holder implements Comparable<Package.Class.Member> {
         ConstantPool.DescriptorEntry descriptor;

         protected Member(int var2, ConstantPool.DescriptorEntry var3) {
            this.flags = var2;
            this.descriptor = var3;
         }

         public Package.Class thisClass() {
            return Class.this;
         }

         public ConstantPool.DescriptorEntry getDescriptor() {
            return this.descriptor;
         }

         public String getName() {
            return this.descriptor.nameRef.stringValue();
         }

         public String getType() {
            return this.descriptor.typeRef.stringValue();
         }

         protected ConstantPool.Entry[] getCPMap() {
            return Class.this.cpMap;
         }

         protected void visitRefs(int var1, Collection<ConstantPool.Entry> var2) {
            if (Package.this.verbose > 2) {
               Utils.log.fine("visitRefs " + this);
            }

            if (var1 == 0) {
               var2.add(this.descriptor.nameRef);
               var2.add(this.descriptor.typeRef);
            } else {
               var2.add(this.descriptor);
            }

            super.visitRefs(var1, var2);
         }

         public String toString() {
            return Class.this + "." + this.descriptor.prettyString();
         }
      }
   }
}
