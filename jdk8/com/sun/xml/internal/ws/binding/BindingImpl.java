package com.sun.xml.internal.ws.binding;

import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.oracle.webservices.internal.api.message.MessageContextFactory;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.developer.BindingTypeFeature;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.AddressingFeature;

public abstract class BindingImpl implements WSBinding {
   protected static final WebServiceFeature[] EMPTY_FEATURES = new WebServiceFeature[0];
   private HandlerConfiguration handlerConfig;
   private final Set<QName> addedHeaders = new HashSet();
   private final Set<QName> knownHeaders = new HashSet();
   private final Set<QName> unmodKnownHeaders;
   private final BindingID bindingId;
   protected final WebServiceFeatureList features;
   protected final Map<QName, WebServiceFeatureList> operationFeatures;
   protected final Map<QName, WebServiceFeatureList> inputMessageFeatures;
   protected final Map<QName, WebServiceFeatureList> outputMessageFeatures;
   protected final Map<BindingImpl.MessageKey, WebServiceFeatureList> faultMessageFeatures;
   protected Service.Mode serviceMode;
   protected MessageContextFactory messageContextFactory;

   protected BindingImpl(BindingID bindingId, WebServiceFeature... features) {
      this.unmodKnownHeaders = Collections.unmodifiableSet(this.knownHeaders);
      this.operationFeatures = new HashMap();
      this.inputMessageFeatures = new HashMap();
      this.outputMessageFeatures = new HashMap();
      this.faultMessageFeatures = new HashMap();
      this.serviceMode = Service.Mode.PAYLOAD;
      this.bindingId = bindingId;
      this.handlerConfig = new HandlerConfiguration(Collections.emptySet(), Collections.emptyList());
      if (this.handlerConfig.getHandlerKnownHeaders() != null) {
         this.knownHeaders.addAll(this.handlerConfig.getHandlerKnownHeaders());
      }

      this.features = new WebServiceFeatureList(features);
      this.features.validate();
   }

   @NotNull
   public List<Handler> getHandlerChain() {
      return this.handlerConfig.getHandlerChain();
   }

   public HandlerConfiguration getHandlerConfig() {
      return this.handlerConfig;
   }

   protected void setHandlerConfig(HandlerConfiguration handlerConfig) {
      this.handlerConfig = handlerConfig;
      this.knownHeaders.clear();
      this.knownHeaders.addAll(this.addedHeaders);
      if (handlerConfig != null && handlerConfig.getHandlerKnownHeaders() != null) {
         this.knownHeaders.addAll(handlerConfig.getHandlerKnownHeaders());
      }

   }

   public void setMode(@NotNull Service.Mode mode) {
      this.serviceMode = mode;
   }

   public Set<QName> getKnownHeaders() {
      return this.unmodKnownHeaders;
   }

   public boolean addKnownHeader(QName headerQName) {
      this.addedHeaders.add(headerQName);
      return this.knownHeaders.add(headerQName);
   }

   @NotNull
   public BindingID getBindingId() {
      return this.bindingId;
   }

   public final SOAPVersion getSOAPVersion() {
      return this.bindingId.getSOAPVersion();
   }

   public AddressingVersion getAddressingVersion() {
      AddressingVersion addressingVersion;
      if (this.features.isEnabled(AddressingFeature.class)) {
         addressingVersion = AddressingVersion.W3C;
      } else if (this.features.isEnabled(MemberSubmissionAddressingFeature.class)) {
         addressingVersion = AddressingVersion.MEMBER;
      } else {
         addressingVersion = null;
      }

      return addressingVersion;
   }

   @NotNull
   public final Codec createCodec() {
      initializeJavaActivationHandlers();
      return this.bindingId.createEncoder(this);
   }

   public static void initializeJavaActivationHandlers() {
      try {
         CommandMap map = CommandMap.getDefaultCommandMap();
         if (map instanceof MailcapCommandMap) {
            MailcapCommandMap mailMap = (MailcapCommandMap)map;
            if (!cmdMapInitialized(mailMap)) {
               mailMap.addMailcap("text/xml;;x-java-content-handler=com.sun.xml.internal.ws.encoding.XmlDataContentHandler");
               mailMap.addMailcap("application/xml;;x-java-content-handler=com.sun.xml.internal.ws.encoding.XmlDataContentHandler");
               mailMap.addMailcap("image/*;;x-java-content-handler=com.sun.xml.internal.ws.encoding.ImageDataContentHandler");
               mailMap.addMailcap("text/plain;;x-java-content-handler=com.sun.xml.internal.ws.encoding.StringDataContentHandler");
            }
         }
      } catch (Throwable var2) {
      }

   }

