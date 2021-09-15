package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.policy.PolicyMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.jws.WebParam;
import javax.xml.namespace.QName;

public final class WSDLModelImpl extends AbstractExtensibleImpl implements EditableWSDLModel {
   private final Map<QName, EditableWSDLMessage> messages = new HashMap();
   private final Map<QName, EditableWSDLPortType> portTypes = new HashMap();
   private final Map<QName, EditableWSDLBoundPortType> bindings = new HashMap();
   private final Map<QName, EditableWSDLService> services = new LinkedHashMap();
   private PolicyMap policyMap;
   private final Map<QName, EditableWSDLBoundPortType> unmBindings;

   public WSDLModelImpl(@NotNull String systemId) {
      super(systemId, -1);
      this.unmBindings = Collections.unmodifiableMap(this.bindings);
   }

   public WSDLModelImpl() {
      super((String)null, -1);
      this.unmBindings = Collections.unmodifiableMap(this.bindings);
   }

   public void addMessage(EditableWSDLMessage msg) {
      this.messages.put(msg.getName(), msg);
   }

   public EditableWSDLMessage getMessage(QName name) {
      return (EditableWSDLMessage)this.messages.get(name);
   }

   public void addPortType(EditableWSDLPortType pt) {
      this.portTypes.put(pt.getName(), pt);
   }

   public EditableWSDLPortType getPortType(QName name) {
      return (EditableWSDLPortType)this.portTypes.get(name);
   }

   public void addBinding(EditableWSDLBoundPortType boundPortType) {
      assert !this.bindings.containsValue(boundPortType);

      this.bindings.put(boundPortType.getName(), boundPortType);
   }

   public EditableWSDLBoundPortType getBinding(QName name) {
      return (EditableWSDLBoundPortType)this.bindings.get(name);
   }

   public void addService(EditableWSDLService svc) {
      this.services.put(svc.getName(), svc);
   }

   public EditableWSDLService getService(QName name) {
      return (EditableWSDLService)this.services.get(name);
   }

   public Map<QName, EditableWSDLMessage> getMessages() {
      return this.messages;
   }

   @NotNull
   public Map<QName, EditableWSDLPortType> getPortTypes() {
      return this.portTypes;
   }

   @NotNull
   public Map<QName, ? extends EditableWSDLBoundPortType> getBindings() {
      return this.unmBindings;
   }

   @NotNull
   public Map<QName, EditableWSDLService> getServices() {
      return this.services;
   }

   public QName getFirstServiceName() {
      return this.services.isEmpty() ? null : ((EditableWSDLService)this.services.values().iterator().next()).getName();
   }

   public EditableWSDLBoundPortType getBinding(QName serviceName, QName portName) {
      EditableWSDLService service = (EditableWSDLService)this.services.get(serviceName);
      if (service != null) {
         EditableWSDLPort port = service.get(portName);
         if (port != null) {
            return port.getBinding();
         }
      }

      return null;
   }

   public void finalizeRpcLitBinding(EditableWSDLBoundPortType boundPortType) {
      assert boundPortType != null;

      QName portTypeName = boundPortType.getPortTypeName();
      if (portTypeName != null) {
         WSDLPortType pt = (WSDLPortType)this.portTypes.get(portTypeName);
         if (pt != null) {
            Iterator var4 = boundPortType.getBindingOperations().iterator();

            while(true) {
               EditableWSDLBoundOperation bop;
               int bodyindex;
               EditableWSDLMessage outMsg;
               do {
                  WSDLMessage outMsgName;
                  do {
                     WSDLOperation pto;
                     do {
                        WSDLMessage inMsgName;
                        do {
                           if (!var4.hasNext()) {
                              return;
                           }

                           bop = (EditableWSDLBoundOperation)var4.next();
                           pto = pt.get(bop.getName().getLocalPart());
                           inMsgName = pto.getInput().getMessage();
                        } while(inMsgName == null);

                        EditableWSDLMessage inMsg = (EditableWSDLMessage)this.messages.get(inMsgName.getName());
                        bodyindex = 0;
                        if (inMsg != null) {
                           Iterator var10 = inMsg.parts().iterator();

                           while(var10.hasNext()) {
                              EditableWSDLPart part = (EditableWSDLPart)var10.next();
                              String name = part.getName();
                              ParameterBinding pb = bop.getInputBinding(name);
                              if (pb.isBody()) {
                                 part.setIndex(bodyindex++);
                                 part.setBinding(pb);
                                 bop.addPart(part, WebParam.Mode.IN);
                              }
                           }
                        }

                        bodyindex = 0;
                     } while(pto.isOneWay());

                     outMsgName = pto.getOutput().getMessage();
                  } while(outMsgName == null);

                  outMsg = (EditableWSDLMessage)this.messages.get(outMsgName.getName());
               } while(outMsg == null);

               Iterator var18 = outMsg.parts().iterator();

               while(var18.hasNext()) {
                  EditableWSDLPart part = (EditableWSDLPart)var18.next();
                  String name = part.getName();
                  ParameterBinding pb = bop.getOutputBinding(name);
                  if (pb.isBody()) {
                     part.setIndex(bodyindex++);
                     part.setBinding(pb);
                     bop.addPart(part, WebParam.Mode.OUT);
                  }
               }
            }
         }
      }
   }

   public PolicyMap getPolicyMap() {
      return this.policyMap;
   }

   public void setPolicyMap(PolicyMap policyMap) {
      this.policyMap = policyMap;
   }

   public void freeze() {
      Iterator var1 = this.services.values().iterator();

      while(var1.hasNext()) {
         EditableWSDLService service = (EditableWSDLService)var1.next();
         service.freeze(this);
      }

      var1 = this.bindings.values().iterator();

      while(var1.hasNext()) {
         EditableWSDLBoundPortType bp = (EditableWSDLBoundPortType)var1.next();
         bp.freeze();
      }

      var1 = this.portTypes.values().iterator();

      while(var1.hasNext()) {
         EditableWSDLPortType pt = (EditableWSDLPortType)var1.next();
         pt.freeze();
      }

   }
}
