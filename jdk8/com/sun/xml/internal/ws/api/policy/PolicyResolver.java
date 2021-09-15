package com.sun.xml.internal.ws.api.policy;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.ws.WebServiceException;

public interface PolicyResolver {
   PolicyMap resolve(PolicyResolver.ServerContext var1) throws WebServiceException;

   PolicyMap resolve(PolicyResolver.ClientContext var1) throws WebServiceException;

   public static class ClientContext {
      private PolicyMap policyMap;
      private Container container;

      public ClientContext(@Nullable PolicyMap policyMap, Container container) {
         this.policyMap = policyMap;
         this.container = container;
      }

      @Nullable
      public PolicyMap getPolicyMap() {
         return this.policyMap;
      }

      public Container getContainer() {
         return this.container;
      }
   }

   public static class ServerContext {
      private final PolicyMap policyMap;
      private final Class endpointClass;
      private final Container container;
      private final boolean hasWsdl;
      private final Collection<PolicyMapMutator> mutators;

      public ServerContext(@Nullable PolicyMap policyMap, Container container, Class endpointClass, PolicyMapMutator... mutators) {
         this.policyMap = policyMap;
         this.endpointClass = endpointClass;
         this.container = container;
         this.hasWsdl = true;
         this.mutators = Arrays.asList(mutators);
      }

      public ServerContext(@Nullable PolicyMap policyMap, Container container, Class endpointClass, boolean hasWsdl, PolicyMapMutator... mutators) {
         this.policyMap = policyMap;
         this.endpointClass = endpointClass;
         this.container = container;
         this.hasWsdl = hasWsdl;
         this.mutators = Arrays.asList(mutators);
      }

      @Nullable
      public PolicyMap getPolicyMap() {
         return this.policyMap;
      }

      @Nullable
      public Class getEndpointClass() {
         return this.endpointClass;
      }

      public Container getContainer() {
         return this.container;
      }

      public boolean hasWsdl() {
         return this.hasWsdl;
      }

      public Collection<PolicyMapMutator> getMutators() {
         return this.mutators;
      }
   }
}
