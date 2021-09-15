package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.stream.buffer.AbstractCreator;
import java.util.ArrayList;
import java.util.List;

abstract class StreamBufferCreator extends AbstractCreator {
   private boolean checkAttributeValue = false;
   protected List<String> attributeValuePrefixes = new ArrayList();

   protected void storeQualifiedName(int item, String prefix, String uri, String localName) {
      if (uri != null && uri.length() > 0) {
         if (prefix != null && prefix.length() > 0) {
            item |= 1;
            this.storeStructureString(prefix);
         }

         item |= 2;
         this.storeStructureString(uri);
      }

      this.storeStructureString(localName);
      this.storeStructure(item);
   }

   protected final void storeNamespaceAttribute(String prefix, String uri) {
      int item = 64;
      if (prefix != null && prefix.length() > 0) {
         item |= 1;
         this.storeStructureString(prefix);
      }

      if (uri != null && uri.length() > 0) {
         item |= 2;
         this.storeStructureString(uri);
      }

      this.storeStructure(item);
   }

   protected final void storeAttribute(String prefix, String uri, String localName, String type, String value) {
      this.storeQualifiedName(48, prefix, uri, localName);
      this.storeStructureString(type);
      this.storeContentString(value);
      if (this.checkAttributeValue && value.indexOf("://") == -1) {
         int firstIndex = value.indexOf(":");
         int lastIndex = value.lastIndexOf(":");
         if (firstIndex != -1 && lastIndex == firstIndex) {
            String valuePrefix = value.substring(0, firstIndex);
            if (!this.attributeValuePrefixes.contains(valuePrefix)) {
               this.attributeValuePrefixes.add(valuePrefix);
            }
         }
      }

   }

   public final List getAttributeValuePrefixes() {
      return this.attributeValuePrefixes;
   }

   protected final void storeProcessingInstruction(String target, String data) {
      this.storeStructure(112);
      this.storeStructureString(target);
      this.storeStructureString(data);
   }

   public final boolean isCheckAttributeValue() {
      return this.checkAttributeValue;
   }

   public final void setCheckAttributeValue(boolean value) {
      this.checkAttributeValue = value;
   }
}
