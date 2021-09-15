package javax.imageio.metadata;

import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;

public interface IIOMetadataFormat {
   int CHILD_POLICY_EMPTY = 0;
   int CHILD_POLICY_ALL = 1;
   int CHILD_POLICY_SOME = 2;
   int CHILD_POLICY_CHOICE = 3;
   int CHILD_POLICY_SEQUENCE = 4;
   int CHILD_POLICY_REPEAT = 5;
   int CHILD_POLICY_MAX = 5;
   int VALUE_NONE = 0;
   int VALUE_ARBITRARY = 1;
   int VALUE_RANGE = 2;
   int VALUE_RANGE_MIN_INCLUSIVE_MASK = 4;
   int VALUE_RANGE_MAX_INCLUSIVE_MASK = 8;
   int VALUE_RANGE_MIN_INCLUSIVE = 6;
   int VALUE_RANGE_MAX_INCLUSIVE = 10;
   int VALUE_RANGE_MIN_MAX_INCLUSIVE = 14;
   int VALUE_ENUMERATION = 16;
   int VALUE_LIST = 32;
   int DATATYPE_STRING = 0;
   int DATATYPE_BOOLEAN = 1;
   int DATATYPE_INTEGER = 2;
   int DATATYPE_FLOAT = 3;
   int DATATYPE_DOUBLE = 4;

   String getRootName();

   boolean canNodeAppear(String var1, ImageTypeSpecifier var2);

   int getElementMinChildren(String var1);

   int getElementMaxChildren(String var1);

   String getElementDescription(String var1, Locale var2);

   int getChildPolicy(String var1);

   String[] getChildNames(String var1);

   String[] getAttributeNames(String var1);

   int getAttributeValueType(String var1, String var2);

   int getAttributeDataType(String var1, String var2);

   boolean isAttributeRequired(String var1, String var2);

   String getAttributeDefaultValue(String var1, String var2);

   String[] getAttributeEnumerations(String var1, String var2);

   String getAttributeMinValue(String var1, String var2);

   String getAttributeMaxValue(String var1, String var2);

   int getAttributeListMinLength(String var1, String var2);

   int getAttributeListMaxLength(String var1, String var2);

   String getAttributeDescription(String var1, String var2, Locale var3);

   int getObjectValueType(String var1);

   Class<?> getObjectClass(String var1);

   Object getObjectDefaultValue(String var1);

   Object[] getObjectEnumerations(String var1);

   Comparable<?> getObjectMinValue(String var1);

   Comparable<?> getObjectMaxValue(String var1);

   int getObjectArrayMinLength(String var1);

   int getObjectArrayMaxLength(String var1);
}
