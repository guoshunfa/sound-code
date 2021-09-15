package org.xml.sax.helpers;

import java.util.Vector;
import org.xml.sax.AttributeList;

/** @deprecated */
public class AttributeListImpl implements AttributeList {
   Vector names = new Vector();
   Vector types = new Vector();
   Vector values = new Vector();

   public AttributeListImpl() {
   }

   public AttributeListImpl(AttributeList atts) {
      this.setAttributeList(atts);
   }

   public void setAttributeList(AttributeList atts) {
      int count = atts.getLength();
      this.clear();

      for(int i = 0; i < count; ++i) {
         this.addAttribute(atts.getName(i), atts.getType(i), atts.getValue(i));
      }

   }

   public void addAttribute(String name, String type, String value) {
      this.names.addElement(name);
      this.types.addElement(type);
      this.values.addElement(value);
   }

   public void removeAttribute(String name) {
      int i = this.names.indexOf(name);
      if (i >= 0) {
         this.names.removeElementAt(i);
         this.types.removeElementAt(i);
         this.values.removeElementAt(i);
      }

   }

   public void clear() {
      this.names.removeAllElements();
      this.types.removeAllElements();
      this.values.removeAllElements();
   }

   public int getLength() {
      return this.names.size();
   }

   public String getName(int i) {
      if (i < 0) {
         return null;
      } else {
         try {
            return (String)this.names.elementAt(i);
         } catch (ArrayIndexOutOfBoundsException var3) {
            return null;
         }
      }
   }

   public String getType(int i) {
      if (i < 0) {
         return null;
      } else {
         try {
            return (String)this.types.elementAt(i);
         } catch (ArrayIndexOutOfBoundsException var3) {
            return null;
         }
      }
   }

   public String getValue(int i) {
      if (i < 0) {
         return null;
      } else {
         try {
            return (String)this.values.elementAt(i);
         } catch (ArrayIndexOutOfBoundsException var3) {
            return null;
         }
      }
   }

   public String getType(String name) {
      return this.getType(this.names.indexOf(name));
   }

   public String getValue(String name) {
      return this.getValue(this.names.indexOf(name));
   }
}
