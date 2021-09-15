package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.impl.xs.util.StringListImpl;
import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import com.sun.org.apache.xerces.internal.xs.StringList;
import com.sun.org.apache.xerces.internal.xs.XSAnnotation;
import com.sun.org.apache.xerces.internal.xs.XSNamespaceItem;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import com.sun.org.apache.xerces.internal.xs.XSWildcard;

public class XSWildcardDecl implements XSWildcard {
   public static final String ABSENT = null;
   public short fType = 1;
   public short fProcessContents = 1;
   public String[] fNamespaceList;
   public XSObjectList fAnnotations = null;
   private String fDescription = null;

   public boolean allowNamespace(String namespace) {
      if (this.fType == 1) {
         return true;
      } else {
         int i;
         if (this.fType == 2) {
            boolean found = false;
            i = this.fNamespaceList.length;

            for(int i = 0; i < i && !found; ++i) {
               if (namespace == this.fNamespaceList[i]) {
                  found = true;
               }
            }

            if (!found) {
               return true;
            }
         }

         if (this.fType == 3) {
            int listNum = this.fNamespaceList.length;

            for(i = 0; i < listNum; ++i) {
               if (namespace == this.fNamespaceList[i]) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean isSubsetOf(XSWildcardDecl superWildcard) {
      if (superWildcard == null) {
         return false;
      } else if (superWildcard.fType == 1) {
         return true;
      } else if (this.fType == 2 && superWildcard.fType == 2 && this.fNamespaceList[0] == superWildcard.fNamespaceList[0]) {
         return true;
      } else {
         if (this.fType == 3) {
            if (superWildcard.fType == 3 && this.subset2sets(this.fNamespaceList, superWildcard.fNamespaceList)) {
               return true;
            }

            if (superWildcard.fType == 2 && !this.elementInSet(superWildcard.fNamespaceList[0], this.fNamespaceList) && !this.elementInSet(ABSENT, this.fNamespaceList)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean weakerProcessContents(XSWildcardDecl superWildcard) {
      return this.fProcessContents == 3 && superWildcard.fProcessContents == 1 || this.fProcessContents == 2 && superWildcard.fProcessContents != 2;
   }

   public XSWildcardDecl performUnionWith(XSWildcardDecl wildcard, short processContents) {
      if (wildcard == null) {
         return null;
      } else {
         XSWildcardDecl unionWildcard = new XSWildcardDecl();
         unionWildcard.fProcessContents = processContents;
         if (this.areSame(wildcard)) {
            unionWildcard.fType = this.fType;
            unionWildcard.fNamespaceList = this.fNamespaceList;
         } else if (this.fType != 1 && wildcard.fType != 1) {
            if (this.fType == 3 && wildcard.fType == 3) {
               unionWildcard.fType = 3;
               unionWildcard.fNamespaceList = this.union2sets(this.fNamespaceList, wildcard.fNamespaceList);
            } else if (this.fType == 2 && wildcard.fType == 2) {
               unionWildcard.fType = 2;
               unionWildcard.fNamespaceList = new String[2];
               unionWildcard.fNamespaceList[0] = ABSENT;
               unionWildcard.fNamespaceList[1] = ABSENT;
            } else if (this.fType == 2 && wildcard.fType == 3 || this.fType == 3 && wildcard.fType == 2) {
               String[] other = null;
               String[] list = null;
               if (this.fType == 2) {
                  other = this.fNamespaceList;
                  list = wildcard.fNamespaceList;
               } else {
                  other = wildcard.fNamespaceList;
                  list = this.fNamespaceList;
               }

               boolean foundAbsent = this.elementInSet(ABSENT, list);
               if (other[0] != ABSENT) {
                  boolean foundNS = this.elementInSet(other[0], list);
                  if (foundNS && foundAbsent) {
                     unionWildcard.fType = 1;
                  } else if (foundNS && !foundAbsent) {
                     unionWildcard.fType = 2;
                     unionWildcard.fNamespaceList = new String[2];
                     unionWildcard.fNamespaceList[0] = ABSENT;
                     unionWildcard.fNamespaceList[1] = ABSENT;
                  } else {
                     if (!foundNS && foundAbsent) {
                        return null;
                     }

                     unionWildcard.fType = 2;
                     unionWildcard.fNamespaceList = other;
                  }
               } else if (foundAbsent) {
                  unionWildcard.fType = 1;
               } else {
                  unionWildcard.fType = 2;
                  unionWildcard.fNamespaceList = other;
               }
            }
         } else {
            unionWildcard.fType = 1;
         }

         return unionWildcard;
      }
   }

   public XSWildcardDecl performIntersectionWith(XSWildcardDecl wildcard, short processContents) {
      if (wildcard == null) {
         return null;
      } else {
         XSWildcardDecl intersectWildcard = new XSWildcardDecl();
         intersectWildcard.fProcessContents = processContents;
         if (this.areSame(wildcard)) {
            intersectWildcard.fType = this.fType;
            intersectWildcard.fNamespaceList = this.fNamespaceList;
         } else {
            XSWildcardDecl other;
            if (this.fType != 1 && wildcard.fType != 1) {
               if (this.fType == 2 && wildcard.fType == 3 || this.fType == 3 && wildcard.fType == 2) {
                  other = null;
                  String[] other = null;
                  String[] list;
                  if (this.fType == 2) {
                     other = this.fNamespaceList;
                     list = wildcard.fNamespaceList;
                  } else {
                     other = wildcard.fNamespaceList;
                     list = this.fNamespaceList;
                  }

                  int listSize = list.length;
                  String[] intersect = new String[listSize];
                  int newSize = 0;

                  for(int i = 0; i < listSize; ++i) {
                     if (list[i] != other[0] && list[i] != ABSENT) {
                        intersect[newSize++] = list[i];
                     }
                  }

                  intersectWildcard.fType = 3;
                  intersectWildcard.fNamespaceList = new String[newSize];
                  System.arraycopy(intersect, 0, intersectWildcard.fNamespaceList, 0, newSize);
               } else if (this.fType == 3 && wildcard.fType == 3) {
                  intersectWildcard.fType = 3;
                  intersectWildcard.fNamespaceList = this.intersect2sets(this.fNamespaceList, wildcard.fNamespaceList);
               } else if (this.fType == 2 && wildcard.fType == 2) {
                  if (this.fNamespaceList[0] != ABSENT && wildcard.fNamespaceList[0] != ABSENT) {
                     return null;
                  }

                  other = this;
                  if (this.fNamespaceList[0] == ABSENT) {
                     other = wildcard;
                  }

                  intersectWildcard.fType = other.fType;
                  intersectWildcard.fNamespaceList = other.fNamespaceList;
               }
            } else {
               other = this;
               if (this.fType == 1) {
                  other = wildcard;
               }

               intersectWildcard.fType = other.fType;
               intersectWildcard.fNamespaceList = other.fNamespaceList;
            }
         }

         return intersectWildcard;
      }
   }

   private boolean areSame(XSWildcardDecl wildcard) {
      if (this.fType == wildcard.fType) {
         if (this.fType == 1) {
            return true;
         }

         if (this.fType == 2) {
            return this.fNamespaceList[0] == wildcard.fNamespaceList[0];
         }

         if (this.fNamespaceList.length == wildcard.fNamespaceList.length) {
            for(int i = 0; i < this.fNamespaceList.length; ++i) {
               if (!this.elementInSet(this.fNamespaceList[i], wildcard.fNamespaceList)) {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   String[] intersect2sets(String[] one, String[] theOther) {
      String[] result = new String[Math.min(one.length, theOther.length)];
      int count = 0;

      for(int i = 0; i < one.length; ++i) {
         if (this.elementInSet(one[i], theOther)) {
            result[count++] = one[i];
         }
      }

      String[] result2 = new String[count];
      System.arraycopy(result, 0, result2, 0, count);
      return result2;
   }

   String[] union2sets(String[] one, String[] theOther) {
      String[] result1 = new String[one.length];
      int count = 0;

      for(int i = 0; i < one.length; ++i) {
         if (!this.elementInSet(one[i], theOther)) {
            result1[count++] = one[i];
         }
      }

      String[] result2 = new String[count + theOther.length];
      System.arraycopy(result1, 0, result2, 0, count);
      System.arraycopy(theOther, 0, result2, count, theOther.length);
      return result2;
   }

   boolean subset2sets(String[] subSet, String[] superSet) {
      for(int i = 0; i < subSet.length; ++i) {
         if (!this.elementInSet(subSet[i], superSet)) {
            return false;
         }
      }

      return true;
   }

   boolean elementInSet(String ele, String[] set) {
      boolean found = false;

      for(int i = 0; i < set.length && !found; ++i) {
         if (ele == set[i]) {
            found = true;
         }
      }

      return found;
   }

   public String toString() {
      if (this.fDescription == null) {
         StringBuffer buffer = new StringBuffer();
         buffer.append("WC[");
         switch(this.fType) {
         case 1:
            buffer.append("##any");
            break;
         case 2:
            buffer.append("##other");
            buffer.append(":\"");
            if (this.fNamespaceList[0] != null) {
               buffer.append(this.fNamespaceList[0]);
            }

            buffer.append("\"");
            break;
         case 3:
            if (this.fNamespaceList.length != 0) {
               buffer.append("\"");
               if (this.fNamespaceList[0] != null) {
                  buffer.append(this.fNamespaceList[0]);
               }

               buffer.append("\"");

               for(int i = 1; i < this.fNamespaceList.length; ++i) {
                  buffer.append(",\"");
                  if (this.fNamespaceList[i] != null) {
                     buffer.append(this.fNamespaceList[i]);
                  }

                  buffer.append("\"");
               }
            }
         }

         buffer.append(']');
         this.fDescription = buffer.toString();
      }

      return this.fDescription;
   }

   public short getType() {
      return 9;
   }

   public String getName() {
      return null;
   }

   public String getNamespace() {
      return null;
   }

   public short getConstraintType() {
      return this.fType;
   }

   public StringList getNsConstraintList() {
      return new StringListImpl(this.fNamespaceList, this.fNamespaceList == null ? 0 : this.fNamespaceList.length);
   }

   public short getProcessContents() {
      return this.fProcessContents;
   }

   public String getProcessContentsAsString() {
      switch(this.fProcessContents) {
      case 1:
         return "strict";
      case 2:
         return "skip";
      case 3:
         return "lax";
      default:
         return "invalid value";
      }
   }

   public XSAnnotation getAnnotation() {
      return this.fAnnotations != null ? (XSAnnotation)this.fAnnotations.item(0) : null;
   }

   public XSObjectList getAnnotations() {
      return (XSObjectList)(this.fAnnotations != null ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST);
   }

   public XSNamespaceItem getNamespaceItem() {
      return null;
   }
}