   private static boolean cmdMapInitialized(MailcapCommandMap mailMap) {
      CommandInfo[] commands = mailMap.getAllCommands("text/xml");
      if (commands != null && commands.length != 0) {
         String saajClassName = "com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler";
         String jaxwsClassName = "com.sun.xml.internal.ws.encoding.XmlDataContentHandler";
         CommandInfo[] var4 = commands;
         int var5 = commands.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            CommandInfo command = var4[var6];
            String commandClass = command.getCommandClass();
            if (saajClassName.equals(commandClass) || jaxwsClassName.equals(commandClass)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static BindingImpl create(@NotNull BindingID bindingId) {
      return (BindingImpl)(bindingId.equals(BindingID.XML_HTTP) ? new HTTPBindingImpl() : new SOAPBindingImpl(bindingId));
   }

   public static BindingImpl create(@NotNull BindingID bindingId, WebServiceFeature[] features) {
      WebServiceFeature[] var2 = features;
      int var3 = features.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         WebServiceFeature feature = var2[var4];
         if (feature instanceof BindingTypeFeature) {
            BindingTypeFeature f = (BindingTypeFeature)feature;
            bindingId = BindingID.parse(f.getBindingId());
         }
      }

      if (bindingId.equals(BindingID.XML_HTTP)) {
         return new HTTPBindingImpl(features);
      } else {
         return new SOAPBindingImpl(bindingId, features);
      }
   }

   public static WSBinding getDefaultBinding() {
      return new SOAPBindingImpl(BindingID.SOAP11_HTTP);
   }

   public String getBindingID() {
      return this.bindingId.toString();
   }

   @Nullable
   public <F extends WebServiceFeature> F getFeature(@NotNull Class<F> featureType) {
      return this.features.get(featureType);
   }

   @Nullable
   public <F extends WebServiceFeature> F getOperationFeature(@NotNull Class<F> featureType, @NotNull QName operationName) {
      WebServiceFeatureList operationFeatureList = (WebServiceFeatureList)this.operationFeatures.get(operationName);
      return FeatureListUtil.mergeFeature(featureType, operationFeatureList, this.features);
   }

   public boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> feature) {
      return this.features.isEnabled(feature);
   }

   public boolean isOperationFeatureEnabled(@NotNull Class<? extends WebServiceFeature> featureType, @NotNull QName operationName) {
      WebServiceFeatureList operationFeatureList = (WebServiceFeatureList)this.operationFeatures.get(operationName);
      return FeatureListUtil.isFeatureEnabled(featureType, operationFeatureList, this.features);
   }

   @NotNull
   public WebServiceFeatureList getFeatures() {
      if (!this.isFeatureEnabled(EnvelopeStyleFeature.class)) {
         WebServiceFeature[] f = new WebServiceFeature[]{this.getSOAPVersion().toFeature()};
         this.features.mergeFeatures(f, false);
      }

      return this.features;
   }

   @NotNull
   public WebServiceFeatureList getOperationFeatures(@NotNull QName operationName) {
      WebServiceFeatureList operationFeatureList = (WebServiceFeatureList)this.operationFeatures.get(operationName);
      return FeatureListUtil.mergeList(operationFeatureList, this.features);
   }

   @NotNull
   public WebServiceFeatureList getInputMessageFeatures(@NotNull QName operationName) {
      WebServiceFeatureList operationFeatureList = (WebServiceFeatureList)this.operationFeatures.get(operationName);
      WebServiceFeatureList messageFeatureList = (WebServiceFeatureList)this.inputMessageFeatures.get(operationName);
      return FeatureListUtil.mergeList(operationFeatureList, messageFeatureList, this.features);
   }

   @NotNull
   public WebServiceFeatureList getOutputMessageFeatures(@NotNull QName operationName) {
      WebServiceFeatureList operationFeatureList = (WebServiceFeatureList)this.operationFeatures.get(operationName);
      WebServiceFeatureList messageFeatureList = (WebServiceFeatureList)this.outputMessageFeatures.get(operationName);
      return FeatureListUtil.mergeList(operationFeatureList, messageFeatureList, this.features);
   }

   @NotNull
   public WebServiceFeatureList getFaultMessageFeatures(@NotNull QName operationName, @NotNull QName messageName) {
      WebServiceFeatureList operationFeatureList = (WebServiceFeatureList)this.operationFeatures.get(operationName);
      WebServiceFeatureList messageFeatureList = (WebServiceFeatureList)this.faultMessageFeatures.get(new BindingImpl.MessageKey(operationName, messageName));
      return FeatureListUtil.mergeList(operationFeatureList, messageFeatureList, this.features);
   }

   public void setOperationFeatures(@NotNull QName operationName, WebServiceFeature... newFeatures) {
      if (newFeatures != null) {
         WebServiceFeatureList featureList = (WebServiceFeatureList)this.operationFeatures.get(operationName);
         if (featureList == null) {
            featureList = new WebServiceFeatureList();
         }

         WebServiceFeature[] var4 = newFeatures;
         int var5 = newFeatures.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            WebServiceFeature f = var4[var6];
            featureList.add(f);
         }

         this.operationFeatures.put(operationName, featureList);
      }

   }

