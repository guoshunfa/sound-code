package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.addressing.policy.AddressingPolicyMapConfigurator;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.policy.ModelGenerator;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.encoding.policy.MtomPolicyMapConfigurator;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.PolicyMapUtil;
import com.sun.xml.internal.ws.policy.PolicyMerger;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelGenerator;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelMarshaller;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class PolicyWSDLGeneratorExtension extends WSDLGeneratorExtension {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyWSDLGeneratorExtension.class);
   private PolicyMap policyMap;
   private SEIModel seiModel;
   private final Collection<PolicySubject> subjects = new LinkedList();
   private final PolicyModelMarshaller marshaller = PolicyModelMarshaller.getXmlMarshaller(true);
   private final PolicyMerger merger = PolicyMerger.getMerger();

   public void start(WSDLGenExtnContext context) {
      LOGGER.entering();

      try {
         this.seiModel = context.getModel();
         PolicyMapConfigurator[] policyMapConfigurators = this.loadConfigurators();
         PolicyMapExtender[] extenders = new PolicyMapExtender[policyMapConfigurators.length];

         for(int i = 0; i < policyMapConfigurators.length; ++i) {
            extenders[i] = PolicyMapExtender.createPolicyMapExtender();
         }

         this.policyMap = PolicyResolverFactory.create().resolve(new PolicyResolver.ServerContext(this.policyMap, context.getContainer(), context.getEndpointClass(), false, extenders));
         if (this.policyMap == null) {
            LOGGER.fine(PolicyMessages.WSP_1019_CREATE_EMPTY_POLICY_MAP());
            this.policyMap = PolicyMap.createPolicyMap(Arrays.asList(extenders));
         }

         WSBinding binding = context.getBinding();

         try {
            Collection<PolicySubject> policySubjects = new LinkedList();
            int i = 0;

            while(true) {
               if (i >= policyMapConfigurators.length) {
                  PolicyMapUtil.insertPolicies(this.policyMap, policySubjects, this.seiModel.getServiceQName(), this.seiModel.getPortName());
                  break;
               }

               policySubjects.addAll(policyMapConfigurators[i].update(this.policyMap, this.seiModel, binding));
               extenders[i].disconnect();
               ++i;
            }
         } catch (PolicyException var10) {
            throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1017_MAP_UPDATE_FAILED(), var10));
         }

         TypedXmlWriter root = context.getRoot();
         root._namespace(NamespaceVersion.v1_2.toString(), NamespaceVersion.v1_2.getDefaultNamespacePrefix());
         root._namespace(NamespaceVersion.v1_5.toString(), NamespaceVersion.v1_5.getDefaultNamespacePrefix());
         root._namespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu");
      } finally {
         LOGGER.exiting();
      }
   }

   public void addDefinitionsExtension(TypedXmlWriter definitions) {
      try {
         LOGGER.entering();
         if (this.policyMap == null) {
            LOGGER.fine(PolicyMessages.WSP_1009_NOT_MARSHALLING_ANY_POLICIES_POLICY_MAP_IS_NULL());
         } else {
            this.subjects.addAll(this.policyMap.getPolicySubjects());
            PolicyModelGenerator generator = ModelGenerator.getGenerator();
            Set<String> policyIDsOrNamesWritten = new HashSet();
            Iterator var4 = this.subjects.iterator();

            while(true) {
               while(var4.hasNext()) {
                  PolicySubject subject = (PolicySubject)var4.next();
                  if (subject.getSubject() == null) {
                     LOGGER.fine(PolicyMessages.WSP_1008_NOT_MARSHALLING_WSDL_SUBJ_NULL(subject));
                  } else {
                     Policy policy;
                     try {
                        policy = subject.getEffectivePolicy(this.merger);
                     } catch (PolicyException var13) {
                        throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT(subject.toString()), var13));
                     }

                     if (null != policy.getIdOrName() && !policyIDsOrNamesWritten.contains(policy.getIdOrName())) {
                        try {
                           PolicySourceModel policyInfoset = generator.translate(policy);
                           this.marshaller.marshal((PolicySourceModel)policyInfoset, definitions);
                        } catch (PolicyException var12) {
                           throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1018_FAILED_TO_MARSHALL_POLICY(policy.getIdOrName()), var12));
                        }

                        policyIDsOrNamesWritten.add(policy.getIdOrName());
                     } else {
                        LOGGER.fine(PolicyMessages.WSP_1016_POLICY_ID_NULL_OR_DUPLICATE(policy));
                     }
                  }
               }

               return;
            }
         }
      } finally {
         LOGGER.exiting();
      }

   }

   public void addServiceExtension(TypedXmlWriter service) {
      LOGGER.entering();
      String serviceName = null == this.seiModel ? null : this.seiModel.getServiceQName().getLocalPart();
      this.selectAndProcessSubject(service, WSDLService.class, PolicyWSDLGeneratorExtension.ScopeType.SERVICE, serviceName);
      LOGGER.exiting();
   }

   public void addPortExtension(TypedXmlWriter port) {
      LOGGER.entering();
      String portName = null == this.seiModel ? null : this.seiModel.getPortName().getLocalPart();
      this.selectAndProcessSubject(port, WSDLPort.class, PolicyWSDLGeneratorExtension.ScopeType.ENDPOINT, portName);
      LOGGER.exiting();
   }

   public void addPortTypeExtension(TypedXmlWriter portType) {
      LOGGER.entering();
      String portTypeName = null == this.seiModel ? null : this.seiModel.getPortTypeName().getLocalPart();
      this.selectAndProcessSubject(portType, WSDLPortType.class, PolicyWSDLGeneratorExtension.ScopeType.ENDPOINT, portTypeName);
      LOGGER.exiting();
   }

   public void addBindingExtension(TypedXmlWriter binding) {
      LOGGER.entering();
      QName bindingName = null == this.seiModel ? null : this.seiModel.getBoundPortTypeName();
      this.selectAndProcessBindingSubject(binding, WSDLBoundPortType.class, PolicyWSDLGeneratorExtension.ScopeType.ENDPOINT, bindingName);
      LOGGER.exiting();
   }

   public void addOperationExtension(TypedXmlWriter operation, JavaMethod method) {
      LOGGER.entering();
      this.selectAndProcessSubject(operation, WSDLOperation.class, PolicyWSDLGeneratorExtension.ScopeType.OPERATION, (String)null);
      LOGGER.exiting();
   }

   public void addBindingOperationExtension(TypedXmlWriter operation, JavaMethod method) {
      LOGGER.entering();
      QName operationName = method == null ? null : new QName(method.getOwner().getTargetNamespace(), method.getOperationName());
      this.selectAndProcessBindingSubject(operation, WSDLBoundOperation.class, PolicyWSDLGeneratorExtension.ScopeType.OPERATION, operationName);
      LOGGER.exiting();
   }

   public void addInputMessageExtension(TypedXmlWriter message, JavaMethod method) {
      LOGGER.entering();
      String messageName = null == method ? null : method.getRequestMessageName();
      this.selectAndProcessSubject(message, WSDLMessage.class, PolicyWSDLGeneratorExtension.ScopeType.INPUT_MESSAGE, messageName);
      LOGGER.exiting();
   }

   public void addOutputMessageExtension(TypedXmlWriter message, JavaMethod method) {
      LOGGER.entering();
      String messageName = null == method ? null : method.getResponseMessageName();
      this.selectAndProcessSubject(message, WSDLMessage.class, PolicyWSDLGeneratorExtension.ScopeType.OUTPUT_MESSAGE, messageName);
      LOGGER.exiting();
   }

   public void addFaultMessageExtension(TypedXmlWriter message, JavaMethod method, CheckedException exception) {
      LOGGER.entering();
      String messageName = null == exception ? null : exception.getMessageName();
      this.selectAndProcessSubject(message, WSDLMessage.class, PolicyWSDLGeneratorExtension.ScopeType.FAULT_MESSAGE, messageName);
      LOGGER.exiting();
   }

   public void addOperationInputExtension(TypedXmlWriter input, JavaMethod method) {
      LOGGER.entering();
      String messageName = null == method ? null : method.getRequestMessageName();
      this.selectAndProcessSubject(input, WSDLInput.class, PolicyWSDLGeneratorExtension.ScopeType.INPUT_MESSAGE, messageName);
      LOGGER.exiting();
   }

   public void addOperationOutputExtension(TypedXmlWriter output, JavaMethod method) {
      LOGGER.entering();
      String messageName = null == method ? null : method.getResponseMessageName();
      this.selectAndProcessSubject(output, WSDLOutput.class, PolicyWSDLGeneratorExtension.ScopeType.OUTPUT_MESSAGE, messageName);
      LOGGER.exiting();
   }

   public void addOperationFaultExtension(TypedXmlWriter fault, JavaMethod method, CheckedException exception) {
      LOGGER.entering();
      String messageName = null == exception ? null : exception.getMessageName();
      this.selectAndProcessSubject(fault, WSDLFault.class, PolicyWSDLGeneratorExtension.ScopeType.FAULT_MESSAGE, messageName);
      LOGGER.exiting();
   }

   public void addBindingOperationInputExtension(TypedXmlWriter input, JavaMethod method) {
      LOGGER.entering();
      QName operationName = new QName(method.getOwner().getTargetNamespace(), method.getOperationName());
      this.selectAndProcessBindingSubject(input, WSDLBoundOperation.class, PolicyWSDLGeneratorExtension.ScopeType.INPUT_MESSAGE, operationName);
      LOGGER.exiting();
   }

   public void addBindingOperationOutputExtension(TypedXmlWriter output, JavaMethod method) {
      LOGGER.entering();
      QName operationName = new QName(method.getOwner().getTargetNamespace(), method.getOperationName());
      this.selectAndProcessBindingSubject(output, WSDLBoundOperation.class, PolicyWSDLGeneratorExtension.ScopeType.OUTPUT_MESSAGE, operationName);
      LOGGER.exiting();
   }

   public void addBindingOperationFaultExtension(TypedXmlWriter writer, JavaMethod method, CheckedException exception) {
      LOGGER.entering(new Object[]{writer, method, exception});
      if (this.subjects != null) {
         Iterator var4 = this.subjects.iterator();

         while(var4.hasNext()) {
            PolicySubject subject = (PolicySubject)var4.next();
            if (this.policyMap.isFaultMessageSubject(subject)) {
               Object concreteSubject = subject.getSubject();
               if (concreteSubject != null) {
                  String exceptionName = exception == null ? null : exception.getMessageName();
                  if (exceptionName == null) {
                     this.writePolicyOrReferenceIt(subject, writer);
                  }

                  if (WSDLBoundFaultContainer.class.isInstance(concreteSubject)) {
                     WSDLBoundFaultContainer faultContainer = (WSDLBoundFaultContainer)concreteSubject;
                     WSDLBoundFault fault = faultContainer.getBoundFault();
                     WSDLBoundOperation operation = faultContainer.getBoundOperation();
                     if (exceptionName.equals(fault.getName()) && operation.getName().getLocalPart().equals(method.getOperationName())) {
                        this.writePolicyOrReferenceIt(subject, writer);
                     }
                  } else if (WsdlBindingSubject.class.isInstance(concreteSubject)) {
                     WsdlBindingSubject wsdlSubject = (WsdlBindingSubject)concreteSubject;
                     if (wsdlSubject.getMessageType() == WsdlBindingSubject.WsdlMessageType.FAULT && exception.getOwner().getTargetNamespace().equals(wsdlSubject.getName().getNamespaceURI()) && exceptionName.equals(wsdlSubject.getName().getLocalPart())) {
                        this.writePolicyOrReferenceIt(subject, writer);
                     }
                  }
               }
            }
         }
      }

      LOGGER.exiting();
   }

   private void selectAndProcessSubject(TypedXmlWriter xmlWriter, Class clazz, PolicyWSDLGeneratorExtension.ScopeType scopeType, QName bindingName) {
      LOGGER.entering(new Object[]{xmlWriter, clazz, scopeType, bindingName});
      if (bindingName == null) {
         this.selectAndProcessSubject(xmlWriter, clazz, scopeType, (String)null);
      } else {
         if (this.subjects != null) {
            Iterator var5 = this.subjects.iterator();

            while(var5.hasNext()) {
               PolicySubject subject = (PolicySubject)var5.next();
               if (bindingName.equals(subject.getSubject())) {
                  this.writePolicyOrReferenceIt(subject, xmlWriter);
               }
            }
         }

         this.selectAndProcessSubject(xmlWriter, clazz, scopeType, bindingName.getLocalPart());
      }

      LOGGER.exiting();
   }

   private void selectAndProcessBindingSubject(TypedXmlWriter xmlWriter, Class clazz, PolicyWSDLGeneratorExtension.ScopeType scopeType, QName bindingName) {
      LOGGER.entering(new Object[]{xmlWriter, clazz, scopeType, bindingName});
      if (this.subjects != null && bindingName != null) {
         Iterator var5 = this.subjects.iterator();

         while(var5.hasNext()) {
            PolicySubject subject = (PolicySubject)var5.next();
            if (subject.getSubject() instanceof WsdlBindingSubject) {
               WsdlBindingSubject wsdlSubject = (WsdlBindingSubject)subject.getSubject();
               if (bindingName.equals(wsdlSubject.getName())) {
                  this.writePolicyOrReferenceIt(subject, xmlWriter);
               }
            }
         }
      }

      this.selectAndProcessSubject(xmlWriter, clazz, scopeType, bindingName);
      LOGGER.exiting();
   }

   private void selectAndProcessSubject(TypedXmlWriter xmlWriter, Class clazz, PolicyWSDLGeneratorExtension.ScopeType scopeType, String wsdlName) {
      LOGGER.entering(new Object[]{xmlWriter, clazz, scopeType, wsdlName});
      if (this.subjects != null) {
         Iterator var5 = this.subjects.iterator();

         while(var5.hasNext()) {
            PolicySubject subject = (PolicySubject)var5.next();
            if (isCorrectType(this.policyMap, subject, scopeType)) {
               Object concreteSubject = subject.getSubject();
               if (concreteSubject != null && clazz.isInstance(concreteSubject)) {
                  if (null == wsdlName) {
                     this.writePolicyOrReferenceIt(subject, xmlWriter);
                  } else {
                     try {
                        Method getNameMethod = clazz.getDeclaredMethod("getName");
                        if (this.stringEqualsToStringOrQName(wsdlName, getNameMethod.invoke(concreteSubject))) {
                           this.writePolicyOrReferenceIt(subject, xmlWriter);
                        }
                     } catch (NoSuchMethodException var9) {
                        throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(clazz.getName(), wsdlName), var9));
                     } catch (IllegalAccessException var10) {
                        throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(clazz.getName(), wsdlName), var10));
                     } catch (InvocationTargetException var11) {
                        throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(clazz.getName(), wsdlName), var11));
                     }
                  }
               }
            }
         }
      }

      LOGGER.exiting();
   }

   private static boolean isCorrectType(PolicyMap map, PolicySubject subject, PolicyWSDLGeneratorExtension.ScopeType type) {
      switch(type) {
      case OPERATION:
         return !map.isInputMessageSubject(subject) && !map.isOutputMessageSubject(subject) && !map.isFaultMessageSubject(subject);
      case INPUT_MESSAGE:
         return map.isInputMessageSubject(subject);
      case OUTPUT_MESSAGE:
         return map.isOutputMessageSubject(subject);
      case FAULT_MESSAGE:
         return map.isFaultMessageSubject(subject);
      default:
         return true;
      }
   }

   private boolean stringEqualsToStringOrQName(String first, Object second) {
      return second instanceof QName ? first.equals(((QName)second).getLocalPart()) : first.equals(second);
   }

   private void writePolicyOrReferenceIt(PolicySubject subject, TypedXmlWriter writer) {
      Policy policy;
      try {
         policy = subject.getEffectivePolicy(this.merger);
      } catch (PolicyException var7) {
         throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT(subject.toString()), var7));
      }

      if (policy != null) {
         if (null == policy.getIdOrName()) {
            PolicyModelGenerator generator = ModelGenerator.getGenerator();

            try {
               PolicySourceModel policyInfoset = generator.translate(policy);
               this.marshaller.marshal((PolicySourceModel)policyInfoset, writer);
            } catch (PolicyException var6) {
               throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1002_UNABLE_TO_MARSHALL_POLICY_OR_POLICY_REFERENCE(), var6));
            }
         } else {
            TypedXmlWriter policyReference = writer._element(policy.getNamespaceVersion().asQName(XmlToken.PolicyReference), TypedXmlWriter.class);
            policyReference._attribute((String)XmlToken.Uri.toString(), '#' + policy.getIdOrName());
         }
      }

   }

   private PolicyMapConfigurator[] loadConfigurators() {
      Collection<PolicyMapConfigurator> configurators = new LinkedList();
      configurators.add(new AddressingPolicyMapConfigurator());
      configurators.add(new MtomPolicyMapConfigurator());
      PolicyUtil.addServiceProviders(configurators, PolicyMapConfigurator.class);
      return (PolicyMapConfigurator[])configurators.toArray(new PolicyMapConfigurator[configurators.size()]);
   }

   static enum ScopeType {
      SERVICE,
      ENDPOINT,
      OPERATION,
      INPUT_MESSAGE,
      OUTPUT_MESSAGE,
      FAULT_MESSAGE;
   }
}
