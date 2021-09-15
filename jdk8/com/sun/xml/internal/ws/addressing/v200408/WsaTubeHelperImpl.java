package com.sun.xml.internal.ws.addressing.v200408;

import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class WsaTubeHelperImpl extends WsaTubeHelper {
   static final JAXBContext jc;

   public WsaTubeHelperImpl(WSDLPort wsdlPort, SEIModel seiModel, WSBinding binding) {
      super(binding, seiModel, wsdlPort);
   }

   private Marshaller createMarshaller() throws JAXBException {
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
      return marshaller;
   }

   public final void getProblemActionDetail(String action, Element element) {
      ProblemAction pa = new ProblemAction(action);

      try {
         this.createMarshaller().marshal(pa, (Node)element);
      } catch (JAXBException var5) {
         throw new WebServiceException(var5);
      }
   }

   public final void getInvalidMapDetail(QName name, Element element) {
      ProblemHeaderQName phq = new ProblemHeaderQName(name);

      try {
         this.createMarshaller().marshal(phq, (Node)element);
      } catch (JAXBException var5) {
         throw new WebServiceException(var5);
      }
   }

   public final void getMapRequiredDetail(QName name, Element element) {
      this.getInvalidMapDetail(name, element);
   }

   static {
      try {
         jc = JAXBContext.newInstance(ProblemAction.class, ProblemHeaderQName.class);
      } catch (JAXBException var1) {
         throw new WebServiceException(var1);
      }
   }
}
