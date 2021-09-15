package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;

public class CommentImpl extends CharacterDataImpl implements CharacterData, Comment {
   static final long serialVersionUID = -2685736833408134044L;

   public CommentImpl(CoreDocumentImpl ownerDoc, String data) {
      super(ownerDoc, data);
   }

   public short getNodeType() {
      return 8;
   }

   public String getNodeName() {
      return "#comment";
   }
}
