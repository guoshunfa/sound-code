package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.XmlPolicyModelUnmarshaller;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;

public class ModelUnmarshaller extends XmlPolicyModelUnmarshaller {
   private static final ModelUnmarshaller INSTANCE = new ModelUnmarshaller();

   private ModelUnmarshaller() {
   }

   public static ModelUnmarshaller getUnmarshaller() {
      return INSTANCE;
   }

   protected PolicySourceModel createSourceModel(NamespaceVersion nsVersion, String id, String name) {
      return SourceModel.createSourceModel(nsVersion, id, name);
   }
}
