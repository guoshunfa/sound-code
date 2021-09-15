package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.xml.internal.stream.XMLBufferListener;

public class XMLAttributesImpl implements XMLAttributes, XMLBufferListener {
   protected static final int TABLE_SIZE = 101;
   protected static final int MAX_HASH_COLLISIONS = 40;
   protected static final int MULTIPLIERS_SIZE = 32;
   protected static final int MULTIPLIERS_MASK = 31;
   protected static final int SIZE_LIMIT = 20;
   protected boolean fNamespaces;
   protected int fLargeCount;
   protected int fLength;
   protected XMLAttributesImpl.Attribute[] fAttributes;
   protected XMLAttributesImpl.Attribute[] fAttributeTableView;
   protected int[] fAttributeTableViewChainState;
   protected int fTableViewBuckets;
   protected boolean fIsTableViewConsistent;
   protected int[] fHashMultipliers;

   public XMLAttributesImpl() {
      this(101);
   }

   public XMLAttributesImpl(int tableSize) {
      this.fNamespaces = true;
      this.fLargeCount = 1;
      this.fAttributes = new XMLAttributesImpl.Attribute[4];
      this.fTableViewBuckets = tableSize;

      for(int i = 0; i < this.fAttributes.length; ++i) {
         this.fAttributes[i] = new XMLAttributesImpl.Attribute();
      }

   }

   public void setNamespaces(boolean namespaces) {
      this.fNamespaces = namespaces;
   }

   public int addAttribute(QName name, String type, String value) {
      return this.addAttribute(name, type, value, (XMLString)null);
   }

   public int addAttribute(QName name, String type, String value, XMLString valueCache) {
      int index;
      int collisionCount;
      if (this.fLength < 20) {
         index = name.uri != null && !name.uri.equals("") ? this.getIndexFast(name.uri, name.localpart) : this.getIndexFast(name.rawname);
         if (index == -1) {
            index = this.fLength;
            if (this.fLength++ == this.fAttributes.length) {
               XMLAttributesImpl.Attribute[] attributes = new XMLAttributesImpl.Attribute[this.fAttributes.length + 4];
               System.arraycopy(this.fAttributes, 0, attributes, 0, this.fAttributes.length);

               for(collisionCount = this.fAttributes.length; collisionCount < attributes.length; ++collisionCount) {
                  attributes[collisionCount] = new XMLAttributesImpl.Attribute();
               }

               this.fAttributes = attributes;
            }
         }
      } else if (name.uri == null || name.uri.length() == 0 || (index = this.getIndexFast(name.uri, name.localpart)) == -1) {
         if (!this.fIsTableViewConsistent || this.fLength == 20 || this.fLength > 20 && this.fLength > this.fTableViewBuckets) {
            this.prepareAndPopulateTableView();
            this.fIsTableViewConsistent = true;
         }

         int bucket = this.getTableViewBucket(name.rawname);
         if (this.fAttributeTableViewChainState[bucket] != this.fLargeCount) {
            index = this.fLength;
            if (this.fLength++ == this.fAttributes.length) {
               XMLAttributesImpl.Attribute[] attributes = new XMLAttributesImpl.Attribute[this.fAttributes.length << 1];
               System.arraycopy(this.fAttributes, 0, attributes, 0, this.fAttributes.length);

               for(int i = this.fAttributes.length; i < attributes.length; ++i) {
                  attributes[i] = new XMLAttributesImpl.Attribute();
               }

               this.fAttributes = attributes;
            }

            this.fAttributeTableViewChainState[bucket] = this.fLargeCount;
            this.fAttributes[index].next = null;
            this.fAttributeTableView[bucket] = this.fAttributes[index];
         } else {
            collisionCount = 0;

            XMLAttributesImpl.Attribute found;
            for(found = this.fAttributeTableView[bucket]; found != null && found.name.rawname != name.rawname; ++collisionCount) {
               found = found.next;
            }

            if (found == null) {
               index = this.fLength;
               if (this.fLength++ == this.fAttributes.length) {
                  XMLAttributesImpl.Attribute[] attributes = new XMLAttributesImpl.Attribute[this.fAttributes.length << 1];
                  System.arraycopy(this.fAttributes, 0, attributes, 0, this.fAttributes.length);

                  for(int i = this.fAttributes.length; i < attributes.length; ++i) {
                     attributes[i] = new XMLAttributesImpl.Attribute();
                  }

                  this.fAttributes = attributes;
               }

               if (collisionCount >= 40) {
                  this.fAttributes[index].name.setValues(name);
                  this.rebalanceTableView(this.fLength);
               } else {
                  this.fAttributes[index].next = this.fAttributeTableView[bucket];
                  this.fAttributeTableView[bucket] = this.fAttributes[index];
               }
            } else {
               index = this.getIndexFast(name.rawname);
            }
         }
      }

      XMLAttributesImpl.Attribute attribute = this.fAttributes[index];
      attribute.name.setValues(name);
      attribute.type = type;
      attribute.value = value;
      attribute.xmlValue = valueCache;
      attribute.nonNormalizedValue = value;
      attribute.specified = false;
      if (attribute.augs != null) {
         attribute.augs.removeAllItems();
      }

      return index;
   }

