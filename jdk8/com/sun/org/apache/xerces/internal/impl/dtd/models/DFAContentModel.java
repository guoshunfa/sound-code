package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.HashMap;

public class DFAContentModel implements ContentModelValidator {
   private static String fEpsilonString = "<<CMNODE_EPSILON>>";
   private static String fEOCString = "<<CMNODE_EOC>>";
   private static final boolean DEBUG_VALIDATE_CONTENT = false;
   private QName[] fElemMap = null;
   private int[] fElemMapType = null;
   private int fElemMapSize = 0;
   private boolean fMixed;
   private int fEOCPos = 0;
   private boolean[] fFinalStateFlags = null;
   private CMStateSet[] fFollowList = null;
   private CMNode fHeadNode = null;
   private int fLeafCount = 0;
   private CMLeaf[] fLeafList = null;
   private int[] fLeafListType = null;
   private int[][] fTransTable = (int[][])null;
   private int fTransTableSize = 0;
   private boolean fEmptyContentIsValid = false;
   private final QName fQName = new QName();

   public DFAContentModel(CMNode syntaxTree, int leafCount, boolean mixed) {
      this.fLeafCount = leafCount;
      this.fMixed = mixed;
      this.buildDFA(syntaxTree);
   }

   public int validate(QName[] children, int offset, int length) {
      if (length == 0) {
         return this.fEmptyContentIsValid ? -1 : 0;
      } else {
         int curState = 0;

         for(int childIndex = 0; childIndex < length; ++childIndex) {
            QName curElem = children[offset + childIndex];
            if (!this.fMixed || curElem.localpart != null) {
               int elemIndex;
               for(elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
                  int type = this.fElemMapType[elemIndex] & 15;
                  if (type == 0) {
                     if (this.fElemMap[elemIndex].rawname == curElem.rawname) {
                        break;
                     }
                  } else if (type == 6) {
                     String uri = this.fElemMap[elemIndex].uri;
                     if (uri == null || uri == curElem.uri) {
                        break;
                     }
                  } else if (type == 8) {
                     if (curElem.uri == null) {
                        break;
                     }
                  } else if (type == 7 && this.fElemMap[elemIndex].uri != curElem.uri) {
                     break;
                  }
               }

               if (elemIndex == this.fElemMapSize) {
                  return childIndex;
               }

               curState = this.fTransTable[curState][elemIndex];
               if (curState == -1) {
                  return childIndex;
               }
            }
         }

         if (!this.fFinalStateFlags[curState]) {
            return length;
         } else {
            return -1;
         }
      }
   }

