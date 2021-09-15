package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;
import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.XSConstraints;
import com.sun.org.apache.xerces.internal.impl.xs.XSElementDecl;
import com.sun.org.apache.xerces.internal.impl.xs.XSWildcardDecl;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class XSDFACM implements XSCMValidator {
   private static final boolean DEBUG = false;
   private static final boolean DEBUG_VALIDATE_CONTENT = false;
   private Object[] fElemMap = null;
   private int[] fElemMapType = null;
   private int[] fElemMapId = null;
   private int fElemMapSize = 0;
   private boolean[] fFinalStateFlags = null;
   private CMStateSet[] fFollowList = null;
   private CMNode fHeadNode = null;
   private int fLeafCount = 0;
   private XSCMLeaf[] fLeafList = null;
   private int[] fLeafListType = null;
   private int[][] fTransTable = (int[][])null;
   private XSDFACM.Occurence[] fCountingStates = null;
   private int fTransTableSize = 0;
   private int[] fElemMapCounter;
   private int[] fElemMapCounterLowerBound;
   private int[] fElemMapCounterUpperBound;
   private static long time = 0L;

   public XSDFACM(CMNode syntaxTree, int leafCount) {
      this.fLeafCount = leafCount;
      this.buildDFA(syntaxTree);
   }

   public boolean isFinalState(int state) {
      return state < 0 ? false : this.fFinalStateFlags[state];
   }

   public Object oneTransition(QName curElem, int[] state, SubstitutionGroupHandler subGroupHandler) {
      int curState = state[0];
      if (curState != -1 && curState != -2) {
         int nextState = 0;
         int elemIndex = 0;

         Object matchingDecl;
         for(matchingDecl = null; elemIndex < this.fElemMapSize; ++elemIndex) {
            nextState = this.fTransTable[curState][elemIndex];
            if (nextState != -1) {
               int type = this.fElemMapType[elemIndex];
               int var10002;
               if (type == 1) {
                  matchingDecl = subGroupHandler.getMatchingElemDecl(curElem, (XSElementDecl)this.fElemMap[elemIndex]);
                  if (matchingDecl != null) {
                     if (this.fElemMapCounter[elemIndex] >= 0) {
                        var10002 = this.fElemMapCounter[elemIndex]++;
                     }
                     break;
                  }
               } else if (type == 2 && ((XSWildcardDecl)this.fElemMap[elemIndex]).allowNamespace(curElem.uri)) {
                  matchingDecl = this.fElemMap[elemIndex];
                  if (this.fElemMapCounter[elemIndex] >= 0) {
                     var10002 = this.fElemMapCounter[elemIndex]++;
                  }
                  break;
               }
            }
         }

         if (elemIndex == this.fElemMapSize) {
            state[1] = state[0];
            state[0] = -1;
            return this.findMatchingDecl(curElem, subGroupHandler);
         } else {
            if (this.fCountingStates != null) {
               XSDFACM.Occurence o = this.fCountingStates[curState];
               if (o != null) {
                  if (curState == nextState) {
                     if (++state[2] > o.maxOccurs && o.maxOccurs != -1) {
                        return this.findMatchingDecl(curElem, state, subGroupHandler, elemIndex);
                     }
                  } else {
                     if (state[2] < o.minOccurs) {
                        state[1] = state[0];
                        state[0] = -1;
                        return this.findMatchingDecl(curElem, subGroupHandler);
                     }

                     o = this.fCountingStates[nextState];
                     if (o != null) {
                        state[2] = elemIndex == o.elemIndex ? 1 : 0;
                     }
                  }
               } else {
                  o = this.fCountingStates[nextState];
                  if (o != null) {
                     state[2] = elemIndex == o.elemIndex ? 1 : 0;
                  }
               }
            }

            state[0] = nextState;
            return matchingDecl;
         }
      } else {
         if (curState == -1) {
            state[0] = -2;
         }

         return this.findMatchingDecl(curElem, subGroupHandler);
      }
   }

   Object findMatchingDecl(QName curElem, SubstitutionGroupHandler subGroupHandler) {
      Object matchingDecl = null;

      for(int elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
         int type = this.fElemMapType[elemIndex];
         if (type == 1) {
            matchingDecl = subGroupHandler.getMatchingElemDecl(curElem, (XSElementDecl)this.fElemMap[elemIndex]);
            if (matchingDecl != null) {
               return matchingDecl;
            }
         } else if (type == 2 && ((XSWildcardDecl)this.fElemMap[elemIndex]).allowNamespace(curElem.uri)) {
            return this.fElemMap[elemIndex];
         }
      }

      return null;
   }

   Object findMatchingDecl(QName curElem, int[] state, SubstitutionGroupHandler subGroupHandler, int elemIndex) {
      int curState = state[0];
      int nextState = 0;
      Object matchingDecl = null;

      while(true) {
         ++elemIndex;
         if (elemIndex >= this.fElemMapSize) {
            break;
         }

         nextState = this.fTransTable[curState][elemIndex];
         if (nextState != -1) {
            int type = this.fElemMapType[elemIndex];
            if (type == 1) {
               matchingDecl = subGroupHandler.getMatchingElemDecl(curElem, (XSElementDecl)this.fElemMap[elemIndex]);
               if (matchingDecl != null) {
                  break;
               }
            } else if (type == 2 && ((XSWildcardDecl)this.fElemMap[elemIndex]).allowNamespace(curElem.uri)) {
               matchingDecl = this.fElemMap[elemIndex];
               break;
            }
         }
      }

      if (elemIndex == this.fElemMapSize) {
         state[1] = state[0];
         state[0] = -1;
         return this.findMatchingDecl(curElem, subGroupHandler);
      } else {
         state[0] = nextState;
         XSDFACM.Occurence o = this.fCountingStates[nextState];
         if (o != null) {
            state[2] = elemIndex == o.elemIndex ? 1 : 0;
         }

         return matchingDecl;
      }
   }

   public int[] startContentModel() {
      for(int elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
         if (this.fElemMapCounter[elemIndex] != -1) {
            this.fElemMapCounter[elemIndex] = 0;
         }
      }

      return new int[3];
   }

   public boolean endContentModel(int[] state) {
      int curState = state[0];
      if (this.fFinalStateFlags[curState]) {
         if (this.fCountingStates != null) {
            XSDFACM.Occurence o = this.fCountingStates[curState];
            if (o != null && state[2] < o.minOccurs) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void buildDFA(CMNode syntaxTree) {
      int EOCPos = this.fLeafCount;
      XSCMLeaf nodeEOC = new XSCMLeaf(1, (Object)null, -1, this.fLeafCount++);
      this.fHeadNode = new XSCMBinOp(102, syntaxTree, nodeEOC);
      this.fLeafList = new XSCMLeaf[this.fLeafCount];
      this.fLeafListType = new int[this.fLeafCount];
      this.postTreeBuildInit(this.fHeadNode);
      this.fFollowList = new CMStateSet[this.fLeafCount];

      for(int index = 0; index < this.fLeafCount; ++index) {
         this.fFollowList[index] = new CMStateSet(this.fLeafCount);
      }

      this.calcFollowList(this.fHeadNode);
      this.fElemMap = new Object[this.fLeafCount];
      this.fElemMapType = new int[this.fLeafCount];
      this.fElemMapId = new int[this.fLeafCount];
      this.fElemMapCounter = new int[this.fLeafCount];
      this.fElemMapCounterLowerBound = new int[this.fLeafCount];
      this.fElemMapCounterUpperBound = new int[this.fLeafCount];
      this.fElemMapSize = 0;
      XSDFACM.Occurence[] elemOccurenceMap = null;

      int inIndex;
      int curArraySize;
      for(int outIndex = 0; outIndex < this.fLeafCount; ++outIndex) {
         this.fElemMap[outIndex] = null;
         inIndex = 0;

         for(curArraySize = this.fLeafList[outIndex].getParticleId(); inIndex < this.fElemMapSize && curArraySize != this.fElemMapId[inIndex]; ++inIndex) {
         }

         if (inIndex == this.fElemMapSize) {
            XSCMLeaf leaf = this.fLeafList[outIndex];
            this.fElemMap[this.fElemMapSize] = leaf.getLeaf();
            if (leaf instanceof XSCMRepeatingLeaf) {
               if (elemOccurenceMap == null) {
                  elemOccurenceMap = new XSDFACM.Occurence[this.fLeafCount];
               }

               elemOccurenceMap[this.fElemMapSize] = new XSDFACM.Occurence((XSCMRepeatingLeaf)leaf, this.fElemMapSize);
            }

            this.fElemMapType[this.fElemMapSize] = this.fLeafListType[outIndex];
            this.fElemMapId[this.fElemMapSize] = curArraySize;
            int[] bounds = (int[])((int[])leaf.getUserData());
            if (bounds != null) {
               this.fElemMapCounter[this.fElemMapSize] = 0;
               this.fElemMapCounterLowerBound[this.fElemMapSize] = bounds[0];
               this.fElemMapCounterUpperBound[this.fElemMapSize] = bounds[1];
            } else {
               this.fElemMapCounter[this.fElemMapSize] = -1;
               this.fElemMapCounterLowerBound[this.fElemMapSize] = -1;
               this.fElemMapCounterUpperBound[this.fElemMapSize] = -1;
            }

            ++this.fElemMapSize;
         }
      }

      --this.fElemMapSize;
      int[] fLeafSorter = new int[this.fLeafCount + this.fElemMapSize];
      inIndex = 0;

      for(curArraySize = 0; curArraySize < this.fElemMapSize; ++curArraySize) {
         int id = this.fElemMapId[curArraySize];

         for(int leafIndex = 0; leafIndex < this.fLeafCount; ++leafIndex) {
            if (id == this.fLeafList[leafIndex].getParticleId()) {
               fLeafSorter[inIndex++] = leafIndex;
            }
         }

         fLeafSorter[inIndex++] = -1;
      }

      curArraySize = this.fLeafCount * 4;
      CMStateSet[] statesToDo = new CMStateSet[curArraySize];
      this.fFinalStateFlags = new boolean[curArraySize];
      this.fTransTable = new int[curArraySize][];
      CMStateSet setT = this.fHeadNode.firstPos();
      int unmarkedState = 0;
      int curState = 0;
      this.fTransTable[curState] = this.makeDefStateList();
      statesToDo[curState] = setT;
      int curState = curState + 1;
      HashMap stateTable = new HashMap();

      int j;
      while(unmarkedState < curState) {
         setT = statesToDo[unmarkedState];
         int[] transEntry = this.fTransTable[unmarkedState];
         this.fFinalStateFlags[unmarkedState] = setT.getBit(EOCPos);
         ++unmarkedState;
         CMStateSet newSet = null;
         j = 0;

         for(int elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
            if (newSet == null) {
               newSet = new CMStateSet(this.fLeafCount);
            } else {
               newSet.zeroBits();
            }

            for(int leafIndex = fLeafSorter[j++]; leafIndex != -1; leafIndex = fLeafSorter[j++]) {
               if (setT.getBit(leafIndex)) {
                  newSet.union(this.fFollowList[leafIndex]);
               }
            }

            if (!newSet.isEmpty()) {
               Integer stateObj = (Integer)stateTable.get(newSet);
               int stateIndex = stateObj == null ? curState : stateObj;
               if (stateIndex == curState) {
                  statesToDo[curState] = newSet;
                  this.fTransTable[curState] = this.makeDefStateList();
                  stateTable.put(newSet, new Integer(curState));
                  ++curState;
                  newSet = null;
               }

               transEntry[elemIndex] = stateIndex;
               if (curState == curArraySize) {
                  int newSize = (int)((double)curArraySize * 1.5D);
                  CMStateSet[] newToDo = new CMStateSet[newSize];
                  boolean[] newFinalFlags = new boolean[newSize];
                  int[][] newTransTable = new int[newSize][];
                  System.arraycopy(statesToDo, 0, newToDo, 0, curArraySize);
                  System.arraycopy(this.fFinalStateFlags, 0, newFinalFlags, 0, curArraySize);
                  System.arraycopy(this.fTransTable, 0, newTransTable, 0, curArraySize);
                  curArraySize = newSize;
                  statesToDo = newToDo;
                  this.fFinalStateFlags = newFinalFlags;
                  this.fTransTable = newTransTable;
               }
            }
         }
      }

      if (elemOccurenceMap != null) {
         this.fCountingStates = new XSDFACM.Occurence[curState];

         for(int i = 0; i < curState; ++i) {
            int[] transitions = this.fTransTable[i];

            for(j = 0; j < transitions.length; ++j) {
               if (i == transitions[j]) {
                  this.fCountingStates[i] = elemOccurenceMap[j];
                  break;
               }
            }
         }
      }

      this.fHeadNode = null;
      this.fLeafList = null;
      this.fFollowList = null;
      this.fLeafListType = null;
      this.fElemMapId = null;
   }

   private void calcFollowList(CMNode nodeCur) {
      if (nodeCur.type() == 101) {
         this.calcFollowList(((XSCMBinOp)nodeCur).getLeft());
         this.calcFollowList(((XSCMBinOp)nodeCur).getRight());
      } else {
         CMStateSet first;
         CMStateSet last;
         int index;
         if (nodeCur.type() == 102) {
            this.calcFollowList(((XSCMBinOp)nodeCur).getLeft());
            this.calcFollowList(((XSCMBinOp)nodeCur).getRight());
            first = ((XSCMBinOp)nodeCur).getLeft().lastPos();
            last = ((XSCMBinOp)nodeCur).getRight().firstPos();

            for(index = 0; index < this.fLeafCount; ++index) {
               if (first.getBit(index)) {
                  this.fFollowList[index].union(last);
               }
            }
         } else if (nodeCur.type() != 4 && nodeCur.type() != 6) {
            if (nodeCur.type() == 5) {
               this.calcFollowList(((XSCMUniOp)nodeCur).getChild());
            }
         } else {
            this.calcFollowList(((XSCMUniOp)nodeCur).getChild());
            first = nodeCur.firstPos();
            last = nodeCur.lastPos();

            for(index = 0; index < this.fLeafCount; ++index) {
               if (last.getBit(index)) {
                  this.fFollowList[index].union(first);
               }
            }
         }
      }

   }

   private void dumpTree(CMNode nodeCur, int level) {
      int type;
      for(type = 0; type < level; ++type) {
         System.out.print("   ");
      }

      type = nodeCur.type();
      switch(type) {
      case 1:
         System.out.print("Leaf: (pos=" + ((XSCMLeaf)nodeCur).getPosition() + "), (elemIndex=" + ((XSCMLeaf)nodeCur).getLeaf() + ") ");
         if (nodeCur.isNullable()) {
            System.out.print(" Nullable ");
         }

         System.out.print("firstPos=");
         System.out.print(nodeCur.firstPos().toString());
         System.out.print(" lastPos=");
         System.out.println(nodeCur.lastPos().toString());
         break;
      case 2:
         System.out.print("Any Node: ");
         System.out.print("firstPos=");
         System.out.print(nodeCur.firstPos().toString());
         System.out.print(" lastPos=");
         System.out.println(nodeCur.lastPos().toString());
         break;
      case 4:
      case 5:
      case 6:
         System.out.print("Rep Node ");
         if (nodeCur.isNullable()) {
            System.out.print("Nullable ");
         }

         System.out.print("firstPos=");
         System.out.print(nodeCur.firstPos().toString());
         System.out.print(" lastPos=");
         System.out.println(nodeCur.lastPos().toString());
         this.dumpTree(((XSCMUniOp)nodeCur).getChild(), level + 1);
         break;
      case 101:
      case 102:
         if (type == 101) {
            System.out.print("Choice Node ");
         } else {
            System.out.print("Seq Node ");
         }

         if (nodeCur.isNullable()) {
            System.out.print("Nullable ");
         }

         System.out.print("firstPos=");
         System.out.print(nodeCur.firstPos().toString());
         System.out.print(" lastPos=");
         System.out.println(nodeCur.lastPos().toString());
         this.dumpTree(((XSCMBinOp)nodeCur).getLeft(), level + 1);
         this.dumpTree(((XSCMBinOp)nodeCur).getRight(), level + 1);
         break;
      default:
         throw new RuntimeException("ImplementationMessages.VAL_NIICM");
      }

   }

   private int[] makeDefStateList() {
      int[] retArray = new int[this.fElemMapSize];

      for(int index = 0; index < this.fElemMapSize; ++index) {
         retArray[index] = -1;
      }

      return retArray;
   }

   private void postTreeBuildInit(CMNode nodeCur) throws RuntimeException {
      nodeCur.setMaxStates(this.fLeafCount);
      XSCMLeaf leaf = null;
      int pos = false;
      int pos;
      if (nodeCur.type() == 2) {
         leaf = (XSCMLeaf)nodeCur;
         pos = leaf.getPosition();
         this.fLeafList[pos] = leaf;
         this.fLeafListType[pos] = 2;
      } else if (nodeCur.type() != 101 && nodeCur.type() != 102) {
         if (nodeCur.type() != 4 && nodeCur.type() != 6 && nodeCur.type() != 5) {
            if (nodeCur.type() != 1) {
               throw new RuntimeException("ImplementationMessages.VAL_NIICM");
            }

            leaf = (XSCMLeaf)nodeCur;
            pos = leaf.getPosition();
            this.fLeafList[pos] = leaf;
            this.fLeafListType[pos] = 1;
         } else {
            this.postTreeBuildInit(((XSCMUniOp)nodeCur).getChild());
         }
      } else {
         this.postTreeBuildInit(((XSCMBinOp)nodeCur).getLeft());
         this.postTreeBuildInit(((XSCMBinOp)nodeCur).getRight());
      }

   }

   public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler subGroupHandler) throws XMLSchemaException {
      byte[][] conflictTable = new byte[this.fElemMapSize][this.fElemMapSize];

      int i;
      int j;
      for(i = 0; i < this.fTransTable.length && this.fTransTable[i] != null; ++i) {
         for(j = 0; j < this.fElemMapSize; ++j) {
            for(int k = j + 1; k < this.fElemMapSize; ++k) {
               if (this.fTransTable[i][j] != -1 && this.fTransTable[i][k] != -1 && conflictTable[j][k] == 0) {
                  if (XSConstraints.overlapUPA(this.fElemMap[j], this.fElemMap[k], subGroupHandler)) {
                     if (this.fCountingStates != null) {
                        XSDFACM.Occurence o = this.fCountingStates[i];
                        if (o != null && this.fTransTable[i][j] == i ^ this.fTransTable[i][k] == i && o.minOccurs == o.maxOccurs) {
                           conflictTable[j][k] = -1;
                           continue;
                        }
                     }

                     conflictTable[j][k] = 1;
                  } else {
                     conflictTable[j][k] = -1;
                  }
               }
            }
         }
      }

      for(i = 0; i < this.fElemMapSize; ++i) {
         for(j = 0; j < this.fElemMapSize; ++j) {
            if (conflictTable[i][j] == 1) {
               throw new XMLSchemaException("cos-nonambig", new Object[]{this.fElemMap[i].toString(), this.fElemMap[j].toString()});
            }
         }
      }

      for(i = 0; i < this.fElemMapSize; ++i) {
         if (this.fElemMapType[i] == 2) {
            XSWildcardDecl wildcard = (XSWildcardDecl)this.fElemMap[i];
            if (wildcard.fType == 3 || wildcard.fType == 2) {
               return true;
            }
         }
      }

      return false;
   }

   public Vector whatCanGoHere(int[] state) {
      int curState = state[0];
      if (curState < 0) {
         curState = state[1];
      }

      XSDFACM.Occurence o = this.fCountingStates != null ? this.fCountingStates[curState] : null;
      int count = state[2];
      Vector ret = new Vector();

      for(int elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
         int nextState = this.fTransTable[curState][elemIndex];
         if (nextState != -1) {
            if (o != null) {
               if (curState == nextState) {
                  if (count >= o.maxOccurs && o.maxOccurs != -1) {
                     continue;
                  }
               } else if (count < o.minOccurs) {
                  continue;
               }
            }

            ret.addElement(this.fElemMap[elemIndex]);
         }
      }

      return ret;
   }

   public ArrayList checkMinMaxBounds() {
      ArrayList result = null;

      for(int elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
         int count = this.fElemMapCounter[elemIndex];
         if (count != -1) {
            int minOccurs = this.fElemMapCounterLowerBound[elemIndex];
            int maxOccurs = this.fElemMapCounterUpperBound[elemIndex];
            if (count < minOccurs) {
               if (result == null) {
                  result = new ArrayList();
               }

               result.add("cvc-complex-type.2.4.b");
               result.add("{" + this.fElemMap[elemIndex] + "}");
            }

            if (maxOccurs != -1 && count > maxOccurs) {
               if (result == null) {
                  result = new ArrayList();
               }

               result.add("cvc-complex-type.2.4.e");
               result.add("{" + this.fElemMap[elemIndex] + "}");
            }
         }
      }

      return result;
   }

   static final class Occurence {
      final int minOccurs;
      final int maxOccurs;
      final int elemIndex;

      public Occurence(XSCMRepeatingLeaf leaf, int elemIndex) {
         this.minOccurs = leaf.getMinOccurs();
         this.maxOccurs = leaf.getMaxOccurs();
         this.elemIndex = elemIndex;
      }

      public String toString() {
         return "minOccurs=" + this.minOccurs + ";maxOccurs=" + (this.maxOccurs != -1 ? Integer.toString(this.maxOccurs) : "unbounded");
      }
   }
}
