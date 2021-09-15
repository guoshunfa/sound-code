package com.sun.org.apache.xerces.internal.impl.xs.opti;

public class NodeImpl extends DefaultNode {
   String prefix;
   String localpart;
   String rawname;
   String uri;
   short nodeType;
   boolean hidden;

   public NodeImpl() {
   }

   public NodeImpl(String prefix, String localpart, String rawname, String uri, short nodeType) {
      this.prefix = prefix;
      this.localpart = localpart;
      this.rawname = rawname;
      this.uri = uri;
      this.nodeType = nodeType;
   }

   public String getNodeName() {
      return this.rawname;
   }

   public String getNamespaceURI() {
      return this.uri;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public String getLocalName() {
      return this.localpart;
   }

   public short getNodeType() {
      return this.nodeType;
   }

   public void setReadOnly(boolean hide, boolean deep) {
      this.hidden = hide;
   }

   public boolean getReadOnly() {
      return this.hidden;
   }
}
