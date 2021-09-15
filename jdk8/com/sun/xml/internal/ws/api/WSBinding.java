package com.sun.xml.internal.ws.api;

import com.oracle.webservices.internal.api.message.MessageContextFactory;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;

public interface WSBinding extends Binding {
   SOAPVersion getSOAPVersion();

   AddressingVersion getAddressingVersion();

   @NotNull
   BindingID getBindingId();

   @NotNull
   List<Handler> getHandlerChain();

   boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> var1);

   boolean isOperationFeatureEnabled(@NotNull Class<? extends WebServiceFeature> var1, @NotNull QName var2);

   @Nullable
   <F extends WebServiceFeature> F getFeature(@NotNull Class<F> var1);

   @Nullable
   <F extends WebServiceFeature> F getOperationFeature(@NotNull Class<F> var1, @NotNull QName var2);

   @NotNull
   WSFeatureList getFeatures();

   @NotNull
   WSFeatureList getOperationFeatures(@NotNull QName var1);

   @NotNull
   WSFeatureList getInputMessageFeatures(@NotNull QName var1);

   @NotNull
   WSFeatureList getOutputMessageFeatures(@NotNull QName var1);

   @NotNull
   WSFeatureList getFaultMessageFeatures(@NotNull QName var1, @NotNull QName var2);

   @NotNull
   Set<QName> getKnownHeaders();

   boolean addKnownHeader(QName var1);

   @NotNull
   MessageContextFactory getMessageContextFactory();
}
