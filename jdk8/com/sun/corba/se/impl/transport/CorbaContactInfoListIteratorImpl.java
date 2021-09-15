package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.protocol.CorbaInvocationInfo;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import com.sun.corba.se.spi.transport.IIOPPrimaryToContactInfo;
import java.util.Iterator;
import java.util.List;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

public class CorbaContactInfoListIteratorImpl implements CorbaContactInfoListIterator {
   protected ORB orb;
   protected CorbaContactInfoList contactInfoList;
   protected CorbaContactInfo successContactInfo;
   protected CorbaContactInfo failureContactInfo;
   protected RuntimeException failureException;
   protected Iterator effectiveTargetIORIterator;
   protected CorbaContactInfo previousContactInfo;
   protected boolean isAddrDispositionRetry;
   protected IIOPPrimaryToContactInfo primaryToContactInfo;
   protected ContactInfo primaryContactInfo;
   protected List listOfContactInfos;

   public CorbaContactInfoListIteratorImpl(ORB var1, CorbaContactInfoList var2, ContactInfo var3, List var4) {
      this.orb = var1;
      this.contactInfoList = var2;
      this.primaryContactInfo = var3;
      if (var4 != null) {
         this.effectiveTargetIORIterator = var4.iterator();
      }

      this.listOfContactInfos = var4;
      this.previousContactInfo = null;
      this.isAddrDispositionRetry = false;
      this.successContactInfo = null;
      this.failureContactInfo = null;
      this.failureException = null;
      this.primaryToContactInfo = var1.getORBData().getIIOPPrimaryToContactInfo();
   }

   public boolean hasNext() {
      if (this.isAddrDispositionRetry) {
         return true;
      } else {
         boolean var1;
         if (this.primaryToContactInfo != null) {
            var1 = this.primaryToContactInfo.hasNext(this.primaryContactInfo, this.previousContactInfo, this.listOfContactInfos);
         } else {
            var1 = this.effectiveTargetIORIterator.hasNext();
         }

         return var1;
      }
   }

   public Object next() {
      if (this.isAddrDispositionRetry) {
         this.isAddrDispositionRetry = false;
         return this.previousContactInfo;
      } else {
         if (this.primaryToContactInfo != null) {
            this.previousContactInfo = (CorbaContactInfo)this.primaryToContactInfo.next(this.primaryContactInfo, this.previousContactInfo, this.listOfContactInfos);
         } else {
            this.previousContactInfo = (CorbaContactInfo)this.effectiveTargetIORIterator.next();
         }

         return this.previousContactInfo;
      }
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

   public ContactInfoList getContactInfoList() {
      return this.contactInfoList;
   }

   public void reportSuccess(ContactInfo var1) {
      this.successContactInfo = (CorbaContactInfo)var1;
   }

   public boolean reportException(ContactInfo var1, RuntimeException var2) {
      this.failureContactInfo = (CorbaContactInfo)var1;
      this.failureException = var2;
      if (var2 instanceof COMM_FAILURE) {
         SystemException var3 = (SystemException)var2;
         if (var3.completed == CompletionStatus.COMPLETED_NO) {
            if (this.hasNext()) {
               return true;
            }

            if (this.contactInfoList.getEffectiveTargetIOR() != this.contactInfoList.getTargetIOR()) {
               this.updateEffectiveTargetIOR(this.contactInfoList.getTargetIOR());
               return true;
            }
         }
      }

      return false;
   }

   public RuntimeException getFailureException() {
      return (RuntimeException)(this.failureException == null ? ORBUtilSystemException.get(this.orb, "rpc.transport").invalidContactInfoListIteratorFailureException() : this.failureException);
   }

   public void reportAddrDispositionRetry(CorbaContactInfo var1, short var2) {
      this.previousContactInfo.setAddressingDisposition(var2);
      this.isAddrDispositionRetry = true;
   }

   public void reportRedirect(CorbaContactInfo var1, IOR var2) {
      this.updateEffectiveTargetIOR(var2);
   }

   public void updateEffectiveTargetIOR(IOR var1) {
      this.contactInfoList.setEffectiveTargetIOR(var1);
      ((CorbaInvocationInfo)this.orb.getInvocationInfo()).setContactInfoListIterator(this.contactInfoList.iterator());
   }
}