   public void setInputMessageFeatures(@NotNull QName operationName, WebServiceFeature... newFeatures) {
      if (newFeatures != null) {
         WebServiceFeatureList featureList = (WebServiceFeatureList)this.inputMessageFeatures.get(operationName);
         if (featureList == null) {
            featureList = new WebServiceFeatureList();
         }

         WebServiceFeature[] var4 = newFeatures;
         int var5 = newFeatures.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            WebServiceFeature f = var4[var6];
            featureList.add(f);
         }

         this.inputMessageFeatures.put(operationName, featureList);
      }

   }

   public void setOutputMessageFeatures(@NotNull QName operationName, WebServiceFeature... newFeatures) {
      if (newFeatures != null) {
         WebServiceFeatureList featureList = (WebServiceFeatureList)this.outputMessageFeatures.get(operationName);
         if (featureList == null) {
            featureList = new WebServiceFeatureList();
         }

         WebServiceFeature[] var4 = newFeatures;
         int var5 = newFeatures.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            WebServiceFeature f = var4[var6];
            featureList.add(f);
         }

         this.outputMessageFeatures.put(operationName, featureList);
      }

   }

   public void setFaultMessageFeatures(@NotNull QName operationName, @NotNull QName messageName, WebServiceFeature... newFeatures) {
      if (newFeatures != null) {
         BindingImpl.MessageKey key = new BindingImpl.MessageKey(operationName, messageName);
         WebServiceFeatureList featureList = (WebServiceFeatureList)this.faultMessageFeatures.get(key);
         if (featureList == null) {
            featureList = new WebServiceFeatureList();
         }

         WebServiceFeature[] var6 = newFeatures;
         int var7 = newFeatures.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            WebServiceFeature f = var6[var8];
            featureList.add(f);
         }

         this.faultMessageFeatures.put(key, featureList);
      }

   }

   @NotNull
   public synchronized MessageContextFactory getMessageContextFactory() {
      if (this.messageContextFactory == null) {
         this.messageContextFactory = MessageContextFactory.createFactory(this.getFeatures().toArray());
      }

      return this.messageContextFactory;
   }

   protected static class MessageKey {
      private final QName operationName;
      private final QName messageName;

      public MessageKey(QName operationName, QName messageName) {
         this.operationName = operationName;
         this.messageName = messageName;
      }

      public int hashCode() {
         int hashFirst = this.operationName != null ? this.operationName.hashCode() : 0;
         int hashSecond = this.messageName != null ? this.messageName.hashCode() : 0;
         return (hashFirst + hashSecond) * hashSecond + hashFirst;
      }

      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            BindingImpl.MessageKey other = (BindingImpl.MessageKey)obj;
            if (this.operationName == other.operationName || this.operationName != null && this.operationName.equals(other.operationName)) {
               return this.messageName == other.messageName || this.messageName != null && this.messageName.equals(other.messageName);
            } else {
               return false;
            }
         }
      }

      public String toString() {
         return "(" + this.operationName + ", " + this.messageName + ")";
      }
   }
}
