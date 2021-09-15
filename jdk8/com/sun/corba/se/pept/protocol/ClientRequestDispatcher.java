package com.sun.corba.se.pept.protocol;

import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.transport.ContactInfo;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;

public interface ClientRequestDispatcher {
   OutputObject beginRequest(Object var1, String var2, boolean var3, ContactInfo var4);

   InputObject marshalingComplete(Object var1, OutputObject var2) throws ApplicationException, RemarshalException;

   void endRequest(Broker var1, Object var2, InputObject var3);
}
