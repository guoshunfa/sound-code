package javax.imageio.metadata;

import com.sun.imageio.plugins.common.StandardMetadataFormat;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.imageio.ImageTypeSpecifier;

public abstract class IIOMetadataFormatImpl implements IIOMetadataFormat {
   public static final String standardMetadataFormatName = "javax_imageio_1.0";
   private static IIOMetadataFormat standardFormat = null;
   private String resourceBaseName = this.getClass().getName() + "Resources";
   private String rootName;
   private HashMap elementMap = new HashMap();

   public IIOMetadataFormatImpl(String var1, int var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("rootName == null!");
      } else if (var2 >= 0 && var2 <= 5 && var2 != 5) {
         this.rootName = var1;
         IIOMetadataFormatImpl.Element var3 = new IIOMetadataFormatImpl.Element();
         var3.elementName = var1;
         var3.childPolicy = var2;
         this.elementMap.put(var1, var3);
      } else {
         throw new IllegalArgumentException("Invalid value for childPolicy!");
      }
   }

   public IIOMetadataFormatImpl(String var1, int var2, int var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("rootName == null!");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("minChildren < 0!");
      } else if (var2 > var3) {
         throw new IllegalArgumentException("minChildren > maxChildren!");
      } else {
         IIOMetadataFormatImpl.Element var4 = new IIOMetadataFormatImpl.Element();
         var4.elementName = var1;
         var4.childPolicy = 5;
         var4.minChildren = var2;
         var4.maxChildren = var3;
         this.rootName = var1;
         this.elementMap.put(var1, var4);
      }
   }

   protected void setResourceBaseName(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("resourceBaseName == null!");
      } else {
         this.resourceBaseName = var1;
      }
   }

   protected String getResourceBaseName() {
      return this.resourceBaseName;
   }

   private IIOMetadataFormatImpl.Element getElement(String var1, boolean var2) {
      if (var2 && var1 == null) {
         throw new IllegalArgumentException("element name is null!");
      } else {
         IIOMetadataFormatImpl.Element var3 = (IIOMetadataFormatImpl.Element)this.elementMap.get(var1);
         if (var2 && var3 == null) {
            throw new IllegalArgumentException("No such element: " + var1);
         } else {
            return var3;
         }
      }
   }

   private IIOMetadataFormatImpl.Element getElement(String var1) {
      return this.getElement(var1, true);
   }

   private IIOMetadataFormatImpl.Attribute getAttribute(String var1, String var2) {
      IIOMetadataFormatImpl.Element var3 = this.getElement(var1);
      IIOMetadataFormatImpl.Attribute var4 = (IIOMetadataFormatImpl.Attribute)var3.attrMap.get(var2);
      if (var4 == null) {
         throw new IllegalArgumentException("No such attribute \"" + var2 + "\"!");
      } else {
         return var4;
      }
   }

   protected void addElement(String var1, String var2, int var3) {
      IIOMetadataFormatImpl.Element var4 = this.getElement(var2);
      if (var3 >= 0 && var3 <= 5 && var3 != 5) {
         IIOMetadataFormatImpl.Element var5 = new IIOMetadataFormatImpl.Element();
         var5.elementName = var1;
         var5.childPolicy = var3;
         var4.childList.add(var1);
         var5.parentList.add(var2);
         this.elementMap.put(var1, var5);
      } else {
         throw new IllegalArgumentException("Invalid value for childPolicy!");
      }
   }

   protected void addElement(String var1, String var2, int var3, int var4) {
      IIOMetadataFormatImpl.Element var5 = this.getElement(var2);
      if (var3 < 0) {
         throw new IllegalArgumentException("minChildren < 0!");
      } else if (var3 > var4) {
         throw new IllegalArgumentException("minChildren > maxChildren!");
      } else {
         IIOMetadataFormatImpl.Element var6 = new IIOMetadataFormatImpl.Element();
         var6.elementName = var1;
         var6.childPolicy = 5;
         var6.minChildren = var3;
         var6.maxChildren = var4;
         var5.childList.add(var1);
         var6.parentList.add(var2);
         this.elementMap.put(var1, var6);
      }
   }

   protected void addChildElement(String var1, String var2) {
      IIOMetadataFormatImpl.Element var3 = this.getElement(var2);
      IIOMetadataFormatImpl.Element var4 = this.getElement(var1);
      var3.childList.add(var1);
      var4.parentList.add(var2);
   }

   protected void removeElement(String var1) {
      IIOMetadataFormatImpl.Element var2 = this.getElement(var1, false);
      if (var2 != null) {
         Iterator var3 = var2.parentList.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            IIOMetadataFormatImpl.Element var5 = this.getElement(var4, false);
            if (var5 != null) {
               var5.childList.remove(var1);
            }
         }

         this.elementMap.remove(var1);
      }

   }

   protected void addAttribute(String var1, String var2, int var3, boolean var4, String var5) {
      IIOMetadataFormatImpl.Element var6 = this.getElement(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("attrName == null!");
      } else if (var3 >= 0 && var3 <= 4) {
         IIOMetadataFormatImpl.Attribute var7 = new IIOMetadataFormatImpl.Attribute();
         var7.attrName = var2;
         var7.valueType = 1;
         var7.dataType = var3;
         var7.required = var4;
         var7.defaultValue = var5;
         var6.attrList.add(var2);
         var6.attrMap.put(var2, var7);
      } else {
         throw new IllegalArgumentException("Invalid value for dataType!");
      }
   }

   protected void addAttribute(String var1, String var2, int var3, boolean var4, String var5, List<String> var6) {
      IIOMetadataFormatImpl.Element var7 = this.getElement(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("attrName == null!");
      } else if (var3 >= 0 && var3 <= 4) {
         if (var6 == null) {
            throw new IllegalArgumentException("enumeratedValues == null!");
         } else if (var6.size() == 0) {
            throw new IllegalArgumentException("enumeratedValues is empty!");
         } else {
            Iterator var8 = var6.iterator();

            Object var9;
            do {
               if (!var8.hasNext()) {
                  IIOMetadataFormatImpl.Attribute var10 = new IIOMetadataFormatImpl.Attribute();
                  var10.attrName = var2;
                  var10.valueType = 16;
                  var10.dataType = var3;
                  var10.required = var4;
                  var10.defaultValue = var5;
                  var10.enumeratedValues = var6;
                  var7.attrList.add(var2);
                  var7.attrMap.put(var2, var10);
                  return;
               }

               var9 = var8.next();
               if (var9 == null) {
                  throw new IllegalArgumentException("enumeratedValues contains a null!");
               }
            } while(var9 instanceof String);

            throw new IllegalArgumentException("enumeratedValues contains a non-String value!");
         }
      } else {
         throw new IllegalArgumentException("Invalid value for dataType!");
      }
   }

   protected void addAttribute(String var1, String var2, int var3, boolean var4, String var5, String var6, String var7, boolean var8, boolean var9) {
      IIOMetadataFormatImpl.Element var10 = this.getElement(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("attrName == null!");
      } else if (var3 >= 0 && var3 <= 4) {
         IIOMetadataFormatImpl.Attribute var11 = new IIOMetadataFormatImpl.Attribute();
         var11.attrName = var2;
         var11.valueType = 2;
         if (var8) {
            var11.valueType |= 4;
         }

         if (var9) {
            var11.valueType |= 8;
         }

         var11.dataType = var3;
         var11.required = var4;
         var11.defaultValue = var5;
         var11.minValue = var6;
         var11.maxValue = var7;
         var10.attrList.add(var2);
         var10.attrMap.put(var2, var11);
      } else {
         throw new IllegalArgumentException("Invalid value for dataType!");
      }
   }

   protected void addAttribute(String var1, String var2, int var3, boolean var4, int var5, int var6) {
      IIOMetadataFormatImpl.Element var7 = this.getElement(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("attrName == null!");
      } else if (var3 >= 0 && var3 <= 4) {
         if (var5 >= 0 && var5 <= var6) {
            IIOMetadataFormatImpl.Attribute var8 = new IIOMetadataFormatImpl.Attribute();
            var8.attrName = var2;
            var8.valueType = 32;
            var8.dataType = var3;
            var8.required = var4;
            var8.listMinLength = var5;
            var8.listMaxLength = var6;
            var7.attrList.add(var2);
            var7.attrMap.put(var2, var8);
         } else {
            throw new IllegalArgumentException("Invalid list bounds!");
         }
      } else {
         throw new IllegalArgumentException("Invalid value for dataType!");
      }
   }

   protected void addBooleanAttribute(String var1, String var2, boolean var3, boolean var4) {
      ArrayList var5 = new ArrayList();
      var5.add("TRUE");
      var5.add("FALSE");
      String var6 = null;
      if (var3) {
         var6 = var4 ? "TRUE" : "FALSE";
      }

      this.addAttribute(var1, var2, 1, true, var6, var5);
   }

   protected void removeAttribute(String var1, String var2) {
      IIOMetadataFormatImpl.Element var3 = this.getElement(var1);
      var3.attrList.remove(var2);
      var3.attrMap.remove(var2);
   }

   protected <T> void addObjectValue(String var1, Class<T> var2, boolean var3, T var4) {
      IIOMetadataFormatImpl.Element var5 = this.getElement(var1);
      IIOMetadataFormatImpl.ObjectValue var6 = new IIOMetadataFormatImpl.ObjectValue();
      var6.valueType = 1;
      var6.classType = var2;
      var6.defaultValue = var4;
      var5.objectValue = var6;
   }

   protected <T> void addObjectValue(String var1, Class<T> var2, boolean var3, T var4, List<? extends T> var5) {
      IIOMetadataFormatImpl.Element var6 = this.getElement(var1);
      if (var5 == null) {
         throw new IllegalArgumentException("enumeratedValues == null!");
      } else if (var5.size() == 0) {
         throw new IllegalArgumentException("enumeratedValues is empty!");
      } else {
         Iterator var7 = var5.iterator();

         Object var8;
         do {
            if (!var7.hasNext()) {
               IIOMetadataFormatImpl.ObjectValue var9 = new IIOMetadataFormatImpl.ObjectValue();
               var9.valueType = 16;
               var9.classType = var2;
               var9.defaultValue = var4;
               var9.enumeratedValues = var5;
               var6.objectValue = var9;
               return;
            }

            var8 = var7.next();
            if (var8 == null) {
               throw new IllegalArgumentException("enumeratedValues contains a null!");
            }
         } while(var2.isInstance(var8));

         throw new IllegalArgumentException("enumeratedValues contains a value not of class classType!");
      }
   }

   protected <T extends Object & Comparable<? super T>> void addObjectValue(String var1, Class<T> var2, T var3, Comparable<? super T> var4, Comparable<? super T> var5, boolean var6, boolean var7) {
      IIOMetadataFormatImpl.Element var8 = this.getElement(var1);
      IIOMetadataFormatImpl.ObjectValue var9 = new IIOMetadataFormatImpl.ObjectValue();
      var9.valueType = 2;
      if (var6) {
         var9.valueType |= 4;
      }

      if (var7) {
         var9.valueType |= 8;
      }

      var9.classType = var2;
      var9.defaultValue = var3;
      var9.minValue = var4;
      var9.maxValue = var5;
      var8.objectValue = var9;
   }

   protected void addObjectValue(String var1, Class<?> var2, int var3, int var4) {
      IIOMetadataFormatImpl.Element var5 = this.getElement(var1);
      IIOMetadataFormatImpl.ObjectValue var6 = new IIOMetadataFormatImpl.ObjectValue();
      var6.valueType = 32;
      var6.classType = var2;
      var6.arrayMinLength = var3;
      var6.arrayMaxLength = var4;
      var5.objectValue = var6;
   }

   protected void removeObjectValue(String var1) {
      IIOMetadataFormatImpl.Element var2 = this.getElement(var1);
      var2.objectValue = null;
   }

   public String getRootName() {
      return this.rootName;
   }

   public abstract boolean canNodeAppear(String var1, ImageTypeSpecifier var2);

   public int getElementMinChildren(String var1) {
      IIOMetadataFormatImpl.Element var2 = this.getElement(var1);
      if (var2.childPolicy != 5) {
         throw new IllegalArgumentException("Child policy not CHILD_POLICY_REPEAT!");
      } else {
         return var2.minChildren;
      }
   }

   public int getElementMaxChildren(String var1) {
      IIOMetadataFormatImpl.Element var2 = this.getElement(var1);
      if (var2.childPolicy != 5) {
         throw new IllegalArgumentException("Child policy not CHILD_POLICY_REPEAT!");
      } else {
         return var2.maxChildren;
      }
   }

   private String getResource(String var1, Locale var2) {
      if (var2 == null) {
         var2 = Locale.getDefault();
      }

      ClassLoader var3 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
      ResourceBundle var4 = null;

      try {
         var4 = ResourceBundle.getBundle(this.resourceBaseName, var2, var3);
      } catch (MissingResourceException var9) {
         try {
            var4 = ResourceBundle.getBundle(this.resourceBaseName, var2);
         } catch (MissingResourceException var8) {
            return null;
         }
      }

      try {
         return var4.getString(var1);
      } catch (MissingResourceException var7) {
         return null;
      }
   }

   public String getElementDescription(String var1, Locale var2) {
      this.getElement(var1);
      return this.getResource(var1, var2);
   }

   public int getChildPolicy(String var1) {
      IIOMetadataFormatImpl.Element var2 = this.getElement(var1);
      return var2.childPolicy;
   }

   public String[] getChildNames(String var1) {
      IIOMetadataFormatImpl.Element var2 = this.getElement(var1);
      return var2.childPolicy == 0 ? null : (String[])((String[])var2.childList.toArray(new String[0]));
   }

   public String[] getAttributeNames(String var1) {
      IIOMetadataFormatImpl.Element var2 = this.getElement(var1);
      List var3 = var2.attrList;
      String[] var4 = new String[var3.size()];
      return (String[])((String[])var3.toArray(var4));
   }

   public int getAttributeValueType(String var1, String var2) {
      IIOMetadataFormatImpl.Attribute var3 = this.getAttribute(var1, var2);
      return var3.valueType;
   }

   public int getAttributeDataType(String var1, String var2) {
      IIOMetadataFormatImpl.Attribute var3 = this.getAttribute(var1, var2);
      return var3.dataType;
   }

   public boolean isAttributeRequired(String var1, String var2) {
      IIOMetadataFormatImpl.Attribute var3 = this.getAttribute(var1, var2);
      return var3.required;
   }

   public String getAttributeDefaultValue(String var1, String var2) {
      IIOMetadataFormatImpl.Attribute var3 = this.getAttribute(var1, var2);
      return var3.defaultValue;
   }

   public String[] getAttributeEnumerations(String var1, String var2) {
      IIOMetadataFormatImpl.Attribute var3 = this.getAttribute(var1, var2);
      if (var3.valueType != 16) {
         throw new IllegalArgumentException("Attribute not an enumeration!");
      } else {
         List var4 = var3.enumeratedValues;
         Iterator var5 = var4.iterator();
         String[] var6 = new String[var4.size()];
         return (String[])((String[])var4.toArray(var6));
      }
   }

   public String getAttributeMinValue(String var1, String var2) {
      IIOMetadataFormatImpl.Attribute var3 = this.getAttribute(var1, var2);
      if (var3.valueType != 2 && var3.valueType != 6 && var3.valueType != 10 && var3.valueType != 14) {
         throw new IllegalArgumentException("Attribute not a range!");
      } else {
         return var3.minValue;
      }
   }

   public String getAttributeMaxValue(String var1, String var2) {
      IIOMetadataFormatImpl.Attribute var3 = this.getAttribute(var1, var2);
      if (var3.valueType != 2 && var3.valueType != 6 && var3.valueType != 10 && var3.valueType != 14) {
         throw new IllegalArgumentException("Attribute not a range!");
      } else {
         return var3.maxValue;
      }
   }

   public int getAttributeListMinLength(String var1, String var2) {
      IIOMetadataFormatImpl.Attribute var3 = this.getAttribute(var1, var2);
      if (var3.valueType != 32) {
         throw new IllegalArgumentException("Attribute not a list!");
      } else {
         return var3.listMinLength;
      }
   }

   public int getAttributeListMaxLength(String var1, String var2) {
      IIOMetadataFormatImpl.Attribute var3 = this.getAttribute(var1, var2);
      if (var3.valueType != 32) {
         throw new IllegalArgumentException("Attribute not a list!");
      } else {
         return var3.listMaxLength;
      }
   }

   public String getAttributeDescription(String var1, String var2, Locale var3) {
      IIOMetadataFormatImpl.Element var4 = this.getElement(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("attrName == null!");
      } else {
         IIOMetadataFormatImpl.Attribute var5 = (IIOMetadataFormatImpl.Attribute)var4.attrMap.get(var2);
         if (var5 == null) {
            throw new IllegalArgumentException("No such attribute!");
         } else {
            String var6 = var1 + "/" + var2;
            return this.getResource(var6, var3);
         }
      }
   }

   private IIOMetadataFormatImpl.ObjectValue getObjectValue(String var1) {
      IIOMetadataFormatImpl.Element var2 = this.getElement(var1);
      IIOMetadataFormatImpl.ObjectValue var3 = var2.objectValue;
      if (var3 == null) {
         throw new IllegalArgumentException("No object within element " + var1 + "!");
      } else {
         return var3;
      }
   }

   public int getObjectValueType(String var1) {
      IIOMetadataFormatImpl.Element var2 = this.getElement(var1);
      IIOMetadataFormatImpl.ObjectValue var3 = var2.objectValue;
      return var3 == null ? 0 : var3.valueType;
   }

   public Class<?> getObjectClass(String var1) {
      IIOMetadataFormatImpl.ObjectValue var2 = this.getObjectValue(var1);
      return var2.classType;
   }

   public Object getObjectDefaultValue(String var1) {
      IIOMetadataFormatImpl.ObjectValue var2 = this.getObjectValue(var1);
      return var2.defaultValue;
   }

   public Object[] getObjectEnumerations(String var1) {
      IIOMetadataFormatImpl.ObjectValue var2 = this.getObjectValue(var1);
      if (var2.valueType != 16) {
         throw new IllegalArgumentException("Not an enumeration!");
      } else {
         List var3 = var2.enumeratedValues;
         Object[] var4 = new Object[var3.size()];
         return var3.toArray(var4);
      }
   }

   public Comparable<?> getObjectMinValue(String var1) {
      IIOMetadataFormatImpl.ObjectValue var2 = this.getObjectValue(var1);
      if ((var2.valueType & 2) != 2) {
         throw new IllegalArgumentException("Not a range!");
      } else {
         return var2.minValue;
      }
   }

   public Comparable<?> getObjectMaxValue(String var1) {
      IIOMetadataFormatImpl.ObjectValue var2 = this.getObjectValue(var1);
      if ((var2.valueType & 2) != 2) {
         throw new IllegalArgumentException("Not a range!");
      } else {
         return var2.maxValue;
      }
   }

   public int getObjectArrayMinLength(String var1) {
      IIOMetadataFormatImpl.ObjectValue var2 = this.getObjectValue(var1);
      if (var2.valueType != 32) {
         throw new IllegalArgumentException("Not a list!");
      } else {
         return var2.arrayMinLength;
      }
   }

   public int getObjectArrayMaxLength(String var1) {
      IIOMetadataFormatImpl.ObjectValue var2 = this.getObjectValue(var1);
      if (var2.valueType != 32) {
         throw new IllegalArgumentException("Not a list!");
      } else {
         return var2.arrayMaxLength;
      }
   }

   private static synchronized void createStandardFormat() {
      if (standardFormat == null) {
         standardFormat = new StandardMetadataFormat();
      }

   }

   public static IIOMetadataFormat getStandardFormatInstance() {
      createStandardFormat();
      return standardFormat;
   }

   class ObjectValue {
      int valueType = 0;
      Class classType = null;
      Object defaultValue = null;
      List enumeratedValues = null;
      Comparable minValue = null;
      Comparable maxValue = null;
      int arrayMinLength = 0;
      int arrayMaxLength = 0;
   }

   class Attribute {
      String attrName;
      int valueType = 1;
      int dataType;
      boolean required;
      String defaultValue = null;
      List enumeratedValues;
      String minValue;
      String maxValue;
      int listMinLength;
      int listMaxLength;
   }

   class Element {
      String elementName;
      int childPolicy;
      int minChildren = 0;
      int maxChildren = 0;
      List childList = new ArrayList();
      List parentList = new ArrayList();
      List attrList = new ArrayList();
      Map attrMap = new HashMap();
      IIOMetadataFormatImpl.ObjectValue objectValue;
   }
}