   public void removeAllAttributes() {
      this.fLength = 0;
   }

   public void removeAttributeAt(int attrIndex) {
      this.fIsTableViewConsistent = false;
      if (attrIndex < this.fLength - 1) {
         XMLAttributesImpl.Attribute removedAttr = this.fAttributes[attrIndex];
         System.arraycopy(this.fAttributes, attrIndex + 1, this.fAttributes, attrIndex, this.fLength - attrIndex - 1);
         this.fAttributes[this.fLength - 1] = removedAttr;
      }

      --this.fLength;
   }

   public void setName(int attrIndex, QName attrName) {
      this.fAttributes[attrIndex].name.setValues(attrName);
   }

   public void getName(int attrIndex, QName attrName) {
      attrName.setValues(this.fAttributes[attrIndex].name);
   }

   public void setType(int attrIndex, String attrType) {
      this.fAttributes[attrIndex].type = attrType;
   }

   public void setValue(int attrIndex, String attrValue) {
      this.setValue(attrIndex, attrValue, (XMLString)null);
   }

   public void setValue(int attrIndex, String attrValue, XMLString value) {
      XMLAttributesImpl.Attribute attribute = this.fAttributes[attrIndex];
      attribute.value = attrValue;
      attribute.nonNormalizedValue = attrValue;
      attribute.xmlValue = value;
   }

   public void setNonNormalizedValue(int attrIndex, String attrValue) {
      if (attrValue == null) {
         attrValue = this.fAttributes[attrIndex].value;
      }

      this.fAttributes[attrIndex].nonNormalizedValue = attrValue;
   }

   public String getNonNormalizedValue(int attrIndex) {
      String value = this.fAttributes[attrIndex].nonNormalizedValue;
      return value;
   }

   public void setSpecified(int attrIndex, boolean specified) {
      this.fAttributes[attrIndex].specified = specified;
   }

   public boolean isSpecified(int attrIndex) {
      return this.fAttributes[attrIndex].specified;
   }

   public int getLength() {
      return this.fLength;
   }

   public String getType(int index) {
      return index >= 0 && index < this.fLength ? this.getReportableType(this.fAttributes[index].type) : null;
   }

   public String getType(String qname) {
      int index = this.getIndex(qname);
      return index != -1 ? this.getReportableType(this.fAttributes[index].type) : null;
   }

   public String getValue(int index) {
      if (index >= 0 && index < this.fLength) {
         if (this.fAttributes[index].value == null && this.fAttributes[index].xmlValue != null) {
            this.fAttributes[index].value = this.fAttributes[index].xmlValue.toString();
         }

         return this.fAttributes[index].value;
      } else {
         return null;
      }
   }

   public String getValue(String qname) {
      int index = this.getIndex(qname);
      if (index == -1) {
         return null;
      } else {
         if (this.fAttributes[index].value == null) {
            this.fAttributes[index].value = this.fAttributes[index].xmlValue.toString();
         }

         return this.fAttributes[index].value;
      }
   }

   public String getName(int index) {
      return index >= 0 && index < this.fLength ? this.fAttributes[index].name.rawname : null;
   }

