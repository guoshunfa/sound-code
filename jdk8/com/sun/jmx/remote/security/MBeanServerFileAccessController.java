package com.sun.jmx.remote.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.security.auth.Subject;

public class MBeanServerFileAccessController extends MBeanServerAccessController {
   static final String READONLY = "readonly";
   static final String READWRITE = "readwrite";
   static final String CREATE = "create";
   static final String UNREGISTER = "unregister";
   private Map<String, MBeanServerFileAccessController.Access> accessMap;
   private Properties originalProps;
   private String accessFileName;

   public MBeanServerFileAccessController(String var1) throws IOException {
      this.accessFileName = var1;
      Properties var2 = propertiesFromFile(var1);
      this.parseProperties(var2);
   }

   public MBeanServerFileAccessController(String var1, MBeanServer var2) throws IOException {
      this(var1);
      this.setMBeanServer(var2);
   }

   public MBeanServerFileAccessController(Properties var1) throws IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("Null properties");
      } else {
         this.originalProps = var1;
         this.parseProperties(var1);
      }
   }

   public MBeanServerFileAccessController(Properties var1, MBeanServer var2) throws IOException {
      this(var1);
      this.setMBeanServer(var2);
   }

   public void checkRead() {
      this.checkAccess(MBeanServerFileAccessController.AccessType.READ, (String)null);
   }

   public void checkWrite() {
      this.checkAccess(MBeanServerFileAccessController.AccessType.WRITE, (String)null);
   }

   public void checkCreate(String var1) {
      this.checkAccess(MBeanServerFileAccessController.AccessType.CREATE, var1);
   }

   public void checkUnregister(ObjectName var1) {
      this.checkAccess(MBeanServerFileAccessController.AccessType.UNREGISTER, (String)null);
   }

   public synchronized void refresh() throws IOException {
      Properties var1;
      if (this.accessFileName == null) {
         var1 = this.originalProps;
      } else {
         var1 = propertiesFromFile(this.accessFileName);
      }

      this.parseProperties(var1);
   }

   private static Properties propertiesFromFile(String var0) throws IOException {
      FileInputStream var1 = new FileInputStream(var0);

      Properties var3;
      try {
         Properties var2 = new Properties();
         var2.load((InputStream)var1);
         var3 = var2;
      } finally {
         var1.close();
      }

      return var3;
   }

   private synchronized void checkAccess(MBeanServerFileAccessController.AccessType var1, String var2) {
      final AccessControlContext var3 = AccessController.getContext();
      Subject var4 = (Subject)AccessController.doPrivileged(new PrivilegedAction<Subject>() {
         public Subject run() {
            return Subject.getSubject(var3);
         }
      });
      if (var4 != null) {
         Set var5 = var4.getPrincipals();
         String var6 = null;
         Iterator var7 = var5.iterator();

         while(var7.hasNext()) {
            Principal var8 = (Principal)var7.next();
            MBeanServerFileAccessController.Access var9 = (MBeanServerFileAccessController.Access)this.accessMap.get(var8.getName());
            if (var9 != null) {
               boolean var10;
               switch(var1) {
               case READ:
                  var10 = true;
                  break;
               case WRITE:
                  var10 = var9.write;
                  break;
               case UNREGISTER:
                  var10 = var9.unregister;
                  if (!var10 && var9.write) {
                     var6 = "unregister";
                  }
                  break;
               case CREATE:
                  var10 = checkCreateAccess(var9, var2);
                  if (!var10 && var9.write) {
                     var6 = "create " + var2;
                  }
                  break;
               default:
                  throw new AssertionError();
               }

               if (var10) {
                  return;
               }
            }
         }

         SecurityException var11 = new SecurityException("Access denied! Invalid access level for requested MBeanServer operation.");
         if (var6 != null) {
            SecurityException var12 = new SecurityException("Access property for this identity should be similar to: readwrite " + var6);
            var11.initCause(var12);
         }

         throw var11;
      }
   }

   private static boolean checkCreateAccess(MBeanServerFileAccessController.Access var0, String var1) {
      String[] var2 = var0.createPatterns;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (classNameMatch(var5, var1)) {
            return true;
         }
      }

      return false;
   }

   private static boolean classNameMatch(String var0, String var1) {
      StringBuilder var2 = new StringBuilder();
      StringTokenizer var3 = new StringTokenizer(var0, "*", true);

      while(var3.hasMoreTokens()) {
         String var4 = var3.nextToken();
         if (var4.equals("*")) {
            var2.append("[^.]*");
         } else {
            var2.append(Pattern.quote(var4));
         }
      }

      return var1.matches(var2.toString());
   }

   private void parseProperties(Properties var1) {
      this.accessMap = new HashMap();
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         String var4 = (String)var3.getKey();
         String var5 = (String)var3.getValue();
         MBeanServerFileAccessController.Access var6 = MBeanServerFileAccessController.Parser.parseAccess(var4, var5);
         this.accessMap.put(var4, var6);
      }

   }

   private static class Parser {
      private static final int EOS = -1;
      private final String identity;
      private final String s;
      private final int len;
      private int i;
      private int c;

      private Parser(String var1, String var2) {
         this.identity = var1;
         this.s = var2;
         this.len = var2.length();
         this.i = 0;
         if (this.i < this.len) {
            this.c = var2.codePointAt(this.i);
         } else {
            this.c = -1;
         }

      }

      static MBeanServerFileAccessController.Access parseAccess(String var0, String var1) {
         return (new MBeanServerFileAccessController.Parser(var0, var1)).parseAccess();
      }

      private MBeanServerFileAccessController.Access parseAccess() {
         this.skipSpace();
         String var1 = this.parseWord();
         MBeanServerFileAccessController.Access var2;
         if (var1.equals("readonly")) {
            var2 = new MBeanServerFileAccessController.Access(false, false, (List)null);
         } else {
            if (!var1.equals("readwrite")) {
               throw this.syntax("Expected readonly or readwrite: " + var1);
            }

            var2 = this.parseReadWrite();
         }

         if (this.c != -1) {
            throw this.syntax("Extra text at end of line");
         } else {
            return var2;
         }
      }

      private MBeanServerFileAccessController.Access parseReadWrite() {
         ArrayList var1 = new ArrayList();
         boolean var2 = false;

         while(true) {
            this.skipSpace();
            if (this.c == -1) {
               return new MBeanServerFileAccessController.Access(true, var2, var1);
            }

            String var3 = this.parseWord();
            if (var3.equals("unregister")) {
               var2 = true;
            } else {
               if (!var3.equals("create")) {
                  throw this.syntax("Unrecognized keyword " + var3);
               }

               this.parseCreate(var1);
            }
         }
      }

      private void parseCreate(List<String> var1) {
         while(true) {
            this.skipSpace();
            var1.add(this.parseClassName());
            this.skipSpace();
            if (this.c != 44) {
               return;
            }

            this.next();
         }
      }

      private String parseClassName() {
         int var1 = this.i;
         boolean var2 = false;

         while(true) {
            if (this.c == 46) {
               if (!var2) {
                  throw this.syntax("Bad . in class name");
               }

               var2 = false;
            } else {
               if (this.c != 42 && !Character.isJavaIdentifierPart(this.c)) {
                  String var3 = this.s.substring(var1, this.i);
                  if (!var2) {
                     throw this.syntax("Bad class name " + var3);
                  }

                  return var3;
               }

               var2 = true;
            }

            this.next();
         }
      }

      private void next() {
         if (this.c != -1) {
            this.i += Character.charCount(this.c);
            if (this.i < this.len) {
               this.c = this.s.codePointAt(this.i);
            } else {
               this.c = -1;
            }
         }

      }

      private void skipSpace() {
         while(Character.isWhitespace(this.c)) {
            this.next();
         }

      }

      private String parseWord() {
         this.skipSpace();
         if (this.c == -1) {
            throw this.syntax("Expected word at end of line");
         } else {
            int var1 = this.i;

            while(this.c != -1 && !Character.isWhitespace(this.c)) {
               this.next();
            }

            String var2 = this.s.substring(var1, this.i);
            this.skipSpace();
            return var2;
         }
      }

      private IllegalArgumentException syntax(String var1) {
         return new IllegalArgumentException(var1 + " [" + this.identity + " " + this.s + "]");
      }

      static {
         assert !Character.isWhitespace((int)-1);

      }
   }

   private static class Access {
      final boolean write;
      final String[] createPatterns;
      private boolean unregister;
      private final String[] NO_STRINGS = new String[0];

      Access(boolean var1, boolean var2, List<String> var3) {
         this.write = var1;
         int var4 = var3 == null ? 0 : var3.size();
         if (var4 == 0) {
            this.createPatterns = this.NO_STRINGS;
         } else {
            this.createPatterns = (String[])var3.toArray(new String[var4]);
         }

         this.unregister = var2;
      }
   }

   private static enum AccessType {
      READ,
      WRITE,
      CREATE,
      UNREGISTER;
   }
}
