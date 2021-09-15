package sun.misc;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.jar.Attributes;

public class ExtensionInfo {
   public static final int COMPATIBLE = 0;
   public static final int REQUIRE_SPECIFICATION_UPGRADE = 1;
   public static final int REQUIRE_IMPLEMENTATION_UPGRADE = 2;
   public static final int REQUIRE_VENDOR_SWITCH = 3;
   public static final int INCOMPATIBLE = 4;
   public String title;
   public String name;
   public String specVersion;
   public String specVendor;
   public String implementationVersion;
   public String vendor;
   public String vendorId;
   public String url;
   private static final ResourceBundle rb = ResourceBundle.getBundle("sun.misc.resources.Messages");

   public ExtensionInfo() {
   }

   public ExtensionInfo(String var1, Attributes var2) throws NullPointerException {
      String var3;
      if (var1 != null) {
         var3 = var1 + "-";
      } else {
         var3 = "";
      }

      String var4 = var3 + Attributes.Name.EXTENSION_NAME.toString();
      this.name = var2.getValue(var4);
      if (this.name != null) {
         this.name = this.name.trim();
      }

      var4 = var3 + Attributes.Name.SPECIFICATION_TITLE.toString();
      this.title = var2.getValue(var4);
      if (this.title != null) {
         this.title = this.title.trim();
      }

      var4 = var3 + Attributes.Name.SPECIFICATION_VERSION.toString();
      this.specVersion = var2.getValue(var4);
      if (this.specVersion != null) {
         this.specVersion = this.specVersion.trim();
      }

      var4 = var3 + Attributes.Name.SPECIFICATION_VENDOR.toString();
      this.specVendor = var2.getValue(var4);
      if (this.specVendor != null) {
         this.specVendor = this.specVendor.trim();
      }

      var4 = var3 + Attributes.Name.IMPLEMENTATION_VERSION.toString();
      this.implementationVersion = var2.getValue(var4);
      if (this.implementationVersion != null) {
         this.implementationVersion = this.implementationVersion.trim();
      }

      var4 = var3 + Attributes.Name.IMPLEMENTATION_VENDOR.toString();
      this.vendor = var2.getValue(var4);
      if (this.vendor != null) {
         this.vendor = this.vendor.trim();
      }

      var4 = var3 + Attributes.Name.IMPLEMENTATION_VENDOR_ID.toString();
      this.vendorId = var2.getValue(var4);
      if (this.vendorId != null) {
         this.vendorId = this.vendorId.trim();
      }

      var4 = var3 + Attributes.Name.IMPLEMENTATION_URL.toString();
      this.url = var2.getValue(var4);
      if (this.url != null) {
         this.url = this.url.trim();
      }

   }

   public int isCompatibleWith(ExtensionInfo var1) {
      if (this.name != null && var1.name != null) {
         if (this.name.compareTo(var1.name) == 0) {
            if (this.specVersion != null && var1.specVersion != null) {
               int var2 = this.compareExtensionVersion(this.specVersion, var1.specVersion);
               if (var2 < 0) {
                  return this.vendorId != null && var1.vendorId != null && this.vendorId.compareTo(var1.vendorId) != 0 ? 3 : 1;
               } else {
                  if (this.vendorId != null && var1.vendorId != null) {
                     if (this.vendorId.compareTo(var1.vendorId) != 0) {
                        return 3;
                     }

                     if (this.implementationVersion != null && var1.implementationVersion != null) {
                        var2 = this.compareExtensionVersion(this.implementationVersion, var1.implementationVersion);
                        if (var2 < 0) {
                           return 2;
                        }
                     }
                  }

                  return 0;
               }
            } else {
               return 0;
            }
         } else {
            return 4;
         }
      } else {
         return 4;
      }
   }

   public String toString() {
      return "Extension : title(" + this.title + "), name(" + this.name + "), spec vendor(" + this.specVendor + "), spec version(" + this.specVersion + "), impl vendor(" + this.vendor + "), impl vendor id(" + this.vendorId + "), impl version(" + this.implementationVersion + "), impl url(" + this.url + ")";
   }