   public int getIndex(String qName) {
      for(int i = 0; i < this.fLength; ++i) {
         XMLAttributesImpl.Attribute attribute = this.fAttributes[i];
         if (attribute.name.rawname != null && attribute.name.rawname.equals(qName)) {
            return i;
         }
      }

      return -1;
   }

   public int getIndex(String uri, String localPart) {
      for(int i = 0; i < this.fLength; ++i) {
         XMLAttributesImpl.Attribute attribute = this.fAttributes[i];
         if (attribute.name.localpart != null && attribute.name.localpart.equals(localPart) && (uri == attribute.name.uri || uri != null && attribute.name.uri != null && attribute.name.uri.equals(uri))) {
            return i;
         }
      }

      return -1;
   }

   public int getIndexByLocalName(String localPart) {
      for(int i = 0; i < this.fLength; ++i) {
         XMLAttributesImpl.Attribute attribute = this.fAttributes[i];
         if (attribute.name.localpart != null && attribute.name.localpart.equals(localPart)) {
            return i;
         }
      }

      return -1;
   }

   public String getLocalName(int index) {
      if (!this.fNamespaces) {
         return "";
      } else {
         return index >= 0 && index < this.fLength ? this.fAttributes[index].name.localpart : null;
      }
   }

   public String getQName(int index) {
      if (index >= 0 && index < this.fLength) {
         String rawname = this.fAttributes[index].name.rawname;
         return rawname != null ? rawname : "";
      } else {
         return null;
      }
   }

   public QName getQualifiedName(int index) {
      return index >= 0 && index < this.fLength ? this.fAttributes[index].name : null;
   }

   public String getType(String uri, String localName) {
      if (!this.fNamespaces) {
         return null;
      } else {
         int index = this.getIndex(uri, localName);
         return index != -1 ? this.getType(index) : null;
      }
   }

   public int getIndexFast(String qName) {
      for(int i = 0; i < this.fLength; ++i) {
         XMLAttributesImpl.Attribute attribute = this.fAttributes[i];
         if (attribute.name.rawname == qName) {
            return i;
         }
      }

      return -1;
   }

   public void addAttributeNS(QName name, String type, String value) {
      int index = this.fLength;
      if (this.fLength++ == this.fAttributes.length) {
         XMLAttributesImpl.Attribute[] attributes;
         if (this.fLength < 20) {
            attributes = new XMLAttributesImpl.Attribute[this.fAttributes.length + 4];
         } else {
            attributes = new XMLAttributesImpl.Attribute[this.fAttributes.length << 1];
         }

         System.arraycopy(this.fAttributes, 0, attributes, 0, this.fAttributes.length);

         for(int i = this.fAttributes.length; i < attributes.length; ++i) {
            attributes[i] = new XMLAttributesImpl.Attribute();
         }

         this.fAttributes = attributes;
      }

      XMLAttributesImpl.Attribute attribute = this.fAttributes[index];
      attribute.name.setValues(name);
      attribute.type = type;
      attribute.value = value;
      attribute.nonNormalizedValue = value;
      attribute.specified = false;
      attribute.augs.removeAllItems();
   }

   public QName checkDuplicatesNS() {
      int length = this.fLength;
      if (length > 20) {
         return this.checkManyDuplicatesNS();
      } else {
         XMLAttributesImpl.Attribute[] attributes = this.fAttributes;

         for(int i = 0; i < length - 1; ++i) {
            XMLAttributesImpl.Attribute att1 = attributes[i];

            for(int j = i + 1; j < length; ++j) {
               XMLAttributesImpl.Attribute att2 = attributes[j];
               if (att1.name.localpart == att2.name.localpart && att1.name.uri == att2.name.uri) {
                  return att2.name;
               }
            }
         }

         return null;
      }
   }

