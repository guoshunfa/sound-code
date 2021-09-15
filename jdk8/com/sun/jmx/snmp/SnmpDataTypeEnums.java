package com.sun.jmx.snmp;

public interface SnmpDataTypeEnums {
   int BooleanTag = 1;
   int IntegerTag = 2;
   int BitStringTag = 2;
   int OctetStringTag = 4;
   int NullTag = 5;
   int ObjectIdentiferTag = 6;
   int UnknownSyntaxTag = 255;
   int SequenceTag = 48;
   int TableTag = 254;
   int ApplFlag = 64;
   int CtxtFlag = 128;
   int IpAddressTag = 64;
   int CounterTag = 65;
   int GaugeTag = 66;
   int TimeticksTag = 67;
   int OpaqueTag = 68;
   int Counter64Tag = 70;
   int NsapTag = 69;
   int UintegerTag = 71;
   int errNoSuchObjectTag = 128;
   int errNoSuchInstanceTag = 129;
   int errEndOfMibViewTag = 130;
}
