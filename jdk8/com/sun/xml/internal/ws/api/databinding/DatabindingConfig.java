package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;
import org.xml.sax.EntityResolver;

public class DatabindingConfig {
   protected Class contractClass;
   protected Class endpointClass;
   protected Set<Class> additionalValueTypes = new HashSet();
   protected MappingInfo mappingInfo = new MappingInfo();
   protected URL wsdlURL;
   protected ClassLoader classLoader;
   protected Iterable<WebServiceFeature> features;
   protected WSBinding wsBinding;
   protected WSDLPort wsdlPort;
   protected MetadataReader metadataReader;
   protected Map<String, Object> properties = new HashMap();
   protected Source wsdlSource;
   protected EntityResolver entityResolver;

   public Class getContractClass() {
      return this.contractClass;
   }

   public void setContractClass(Class contractClass) {
      this.contractClass = contractClass;
   }

   public Class getEndpointClass() {
      return this.endpointClass;
   }

   public void setEndpointClass(Class implBeanClass) {
      this.endpointClass = implBeanClass;
   }

   public MappingInfo getMappingInfo() {
      return this.mappingInfo;
   }

   public void setMappingInfo(MappingInfo mappingInfo) {
      this.mappingInfo = mappingInfo;
   }

   public URL getWsdlURL() {
      return this.wsdlURL;
   }

   public void setWsdlURL(URL wsdlURL) {
      this.wsdlURL = wsdlURL;
   }

   public ClassLoader getClassLoader() {
      return this.classLoader;
   }

   public void setClassLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   public Iterable<WebServiceFeature> getFeatures() {
      return (Iterable)(this.features == null && this.wsBinding != null ? this.wsBinding.getFeatures() : this.features);
   }

   public void setFeatures(WebServiceFeature[] features) {
      this.setFeatures((Iterable)(new WebServiceFeatureList(features)));
   }

   public void setFeatures(Iterable<WebServiceFeature> features) {
      this.features = WebServiceFeatureList.toList(features);
   }

   public WSDLPort getWsdlPort() {
      return this.wsdlPort;
   }

   public void setWsdlPort(WSDLPort wsdlPort) {
      this.wsdlPort = wsdlPort;
   }

   public Set<Class> additionalValueTypes() {
      return this.additionalValueTypes;
   }

   public Map<String, Object> properties() {
      return this.properties;
   }

   public WSBinding getWSBinding() {
      return this.wsBinding;
   }

   public void setWSBinding(WSBinding wsBinding) {
      this.wsBinding = wsBinding;
   }

   public MetadataReader getMetadataReader() {
      return this.metadataReader;
   }

   public void setMetadataReader(MetadataReader reader) {
      this.metadataReader = reader;
   }

   public Source getWsdlSource() {
      return this.wsdlSource;
   }

   public void setWsdlSource(Source wsdlSource) {
      this.wsdlSource = wsdlSource;
   }

   public EntityResolver getEntityResolver() {
      return this.entityResolver;
   }

   public void setEntityResolver(EntityResolver entityResolver) {
      this.entityResolver = entityResolver;
   }
}
