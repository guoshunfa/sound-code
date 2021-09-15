package org.jcp.xml.dsig.internal.dom;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.Security;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class Policy {
   private static Set<URI> disallowedAlgs = new HashSet();
   private static int maxTrans = Integer.MAX_VALUE;
   private static int maxRefs = Integer.MAX_VALUE;
   private static Set<String> disallowedRefUriSchemes = new HashSet();
   private static Map<String, Integer> minKeyMap = new HashMap();
   private static boolean noDuplicateIds = false;
   private static boolean noRMLoops = false;

   private Policy() {
   }

   private static void initialize() {
      String var0 = (String)AccessController.doPrivileged(() -> {
         return Security.getProperty("jdk.xml.dsig.secureValidationPolicy");
      });
      if (var0 != null && !var0.isEmpty()) {
         String[] var1 = var0.split(",");
         String[] var2 = var1;
         int var3 = var1.length;

         label86:
         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            String[] var6 = var5.split("\\s");
            String var7 = var6[0];
            byte var9 = -1;
            switch(var7.hashCode()) {
            case -1464517554:
               if (var7.equals("minKeySize")) {
                  var9 = 4;
               }
               break;
            case -1401029998:
               if (var7.equals("disallowReferenceUriSchemes")) {
                  var9 = 3;
               }
               break;
            case -1186039282:
               if (var7.equals("noDuplicateIds")) {
                  var9 = 5;
               }
               break;
            case 212582156:
               if (var7.equals("maxReferences")) {
                  var9 = 2;
               }
               break;
            case 1395529483:
               if (var7.equals("maxTransforms")) {
                  var9 = 1;
               }
               break;
            case 1648673825:
               if (var7.equals("disallowAlg")) {
                  var9 = 0;
               }
               break;
            case 1978245499:
               if (var7.equals("noRetrievalMethodLoops")) {
                  var9 = 6;
               }
            }

            switch(var9) {
            case 0:
               if (var6.length != 2) {
                  error(var5);
               }

               disallowedAlgs.add(URI.create(var6[1]));
               break;
            case 1:
               if (var6.length != 2) {
                  error(var5);
               }

               maxTrans = Integer.parseUnsignedInt(var6[1]);
               break;
            case 2:
               if (var6.length != 2) {
                  error(var5);
               }

               maxRefs = Integer.parseUnsignedInt(var6[1]);
               break;
            case 3:
               if (var6.length == 1) {
                  error(var5);
               }

               int var10 = 1;

               while(true) {
                  if (var10 >= var6.length) {
                     continue label86;
                  }

                  String var11 = var6[var10];
                  disallowedRefUriSchemes.add(var11.toLowerCase(Locale.ROOT));
                  ++var10;
               }
            case 4:
               if (var6.length != 3) {
                  error(var5);
               }

               minKeyMap.put(var6[1], Integer.parseUnsignedInt(var6[2]));
               break;
            case 5:
               if (var6.length != 1) {
                  error(var5);
               }

               noDuplicateIds = true;
               break;
            case 6:
               if (var6.length != 1) {
                  error(var5);
               }

               noRMLoops = true;
               break;
            default:
               error(var5);
            }
         }

      }
   }

   public static boolean restrictAlg(String var0) {
      try {
         URI var1 = new URI(var0);
         return disallowedAlgs.contains(var1);
      } catch (URISyntaxException var2) {
         return false;
      }
   }

   public static boolean restrictNumTransforms(int var0) {
      return var0 > maxTrans;
   }

   public static boolean restrictNumReferences(int var0) {
      return var0 > maxRefs;
   }

   public static boolean restrictReferenceUriScheme(String var0) {
      if (var0 != null) {
         String var1 = URI.create(var0).getScheme();
         if (var1 != null) {
            return disallowedRefUriSchemes.contains(var1.toLowerCase(Locale.ROOT));
         }
      }

      return false;
   }

   public static boolean restrictKey(String var0, int var1) {
      return var1 < (Integer)minKeyMap.getOrDefault(var0, 0);
   }

   public static boolean restrictDuplicateIds() {
      return noDuplicateIds;
   }

   public static boolean restrictRetrievalMethodLoops() {
      return noRMLoops;
   }

   public static Set<URI> disabledAlgs() {
      return Collections.unmodifiableSet(disallowedAlgs);
   }

   public static int maxTransforms() {
      return maxTrans;
   }

   public static int maxReferences() {
      return maxRefs;
   }

   public static Set<String> disabledReferenceUriSchemes() {
      return Collections.unmodifiableSet(disallowedRefUriSchemes);
   }

   public static int minKeySize(String var0) {
      return (Integer)minKeyMap.getOrDefault(var0, 0);
   }

   private static void error(String var0) {
      throw new IllegalArgumentException("Invalid jdk.xml.dsig.secureValidationPolicy entry: " + var0);
   }

   static {
      try {
         initialize();
      } catch (Exception var1) {
         throw new SecurityException("Cannot initialize the secure validation policy", var1);
      }
   }
}
