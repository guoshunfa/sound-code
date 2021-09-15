package com.sun.xml.internal.ws.model;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.soap.SOAPBindingImpl;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.wsdl.ActionBasedOperationSignature;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.xml.namespace.QName;
import javax.xml.ws.Action;
import javax.xml.ws.WebServiceException;

public final class JavaMethodImpl implements JavaMethod {
   private String inputAction = "";
   private String outputAction = "";
   private final List<CheckedExceptionImpl> exceptions = new ArrayList();
   private final Method method;
   final List<ParameterImpl> requestParams = new ArrayList();
   final List<ParameterImpl> responseParams = new ArrayList();
   private final List<ParameterImpl> unmReqParams;
   private final List<ParameterImpl> unmResParams;
   private SOAPBinding binding;
   private MEP mep;
   private QName operationName;
   private WSDLBoundOperation wsdlOperation;
   final AbstractSEIModelImpl owner;
   private final Method seiMethod;
   private QName requestPayloadName;
   private String soapAction;
   private static final Logger LOGGER = Logger.getLogger(JavaMethodImpl.class.getName());

   public JavaMethodImpl(AbstractSEIModelImpl owner, Method method, Method seiMethod, MetadataReader metadataReader) {
      this.unmReqParams = Collections.unmodifiableList(this.requestParams);
      this.unmResParams = Collections.unmodifiableList(this.responseParams);
      this.owner = owner;
      this.method = method;
      this.seiMethod = seiMethod;
      this.setWsaActions(metadataReader);
   }

   private void setWsaActions(MetadataReader metadataReader) {
      Action action = metadataReader != null ? (Action)metadataReader.getAnnotation(Action.class, this.seiMethod) : (Action)this.seiMethod.getAnnotation(Action.class);
      if (action != null) {
         this.inputAction = action.input();
         this.outputAction = action.output();
      }

      WebMethod webMethod = metadataReader != null ? (WebMethod)metadataReader.getAnnotation(WebMethod.class, this.seiMethod) : (WebMethod)this.seiMethod.getAnnotation(WebMethod.class);
      this.soapAction = "";
      if (webMethod != null) {
         this.soapAction = webMethod.action();
      }

      if (!this.soapAction.equals("")) {
         if (this.inputAction.equals("")) {
            this.inputAction = this.soapAction;
         } else if (!this.inputAction.equals(this.soapAction)) {
         }
      }

   }

   public ActionBasedOperationSignature getOperationSignature() {
      QName qname = this.getRequestPayloadName();
      if (qname == null) {
         qname = new QName("", "");
      }

      return new ActionBasedOperationSignature(this.getInputAction(), qname);
   }

   public SEIModel getOwner() {
      return this.owner;
   }

   public Method getMethod() {
      return this.method;
   }

   public Method getSEIMethod() {
      return this.seiMethod;
   }

   public MEP getMEP() {
      return this.mep;
   }

   void setMEP(MEP mep) {
      this.mep = mep;
   }

   public SOAPBinding getBinding() {
      return (SOAPBinding)(this.binding == null ? new SOAPBindingImpl() : this.binding);
   }

   void setBinding(SOAPBinding binding) {
      this.binding = binding;
   }

   /** @deprecated */
   public WSDLBoundOperation getOperation() {
      return this.wsdlOperation;
   }

   public void setOperationQName(QName name) {
      this.operationName = name;
   }

   public QName getOperationQName() {
      return this.wsdlOperation != null ? this.wsdlOperation.getName() : this.operationName;
   }

   public String getSOAPAction() {
      return this.wsdlOperation != null ? this.wsdlOperation.getSOAPAction() : this.soapAction;
   }

   public String getOperationName() {
      return this.operationName.getLocalPart();
   }

   public String getRequestMessageName() {
      return this.getOperationName();
   }

   public String getResponseMessageName() {
      return this.mep.isOneWay() ? null : this.getOperationName() + "Response";
   }

   public void setRequestPayloadName(QName n) {
      this.requestPayloadName = n;
   }

   @Nullable
   public QName getRequestPayloadName() {
      return this.wsdlOperation != null ? this.wsdlOperation.getRequestPayloadName() : this.requestPayloadName;
   }

   @Nullable
   public QName getResponsePayloadName() {
      return this.mep == MEP.ONE_WAY ? null : this.wsdlOperation.getResponsePayloadName();
   }

   public List<ParameterImpl> getRequestParameters() {
      return this.unmReqParams;
   }

   public List<ParameterImpl> getResponseParameters() {
      return this.unmResParams;
   }

   void addParameter(ParameterImpl p) {
      if (p.isIN() || p.isINOUT()) {
         assert !this.requestParams.contains(p);

         this.requestParams.add(p);
      }

      if (p.isOUT() || p.isINOUT()) {
         assert !this.responseParams.contains(p);

         this.responseParams.add(p);
      }

   }

   void addRequestParameter(ParameterImpl p) {
      if (p.isIN() || p.isINOUT()) {
         this.requestParams.add(p);
      }

   }

