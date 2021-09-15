package com.sun.org.apache.xerces.internal.dom;

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DeferredDocumentImpl extends DocumentImpl implements DeferredNode {
   static final long serialVersionUID = 5186323580749626857L;
   private static final boolean DEBUG_PRINT_REF_COUNTS = false;
   private static final boolean DEBUG_PRINT_TABLES = false;
   private static final boolean DEBUG_IDS = false;
   protected static final int CHUNK_SHIFT = 8;
   protected static final int CHUNK_SIZE = 256;
   protected static final int CHUNK_MASK = 255;
   protected static final int INITIAL_CHUNK_COUNT = 32;
   protected transient int fNodeCount;
   protected transient int[][] fNodeType;
   protected transient Object[][] fNodeName;
   protected transient Object[][] fNodeValue;
   protected transient int[][] fNodeParent;
   protected transient int[][] fNodeLastChild;
   protected transient int[][] fNodePrevSib;
   protected transient Object[][] fNodeURI;
   protected transient int[][] fNodeExtra;
   protected transient int fIdCount;
   protected transient String[] fIdName;
   protected transient int[] fIdElement;
   protected boolean fNamespacesEnabled;
   private final transient StringBuilder fBufferStr;
   private final transient ArrayList fStrChunks;
   private static final int[] INIT_ARRAY = new int[257];

   public DeferredDocumentImpl() {
      this(false);
   }

   public DeferredDocumentImpl(boolean namespacesEnabled) {
      this(namespacesEnabled, false);
   }

   public DeferredDocumentImpl(boolean namespaces, boolean grammarAccess) {
      super(grammarAccess);
      this.fNodeCount = 0;
      this.fNamespacesEnabled = false;
      this.fBufferStr = new StringBuilder();
      this.fStrChunks = new ArrayList();
      this.needsSyncData(true);
      this.needsSyncChildren(true);
      this.fNamespacesEnabled = namespaces;
   }

   public DOMImplementation getImplementation() {
      return DeferredDOMImplementationImpl.getDOMImplementation();
   }

   boolean getNamespacesEnabled() {
      return this.fNamespacesEnabled;
   }

   void setNamespacesEnabled(boolean enable) {
      this.fNamespacesEnabled = enable;
   }

   public int createDeferredDocument() {
      int nodeIndex = this.createNode((short)9);
      return nodeIndex;
   }

   public int createDeferredDocumentType(String rootElementName, String publicId, String systemId) {
      int nodeIndex = this.createNode((short)10);
      int chunk = nodeIndex >> 8;
      int index = nodeIndex & 255;
      this.setChunkValue(this.fNodeName, rootElementName, chunk, index);
      this.setChunkValue(this.fNodeValue, publicId, chunk, index);
      this.setChunkValue(this.fNodeURI, systemId, chunk, index);
      return nodeIndex;
   }

   public void setInternalSubset(int doctypeIndex, String subset) {
      int chunk = doctypeIndex >> 8;
      int index = doctypeIndex & 255;
      int extraDataIndex = this.createNode((short)10);
      int echunk = extraDataIndex >> 8;
      int eindex = extraDataIndex & 255;
      this.setChunkIndex(this.fNodeExtra, extraDataIndex, chunk, index);
      this.setChunkValue(this.fNodeValue, subset, echunk, eindex);
   }

   public int createDeferredNotation(String notationName, String publicId, String systemId, String baseURI) {
      int nodeIndex = this.createNode((short)12);
      int chunk = nodeIndex >> 8;
      int index = nodeIndex & 255;
      int extraDataIndex = this.createNode((short)12);
      int echunk = extraDataIndex >> 8;
      int eindex = extraDataIndex & 255;
      this.setChunkValue(this.fNodeName, notationName, chunk, index);
      this.setChunkValue(this.fNodeValue, publicId, chunk, index);
      this.setChunkValue(this.fNodeURI, systemId, chunk, index);
      this.setChunkIndex(this.fNodeExtra, extraDataIndex, chunk, index);
      this.setChunkValue(this.fNodeName, baseURI, echunk, eindex);
      return nodeIndex;
   }

   public int createDeferredEntity(String entityName, String publicId, String systemId, String notationName, String baseURI) {
      int nodeIndex = this.createNode((short)6);
      int chunk = nodeIndex >> 8;
      int index = nodeIndex & 255;
      int extraDataIndex = this.createNode((short)6);
      int echunk = extraDataIndex >> 8;
      int eindex = extraDataIndex & 255;
      this.setChunkValue(this.fNodeName, entityName, chunk, index);
      this.setChunkValue(this.fNodeValue, publicId, chunk, index);
      this.setChunkValue(this.fNodeURI, systemId, chunk, index);
      this.setChunkIndex(this.fNodeExtra, extraDataIndex, chunk, index);
      this.setChunkValue(this.fNodeName, notationName, echunk, eindex);
      this.setChunkValue(this.fNodeValue, (Object)null, echunk, eindex);
      this.setChunkValue(this.fNodeURI, (Object)null, echunk, eindex);
      int extraDataIndex2 = this.createNode((short)6);
      int echunk2 = extraDataIndex2 >> 8;
      int eindex2 = extraDataIndex2 & 255;
      this.setChunkIndex(this.fNodeExtra, extraDataIndex2, echunk, eindex);
      this.setChunkValue(this.fNodeName, baseURI, echunk2, eindex2);
      return nodeIndex;
   }

   public String getDeferredEntityBaseURI(int entityIndex) {
      if (entityIndex != -1) {
         int extraDataIndex = this.getNodeExtra(entityIndex, false);
         extraDataIndex = this.getNodeExtra(extraDataIndex, false);
         return this.getNodeName(extraDataIndex, false);
      } else {
         return null;
      }
   }

   public void setEntityInfo(int currentEntityDecl, String version, String encoding) {
      int eNodeIndex = this.getNodeExtra(currentEntityDecl, false);
      if (eNodeIndex != -1) {
         int echunk = eNodeIndex >> 8;
         int eindex = eNodeIndex & 255;
         this.setChunkValue(this.fNodeValue, version, echunk, eindex);
         this.setChunkValue(this.fNodeURI, encoding, echunk, eindex);
      }

   }

   public void setTypeInfo(int elementNodeIndex, Object type) {
      int elementChunk = elementNodeIndex >> 8;
      int elementIndex = elementNodeIndex & 255;
      this.setChunkValue(this.fNodeValue, type, elementChunk, elementIndex);
   }

   public void setInputEncoding(int currentEntityDecl, String value) {
      int nodeIndex = this.getNodeExtra(currentEntityDecl, false);
      int extraDataIndex = this.getNodeExtra(nodeIndex, false);
      int echunk = extraDataIndex >> 8;
      int eindex = extraDataIndex & 255;
      this.setChunkValue(this.fNodeValue, value, echunk, eindex);
   }

   public int createDeferredEntityReference(String name, String baseURI) {
      int nodeIndex = this.createNode((short)5);
      int chunk = nodeIndex >> 8;
      int index = nodeIndex & 255;
      this.setChunkValue(this.fNodeName, name, chunk, index);
      this.setChunkValue(this.fNodeValue, baseURI, chunk, index);
      return nodeIndex;
   }

   /** @deprecated */
   public int createDeferredElement(String elementURI, String elementName, Object type) {
      int elementNodeIndex = this.createNode((short)1);
      int elementChunk = elementNodeIndex >> 8;
      int elementIndex = elementNodeIndex & 255;
      this.setChunkValue(this.fNodeName, elementName, elementChunk, elementIndex);
      this.setChunkValue(this.fNodeURI, elementURI, elementChunk, elementIndex);
      this.setChunkValue(this.fNodeValue, type, elementChunk, elementIndex);
      return elementNodeIndex;
   }

   /** @deprecated */
   public int createDeferredElement(String elementName) {
      return this.createDeferredElement((String)null, elementName);
   }

   public int createDeferredElement(String elementURI, String elementName) {
      int elementNodeIndex = this.createNode((short)1);
      int elementChunk = elementNodeIndex >> 8;
      int elementIndex = elementNodeIndex & 255;
      this.setChunkValue(this.fNodeName, elementName, elementChunk, elementIndex);
      this.setChunkValue(this.fNodeURI, elementURI, elementChunk, elementIndex);
      return elementNodeIndex;
   }

   public int setDeferredAttribute(int elementNodeIndex, String attrName, String attrURI, String attrValue, boolean specified, boolean id, Object type) {
      int attrNodeIndex = this.createDeferredAttribute(attrName, attrURI, attrValue, specified);
      int attrChunk = attrNodeIndex >> 8;
      int attrIndex = attrNodeIndex & 255;
      this.setChunkIndex(this.fNodeParent, elementNodeIndex, attrChunk, attrIndex);
      int elementChunk = elementNodeIndex >> 8;
      int elementIndex = elementNodeIndex & 255;
      int lastAttrNodeIndex = this.getChunkIndex(this.fNodeExtra, elementChunk, elementIndex);
      if (lastAttrNodeIndex != 0) {
         this.setChunkIndex(this.fNodePrevSib, lastAttrNodeIndex, attrChunk, attrIndex);
      }

      this.setChunkIndex(this.fNodeExtra, attrNodeIndex, elementChunk, elementIndex);
      int extra = this.getChunkIndex(this.fNodeExtra, attrChunk, attrIndex);
      if (id) {
         extra |= 512;
         this.setChunkIndex(this.fNodeExtra, extra, attrChunk, attrIndex);
         String value = this.getChunkValue(this.fNodeValue, attrChunk, attrIndex);
         this.putIdentifier(value, elementNodeIndex);
      }

      if (type != null) {
         int extraDataIndex = this.createNode((short)20);
         int echunk = extraDataIndex >> 8;
         int eindex = extraDataIndex & 255;
         this.setChunkIndex(this.fNodeLastChild, extraDataIndex, attrChunk, attrIndex);
         this.setChunkValue(this.fNodeValue, type, echunk, eindex);
      }

      return attrNodeIndex;
   }

   /** @deprecated */
   public int setDeferredAttribute(int elementNodeIndex, String attrName, String attrURI, String attrValue, boolean specified) {
      int attrNodeIndex = this.createDeferredAttribute(attrName, attrURI, attrValue, specified);
      int attrChunk = attrNodeIndex >> 8;
      int attrIndex = attrNodeIndex & 255;
      this.setChunkIndex(this.fNodeParent, elementNodeIndex, attrChunk, attrIndex);
      int elementChunk = elementNodeIndex >> 8;
      int elementIndex = elementNodeIndex & 255;
      int lastAttrNodeIndex = this.getChunkIndex(this.fNodeExtra, elementChunk, elementIndex);
      if (lastAttrNodeIndex != 0) {
         this.setChunkIndex(this.fNodePrevSib, lastAttrNodeIndex, attrChunk, attrIndex);
      }

      this.setChunkIndex(this.fNodeExtra, attrNodeIndex, elementChunk, elementIndex);
      return attrNodeIndex;
   }

   public int createDeferredAttribute(String attrName, String attrValue, boolean specified) {
      return this.createDeferredAttribute(attrName, (String)null, attrValue, specified);
   }

   public int createDeferredAttribute(String attrName, String attrURI, String attrValue, boolean specified) {
      int nodeIndex = this.createNode((short)2);
      int chunk = nodeIndex >> 8;
      int index = nodeIndex & 255;
      this.setChunkValue(this.fNodeName, attrName, chunk, index);
      this.setChunkValue(this.fNodeURI, attrURI, chunk, index);
      this.setChunkValue(this.fNodeValue, attrValue, chunk, index);
      int extra = specified ? 32 : 0;
      this.setChunkIndex(this.fNodeExtra, extra, chunk, index);
      return nodeIndex;
   }

   public int createDeferredElementDefinition(String elementName) {
      int nodeIndex = this.createNode((short)21);
      int chunk = nodeIndex >> 8;
      int index = nodeIndex & 255;
      this.setChunkValue(this.fNodeName, elementName, chunk, index);
      return nodeIndex;
   }

   public int createDeferredTextNode(String data, boolean ignorableWhitespace) {
      int nodeIndex = this.createNode((short)3);
      int chunk = nodeIndex >> 8;
      int index = nodeIndex & 255;
      this.setChunkValue(this.fNodeValue, data, chunk, index);
      this.setChunkIndex(this.fNodeExtra, ignorableWhitespace ? 1 : 0, chunk, index);
      return nodeIndex;
   }

   public int createDeferredCDATASection(String data) {
      int nodeIndex = this.createNode((short)4);
      int chunk = nodeIndex >> 8;
      int index = nodeIndex & 255;
      this.setChunkValue(this.fNodeValue, data, chunk, index);
      return nodeIndex;
   }

   public int createDeferredProcessingInstruction(String target, String data) {
      int nodeIndex = this.createNode((short)7);
      int chunk = nodeIndex >> 8;
      int index = nodeIndex & 255;
      this.setChunkValue(this.fNodeName, target, chunk, index);
      this.setChunkValue(this.fNodeValue, data, chunk, index);
      return nodeIndex;
   }

   public int createDeferredComment(String data) {
      int nodeIndex = this.createNode((short)8);
      int chunk = nodeIndex >> 8;
      int index = nodeIndex & 255;
      this.setChunkValue(this.fNodeValue, data, chunk, index);
      return nodeIndex;
   }

   public int cloneNode(int nodeIndex, boolean deep) {
      int nchunk = nodeIndex >> 8;
      int nindex = nodeIndex & 255;
      int nodeType = this.fNodeType[nchunk][nindex];
      int cloneIndex = this.createNode((short)nodeType);
      int cchunk = cloneIndex >> 8;
      int cindex = cloneIndex & 255;
      this.setChunkValue(this.fNodeName, this.fNodeName[nchunk][nindex], cchunk, cindex);
      this.setChunkValue(this.fNodeValue, this.fNodeValue[nchunk][nindex], cchunk, cindex);
      this.setChunkValue(this.fNodeURI, this.fNodeURI[nchunk][nindex], cchunk, cindex);
      int extraIndex = this.fNodeExtra[nchunk][nindex];
      if (extraIndex != -1) {
         if (nodeType != 2 && nodeType != 3) {
            extraIndex = this.cloneNode(extraIndex, false);
         }

         this.setChunkIndex(this.fNodeExtra, extraIndex, cchunk, cindex);
      }

      if (deep) {
         int prevIndex = -1;

         for(int childIndex = this.getLastChild(nodeIndex, false); childIndex != -1; childIndex = this.getRealPrevSibling(childIndex, false)) {
            int clonedChildIndex = this.cloneNode(childIndex, deep);
            this.insertBefore(cloneIndex, clonedChildIndex, prevIndex);
            prevIndex = clonedChildIndex;
         }
      }

      return cloneIndex;
   }

   public void appendChild(int parentIndex, int childIndex) {
      int pchunk = parentIndex >> 8;
      int pindex = parentIndex & 255;
      int cchunk = childIndex >> 8;
      int cindex = childIndex & 255;
      this.setChunkIndex(this.fNodeParent, parentIndex, cchunk, cindex);
      int olast = this.getChunkIndex(this.fNodeLastChild, pchunk, pindex);
      this.setChunkIndex(this.fNodePrevSib, olast, cchunk, cindex);
      this.setChunkIndex(this.fNodeLastChild, childIndex, pchunk, pindex);
   }

   public int setAttributeNode(int elemIndex, int attrIndex) {
      int echunk = elemIndex >> 8;
      int eindex = elemIndex & 255;
      int achunk = attrIndex >> 8;
      int aindex = attrIndex & 255;
      String attrName = this.getChunkValue(this.fNodeName, achunk, aindex);
      int oldAttrIndex = this.getChunkIndex(this.fNodeExtra, echunk, eindex);
      int nextIndex = -1;
      int oachunk = -1;

      int oaindex;
      for(oaindex = -1; oldAttrIndex != -1; oldAttrIndex = this.getChunkIndex(this.fNodePrevSib, oachunk, oaindex)) {
         oachunk = oldAttrIndex >> 8;
         oaindex = oldAttrIndex & 255;
         String oldAttrName = this.getChunkValue(this.fNodeName, oachunk, oaindex);
         if (oldAttrName.equals(attrName)) {
            break;
         }

         nextIndex = oldAttrIndex;
      }

      int prevIndex;
      if (oldAttrIndex != -1) {
         prevIndex = this.getChunkIndex(this.fNodePrevSib, oachunk, oaindex);
         int attrTextIndex;
         int atchunk;
         if (nextIndex == -1) {
            this.setChunkIndex(this.fNodeExtra, prevIndex, echunk, eindex);
         } else {
            attrTextIndex = nextIndex >> 8;
            atchunk = nextIndex & 255;
            this.setChunkIndex(this.fNodePrevSib, prevIndex, attrTextIndex, atchunk);
         }

         this.clearChunkIndex(this.fNodeType, oachunk, oaindex);
         this.clearChunkValue(this.fNodeName, oachunk, oaindex);
         this.clearChunkValue(this.fNodeValue, oachunk, oaindex);
         this.clearChunkIndex(this.fNodeParent, oachunk, oaindex);
         this.clearChunkIndex(this.fNodePrevSib, oachunk, oaindex);
         attrTextIndex = this.clearChunkIndex(this.fNodeLastChild, oachunk, oaindex);
         atchunk = attrTextIndex >> 8;
         int atindex = attrTextIndex & 255;
         this.clearChunkIndex(this.fNodeType, atchunk, atindex);
         this.clearChunkValue(this.fNodeValue, atchunk, atindex);
         this.clearChunkIndex(this.fNodeParent, atchunk, atindex);
         this.clearChunkIndex(this.fNodeLastChild, atchunk, atindex);
      }

      prevIndex = this.getChunkIndex(this.fNodeExtra, echunk, eindex);
      this.setChunkIndex(this.fNodeExtra, attrIndex, echunk, eindex);
      this.setChunkIndex(this.fNodePrevSib, prevIndex, achunk, aindex);
      return oldAttrIndex;
   }

   public void setIdAttributeNode(int elemIndex, int attrIndex) {
      int chunk = attrIndex >> 8;
      int index = attrIndex & 255;
      int extra = this.getChunkIndex(this.fNodeExtra, chunk, index);
      extra |= 512;
      this.setChunkIndex(this.fNodeExtra, extra, chunk, index);
      String value = this.getChunkValue(this.fNodeValue, chunk, index);
      this.putIdentifier(value, elemIndex);
   }

   public void setIdAttribute(int attrIndex) {
      int chunk = attrIndex >> 8;
      int index = attrIndex & 255;
      int extra = this.getChunkIndex(this.fNodeExtra, chunk, index);
      extra |= 512;
      this.setChunkIndex(this.fNodeExtra, extra, chunk, index);
   }

   public int insertBefore(int parentIndex, int newChildIndex, int refChildIndex) {
      if (refChildIndex == -1) {
         this.appendChild(parentIndex, newChildIndex);
         return newChildIndex;
      } else {
         int nchunk = newChildIndex >> 8;
         int nindex = newChildIndex & 255;
         int rchunk = refChildIndex >> 8;
         int rindex = refChildIndex & 255;
         int previousIndex = this.getChunkIndex(this.fNodePrevSib, rchunk, rindex);
         this.setChunkIndex(this.fNodePrevSib, newChildIndex, rchunk, rindex);
         this.setChunkIndex(this.fNodePrevSib, previousIndex, nchunk, nindex);
         return newChildIndex;
      }
   }

   public void setAsLastChild(int parentIndex, int childIndex) {
      int pchunk = parentIndex >> 8;
      int pindex = parentIndex & 255;
      this.setChunkIndex(this.fNodeLastChild, childIndex, pchunk, pindex);
   }

   public int getParentNode(int nodeIndex) {
      return this.getParentNode(nodeIndex, false);
   }

   public int getParentNode(int nodeIndex, boolean free) {
      if (nodeIndex == -1) {
         return -1;
      } else {
         int chunk = nodeIndex >> 8;
         int index = nodeIndex & 255;
         return free ? this.clearChunkIndex(this.fNodeParent, chunk, index) : this.getChunkIndex(this.fNodeParent, chunk, index);
      }
   }

   public int getLastChild(int nodeIndex) {
      return this.getLastChild(nodeIndex, true);
   }

   public int getLastChild(int nodeIndex, boolean free) {
      if (nodeIndex == -1) {
         return -1;
      } else {
         int chunk = nodeIndex >> 8;
         int index = nodeIndex & 255;
         return free ? this.clearChunkIndex(this.fNodeLastChild, chunk, index) : this.getChunkIndex(this.fNodeLastChild, chunk, index);
      }
   }

   public int getPrevSibling(int nodeIndex) {
      return this.getPrevSibling(nodeIndex, true);
   }

   public int getPrevSibling(int nodeIndex, boolean free) {
      if (nodeIndex == -1) {
         return -1;
      } else {
         int chunk = nodeIndex >> 8;
         int index = nodeIndex & 255;
         int type = this.getChunkIndex(this.fNodeType, chunk, index);
         if (type == 3) {
            do {
               nodeIndex = this.getChunkIndex(this.fNodePrevSib, chunk, index);
               if (nodeIndex == -1) {
                  break;
               }

               chunk = nodeIndex >> 8;
               index = nodeIndex & 255;
               type = this.getChunkIndex(this.fNodeType, chunk, index);
            } while(type == 3);
         } else {
            nodeIndex = this.getChunkIndex(this.fNodePrevSib, chunk, index);
         }

         return nodeIndex;
      }
   }

   public int getRealPrevSibling(int nodeIndex) {
      return this.getRealPrevSibling(nodeIndex, true);
   }

   public int getRealPrevSibling(int nodeIndex, boolean free) {
      if (nodeIndex == -1) {
         return -1;
      } else {
         int chunk = nodeIndex >> 8;
         int index = nodeIndex & 255;
         return free ? this.clearChunkIndex(this.fNodePrevSib, chunk, index) : this.getChunkIndex(this.fNodePrevSib, chunk, index);
      }
   }

   public int lookupElementDefinition(String elementName) {
      if (this.fNodeCount > 1) {
         int docTypeIndex = -1;
         int nchunk = 0;
         int nindex = 0;

         int index;
         int nchunk;
         int nindex;
         for(index = this.getChunkIndex(this.fNodeLastChild, nchunk, nindex); index != -1; index = this.getChunkIndex(this.fNodePrevSib, nchunk, nindex)) {
            nchunk = index >> 8;
            nindex = index & 255;
            if (this.getChunkIndex(this.fNodeType, nchunk, nindex) == 10) {
               docTypeIndex = index;
               break;
            }
         }

         if (docTypeIndex == -1) {
            return -1;
         }

         nchunk = docTypeIndex >> 8;
         nindex = docTypeIndex & 255;

         for(index = this.getChunkIndex(this.fNodeLastChild, nchunk, nindex); index != -1; index = this.getChunkIndex(this.fNodePrevSib, nchunk, nindex)) {
            nchunk = index >> 8;
            nindex = index & 255;
            if (this.getChunkIndex(this.fNodeType, nchunk, nindex) == 21 && this.getChunkValue(this.fNodeName, nchunk, nindex) == elementName) {
               return index;
            }
         }
      }

      return -1;
   }

   public DeferredNode getNodeObject(int nodeIndex) {
      if (nodeIndex == -1) {
         return null;
      } else {
         int chunk = nodeIndex >> 8;
         int index = nodeIndex & 255;
         int type = this.getChunkIndex(this.fNodeType, chunk, index);
         if (type != 3 && type != 4) {
            this.clearChunkIndex(this.fNodeType, chunk, index);
         }

         Object node;
         node = null;
         label62:
         switch(type) {
         case 1:
            if (this.fNamespacesEnabled) {
               node = new DeferredElementNSImpl(this, nodeIndex);
            } else {
               node = new DeferredElementImpl(this, nodeIndex);
            }

            if (this.fIdElement != null) {
               int idIndex = binarySearch(this.fIdElement, 0, this.fIdCount - 1, nodeIndex);

               while(true) {
                  while(true) {
                     if (idIndex == -1) {
                        break label62;
                     }

                     String name = this.fIdName[idIndex];
                     if (name != null) {
                        this.putIdentifier0(name, (Element)node);
                        this.fIdName[idIndex] = null;
                     }

                     if (idIndex + 1 < this.fIdCount && this.fIdElement[idIndex + 1] == nodeIndex) {
                        ++idIndex;
                     } else {
                        idIndex = -1;
                     }
                  }
               }
            }
            break;
         case 2:
            if (this.fNamespacesEnabled) {
               node = new DeferredAttrNSImpl(this, nodeIndex);
            } else {
               node = new DeferredAttrImpl(this, nodeIndex);
            }
            break;
         case 3:
            node = new DeferredTextImpl(this, nodeIndex);
            break;
         case 4:
            node = new DeferredCDATASectionImpl(this, nodeIndex);
            break;
         case 5:
            node = new DeferredEntityReferenceImpl(this, nodeIndex);
            break;
         case 6:
            node = new DeferredEntityImpl(this, nodeIndex);
            break;
         case 7:
            node = new DeferredProcessingInstructionImpl(this, nodeIndex);
            break;
         case 8:
            node = new DeferredCommentImpl(this, nodeIndex);
            break;
         case 9:
            node = this;
            break;
         case 10:
            node = new DeferredDocumentTypeImpl(this, nodeIndex);
            this.docType = (DocumentTypeImpl)node;
            break;
         case 11:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         default:
            throw new IllegalArgumentException("type: " + type);
         case 12:
            node = new DeferredNotationImpl(this, nodeIndex);
            break;
         case 21:
            node = new DeferredElementDefinitionImpl(this, nodeIndex);
         }

         if (node != null) {
            return (DeferredNode)node;
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public String getNodeName(int nodeIndex) {
      return this.getNodeName(nodeIndex, true);
   }

   public String getNodeName(int nodeIndex, boolean free) {
      if (nodeIndex == -1) {
         return null;
      } else {
         int chunk = nodeIndex >> 8;
         int index = nodeIndex & 255;
         return free ? this.clearChunkValue(this.fNodeName, chunk, index) : this.getChunkValue(this.fNodeName, chunk, index);
      }
   }

   public String getNodeValueString(int nodeIndex) {
      return this.getNodeValueString(nodeIndex, true);
   }

   public String getNodeValueString(int nodeIndex, boolean free) {
      if (nodeIndex == -1) {
         return null;
      } else {
         int chunk = nodeIndex >> 8;
         int index = nodeIndex & 255;
         String value = free ? this.clearChunkValue(this.fNodeValue, chunk, index) : this.getChunkValue(this.fNodeValue, chunk, index);
         if (value == null) {
            return null;
         } else {
            int type = this.getChunkIndex(this.fNodeType, chunk, index);
            int child;
            int i;
            if (type == 3) {
               child = this.getRealPrevSibling(nodeIndex);
               if (child != -1 && this.getNodeType(child, false) == 3) {
                  this.fStrChunks.add(value);

                  do {
                     chunk = child >> 8;
                     index = child & 255;
                     value = this.getChunkValue(this.fNodeValue, chunk, index);
                     this.fStrChunks.add(value);
                     child = this.getChunkIndex(this.fNodePrevSib, chunk, index);
                  } while(child != -1 && this.getNodeType(child, false) == 3);

                  i = this.fStrChunks.size();

                  for(int i = i - 1; i >= 0; --i) {
                     this.fBufferStr.append((String)this.fStrChunks.get(i));
                  }

                  value = this.fBufferStr.toString();
                  this.fStrChunks.clear();
                  this.fBufferStr.setLength(0);
                  return value;
               }
            } else if (type == 4) {
               child = this.getLastChild(nodeIndex, false);
               if (child != -1) {
                  this.fBufferStr.append(value);

                  while(child != -1) {
                     chunk = child >> 8;
                     index = child & 255;
                     value = this.getChunkValue(this.fNodeValue, chunk, index);
                     this.fStrChunks.add(value);
                     child = this.getChunkIndex(this.fNodePrevSib, chunk, index);
                  }

                  for(i = this.fStrChunks.size() - 1; i >= 0; --i) {
                     this.fBufferStr.append((String)this.fStrChunks.get(i));
                  }

                  value = this.fBufferStr.toString();
                  this.fStrChunks.clear();
                  this.fBufferStr.setLength(0);
                  return value;
               }
            }

            return value;
         }
      }
   }

   public String getNodeValue(int nodeIndex) {
      return this.getNodeValue(nodeIndex, true);
   }

   public Object getTypeInfo(int nodeIndex) {
      if (nodeIndex == -1) {
         return null;
      } else {
         int chunk = nodeIndex >> 8;
         int index = nodeIndex & 255;
         Object value = this.fNodeValue[chunk] != null ? this.fNodeValue[chunk][index] : null;
         if (value != null) {
            this.fNodeValue[chunk][index] = null;
            DeferredDocumentImpl.RefCount c = (DeferredDocumentImpl.RefCount)this.fNodeValue[chunk][256];
            --c.fCount;
            if (c.fCount == 0) {
               this.fNodeValue[chunk] = null;
            }
         }

         return value;
      }
   }

   public String getNodeValue(int nodeIndex, boolean free) {
      if (nodeIndex == -1) {
         return null;
      } else {
         int chunk = nodeIndex >> 8;
         int index = nodeIndex & 255;
         return free ? this.clearChunkValue(this.fNodeValue, chunk, index) : this.getChunkValue(this.fNodeValue, chunk, index);
      }
   }

   public int getNodeExtra(int nodeIndex) {
      return this.getNodeExtra(nodeIndex, true);
   }

   public int getNodeExtra(int nodeIndex, boolean free) {
      if (nodeIndex == -1) {
         return -1;
      } else {
         int chunk = nodeIndex >> 8;
         int index = nodeIndex & 255;
         return free ? this.clearChunkIndex(this.fNodeExtra, chunk, index) : this.getChunkIndex(this.fNodeExtra, chunk, index);
      }
   }

   public short getNodeType(int nodeIndex) {
      return this.getNodeType(nodeIndex, true);
   }

   public short getNodeType(int nodeIndex, boolean free) {
      if (nodeIndex == -1) {
         return -1;
      } else {
         int chunk = nodeIndex >> 8;
         int index = nodeIndex & 255;
         return free ? (short)this.clearChunkIndex(this.fNodeType, chunk, index) : (short)this.getChunkIndex(this.fNodeType, chunk, index);
      }
   }

   public String getAttribute(int elemIndex, String name) {
      if (elemIndex != -1 && name != null) {
         int echunk = elemIndex >> 8;
         int eindex = elemIndex & 255;

         int achunk;
         int aindex;
         for(int attrIndex = this.getChunkIndex(this.fNodeExtra, echunk, eindex); attrIndex != -1; attrIndex = this.getChunkIndex(this.fNodePrevSib, achunk, aindex)) {
            achunk = attrIndex >> 8;
            aindex = attrIndex & 255;
            if (this.getChunkValue(this.fNodeName, achunk, aindex) == name) {
               return this.getChunkValue(this.fNodeValue, achunk, aindex);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public String getNodeURI(int nodeIndex) {
      return this.getNodeURI(nodeIndex, true);
   }

   public String getNodeURI(int nodeIndex, boolean free) {
      if (nodeIndex == -1) {
         return null;
      } else {
         int chunk = nodeIndex >> 8;
         int index = nodeIndex & 255;
         return free ? this.clearChunkValue(this.fNodeURI, chunk, index) : this.getChunkValue(this.fNodeURI, chunk, index);
      }
   }

   public void putIdentifier(String name, int elementNodeIndex) {
      if (this.fIdName == null) {
         this.fIdName = new String[64];
         this.fIdElement = new int[64];
      }

      if (this.fIdCount == this.fIdName.length) {
         String[] idName = new String[this.fIdCount * 2];
         System.arraycopy(this.fIdName, 0, idName, 0, this.fIdCount);
         this.fIdName = idName;
         int[] idElement = new int[idName.length];
         System.arraycopy(this.fIdElement, 0, idElement, 0, this.fIdCount);
         this.fIdElement = idElement;
      }

      this.fIdName[this.fIdCount] = name;
      this.fIdElement[this.fIdCount] = elementNodeIndex;
      ++this.fIdCount;
   }

   public void print() {
   }

   public int getNodeIndex() {
      return 0;
   }

   protected void synchronizeData() {
      this.needsSyncData(false);
      if (this.fIdElement != null) {
         DeferredDocumentImpl.IntVector path = new DeferredDocumentImpl.IntVector();

         for(int i = 0; i < this.fIdCount; ++i) {
            int elementNodeIndex = this.fIdElement[i];
            String idName = this.fIdName[i];
            if (idName != null) {
               path.removeAllElements();
               int index = elementNodeIndex;

               int j;
               do {
                  path.addElement(index);
                  int pchunk = index >> 8;
                  j = index & 255;
                  index = this.getChunkIndex(this.fNodeParent, pchunk, j);
               } while(index != -1);

               Node place = this;

               for(j = path.size() - 2; j >= 0; --j) {
                  index = path.elementAt(j);

                  for(Node child = ((Node)place).getLastChild(); child != null; child = child.getPreviousSibling()) {
                     if (child instanceof DeferredNode) {
                        int nodeIndex = ((DeferredNode)child).getNodeIndex();
                        if (nodeIndex == index) {
                           place = child;
                           break;
                        }
                     }
                  }
               }

               Element element = (Element)place;
               this.putIdentifier0(idName, element);
               this.fIdName[i] = null;

               while(i + 1 < this.fIdCount && this.fIdElement[i + 1] == elementNodeIndex) {
                  ++i;
                  idName = this.fIdName[i];
                  if (idName != null) {
                     this.putIdentifier0(idName, element);
                  }
               }
            }
         }
      }

   }

   protected void synchronizeChildren() {
      if (this.needsSyncData()) {
         this.synchronizeData();
         if (!this.needsSyncChildren()) {
            return;
         }
      }

      boolean orig = this.mutationEvents;
      this.mutationEvents = false;
      this.needsSyncChildren(false);
      this.getNodeType(0);
      ChildNode first = null;
      ChildNode last = null;

      for(int index = this.getLastChild(0); index != -1; index = this.getPrevSibling(index)) {
         ChildNode node = (ChildNode)this.getNodeObject(index);
         if (last == null) {
            last = node;
         } else {
            first.previousSibling = node;
         }

         node.ownerNode = this;
         node.isOwned(true);
         node.nextSibling = first;
         first = node;
         int type = node.getNodeType();
         if (type == 1) {
            this.docElement = (ElementImpl)node;
         } else if (type == 10) {
            this.docType = (DocumentTypeImpl)node;
         }
      }

      if (first != null) {
         this.firstChild = first;
         first.isFirstChild(true);
         this.lastChild(last);
      }

      this.mutationEvents = orig;
   }

   protected final void synchronizeChildren(AttrImpl a, int nodeIndex) {
      boolean orig = this.getMutationEvents();
      this.setMutationEvents(false);
      a.needsSyncChildren(false);
      int last = this.getLastChild(nodeIndex);
      int prev = this.getPrevSibling(last);
      if (prev == -1) {
         a.value = this.getNodeValueString(nodeIndex);
         a.hasStringValue(true);
      } else {
         ChildNode firstNode = null;
         ChildNode lastNode = null;

         for(int index = last; index != -1; index = this.getPrevSibling(index)) {
            ChildNode node = (ChildNode)this.getNodeObject(index);
            if (lastNode == null) {
               lastNode = node;
            } else {
               firstNode.previousSibling = node;
            }

            node.ownerNode = a;
            node.isOwned(true);
            node.nextSibling = firstNode;
            firstNode = node;
         }

         if (lastNode != null) {
            a.value = firstNode;
            firstNode.isFirstChild(true);
            a.lastChild(lastNode);
         }

         a.hasStringValue(false);
      }

      this.setMutationEvents(orig);
   }

   protected final void synchronizeChildren(ParentNode p, int nodeIndex) {
      boolean orig = this.getMutationEvents();
      this.setMutationEvents(false);
      p.needsSyncChildren(false);
      ChildNode firstNode = null;
      ChildNode lastNode = null;

      for(int index = this.getLastChild(nodeIndex); index != -1; index = this.getPrevSibling(index)) {
         ChildNode node = (ChildNode)this.getNodeObject(index);
         if (lastNode == null) {
            lastNode = node;
         } else {
            firstNode.previousSibling = node;
         }

         node.ownerNode = p;
         node.isOwned(true);
         node.nextSibling = firstNode;
         firstNode = node;
      }

      if (lastNode != null) {
         p.firstChild = firstNode;
         firstNode.isFirstChild(true);
         p.lastChild(lastNode);
      }

      this.setMutationEvents(orig);
   }

   protected void ensureCapacity(int chunk) {
      if (this.fNodeType == null) {
         this.fNodeType = new int[32][];
         this.fNodeName = new Object[32][];
         this.fNodeValue = new Object[32][];
         this.fNodeParent = new int[32][];
         this.fNodeLastChild = new int[32][];
         this.fNodePrevSib = new int[32][];
         this.fNodeURI = new Object[32][];
         this.fNodeExtra = new int[32][];
      } else if (this.fNodeType.length <= chunk) {
         int newsize = chunk * 2;
         int[][] newArray = new int[newsize][];
         System.arraycopy(this.fNodeType, 0, newArray, 0, chunk);
         this.fNodeType = newArray;
         Object[][] newStrArray = new Object[newsize][];
         System.arraycopy(this.fNodeName, 0, newStrArray, 0, chunk);
         this.fNodeName = newStrArray;
         newStrArray = new Object[newsize][];
         System.arraycopy(this.fNodeValue, 0, newStrArray, 0, chunk);
         this.fNodeValue = newStrArray;
         newArray = new int[newsize][];
         System.arraycopy(this.fNodeParent, 0, newArray, 0, chunk);
         this.fNodeParent = newArray;
         newArray = new int[newsize][];
         System.arraycopy(this.fNodeLastChild, 0, newArray, 0, chunk);
         this.fNodeLastChild = newArray;
         newArray = new int[newsize][];
         System.arraycopy(this.fNodePrevSib, 0, newArray, 0, chunk);
         this.fNodePrevSib = newArray;
         newStrArray = new Object[newsize][];
         System.arraycopy(this.fNodeURI, 0, newStrArray, 0, chunk);
         this.fNodeURI = newStrArray;
         newArray = new int[newsize][];
         System.arraycopy(this.fNodeExtra, 0, newArray, 0, chunk);
         this.fNodeExtra = newArray;
      } else if (this.fNodeType[chunk] != null) {
         return;
      }

      this.createChunk(this.fNodeType, chunk);
      this.createChunk(this.fNodeName, chunk);
      this.createChunk(this.fNodeValue, chunk);
      this.createChunk(this.fNodeParent, chunk);
      this.createChunk(this.fNodeLastChild, chunk);
      this.createChunk(this.fNodePrevSib, chunk);
      this.createChunk(this.fNodeURI, chunk);
      this.createChunk(this.fNodeExtra, chunk);
   }

   protected int createNode(short nodeType) {
      int chunk = this.fNodeCount >> 8;
      int index = this.fNodeCount & 255;
      this.ensureCapacity(chunk);
      this.setChunkIndex(this.fNodeType, nodeType, chunk, index);
      return this.fNodeCount++;
   }

   protected static int binarySearch(int[] values, int start, int end, int target) {
      while(start <= end) {
         int middle = start + end >>> 1;
         int value = values[middle];
         if (value == target) {
            while(middle > 0 && values[middle - 1] == target) {
               --middle;
            }

            return middle;
         }

         if (value > target) {
            end = middle - 1;
         } else {
            start = middle + 1;
         }
      }

      return -1;
   }

   private final void createChunk(int[][] data, int chunk) {
      data[chunk] = new int[257];
      System.arraycopy(INIT_ARRAY, 0, data[chunk], 0, 256);
   }

   private final void createChunk(Object[][] data, int chunk) {
      data[chunk] = new Object[257];
      data[chunk][256] = new DeferredDocumentImpl.RefCount();
   }

   private final int setChunkIndex(int[][] data, int value, int chunk, int index) {
      if (value == -1) {
         return this.clearChunkIndex(data, chunk, index);
      } else {
         int[] dataChunk = data[chunk];
         if (dataChunk == null) {
            this.createChunk(data, chunk);
            dataChunk = data[chunk];
         }

         int ovalue = dataChunk[index];
         if (ovalue == -1) {
            int var10002 = dataChunk[256]++;
         }

         dataChunk[index] = value;
         return ovalue;
      }
   }

   private final String setChunkValue(Object[][] data, Object value, int chunk, int index) {
      if (value == null) {
         return this.clearChunkValue(data, chunk, index);
      } else {
         Object[] dataChunk = data[chunk];
         if (dataChunk == null) {
            this.createChunk(data, chunk);
            dataChunk = data[chunk];
         }

         String ovalue = (String)dataChunk[index];
         if (ovalue == null) {
            DeferredDocumentImpl.RefCount c = (DeferredDocumentImpl.RefCount)dataChunk[256];
            ++c.fCount;
         }

         dataChunk[index] = value;
         return ovalue;
      }
   }

   private final int getChunkIndex(int[][] data, int chunk, int index) {
      return data[chunk] != null ? data[chunk][index] : -1;
   }

   private final String getChunkValue(Object[][] data, int chunk, int index) {
      return data[chunk] != null ? (String)data[chunk][index] : null;
   }

   private final String getNodeValue(int chunk, int index) {
      Object data = this.fNodeValue[chunk][index];
      if (data == null) {
         return null;
      } else {
         return data instanceof String ? (String)data : data.toString();
      }
   }

   private final int clearChunkIndex(int[][] data, int chunk, int index) {
      int value = data[chunk] != null ? data[chunk][index] : -1;
      if (value != -1) {
         int var10002 = data[chunk][256]--;
         data[chunk][index] = -1;
         if (data[chunk][256] == 0) {
            data[chunk] = null;
         }
      }

      return value;
   }

   private final String clearChunkValue(Object[][] data, int chunk, int index) {
      String value = data[chunk] != null ? (String)data[chunk][index] : null;
      if (value != null) {
         data[chunk][index] = null;
         DeferredDocumentImpl.RefCount c = (DeferredDocumentImpl.RefCount)data[chunk][256];
         --c.fCount;
         if (c.fCount == 0) {
            data[chunk] = null;
         }
      }

      return value;
   }

   private final void putIdentifier0(String idName, Element element) {
      if (this.identifiers == null) {
         this.identifiers = new HashMap();
      }

      this.identifiers.put(idName, element);
   }

   private static void print(int[] values, int start, int end, int middle, int target) {
   }

   static {
      for(int i = 0; i < 256; ++i) {
         INIT_ARRAY[i] = -1;
      }

   }

   static final class IntVector {
      private int[] data;
      private int size;

      public int size() {
         return this.size;
      }

      public int elementAt(int index) {
         return this.data[index];
      }

      public void addElement(int element) {
         this.ensureCapacity(this.size + 1);
         this.data[this.size++] = element;
      }

      public void removeAllElements() {
         this.size = 0;
      }

      private void ensureCapacity(int newsize) {
         if (this.data == null) {
            this.data = new int[newsize + 15];
         } else if (newsize > this.data.length) {
            int[] newdata = new int[newsize + 15];
            System.arraycopy(this.data, 0, newdata, 0, this.data.length);
            this.data = newdata;
         }

      }
   }

   static final class RefCount {
      int fCount;
   }
}
