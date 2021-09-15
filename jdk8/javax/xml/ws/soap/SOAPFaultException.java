package javax.xml.ws.soap;

import javax.xml.soap.SOAPFault;
import javax.xml.ws.ProtocolException;

public class SOAPFaultException extends ProtocolException {
   private SOAPFault fault;

   public SOAPFaultException(SOAPFault fault) {
      super(fault.getFaultString());
      this.fault = fault;
   }

   public SOAPFault getFault() {
      return this.fault;
   }
}
