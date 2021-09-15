package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.addressing.policy.AddressingPrefixMapper;
import com.sun.xml.internal.ws.config.management.policy.ManagementPrefixMapper;
import com.sun.xml.internal.ws.encoding.policy.EncodingPrefixMapper;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.spi.PrefixMapper;
import java.util.Arrays;

public class SourceModel extends PolicySourceModel {
   private static final PrefixMapper[] JAXWS_PREFIX_MAPPERS = new PrefixMapper[]{new AddressingPrefixMapper(), new EncodingPrefixMapper(), new ManagementPrefixMapper()};

   private SourceModel(NamespaceVersion nsVersion) {
      this(nsVersion, (String)null, (String)null);
   }

   private SourceModel(NamespaceVersion nsVersion, String policyId, String policyName) {
      super(nsVersion, policyId, policyName, Arrays.asList(JAXWS_PREFIX_MAPPERS));
   }

   public static PolicySourceModel createSourceModel(NamespaceVersion nsVersion) {
      return new SourceModel(nsVersion);
   }

   public static PolicySourceModel createSourceModel(NamespaceVersion nsVersion, String policyId, String policyName) {
      return new SourceModel(nsVersion, policyId, policyName);
   }
}
