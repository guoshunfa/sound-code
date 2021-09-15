package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.xni.QName;

public class SimpleContentModel implements ContentModelValidator {
   public static final short CHOICE = -1;
   public static final short SEQUENCE = -1;
   private QName fFirstChild = new QName();
   private QName fSecondChild = new QName();
   private int fOperator;

   public SimpleContentModel(short operator, QName firstChild, QName secondChild) {
      this.fFirstChild.setValues(firstChild);
      if (secondChild != null) {
         this.fSecondChild.setValues(secondChild);
      } else {
         this.fSecondChild.clear();
      }

      this.fOperator = operator;
   }

   public int validate(QName[] children, int offset, int length) {
      int index;
      switch(this.fOperator) {
      case 0:
         if (length == 0) {
            return 0;
         }

         if (children[offset].rawname != this.fFirstChild.rawname) {
            return 0;
         }

         if (length > 1) {
            return 1;
         }
         break;
      case 1:
         if (length == 1 && children[offset].rawname != this.fFirstChild.rawname) {
            return 0;
         }

         if (length > 1) {
            return 1;
         }
         break;
      case 2:
         if (length > 0) {
            for(index = 0; index < length; ++index) {
               if (children[offset + index].rawname != this.fFirstChild.rawname) {
                  return index;
               }
            }
         }
         break;
      case 3:
         if (length == 0) {
            return 0;
         }

         for(index = 0; index < length; ++index) {
            if (children[offset + index].rawname != this.fFirstChild.rawname) {
               return index;
            }
         }

         return -1;
      case 4:
         if (length == 0) {
            return 0;
         }

         if (children[offset].rawname != this.fFirstChild.rawname && children[offset].rawname != this.fSecondChild.rawname) {
            return 0;
         }

         if (length > 1) {
            return 1;
         }
         break;
      case 5:
         if (length != 2) {
            if (length > 2) {
               return 2;
            }

            return length;
         }

         if (children[offset].rawname != this.fFirstChild.rawname) {
            return 0;
         }

         if (children[offset + 1].rawname != this.fSecondChild.rawname) {
            return 1;
         }
         break;
      default:
         throw new RuntimeException("ImplementationMessages.VAL_CST");
      }

      return -1;
   }
}
