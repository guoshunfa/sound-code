package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.Dynamic.Parameter;
import org.omg.IOP.ServiceContext;

public interface RequestInfoOperations {
   int request_id();

   String operation();

   Parameter[] arguments();

   TypeCode[] exceptions();

   String[] contexts();

   String[] operation_context();

   Any result();

   boolean response_expected();

   short sync_scope();

   short reply_status();

   Object forward_reference();

   Any get_slot(int var1) throws InvalidSlot;

   ServiceContext get_request_service_context(int var1);

   ServiceContext get_reply_service_context(int var1);
}
