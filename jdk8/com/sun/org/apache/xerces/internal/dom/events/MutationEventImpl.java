package com.sun.org.apache.xerces.internal.dom.events;

import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

public class MutationEventImpl extends EventImpl implements MutationEvent {
   Node relatedNode = null;
   String prevValue = null;
   String newValue = null;
   String attrName = null;
   public short attrChange;
   public static final String DOM_SUBTREE_MODIFIED = "DOMSubtreeModified";
   public static final String DOM_NODE_INSERTED = "DOMNodeInserted";
   public static final String DOM_NODE_REMOVED = "DOMNodeRemoved";
   public static final String DOM_NODE_REMOVED_FROM_DOCUMENT = "DOMNodeRemovedFromDocument";
   public static final String DOM_NODE_INSERTED_INTO_DOCUMENT = "DOMNodeInsertedIntoDocument";
   public static final String DOM_ATTR_MODIFIED = "DOMAttrModified";
   public static final String DOM_CHARACTER_DATA_MODIFIED = "DOMCharacterDataModified";

   public String getAttrName() {
      return this.attrName;
   }

   public short getAttrChange() {
      return this.attrChange;
   }

   public String getNewValue() {
      return this.newValue;
   }

   public String getPrevValue() {
      return this.prevValue;
   }

   public Node getRelatedNode() {
      return this.relatedNode;
   }

   public void initMutationEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, Node relatedNodeArg, String prevValueArg, String newValueArg, String attrNameArg, short attrChangeArg) {
      this.relatedNode = relatedNodeArg;
      this.prevValue = prevValueArg;
      this.newValue = newValueArg;
      this.attrName = attrNameArg;
      this.attrChange = attrChangeArg;
      super.initEvent(typeArg, canBubbleArg, cancelableArg);
   }
}
