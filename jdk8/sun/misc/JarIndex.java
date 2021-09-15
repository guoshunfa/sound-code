package sun.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import sun.security.action.GetPropertyAction;

public class JarIndex {
   private HashMap<String, LinkedList<String>> indexMap;
   private HashMap<String, LinkedList<String>> jarMap;
   private String[] jarFiles;
   public static final String INDEX_NAME = "META-INF/INDEX.LIST";
   private static final boolean metaInfFilenames = "true".equals(AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.misc.JarIndex.metaInfFilenames"))));

   public JarIndex() {
      this.indexMap = new HashMap();
      this.jarMap = new HashMap();
   }

   public JarIndex(InputStream var1) throws IOException {
      this();
      this.read(var1);
   }

   public JarIndex(String[] var1) throws IOException {
      this();
      this.jarFiles = var1;
      this.parseJars(var1);
   }

   public static JarIndex getJarIndex(JarFile var0) throws IOException {
      return getJarIndex(var0, (MetaIndex)null);
   }

   public static JarIndex getJarIndex(JarFile var0, MetaIndex var1) throws IOException {
      JarIndex var2 = null;
      if (var1 != null && !var1.mayContain("META-INF/INDEX.LIST")) {
         return null;
      } else {
         JarEntry var3 = var0.getJarEntry("META-INF/INDEX.LIST");
         if (var3 != null) {
            var2 = new JarIndex(var0.getInputStream(var3));
         }

         return var2;
      }
   }

   public String[] getJarFiles() {
      return this.jarFiles;
   }

   private void addToList(String var1, String var2, HashMap<String, LinkedList<String>> var3) {
      LinkedList var4 = (LinkedList)var3.get(var1);
      if (var4 == null) {
         var4 = new LinkedList();
         var4.add(var2);
         var3.put(var1, var4);
      } else if (!var4.contains(var2)) {
         var4.add(var2);
      }

   }

   public LinkedList<String> get(String var1) {
      LinkedList var2 = null;
      int var3;
      if ((var2 = (LinkedList)this.indexMap.get(var1)) == null && (var3 = var1.lastIndexOf("/")) != -1) {
         var2 = (LinkedList)this.indexMap.get(var1.substring(0, var3));
      }

      return var2;
   }

   public void add(String var1, String var2) {
      String var3;
      int var4;
      if ((var4 = var1.lastIndexOf("/")) != -1) {
         var3 = var1.substring(0, var4);
      } else {
         var3 = var1;
      }

      this.addMapping(var3, var2);
   }

   private void addMapping(String var1, String var2) {
      this.addToList(var1, var2, this.indexMap);
      this.addToList(var2, var1, this.jarMap);
   }

   private void parseJars(String[] var1) throws IOException {
      if (var1 != null) {
         String var2 = null;

         label51:
         for(int var3 = 0; var3 < var1.length; ++var3) {
            var2 = var1[var3];
            ZipFile var4 = new ZipFile(var2.replace('/', File.separatorChar));
            Enumeration var5 = var4.entries();

            while(true) {
               while(true) {
                  ZipEntry var6;
                  String var7;
                  do {
                     do {
                        do {
                           if (!var5.hasMoreElements()) {
                              var4.close();
                              continue label51;
                           }

                           var6 = (ZipEntry)var5.nextElement();
                           var7 = var6.getName();
                        } while(var7.equals("META-INF/"));
                     } while(var7.equals("META-INF/INDEX.LIST"));
                  } while(var7.equals("META-INF/MANIFEST.MF"));

                  if (metaInfFilenames && var7.startsWith("META-INF/")) {
                     if (!var6.isDirectory()) {
                        this.addMapping(var7, var2);
                     }
                  } else {
                     this.add(var7, var2);
                  }
               }
            }
         }

      }
   }

   public void write(OutputStream var1) throws IOException {
      BufferedWriter var2 = new BufferedWriter(new OutputStreamWriter(var1, "UTF8"));
      var2.write("JarIndex-Version: 1.0\n\n");
      if (this.jarFiles != null) {
         for(int var3 = 0; var3 < this.jarFiles.length; ++var3) {
            String var4 = this.jarFiles[var3];
            var2.write(var4 + "\n");
            LinkedList var5 = (LinkedList)this.jarMap.get(var4);
            if (var5 != null) {
               Iterator var6 = var5.iterator();

               while(var6.hasNext()) {
                  var2.write((String)var6.next() + "\n");
               }
            }

            var2.write("\n");
         }

         var2.flush();
      }

   }

   public void read(InputStream var1) throws IOException {
      BufferedReader var2 = new BufferedReader(new InputStreamReader(var1, "UTF8"));
      String var3 = null;
      String var4 = null;
      Vector var5 = new Vector();

      while((var3 = var2.readLine()) != null && !var3.endsWith(".jar")) {
      }

      for(; var3 != null; var3 = var2.readLine()) {
         if (var3.length() != 0) {
            if (var3.endsWith(".jar")) {
               var4 = var3;
               var5.add(var3);
            } else {
               this.addMapping(var3, var4);
            }
         }
      }

      this.jarFiles = (String[])var5.toArray(new String[var5.size()]);
   }

   public void merge(JarIndex var1, String var2) {
      Iterator var3 = this.indexMap.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         String var5 = (String)var4.getKey();
         LinkedList var6 = (LinkedList)var4.getValue();

         String var8;
         for(Iterator var7 = var6.iterator(); var7.hasNext(); var1.addMapping(var5, var8)) {
            var8 = (String)var7.next();
            if (var2 != null) {
               var8 = var2.concat(var8);
            }
         }
      }

   }
}