   private void buildDFA(CMNode syntaxTree) {
      this.fQName.setValues((String)null, fEOCString, fEOCString, (String)null);
      CMLeaf nodeEOC = new CMLeaf(this.fQName);
      this.fHeadNode = new CMBinOp(5, syntaxTree, nodeEOC);
      this.fEOCPos = this.fLeafCount;
      nodeEOC.setPosition(this.fLeafCount++);
      this.fLeafList = new CMLeaf[this.fLeafCount];
      this.fLeafListType = new int[this.fLeafCount];
      this.postTreeBuildInit(this.fHeadNode, 0);
      this.fFollowList = new CMStateSet[this.fLeafCount];

      int outIndex;
      for(outIndex = 0; outIndex < this.fLeafCount; ++outIndex) {
         this.fFollowList[outIndex] = new CMStateSet(this.fLeafCount);
      }

      this.calcFollowList(this.fHeadNode);
      this.fElemMap = new QName[this.fLeafCount];
      this.fElemMapType = new int[this.fLeafCount];
      this.fElemMapSize = 0;

      int curArraySize;
      for(outIndex = 0; outIndex < this.fLeafCount; ++outIndex) {
         this.fElemMap[outIndex] = new QName();
         QName element = this.fLeafList[outIndex].getElement();

         for(curArraySize = 0; curArraySize < this.fElemMapSize && this.fElemMap[curArraySize].rawname != element.rawname; ++curArraySize) {
         }

         if (curArraySize == this.fElemMapSize) {
            this.fElemMap[this.fElemMapSize].setValues(element);
            this.fElemMapType[this.fElemMapSize] = this.fLeafListType[outIndex];
            ++this.fElemMapSize;
         }
      }

      int[] fLeafSorter = new int[this.fLeafCount + this.fElemMapSize];
      int fSortCount = 0;

      for(curArraySize = 0; curArraySize < this.fElemMapSize; ++curArraySize) {
         for(int leafIndex = 0; leafIndex < this.fLeafCount; ++leafIndex) {
            QName leaf = this.fLeafList[leafIndex].getElement();
            QName element = this.fElemMap[curArraySize];
            if (leaf.rawname == element.rawname) {
               fLeafSorter[fSortCount++] = leafIndex;
            }
         }

         fLeafSorter[fSortCount++] = -1;
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

      while(unmarkedState < curState) {
         setT = statesToDo[unmarkedState];
         int[] transEntry = this.fTransTable[unmarkedState];
         this.fFinalStateFlags[unmarkedState] = setT.getBit(this.fEOCPos);
         ++unmarkedState;
         CMStateSet newSet = null;
         int sorterIndex = 0;

         for(int elemIndex = 0; elemIndex < this.fElemMapSize; ++elemIndex) {
            if (newSet == null) {
               newSet = new CMStateSet(this.fLeafCount);
            } else {
               newSet.zeroBits();
            }

            for(int leafIndex = fLeafSorter[sorterIndex++]; leafIndex != -1; leafIndex = fLeafSorter[sorterIndex++]) {
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

      this.fEmptyContentIsValid = ((CMBinOp)this.fHeadNode).getLeft().isNullable();
      this.fHeadNode = null;
      this.fLeafList = null;
      this.fFollowList = null;
   }

   private void calcFollowList(CMNode nodeCur) {
      if (nodeCur.type() == 4) {
         this.calcFollowList(((CMBinOp)nodeCur).getLeft());
         this.calcFollowList(((CMBinOp)nodeCur).getRight());
      } else {
         CMStateSet first;
         CMStateSet last;
         int index;
         if (nodeCur.type() == 5) {
            this.calcFollowList(((CMBinOp)nodeCur).getLeft());
            this.calcFollowList(((CMBinOp)nodeCur).getRight());
            first = ((CMBinOp)nodeCur).getLeft().lastPos();
            last = ((CMBinOp)nodeCur).getRight().firstPos();

            for(index = 0; index < this.fLeafCount; ++index) {
               if (first.getBit(index)) {
                  this.fFollowList[index].union(last);
               }
            }
         } else if (nodeCur.type() != 2 && nodeCur.type() != 3) {
            if (nodeCur.type() == 1) {
               this.calcFollowList(((CMUniOp)nodeCur).getChild());
            }
         } else {
            this.calcFollowList(((CMUniOp)nodeCur).getChild());
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
      if (type != 4 && type != 5) {
         if (nodeCur.type() == 2) {
            System.out.print("Rep Node ");
            if (nodeCur.isNullable()) {
               System.out.print("Nullable ");
            }

            System.out.print("firstPos=");
            System.out.print(nodeCur.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(nodeCur.lastPos().toString());
            this.dumpTree(((CMUniOp)nodeCur).getChild(), level + 1);
         } else {
            if (nodeCur.type() != 0) {
               throw new RuntimeException("ImplementationMessages.VAL_NIICM");
            }

            System.out.print("Leaf: (pos=" + ((CMLeaf)nodeCur).getPosition() + "), " + ((CMLeaf)nodeCur).getElement() + "(elemIndex=" + ((CMLeaf)nodeCur).getElement() + ") ");
            if (nodeCur.isNullable()) {
               System.out.print(" Nullable ");
            }

            System.out.print("firstPos=");
            System.out.print(nodeCur.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(nodeCur.lastPos().toString());
         }
      } else {
         if (type == 4) {
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
         this.dumpTree(((CMBinOp)nodeCur).getLeft(), level + 1);
         this.dumpTree(((CMBinOp)nodeCur).getRight(), level + 1);
      }

   }

   private int[] makeDefStateList() {
      int[] retArray = new int[this.fElemMapSize];

      for(int index = 0; index < this.fElemMapSize; ++index) {
         retArray[index] = -1;
      }

      return retArray;
   }

   private int postTreeBuildInit(CMNode nodeCur, int curIndex) {
      nodeCur.setMaxStates(this.fLeafCount);
      QName node;
      if ((nodeCur.type() & 15) != 6 && (nodeCur.type() & 15) != 8 && (nodeCur.type() & 15) != 7) {
         if (nodeCur.type() != 4 && nodeCur.type() != 5) {
            if (nodeCur.type() != 2 && nodeCur.type() != 3 && nodeCur.type() != 1) {
               if (nodeCur.type() != 0) {
                  throw new RuntimeException("ImplementationMessages.VAL_NIICM: type=" + nodeCur.type());
               }

               node = ((CMLeaf)nodeCur).getElement();
               if (node.localpart != fEpsilonString) {
                  this.fLeafList[curIndex] = (CMLeaf)nodeCur;
                  this.fLeafListType[curIndex] = 0;
                  ++curIndex;
               }
            } else {
               curIndex = this.postTreeBuildInit(((CMUniOp)nodeCur).getChild(), curIndex);
            }
         } else {
            curIndex = this.postTreeBuildInit(((CMBinOp)nodeCur).getLeft(), curIndex);
            curIndex = this.postTreeBuildInit(((CMBinOp)nodeCur).getRight(), curIndex);
         }
      } else {
         node = new QName((String)null, (String)null, (String)null, ((CMAny)nodeCur).getURI());
         this.fLeafList[curIndex] = new CMLeaf(node, ((CMAny)nodeCur).getPosition());
         this.fLeafListType[curIndex] = nodeCur.type();
         ++curIndex;
      }

      return curIndex;
   }

   static {
      fEpsilonString = fEpsilonString.intern();
      fEOCString = fEOCString.intern();
   }
}
