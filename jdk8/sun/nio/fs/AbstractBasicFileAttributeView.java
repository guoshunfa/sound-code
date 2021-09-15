package sun.nio.fs;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

abstract class AbstractBasicFileAttributeView implements BasicFileAttributeView, DynamicFileAttributeView {
   private static final String SIZE_NAME = "size";
   private static final String CREATION_TIME_NAME = "creationTime";
   private static final String LAST_ACCESS_TIME_NAME = "lastAccessTime";
   private static final String LAST_MODIFIED_TIME_NAME = "lastModifiedTime";
   private static final String FILE_KEY_NAME = "fileKey";
   private static final String IS_DIRECTORY_NAME = "isDirectory";
   private static final String IS_REGULAR_FILE_NAME = "isRegularFile";
   private static final String IS_SYMBOLIC_LINK_NAME = "isSymbolicLink";
   private static final String IS_OTHER_NAME = "isOther";
   static final Set<String> basicAttributeNames = Util.newSet("size", "creationTime", "lastAccessTime", "lastModifiedTime", "fileKey", "isDirectory", "isRegularFile", "isSymbolicLink", "isOther");

   protected AbstractBasicFileAttributeView() {
   }

   public String name() {
      return "basic";
   }

   public void setAttribute(String var1, Object var2) throws IOException {
      if (var1.equals("lastModifiedTime")) {
         this.setTimes((FileTime)var2, (FileTime)null, (FileTime)null);
      } else if (var1.equals("lastAccessTime")) {
         this.setTimes((FileTime)null, (FileTime)var2, (FileTime)null);
      } else if (var1.equals("creationTime")) {
         this.setTimes((FileTime)null, (FileTime)null, (FileTime)var2);
      } else {
         throw new IllegalArgumentException("'" + this.name() + ":" + var1 + "' not recognized");
      }
   }

   final void addRequestedBasicAttributes(BasicFileAttributes var1, AbstractBasicFileAttributeView.AttributesBuilder var2) {
      if (var2.match("size")) {
         var2.add("size", var1.size());
      }

      if (var2.match("creationTime")) {
         var2.add("creationTime", var1.creationTime());
      }

      if (var2.match("lastAccessTime")) {
         var2.add("lastAccessTime", var1.lastAccessTime());
      }

      if (var2.match("lastModifiedTime")) {
         var2.add("lastModifiedTime", var1.lastModifiedTime());
      }

      if (var2.match("fileKey")) {
         var2.add("fileKey", var1.fileKey());
      }

      if (var2.match("isDirectory")) {
         var2.add("isDirectory", var1.isDirectory());
      }

      if (var2.match("isRegularFile")) {
         var2.add("isRegularFile", var1.isRegularFile());
      }

      if (var2.match("isSymbolicLink")) {
         var2.add("isSymbolicLink", var1.isSymbolicLink());
      }

      if (var2.match("isOther")) {
         var2.add("isOther", var1.isOther());
      }

   }

   public Map<String, Object> readAttributes(String[] var1) throws IOException {
      AbstractBasicFileAttributeView.AttributesBuilder var2 = AbstractBasicFileAttributeView.AttributesBuilder.create(basicAttributeNames, var1);
      this.addRequestedBasicAttributes(this.readAttributes(), var2);
      return var2.unmodifiableMap();
   }

   static class AttributesBuilder {
      private Set<String> names = new HashSet();
      private Map<String, Object> map = new HashMap();
      private boolean copyAll;

      private AttributesBuilder(Set<String> var1, String[] var2) {
         String[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            if (var6.equals("*")) {
               this.copyAll = true;
            } else {
               if (!var1.contains(var6)) {
                  throw new IllegalArgumentException("'" + var6 + "' not recognized");
               }

               this.names.add(var6);
            }
         }

      }

      static AbstractBasicFileAttributeView.AttributesBuilder create(Set<String> var0, String[] var1) {
         return new AbstractBasicFileAttributeView.AttributesBuilder(var0, var1);
      }

      boolean match(String var1) {
         return this.copyAll || this.names.contains(var1);
      }

      void add(String var1, Object var2) {
         this.map.put(var1, var2);
      }

      Map<String, Object> unmodifiableMap() {
         return Collections.unmodifiableMap(this.map);
      }
   }
}