   void addResponseParameter(ParameterImpl p) {
      if (p.isOUT() || p.isINOUT()) {
         this.responseParams.add(p);
      }

   }

   /** @deprecated */
   public int getInputParametersCount() {
      int count = 0;
      Iterator var2 = this.requestParams.iterator();

      ParameterImpl param;
      while(var2.hasNext()) {
         param = (ParameterImpl)var2.next();
         if (param.isWrapperStyle()) {
            count += ((WrapperParameter)param).getWrapperChildren().size();
         } else {
            ++count;
         }
      }

      var2 = this.responseParams.iterator();

      while(true) {
         while(var2.hasNext()) {
            param = (ParameterImpl)var2.next();
            if (param.isWrapperStyle()) {
               Iterator var4 = ((WrapperParameter)param).getWrapperChildren().iterator();

               while(var4.hasNext()) {
                  ParameterImpl wc = (ParameterImpl)var4.next();
                  if (!wc.isResponse() && wc.isOUT()) {
                     ++count;
                  }
               }
            } else if (!param.isResponse() && param.isOUT()) {
               ++count;
            }
         }

         return count;
      }
   }

   void addException(CheckedExceptionImpl ce) {
      if (!this.exceptions.contains(ce)) {
         this.exceptions.add(ce);
      }

   }

   public CheckedExceptionImpl getCheckedException(Class exceptionClass) {
      Iterator var2 = this.exceptions.iterator();

      CheckedExceptionImpl ce;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         ce = (CheckedExceptionImpl)var2.next();
      } while(ce.getExceptionClass() != exceptionClass);

      return ce;
   }

   public List<CheckedExceptionImpl> getCheckedExceptions() {
      return Collections.unmodifiableList(this.exceptions);
   }

   public String getInputAction() {
      return this.inputAction;
   }

   public String getOutputAction() {
      return this.outputAction;
   }

   /** @deprecated */
   public CheckedExceptionImpl getCheckedException(TypeReference detailType) {
      Iterator var2 = this.exceptions.iterator();

      CheckedExceptionImpl ce;
      TypeInfo actual;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         ce = (CheckedExceptionImpl)var2.next();
         actual = ce.getDetailType();
      } while(!actual.tagName.equals(detailType.tagName) || actual.type != detailType.type);

      return ce;
   }

   public boolean isAsync() {
      return this.mep.isAsync;
   }

   void freeze(WSDLPort portType) {
      this.wsdlOperation = portType.getBinding().get(new QName(portType.getBinding().getPortType().getName().getNamespaceURI(), this.getOperationName()));
      if (this.wsdlOperation == null) {
         throw new WebServiceException("Method " + this.seiMethod.getName() + " is exposed as WebMethod, but there is no corresponding wsdl operation with name " + this.operationName + " in the wsdl:portType" + portType.getBinding().getPortType().getName());
      } else {
         if (this.inputAction.equals("")) {
            this.inputAction = this.wsdlOperation.getOperation().getInput().getAction();
         } else if (!this.inputAction.equals(this.wsdlOperation.getOperation().getInput().getAction())) {
            LOGGER.warning("Input Action on WSDL operation " + this.wsdlOperation.getName().getLocalPart() + " and @Action on its associated Web Method " + this.seiMethod.getName() + " did not match and will cause problems in dispatching the requests");
         }

         if (!this.mep.isOneWay()) {
            if (this.outputAction.equals("")) {
               this.outputAction = this.wsdlOperation.getOperation().getOutput().getAction();
            }

            Iterator var2 = this.exceptions.iterator();

            while(var2.hasNext()) {
               CheckedExceptionImpl ce = (CheckedExceptionImpl)var2.next();
               if (ce.getFaultAction().equals("")) {
                  QName detailQName = ce.getDetailType().tagName;
                  WSDLFault wsdlfault = this.wsdlOperation.getOperation().getFault(detailQName);
                  if (wsdlfault == null) {
                     LOGGER.warning("Mismatch between Java model and WSDL model found, For wsdl operation " + this.wsdlOperation.getName() + ",There is no matching wsdl fault with detail QName " + ce.getDetailType().tagName);
                     ce.setFaultAction(ce.getDefaultFaultAction());
                  } else {
                     ce.setFaultAction(wsdlfault.getAction());
                  }
               }
            }
         }

      }
   }

   final void fillTypes(List<TypeInfo> types) {
      this.fillTypes(this.requestParams, types);
      this.fillTypes(this.responseParams, types);
      Iterator var2 = this.exceptions.iterator();

      while(var2.hasNext()) {
         CheckedExceptionImpl ce = (CheckedExceptionImpl)var2.next();
         types.add(ce.getDetailType());
      }

   }

   private void fillTypes(List<ParameterImpl> params, List<TypeInfo> types) {
      Iterator var3 = params.iterator();

      while(var3.hasNext()) {
         ParameterImpl p = (ParameterImpl)var3.next();
         p.fillTypes(types);
      }

   }
}