   private QName checkManyDuplicatesNS() {
      this.fIsTableViewConsistent = false;
      this.prepareTableView();
      int length = this.fLength;
      XMLAttributesImpl.Attribute[] attributes = this.fAttributes;
      XMLAttributesImpl.Attribute[] attributeTableView = this.fAttributeTableView;
      int[] attributeTableViewChainState = this.fAttributeTableViewChainState;
      int largeCount = this.fLargeCount;

      for(int i = 0; i < length; ++i) {
         XMLAttributesImpl.Attribute attr = attributes[i];
         int bucket = this.getTableViewBucket(attr.name.localpart, attr.name.uri);
         if (attributeTableViewChainState[bucket] != largeCount) {
            attributeTableViewChainState[bucket] = largeCount;
            attr.next = null;
            attributeTableView[bucket] = attr;
         } else {
            int collisionCount = 0;

            for(XMLAttributesImpl.Attribute found = attributeTableView[bucket]; found != null; ++collisionCount) {
               if (found.name.localpart == attr.name.localpart && found.name.uri == attr.name.uri) {
                  return attr.name;
               }

               found = found.next;
            }

            if (collisionCount >= 40) {
               this.rebalanceTableViewNS(i + 1);
               largeCount = this.fLargeCount;
            } else {
               attr.next = attributeTableView[bucket];
               attributeTableView[bucket] = attr;
            }
         }
      }

      return null;
   }

   public int getIndexFast(String uri, String localPart) {
      for(int i = 0; i < this.fLength; ++i) {
         XMLAttributesImpl.Attribute attribute = this.fAttributes[i];
         if (attribute.name.localpart == localPart && attribute.name.uri == uri) {
            return i;
         }
      }

      return -1;
   }

   private String getReportableType(String type) {
      return type.charAt(0) == '(' ? "NMTOKEN" : type;
   }

   protected int getTableViewBucket(String qname) {
      return (this.hash(qname) & Integer.MAX_VALUE) % this.fTableViewBuckets;
   }

   protected int getTableViewBucket(String localpart, String uri) {
      return uri == null ? (this.hash(localpart) & Integer.MAX_VALUE) % this.fTableViewBuckets : (this.hash(localpart, uri) & Integer.MAX_VALUE) % this.fTableViewBuckets;
   }

   private int hash(String localpart) {
      return this.fHashMultipliers == null ? localpart.hashCode() : this.hash0(localpart);
   }

   private int hash(String localpart, String uri) {
      return this.fHashMultipliers == null ? localpart.hashCode() + uri.hashCode() * 31 : this.hash0(localpart) + this.hash0(uri) * this.fHashMultipliers[32];
   }

   private int hash0(String symbol) {
      int code = 0;
      int length = symbol.length();
      int[] multipliers = this.fHashMultipliers;

      for(int i = 0; i < length; ++i) {
         code = code * multipliers[i & 31] + symbol.charAt(i);
      }

      return code;
   }

   protected void cleanTableView() {
      if (++this.fLargeCount < 0) {
         if (this.fAttributeTableViewChainState != null) {
            for(int i = this.fTableViewBuckets - 1; i >= 0; --i) {
               this.fAttributeTableViewChainState[i] = 0;
            }
         }

         this.fLargeCount = 1;
      }

   }

   private void growTableView() {
      int length = this.fLength;
      int tableViewBuckets = this.fTableViewBuckets;

      do {
         tableViewBuckets = (tableViewBuckets << 1) + 1;
         if (tableViewBuckets < 0) {
            tableViewBuckets = Integer.MAX_VALUE;
            break;
         }
      } while(length > tableViewBuckets);

      this.fTableViewBuckets = tableViewBuckets;
      this.fAttributeTableView = null;
      this.fLargeCount = 1;
   }

   protected void prepareTableView() {
      if (this.fLength > this.fTableViewBuckets) {
         this.growTableView();
      }

      if (this.fAttributeTableView == null) {
         this.fAttributeTableView = new XMLAttributesImpl.Attribute[this.fTableViewBuckets];
         this.fAttributeTableViewChainState = new int[this.fTableViewBuckets];
      } else {
         this.cleanTableView();
      }

   }

   protected void prepareAndPopulateTableView() {
      this.prepareAndPopulateTableView(this.fLength);
   }

