package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public final class ContentTypeImpl implements com.sun.xml.internal.ws.api.pipe.ContentType {
   @NotNull
   private final String contentType;
   @NotNull
   private final String soapAction;
   private String accept;
   @Nullable
   private final String charset;
   private String boundary;
   private String boundaryParameter;
   private String rootId;
   private ContentType internalContentType;

   public ContentTypeImpl(String contentType) {
      this(contentType, (String)null, (String)null);
   }

   public ContentTypeImpl(String contentType, @Nullable String soapAction) {
      this(contentType, soapAction, (String)null);
   }

   public ContentTypeImpl(String contentType, @Nullable String soapAction, @Nullable String accept) {
      this(contentType, soapAction, accept, (String)null);
   }

   public ContentTypeImpl(String contentType, @Nullable String soapAction, @Nullable String accept, String charsetParam) {
      this.contentType = contentType;
      this.accept = accept;
      this.soapAction = this.getQuotedSOAPAction(soapAction);
      if (charsetParam == null) {
         String tmpCharset = null;

         try {
            this.internalContentType = new ContentType(contentType);
            tmpCharset = this.internalContentType.getParameter("charset");
         } catch (Exception var7) {
         }

         this.charset = tmpCharset;
      } else {
         this.charset = charsetParam;
      }

   }

   @Nullable
   public String getCharSet() {
      return this.charset;
   }

   private String getQuotedSOAPAction(String soapAction) {
      if (soapAction != null && soapAction.length() != 0) {
         return soapAction.charAt(0) != '"' && soapAction.charAt(soapAction.length() - 1) != '"' ? "\"" + soapAction + "\"" : soapAction;
      } else {
         return "\"\"";
      }
   }

   public String getContentType() {
      return this.contentType;
   }

   public String getSOAPActionHeader() {
      return this.soapAction;
   }

   public String getAcceptHeader() {
      return this.accept;
   }

   public void setAcceptHeader(String accept) {
      this.accept = accept;
   }

   public String getBoundary() {
      if (this.boundary == null) {
         if (this.internalContentType == null) {
            this.internalContentType = new ContentType(this.contentType);
         }

         this.boundary = this.internalContentType.getParameter("boundary");
      }

      return this.boundary;
   }

   public void setBoundary(String boundary) {
      this.boundary = boundary;
   }

   public String getBoundaryParameter() {
      return this.boundaryParameter;
   }

   public void setBoundaryParameter(String boundaryParameter) {
      this.boundaryParameter = boundaryParameter;
   }

   public String getRootId() {
      if (this.rootId == null) {
         if (this.internalContentType == null) {
            this.internalContentType = new ContentType(this.contentType);
         }

         this.rootId = this.internalContentType.getParameter("start");
      }

      return this.rootId;
   }

   public void setRootId(String rootId) {
      this.rootId = rootId;
   }

   public static class Builder {
      public String contentType;
      public String soapAction;
      public String accept;
      public String charset;

      public ContentTypeImpl build() {
         return new ContentTypeImpl(this.contentType, this.soapAction, this.accept, this.charset);
      }
   }
}
