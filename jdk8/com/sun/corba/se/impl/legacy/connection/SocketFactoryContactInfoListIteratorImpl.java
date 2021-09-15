package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.transport.CorbaContactInfoListIteratorImpl;
import com.sun.corba.se.impl.transport.SharedCDRContactInfoImpl;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.SocketInfo;
import java.util.List;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

public class SocketFactoryContactInfoListIteratorImpl extends CorbaContactInfoListIteratorImpl {
   private SocketInfo socketInfoCookie;

   public SocketFactoryContactInfoListIteratorImpl(ORB var1, CorbaContactInfoList var2) {
      super(var1, var2, (ContactInfo)null, (List)null);
   }

   public boolean hasNext() {
      return true;
   }

   public Object next() {
      return this.contactInfoList.getEffectiveTargetIOR().getProfile().isLocal() ? new SharedCDRContactInfoImpl(this.orb, this.contactInfoList, this.contactInfoList.getEffectiveTargetIOR(), this.orb.getORBData().getGIOPAddressDisposition()) : new SocketFactoryContactInfoImpl(this.orb, this.contactInfoList, this.contactInfoList.getEffectiveTargetIOR(), this.orb.getORBData().getGIOPAddressDisposition(), this.socketInfoCookie);
   }

   public boolean reportException(ContactInfo var1, RuntimeException var2) {
      this.failureContactInfo = (CorbaContactInfo)var1;
      this.failureException = var2;
      if (var2 instanceof COMM_FAILURE) {
         if (var2.getCause() instanceof GetEndPointInfoAgainException) {
            this.socketInfoCookie = ((GetEndPointInfoAgainException)var2.getCause()).getEndPointInfo();
            return true;
         }

         SystemException var3 = (SystemException)var2;
         if (var3.completed == CompletionStatus.COMPLETED_NO && this.contactInfoList.getEffectiveTargetIOR() != this.contactInfoList.getTargetIOR()) {
            this.contactInfoList.setEffectiveTargetIOR(this.contactInfoList.getTargetIOR());
            return true;
         }
      }

      return false;
   }
}
