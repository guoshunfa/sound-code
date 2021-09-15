package com.sun.xml.internal.ws.api.databinding;

public enum SoapBodyStyle {
   DocumentBare,
   DocumentWrapper,
   RpcLiteral,
   RpcEncoded,
   Unspecificed;

   public boolean isDocument() {
      return this.equals(DocumentBare) || this.equals(DocumentWrapper);
   }

   public boolean isRpc() {
      return this.equals(RpcLiteral) || this.equals(RpcEncoded);
   }

   public boolean isLiteral() {
      return this.equals(RpcLiteral) || this.isDocument();
   }

   public boolean isBare() {
      return this.equals(DocumentBare);
   }

   public boolean isDocumentWrapper() {
      return this.equals(DocumentWrapper);
   }
}
