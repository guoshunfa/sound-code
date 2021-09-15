package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.orbutil.ObjectUtility;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class IDLNameTranslatorImpl implements IDLNameTranslator {
   private static String[] IDL_KEYWORDS = new String[]{"abstract", "any", "attribute", "boolean", "case", "char", "const", "context", "custom", "default", "double", "enum", "exception", "factory", "FALSE", "fixed", "float", "in", "inout", "interface", "long", "module", "native", "Object", "octet", "oneway", "out", "private", "public", "raises", "readonly", "sequence", "short", "string", "struct", "supports", "switch", "TRUE", "truncatable", "typedef", "unsigned", "union", "ValueBase", "valuetype", "void", "wchar", "wstring"};
   private static char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
   private static final String UNDERSCORE = "_";
   private static final String INNER_CLASS_SEPARATOR = "__";
   private static final String[] BASE_IDL_ARRAY_MODULE_TYPE = new String[]{"org", "omg", "boxedRMI"};
   private static final String BASE_IDL_ARRAY_ELEMENT_TYPE = "seq";
   private static final String LEADING_UNDERSCORE_CHAR = "J";
   private static final String ID_CONTAINER_CLASH_CHAR = "_";
   private static final String OVERLOADED_TYPE_SEPARATOR = "__";
   private static final String ATTRIBUTE_METHOD_CLASH_MANGLE_CHARS = "__";
   private static final String GET_ATTRIBUTE_PREFIX = "_get_";
   private static final String SET_ATTRIBUTE_PREFIX = "_set_";
   private static final String IS_ATTRIBUTE_PREFIX = "_get_";
   private static Set idlKeywords_ = new HashSet();
   private Class[] interf_;
   private Map methodToIDLNameMap_;
   private Map IDLNameToMethodMap_;
   private Method[] methods_;

   public static IDLNameTranslator get(Class var0) {
      return new IDLNameTranslatorImpl(new Class[]{var0});
   }

   public static IDLNameTranslator get(Class[] var0) {
      return new IDLNameTranslatorImpl(var0);
   }

   public static String getExceptionId(Class var0) {
      IDLType var1 = classToIDLType(var0);
      return var1.getExceptionName();
   }

   public Class[] getInterfaces() {
      return this.interf_;
   }

   public Method[] getMethods() {
      return this.methods_;
   }

   public Method getMethod(String var1) {
      return (Method)this.IDLNameToMethodMap_.get(var1);
   }

   public String getIDLName(Method var1) {
      return (String)this.methodToIDLNameMap_.get(var1);
   }

   private IDLNameTranslatorImpl(Class[] var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(new DynamicAccessPermission("access"));
      }

      try {
         IDLTypesUtil var3 = new IDLTypesUtil();

         for(int var7 = 0; var7 < var1.length; ++var7) {
            var3.validateRemoteInterface(var1[var7]);
         }

         this.interf_ = var1;
         this.buildNameTranslation();
      } catch (IDLTypeException var6) {
         String var4 = var6.getMessage();
         IllegalStateException var5 = new IllegalStateException(var4);
         var5.initCause(var6);
         throw var5;
      }
   }

   private void buildNameTranslation() {
      HashMap var1 = new HashMap();

      int var2;
      Class var3;
      for(var2 = 0; var2 < this.interf_.length; ++var2) {
         var3 = this.interf_[var2];
         IDLTypesUtil var4 = new IDLTypesUtil();
         final Method[] var5 = var3.getMethods();
         AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               Method.setAccessible(var5, true);
               return null;
            }
         });

         for(int var6 = 0; var6 < var5.length; ++var6) {
            Method var7 = var5[var6];
            IDLNameTranslatorImpl.IDLMethodInfo var8 = new IDLNameTranslatorImpl.IDLMethodInfo();
            var8.method = var7;
            if (var4.isPropertyAccessorMethod(var7, var3)) {
               var8.isProperty = true;
               String var9 = var4.getAttributeNameForProperty(var7.getName());
               var8.originalName = var9;
               var8.mangledName = var9;
            } else {
               var8.isProperty = false;
               var8.originalName = var7.getName();
               var8.mangledName = var7.getName();
            }

            var1.put(var7, var8);
         }
      }

      Iterator var10 = var1.values().iterator();

      while(true) {
         IDLNameTranslatorImpl.IDLMethodInfo var11;
         Iterator var12;
         IDLNameTranslatorImpl.IDLMethodInfo var13;
         while(var10.hasNext()) {
            var11 = (IDLNameTranslatorImpl.IDLMethodInfo)var10.next();
            var12 = var1.values().iterator();

            while(var12.hasNext()) {
               var13 = (IDLNameTranslatorImpl.IDLMethodInfo)var12.next();
               if (var11 != var13 && !var11.originalName.equals(var13.originalName) && var11.originalName.equalsIgnoreCase(var13.originalName)) {
                  var11.mangledName = this.mangleCaseSensitiveCollision(var11.originalName);
                  break;
               }
            }
         }

         for(var10 = var1.values().iterator(); var10.hasNext(); var11.mangledName = mangleIdentifier(var11.mangledName, var11.isProperty)) {
            var11 = (IDLNameTranslatorImpl.IDLMethodInfo)var10.next();
         }

         var10 = var1.values().iterator();

         while(true) {
            while(true) {
               do {
                  if (!var10.hasNext()) {
                     var10 = var1.values().iterator();

                     while(true) {
                        while(true) {
                           do {
                              if (!var10.hasNext()) {
                                 String var14;
                                 for(var2 = 0; var2 < this.interf_.length; ++var2) {
                                    var3 = this.interf_[var2];
                                    var14 = getMappedContainerName(var3);
                                    Iterator var15 = var1.values().iterator();

                                    while(var15.hasNext()) {
                                       IDLNameTranslatorImpl.IDLMethodInfo var16 = (IDLNameTranslatorImpl.IDLMethodInfo)var15.next();
                                       if (!var16.isProperty && identifierClashesWithContainer(var14, var16.mangledName)) {
                                          var16.mangledName = mangleContainerClash(var16.mangledName);
                                       }
                                    }
                                 }

                                 this.methodToIDLNameMap_ = new HashMap();
                                 this.IDLNameToMethodMap_ = new HashMap();
                                 this.methods_ = (Method[])((Method[])var1.keySet().toArray(new Method[0]));
                                 var10 = var1.values().iterator();

                                 while(var10.hasNext()) {
                                    var11 = (IDLNameTranslatorImpl.IDLMethodInfo)var10.next();
                                    var14 = var11.mangledName;
                                    if (var11.isProperty) {
                                       String var17 = var11.method.getName();
                                       String var18 = "";
                                       if (var17.startsWith("get")) {
                                          var18 = "_get_";
                                       } else if (var17.startsWith("set")) {
                                          var18 = "_set_";
                                       } else {
                                          var18 = "_get_";
                                       }

                                       var14 = var18 + var11.mangledName;
                                    }

                                    this.methodToIDLNameMap_.put(var11.method, var14);
                                    if (this.IDLNameToMethodMap_.containsKey(var14)) {
                                       Method var19 = (Method)this.IDLNameToMethodMap_.get(var14);
                                       throw new IllegalStateException("Error : methods " + var19 + " and " + var11.method + " both result in IDL name '" + var14 + "'");
                                    }

                                    this.IDLNameToMethodMap_.put(var14, var11.method);
                                 }

                                 return;
                              }

                              var11 = (IDLNameTranslatorImpl.IDLMethodInfo)var10.next();
                           } while(!var11.isProperty);

                           var12 = var1.values().iterator();

                           while(var12.hasNext()) {
                              var13 = (IDLNameTranslatorImpl.IDLMethodInfo)var12.next();
                              if (var11 != var13 && !var13.isProperty && var11.mangledName.equals(var13.mangledName)) {
                                 var11.mangledName = var11.mangledName + "__";
                                 break;
                              }
                           }
                        }
                     }
                  }

                  var11 = (IDLNameTranslatorImpl.IDLMethodInfo)var10.next();
               } while(var11.isProperty);

               var12 = var1.values().iterator();

               while(var12.hasNext()) {
                  var13 = (IDLNameTranslatorImpl.IDLMethodInfo)var12.next();
                  if (var11 != var13 && !var13.isProperty && var11.originalName.equals(var13.originalName)) {
                     var11.mangledName = mangleOverloadedMethod(var11.mangledName, var11.method);
                     break;
                  }
               }
            }
         }
      }
   }

   private static String mangleIdentifier(String var0) {
      return mangleIdentifier(var0, false);
   }

   private static String mangleIdentifier(String var0, boolean var1) {
      String var2 = var0;
      if (hasLeadingUnderscore(var0)) {
         var2 = mangleLeadingUnderscore(var0);
      }

      if (!var1 && isIDLKeyword(var2)) {
         var2 = mangleIDLKeywordClash(var2);
      }

      if (!isIDLIdentifier(var2)) {
         var2 = mangleUnicodeChars(var2);
      }

      return var2;
   }

   static boolean isIDLKeyword(String var0) {
      String var1 = var0.toUpperCase();
      return idlKeywords_.contains(var1);
   }

   static String mangleIDLKeywordClash(String var0) {
      return "_" + var0;
   }

   private static String mangleLeadingUnderscore(String var0) {
      return "J" + var0;
   }

   private static boolean hasLeadingUnderscore(String var0) {
      return var0.startsWith("_");
   }

   static String mangleUnicodeChars(String var0) {
      StringBuffer var1 = new StringBuffer();

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         if (isIDLIdentifierChar(var3)) {
            var1.append(var3);
         } else {
            String var4 = charToUnicodeRepresentation(var3);
            var1.append(var4);
         }
      }

      return var1.toString();
   }

   String mangleCaseSensitiveCollision(String var1) {
      StringBuffer var2 = new StringBuffer(var1);
      var2.append("_");
      boolean var3 = false;

      for(int var4 = 0; var4 < var1.length(); ++var4) {
         char var5 = var1.charAt(var4);
         if (Character.isUpperCase(var5)) {
            if (var3) {
               var2.append("_");
            }

            var2.append(var4);
            var3 = true;
         }
      }

      return var2.toString();
   }

   private static String mangleContainerClash(String var0) {
      return var0 + "_";
   }

   private static boolean identifierClashesWithContainer(String var0, String var1) {
      return var1.equalsIgnoreCase(var0);
   }

   public static String charToUnicodeRepresentation(char var0) {
      StringBuffer var2 = new StringBuffer();

      int var4;
      int var5;
      for(int var3 = var0; var3 > 0; var3 = var4) {
         var4 = var3 / 16;
         var5 = var3 % 16;
         var2.insert(0, (char)HEX_DIGITS[var5]);
      }

      var4 = 4 - var2.length();

      for(var5 = 0; var5 < var4; ++var5) {
         var2.insert(0, (String)"0");
      }

      var2.insert(0, (String)"U");
      return var2.toString();
   }

   private static boolean isIDLIdentifier(String var0) {
      boolean var1 = true;

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         var1 = var2 == 0 ? isIDLAlphabeticChar(var3) : isIDLIdentifierChar(var3);
         if (!var1) {
            break;
         }
      }

      return var1;
   }

   private static boolean isIDLIdentifierChar(char var0) {
      return isIDLAlphabeticChar(var0) || isIDLDecimalDigit(var0) || isUnderscore(var0);
   }

   private static boolean isIDLAlphabeticChar(char var0) {
      boolean var1 = var0 >= 'A' && var0 <= 'Z' || var0 >= 'a' && var0 <= 'z' || var0 >= 192 && var0 <= 255 && var0 != 215 && var0 != 247;
      return var1;
   }

   private static boolean isIDLDecimalDigit(char var0) {
      return var0 >= '0' && var0 <= '9';
   }

   private static boolean isUnderscore(char var0) {
      return var0 == '_';
   }

   private static String mangleOverloadedMethod(String var0, Method var1) {
      IDLTypesUtil var2 = new IDLTypesUtil();
      String var3 = var0 + "__";
      Class[] var4 = var1.getParameterTypes();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         Class var6 = var4[var5];
         if (var5 > 0) {
            var3 = var3 + "__";
         }

         IDLType var7 = classToIDLType(var6);
         String var8 = var7.getModuleName();
         String var9 = var7.getMemberName();
         String var10 = var8.length() > 0 ? var8 + "_" + var9 : var9;
         if (!var2.isPrimitive(var6) && var2.getSpecialCaseIDLTypeMapping(var6) == null && isIDLKeyword(var10)) {
            var10 = mangleIDLKeywordClash(var10);
         }

         var10 = mangleUnicodeChars(var10);
         var3 = var3 + var10;
      }

      return var3;
   }

   private static IDLType classToIDLType(Class var0) {
      IDLType var1 = null;
      IDLTypesUtil var2 = new IDLTypesUtil();
      if (var2.isPrimitive(var0)) {
         var1 = var2.getPrimitiveIDLTypeMapping(var0);
      } else {
         String[] var6;
         if (var0.isArray()) {
            Class var3 = var0.getComponentType();

            int var4;
            for(var4 = 1; var3.isArray(); ++var4) {
               var3 = var3.getComponentType();
            }

            IDLType var5 = classToIDLType(var3);
            var6 = BASE_IDL_ARRAY_MODULE_TYPE;
            if (var5.hasModule()) {
               var6 = (String[])((String[])ObjectUtility.concatenateArrays(var6, var5.getModules()));
            }

            String var7 = "seq" + var4 + "_" + var5.getMemberName();
            var1 = new IDLType(var0, var6, var7);
         } else {
            var1 = var2.getSpecialCaseIDLTypeMapping(var0);
            if (var1 == null) {
               String var10 = getUnmappedContainerName(var0);
               var10 = var10.replaceAll("\\$", "__");
               if (hasLeadingUnderscore(var10)) {
                  var10 = mangleLeadingUnderscore(var10);
               }

               String var11 = getPackageName(var0);
               if (var11 == null) {
                  var1 = new IDLType(var0, var10);
               } else {
                  if (var2.isEntity(var0)) {
                     var11 = "org.omg.boxedIDL." + var11;
                  }

                  StringTokenizer var12 = new StringTokenizer(var11, ".");
                  var6 = new String[var12.countTokens()];

                  String var9;
                  for(int var13 = 0; var12.hasMoreElements(); var6[var13++] = var9) {
                     String var8 = var12.nextToken();
                     var9 = hasLeadingUnderscore(var8) ? mangleLeadingUnderscore(var8) : var8;
                  }

                  var1 = new IDLType(var0, var6, var10);
               }
            }
         }
      }

      return var1;
   }

   private static String getPackageName(Class var0) {
      Package var1 = var0.getPackage();
      String var2 = null;
      if (var1 != null) {
         var2 = var1.getName();
      } else {
         String var3 = var0.getName();
         int var4 = var3.indexOf(46);
         var2 = var4 == -1 ? null : var3.substring(0, var4);
      }

      return var2;
   }

   private static String getMappedContainerName(Class var0) {
      String var1 = getUnmappedContainerName(var0);
      return mangleIdentifier(var1);
   }

   private static String getUnmappedContainerName(Class var0) {
      String var1 = null;
      String var2 = getPackageName(var0);
      String var3 = var0.getName();
      if (var2 != null) {
         int var4 = var2.length();
         var1 = var3.substring(var4 + 1);
      } else {
         var1 = var3;
      }

      return var1;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("IDLNameTranslator[");

      for(int var2 = 0; var2 < this.interf_.length; ++var2) {
         if (var2 != 0) {
            var1.append(" ");
         }

         var1.append(this.interf_[var2].getName());
      }

      var1.append("]\n");
      Iterator var5 = this.methodToIDLNameMap_.keySet().iterator();

      while(var5.hasNext()) {
         Method var3 = (Method)var5.next();
         String var4 = (String)this.methodToIDLNameMap_.get(var3);
         var1.append(var4 + ":" + var3 + "\n");
      }

      return var1.toString();
   }

   static {
      for(int var0 = 0; var0 < IDL_KEYWORDS.length; ++var0) {
         String var1 = IDL_KEYWORDS[var0];
         String var2 = var1.toUpperCase();
         idlKeywords_.add(var2);
      }

   }

   private static class IDLMethodInfo {
      public Method method;
      public boolean isProperty;
      public String originalName;
      public String mangledName;

      private IDLMethodInfo() {
      }

      // $FF: synthetic method
      IDLMethodInfo(Object var1) {
         this();
      }
   }
}
