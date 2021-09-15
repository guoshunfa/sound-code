package com.sun.xml.internal.ws.api.wsdl.writer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.server.Container;

public class WSDLGenExtnContext {
   private final TypedXmlWriter root;
   private final SEIModel model;
   private final WSBinding binding;
   private final Container container;
   private final Class endpointClass;

   public WSDLGenExtnContext(@NotNull TypedXmlWriter root, @NotNull SEIModel model, @NotNull WSBinding binding, @Nullable Container container, @NotNull Class endpointClass) {
      this.root = root;
      this.model = model;
      this.binding = binding;
      this.container = container;
      this.endpointClass = endpointClass;
   }

   public TypedXmlWriter getRoot() {
      return this.root;
   }

   public SEIModel getModel() {
      return this.model;
   }

   public WSBinding getBinding() {
      return this.binding;
   }

   public Container getContainer() {
      return this.container;
   }

   public Class getEndpointClass() {
      return this.endpointClass;
   }
}
