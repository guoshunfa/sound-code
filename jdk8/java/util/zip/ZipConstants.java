package java.util.zip;

interface ZipConstants {
   long LOCSIG = 67324752L;
   long EXTSIG = 134695760L;
   long CENSIG = 33639248L;
   long ENDSIG = 101010256L;
   int LOCHDR = 30;
   int EXTHDR = 16;
   int CENHDR = 46;
   int ENDHDR = 22;
   int LOCVER = 4;
   int LOCFLG = 6;
   int LOCHOW = 8;
   int LOCTIM = 10;
   int LOCCRC = 14;
   int LOCSIZ = 18;
   int LOCLEN = 22;
   int LOCNAM = 26;
   int LOCEXT = 28;
   int EXTCRC = 4;
   int EXTSIZ = 8;
   int EXTLEN = 12;
   int CENVEM = 4;
   int CENVER = 6;
   int CENFLG = 8;
   int CENHOW = 10;
   int CENTIM = 12;
   int CENCRC = 16;
   int CENSIZ = 20;
   int CENLEN = 24;
   int CENNAM = 28;
   int CENEXT = 30;
   int CENCOM = 32;
   int CENDSK = 34;
   int CENATT = 36;
   int CENATX = 38;
   int CENOFF = 42;
   int ENDSUB = 8;
   int ENDTOT = 10;
   int ENDSIZ = 12;
   int ENDOFF = 16;
   int ENDCOM = 20;
}
