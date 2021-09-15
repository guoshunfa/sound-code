package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.xni.QName;

public class MixedContentModel implements ContentModelValidator {
   private int fCount;
   private QName[] fChildren;
   private int[] fChildrenType;
   private boolean fOrdered;

   public MixedContentModel(QName[] children, int[] type, int offset, int length, boolean ordered) {
      this.fCount = length;
      this.fChildren = new QName[this.fCount];
      this.fChildrenType = new int[this.fCount];

      for(int i = 0; i < this.fCount; ++i) {
         this.fChildren[i] = new QName(children[offset + i]);
         this.fChildrenType[i] = type[offset + i];
      }

      this.fOrdered = ordered;
   }

   public int validate(QName[] children, int offset, int length) {
      int outIndex;
      int type;
      String uri;
      if (this.fOrdered) {
         outIndex = 0;

         for(int outIndex = 0; outIndex < length; ++outIndex) {
            QName curChild = children[offset + outIndex];
            if (curChild.localpart != null) {
               type = this.fChildrenType[outIndex];
               if (type == 0) {
                  if (this.fChildren[outIndex].rawname != children[offset + outIndex].rawname) {
                     return outIndex;
                  }
               } else if (type == 6) {
                  uri = this.fChildren[outIndex].uri;
                  if (uri != null && uri != children[outIndex].uri) {
                     return outIndex;
                  }
               } else if (type == 8) {
                  if (children[outIndex].uri != null) {
                     return outIndex;
                  }
               } else if (type == 7 && this.fChildren[outIndex].uri == children[outIndex].uri) {
                  return outIndex;
               }

               ++outIndex;
            }
         }
      } else {
         for(outIndex = 0; outIndex < length; ++outIndex) {
            QName curChild = children[offset + outIndex];
            if (curChild.localpart != null) {
               int inIndex;
               for(inIndex = 0; inIndex < this.fCount; ++inIndex) {
                  type = this.fChildrenType[inIndex];
                  if (type == 0) {
                     if (curChild.rawname == this.fChildren[inIndex].rawname) {
                        break;
                     }
                  } else if (type == 6) {
                     uri = this.fChildren[inIndex].uri;
                     if (uri == null || uri == children[outIndex].uri) {
                        break;
                     }
                  } else if (type == 8) {
                     if (children[outIndex].uri == null) {
                        break;
                     }
                  } else if (type == 7 && this.fChildren[inIndex].uri != children[outIndex].uri) {
                     break;
                  }
               }

               if (inIndex == this.fCount) {
                  return outIndex;
               }
            }
         }
      }

      return -1;
   }
}
