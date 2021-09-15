package org.omg.CORBA.portable;

public interface ResponseHandler {
   OutputStream createReply();

   OutputStream createExceptionReply();
}
