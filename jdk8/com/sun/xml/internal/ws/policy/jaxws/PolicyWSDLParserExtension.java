package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModelContext;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;

public final class PolicyWSDLParserExtension extends WSDLParserExtension {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyWSDLParserExtension.class);
   private static final StringBuffer AnonymnousPolicyIdPrefix = new StringBuffer("#__anonymousPolicy__ID");
   private int anonymousPoliciesCount;
   private final SafePolicyReader policyReader = new SafePolicyReader();
   private SafePolicyReader.PolicyRecord expandQueueHead = null;
   private Map<String, SafePolicyReader.PolicyRecord> policyRecordsPassedBy = null;
   private Map<String, PolicySourceModel> anonymousPolicyModels = null;
   private List<String> unresolvedUris = null;
   private final LinkedList<String> urisNeeded = new LinkedList();
   private final Map<String, PolicySourceModel> modelsNeeded = new HashMap();
   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> handlers4ServiceMap = null;
   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> handlers4PortMap = null;
   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> handlers4PortTypeMap = null;
   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> handlers4BindingMap = null;
   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> handlers4BoundOperationMap = null;
   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> handlers4OperationMap = null;
   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> handlers4MessageMap = null;
   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> handlers4InputMap = null;
   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> handlers4OutputMap = null;
   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> handlers4FaultMap = null;
   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> handlers4BindingInputOpMap = null;
   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> handlers4BindingOutputOpMap = null;
   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> handlers4BindingFaultOpMap = null;
   private PolicyMapBuilder policyBuilder = new PolicyMapBuilder();

   private boolean isPolicyProcessed(String policyUri) {
      return this.modelsNeeded.containsKey(policyUri);
   }

   private void addNewPolicyNeeded(String policyUri, PolicySourceModel policyModel) {
      if (!this.modelsNeeded.containsKey(policyUri)) {
         this.modelsNeeded.put(policyUri, policyModel);
         this.urisNeeded.addFirst(policyUri);
      }

   }

   private Map<String, PolicySourceModel> getPolicyModels() {
      return this.modelsNeeded;
   }

   private Map<String, SafePolicyReader.PolicyRecord> getPolicyRecordsPassedBy() {
      if (null == this.policyRecordsPassedBy) {
         this.policyRecordsPassedBy = new HashMap();
      }

      return this.policyRecordsPassedBy;
   }

   private Map<String, PolicySourceModel> getAnonymousPolicyModels() {
      if (null == this.anonymousPolicyModels) {
         this.anonymousPolicyModels = new HashMap();
      }

      return this.anonymousPolicyModels;
   }

   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> getHandlers4ServiceMap() {
      if (null == this.handlers4ServiceMap) {
         this.handlers4ServiceMap = new HashMap();
      }

      return this.handlers4ServiceMap;
   }

   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> getHandlers4PortMap() {
      if (null == this.handlers4PortMap) {
         this.handlers4PortMap = new HashMap();
      }

      return this.handlers4PortMap;
   }

   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> getHandlers4PortTypeMap() {
      if (null == this.handlers4PortTypeMap) {
         this.handlers4PortTypeMap = new HashMap();
      }

      return this.handlers4PortTypeMap;
   }

   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> getHandlers4BindingMap() {
      if (null == this.handlers4BindingMap) {
         this.handlers4BindingMap = new HashMap();
      }

      return this.handlers4BindingMap;
   }

   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> getHandlers4OperationMap() {
      if (null == this.handlers4OperationMap) {
         this.handlers4OperationMap = new HashMap();
      }

      return this.handlers4OperationMap;
   }

   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> getHandlers4BoundOperationMap() {
      if (null == this.handlers4BoundOperationMap) {
         this.handlers4BoundOperationMap = new HashMap();
      }

      return this.handlers4BoundOperationMap;
   }

   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> getHandlers4MessageMap() {
      if (null == this.handlers4MessageMap) {
         this.handlers4MessageMap = new HashMap();
      }

      return this.handlers4MessageMap;
   }

   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> getHandlers4InputMap() {
      if (null == this.handlers4InputMap) {
         this.handlers4InputMap = new HashMap();
      }

      return this.handlers4InputMap;
   }

   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> getHandlers4OutputMap() {
      if (null == this.handlers4OutputMap) {
         this.handlers4OutputMap = new HashMap();
      }

      return this.handlers4OutputMap;
   }

   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> getHandlers4FaultMap() {
      if (null == this.handlers4FaultMap) {
         this.handlers4FaultMap = new HashMap();
      }

      return this.handlers4FaultMap;
   }

   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> getHandlers4BindingInputOpMap() {
      if (null == this.handlers4BindingInputOpMap) {
         this.handlers4BindingInputOpMap = new HashMap();
      }

      return this.handlers4BindingInputOpMap;
   }

   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> getHandlers4BindingOutputOpMap() {
      if (null == this.handlers4BindingOutputOpMap) {
         this.handlers4BindingOutputOpMap = new HashMap();
      }

      return this.handlers4BindingOutputOpMap;
   }

   private Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> getHandlers4BindingFaultOpMap() {
      if (null == this.handlers4BindingFaultOpMap) {
         this.handlers4BindingFaultOpMap = new HashMap();
      }

      return this.handlers4BindingFaultOpMap;
   }

   private List<String> getUnresolvedUris(boolean emptyListNeeded) {
      if (null == this.unresolvedUris || emptyListNeeded) {
         this.unresolvedUris = new LinkedList();
      }

      return this.unresolvedUris;
   }

   private void policyRecToExpandQueue(SafePolicyReader.PolicyRecord policyRec) {
      if (null == this.expandQueueHead) {
         this.expandQueueHead = policyRec;
      } else {
         this.expandQueueHead = this.expandQueueHead.insert(policyRec);
      }

   }

   private PolicyWSDLParserExtension.PolicyRecordHandler readSinglePolicy(SafePolicyReader.PolicyRecord policyRec, boolean inner) {
      PolicyWSDLParserExtension.PolicyRecordHandler handler = null;
      String policyId = policyRec.policyModel.getPolicyId();
      if (policyId == null) {
         policyId = policyRec.policyModel.getPolicyName();
      }

      if (policyId != null) {
         handler = new PolicyWSDLParserExtension.PolicyRecordHandler(PolicyWSDLParserExtension.HandlerType.PolicyUri, policyRec.getUri());
         this.getPolicyRecordsPassedBy().put(policyRec.getUri(), policyRec);
         this.policyRecToExpandQueue(policyRec);
      } else if (inner) {
         String anonymousId = AnonymnousPolicyIdPrefix.append(this.anonymousPoliciesCount++).toString();
         handler = new PolicyWSDLParserExtension.PolicyRecordHandler(PolicyWSDLParserExtension.HandlerType.AnonymousPolicyId, anonymousId);
         this.getAnonymousPolicyModels().put(anonymousId, policyRec.policyModel);
         if (null != policyRec.unresolvedURIs) {
            this.getUnresolvedUris(false).addAll(policyRec.unresolvedURIs);
         }
      }

      return handler;
   }

   private void addHandlerToMap(Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> map, WSDLObject key, PolicyWSDLParserExtension.PolicyRecordHandler handler) {
      if (map.containsKey(key)) {
         ((Collection)map.get(key)).add(handler);
      } else {
         Collection<PolicyWSDLParserExtension.PolicyRecordHandler> newSet = new LinkedList();
         newSet.add(handler);
         map.put(key, newSet);
      }

   }

   private String getBaseUrl(String policyUri) {
      if (null == policyUri) {
         return null;
      } else {
         int fragmentIdx = policyUri.indexOf(35);
         return fragmentIdx == -1 ? policyUri : policyUri.substring(0, fragmentIdx);
      }
   }

   private void processReferenceUri(String policyUri, WSDLObject element, XMLStreamReader reader, Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> map) {
      if (null != policyUri && policyUri.length() != 0) {
         if ('#' != policyUri.charAt(0)) {
            this.getUnresolvedUris(false).add(policyUri);
         }

         this.addHandlerToMap(map, element, new PolicyWSDLParserExtension.PolicyRecordHandler(PolicyWSDLParserExtension.HandlerType.PolicyUri, SafePolicyReader.relativeToAbsoluteUrl(policyUri, reader.getLocation().getSystemId())));
      }
   }

   private boolean processSubelement(WSDLObject element, XMLStreamReader reader, Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> map) {
      if (NamespaceVersion.resolveAsToken(reader.getName()) == XmlToken.PolicyReference) {
         this.processReferenceUri(this.policyReader.readPolicyReferenceElement(reader), element, reader, map);
         return true;
      } else if (NamespaceVersion.resolveAsToken(reader.getName()) == XmlToken.Policy) {
         PolicyWSDLParserExtension.PolicyRecordHandler handler = this.readSinglePolicy(this.policyReader.readPolicyElement(reader, null == reader.getLocation().getSystemId() ? "" : reader.getLocation().getSystemId()), true);
         if (null != handler) {
            this.addHandlerToMap(map, element, handler);
         }

         return true;
      } else {
         return false;
      }
   }

   private void processAttributes(WSDLObject element, XMLStreamReader reader, Map<WSDLObject, Collection<PolicyWSDLParserExtension.PolicyRecordHandler>> map) {
      String[] uriArray = this.getPolicyURIsFromAttr(reader);
      if (null != uriArray) {
         String[] var5 = uriArray;
         int var6 = uriArray.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String policyUri = var5[var7];
            this.processReferenceUri(policyUri, element, reader, map);
         }
      }

   }

   public boolean portElements(EditableWSDLPort port, XMLStreamReader reader) {
      LOGGER.entering();
      boolean result = this.processSubelement(port, reader, this.getHandlers4PortMap());
      LOGGER.exiting();
      return result;
   }

   public void portAttributes(EditableWSDLPort port, XMLStreamReader reader) {
      LOGGER.entering();
      this.processAttributes(port, reader, this.getHandlers4PortMap());
      LOGGER.exiting();
   }

   public boolean serviceElements(EditableWSDLService service, XMLStreamReader reader) {
      LOGGER.entering();
      boolean result = this.processSubelement(service, reader, this.getHandlers4ServiceMap());
      LOGGER.exiting();
      return result;
   }

   public void serviceAttributes(EditableWSDLService service, XMLStreamReader reader) {
      LOGGER.entering();
      this.processAttributes(service, reader, this.getHandlers4ServiceMap());
      LOGGER.exiting();
   }

   public boolean definitionsElements(XMLStreamReader reader) {
      LOGGER.entering();
      if (NamespaceVersion.resolveAsToken(reader.getName()) == XmlToken.Policy) {
         this.readSinglePolicy(this.policyReader.readPolicyElement(reader, null == reader.getLocation().getSystemId() ? "" : reader.getLocation().getSystemId()), false);
         LOGGER.exiting();
         return true;
      } else {
         LOGGER.exiting();
         return false;
      }
   }

   public boolean bindingElements(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
      LOGGER.entering();
      boolean result = this.processSubelement(binding, reader, this.getHandlers4BindingMap());
      LOGGER.exiting();
      return result;
   }

   public void bindingAttributes(EditableWSDLBoundPortType binding, XMLStreamReader reader) {
      LOGGER.entering();
      this.processAttributes(binding, reader, this.getHandlers4BindingMap());
      LOGGER.exiting();
   }

   public boolean portTypeElements(EditableWSDLPortType portType, XMLStreamReader reader) {
      LOGGER.entering();
      boolean result = this.processSubelement(portType, reader, this.getHandlers4PortTypeMap());
      LOGGER.exiting();
      return result;
   }

   public void portTypeAttributes(EditableWSDLPortType portType, XMLStreamReader reader) {
      LOGGER.entering();
      this.processAttributes(portType, reader, this.getHandlers4PortTypeMap());
      LOGGER.exiting();
   }

   public boolean portTypeOperationElements(EditableWSDLOperation operation, XMLStreamReader reader) {
      LOGGER.entering();
      boolean result = this.processSubelement(operation, reader, this.getHandlers4OperationMap());
      LOGGER.exiting();
      return result;
   }

   public void portTypeOperationAttributes(EditableWSDLOperation operation, XMLStreamReader reader) {
      LOGGER.entering();
      this.processAttributes(operation, reader, this.getHandlers4OperationMap());
      LOGGER.exiting();
   }

   public boolean bindingOperationElements(EditableWSDLBoundOperation boundOperation, XMLStreamReader reader) {
      LOGGER.entering();
      boolean result = this.processSubelement(boundOperation, reader, this.getHandlers4BoundOperationMap());
      LOGGER.exiting();
      return result;
   }

   public void bindingOperationAttributes(EditableWSDLBoundOperation boundOperation, XMLStreamReader reader) {
      LOGGER.entering();
      this.processAttributes(boundOperation, reader, this.getHandlers4BoundOperationMap());
      LOGGER.exiting();
   }

   public boolean messageElements(EditableWSDLMessage msg, XMLStreamReader reader) {
      LOGGER.entering();
      boolean result = this.processSubelement(msg, reader, this.getHandlers4MessageMap());
      LOGGER.exiting();
      return result;
   }

   public void messageAttributes(EditableWSDLMessage msg, XMLStreamReader reader) {
      LOGGER.entering();
      this.processAttributes(msg, reader, this.getHandlers4MessageMap());
      LOGGER.exiting();
   }

   public boolean portTypeOperationInputElements(EditableWSDLInput input, XMLStreamReader reader) {
      LOGGER.entering();
      boolean result = this.processSubelement(input, reader, this.getHandlers4InputMap());
      LOGGER.exiting();
      return result;
   }

   public void portTypeOperationInputAttributes(EditableWSDLInput input, XMLStreamReader reader) {
      LOGGER.entering();
      this.processAttributes(input, reader, this.getHandlers4InputMap());
      LOGGER.exiting();
   }

   public boolean portTypeOperationOutputElements(EditableWSDLOutput output, XMLStreamReader reader) {
      LOGGER.entering();
      boolean result = this.processSubelement(output, reader, this.getHandlers4OutputMap());
      LOGGER.exiting();
      return result;
   }

   public void portTypeOperationOutputAttributes(EditableWSDLOutput output, XMLStreamReader reader) {
      LOGGER.entering();
      this.processAttributes(output, reader, this.getHandlers4OutputMap());
      LOGGER.exiting();
   }

   public boolean portTypeOperationFaultElements(EditableWSDLFault fault, XMLStreamReader reader) {
      LOGGER.entering();
      boolean result = this.processSubelement(fault, reader, this.getHandlers4FaultMap());
      LOGGER.exiting();
      return result;
   }

   public void portTypeOperationFaultAttributes(EditableWSDLFault fault, XMLStreamReader reader) {
      LOGGER.entering();
      this.processAttributes(fault, reader, this.getHandlers4FaultMap());
      LOGGER.exiting();
   }

   public boolean bindingOperationInputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      LOGGER.entering();
      boolean result = this.processSubelement(operation, reader, this.getHandlers4BindingInputOpMap());
      LOGGER.exiting();
      return result;
   }

   public void bindingOperationInputAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      LOGGER.entering();
      this.processAttributes(operation, reader, this.getHandlers4BindingInputOpMap());
      LOGGER.exiting();
   }

   public boolean bindingOperationOutputElements(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      LOGGER.entering();
      boolean result = this.processSubelement(operation, reader, this.getHandlers4BindingOutputOpMap());
      LOGGER.exiting();
      return result;
   }

   public void bindingOperationOutputAttributes(EditableWSDLBoundOperation operation, XMLStreamReader reader) {
      LOGGER.entering();
      this.processAttributes(operation, reader, this.getHandlers4BindingOutputOpMap());
      LOGGER.exiting();
   }

   public boolean bindingOperationFaultElements(EditableWSDLBoundFault fault, XMLStreamReader reader) {
      LOGGER.entering();
      boolean result = this.processSubelement(fault, reader, this.getHandlers4BindingFaultOpMap());
      LOGGER.exiting(result);
      return result;
   }

   public void bindingOperationFaultAttributes(EditableWSDLBoundFault fault, XMLStreamReader reader) {
      LOGGER.entering();
      this.processAttributes(fault, reader, this.getHandlers4BindingFaultOpMap());
      LOGGER.exiting();
   }

   private PolicyMapBuilder getPolicyMapBuilder() {
      if (null == this.policyBuilder) {
         this.policyBuilder = new PolicyMapBuilder();
      }

      return this.policyBuilder;
   }

   private Collection<String> getPolicyURIs(Collection<PolicyWSDLParserExtension.PolicyRecordHandler> handlers, PolicySourceModelContext modelContext) throws PolicyException {
      Collection<String> result = new ArrayList(handlers.size());

      String policyUri;
      for(Iterator var5 = handlers.iterator(); var5.hasNext(); result.add(policyUri)) {
         PolicyWSDLParserExtension.PolicyRecordHandler handler = (PolicyWSDLParserExtension.PolicyRecordHandler)var5.next();
         policyUri = handler.handler;
         if (PolicyWSDLParserExtension.HandlerType.AnonymousPolicyId == handler.type) {
            PolicySourceModel policyModel = (PolicySourceModel)this.getAnonymousPolicyModels().get(policyUri);
            policyModel.expand(modelContext);

            while(this.getPolicyModels().containsKey(policyUri)) {
               policyUri = AnonymnousPolicyIdPrefix.append(this.anonymousPoliciesCount++).toString();
            }

            this.getPolicyModels().put(policyUri, policyModel);
         }
      }

      return result;
   }

   private boolean readExternalFile(String fileUrl) {
      InputStream ios = null;
      XMLStreamReader reader = null;

      boolean var5;
      try {
         URL xmlURL = new URL(fileUrl);
         ios = xmlURL.openStream();

         for(reader = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(ios); reader.hasNext(); reader.next()) {
            if (reader.isStartElement() && NamespaceVersion.resolveAsToken(reader.getName()) == XmlToken.Policy) {
               this.readSinglePolicy(this.policyReader.readPolicyElement(reader, fileUrl), false);
            }
         }

         var5 = true;
         return var5;
      } catch (IOException var10) {
         var5 = false;
      } catch (XMLStreamException var11) {
         var5 = false;
         return var5;
      } finally {
         PolicyUtils.IO.closeResource(reader);
         PolicyUtils.IO.closeResource((Closeable)ios);
      }

      return var5;
   }

   public void finished(WSDLParserExtensionContext context) {
      LOGGER.entering(new Object[]{context});
      List externalUris;
      if (null != this.expandQueueHead) {
         externalUris = this.getUnresolvedUris(false);
         this.getUnresolvedUris(true);
         LinkedList<String> baseUnresolvedUris = new LinkedList();

         for(SafePolicyReader.PolicyRecord currentRec = this.expandQueueHead; null != currentRec; currentRec = currentRec.next) {
            baseUnresolvedUris.addFirst(currentRec.getUri());
         }

         this.getUnresolvedUris(false).addAll(baseUnresolvedUris);
         this.expandQueueHead = null;
         this.getUnresolvedUris(false).addAll(externalUris);
      }

      Iterator var23;
      String currentUri;
      while(!this.getUnresolvedUris(false).isEmpty()) {
         externalUris = this.getUnresolvedUris(false);
         this.getUnresolvedUris(true);
         var23 = externalUris.iterator();

         while(var23.hasNext()) {
            currentUri = (String)var23.next();
            if (!this.isPolicyProcessed(currentUri)) {
               SafePolicyReader.PolicyRecord prefetchedRecord = (SafePolicyReader.PolicyRecord)this.getPolicyRecordsPassedBy().get(currentUri);
               if (null == prefetchedRecord) {
                  if (this.policyReader.getUrlsRead().contains(this.getBaseUrl(currentUri))) {
                     LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1014_CAN_NOT_FIND_POLICY(currentUri)));
                  } else if (this.readExternalFile(this.getBaseUrl(currentUri))) {
                     this.getUnresolvedUris(false).add(currentUri);
                  }
               } else {
                  if (null != prefetchedRecord.unresolvedURIs) {
                     this.getUnresolvedUris(false).addAll(prefetchedRecord.unresolvedURIs);
                  }

                  this.addNewPolicyNeeded(currentUri, prefetchedRecord.policyModel);
               }
            }
         }
      }

      PolicySourceModelContext modelContext = PolicySourceModelContext.createContext();
      var23 = this.urisNeeded.iterator();

      while(var23.hasNext()) {
         currentUri = (String)var23.next();
         PolicySourceModel sourceModel = (PolicySourceModel)this.modelsNeeded.get(currentUri);

         try {
            sourceModel.expand(modelContext);
            modelContext.addModel(new URI(currentUri), sourceModel);
         } catch (URISyntaxException var19) {
            LOGGER.logSevereException(var19);
         } catch (PolicyException var20) {
            LOGGER.logSevereException(var20);
         }
      }

      try {
         HashSet<BuilderHandlerMessageScope> messageSet = new HashSet();
         Iterator var27 = context.getWSDLModel().getServices().values().iterator();

         label165:
         while(var27.hasNext()) {
            EditableWSDLService service = (EditableWSDLService)var27.next();
            if (this.getHandlers4ServiceMap().containsKey(service)) {
               this.getPolicyMapBuilder().registerHandler(new BuilderHandlerServiceScope(this.getPolicyURIs((Collection)this.getHandlers4ServiceMap().get(service), modelContext), this.getPolicyModels(), service, service.getName()));
            }

            Iterator var6 = service.getPorts().iterator();

            while(true) {
               EditableWSDLPort port;
               do {
                  if (!var6.hasNext()) {
                     continue label165;
                  }

                  port = (EditableWSDLPort)var6.next();
                  if (this.getHandlers4PortMap().containsKey(port)) {
                     this.getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(this.getPolicyURIs((Collection)this.getHandlers4PortMap().get(port), modelContext), this.getPolicyModels(), port, port.getOwner().getName(), port.getName()));
                  }
               } while(null == port.getBinding());

               if (this.getHandlers4BindingMap().containsKey(port.getBinding())) {
                  this.getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(this.getPolicyURIs((Collection)this.getHandlers4BindingMap().get(port.getBinding()), modelContext), this.getPolicyModels(), port.getBinding(), service.getName(), port.getName()));
               }

               if (this.getHandlers4PortTypeMap().containsKey(port.getBinding().getPortType())) {
                  this.getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(this.getPolicyURIs((Collection)this.getHandlers4PortTypeMap().get(port.getBinding().getPortType()), modelContext), this.getPolicyModels(), port.getBinding().getPortType(), service.getName(), port.getName()));
               }

               Iterator var8 = port.getBinding().getBindingOperations().iterator();

               while(var8.hasNext()) {
                  EditableWSDLBoundOperation boundOperation = (EditableWSDLBoundOperation)var8.next();
                  EditableWSDLOperation operation = boundOperation.getOperation();
                  QName operationName = new QName(boundOperation.getBoundPortType().getName().getNamespaceURI(), boundOperation.getName().getLocalPart());
                  if (this.getHandlers4BoundOperationMap().containsKey(boundOperation)) {
                     this.getPolicyMapBuilder().registerHandler(new BuilderHandlerOperationScope(this.getPolicyURIs((Collection)this.getHandlers4BoundOperationMap().get(boundOperation), modelContext), this.getPolicyModels(), boundOperation, service.getName(), port.getName(), operationName));
                  }

                  if (this.getHandlers4OperationMap().containsKey(operation)) {
                     this.getPolicyMapBuilder().registerHandler(new BuilderHandlerOperationScope(this.getPolicyURIs((Collection)this.getHandlers4OperationMap().get(operation), modelContext), this.getPolicyModels(), operation, service.getName(), port.getName(), operationName));
                  }

                  EditableWSDLInput input = operation.getInput();
                  if (null != input) {
                     EditableWSDLMessage inputMsg = input.getMessage();
                     if (inputMsg != null && this.getHandlers4MessageMap().containsKey(inputMsg)) {
                        messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs((Collection)this.getHandlers4MessageMap().get(inputMsg), modelContext), this.getPolicyModels(), inputMsg, BuilderHandlerMessageScope.Scope.InputMessageScope, service.getName(), port.getName(), operationName, (QName)null));
                     }
                  }

                  if (this.getHandlers4BindingInputOpMap().containsKey(boundOperation)) {
                     this.getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(this.getPolicyURIs((Collection)this.getHandlers4BindingInputOpMap().get(boundOperation), modelContext), this.getPolicyModels(), boundOperation, BuilderHandlerMessageScope.Scope.InputMessageScope, service.getName(), port.getName(), operationName, (QName)null));
                  }

                  if (null != input && this.getHandlers4InputMap().containsKey(input)) {
                     this.getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(this.getPolicyURIs((Collection)this.getHandlers4InputMap().get(input), modelContext), this.getPolicyModels(), input, BuilderHandlerMessageScope.Scope.InputMessageScope, service.getName(), port.getName(), operationName, (QName)null));
                  }

                  EditableWSDLOutput output = operation.getOutput();
                  if (null != output) {
                     EditableWSDLMessage outputMsg = output.getMessage();
                     if (outputMsg != null && this.getHandlers4MessageMap().containsKey(outputMsg)) {
                        messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs((Collection)this.getHandlers4MessageMap().get(outputMsg), modelContext), this.getPolicyModels(), outputMsg, BuilderHandlerMessageScope.Scope.OutputMessageScope, service.getName(), port.getName(), operationName, (QName)null));
                     }
                  }

                  if (this.getHandlers4BindingOutputOpMap().containsKey(boundOperation)) {
                     this.getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(this.getPolicyURIs((Collection)this.getHandlers4BindingOutputOpMap().get(boundOperation), modelContext), this.getPolicyModels(), boundOperation, BuilderHandlerMessageScope.Scope.OutputMessageScope, service.getName(), port.getName(), operationName, (QName)null));
                  }

                  if (null != output && this.getHandlers4OutputMap().containsKey(output)) {
                     this.getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(this.getPolicyURIs((Collection)this.getHandlers4OutputMap().get(output), modelContext), this.getPolicyModels(), output, BuilderHandlerMessageScope.Scope.OutputMessageScope, service.getName(), port.getName(), operationName, (QName)null));
                  }

                  Iterator var31 = boundOperation.getFaults().iterator();

                  while(var31.hasNext()) {
                     EditableWSDLBoundFault boundFault = (EditableWSDLBoundFault)var31.next();
                     EditableWSDLFault fault = boundFault.getFault();
                     if (fault == null) {
                        LOGGER.warning(PolicyMessages.WSP_1021_FAULT_NOT_BOUND(boundFault.getName()));
                     } else {
                        EditableWSDLMessage faultMessage = fault.getMessage();
                        QName faultName = new QName(boundOperation.getBoundPortType().getName().getNamespaceURI(), boundFault.getName());
                        if (faultMessage != null && this.getHandlers4MessageMap().containsKey(faultMessage)) {
                           messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs((Collection)this.getHandlers4MessageMap().get(faultMessage), modelContext), this.getPolicyModels(), new WSDLBoundFaultContainer(boundFault, boundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, service.getName(), port.getName(), operationName, faultName));
                        }

                        if (this.getHandlers4FaultMap().containsKey(fault)) {
                           messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs((Collection)this.getHandlers4FaultMap().get(fault), modelContext), this.getPolicyModels(), new WSDLBoundFaultContainer(boundFault, boundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, service.getName(), port.getName(), operationName, faultName));
                        }

                        if (this.getHandlers4BindingFaultOpMap().containsKey(boundFault)) {
                           messageSet.add(new BuilderHandlerMessageScope(this.getPolicyURIs((Collection)this.getHandlers4BindingFaultOpMap().get(boundFault), modelContext), this.getPolicyModels(), new WSDLBoundFaultContainer(boundFault, boundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, service.getName(), port.getName(), operationName, faultName));
                        }
                     }
                  }
               }
            }
         }

         var27 = messageSet.iterator();

         while(var27.hasNext()) {
            BuilderHandlerMessageScope scopeHandler = (BuilderHandlerMessageScope)var27.next();
            this.getPolicyMapBuilder().registerHandler(scopeHandler);
         }
      } catch (PolicyException var21) {
         LOGGER.logSevereException(var21);
      }

      LOGGER.exiting();
   }

   public void postFinished(WSDLParserExtensionContext context) {
      EditableWSDLModel wsdlModel = context.getWSDLModel();

      PolicyMap effectiveMap;
      try {
         if (context.isClientSide()) {
            effectiveMap = context.getPolicyResolver().resolve(new PolicyResolver.ClientContext(this.policyBuilder.getPolicyMap(), context.getContainer()));
         } else {
            effectiveMap = context.getPolicyResolver().resolve(new PolicyResolver.ServerContext(this.policyBuilder.getPolicyMap(), context.getContainer(), (Class)null, new PolicyMapMutator[0]));
         }

         wsdlModel.setPolicyMap(effectiveMap);
      } catch (PolicyException var6) {
         LOGGER.logSevereException(var6);
         throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1007_POLICY_EXCEPTION_WHILE_FINISHING_PARSING_WSDL(), var6));
      }

      try {
         PolicyUtil.configureModel(wsdlModel, effectiveMap);
      } catch (PolicyException var5) {
         LOGGER.logSevereException(var5);
         throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1012_FAILED_CONFIGURE_WSDL_MODEL(), var5));
      }

      LOGGER.exiting();
   }

   private String[] getPolicyURIsFromAttr(XMLStreamReader reader) {
      StringBuilder policyUriBuffer = new StringBuilder();
      NamespaceVersion[] var3 = NamespaceVersion.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         NamespaceVersion version = var3[var5];
         String value = reader.getAttributeValue(version.toString(), XmlToken.PolicyUris.toString());
         if (value != null) {
            policyUriBuffer.append(value).append(" ");
         }
      }

      return policyUriBuffer.length() > 0 ? policyUriBuffer.toString().split("[\\n ]+") : null;
   }

   static final class PolicyRecordHandler {
      String handler;
      PolicyWSDLParserExtension.HandlerType type;

      PolicyRecordHandler(PolicyWSDLParserExtension.HandlerType type, String handler) {
         this.type = type;
         this.handler = handler;
      }

      PolicyWSDLParserExtension.HandlerType getType() {
         return this.type;
      }

      String getHandler() {
         return this.handler;
      }
   }

   static enum HandlerType {
      PolicyUri,
      AnonymousPolicyId;
   }
}
