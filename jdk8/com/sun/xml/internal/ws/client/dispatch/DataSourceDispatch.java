package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import javax.activation.DataSource;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

public class DataSourceDispatch extends DispatchImpl<DataSource> {
   /** @deprecated */
   @Deprecated
   public DataSourceDispatch(QName port, Service.Mode mode, WSServiceDelegate service, Tube pipe, BindingImpl binding, WSEndpointReference epr) {
      super(port, mode, service, pipe, binding, epr);
   }

   public DataSourceDispatch(WSPortInfo portInfo, Service.Mode mode, BindingImpl binding, WSEndpointReference epr) {
      super(portInfo, mode, binding, epr);
   }

   Packet createPacket(DataSource arg) {
      switch(this.mode) {
      case PAYLOAD:
         throw new IllegalArgumentException("DataSource use is not allowed in Service.Mode.PAYLOAD\n");
      case MESSAGE:
         return new Packet(XMLMessage.create(arg, this.binding.getFeatures()));
      default:
         throw new WebServiceException("Unrecognized message mode");
      }
   }

   DataSource toReturnValue(Packet response) {
      Message message = response.getInternalMessage();
      return message instanceof XMLMessage.MessageDataSource ? ((XMLMessage.MessageDataSource)message).getDataSource() : XMLMessage.getDataSource(message, this.binding.getFeatures());
   }
}
