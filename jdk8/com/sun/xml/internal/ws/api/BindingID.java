package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.encoding.SOAPBindingCodec;
import com.sun.xml.internal.ws.encoding.XMLHTTPBindingCodec;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public abstract class BindingID {
   public static final BindingID.SOAPHTTPImpl X_SOAP12_HTTP;
   public static final BindingID.SOAPHTTPImpl SOAP12_HTTP;
   public static final BindingID.SOAPHTTPImpl SOAP11_HTTP;
   public static final BindingID.SOAPHTTPImpl SOAP12_HTTP_MTOM;
   public static final BindingID.SOAPHTTPImpl SOAP11_HTTP_MTOM;
   public static final BindingID XML_HTTP;
   private static final BindingID REST_HTTP;

   @NotNull
   public final WSBinding createBinding() {
      return BindingImpl.create(this);
   }

   @NotNull
   public String getTransport() {
      return "http://schemas.xmlsoap.org/soap/http";
   }

   @NotNull
   public final WSBinding createBinding(WebServiceFeature... features) {
      return BindingImpl.create(this, features);
   }

   @NotNull
   public final WSBinding createBinding(WSFeatureList features) {
      return this.createBinding(features.toArray());
   }

   public abstract SOAPVersion getSOAPVersion();

   @NotNull
   public abstract Codec createEncoder(@NotNull WSBinding var1);

   public abstract String toString();

   public WebServiceFeatureList createBuiltinFeatureList() {
      return new WebServiceFeatureList();
   }

   public boolean canGenerateWSDL() {
      return false;
   }

   public String getParameter(String parameterName, String defaultValue) {
      return defaultValue;
   }

   public boolean equals(Object obj) {
      return !(obj instanceof BindingID) ? false : this.toString().equals(obj.toString());
   }

   public int hashCode() {
      return this.toString().hashCode();
   }

   @NotNull
   public static BindingID parse(String lexical) {
      if (lexical.equals(XML_HTTP.toString())) {
         return XML_HTTP;
      } else if (lexical.equals(REST_HTTP.toString())) {
         return REST_HTTP;
      } else if (belongsTo(lexical, SOAP11_HTTP.toString())) {
         return customize(lexical, SOAP11_HTTP);
      } else if (belongsTo(lexical, SOAP12_HTTP.toString())) {
         return customize(lexical, SOAP12_HTTP);
      } else if (belongsTo(lexical, "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")) {
         return customize(lexical, X_SOAP12_HTTP);
      } else {
         Iterator var1 = ServiceFinder.find(BindingIDFactory.class).iterator();

         BindingID r;
         do {
            if (!var1.hasNext()) {
               throw new WebServiceException("Wrong binding ID: " + lexical);
            }

            BindingIDFactory f = (BindingIDFactory)var1.next();
            r = f.parse(lexical);
         } while(r == null);

         return r;
      }
   }

   private static boolean belongsTo(String lexical, String id) {
      return lexical.equals(id) || lexical.startsWith(id + '?');
   }

   private static BindingID.SOAPHTTPImpl customize(String lexical, BindingID.SOAPHTTPImpl base) {
      if (lexical.equals(base.toString())) {
         return base;
      } else {
         BindingID.SOAPHTTPImpl r = new BindingID.SOAPHTTPImpl(base.getSOAPVersion(), lexical, base.canGenerateWSDL());

         try {
            if (lexical.indexOf(63) == -1) {
               return r;
            } else {
               String query = URLDecoder.decode(lexical.substring(lexical.indexOf(63) + 1), "UTF-8");
               String[] var4 = query.split("&");
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  String token = var4[var6];
                  int idx = token.indexOf(61);
                  if (idx < 0) {
                     throw new WebServiceException("Malformed binding ID (no '=' in " + token + ")");
                  }

                  r.parameters.put(token.substring(0, idx), token.substring(idx + 1));
               }

               return r;
            }
         } catch (UnsupportedEncodingException var9) {
            throw new AssertionError(var9);
         }
      }
   }

   @NotNull
   public static BindingID parse(Class<?> implClass) {
      BindingType bindingType = (BindingType)implClass.getAnnotation(BindingType.class);
      if (bindingType != null) {
         String bindingId = bindingType.value();
         if (bindingId.length() > 0) {
            return parse(bindingId);
         }
      }

      return SOAP11_HTTP;
   }

   static {
      X_SOAP12_HTTP = new BindingID.SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/", true);
      SOAP12_HTTP = new BindingID.SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://www.w3.org/2003/05/soap/bindings/HTTP/", true);
      SOAP11_HTTP = new BindingID.SOAPHTTPImpl(SOAPVersion.SOAP_11, "http://schemas.xmlsoap.org/wsdl/soap/http", true);
      SOAP12_HTTP_MTOM = new BindingID.SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true", true, true);
      SOAP11_HTTP_MTOM = new BindingID.SOAPHTTPImpl(SOAPVersion.SOAP_11, "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true", true, true);
      XML_HTTP = new BindingID.Impl(SOAPVersion.SOAP_11, "http://www.w3.org/2004/08/wsdl/http", false) {
         public Codec createEncoder(WSBinding binding) {
            return new XMLHTTPBindingCodec(binding.getFeatures());
         }
      };
      REST_HTTP = new BindingID.Impl(SOAPVersion.SOAP_11, "http://jax-ws.dev.java.net/rest", true) {
         public Codec createEncoder(WSBinding binding) {
            return new XMLHTTPBindingCodec(binding.getFeatures());
         }
      };
   }

   private static final class SOAPHTTPImpl extends BindingID.Impl implements Cloneable {
      Map<String, String> parameters;
      static final String MTOM_PARAM = "mtom";

      public SOAPHTTPImpl(SOAPVersion version, String lexical, boolean canGenerateWSDL) {
         super(version, lexical, canGenerateWSDL);
         this.parameters = new HashMap();
      }

      public SOAPHTTPImpl(SOAPVersion version, String lexical, boolean canGenerateWSDL, boolean mtomEnabled) {
         this(version, lexical, canGenerateWSDL);
         String mtomStr = mtomEnabled ? "true" : "false";
         this.parameters.put("mtom", mtomStr);
      }

      @NotNull
      public Codec createEncoder(WSBinding binding) {
         return new SOAPBindingCodec(binding.getFeatures());
      }

      private Boolean isMTOMEnabled() {
         String mtom = (String)this.parameters.get("mtom");
         return mtom == null ? null : Boolean.valueOf(mtom);
      }

      public WebServiceFeatureList createBuiltinFeatureList() {
         WebServiceFeatureList r = super.createBuiltinFeatureList();
         Boolean mtom = this.isMTOMEnabled();
         if (mtom != null) {
            r.add(new MTOMFeature(mtom));
         }

         return r;
      }

      public String getParameter(String parameterName, String defaultValue) {
         return this.parameters.get(parameterName) == null ? super.getParameter(parameterName, defaultValue) : (String)this.parameters.get(parameterName);
      }

      public BindingID.SOAPHTTPImpl clone() throws CloneNotSupportedException {
         return (BindingID.SOAPHTTPImpl)super.clone();
      }
   }

   private abstract static class Impl extends BindingID {
      final SOAPVersion version;
      private final String lexical;
      private final boolean canGenerateWSDL;

      public Impl(SOAPVersion version, String lexical, boolean canGenerateWSDL) {
         this.version = version;
         this.lexical = lexical;
         this.canGenerateWSDL = canGenerateWSDL;
      }

      public SOAPVersion getSOAPVersion() {
         return this.version;
      }

      public String toString() {
         return this.lexical;
      }

      /** @deprecated */
      @Deprecated
      public boolean canGenerateWSDL() {
         return this.canGenerateWSDL;
      }
   }
}