   private int compareExtensionVersion(String var1, String var2) throws NumberFormatException {
      var1 = var1.toLowerCase();
      var2 = var2.toLowerCase();
      return this.strictCompareExtensionVersion(var1, var2);
   }

   private int strictCompareExtensionVersion(String var1, String var2) throws NumberFormatException {
      if (var1.equals(var2)) {
         return 0;
      } else {
         StringTokenizer var3 = new StringTokenizer(var1, ".,");
         StringTokenizer var4 = new StringTokenizer(var2, ".,");
         int var5 = 0;
         int var6 = 0;
         boolean var7 = false;
         if (var3.hasMoreTokens()) {
            var5 = this.convertToken(var3.nextToken().toString());
         }

         if (var4.hasMoreTokens()) {
            var6 = this.convertToken(var4.nextToken().toString());
         }

         if (var5 > var6) {
            return 1;
         } else if (var6 > var5) {
            return -1;
         } else {
            int var8 = var1.indexOf(".");
            int var9 = var2.indexOf(".");
            if (var8 == -1) {
               var8 = var1.length() - 1;
            }

            if (var9 == -1) {
               var9 = var2.length() - 1;
            }

            return this.strictCompareExtensionVersion(var1.substring(var8 + 1), var2.substring(var9 + 1));
         }
      }
   }

   private int convertToken(String var1) {
      if (var1 != null && !var1.equals("")) {
         boolean var2 = false;
         int var3 = 0;
         boolean var4 = false;
         int var5 = var1.length();
         int var6 = var5;
         Object[] var8 = new Object[]{this.name};
         MessageFormat var9 = new MessageFormat(rb.getString("optpkg.versionerror"));
         String var10 = var9.format(var8);
         int var11 = var1.indexOf("-");
         int var12 = var1.indexOf("_");
         if (var11 == -1 && var12 == -1) {
            try {
               return Integer.parseInt(var1) * 100;
            } catch (NumberFormatException var18) {
               System.out.println(var10);
               return 0;
            }
         } else {
            int var13;
            if (var12 == -1) {
               try {
                  var13 = Integer.parseInt(var1.substring(0, var11));
               } catch (NumberFormatException var20) {
                  System.out.println(var10);
                  return 0;
               }

               String var14 = var1.substring(var11 + 1);
               String var15 = "";
               byte var16 = 0;
               if (var14.indexOf("ea") != -1) {
                  var15 = var14.substring(2);
                  var16 = 50;
               } else if (var14.indexOf("alpha") != -1) {
                  var15 = var14.substring(5);
                  var16 = 40;
               } else if (var14.indexOf("beta") != -1) {
                  var15 = var14.substring(4);
                  var16 = 30;
               } else if (var14.indexOf("rc") != -1) {
                  var15 = var14.substring(2);
                  var16 = 20;
               }

               if (var15 != null && !var15.equals("")) {
                  try {
                     return var13 * 100 - var16 + Integer.parseInt(var15);
                  } catch (NumberFormatException var19) {
                     System.out.println(var10);
                     return 0;
                  }
               } else {
                  return var13 * 100 - var16;
               }
            } else {
               int var23;
               try {
                  var13 = Integer.parseInt(var1.substring(0, var12));
                  char var7 = var1.charAt(var5 - 1);
                  if (Character.isLetter(var7)) {
                     int var22 = Character.getNumericValue(var7);
                     var6 = var5 - 1;
                     var23 = Integer.parseInt(var1.substring(var12 + 1, var6));
                     if (var22 >= Character.getNumericValue('a') && var22 <= Character.getNumericValue('z')) {
                        var3 = var23 * 100 + var22;
                     } else {
                        var3 = 0;
                        System.out.println(var10);
                     }
                  } else {
                     var23 = Integer.parseInt(var1.substring(var12 + 1, var6));
                  }
               } catch (NumberFormatException var21) {
                  System.out.println(var10);
                  return 0;
               }

               return var13 * 100 + var23 + var3;
            }
         }
      } else {
         return 0;
      }
   }
}