   private void prepareAndPopulateTableView(int count) {
      this.prepareTableView();

      for(int i = 0; i < count; ++i) {
         XMLAttributesImpl.Attribute attr = this.fAttributes[i];
         int bucket = this.getTableViewBucket(attr.name.rawname);
         if (this.fAttributeTableViewChainState[bucket] != this.fLargeCount) {
            this.fAttributeTableViewChainState[bucket] = this.fLargeCount;
            attr.next = null;
            this.fAttributeTableView[bucket] = attr;
         } else {
            attr.next = this.fAttributeTableView[bucket];
            this.fAttributeTableView[bucket] = attr;
         }
      }

   }

   public String getPrefix(int index) {
      if (index >= 0 && index < this.fLength) {
         String prefix = this.fAttributes[index].name.prefix;
         return prefix != null ? prefix : "";
      } else {
         return null;
      }
   }

   public String getURI(int index) {
      if (index >= 0 && index < this.fLength) {
         String uri = this.fAttributes[index].name.uri;
         return uri;
      } else {
         return null;
      }
   }

   public String getValue(String uri, String localName) {
      int index = this.getIndex(uri, localName);
      return index != -1 ? this.getValue(index) : null;
   }

   public Augmentations getAugmentations(String uri, String localName) {
      int index = this.getIndex(uri, localName);
      return index != -1 ? this.fAttributes[index].augs : null;
   }

   public Augmentations getAugmentations(String qName) {
      int index = this.getIndex(qName);
      return index != -1 ? this.fAttributes[index].augs : null;
   }

   public Augmentations getAugmentations(int attributeIndex) {
      return attributeIndex >= 0 && attributeIndex < this.fLength ? this.fAttributes[attributeIndex].augs : null;
   }

   public void setAugmentations(int attrIndex, Augmentations augs) {
      this.fAttributes[attrIndex].augs = augs;
   }

   public void setURI(int attrIndex, String uri) {
      this.fAttributes[attrIndex].name.uri = uri;
   }

   public void setSchemaId(int attrIndex, boolean schemaId) {
      this.fAttributes[attrIndex].schemaId = schemaId;
   }

   public boolean getSchemaId(int index) {
      return index >= 0 && index < this.fLength ? this.fAttributes[index].schemaId : false;
   }

   public boolean getSchemaId(String qname) {
      int index = this.getIndex(qname);
      return index != -1 ? this.fAttributes[index].schemaId : false;
   }

   public boolean getSchemaId(String uri, String localName) {
      if (!this.fNamespaces) {
         return false;
      } else {
         int index = this.getIndex(uri, localName);
         return index != -1 ? this.fAttributes[index].schemaId : false;
      }
   }

   public void refresh() {
      if (this.fLength > 0) {
         for(int i = 0; i < this.fLength; ++i) {
            this.getValue(i);
         }
      }

   }

   public void refresh(int pos) {
   }

   private void prepareAndPopulateTableViewNS(int count) {
      this.prepareTableView();

      for(int i = 0; i < count; ++i) {
         XMLAttributesImpl.Attribute attr = this.fAttributes[i];
         int bucket = this.getTableViewBucket(attr.name.localpart, attr.name.uri);
         if (this.fAttributeTableViewChainState[bucket] != this.fLargeCount) {
            this.fAttributeTableViewChainState[bucket] = this.fLargeCount;
            attr.next = null;
            this.fAttributeTableView[bucket] = attr;
         } else {
            attr.next = this.fAttributeTableView[bucket];
            this.fAttributeTableView[bucket] = attr;
         }
      }

   }

   private void rebalanceTableView(int count) {
      if (this.fHashMultipliers == null) {
         this.fHashMultipliers = new int[33];
      }

      PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
      this.prepareAndPopulateTableView(count);
   }

   private void rebalanceTableViewNS(int count) {
      if (this.fHashMultipliers == null) {
         this.fHashMultipliers = new int[33];
      }

      PrimeNumberSequenceGenerator.generateSequence(this.fHashMultipliers);
      this.prepareAndPopulateTableViewNS(count);
   }

   static class Attribute {
      public QName name = new QName();
      public String type;
      public String value;
      public XMLString xmlValue;
      public String nonNormalizedValue;
      public boolean specified;
      public boolean schemaId;
      public Augmentations augs = new AugmentationsImpl();
      public XMLAttributesImpl.Attribute next;
   }
}
