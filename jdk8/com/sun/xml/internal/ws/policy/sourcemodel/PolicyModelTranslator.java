package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class PolicyModelTranslator {
   private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyModelTranslator.class);
   private static final PolicyAssertionCreator defaultCreator = new DefaultPolicyAssertionCreator();
   private final Map<String, PolicyAssertionCreator> assertionCreators;

   private PolicyModelTranslator() throws PolicyException {
      this((Collection)null);
   }

   protected PolicyModelTranslator(Collection<PolicyAssertionCreator> creators) throws PolicyException {
      LOGGER.entering(new Object[]{creators});
      Collection<PolicyAssertionCreator> allCreators = new LinkedList();
      PolicyAssertionCreator[] discoveredCreators = (PolicyAssertionCreator[])PolicyUtils.ServiceProvider.load(PolicyAssertionCreator.class);
      PolicyAssertionCreator[] var4 = discoveredCreators;
      int var5 = discoveredCreators.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         PolicyAssertionCreator creator = var4[var6];
         allCreators.add(creator);
      }

      if (creators != null) {
         Iterator var14 = creators.iterator();

         while(var14.hasNext()) {
            PolicyAssertionCreator creator = (PolicyAssertionCreator)var14.next();
            allCreators.add(creator);
         }
      }

      Map<String, PolicyAssertionCreator> pacMap = new HashMap();
      Iterator var17 = allCreators.iterator();

      while(true) {
         while(var17.hasNext()) {
            PolicyAssertionCreator creator = (PolicyAssertionCreator)var17.next();
            String[] supportedURIs = creator.getSupportedDomainNamespaceURIs();
            String creatorClassName = creator.getClass().getName();
            if (supportedURIs != null && supportedURIs.length != 0) {
               String[] var9 = supportedURIs;
               int var10 = supportedURIs.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  String supportedURI = var9[var11];
                  LOGGER.config(LocalizationMessages.WSP_0078_ASSERTION_CREATOR_DISCOVERED(creatorClassName, supportedURI));
                  if (supportedURI == null || supportedURI.length() == 0) {
                     throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0070_ERROR_REGISTERING_ASSERTION_CREATOR(creatorClassName)));
                  }

                  PolicyAssertionCreator oldCreator = (PolicyAssertionCreator)pacMap.put(supportedURI, creator);
                  if (oldCreator != null) {
                     throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0071_ERROR_MULTIPLE_ASSERTION_CREATORS_FOR_NAMESPACE(supportedURI, oldCreator.getClass().getName(), creator.getClass().getName())));
                  }
               }
            } else {
               LOGGER.warning(LocalizationMessages.WSP_0077_ASSERTION_CREATOR_DOES_NOT_SUPPORT_ANY_URI(creatorClassName));
            }
         }

         this.assertionCreators = Collections.unmodifiableMap(pacMap);
         LOGGER.exiting();
         return;
      }
   }

   public static PolicyModelTranslator getTranslator() throws PolicyException {
      return new PolicyModelTranslator();
   }

   public Policy translate(PolicySourceModel model) throws PolicyException {
      LOGGER.entering(new Object[]{model});
      if (model == null) {
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0043_POLICY_MODEL_TRANSLATION_ERROR_INPUT_PARAM_NULL()));
      } else {
         PolicySourceModel localPolicyModelCopy;
         try {
            localPolicyModelCopy = model.clone();
         } catch (CloneNotSupportedException var7) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0016_UNABLE_TO_CLONE_POLICY_SOURCE_MODEL(), var7));
         }

         String policyId = localPolicyModelCopy.getPolicyId();
         String policyName = localPolicyModelCopy.getPolicyName();
         Collection<AssertionSet> alternatives = this.createPolicyAlternatives(localPolicyModelCopy);
         LOGGER.finest(LocalizationMessages.WSP_0052_NUMBER_OF_ALTERNATIVE_COMBINATIONS_CREATED(alternatives.size()));
         Policy policy = null;
         if (alternatives.size() == 0) {
            policy = Policy.createNullPolicy(model.getNamespaceVersion(), policyName, policyId);
            LOGGER.finest(LocalizationMessages.WSP_0055_NO_ALTERNATIVE_COMBINATIONS_CREATED());
         } else if (alternatives.size() == 1 && ((AssertionSet)alternatives.iterator().next()).isEmpty()) {
            policy = Policy.createEmptyPolicy(model.getNamespaceVersion(), policyName, policyId);
            LOGGER.finest(LocalizationMessages.WSP_0026_SINGLE_EMPTY_ALTERNATIVE_COMBINATION_CREATED());
         } else {
            policy = Policy.createPolicy(model.getNamespaceVersion(), policyName, policyId, alternatives);
            LOGGER.finest(LocalizationMessages.WSP_0057_N_ALTERNATIVE_COMBINATIONS_M_POLICY_ALTERNATIVES_CREATED(alternatives.size(), policy.getNumberOfAssertionSets()));
         }

         LOGGER.exiting(policy);
         return policy;
      }
   }

   private Collection<AssertionSet> createPolicyAlternatives(PolicySourceModel model) throws PolicyException {
      PolicyModelTranslator.ContentDecomposition decomposition = new PolicyModelTranslator.ContentDecomposition();
      Queue<PolicyModelTranslator.RawPolicy> policyQueue = new LinkedList();
      Queue<Collection<ModelNode>> contentQueue = new LinkedList();
      PolicyModelTranslator.RawPolicy rootPolicy = new PolicyModelTranslator.RawPolicy(model.getRootNode(), new LinkedList());
      PolicyModelTranslator.RawPolicy processedPolicy = rootPolicy;

      do {
         Collection processedContent = processedPolicy.originalContent;

         do {
            this.decompose(processedContent, decomposition);
            if (decomposition.exactlyOneContents.isEmpty()) {
               PolicyModelTranslator.RawAlternative alternative = new PolicyModelTranslator.RawAlternative(decomposition.assertions);
               processedPolicy.alternatives.add(alternative);
               if (!alternative.allNestedPolicies.isEmpty()) {
                  policyQueue.addAll(alternative.allNestedPolicies);
               }
            } else {
               Collection<Collection<ModelNode>> combinations = PolicyUtils.Collections.combine(decomposition.assertions, decomposition.exactlyOneContents, false);
               if (combinations != null && !combinations.isEmpty()) {
                  contentQueue.addAll(combinations);
               }
            }
         } while((processedContent = (Collection)contentQueue.poll()) != null);
      } while((processedPolicy = (PolicyModelTranslator.RawPolicy)policyQueue.poll()) != null);

      Collection<AssertionSet> assertionSets = new LinkedList();
      Iterator var13 = rootPolicy.alternatives.iterator();

      while(var13.hasNext()) {
         PolicyModelTranslator.RawAlternative rootAlternative = (PolicyModelTranslator.RawAlternative)var13.next();
         Collection<AssertionSet> normalizedAlternatives = this.normalizeRawAlternative(rootAlternative);
         assertionSets.addAll(normalizedAlternatives);
      }

      return assertionSets;
   }

   private void decompose(Collection<ModelNode> content, PolicyModelTranslator.ContentDecomposition decomposition) throws PolicyException {
      decomposition.reset();
      LinkedList allContentQueue = new LinkedList(content);

      ModelNode node;
      while((node = (ModelNode)allContentQueue.poll()) != null) {
         switch(node.getType()) {
         case POLICY:
         case ALL:
            allContentQueue.addAll(node.getChildren());
            break;
         case POLICY_REFERENCE:
            allContentQueue.addAll(getReferencedModelRootNode(node).getChildren());
            break;
         case EXACTLY_ONE:
            decomposition.exactlyOneContents.add(this.expandsExactlyOneContent(node.getChildren()));
            break;
         case ASSERTION:
            decomposition.assertions.add(node);
            break;
         default:
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0007_UNEXPECTED_MODEL_NODE_TYPE_FOUND(node.getType())));
         }
      }

   }

   private static ModelNode getReferencedModelRootNode(ModelNode policyReferenceNode) throws PolicyException {
      PolicySourceModel referencedModel = policyReferenceNode.getReferencedModel();
      if (referencedModel == null) {
         PolicyReferenceData refData = policyReferenceNode.getPolicyReferenceData();
         if (refData == null) {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0041_POLICY_REFERENCE_NODE_FOUND_WITH_NO_POLICY_REFERENCE_IN_IT()));
         } else {
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0010_UNEXPANDED_POLICY_REFERENCE_NODE_FOUND_REFERENCING(refData.getReferencedModelUri())));
         }
      } else {
         return referencedModel.getRootNode();
      }
   }

   private Collection<ModelNode> expandsExactlyOneContent(Collection<ModelNode> content) throws PolicyException {
      Collection<ModelNode> result = new LinkedList();
      LinkedList eoContentQueue = new LinkedList(content);

      ModelNode node;
      while((node = (ModelNode)eoContentQueue.poll()) != null) {
         switch(node.getType()) {
         case POLICY:
         case ALL:
         case ASSERTION:
            result.add(node);
            break;
         case POLICY_REFERENCE:
            result.add(getReferencedModelRootNode(node));
            break;
         case EXACTLY_ONE:
            eoContentQueue.addAll(node.getChildren());
            break;
         default:
            throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0001_UNSUPPORTED_MODEL_NODE_TYPE(node.getType())));
         }
      }

      return result;
   }

   private List<AssertionSet> normalizeRawAlternative(PolicyModelTranslator.RawAlternative alternative) throws AssertionCreationException, PolicyException {
      List<PolicyAssertion> normalizedContentBase = new LinkedList();
      Collection<List<PolicyAssertion>> normalizedContentOptions = new LinkedList();
      LinkedList options;
      if (!alternative.nestedAssertions.isEmpty()) {
         options = new LinkedList(alternative.nestedAssertions);

         PolicyModelTranslator.RawAssertion rawAssertion;
         while((rawAssertion = (PolicyModelTranslator.RawAssertion)options.poll()) != null) {
            List<PolicyAssertion> normalized = this.normalizeRawAssertion(rawAssertion);
            if (normalized.size() == 1) {
               normalizedContentBase.addAll(normalized);
            } else {
               normalizedContentOptions.add(normalized);
            }
         }
      }

      options = new LinkedList();
      if (normalizedContentOptions.isEmpty()) {
         options.add(AssertionSet.createAssertionSet(normalizedContentBase));
      } else {
         Collection<Collection<PolicyAssertion>> contentCombinations = PolicyUtils.Collections.combine(normalizedContentBase, normalizedContentOptions, true);
         Iterator var9 = contentCombinations.iterator();

         while(var9.hasNext()) {
            Collection<PolicyAssertion> contentOption = (Collection)var9.next();
            options.add(AssertionSet.createAssertionSet(contentOption));
         }
      }

      return options;
   }

   private List<PolicyAssertion> normalizeRawAssertion(PolicyModelTranslator.RawAssertion assertion) throws AssertionCreationException, PolicyException {
      ArrayList parameters;
      if (assertion.parameters.isEmpty()) {
         parameters = null;
      } else {
         parameters = new ArrayList(assertion.parameters.size());
         Iterator var3 = assertion.parameters.iterator();

         while(var3.hasNext()) {
            ModelNode parameterNode = (ModelNode)var3.next();
            parameters.add(this.createPolicyAssertionParameter(parameterNode));
         }
      }

      List<AssertionSet> nestedAlternatives = new LinkedList();
      LinkedList assertionOptions;
      if (assertion.nestedAlternatives != null && !assertion.nestedAlternatives.isEmpty()) {
         assertionOptions = new LinkedList(assertion.nestedAlternatives);

         PolicyModelTranslator.RawAlternative rawAlternative;
         while((rawAlternative = (PolicyModelTranslator.RawAlternative)assertionOptions.poll()) != null) {
            nestedAlternatives.addAll(this.normalizeRawAlternative(rawAlternative));
         }
      }

      assertionOptions = new LinkedList();
      boolean nestedAlternativesAvailable = !nestedAlternatives.isEmpty();
      if (nestedAlternativesAvailable) {
         Iterator var6 = nestedAlternatives.iterator();

         while(var6.hasNext()) {
            AssertionSet nestedAlternative = (AssertionSet)var6.next();
            assertionOptions.add(this.createPolicyAssertion(assertion.originalNode.getNodeData(), parameters, nestedAlternative));
         }
      } else {
         assertionOptions.add(this.createPolicyAssertion(assertion.originalNode.getNodeData(), parameters, (AssertionSet)null));
      }

      return assertionOptions;
   }

   private PolicyAssertion createPolicyAssertionParameter(ModelNode parameterNode) throws AssertionCreationException, PolicyException {
      if (parameterNode.getType() != ModelNode.Type.ASSERTION_PARAMETER_NODE) {
         throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0065_INCONSISTENCY_IN_POLICY_SOURCE_MODEL(parameterNode.getType())));
      } else {
         List<PolicyAssertion> childParameters = null;
         if (parameterNode.hasChildren()) {
            childParameters = new ArrayList(parameterNode.childrenSize());
            Iterator var3 = parameterNode.iterator();

            while(var3.hasNext()) {
               ModelNode childParameterNode = (ModelNode)var3.next();
               childParameters.add(this.createPolicyAssertionParameter(childParameterNode));
            }
         }

         return this.createPolicyAssertion(parameterNode.getNodeData(), childParameters, (AssertionSet)null);
      }
   }

   private PolicyAssertion createPolicyAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters, AssertionSet nestedAlternative) throws AssertionCreationException {
      String assertionNamespace = data.getName().getNamespaceURI();
      PolicyAssertionCreator domainSpecificPAC = (PolicyAssertionCreator)this.assertionCreators.get(assertionNamespace);
      return domainSpecificPAC == null ? defaultCreator.createAssertion(data, assertionParameters, nestedAlternative, (PolicyAssertionCreator)null) : domainSpecificPAC.createAssertion(data, assertionParameters, nestedAlternative, defaultCreator);
   }

   private static final class RawPolicy {
      final Collection<ModelNode> originalContent;
      final Collection<PolicyModelTranslator.RawAlternative> alternatives;

      RawPolicy(ModelNode policyNode, Collection<PolicyModelTranslator.RawAlternative> alternatives) {
         this.originalContent = policyNode.getChildren();
         this.alternatives = alternatives;
      }
   }

   private static final class RawAlternative {
      private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyModelTranslator.RawAlternative.class);
      final List<PolicyModelTranslator.RawPolicy> allNestedPolicies = new LinkedList();
      final Collection<PolicyModelTranslator.RawAssertion> nestedAssertions = new LinkedList();

      RawAlternative(Collection<ModelNode> assertionNodes) throws PolicyException {
         Iterator var2 = assertionNodes.iterator();

         while(var2.hasNext()) {
            ModelNode node = (ModelNode)var2.next();
            PolicyModelTranslator.RawAssertion assertion = new PolicyModelTranslator.RawAssertion(node, new LinkedList());
            this.nestedAssertions.add(assertion);
            Iterator var5 = assertion.originalNode.getChildren().iterator();

            while(var5.hasNext()) {
               ModelNode assertionNodeChild = (ModelNode)var5.next();
               switch(assertionNodeChild.getType()) {
               case ASSERTION_PARAMETER_NODE:
                  assertion.parameters.add(assertionNodeChild);
                  break;
               case POLICY:
               case POLICY_REFERENCE:
                  if (assertion.nestedAlternatives != null) {
                     throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0006_UNEXPECTED_MULTIPLE_POLICY_NODES()));
                  }

                  assertion.nestedAlternatives = new LinkedList();
                  PolicyModelTranslator.RawPolicy nestedPolicy;
                  if (assertionNodeChild.getType() == ModelNode.Type.POLICY) {
                     nestedPolicy = new PolicyModelTranslator.RawPolicy(assertionNodeChild, assertion.nestedAlternatives);
                  } else {
                     nestedPolicy = new PolicyModelTranslator.RawPolicy(PolicyModelTranslator.getReferencedModelRootNode(assertionNodeChild), assertion.nestedAlternatives);
                  }

                  this.allNestedPolicies.add(nestedPolicy);
                  break;
               default:
                  throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0008_UNEXPECTED_CHILD_MODEL_TYPE(assertionNodeChild.getType())));
               }
            }
         }

      }
   }

   private static final class RawAssertion {
      ModelNode originalNode;
      Collection<PolicyModelTranslator.RawAlternative> nestedAlternatives = null;
      final Collection<ModelNode> parameters;

      RawAssertion(ModelNode originalNode, Collection<ModelNode> parameters) {
         this.parameters = parameters;
         this.originalNode = originalNode;
      }
   }

   private static final class ContentDecomposition {
      final List<Collection<ModelNode>> exactlyOneContents;
      final List<ModelNode> assertions;

      private ContentDecomposition() {
         this.exactlyOneContents = new LinkedList();
         this.assertions = new LinkedList();
      }

      void reset() {
         this.exactlyOneContents.clear();
         this.assertions.clear();
      }

      // $FF: synthetic method
      ContentDecomposition(Object x0) {
         this();
      }
   }
}
