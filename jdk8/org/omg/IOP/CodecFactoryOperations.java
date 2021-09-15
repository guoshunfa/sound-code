package org.omg.IOP;

import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;

public interface CodecFactoryOperations {
   Codec create_codec(Encoding var1) throws UnknownEncoding;
}
