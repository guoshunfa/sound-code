package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.util.exception.LocatableWebServiceException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;

public final class WSDLPortImpl extends AbstractFeaturedObjectImpl implements EditableWSDLPort {
   private final QName name;
   private EndpointAddress address;
   private final QName bindingName;
   private final EditableWSDLService owner;
   private WSEndpointReference epr;
   private EditableWSDLBoundPortType boundPortType;

   public WSDLPortImpl(XMLStreamReader xsr, EditableWSDLService owner, QName name, QName binding) {
      super(xsr);
      this.owner = owner;
      this.name = name;
      this.bindingName = binding;
   }

   public QName getName() {
      return this.name;
   }

   public QName getBindingName() {
      return this.bindingName;
   }

   public EndpointAddress getAddress() {
      return this.address;
   }

   public EditableWSDLService getOwner() {
      return this.owner;
   }

   public void setAddress(EndpointAddress address) {
      assert address != null;

      this.address = address;
   }

   public void setEPR(@NotNull WSEndpointReference epr) {
      assert epr != null;

      this.addExtension(epr);
      this.epr = epr;
   }

   @Nullable
   public WSEndpointReference getEPR() {
      return this.epr;
   }

   public EditableWSDLBoundPortType getBinding() {
      return this.boundPortType;
   }

   public void freeze(EditableWSDLModel root) {
      this.boundPortType = root.getBinding(this.bindingName);
      if (this.boundPortType == null) {
         throw new LocatableWebServiceException(ClientMessages.UNDEFINED_BINDING(this.bindingName), new Locator[]{this.getLocation()});
      } else {
         if (this.features == null) {
            this.features = new WebServiceFeatureList();
         }

         this.features.setParentFeaturedObject(this.boundPortType);
         this.notUnderstoodExtensions.addAll(this.boundPortType.getNotUnderstoodExtensions());
      }
   }
}
