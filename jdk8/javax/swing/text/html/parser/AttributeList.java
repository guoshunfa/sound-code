package javax.swing.text.html.parser;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class AttributeList implements DTDConstants, Serializable {
   public String name;
   public int type;
   public Vector<?> values;
   public int modifier;
   public String value;
   public AttributeList next;
   static Hashtable<Object, Object> attributeTypes = new Hashtable();

   AttributeList() {
   }

   public AttributeList(String var1) {
      this.name = var1;
   }

   public AttributeList(String var1, int var2, int var3, String var4, Vector<?> var5, AttributeList var6) {
      this.name = var1;
      this.type = var2;
      this.modifier = var3;
      this.value = var4;
      this.values = var5;
      this.next = var6;
   }

   public String getName() {
      return this.name;
   }

   public int getType() {
      return this.type;
   }

   public int getModifier() {
      return this.modifier;
   }

   public Enumeration<?> getValues() {
      return this.values != null ? this.values.elements() : null;
   }

   public String getValue() {
      return this.value;
   }

   public AttributeList getNext() {
      return this.next;
   }

   public String toString() {
      return this.name;
   }

   static void defineAttributeType(String var0, int var1) {
      Integer var2 = var1;
      attributeTypes.put(var0, var2);
      attributeTypes.put(var2, var0);
   }

   public static int name2type(String var0) {
      Integer var1 = (Integer)attributeTypes.get(var0);
      return var1 == null ? 1 : var1;
   }

   public static String type2name(int var0) {
      return (String)attributeTypes.get(var0);
   }

   static {
      defineAttributeType("CDATA", 1);
      defineAttributeType("ENTITY", 2);
      defineAttributeType("ENTITIES", 3);
      defineAttributeType("ID", 4);
      defineAttributeType("IDREF", 5);
      defineAttributeType("IDREFS", 6);
      defineAttributeType("NAME", 7);
      defineAttributeType("NAMES", 8);
      defineAttributeType("NMTOKEN", 9);
      defineAttributeType("NMTOKENS", 10);
      defineAttributeType("NOTATION", 11);
      defineAttributeType("NUMBER", 12);
      defineAttributeType("NUMBERS", 13);
      defineAttributeType("NUTOKEN", 14);
      defineAttributeType("NUTOKENS", 15);
      attributeTypes.put("fixed", 1);
      attributeTypes.put("required", 2);
      attributeTypes.put("current", 3);
      attributeTypes.put("conref", 4);
      attributeTypes.put("implied", 5);
   }
}
