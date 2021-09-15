package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_TYPECODE;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.UNKNOWN;

public class ORBUtilSystemException extends LogWrapperBase {
   private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger var1) {
         return new ORBUtilSystemException(var1);
      }
   };
   public static final int ADAPTER_ID_NOT_AVAILABLE = 1398079689;
   public static final int SERVER_ID_NOT_AVAILABLE = 1398079690;
   public static final int ORB_ID_NOT_AVAILABLE = 1398079691;
   public static final int OBJECT_ADAPTER_ID_NOT_AVAILABLE = 1398079692;
   public static final int CONNECTING_SERVANT = 1398079693;
   public static final int EXTRACT_WRONG_TYPE = 1398079694;
   public static final int EXTRACT_WRONG_TYPE_LIST = 1398079695;
   public static final int BAD_STRING_BOUNDS = 1398079696;
   public static final int INSERT_OBJECT_INCOMPATIBLE = 1398079698;
   public static final int INSERT_OBJECT_FAILED = 1398079699;
   public static final int EXTRACT_OBJECT_INCOMPATIBLE = 1398079700;
   public static final int FIXED_NOT_MATCH = 1398079701;
   public static final int FIXED_BAD_TYPECODE = 1398079702;
   public static final int SET_EXCEPTION_CALLED_NULL_ARGS = 1398079711;
   public static final int SET_EXCEPTION_CALLED_BAD_TYPE = 1398079712;
   public static final int CONTEXT_CALLED_OUT_OF_ORDER = 1398079713;
   public static final int BAD_ORB_CONFIGURATOR = 1398079714;
   public static final int ORB_CONFIGURATOR_ERROR = 1398079715;
   public static final int ORB_DESTROYED = 1398079716;
   public static final int NEGATIVE_BOUNDS = 1398079717;
   public static final int EXTRACT_NOT_INITIALIZED = 1398079718;
   public static final int EXTRACT_OBJECT_FAILED = 1398079719;
   public static final int METHOD_NOT_FOUND_IN_TIE = 1398079720;
   public static final int CLASS_NOT_FOUND1 = 1398079721;
   public static final int CLASS_NOT_FOUND2 = 1398079722;
   public static final int CLASS_NOT_FOUND3 = 1398079723;
   public static final int GET_DELEGATE_SERVANT_NOT_ACTIVE = 1398079724;
   public static final int GET_DELEGATE_WRONG_POLICY = 1398079725;
   public static final int SET_DELEGATE_REQUIRES_STUB = 1398079726;
   public static final int GET_DELEGATE_REQUIRES_STUB = 1398079727;
   public static final int GET_TYPE_IDS_REQUIRES_STUB = 1398079728;
   public static final int GET_ORB_REQUIRES_STUB = 1398079729;
   public static final int CONNECT_REQUIRES_STUB = 1398079730;
   public static final int IS_LOCAL_REQUIRES_STUB = 1398079731;
   public static final int REQUEST_REQUIRES_STUB = 1398079732;
   public static final int BAD_ACTIVATE_TIE_CALL = 1398079733;
   public static final int IO_EXCEPTION_ON_CLOSE = 1398079734;
   public static final int NULL_PARAM = 1398079689;
   public static final int UNABLE_FIND_VALUE_FACTORY = 1398079690;
   public static final int ABSTRACT_FROM_NON_ABSTRACT = 1398079691;
   public static final int INVALID_TAGGED_PROFILE = 1398079692;
   public static final int OBJREF_FROM_FOREIGN_ORB = 1398079693;
   public static final int LOCAL_OBJECT_NOT_ALLOWED = 1398079694;
   public static final int NULL_OBJECT_REFERENCE = 1398079695;
   public static final int COULD_NOT_LOAD_CLASS = 1398079696;
   public static final int BAD_URL = 1398079697;
   public static final int FIELD_NOT_FOUND = 1398079698;
   public static final int ERROR_SETTING_FIELD = 1398079699;
   public static final int BOUNDS_ERROR_IN_DII_REQUEST = 1398079700;
   public static final int PERSISTENT_SERVER_INIT_ERROR = 1398079701;
   public static final int COULD_NOT_CREATE_ARRAY = 1398079702;
   public static final int COULD_NOT_SET_ARRAY = 1398079703;
   public static final int ILLEGAL_BOOTSTRAP_OPERATION = 1398079704;
   public static final int BOOTSTRAP_RUNTIME_EXCEPTION = 1398079705;
   public static final int BOOTSTRAP_EXCEPTION = 1398079706;
   public static final int STRING_EXPECTED = 1398079707;
   public static final int INVALID_TYPECODE_KIND = 1398079708;
   public static final int SOCKET_FACTORY_AND_CONTACT_INFO_LIST_AT_SAME_TIME = 1398079709;
   public static final int ACCEPTORS_AND_LEGACY_SOCKET_FACTORY_AT_SAME_TIME = 1398079710;
   public static final int BAD_ORB_FOR_SERVANT = 1398079711;
   public static final int INVALID_REQUEST_PARTITIONING_POLICY_VALUE = 1398079712;
   public static final int INVALID_REQUEST_PARTITIONING_COMPONENT_VALUE = 1398079713;
   public static final int INVALID_REQUEST_PARTITIONING_ID = 1398079714;
   public static final int ERROR_IN_SETTING_DYNAMIC_STUB_FACTORY_FACTORY = 1398079715;
   public static final int DSIMETHOD_NOTCALLED = 1398079689;
   public static final int ARGUMENTS_CALLED_MULTIPLE = 1398079690;
   public static final int ARGUMENTS_CALLED_AFTER_EXCEPTION = 1398079691;
   public static final int ARGUMENTS_CALLED_NULL_ARGS = 1398079692;
   public static final int ARGUMENTS_NOT_CALLED = 1398079693;
   public static final int SET_RESULT_CALLED_MULTIPLE = 1398079694;
   public static final int SET_RESULT_AFTER_EXCEPTION = 1398079695;
   public static final int SET_RESULT_CALLED_NULL_ARGS = 1398079696;
   public static final int BAD_REMOTE_TYPECODE = 1398079689;
   public static final int UNRESOLVED_RECURSIVE_TYPECODE = 1398079690;
   public static final int CONNECT_FAILURE = 1398079689;
   public static final int CONNECTION_CLOSE_REBIND = 1398079690;
   public static final int WRITE_ERROR_SEND = 1398079691;
   public static final int GET_PROPERTIES_ERROR = 1398079692;
   public static final int BOOTSTRAP_SERVER_NOT_AVAIL = 1398079693;
   public static final int INVOKE_ERROR = 1398079694;
   public static final int DEFAULT_CREATE_SERVER_SOCKET_GIVEN_NON_IIOP_CLEAR_TEXT = 1398079695;
   public static final int CONNECTION_ABORT = 1398079696;
   public static final int CONNECTION_REBIND = 1398079697;
   public static final int RECV_MSG_ERROR = 1398079698;
   public static final int IOEXCEPTION_WHEN_READING_CONNECTION = 1398079699;
   public static final int SELECTION_KEY_INVALID = 1398079700;
   public static final int EXCEPTION_IN_ACCEPT = 1398079701;
   public static final int SECURITY_EXCEPTION_IN_ACCEPT = 1398079702;
   public static final int TRANSPORT_READ_TIMEOUT_EXCEEDED = 1398079703;
   public static final int CREATE_LISTENER_FAILED = 1398079704;
   public static final int BUFFER_READ_MANAGER_TIMEOUT = 1398079705;
   public static final int BAD_STRINGIFIED_IOR_LEN = 1398079689;
   public static final int BAD_STRINGIFIED_IOR = 1398079690;
   public static final int BAD_MODIFIER = 1398079691;
   public static final int CODESET_INCOMPATIBLE = 1398079692;
   public static final int BAD_HEX_DIGIT = 1398079693;
   public static final int BAD_UNICODE_PAIR = 1398079694;
   public static final int BTC_RESULT_MORE_THAN_ONE_CHAR = 1398079695;
   public static final int BAD_CODESETS_FROM_CLIENT = 1398079696;
   public static final int INVALID_SINGLE_CHAR_CTB = 1398079697;
   public static final int BAD_GIOP_1_1_CTB = 1398079698;
   public static final int BAD_SEQUENCE_BOUNDS = 1398079700;
   public static final int ILLEGAL_SOCKET_FACTORY_TYPE = 1398079701;
   public static final int BAD_CUSTOM_SOCKET_FACTORY = 1398079702;
   public static final int FRAGMENT_SIZE_MINIMUM = 1398079703;
   public static final int FRAGMENT_SIZE_DIV = 1398079704;
   public static final int ORB_INITIALIZER_FAILURE = 1398079705;
   public static final int ORB_INITIALIZER_TYPE = 1398079706;
   public static final int ORB_INITIALREFERENCE_SYNTAX = 1398079707;
   public static final int ACCEPTOR_INSTANTIATION_FAILURE = 1398079708;
   public static final int ACCEPTOR_INSTANTIATION_TYPE_FAILURE = 1398079709;
   public static final int ILLEGAL_CONTACT_INFO_LIST_FACTORY_TYPE = 1398079710;
   public static final int BAD_CONTACT_INFO_LIST_FACTORY = 1398079711;
   public static final int ILLEGAL_IOR_TO_SOCKET_INFO_TYPE = 1398079712;
   public static final int BAD_CUSTOM_IOR_TO_SOCKET_INFO = 1398079713;
   public static final int ILLEGAL_IIOP_PRIMARY_TO_CONTACT_INFO_TYPE = 1398079714;
   public static final int BAD_CUSTOM_IIOP_PRIMARY_TO_CONTACT_INFO = 1398079715;
   public static final int BAD_CORBALOC_STRING = 1398079689;
   public static final int NO_PROFILE_PRESENT = 1398079690;
   public static final int CANNOT_CREATE_ORBID_DB = 1398079689;
   public static final int CANNOT_READ_ORBID_DB = 1398079690;
   public static final int CANNOT_WRITE_ORBID_DB = 1398079691;
   public static final int GET_SERVER_PORT_CALLED_BEFORE_ENDPOINTS_INITIALIZED = 1398079692;
   public static final int PERSISTENT_SERVERPORT_NOT_SET = 1398079693;
   public static final int PERSISTENT_SERVERID_NOT_SET = 1398079694;
   public static final int NON_EXISTENT_ORBID = 1398079689;
   public static final int NO_SERVER_SUBCONTRACT = 1398079690;
   public static final int SERVER_SC_TEMP_SIZE = 1398079691;
   public static final int NO_CLIENT_SC_CLASS = 1398079692;
   public static final int SERVER_SC_NO_IIOP_PROFILE = 1398079693;
   public static final int GET_SYSTEM_EX_RETURNED_NULL = 1398079694;
   public static final int PEEKSTRING_FAILED = 1398079695;
   public static final int GET_LOCAL_HOST_FAILED = 1398079696;
   public static final int BAD_LOCATE_REQUEST_STATUS = 1398079698;
   public static final int STRINGIFY_WRITE_ERROR = 1398079699;
   public static final int BAD_GIOP_REQUEST_TYPE = 1398079700;
   public static final int ERROR_UNMARSHALING_USEREXC = 1398079701;
   public static final int RequestDispatcherRegistry_ERROR = 1398079702;
   public static final int LOCATIONFORWARD_ERROR = 1398079703;
   public static final int WRONG_CLIENTSC = 1398079704;
   public static final int BAD_SERVANT_READ_OBJECT = 1398079705;
   public static final int MULT_IIOP_PROF_NOT_SUPPORTED = 1398079706;
   public static final int GIOP_MAGIC_ERROR = 1398079708;
   public static final int GIOP_VERSION_ERROR = 1398079709;
   public static final int ILLEGAL_REPLY_STATUS = 1398079710;
   public static final int ILLEGAL_GIOP_MSG_TYPE = 1398079711;
   public static final int FRAGMENTATION_DISALLOWED = 1398079712;
   public static final int BAD_REPLYSTATUS = 1398079713;
   public static final int CTB_CONVERTER_FAILURE = 1398079714;
   public static final int BTC_CONVERTER_FAILURE = 1398079715;
   public static final int WCHAR_ARRAY_UNSUPPORTED_ENCODING = 1398079716;
   public static final int ILLEGAL_TARGET_ADDRESS_DISPOSITION = 1398079717;
   public static final int NULL_REPLY_IN_GET_ADDR_DISPOSITION = 1398079718;
   public static final int ORB_TARGET_ADDR_PREFERENCE_IN_EXTRACT_OBJECTKEY_INVALID = 1398079719;
   public static final int INVALID_ISSTREAMED_TCKIND = 1398079720;
   public static final int INVALID_JDK1_3_1_PATCH_LEVEL = 1398079721;
   public static final int SVCCTX_UNMARSHAL_ERROR = 1398079722;
   public static final int NULL_IOR = 1398079723;
   public static final int UNSUPPORTED_GIOP_VERSION = 1398079724;
   public static final int APPLICATION_EXCEPTION_IN_SPECIAL_METHOD = 1398079725;
   public static final int STATEMENT_NOT_REACHABLE1 = 1398079726;
   public static final int STATEMENT_NOT_REACHABLE2 = 1398079727;
   public static final int STATEMENT_NOT_REACHABLE3 = 1398079728;
   public static final int STATEMENT_NOT_REACHABLE4 = 1398079729;
   public static final int STATEMENT_NOT_REACHABLE5 = 1398079730;
   public static final int STATEMENT_NOT_REACHABLE6 = 1398079731;
   public static final int UNEXPECTED_DII_EXCEPTION = 1398079732;
   public static final int METHOD_SHOULD_NOT_BE_CALLED = 1398079733;
   public static final int CANCEL_NOT_SUPPORTED = 1398079734;
   public static final int EMPTY_STACK_RUN_SERVANT_POST_INVOKE = 1398079735;
   public static final int PROBLEM_WITH_EXCEPTION_TYPECODE = 1398079736;
   public static final int ILLEGAL_SUBCONTRACT_ID = 1398079737;
   public static final int BAD_SYSTEM_EXCEPTION_IN_LOCATE_REPLY = 1398079738;
   public static final int BAD_SYSTEM_EXCEPTION_IN_REPLY = 1398079739;
   public static final int BAD_COMPLETION_STATUS_IN_LOCATE_REPLY = 1398079740;
   public static final int BAD_COMPLETION_STATUS_IN_REPLY = 1398079741;
   public static final int BADKIND_CANNOT_OCCUR = 1398079742;
   public static final int ERROR_RESOLVING_ALIAS = 1398079743;
   public static final int TK_LONG_DOUBLE_NOT_SUPPORTED = 1398079744;
   public static final int TYPECODE_NOT_SUPPORTED = 1398079745;
   public static final int BOUNDS_CANNOT_OCCUR = 1398079747;
   public static final int NUM_INVOCATIONS_ALREADY_ZERO = 1398079749;
   public static final int ERROR_INIT_BADSERVERIDHANDLER = 1398079750;
   public static final int NO_TOA = 1398079751;
   public static final int NO_POA = 1398079752;
   public static final int INVOCATION_INFO_STACK_EMPTY = 1398079753;
   public static final int BAD_CODE_SET_STRING = 1398079754;
   public static final int UNKNOWN_NATIVE_CODESET = 1398079755;
   public static final int UNKNOWN_CONVERSION_CODE_SET = 1398079756;
   public static final int INVALID_CODE_SET_NUMBER = 1398079757;
   public static final int INVALID_CODE_SET_STRING = 1398079758;
   public static final int INVALID_CTB_CONVERTER_NAME = 1398079759;
   public static final int INVALID_BTC_CONVERTER_NAME = 1398079760;
   public static final int COULD_NOT_DUPLICATE_CDR_INPUT_STREAM = 1398079761;
   public static final int BOOTSTRAP_APPLICATION_EXCEPTION = 1398079762;
   public static final int DUPLICATE_INDIRECTION_OFFSET = 1398079763;
   public static final int BAD_MESSAGE_TYPE_FOR_CANCEL = 1398079764;
   public static final int DUPLICATE_EXCEPTION_DETAIL_MESSAGE = 1398079765;
   public static final int BAD_EXCEPTION_DETAIL_MESSAGE_SERVICE_CONTEXT_TYPE = 1398079766;
   public static final int UNEXPECTED_DIRECT_BYTE_BUFFER_WITH_NON_CHANNEL_SOCKET = 1398079767;
   public static final int UNEXPECTED_NON_DIRECT_BYTE_BUFFER_WITH_CHANNEL_SOCKET = 1398079768;
   public static final int INVALID_CONTACT_INFO_LIST_ITERATOR_FAILURE_EXCEPTION = 1398079770;
   public static final int REMARSHAL_WITH_NOWHERE_TO_GO = 1398079771;
   public static final int EXCEPTION_WHEN_SENDING_CLOSE_CONNECTION = 1398079772;
   public static final int INVOCATION_ERROR_IN_REFLECTIVE_TIE = 1398079773;
   public static final int BAD_HELPER_WRITE_METHOD = 1398079774;
   public static final int BAD_HELPER_READ_METHOD = 1398079775;
   public static final int BAD_HELPER_ID_METHOD = 1398079776;
   public static final int WRITE_UNDECLARED_EXCEPTION = 1398079777;
   public static final int READ_UNDECLARED_EXCEPTION = 1398079778;
   public static final int UNABLE_TO_SET_SOCKET_FACTORY_ORB = 1398079779;
   public static final int UNEXPECTED_EXCEPTION = 1398079780;
   public static final int NO_INVOCATION_HANDLER = 1398079781;
   public static final int INVALID_BUFF_MGR_STRATEGY = 1398079782;
   public static final int JAVA_STREAM_INIT_FAILED = 1398079783;
   public static final int DUPLICATE_ORB_VERSION_SERVICE_CONTEXT = 1398079784;
   public static final int DUPLICATE_SENDING_CONTEXT_SERVICE_CONTEXT = 1398079785;
   public static final int WORK_QUEUE_THREAD_INTERRUPTED = 1398079786;
   public static final int WORKER_THREAD_CREATED = 1398079792;
   public static final int WORKER_THREAD_THROWABLE_FROM_REQUEST_WORK = 1398079797;
   public static final int WORKER_THREAD_NOT_NEEDED = 1398079798;
   public static final int WORKER_THREAD_DO_WORK_THROWABLE = 1398079799;
   public static final int WORKER_THREAD_CAUGHT_UNEXPECTED_THROWABLE = 1398079800;
   public static final int WORKER_THREAD_CREATION_FAILURE = 1398079801;
   public static final int WORKER_THREAD_SET_NAME_FAILURE = 1398079802;
   public static final int WORK_QUEUE_REQUEST_WORK_NO_WORK_FOUND = 1398079804;
   public static final int THREAD_POOL_CLOSE_ERROR = 1398079814;
   public static final int THREAD_GROUP_IS_DESTROYED = 1398079815;
   public static final int THREAD_GROUP_HAS_ACTIVE_THREADS_IN_CLOSE = 1398079816;
   public static final int THREAD_GROUP_HAS_SUB_GROUPS_IN_CLOSE = 1398079817;
   public static final int THREAD_GROUP_DESTROY_FAILED = 1398079818;
   public static final int INTERRUPTED_JOIN_CALL_WHILE_CLOSING_THREAD_POOL = 1398079819;
   public static final int CHUNK_OVERFLOW = 1398079689;
   public static final int UNEXPECTED_EOF = 1398079690;
   public static final int READ_OBJECT_EXCEPTION = 1398079691;
   public static final int CHARACTER_OUTOFRANGE = 1398079692;
   public static final int DSI_RESULT_EXCEPTION = 1398079693;
   public static final int IIOPINPUTSTREAM_GROW = 1398079694;
   public static final int END_OF_STREAM = 1398079695;
   public static final int INVALID_OBJECT_KEY = 1398079696;
   public static final int MALFORMED_URL = 1398079697;
   public static final int VALUEHANDLER_READ_ERROR = 1398079698;
   public static final int VALUEHANDLER_READ_EXCEPTION = 1398079699;
   public static final int BAD_KIND = 1398079700;
   public static final int CNFE_READ_CLASS = 1398079701;
   public static final int BAD_REP_ID_INDIRECTION = 1398079702;
   public static final int BAD_CODEBASE_INDIRECTION = 1398079703;
   public static final int UNKNOWN_CODESET = 1398079704;
   public static final int WCHAR_DATA_IN_GIOP_1_0 = 1398079705;
   public static final int NEGATIVE_STRING_LENGTH = 1398079706;
   public static final int EXPECTED_TYPE_NULL_AND_NO_REP_ID = 1398079707;
   public static final int READ_VALUE_AND_NO_REP_ID = 1398079708;
   public static final int UNEXPECTED_ENCLOSING_VALUETYPE = 1398079710;
   public static final int POSITIVE_END_TAG = 1398079711;
   public static final int NULL_OUT_CALL = 1398079712;
   public static final int WRITE_LOCAL_OBJECT = 1398079713;
   public static final int BAD_INSERTOBJ_PARAM = 1398079714;
   public static final int CUSTOM_WRAPPER_WITH_CODEBASE = 1398079715;
   public static final int CUSTOM_WRAPPER_INDIRECTION = 1398079716;
   public static final int CUSTOM_WRAPPER_NOT_SINGLE_REPID = 1398079717;
   public static final int BAD_VALUE_TAG = 1398079718;
   public static final int BAD_TYPECODE_FOR_CUSTOM_VALUE = 1398079719;
   public static final int ERROR_INVOKING_HELPER_WRITE = 1398079720;
   public static final int BAD_DIGIT_IN_FIXED = 1398079721;
   public static final int REF_TYPE_INDIR_TYPE = 1398079722;
   public static final int BAD_RESERVED_LENGTH = 1398079723;
   public static final int NULL_NOT_ALLOWED = 1398079724;
   public static final int UNION_DISCRIMINATOR_ERROR = 1398079726;
   public static final int CANNOT_MARSHAL_NATIVE = 1398079727;
   public static final int CANNOT_MARSHAL_BAD_TCKIND = 1398079728;
   public static final int INVALID_INDIRECTION = 1398079729;
   public static final int INDIRECTION_NOT_FOUND = 1398079730;
   public static final int RECURSIVE_TYPECODE_ERROR = 1398079731;
   public static final int INVALID_SIMPLE_TYPECODE = 1398079732;
   public static final int INVALID_COMPLEX_TYPECODE = 1398079733;
   public static final int INVALID_TYPECODE_KIND_MARSHAL = 1398079734;
   public static final int UNEXPECTED_UNION_DEFAULT = 1398079735;
   public static final int ILLEGAL_UNION_DISCRIMINATOR_TYPE = 1398079736;
   public static final int COULD_NOT_SKIP_BYTES = 1398079737;
   public static final int BAD_CHUNK_LENGTH = 1398079738;
   public static final int UNABLE_TO_LOCATE_REP_ID_ARRAY = 1398079739;
   public static final int BAD_FIXED = 1398079740;
   public static final int READ_OBJECT_LOAD_CLASS_FAILURE = 1398079741;
   public static final int COULD_NOT_INSTANTIATE_HELPER = 1398079742;
   public static final int BAD_TOA_OAID = 1398079743;
   public static final int COULD_NOT_INVOKE_HELPER_READ_METHOD = 1398079744;
   public static final int COULD_NOT_FIND_CLASS = 1398079745;
   public static final int BAD_ARGUMENTS_NVLIST = 1398079746;
   public static final int STUB_CREATE_ERROR = 1398079747;
   public static final int JAVA_SERIALIZATION_EXCEPTION = 1398079748;
   public static final int GENERIC_NO_IMPL = 1398079689;
   public static final int CONTEXT_NOT_IMPLEMENTED = 1398079690;
   public static final int GETINTERFACE_NOT_IMPLEMENTED = 1398079691;
   public static final int SEND_DEFERRED_NOTIMPLEMENTED = 1398079692;
   public static final int LONG_DOUBLE_NOT_IMPLEMENTED = 1398079693;
   public static final int NO_SERVER_SC_IN_DISPATCH = 1398079689;
   public static final int ORB_CONNECT_ERROR = 1398079690;
   public static final int ADAPTER_INACTIVE_IN_ACTIVATION = 1398079691;
   public static final int LOCATE_UNKNOWN_OBJECT = 1398079689;
   public static final int BAD_SERVER_ID = 1398079690;
   public static final int BAD_SKELETON = 1398079691;
   public static final int SERVANT_NOT_FOUND = 1398079692;
   public static final int NO_OBJECT_ADAPTER_FACTORY = 1398079693;
   public static final int BAD_ADAPTER_ID = 1398079694;
   public static final int DYN_ANY_DESTROYED = 1398079695;
   public static final int REQUEST_CANCELED = 1398079689;
   public static final int UNKNOWN_CORBA_EXC = 1398079689;
   public static final int RUNTIMEEXCEPTION = 1398079690;
   public static final int UNKNOWN_SERVER_ERROR = 1398079691;
   public static final int UNKNOWN_DSI_SYSEX = 1398079692;
   public static final int UNKNOWN_SYSEX = 1398079693;
   public static final int WRONG_INTERFACE_DEF = 1398079694;
   public static final int NO_INTERFACE_DEF_STUB = 1398079695;
   public static final int UNKNOWN_EXCEPTION_IN_DISPATCH = 1398079697;

   public ORBUtilSystemException(Logger var1) {
      super(var1);
   }

   public static ORBUtilSystemException get(ORB var0, String var1) {
      ORBUtilSystemException var2 = (ORBUtilSystemException)var0.getLogWrapper(var1, "ORBUTIL", factory);
      return var2;
   }

   public static ORBUtilSystemException get(String var0) {
      ORBUtilSystemException var1 = (ORBUtilSystemException)ORB.staticGetLogWrapper(var0, "ORBUTIL", factory);
      return var1;
   }

   public BAD_OPERATION adapterIdNotAvailable(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.adapterIdNotAvailable", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION adapterIdNotAvailable(CompletionStatus var1) {
      return this.adapterIdNotAvailable(var1, (Throwable)null);
   }

   public BAD_OPERATION adapterIdNotAvailable(Throwable var1) {
      return this.adapterIdNotAvailable(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION adapterIdNotAvailable() {
      return this.adapterIdNotAvailable(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION serverIdNotAvailable(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.serverIdNotAvailable", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION serverIdNotAvailable(CompletionStatus var1) {
      return this.serverIdNotAvailable(var1, (Throwable)null);
   }

   public BAD_OPERATION serverIdNotAvailable(Throwable var1) {
      return this.serverIdNotAvailable(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION serverIdNotAvailable() {
      return this.serverIdNotAvailable(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION orbIdNotAvailable(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079691, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.orbIdNotAvailable", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION orbIdNotAvailable(CompletionStatus var1) {
      return this.orbIdNotAvailable(var1, (Throwable)null);
   }

   public BAD_OPERATION orbIdNotAvailable(Throwable var1) {
      return this.orbIdNotAvailable(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION orbIdNotAvailable() {
      return this.orbIdNotAvailable(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION objectAdapterIdNotAvailable(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079692, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.objectAdapterIdNotAvailable", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION objectAdapterIdNotAvailable(CompletionStatus var1) {
      return this.objectAdapterIdNotAvailable(var1, (Throwable)null);
   }

   public BAD_OPERATION objectAdapterIdNotAvailable(Throwable var1) {
      return this.objectAdapterIdNotAvailable(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION objectAdapterIdNotAvailable() {
      return this.objectAdapterIdNotAvailable(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION connectingServant(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079693, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.connectingServant", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION connectingServant(CompletionStatus var1) {
      return this.connectingServant(var1, (Throwable)null);
   }

   public BAD_OPERATION connectingServant(Throwable var1) {
      return this.connectingServant(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION connectingServant() {
      return this.connectingServant(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION extractWrongType(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      BAD_OPERATION var5 = new BAD_OPERATION(1398079694, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.FINE, "ORBUTIL.extractWrongType", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public BAD_OPERATION extractWrongType(CompletionStatus var1, Object var2, Object var3) {
      return this.extractWrongType(var1, (Throwable)null, var2, var3);
   }

   public BAD_OPERATION extractWrongType(Throwable var1, Object var2, Object var3) {
      return this.extractWrongType(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public BAD_OPERATION extractWrongType(Object var1, Object var2) {
      return this.extractWrongType(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public BAD_OPERATION extractWrongTypeList(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      BAD_OPERATION var5 = new BAD_OPERATION(1398079695, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.extractWrongTypeList", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public BAD_OPERATION extractWrongTypeList(CompletionStatus var1, Object var2, Object var3) {
      return this.extractWrongTypeList(var1, (Throwable)null, var2, var3);
   }

   public BAD_OPERATION extractWrongTypeList(Throwable var1, Object var2, Object var3) {
      return this.extractWrongTypeList(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public BAD_OPERATION extractWrongTypeList(Object var1, Object var2) {
      return this.extractWrongTypeList(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public BAD_OPERATION badStringBounds(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      BAD_OPERATION var5 = new BAD_OPERATION(1398079696, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.badStringBounds", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public BAD_OPERATION badStringBounds(CompletionStatus var1, Object var2, Object var3) {
      return this.badStringBounds(var1, (Throwable)null, var2, var3);
   }

   public BAD_OPERATION badStringBounds(Throwable var1, Object var2, Object var3) {
      return this.badStringBounds(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public BAD_OPERATION badStringBounds(Object var1, Object var2) {
      return this.badStringBounds(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public BAD_OPERATION insertObjectIncompatible(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079698, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.insertObjectIncompatible", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION insertObjectIncompatible(CompletionStatus var1) {
      return this.insertObjectIncompatible(var1, (Throwable)null);
   }

   public BAD_OPERATION insertObjectIncompatible(Throwable var1) {
      return this.insertObjectIncompatible(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION insertObjectIncompatible() {
      return this.insertObjectIncompatible(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION insertObjectFailed(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079699, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.insertObjectFailed", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION insertObjectFailed(CompletionStatus var1) {
      return this.insertObjectFailed(var1, (Throwable)null);
   }

   public BAD_OPERATION insertObjectFailed(Throwable var1) {
      return this.insertObjectFailed(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION insertObjectFailed() {
      return this.insertObjectFailed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION extractObjectIncompatible(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079700, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.extractObjectIncompatible", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION extractObjectIncompatible(CompletionStatus var1) {
      return this.extractObjectIncompatible(var1, (Throwable)null);
   }

   public BAD_OPERATION extractObjectIncompatible(Throwable var1) {
      return this.extractObjectIncompatible(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION extractObjectIncompatible() {
      return this.extractObjectIncompatible(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION fixedNotMatch(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079701, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.fixedNotMatch", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION fixedNotMatch(CompletionStatus var1) {
      return this.fixedNotMatch(var1, (Throwable)null);
   }

   public BAD_OPERATION fixedNotMatch(Throwable var1) {
      return this.fixedNotMatch(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION fixedNotMatch() {
      return this.fixedNotMatch(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION fixedBadTypecode(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079702, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.fixedBadTypecode", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION fixedBadTypecode(CompletionStatus var1) {
      return this.fixedBadTypecode(var1, (Throwable)null);
   }

   public BAD_OPERATION fixedBadTypecode(Throwable var1) {
      return this.fixedBadTypecode(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION fixedBadTypecode() {
      return this.fixedBadTypecode(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION setExceptionCalledNullArgs(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079711, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.setExceptionCalledNullArgs", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION setExceptionCalledNullArgs(CompletionStatus var1) {
      return this.setExceptionCalledNullArgs(var1, (Throwable)null);
   }

   public BAD_OPERATION setExceptionCalledNullArgs(Throwable var1) {
      return this.setExceptionCalledNullArgs(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION setExceptionCalledNullArgs() {
      return this.setExceptionCalledNullArgs(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION setExceptionCalledBadType(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079712, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.setExceptionCalledBadType", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION setExceptionCalledBadType(CompletionStatus var1) {
      return this.setExceptionCalledBadType(var1, (Throwable)null);
   }

   public BAD_OPERATION setExceptionCalledBadType(Throwable var1) {
      return this.setExceptionCalledBadType(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION setExceptionCalledBadType() {
      return this.setExceptionCalledBadType(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION contextCalledOutOfOrder(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079713, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.contextCalledOutOfOrder", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION contextCalledOutOfOrder(CompletionStatus var1) {
      return this.contextCalledOutOfOrder(var1, (Throwable)null);
   }

   public BAD_OPERATION contextCalledOutOfOrder(Throwable var1) {
      return this.contextCalledOutOfOrder(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION contextCalledOutOfOrder() {
      return this.contextCalledOutOfOrder(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION badOrbConfigurator(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_OPERATION var4 = new BAD_OPERATION(1398079714, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badOrbConfigurator", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public BAD_OPERATION badOrbConfigurator(CompletionStatus var1, Object var2) {
      return this.badOrbConfigurator(var1, (Throwable)null, var2);
   }

   public BAD_OPERATION badOrbConfigurator(Throwable var1, Object var2) {
      return this.badOrbConfigurator(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_OPERATION badOrbConfigurator(Object var1) {
      return this.badOrbConfigurator(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_OPERATION orbConfiguratorError(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079715, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.orbConfiguratorError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION orbConfiguratorError(CompletionStatus var1) {
      return this.orbConfiguratorError(var1, (Throwable)null);
   }

   public BAD_OPERATION orbConfiguratorError(Throwable var1) {
      return this.orbConfiguratorError(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION orbConfiguratorError() {
      return this.orbConfiguratorError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION orbDestroyed(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079716, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.orbDestroyed", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION orbDestroyed(CompletionStatus var1) {
      return this.orbDestroyed(var1, (Throwable)null);
   }

   public BAD_OPERATION orbDestroyed(Throwable var1) {
      return this.orbDestroyed(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION orbDestroyed() {
      return this.orbDestroyed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION negativeBounds(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079717, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.negativeBounds", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION negativeBounds(CompletionStatus var1) {
      return this.negativeBounds(var1, (Throwable)null);
   }

   public BAD_OPERATION negativeBounds(Throwable var1) {
      return this.negativeBounds(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION negativeBounds() {
      return this.negativeBounds(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION extractNotInitialized(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079718, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.extractNotInitialized", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION extractNotInitialized(CompletionStatus var1) {
      return this.extractNotInitialized(var1, (Throwable)null);
   }

   public BAD_OPERATION extractNotInitialized(Throwable var1) {
      return this.extractNotInitialized(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION extractNotInitialized() {
      return this.extractNotInitialized(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION extractObjectFailed(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079719, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.extractObjectFailed", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION extractObjectFailed(CompletionStatus var1) {
      return this.extractObjectFailed(var1, (Throwable)null);
   }

   public BAD_OPERATION extractObjectFailed(Throwable var1) {
      return this.extractObjectFailed(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION extractObjectFailed() {
      return this.extractObjectFailed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION methodNotFoundInTie(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      BAD_OPERATION var5 = new BAD_OPERATION(1398079720, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.FINE, "ORBUTIL.methodNotFoundInTie", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public BAD_OPERATION methodNotFoundInTie(CompletionStatus var1, Object var2, Object var3) {
      return this.methodNotFoundInTie(var1, (Throwable)null, var2, var3);
   }

   public BAD_OPERATION methodNotFoundInTie(Throwable var1, Object var2, Object var3) {
      return this.methodNotFoundInTie(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public BAD_OPERATION methodNotFoundInTie(Object var1, Object var2) {
      return this.methodNotFoundInTie(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public BAD_OPERATION classNotFound1(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_OPERATION var4 = new BAD_OPERATION(1398079721, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.FINE, "ORBUTIL.classNotFound1", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public BAD_OPERATION classNotFound1(CompletionStatus var1, Object var2) {
      return this.classNotFound1(var1, (Throwable)null, var2);
   }

   public BAD_OPERATION classNotFound1(Throwable var1, Object var2) {
      return this.classNotFound1(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_OPERATION classNotFound1(Object var1) {
      return this.classNotFound1(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_OPERATION classNotFound2(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_OPERATION var4 = new BAD_OPERATION(1398079722, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.FINE, "ORBUTIL.classNotFound2", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public BAD_OPERATION classNotFound2(CompletionStatus var1, Object var2) {
      return this.classNotFound2(var1, (Throwable)null, var2);
   }

   public BAD_OPERATION classNotFound2(Throwable var1, Object var2) {
      return this.classNotFound2(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_OPERATION classNotFound2(Object var1) {
      return this.classNotFound2(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_OPERATION classNotFound3(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_OPERATION var4 = new BAD_OPERATION(1398079723, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.FINE, "ORBUTIL.classNotFound3", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public BAD_OPERATION classNotFound3(CompletionStatus var1, Object var2) {
      return this.classNotFound3(var1, (Throwable)null, var2);
   }

   public BAD_OPERATION classNotFound3(Throwable var1, Object var2) {
      return this.classNotFound3(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_OPERATION classNotFound3(Object var1) {
      return this.classNotFound3(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_OPERATION getDelegateServantNotActive(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079724, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.getDelegateServantNotActive", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION getDelegateServantNotActive(CompletionStatus var1) {
      return this.getDelegateServantNotActive(var1, (Throwable)null);
   }

   public BAD_OPERATION getDelegateServantNotActive(Throwable var1) {
      return this.getDelegateServantNotActive(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION getDelegateServantNotActive() {
      return this.getDelegateServantNotActive(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION getDelegateWrongPolicy(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079725, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.getDelegateWrongPolicy", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION getDelegateWrongPolicy(CompletionStatus var1) {
      return this.getDelegateWrongPolicy(var1, (Throwable)null);
   }

   public BAD_OPERATION getDelegateWrongPolicy(Throwable var1) {
      return this.getDelegateWrongPolicy(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION getDelegateWrongPolicy() {
      return this.getDelegateWrongPolicy(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION setDelegateRequiresStub(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079726, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.setDelegateRequiresStub", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION setDelegateRequiresStub(CompletionStatus var1) {
      return this.setDelegateRequiresStub(var1, (Throwable)null);
   }

   public BAD_OPERATION setDelegateRequiresStub(Throwable var1) {
      return this.setDelegateRequiresStub(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION setDelegateRequiresStub() {
      return this.setDelegateRequiresStub(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION getDelegateRequiresStub(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079727, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.getDelegateRequiresStub", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION getDelegateRequiresStub(CompletionStatus var1) {
      return this.getDelegateRequiresStub(var1, (Throwable)null);
   }

   public BAD_OPERATION getDelegateRequiresStub(Throwable var1) {
      return this.getDelegateRequiresStub(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION getDelegateRequiresStub() {
      return this.getDelegateRequiresStub(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION getTypeIdsRequiresStub(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079728, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.getTypeIdsRequiresStub", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION getTypeIdsRequiresStub(CompletionStatus var1) {
      return this.getTypeIdsRequiresStub(var1, (Throwable)null);
   }

   public BAD_OPERATION getTypeIdsRequiresStub(Throwable var1) {
      return this.getTypeIdsRequiresStub(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION getTypeIdsRequiresStub() {
      return this.getTypeIdsRequiresStub(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION getOrbRequiresStub(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079729, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.getOrbRequiresStub", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION getOrbRequiresStub(CompletionStatus var1) {
      return this.getOrbRequiresStub(var1, (Throwable)null);
   }

   public BAD_OPERATION getOrbRequiresStub(Throwable var1) {
      return this.getOrbRequiresStub(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION getOrbRequiresStub() {
      return this.getOrbRequiresStub(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION connectRequiresStub(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079730, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.connectRequiresStub", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION connectRequiresStub(CompletionStatus var1) {
      return this.connectRequiresStub(var1, (Throwable)null);
   }

   public BAD_OPERATION connectRequiresStub(Throwable var1) {
      return this.connectRequiresStub(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION connectRequiresStub() {
      return this.connectRequiresStub(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION isLocalRequiresStub(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079731, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.isLocalRequiresStub", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION isLocalRequiresStub(CompletionStatus var1) {
      return this.isLocalRequiresStub(var1, (Throwable)null);
   }

   public BAD_OPERATION isLocalRequiresStub(Throwable var1) {
      return this.isLocalRequiresStub(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION isLocalRequiresStub() {
      return this.isLocalRequiresStub(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION requestRequiresStub(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079732, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.requestRequiresStub", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION requestRequiresStub(CompletionStatus var1) {
      return this.requestRequiresStub(var1, (Throwable)null);
   }

   public BAD_OPERATION requestRequiresStub(Throwable var1) {
      return this.requestRequiresStub(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION requestRequiresStub() {
      return this.requestRequiresStub(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION badActivateTieCall(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079733, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badActivateTieCall", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION badActivateTieCall(CompletionStatus var1) {
      return this.badActivateTieCall(var1, (Throwable)null);
   }

   public BAD_OPERATION badActivateTieCall(Throwable var1) {
      return this.badActivateTieCall(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION badActivateTieCall() {
      return this.badActivateTieCall(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_OPERATION ioExceptionOnClose(CompletionStatus var1, Throwable var2) {
      BAD_OPERATION var3 = new BAD_OPERATION(1398079734, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.ioExceptionOnClose", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_OPERATION ioExceptionOnClose(CompletionStatus var1) {
      return this.ioExceptionOnClose(var1, (Throwable)null);
   }

   public BAD_OPERATION ioExceptionOnClose(Throwable var1) {
      return this.ioExceptionOnClose(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_OPERATION ioExceptionOnClose() {
      return this.ioExceptionOnClose(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM nullParam(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.nullParam", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM nullParam(CompletionStatus var1) {
      return this.nullParam(var1, (Throwable)null);
   }

   public BAD_PARAM nullParam(Throwable var1) {
      return this.nullParam(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM nullParam() {
      return this.nullParam(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM unableFindValueFactory(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.unableFindValueFactory", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM unableFindValueFactory(CompletionStatus var1) {
      return this.unableFindValueFactory(var1, (Throwable)null);
   }

   public BAD_PARAM unableFindValueFactory(Throwable var1) {
      return this.unableFindValueFactory(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM unableFindValueFactory() {
      return this.unableFindValueFactory(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM abstractFromNonAbstract(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079691, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.abstractFromNonAbstract", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM abstractFromNonAbstract(CompletionStatus var1) {
      return this.abstractFromNonAbstract(var1, (Throwable)null);
   }

   public BAD_PARAM abstractFromNonAbstract(Throwable var1) {
      return this.abstractFromNonAbstract(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM abstractFromNonAbstract() {
      return this.abstractFromNonAbstract(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM invalidTaggedProfile(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079692, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.invalidTaggedProfile", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM invalidTaggedProfile(CompletionStatus var1) {
      return this.invalidTaggedProfile(var1, (Throwable)null);
   }

   public BAD_PARAM invalidTaggedProfile(Throwable var1) {
      return this.invalidTaggedProfile(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM invalidTaggedProfile() {
      return this.invalidTaggedProfile(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM objrefFromForeignOrb(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079693, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.objrefFromForeignOrb", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM objrefFromForeignOrb(CompletionStatus var1) {
      return this.objrefFromForeignOrb(var1, (Throwable)null);
   }

   public BAD_PARAM objrefFromForeignOrb(Throwable var1) {
      return this.objrefFromForeignOrb(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM objrefFromForeignOrb() {
      return this.objrefFromForeignOrb(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM localObjectNotAllowed(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079694, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.localObjectNotAllowed", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM localObjectNotAllowed(CompletionStatus var1) {
      return this.localObjectNotAllowed(var1, (Throwable)null);
   }

   public BAD_PARAM localObjectNotAllowed(Throwable var1) {
      return this.localObjectNotAllowed(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM localObjectNotAllowed() {
      return this.localObjectNotAllowed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM nullObjectReference(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079695, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.nullObjectReference", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM nullObjectReference(CompletionStatus var1) {
      return this.nullObjectReference(var1, (Throwable)null);
   }

   public BAD_PARAM nullObjectReference(Throwable var1) {
      return this.nullObjectReference(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM nullObjectReference() {
      return this.nullObjectReference(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM couldNotLoadClass(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_PARAM var4 = new BAD_PARAM(1398079696, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.couldNotLoadClass", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public BAD_PARAM couldNotLoadClass(CompletionStatus var1, Object var2) {
      return this.couldNotLoadClass(var1, (Throwable)null, var2);
   }

   public BAD_PARAM couldNotLoadClass(Throwable var1, Object var2) {
      return this.couldNotLoadClass(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_PARAM couldNotLoadClass(Object var1) {
      return this.couldNotLoadClass(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_PARAM badUrl(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_PARAM var4 = new BAD_PARAM(1398079697, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badUrl", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public BAD_PARAM badUrl(CompletionStatus var1, Object var2) {
      return this.badUrl(var1, (Throwable)null, var2);
   }

   public BAD_PARAM badUrl(Throwable var1, Object var2) {
      return this.badUrl(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_PARAM badUrl(Object var1) {
      return this.badUrl(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_PARAM fieldNotFound(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_PARAM var4 = new BAD_PARAM(1398079698, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.fieldNotFound", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public BAD_PARAM fieldNotFound(CompletionStatus var1, Object var2) {
      return this.fieldNotFound(var1, (Throwable)null, var2);
   }

   public BAD_PARAM fieldNotFound(Throwable var1, Object var2) {
      return this.fieldNotFound(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_PARAM fieldNotFound(Object var1) {
      return this.fieldNotFound(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_PARAM errorSettingField(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      BAD_PARAM var5 = new BAD_PARAM(1398079699, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.errorSettingField", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public BAD_PARAM errorSettingField(CompletionStatus var1, Object var2, Object var3) {
      return this.errorSettingField(var1, (Throwable)null, var2, var3);
   }

   public BAD_PARAM errorSettingField(Throwable var1, Object var2, Object var3) {
      return this.errorSettingField(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public BAD_PARAM errorSettingField(Object var1, Object var2) {
      return this.errorSettingField(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public BAD_PARAM boundsErrorInDiiRequest(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079700, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.boundsErrorInDiiRequest", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM boundsErrorInDiiRequest(CompletionStatus var1) {
      return this.boundsErrorInDiiRequest(var1, (Throwable)null);
   }

   public BAD_PARAM boundsErrorInDiiRequest(Throwable var1) {
      return this.boundsErrorInDiiRequest(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM boundsErrorInDiiRequest() {
      return this.boundsErrorInDiiRequest(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM persistentServerInitError(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079701, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.persistentServerInitError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM persistentServerInitError(CompletionStatus var1) {
      return this.persistentServerInitError(var1, (Throwable)null);
   }

   public BAD_PARAM persistentServerInitError(Throwable var1) {
      return this.persistentServerInitError(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM persistentServerInitError() {
      return this.persistentServerInitError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM couldNotCreateArray(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      BAD_PARAM var6 = new BAD_PARAM(1398079702, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "ORBUTIL.couldNotCreateArray", var7, ORBUtilSystemException.class, var6);
      }

      return var6;
   }

   public BAD_PARAM couldNotCreateArray(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.couldNotCreateArray(var1, (Throwable)null, var2, var3, var4);
   }

   public BAD_PARAM couldNotCreateArray(Throwable var1, Object var2, Object var3, Object var4) {
      return this.couldNotCreateArray(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public BAD_PARAM couldNotCreateArray(Object var1, Object var2, Object var3) {
      return this.couldNotCreateArray(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public BAD_PARAM couldNotSetArray(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      BAD_PARAM var8 = new BAD_PARAM(1398079703, var1);
      if (var2 != null) {
         var8.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var9 = new Object[]{var3, var4, var5, var6, var7};
         this.doLog(Level.WARNING, "ORBUTIL.couldNotSetArray", var9, ORBUtilSystemException.class, var8);
      }

      return var8;
   }

   public BAD_PARAM couldNotSetArray(CompletionStatus var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      return this.couldNotSetArray(var1, (Throwable)null, var2, var3, var4, var5, var6);
   }

   public BAD_PARAM couldNotSetArray(Throwable var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      return this.couldNotSetArray(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4, var5, var6);
   }

   public BAD_PARAM couldNotSetArray(Object var1, Object var2, Object var3, Object var4, Object var5) {
      return this.couldNotSetArray(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3, var4, var5);
   }

   public BAD_PARAM illegalBootstrapOperation(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_PARAM var4 = new BAD_PARAM(1398079704, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.illegalBootstrapOperation", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public BAD_PARAM illegalBootstrapOperation(CompletionStatus var1, Object var2) {
      return this.illegalBootstrapOperation(var1, (Throwable)null, var2);
   }

   public BAD_PARAM illegalBootstrapOperation(Throwable var1, Object var2) {
      return this.illegalBootstrapOperation(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_PARAM illegalBootstrapOperation(Object var1) {
      return this.illegalBootstrapOperation(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_PARAM bootstrapRuntimeException(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079705, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.bootstrapRuntimeException", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM bootstrapRuntimeException(CompletionStatus var1) {
      return this.bootstrapRuntimeException(var1, (Throwable)null);
   }

   public BAD_PARAM bootstrapRuntimeException(Throwable var1) {
      return this.bootstrapRuntimeException(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM bootstrapRuntimeException() {
      return this.bootstrapRuntimeException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM bootstrapException(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079706, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.bootstrapException", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM bootstrapException(CompletionStatus var1) {
      return this.bootstrapException(var1, (Throwable)null);
   }

   public BAD_PARAM bootstrapException(Throwable var1) {
      return this.bootstrapException(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM bootstrapException() {
      return this.bootstrapException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM stringExpected(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079707, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.stringExpected", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM stringExpected(CompletionStatus var1) {
      return this.stringExpected(var1, (Throwable)null);
   }

   public BAD_PARAM stringExpected(Throwable var1) {
      return this.stringExpected(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM stringExpected() {
      return this.stringExpected(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM invalidTypecodeKind(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_PARAM var4 = new BAD_PARAM(1398079708, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.invalidTypecodeKind", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public BAD_PARAM invalidTypecodeKind(CompletionStatus var1, Object var2) {
      return this.invalidTypecodeKind(var1, (Throwable)null, var2);
   }

   public BAD_PARAM invalidTypecodeKind(Throwable var1, Object var2) {
      return this.invalidTypecodeKind(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_PARAM invalidTypecodeKind(Object var1) {
      return this.invalidTypecodeKind(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_PARAM socketFactoryAndContactInfoListAtSameTime(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079709, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.socketFactoryAndContactInfoListAtSameTime", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM socketFactoryAndContactInfoListAtSameTime(CompletionStatus var1) {
      return this.socketFactoryAndContactInfoListAtSameTime(var1, (Throwable)null);
   }

   public BAD_PARAM socketFactoryAndContactInfoListAtSameTime(Throwable var1) {
      return this.socketFactoryAndContactInfoListAtSameTime(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM socketFactoryAndContactInfoListAtSameTime() {
      return this.socketFactoryAndContactInfoListAtSameTime(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM acceptorsAndLegacySocketFactoryAtSameTime(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079710, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.acceptorsAndLegacySocketFactoryAtSameTime", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM acceptorsAndLegacySocketFactoryAtSameTime(CompletionStatus var1) {
      return this.acceptorsAndLegacySocketFactoryAtSameTime(var1, (Throwable)null);
   }

   public BAD_PARAM acceptorsAndLegacySocketFactoryAtSameTime(Throwable var1) {
      return this.acceptorsAndLegacySocketFactoryAtSameTime(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM acceptorsAndLegacySocketFactoryAtSameTime() {
      return this.acceptorsAndLegacySocketFactoryAtSameTime(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM badOrbForServant(CompletionStatus var1, Throwable var2) {
      BAD_PARAM var3 = new BAD_PARAM(1398079711, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badOrbForServant", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_PARAM badOrbForServant(CompletionStatus var1) {
      return this.badOrbForServant(var1, (Throwable)null);
   }

   public BAD_PARAM badOrbForServant(Throwable var1) {
      return this.badOrbForServant(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_PARAM badOrbForServant() {
      return this.badOrbForServant(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_PARAM invalidRequestPartitioningPolicyValue(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      BAD_PARAM var6 = new BAD_PARAM(1398079712, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "ORBUTIL.invalidRequestPartitioningPolicyValue", var7, ORBUtilSystemException.class, var6);
      }

      return var6;
   }

   public BAD_PARAM invalidRequestPartitioningPolicyValue(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.invalidRequestPartitioningPolicyValue(var1, (Throwable)null, var2, var3, var4);
   }

   public BAD_PARAM invalidRequestPartitioningPolicyValue(Throwable var1, Object var2, Object var3, Object var4) {
      return this.invalidRequestPartitioningPolicyValue(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public BAD_PARAM invalidRequestPartitioningPolicyValue(Object var1, Object var2, Object var3) {
      return this.invalidRequestPartitioningPolicyValue(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public BAD_PARAM invalidRequestPartitioningComponentValue(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      BAD_PARAM var6 = new BAD_PARAM(1398079713, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "ORBUTIL.invalidRequestPartitioningComponentValue", var7, ORBUtilSystemException.class, var6);
      }

      return var6;
   }

   public BAD_PARAM invalidRequestPartitioningComponentValue(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.invalidRequestPartitioningComponentValue(var1, (Throwable)null, var2, var3, var4);
   }

   public BAD_PARAM invalidRequestPartitioningComponentValue(Throwable var1, Object var2, Object var3, Object var4) {
      return this.invalidRequestPartitioningComponentValue(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public BAD_PARAM invalidRequestPartitioningComponentValue(Object var1, Object var2, Object var3) {
      return this.invalidRequestPartitioningComponentValue(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public BAD_PARAM invalidRequestPartitioningId(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      BAD_PARAM var6 = new BAD_PARAM(1398079714, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "ORBUTIL.invalidRequestPartitioningId", var7, ORBUtilSystemException.class, var6);
      }

      return var6;
   }

   public BAD_PARAM invalidRequestPartitioningId(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.invalidRequestPartitioningId(var1, (Throwable)null, var2, var3, var4);
   }

   public BAD_PARAM invalidRequestPartitioningId(Throwable var1, Object var2, Object var3, Object var4) {
      return this.invalidRequestPartitioningId(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public BAD_PARAM invalidRequestPartitioningId(Object var1, Object var2, Object var3) {
      return this.invalidRequestPartitioningId(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public BAD_PARAM errorInSettingDynamicStubFactoryFactory(CompletionStatus var1, Throwable var2, Object var3) {
      BAD_PARAM var4 = new BAD_PARAM(1398079715, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.FINE, "ORBUTIL.errorInSettingDynamicStubFactoryFactory", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public BAD_PARAM errorInSettingDynamicStubFactoryFactory(CompletionStatus var1, Object var2) {
      return this.errorInSettingDynamicStubFactoryFactory(var1, (Throwable)null, var2);
   }

   public BAD_PARAM errorInSettingDynamicStubFactoryFactory(Throwable var1, Object var2) {
      return this.errorInSettingDynamicStubFactoryFactory(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public BAD_PARAM errorInSettingDynamicStubFactoryFactory(Object var1) {
      return this.errorInSettingDynamicStubFactoryFactory(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public BAD_INV_ORDER dsimethodNotcalled(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.dsimethodNotcalled", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER dsimethodNotcalled(CompletionStatus var1) {
      return this.dsimethodNotcalled(var1, (Throwable)null);
   }

   public BAD_INV_ORDER dsimethodNotcalled(Throwable var1) {
      return this.dsimethodNotcalled(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER dsimethodNotcalled() {
      return this.dsimethodNotcalled(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER argumentsCalledMultiple(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.argumentsCalledMultiple", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER argumentsCalledMultiple(CompletionStatus var1) {
      return this.argumentsCalledMultiple(var1, (Throwable)null);
   }

   public BAD_INV_ORDER argumentsCalledMultiple(Throwable var1) {
      return this.argumentsCalledMultiple(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER argumentsCalledMultiple() {
      return this.argumentsCalledMultiple(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER argumentsCalledAfterException(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1398079691, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.argumentsCalledAfterException", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER argumentsCalledAfterException(CompletionStatus var1) {
      return this.argumentsCalledAfterException(var1, (Throwable)null);
   }

   public BAD_INV_ORDER argumentsCalledAfterException(Throwable var1) {
      return this.argumentsCalledAfterException(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER argumentsCalledAfterException() {
      return this.argumentsCalledAfterException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER argumentsCalledNullArgs(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1398079692, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.argumentsCalledNullArgs", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER argumentsCalledNullArgs(CompletionStatus var1) {
      return this.argumentsCalledNullArgs(var1, (Throwable)null);
   }

   public BAD_INV_ORDER argumentsCalledNullArgs(Throwable var1) {
      return this.argumentsCalledNullArgs(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER argumentsCalledNullArgs() {
      return this.argumentsCalledNullArgs(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER argumentsNotCalled(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1398079693, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.argumentsNotCalled", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER argumentsNotCalled(CompletionStatus var1) {
      return this.argumentsNotCalled(var1, (Throwable)null);
   }

   public BAD_INV_ORDER argumentsNotCalled(Throwable var1) {
      return this.argumentsNotCalled(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER argumentsNotCalled() {
      return this.argumentsNotCalled(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER setResultCalledMultiple(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1398079694, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.setResultCalledMultiple", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER setResultCalledMultiple(CompletionStatus var1) {
      return this.setResultCalledMultiple(var1, (Throwable)null);
   }

   public BAD_INV_ORDER setResultCalledMultiple(Throwable var1) {
      return this.setResultCalledMultiple(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER setResultCalledMultiple() {
      return this.setResultCalledMultiple(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER setResultAfterException(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1398079695, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.setResultAfterException", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER setResultAfterException(CompletionStatus var1) {
      return this.setResultAfterException(var1, (Throwable)null);
   }

   public BAD_INV_ORDER setResultAfterException(Throwable var1) {
      return this.setResultAfterException(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER setResultAfterException() {
      return this.setResultAfterException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_INV_ORDER setResultCalledNullArgs(CompletionStatus var1, Throwable var2) {
      BAD_INV_ORDER var3 = new BAD_INV_ORDER(1398079696, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.setResultCalledNullArgs", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_INV_ORDER setResultCalledNullArgs(CompletionStatus var1) {
      return this.setResultCalledNullArgs(var1, (Throwable)null);
   }

   public BAD_INV_ORDER setResultCalledNullArgs(Throwable var1) {
      return this.setResultCalledNullArgs(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_INV_ORDER setResultCalledNullArgs() {
      return this.setResultCalledNullArgs(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_TYPECODE badRemoteTypecode(CompletionStatus var1, Throwable var2) {
      BAD_TYPECODE var3 = new BAD_TYPECODE(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badRemoteTypecode", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_TYPECODE badRemoteTypecode(CompletionStatus var1) {
      return this.badRemoteTypecode(var1, (Throwable)null);
   }

   public BAD_TYPECODE badRemoteTypecode(Throwable var1) {
      return this.badRemoteTypecode(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_TYPECODE badRemoteTypecode() {
      return this.badRemoteTypecode(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public BAD_TYPECODE unresolvedRecursiveTypecode(CompletionStatus var1, Throwable var2) {
      BAD_TYPECODE var3 = new BAD_TYPECODE(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.unresolvedRecursiveTypecode", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public BAD_TYPECODE unresolvedRecursiveTypecode(CompletionStatus var1) {
      return this.unresolvedRecursiveTypecode(var1, (Throwable)null);
   }

   public BAD_TYPECODE unresolvedRecursiveTypecode(Throwable var1) {
      return this.unresolvedRecursiveTypecode(CompletionStatus.COMPLETED_NO, var1);
   }

   public BAD_TYPECODE unresolvedRecursiveTypecode() {
      return this.unresolvedRecursiveTypecode(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public COMM_FAILURE connectFailure(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      COMM_FAILURE var6 = new COMM_FAILURE(1398079689, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "ORBUTIL.connectFailure", var7, ORBUtilSystemException.class, var6);
      }

      return var6;
   }

   public COMM_FAILURE connectFailure(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.connectFailure(var1, (Throwable)null, var2, var3, var4);
   }

   public COMM_FAILURE connectFailure(Throwable var1, Object var2, Object var3, Object var4) {
      return this.connectFailure(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public COMM_FAILURE connectFailure(Object var1, Object var2, Object var3) {
      return this.connectFailure(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public COMM_FAILURE connectionCloseRebind(CompletionStatus var1, Throwable var2) {
      COMM_FAILURE var3 = new COMM_FAILURE(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.connectionCloseRebind", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public COMM_FAILURE connectionCloseRebind(CompletionStatus var1) {
      return this.connectionCloseRebind(var1, (Throwable)null);
   }

   public COMM_FAILURE connectionCloseRebind(Throwable var1) {
      return this.connectionCloseRebind(CompletionStatus.COMPLETED_NO, var1);
   }

   public COMM_FAILURE connectionCloseRebind() {
      return this.connectionCloseRebind(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public COMM_FAILURE writeErrorSend(CompletionStatus var1, Throwable var2) {
      COMM_FAILURE var3 = new COMM_FAILURE(1398079691, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.writeErrorSend", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public COMM_FAILURE writeErrorSend(CompletionStatus var1) {
      return this.writeErrorSend(var1, (Throwable)null);
   }

   public COMM_FAILURE writeErrorSend(Throwable var1) {
      return this.writeErrorSend(CompletionStatus.COMPLETED_NO, var1);
   }

   public COMM_FAILURE writeErrorSend() {
      return this.writeErrorSend(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public COMM_FAILURE getPropertiesError(CompletionStatus var1, Throwable var2) {
      COMM_FAILURE var3 = new COMM_FAILURE(1398079692, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.getPropertiesError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public COMM_FAILURE getPropertiesError(CompletionStatus var1) {
      return this.getPropertiesError(var1, (Throwable)null);
   }

   public COMM_FAILURE getPropertiesError(Throwable var1) {
      return this.getPropertiesError(CompletionStatus.COMPLETED_NO, var1);
   }

   public COMM_FAILURE getPropertiesError() {
      return this.getPropertiesError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public COMM_FAILURE bootstrapServerNotAvail(CompletionStatus var1, Throwable var2) {
      COMM_FAILURE var3 = new COMM_FAILURE(1398079693, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.bootstrapServerNotAvail", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public COMM_FAILURE bootstrapServerNotAvail(CompletionStatus var1) {
      return this.bootstrapServerNotAvail(var1, (Throwable)null);
   }

   public COMM_FAILURE bootstrapServerNotAvail(Throwable var1) {
      return this.bootstrapServerNotAvail(CompletionStatus.COMPLETED_NO, var1);
   }

   public COMM_FAILURE bootstrapServerNotAvail() {
      return this.bootstrapServerNotAvail(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public COMM_FAILURE invokeError(CompletionStatus var1, Throwable var2) {
      COMM_FAILURE var3 = new COMM_FAILURE(1398079694, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.invokeError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public COMM_FAILURE invokeError(CompletionStatus var1) {
      return this.invokeError(var1, (Throwable)null);
   }

   public COMM_FAILURE invokeError(Throwable var1) {
      return this.invokeError(CompletionStatus.COMPLETED_NO, var1);
   }

   public COMM_FAILURE invokeError() {
      return this.invokeError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public COMM_FAILURE defaultCreateServerSocketGivenNonIiopClearText(CompletionStatus var1, Throwable var2, Object var3) {
      COMM_FAILURE var4 = new COMM_FAILURE(1398079695, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.defaultCreateServerSocketGivenNonIiopClearText", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public COMM_FAILURE defaultCreateServerSocketGivenNonIiopClearText(CompletionStatus var1, Object var2) {
      return this.defaultCreateServerSocketGivenNonIiopClearText(var1, (Throwable)null, var2);
   }

   public COMM_FAILURE defaultCreateServerSocketGivenNonIiopClearText(Throwable var1, Object var2) {
      return this.defaultCreateServerSocketGivenNonIiopClearText(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public COMM_FAILURE defaultCreateServerSocketGivenNonIiopClearText(Object var1) {
      return this.defaultCreateServerSocketGivenNonIiopClearText(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public COMM_FAILURE connectionAbort(CompletionStatus var1, Throwable var2) {
      COMM_FAILURE var3 = new COMM_FAILURE(1398079696, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.connectionAbort", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public COMM_FAILURE connectionAbort(CompletionStatus var1) {
      return this.connectionAbort(var1, (Throwable)null);
   }

   public COMM_FAILURE connectionAbort(Throwable var1) {
      return this.connectionAbort(CompletionStatus.COMPLETED_NO, var1);
   }

   public COMM_FAILURE connectionAbort() {
      return this.connectionAbort(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public COMM_FAILURE connectionRebind(CompletionStatus var1, Throwable var2) {
      COMM_FAILURE var3 = new COMM_FAILURE(1398079697, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.connectionRebind", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public COMM_FAILURE connectionRebind(CompletionStatus var1) {
      return this.connectionRebind(var1, (Throwable)null);
   }

   public COMM_FAILURE connectionRebind(Throwable var1) {
      return this.connectionRebind(CompletionStatus.COMPLETED_NO, var1);
   }

   public COMM_FAILURE connectionRebind() {
      return this.connectionRebind(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public COMM_FAILURE recvMsgError(CompletionStatus var1, Throwable var2) {
      COMM_FAILURE var3 = new COMM_FAILURE(1398079698, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.recvMsgError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public COMM_FAILURE recvMsgError(CompletionStatus var1) {
      return this.recvMsgError(var1, (Throwable)null);
   }

   public COMM_FAILURE recvMsgError(Throwable var1) {
      return this.recvMsgError(CompletionStatus.COMPLETED_NO, var1);
   }

   public COMM_FAILURE recvMsgError() {
      return this.recvMsgError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public COMM_FAILURE ioexceptionWhenReadingConnection(CompletionStatus var1, Throwable var2) {
      COMM_FAILURE var3 = new COMM_FAILURE(1398079699, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.ioexceptionWhenReadingConnection", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public COMM_FAILURE ioexceptionWhenReadingConnection(CompletionStatus var1) {
      return this.ioexceptionWhenReadingConnection(var1, (Throwable)null);
   }

   public COMM_FAILURE ioexceptionWhenReadingConnection(Throwable var1) {
      return this.ioexceptionWhenReadingConnection(CompletionStatus.COMPLETED_NO, var1);
   }

   public COMM_FAILURE ioexceptionWhenReadingConnection() {
      return this.ioexceptionWhenReadingConnection(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public COMM_FAILURE selectionKeyInvalid(CompletionStatus var1, Throwable var2, Object var3) {
      COMM_FAILURE var4 = new COMM_FAILURE(1398079700, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.FINE, "ORBUTIL.selectionKeyInvalid", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public COMM_FAILURE selectionKeyInvalid(CompletionStatus var1, Object var2) {
      return this.selectionKeyInvalid(var1, (Throwable)null, var2);
   }

   public COMM_FAILURE selectionKeyInvalid(Throwable var1, Object var2) {
      return this.selectionKeyInvalid(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public COMM_FAILURE selectionKeyInvalid(Object var1) {
      return this.selectionKeyInvalid(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public COMM_FAILURE exceptionInAccept(CompletionStatus var1, Throwable var2, Object var3) {
      COMM_FAILURE var4 = new COMM_FAILURE(1398079701, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.FINE, "ORBUTIL.exceptionInAccept", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public COMM_FAILURE exceptionInAccept(CompletionStatus var1, Object var2) {
      return this.exceptionInAccept(var1, (Throwable)null, var2);
   }

   public COMM_FAILURE exceptionInAccept(Throwable var1, Object var2) {
      return this.exceptionInAccept(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public COMM_FAILURE exceptionInAccept(Object var1) {
      return this.exceptionInAccept(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public COMM_FAILURE securityExceptionInAccept(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      COMM_FAILURE var5 = new COMM_FAILURE(1398079702, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.FINE, "ORBUTIL.securityExceptionInAccept", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public COMM_FAILURE securityExceptionInAccept(CompletionStatus var1, Object var2, Object var3) {
      return this.securityExceptionInAccept(var1, (Throwable)null, var2, var3);
   }

   public COMM_FAILURE securityExceptionInAccept(Throwable var1, Object var2, Object var3) {
      return this.securityExceptionInAccept(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public COMM_FAILURE securityExceptionInAccept(Object var1, Object var2) {
      return this.securityExceptionInAccept(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public COMM_FAILURE transportReadTimeoutExceeded(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5, Object var6) {
      COMM_FAILURE var7 = new COMM_FAILURE(1398079703, var1);
      if (var2 != null) {
         var7.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var8 = new Object[]{var3, var4, var5, var6};
         this.doLog(Level.WARNING, "ORBUTIL.transportReadTimeoutExceeded", var8, ORBUtilSystemException.class, var7);
      }

      return var7;
   }

   public COMM_FAILURE transportReadTimeoutExceeded(CompletionStatus var1, Object var2, Object var3, Object var4, Object var5) {
      return this.transportReadTimeoutExceeded(var1, (Throwable)null, var2, var3, var4, var5);
   }

   public COMM_FAILURE transportReadTimeoutExceeded(Throwable var1, Object var2, Object var3, Object var4, Object var5) {
      return this.transportReadTimeoutExceeded(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4, var5);
   }

   public COMM_FAILURE transportReadTimeoutExceeded(Object var1, Object var2, Object var3, Object var4) {
      return this.transportReadTimeoutExceeded(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3, var4);
   }

   public COMM_FAILURE createListenerFailed(CompletionStatus var1, Throwable var2, Object var3) {
      COMM_FAILURE var4 = new COMM_FAILURE(1398079704, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.SEVERE)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.SEVERE, "ORBUTIL.createListenerFailed", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public COMM_FAILURE createListenerFailed(CompletionStatus var1, Object var2) {
      return this.createListenerFailed(var1, (Throwable)null, var2);
   }

   public COMM_FAILURE createListenerFailed(Throwable var1, Object var2) {
      return this.createListenerFailed(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public COMM_FAILURE createListenerFailed(Object var1) {
      return this.createListenerFailed(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public COMM_FAILURE bufferReadManagerTimeout(CompletionStatus var1, Throwable var2) {
      COMM_FAILURE var3 = new COMM_FAILURE(1398079705, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.bufferReadManagerTimeout", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public COMM_FAILURE bufferReadManagerTimeout(CompletionStatus var1) {
      return this.bufferReadManagerTimeout(var1, (Throwable)null);
   }

   public COMM_FAILURE bufferReadManagerTimeout(Throwable var1) {
      return this.bufferReadManagerTimeout(CompletionStatus.COMPLETED_NO, var1);
   }

   public COMM_FAILURE bufferReadManagerTimeout() {
      return this.bufferReadManagerTimeout(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION badStringifiedIorLen(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badStringifiedIorLen", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION badStringifiedIorLen(CompletionStatus var1) {
      return this.badStringifiedIorLen(var1, (Throwable)null);
   }

   public DATA_CONVERSION badStringifiedIorLen(Throwable var1) {
      return this.badStringifiedIorLen(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION badStringifiedIorLen() {
      return this.badStringifiedIorLen(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION badStringifiedIor(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badStringifiedIor", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION badStringifiedIor(CompletionStatus var1) {
      return this.badStringifiedIor(var1, (Throwable)null);
   }

   public DATA_CONVERSION badStringifiedIor(Throwable var1) {
      return this.badStringifiedIor(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION badStringifiedIor() {
      return this.badStringifiedIor(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION badModifier(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1398079691, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badModifier", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION badModifier(CompletionStatus var1) {
      return this.badModifier(var1, (Throwable)null);
   }

   public DATA_CONVERSION badModifier(Throwable var1) {
      return this.badModifier(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION badModifier() {
      return this.badModifier(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION codesetIncompatible(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1398079692, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.codesetIncompatible", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION codesetIncompatible(CompletionStatus var1) {
      return this.codesetIncompatible(var1, (Throwable)null);
   }

   public DATA_CONVERSION codesetIncompatible(Throwable var1) {
      return this.codesetIncompatible(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION codesetIncompatible() {
      return this.codesetIncompatible(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION badHexDigit(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1398079693, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badHexDigit", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION badHexDigit(CompletionStatus var1) {
      return this.badHexDigit(var1, (Throwable)null);
   }

   public DATA_CONVERSION badHexDigit(Throwable var1) {
      return this.badHexDigit(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION badHexDigit() {
      return this.badHexDigit(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION badUnicodePair(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1398079694, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badUnicodePair", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION badUnicodePair(CompletionStatus var1) {
      return this.badUnicodePair(var1, (Throwable)null);
   }

   public DATA_CONVERSION badUnicodePair(Throwable var1) {
      return this.badUnicodePair(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION badUnicodePair() {
      return this.badUnicodePair(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION btcResultMoreThanOneChar(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1398079695, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.btcResultMoreThanOneChar", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION btcResultMoreThanOneChar(CompletionStatus var1) {
      return this.btcResultMoreThanOneChar(var1, (Throwable)null);
   }

   public DATA_CONVERSION btcResultMoreThanOneChar(Throwable var1) {
      return this.btcResultMoreThanOneChar(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION btcResultMoreThanOneChar() {
      return this.btcResultMoreThanOneChar(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION badCodesetsFromClient(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1398079696, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badCodesetsFromClient", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION badCodesetsFromClient(CompletionStatus var1) {
      return this.badCodesetsFromClient(var1, (Throwable)null);
   }

   public DATA_CONVERSION badCodesetsFromClient(Throwable var1) {
      return this.badCodesetsFromClient(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION badCodesetsFromClient() {
      return this.badCodesetsFromClient(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION invalidSingleCharCtb(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1398079697, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.invalidSingleCharCtb", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION invalidSingleCharCtb(CompletionStatus var1) {
      return this.invalidSingleCharCtb(var1, (Throwable)null);
   }

   public DATA_CONVERSION invalidSingleCharCtb(Throwable var1) {
      return this.invalidSingleCharCtb(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION invalidSingleCharCtb() {
      return this.invalidSingleCharCtb(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION badGiop11Ctb(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1398079698, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badGiop11Ctb", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION badGiop11Ctb(CompletionStatus var1) {
      return this.badGiop11Ctb(var1, (Throwable)null);
   }

   public DATA_CONVERSION badGiop11Ctb(Throwable var1) {
      return this.badGiop11Ctb(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION badGiop11Ctb() {
      return this.badGiop11Ctb(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION badSequenceBounds(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      DATA_CONVERSION var5 = new DATA_CONVERSION(1398079700, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.badSequenceBounds", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public DATA_CONVERSION badSequenceBounds(CompletionStatus var1, Object var2, Object var3) {
      return this.badSequenceBounds(var1, (Throwable)null, var2, var3);
   }

   public DATA_CONVERSION badSequenceBounds(Throwable var1, Object var2, Object var3) {
      return this.badSequenceBounds(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public DATA_CONVERSION badSequenceBounds(Object var1, Object var2) {
      return this.badSequenceBounds(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public DATA_CONVERSION illegalSocketFactoryType(CompletionStatus var1, Throwable var2, Object var3) {
      DATA_CONVERSION var4 = new DATA_CONVERSION(1398079701, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.illegalSocketFactoryType", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public DATA_CONVERSION illegalSocketFactoryType(CompletionStatus var1, Object var2) {
      return this.illegalSocketFactoryType(var1, (Throwable)null, var2);
   }

   public DATA_CONVERSION illegalSocketFactoryType(Throwable var1, Object var2) {
      return this.illegalSocketFactoryType(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public DATA_CONVERSION illegalSocketFactoryType(Object var1) {
      return this.illegalSocketFactoryType(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public DATA_CONVERSION badCustomSocketFactory(CompletionStatus var1, Throwable var2, Object var3) {
      DATA_CONVERSION var4 = new DATA_CONVERSION(1398079702, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badCustomSocketFactory", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public DATA_CONVERSION badCustomSocketFactory(CompletionStatus var1, Object var2) {
      return this.badCustomSocketFactory(var1, (Throwable)null, var2);
   }

   public DATA_CONVERSION badCustomSocketFactory(Throwable var1, Object var2) {
      return this.badCustomSocketFactory(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public DATA_CONVERSION badCustomSocketFactory(Object var1) {
      return this.badCustomSocketFactory(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public DATA_CONVERSION fragmentSizeMinimum(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      DATA_CONVERSION var5 = new DATA_CONVERSION(1398079703, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.fragmentSizeMinimum", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public DATA_CONVERSION fragmentSizeMinimum(CompletionStatus var1, Object var2, Object var3) {
      return this.fragmentSizeMinimum(var1, (Throwable)null, var2, var3);
   }

   public DATA_CONVERSION fragmentSizeMinimum(Throwable var1, Object var2, Object var3) {
      return this.fragmentSizeMinimum(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public DATA_CONVERSION fragmentSizeMinimum(Object var1, Object var2) {
      return this.fragmentSizeMinimum(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public DATA_CONVERSION fragmentSizeDiv(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      DATA_CONVERSION var5 = new DATA_CONVERSION(1398079704, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.fragmentSizeDiv", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public DATA_CONVERSION fragmentSizeDiv(CompletionStatus var1, Object var2, Object var3) {
      return this.fragmentSizeDiv(var1, (Throwable)null, var2, var3);
   }

   public DATA_CONVERSION fragmentSizeDiv(Throwable var1, Object var2, Object var3) {
      return this.fragmentSizeDiv(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public DATA_CONVERSION fragmentSizeDiv(Object var1, Object var2) {
      return this.fragmentSizeDiv(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public DATA_CONVERSION orbInitializerFailure(CompletionStatus var1, Throwable var2, Object var3) {
      DATA_CONVERSION var4 = new DATA_CONVERSION(1398079705, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.orbInitializerFailure", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public DATA_CONVERSION orbInitializerFailure(CompletionStatus var1, Object var2) {
      return this.orbInitializerFailure(var1, (Throwable)null, var2);
   }

   public DATA_CONVERSION orbInitializerFailure(Throwable var1, Object var2) {
      return this.orbInitializerFailure(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public DATA_CONVERSION orbInitializerFailure(Object var1) {
      return this.orbInitializerFailure(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public DATA_CONVERSION orbInitializerType(CompletionStatus var1, Throwable var2, Object var3) {
      DATA_CONVERSION var4 = new DATA_CONVERSION(1398079706, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.orbInitializerType", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public DATA_CONVERSION orbInitializerType(CompletionStatus var1, Object var2) {
      return this.orbInitializerType(var1, (Throwable)null, var2);
   }

   public DATA_CONVERSION orbInitializerType(Throwable var1, Object var2) {
      return this.orbInitializerType(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public DATA_CONVERSION orbInitializerType(Object var1) {
      return this.orbInitializerType(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public DATA_CONVERSION orbInitialreferenceSyntax(CompletionStatus var1, Throwable var2) {
      DATA_CONVERSION var3 = new DATA_CONVERSION(1398079707, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.orbInitialreferenceSyntax", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public DATA_CONVERSION orbInitialreferenceSyntax(CompletionStatus var1) {
      return this.orbInitialreferenceSyntax(var1, (Throwable)null);
   }

   public DATA_CONVERSION orbInitialreferenceSyntax(Throwable var1) {
      return this.orbInitialreferenceSyntax(CompletionStatus.COMPLETED_NO, var1);
   }

   public DATA_CONVERSION orbInitialreferenceSyntax() {
      return this.orbInitialreferenceSyntax(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public DATA_CONVERSION acceptorInstantiationFailure(CompletionStatus var1, Throwable var2, Object var3) {
      DATA_CONVERSION var4 = new DATA_CONVERSION(1398079708, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.acceptorInstantiationFailure", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public DATA_CONVERSION acceptorInstantiationFailure(CompletionStatus var1, Object var2) {
      return this.acceptorInstantiationFailure(var1, (Throwable)null, var2);
   }

   public DATA_CONVERSION acceptorInstantiationFailure(Throwable var1, Object var2) {
      return this.acceptorInstantiationFailure(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public DATA_CONVERSION acceptorInstantiationFailure(Object var1) {
      return this.acceptorInstantiationFailure(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public DATA_CONVERSION acceptorInstantiationTypeFailure(CompletionStatus var1, Throwable var2, Object var3) {
      DATA_CONVERSION var4 = new DATA_CONVERSION(1398079709, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.acceptorInstantiationTypeFailure", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public DATA_CONVERSION acceptorInstantiationTypeFailure(CompletionStatus var1, Object var2) {
      return this.acceptorInstantiationTypeFailure(var1, (Throwable)null, var2);
   }

   public DATA_CONVERSION acceptorInstantiationTypeFailure(Throwable var1, Object var2) {
      return this.acceptorInstantiationTypeFailure(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public DATA_CONVERSION acceptorInstantiationTypeFailure(Object var1) {
      return this.acceptorInstantiationTypeFailure(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public DATA_CONVERSION illegalContactInfoListFactoryType(CompletionStatus var1, Throwable var2, Object var3) {
      DATA_CONVERSION var4 = new DATA_CONVERSION(1398079710, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.illegalContactInfoListFactoryType", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public DATA_CONVERSION illegalContactInfoListFactoryType(CompletionStatus var1, Object var2) {
      return this.illegalContactInfoListFactoryType(var1, (Throwable)null, var2);
   }

   public DATA_CONVERSION illegalContactInfoListFactoryType(Throwable var1, Object var2) {
      return this.illegalContactInfoListFactoryType(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public DATA_CONVERSION illegalContactInfoListFactoryType(Object var1) {
      return this.illegalContactInfoListFactoryType(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public DATA_CONVERSION badContactInfoListFactory(CompletionStatus var1, Throwable var2, Object var3) {
      DATA_CONVERSION var4 = new DATA_CONVERSION(1398079711, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badContactInfoListFactory", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public DATA_CONVERSION badContactInfoListFactory(CompletionStatus var1, Object var2) {
      return this.badContactInfoListFactory(var1, (Throwable)null, var2);
   }

   public DATA_CONVERSION badContactInfoListFactory(Throwable var1, Object var2) {
      return this.badContactInfoListFactory(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public DATA_CONVERSION badContactInfoListFactory(Object var1) {
      return this.badContactInfoListFactory(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public DATA_CONVERSION illegalIorToSocketInfoType(CompletionStatus var1, Throwable var2, Object var3) {
      DATA_CONVERSION var4 = new DATA_CONVERSION(1398079712, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.illegalIorToSocketInfoType", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public DATA_CONVERSION illegalIorToSocketInfoType(CompletionStatus var1, Object var2) {
      return this.illegalIorToSocketInfoType(var1, (Throwable)null, var2);
   }

   public DATA_CONVERSION illegalIorToSocketInfoType(Throwable var1, Object var2) {
      return this.illegalIorToSocketInfoType(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public DATA_CONVERSION illegalIorToSocketInfoType(Object var1) {
      return this.illegalIorToSocketInfoType(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public DATA_CONVERSION badCustomIorToSocketInfo(CompletionStatus var1, Throwable var2, Object var3) {
      DATA_CONVERSION var4 = new DATA_CONVERSION(1398079713, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badCustomIorToSocketInfo", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public DATA_CONVERSION badCustomIorToSocketInfo(CompletionStatus var1, Object var2) {
      return this.badCustomIorToSocketInfo(var1, (Throwable)null, var2);
   }

   public DATA_CONVERSION badCustomIorToSocketInfo(Throwable var1, Object var2) {
      return this.badCustomIorToSocketInfo(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public DATA_CONVERSION badCustomIorToSocketInfo(Object var1) {
      return this.badCustomIorToSocketInfo(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public DATA_CONVERSION illegalIiopPrimaryToContactInfoType(CompletionStatus var1, Throwable var2, Object var3) {
      DATA_CONVERSION var4 = new DATA_CONVERSION(1398079714, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.illegalIiopPrimaryToContactInfoType", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public DATA_CONVERSION illegalIiopPrimaryToContactInfoType(CompletionStatus var1, Object var2) {
      return this.illegalIiopPrimaryToContactInfoType(var1, (Throwable)null, var2);
   }

   public DATA_CONVERSION illegalIiopPrimaryToContactInfoType(Throwable var1, Object var2) {
      return this.illegalIiopPrimaryToContactInfoType(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public DATA_CONVERSION illegalIiopPrimaryToContactInfoType(Object var1) {
      return this.illegalIiopPrimaryToContactInfoType(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public DATA_CONVERSION badCustomIiopPrimaryToContactInfo(CompletionStatus var1, Throwable var2, Object var3) {
      DATA_CONVERSION var4 = new DATA_CONVERSION(1398079715, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badCustomIiopPrimaryToContactInfo", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public DATA_CONVERSION badCustomIiopPrimaryToContactInfo(CompletionStatus var1, Object var2) {
      return this.badCustomIiopPrimaryToContactInfo(var1, (Throwable)null, var2);
   }

   public DATA_CONVERSION badCustomIiopPrimaryToContactInfo(Throwable var1, Object var2) {
      return this.badCustomIiopPrimaryToContactInfo(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public DATA_CONVERSION badCustomIiopPrimaryToContactInfo(Object var1) {
      return this.badCustomIiopPrimaryToContactInfo(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INV_OBJREF badCorbalocString(CompletionStatus var1, Throwable var2) {
      INV_OBJREF var3 = new INV_OBJREF(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badCorbalocString", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INV_OBJREF badCorbalocString(CompletionStatus var1) {
      return this.badCorbalocString(var1, (Throwable)null);
   }

   public INV_OBJREF badCorbalocString(Throwable var1) {
      return this.badCorbalocString(CompletionStatus.COMPLETED_NO, var1);
   }

   public INV_OBJREF badCorbalocString() {
      return this.badCorbalocString(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INV_OBJREF noProfilePresent(CompletionStatus var1, Throwable var2) {
      INV_OBJREF var3 = new INV_OBJREF(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.noProfilePresent", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INV_OBJREF noProfilePresent(CompletionStatus var1) {
      return this.noProfilePresent(var1, (Throwable)null);
   }

   public INV_OBJREF noProfilePresent(Throwable var1) {
      return this.noProfilePresent(CompletionStatus.COMPLETED_NO, var1);
   }

   public INV_OBJREF noProfilePresent() {
      return this.noProfilePresent(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE cannotCreateOrbidDb(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.cannotCreateOrbidDb", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE cannotCreateOrbidDb(CompletionStatus var1) {
      return this.cannotCreateOrbidDb(var1, (Throwable)null);
   }

   public INITIALIZE cannotCreateOrbidDb(Throwable var1) {
      return this.cannotCreateOrbidDb(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE cannotCreateOrbidDb() {
      return this.cannotCreateOrbidDb(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE cannotReadOrbidDb(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.cannotReadOrbidDb", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE cannotReadOrbidDb(CompletionStatus var1) {
      return this.cannotReadOrbidDb(var1, (Throwable)null);
   }

   public INITIALIZE cannotReadOrbidDb(Throwable var1) {
      return this.cannotReadOrbidDb(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE cannotReadOrbidDb() {
      return this.cannotReadOrbidDb(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE cannotWriteOrbidDb(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398079691, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.cannotWriteOrbidDb", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE cannotWriteOrbidDb(CompletionStatus var1) {
      return this.cannotWriteOrbidDb(var1, (Throwable)null);
   }

   public INITIALIZE cannotWriteOrbidDb(Throwable var1) {
      return this.cannotWriteOrbidDb(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE cannotWriteOrbidDb() {
      return this.cannotWriteOrbidDb(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE getServerPortCalledBeforeEndpointsInitialized(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398079692, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.getServerPortCalledBeforeEndpointsInitialized", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE getServerPortCalledBeforeEndpointsInitialized(CompletionStatus var1) {
      return this.getServerPortCalledBeforeEndpointsInitialized(var1, (Throwable)null);
   }

   public INITIALIZE getServerPortCalledBeforeEndpointsInitialized(Throwable var1) {
      return this.getServerPortCalledBeforeEndpointsInitialized(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE getServerPortCalledBeforeEndpointsInitialized() {
      return this.getServerPortCalledBeforeEndpointsInitialized(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE persistentServerportNotSet(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398079693, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.persistentServerportNotSet", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE persistentServerportNotSet(CompletionStatus var1) {
      return this.persistentServerportNotSet(var1, (Throwable)null);
   }

   public INITIALIZE persistentServerportNotSet(Throwable var1) {
      return this.persistentServerportNotSet(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE persistentServerportNotSet() {
      return this.persistentServerportNotSet(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INITIALIZE persistentServeridNotSet(CompletionStatus var1, Throwable var2) {
      INITIALIZE var3 = new INITIALIZE(1398079694, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.persistentServeridNotSet", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INITIALIZE persistentServeridNotSet(CompletionStatus var1) {
      return this.persistentServeridNotSet(var1, (Throwable)null);
   }

   public INITIALIZE persistentServeridNotSet(Throwable var1) {
      return this.persistentServeridNotSet(CompletionStatus.COMPLETED_NO, var1);
   }

   public INITIALIZE persistentServeridNotSet() {
      return this.persistentServeridNotSet(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL nonExistentOrbid(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.nonExistentOrbid", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL nonExistentOrbid(CompletionStatus var1) {
      return this.nonExistentOrbid(var1, (Throwable)null);
   }

   public INTERNAL nonExistentOrbid(Throwable var1) {
      return this.nonExistentOrbid(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL nonExistentOrbid() {
      return this.nonExistentOrbid(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL noServerSubcontract(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.noServerSubcontract", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL noServerSubcontract(CompletionStatus var1) {
      return this.noServerSubcontract(var1, (Throwable)null);
   }

   public INTERNAL noServerSubcontract(Throwable var1) {
      return this.noServerSubcontract(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL noServerSubcontract() {
      return this.noServerSubcontract(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL serverScTempSize(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079691, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.serverScTempSize", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL serverScTempSize(CompletionStatus var1) {
      return this.serverScTempSize(var1, (Throwable)null);
   }

   public INTERNAL serverScTempSize(Throwable var1) {
      return this.serverScTempSize(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL serverScTempSize() {
      return this.serverScTempSize(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL noClientScClass(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079692, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.noClientScClass", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL noClientScClass(CompletionStatus var1) {
      return this.noClientScClass(var1, (Throwable)null);
   }

   public INTERNAL noClientScClass(Throwable var1) {
      return this.noClientScClass(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL noClientScClass() {
      return this.noClientScClass(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL serverScNoIiopProfile(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079693, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.serverScNoIiopProfile", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL serverScNoIiopProfile(CompletionStatus var1) {
      return this.serverScNoIiopProfile(var1, (Throwable)null);
   }

   public INTERNAL serverScNoIiopProfile(Throwable var1) {
      return this.serverScNoIiopProfile(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL serverScNoIiopProfile() {
      return this.serverScNoIiopProfile(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL getSystemExReturnedNull(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079694, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.getSystemExReturnedNull", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL getSystemExReturnedNull(CompletionStatus var1) {
      return this.getSystemExReturnedNull(var1, (Throwable)null);
   }

   public INTERNAL getSystemExReturnedNull(Throwable var1) {
      return this.getSystemExReturnedNull(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL getSystemExReturnedNull() {
      return this.getSystemExReturnedNull(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL peekstringFailed(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079695, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.peekstringFailed", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL peekstringFailed(CompletionStatus var1) {
      return this.peekstringFailed(var1, (Throwable)null);
   }

   public INTERNAL peekstringFailed(Throwable var1) {
      return this.peekstringFailed(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL peekstringFailed() {
      return this.peekstringFailed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL getLocalHostFailed(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079696, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.getLocalHostFailed", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL getLocalHostFailed(CompletionStatus var1) {
      return this.getLocalHostFailed(var1, (Throwable)null);
   }

   public INTERNAL getLocalHostFailed(Throwable var1) {
      return this.getLocalHostFailed(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL getLocalHostFailed() {
      return this.getLocalHostFailed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL badLocateRequestStatus(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079698, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badLocateRequestStatus", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL badLocateRequestStatus(CompletionStatus var1) {
      return this.badLocateRequestStatus(var1, (Throwable)null);
   }

   public INTERNAL badLocateRequestStatus(Throwable var1) {
      return this.badLocateRequestStatus(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL badLocateRequestStatus() {
      return this.badLocateRequestStatus(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL stringifyWriteError(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079699, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.stringifyWriteError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL stringifyWriteError(CompletionStatus var1) {
      return this.stringifyWriteError(var1, (Throwable)null);
   }

   public INTERNAL stringifyWriteError(Throwable var1) {
      return this.stringifyWriteError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL stringifyWriteError() {
      return this.stringifyWriteError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL badGiopRequestType(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079700, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badGiopRequestType", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL badGiopRequestType(CompletionStatus var1) {
      return this.badGiopRequestType(var1, (Throwable)null);
   }

   public INTERNAL badGiopRequestType(Throwable var1) {
      return this.badGiopRequestType(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL badGiopRequestType() {
      return this.badGiopRequestType(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL errorUnmarshalingUserexc(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079701, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.errorUnmarshalingUserexc", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL errorUnmarshalingUserexc(CompletionStatus var1) {
      return this.errorUnmarshalingUserexc(var1, (Throwable)null);
   }

   public INTERNAL errorUnmarshalingUserexc(Throwable var1) {
      return this.errorUnmarshalingUserexc(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL errorUnmarshalingUserexc() {
      return this.errorUnmarshalingUserexc(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL requestdispatcherregistryError(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079702, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.requestdispatcherregistryError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL requestdispatcherregistryError(CompletionStatus var1) {
      return this.requestdispatcherregistryError(var1, (Throwable)null);
   }

   public INTERNAL requestdispatcherregistryError(Throwable var1) {
      return this.requestdispatcherregistryError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL requestdispatcherregistryError() {
      return this.requestdispatcherregistryError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL locationforwardError(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079703, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.locationforwardError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL locationforwardError(CompletionStatus var1) {
      return this.locationforwardError(var1, (Throwable)null);
   }

   public INTERNAL locationforwardError(Throwable var1) {
      return this.locationforwardError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL locationforwardError() {
      return this.locationforwardError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL wrongClientsc(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079704, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.wrongClientsc", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL wrongClientsc(CompletionStatus var1) {
      return this.wrongClientsc(var1, (Throwable)null);
   }

   public INTERNAL wrongClientsc(Throwable var1) {
      return this.wrongClientsc(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL wrongClientsc() {
      return this.wrongClientsc(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL badServantReadObject(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079705, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badServantReadObject", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL badServantReadObject(CompletionStatus var1) {
      return this.badServantReadObject(var1, (Throwable)null);
   }

   public INTERNAL badServantReadObject(Throwable var1) {
      return this.badServantReadObject(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL badServantReadObject() {
      return this.badServantReadObject(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL multIiopProfNotSupported(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079706, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.multIiopProfNotSupported", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL multIiopProfNotSupported(CompletionStatus var1) {
      return this.multIiopProfNotSupported(var1, (Throwable)null);
   }

   public INTERNAL multIiopProfNotSupported(Throwable var1) {
      return this.multIiopProfNotSupported(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL multIiopProfNotSupported() {
      return this.multIiopProfNotSupported(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL giopMagicError(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079708, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.giopMagicError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL giopMagicError(CompletionStatus var1) {
      return this.giopMagicError(var1, (Throwable)null);
   }

   public INTERNAL giopMagicError(Throwable var1) {
      return this.giopMagicError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL giopMagicError() {
      return this.giopMagicError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL giopVersionError(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079709, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.giopVersionError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL giopVersionError(CompletionStatus var1) {
      return this.giopVersionError(var1, (Throwable)null);
   }

   public INTERNAL giopVersionError(Throwable var1) {
      return this.giopVersionError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL giopVersionError() {
      return this.giopVersionError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL illegalReplyStatus(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079710, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.illegalReplyStatus", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL illegalReplyStatus(CompletionStatus var1) {
      return this.illegalReplyStatus(var1, (Throwable)null);
   }

   public INTERNAL illegalReplyStatus(Throwable var1) {
      return this.illegalReplyStatus(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL illegalReplyStatus() {
      return this.illegalReplyStatus(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL illegalGiopMsgType(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079711, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.illegalGiopMsgType", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL illegalGiopMsgType(CompletionStatus var1) {
      return this.illegalGiopMsgType(var1, (Throwable)null);
   }

   public INTERNAL illegalGiopMsgType(Throwable var1) {
      return this.illegalGiopMsgType(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL illegalGiopMsgType() {
      return this.illegalGiopMsgType(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL fragmentationDisallowed(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079712, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.fragmentationDisallowed", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL fragmentationDisallowed(CompletionStatus var1) {
      return this.fragmentationDisallowed(var1, (Throwable)null);
   }

   public INTERNAL fragmentationDisallowed(Throwable var1) {
      return this.fragmentationDisallowed(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL fragmentationDisallowed() {
      return this.fragmentationDisallowed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL badReplystatus(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079713, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badReplystatus", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL badReplystatus(CompletionStatus var1) {
      return this.badReplystatus(var1, (Throwable)null);
   }

   public INTERNAL badReplystatus(Throwable var1) {
      return this.badReplystatus(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL badReplystatus() {
      return this.badReplystatus(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL ctbConverterFailure(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079714, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.ctbConverterFailure", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL ctbConverterFailure(CompletionStatus var1) {
      return this.ctbConverterFailure(var1, (Throwable)null);
   }

   public INTERNAL ctbConverterFailure(Throwable var1) {
      return this.ctbConverterFailure(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL ctbConverterFailure() {
      return this.ctbConverterFailure(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL btcConverterFailure(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079715, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.btcConverterFailure", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL btcConverterFailure(CompletionStatus var1) {
      return this.btcConverterFailure(var1, (Throwable)null);
   }

   public INTERNAL btcConverterFailure(Throwable var1) {
      return this.btcConverterFailure(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL btcConverterFailure() {
      return this.btcConverterFailure(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL wcharArrayUnsupportedEncoding(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079716, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.wcharArrayUnsupportedEncoding", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL wcharArrayUnsupportedEncoding(CompletionStatus var1) {
      return this.wcharArrayUnsupportedEncoding(var1, (Throwable)null);
   }

   public INTERNAL wcharArrayUnsupportedEncoding(Throwable var1) {
      return this.wcharArrayUnsupportedEncoding(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL wcharArrayUnsupportedEncoding() {
      return this.wcharArrayUnsupportedEncoding(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL illegalTargetAddressDisposition(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079717, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.illegalTargetAddressDisposition", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL illegalTargetAddressDisposition(CompletionStatus var1) {
      return this.illegalTargetAddressDisposition(var1, (Throwable)null);
   }

   public INTERNAL illegalTargetAddressDisposition(Throwable var1) {
      return this.illegalTargetAddressDisposition(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL illegalTargetAddressDisposition() {
      return this.illegalTargetAddressDisposition(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL nullReplyInGetAddrDisposition(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079718, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.nullReplyInGetAddrDisposition", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL nullReplyInGetAddrDisposition(CompletionStatus var1) {
      return this.nullReplyInGetAddrDisposition(var1, (Throwable)null);
   }

   public INTERNAL nullReplyInGetAddrDisposition(Throwable var1) {
      return this.nullReplyInGetAddrDisposition(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL nullReplyInGetAddrDisposition() {
      return this.nullReplyInGetAddrDisposition(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL orbTargetAddrPreferenceInExtractObjectkeyInvalid(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079719, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.orbTargetAddrPreferenceInExtractObjectkeyInvalid", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL orbTargetAddrPreferenceInExtractObjectkeyInvalid(CompletionStatus var1) {
      return this.orbTargetAddrPreferenceInExtractObjectkeyInvalid(var1, (Throwable)null);
   }

   public INTERNAL orbTargetAddrPreferenceInExtractObjectkeyInvalid(Throwable var1) {
      return this.orbTargetAddrPreferenceInExtractObjectkeyInvalid(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL orbTargetAddrPreferenceInExtractObjectkeyInvalid() {
      return this.orbTargetAddrPreferenceInExtractObjectkeyInvalid(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL invalidIsstreamedTckind(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079720, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.invalidIsstreamedTckind", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL invalidIsstreamedTckind(CompletionStatus var1, Object var2) {
      return this.invalidIsstreamedTckind(var1, (Throwable)null, var2);
   }

   public INTERNAL invalidIsstreamedTckind(Throwable var1, Object var2) {
      return this.invalidIsstreamedTckind(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL invalidIsstreamedTckind(Object var1) {
      return this.invalidIsstreamedTckind(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL invalidJdk131PatchLevel(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079721, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.invalidJdk131PatchLevel", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL invalidJdk131PatchLevel(CompletionStatus var1) {
      return this.invalidJdk131PatchLevel(var1, (Throwable)null);
   }

   public INTERNAL invalidJdk131PatchLevel(Throwable var1) {
      return this.invalidJdk131PatchLevel(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL invalidJdk131PatchLevel() {
      return this.invalidJdk131PatchLevel(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL svcctxUnmarshalError(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079722, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.svcctxUnmarshalError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL svcctxUnmarshalError(CompletionStatus var1) {
      return this.svcctxUnmarshalError(var1, (Throwable)null);
   }

   public INTERNAL svcctxUnmarshalError(Throwable var1) {
      return this.svcctxUnmarshalError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL svcctxUnmarshalError() {
      return this.svcctxUnmarshalError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL nullIor(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079723, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.nullIor", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL nullIor(CompletionStatus var1) {
      return this.nullIor(var1, (Throwable)null);
   }

   public INTERNAL nullIor(Throwable var1) {
      return this.nullIor(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL nullIor() {
      return this.nullIor(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL unsupportedGiopVersion(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079724, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.unsupportedGiopVersion", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL unsupportedGiopVersion(CompletionStatus var1, Object var2) {
      return this.unsupportedGiopVersion(var1, (Throwable)null, var2);
   }

   public INTERNAL unsupportedGiopVersion(Throwable var1, Object var2) {
      return this.unsupportedGiopVersion(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL unsupportedGiopVersion(Object var1) {
      return this.unsupportedGiopVersion(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL applicationExceptionInSpecialMethod(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079725, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.applicationExceptionInSpecialMethod", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL applicationExceptionInSpecialMethod(CompletionStatus var1) {
      return this.applicationExceptionInSpecialMethod(var1, (Throwable)null);
   }

   public INTERNAL applicationExceptionInSpecialMethod(Throwable var1) {
      return this.applicationExceptionInSpecialMethod(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL applicationExceptionInSpecialMethod() {
      return this.applicationExceptionInSpecialMethod(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL statementNotReachable1(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079726, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.statementNotReachable1", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL statementNotReachable1(CompletionStatus var1) {
      return this.statementNotReachable1(var1, (Throwable)null);
   }

   public INTERNAL statementNotReachable1(Throwable var1) {
      return this.statementNotReachable1(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL statementNotReachable1() {
      return this.statementNotReachable1(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL statementNotReachable2(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079727, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.statementNotReachable2", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL statementNotReachable2(CompletionStatus var1) {
      return this.statementNotReachable2(var1, (Throwable)null);
   }

   public INTERNAL statementNotReachable2(Throwable var1) {
      return this.statementNotReachable2(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL statementNotReachable2() {
      return this.statementNotReachable2(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL statementNotReachable3(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079728, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.statementNotReachable3", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL statementNotReachable3(CompletionStatus var1) {
      return this.statementNotReachable3(var1, (Throwable)null);
   }

   public INTERNAL statementNotReachable3(Throwable var1) {
      return this.statementNotReachable3(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL statementNotReachable3() {
      return this.statementNotReachable3(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL statementNotReachable4(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079729, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.statementNotReachable4", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL statementNotReachable4(CompletionStatus var1) {
      return this.statementNotReachable4(var1, (Throwable)null);
   }

   public INTERNAL statementNotReachable4(Throwable var1) {
      return this.statementNotReachable4(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL statementNotReachable4() {
      return this.statementNotReachable4(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL statementNotReachable5(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079730, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.statementNotReachable5", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL statementNotReachable5(CompletionStatus var1) {
      return this.statementNotReachable5(var1, (Throwable)null);
   }

   public INTERNAL statementNotReachable5(Throwable var1) {
      return this.statementNotReachable5(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL statementNotReachable5() {
      return this.statementNotReachable5(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL statementNotReachable6(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079731, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.statementNotReachable6", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL statementNotReachable6(CompletionStatus var1) {
      return this.statementNotReachable6(var1, (Throwable)null);
   }

   public INTERNAL statementNotReachable6(Throwable var1) {
      return this.statementNotReachable6(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL statementNotReachable6() {
      return this.statementNotReachable6(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL unexpectedDiiException(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079732, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.unexpectedDiiException", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL unexpectedDiiException(CompletionStatus var1) {
      return this.unexpectedDiiException(var1, (Throwable)null);
   }

   public INTERNAL unexpectedDiiException(Throwable var1) {
      return this.unexpectedDiiException(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL unexpectedDiiException() {
      return this.unexpectedDiiException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL methodShouldNotBeCalled(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079733, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.methodShouldNotBeCalled", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL methodShouldNotBeCalled(CompletionStatus var1) {
      return this.methodShouldNotBeCalled(var1, (Throwable)null);
   }

   public INTERNAL methodShouldNotBeCalled(Throwable var1) {
      return this.methodShouldNotBeCalled(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL methodShouldNotBeCalled() {
      return this.methodShouldNotBeCalled(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL cancelNotSupported(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079734, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.cancelNotSupported", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL cancelNotSupported(CompletionStatus var1) {
      return this.cancelNotSupported(var1, (Throwable)null);
   }

   public INTERNAL cancelNotSupported(Throwable var1) {
      return this.cancelNotSupported(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL cancelNotSupported() {
      return this.cancelNotSupported(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL emptyStackRunServantPostInvoke(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079735, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.emptyStackRunServantPostInvoke", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL emptyStackRunServantPostInvoke(CompletionStatus var1) {
      return this.emptyStackRunServantPostInvoke(var1, (Throwable)null);
   }

   public INTERNAL emptyStackRunServantPostInvoke(Throwable var1) {
      return this.emptyStackRunServantPostInvoke(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL emptyStackRunServantPostInvoke() {
      return this.emptyStackRunServantPostInvoke(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL problemWithExceptionTypecode(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079736, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.problemWithExceptionTypecode", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL problemWithExceptionTypecode(CompletionStatus var1) {
      return this.problemWithExceptionTypecode(var1, (Throwable)null);
   }

   public INTERNAL problemWithExceptionTypecode(Throwable var1) {
      return this.problemWithExceptionTypecode(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL problemWithExceptionTypecode() {
      return this.problemWithExceptionTypecode(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL illegalSubcontractId(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079737, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.illegalSubcontractId", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL illegalSubcontractId(CompletionStatus var1, Object var2) {
      return this.illegalSubcontractId(var1, (Throwable)null, var2);
   }

   public INTERNAL illegalSubcontractId(Throwable var1, Object var2) {
      return this.illegalSubcontractId(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL illegalSubcontractId(Object var1) {
      return this.illegalSubcontractId(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL badSystemExceptionInLocateReply(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079738, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badSystemExceptionInLocateReply", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL badSystemExceptionInLocateReply(CompletionStatus var1) {
      return this.badSystemExceptionInLocateReply(var1, (Throwable)null);
   }

   public INTERNAL badSystemExceptionInLocateReply(Throwable var1) {
      return this.badSystemExceptionInLocateReply(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL badSystemExceptionInLocateReply() {
      return this.badSystemExceptionInLocateReply(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL badSystemExceptionInReply(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079739, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badSystemExceptionInReply", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL badSystemExceptionInReply(CompletionStatus var1) {
      return this.badSystemExceptionInReply(var1, (Throwable)null);
   }

   public INTERNAL badSystemExceptionInReply(Throwable var1) {
      return this.badSystemExceptionInReply(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL badSystemExceptionInReply() {
      return this.badSystemExceptionInReply(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL badCompletionStatusInLocateReply(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079740, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badCompletionStatusInLocateReply", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL badCompletionStatusInLocateReply(CompletionStatus var1, Object var2) {
      return this.badCompletionStatusInLocateReply(var1, (Throwable)null, var2);
   }

   public INTERNAL badCompletionStatusInLocateReply(Throwable var1, Object var2) {
      return this.badCompletionStatusInLocateReply(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL badCompletionStatusInLocateReply(Object var1) {
      return this.badCompletionStatusInLocateReply(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL badCompletionStatusInReply(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079741, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badCompletionStatusInReply", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL badCompletionStatusInReply(CompletionStatus var1, Object var2) {
      return this.badCompletionStatusInReply(var1, (Throwable)null, var2);
   }

   public INTERNAL badCompletionStatusInReply(Throwable var1, Object var2) {
      return this.badCompletionStatusInReply(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL badCompletionStatusInReply(Object var1) {
      return this.badCompletionStatusInReply(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL badkindCannotOccur(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079742, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badkindCannotOccur", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL badkindCannotOccur(CompletionStatus var1) {
      return this.badkindCannotOccur(var1, (Throwable)null);
   }

   public INTERNAL badkindCannotOccur(Throwable var1) {
      return this.badkindCannotOccur(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL badkindCannotOccur() {
      return this.badkindCannotOccur(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL errorResolvingAlias(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079743, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.errorResolvingAlias", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL errorResolvingAlias(CompletionStatus var1) {
      return this.errorResolvingAlias(var1, (Throwable)null);
   }

   public INTERNAL errorResolvingAlias(Throwable var1) {
      return this.errorResolvingAlias(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL errorResolvingAlias() {
      return this.errorResolvingAlias(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL tkLongDoubleNotSupported(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079744, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.tkLongDoubleNotSupported", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL tkLongDoubleNotSupported(CompletionStatus var1) {
      return this.tkLongDoubleNotSupported(var1, (Throwable)null);
   }

   public INTERNAL tkLongDoubleNotSupported(Throwable var1) {
      return this.tkLongDoubleNotSupported(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL tkLongDoubleNotSupported() {
      return this.tkLongDoubleNotSupported(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL typecodeNotSupported(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079745, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.typecodeNotSupported", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL typecodeNotSupported(CompletionStatus var1) {
      return this.typecodeNotSupported(var1, (Throwable)null);
   }

   public INTERNAL typecodeNotSupported(Throwable var1) {
      return this.typecodeNotSupported(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL typecodeNotSupported() {
      return this.typecodeNotSupported(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL boundsCannotOccur(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079747, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.boundsCannotOccur", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL boundsCannotOccur(CompletionStatus var1) {
      return this.boundsCannotOccur(var1, (Throwable)null);
   }

   public INTERNAL boundsCannotOccur(Throwable var1) {
      return this.boundsCannotOccur(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL boundsCannotOccur() {
      return this.boundsCannotOccur(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL numInvocationsAlreadyZero(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079749, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.numInvocationsAlreadyZero", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL numInvocationsAlreadyZero(CompletionStatus var1) {
      return this.numInvocationsAlreadyZero(var1, (Throwable)null);
   }

   public INTERNAL numInvocationsAlreadyZero(Throwable var1) {
      return this.numInvocationsAlreadyZero(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL numInvocationsAlreadyZero() {
      return this.numInvocationsAlreadyZero(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL errorInitBadserveridhandler(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079750, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.errorInitBadserveridhandler", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL errorInitBadserveridhandler(CompletionStatus var1) {
      return this.errorInitBadserveridhandler(var1, (Throwable)null);
   }

   public INTERNAL errorInitBadserveridhandler(Throwable var1) {
      return this.errorInitBadserveridhandler(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL errorInitBadserveridhandler() {
      return this.errorInitBadserveridhandler(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL noToa(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079751, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.noToa", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL noToa(CompletionStatus var1) {
      return this.noToa(var1, (Throwable)null);
   }

   public INTERNAL noToa(Throwable var1) {
      return this.noToa(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL noToa() {
      return this.noToa(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL noPoa(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079752, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.noPoa", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL noPoa(CompletionStatus var1) {
      return this.noPoa(var1, (Throwable)null);
   }

   public INTERNAL noPoa(Throwable var1) {
      return this.noPoa(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL noPoa() {
      return this.noPoa(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL invocationInfoStackEmpty(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079753, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.invocationInfoStackEmpty", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL invocationInfoStackEmpty(CompletionStatus var1) {
      return this.invocationInfoStackEmpty(var1, (Throwable)null);
   }

   public INTERNAL invocationInfoStackEmpty(Throwable var1) {
      return this.invocationInfoStackEmpty(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL invocationInfoStackEmpty() {
      return this.invocationInfoStackEmpty(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL badCodeSetString(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079754, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badCodeSetString", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL badCodeSetString(CompletionStatus var1) {
      return this.badCodeSetString(var1, (Throwable)null);
   }

   public INTERNAL badCodeSetString(Throwable var1) {
      return this.badCodeSetString(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL badCodeSetString() {
      return this.badCodeSetString(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL unknownNativeCodeset(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079755, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.unknownNativeCodeset", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL unknownNativeCodeset(CompletionStatus var1, Object var2) {
      return this.unknownNativeCodeset(var1, (Throwable)null, var2);
   }

   public INTERNAL unknownNativeCodeset(Throwable var1, Object var2) {
      return this.unknownNativeCodeset(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL unknownNativeCodeset(Object var1) {
      return this.unknownNativeCodeset(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL unknownConversionCodeSet(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079756, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.unknownConversionCodeSet", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL unknownConversionCodeSet(CompletionStatus var1, Object var2) {
      return this.unknownConversionCodeSet(var1, (Throwable)null, var2);
   }

   public INTERNAL unknownConversionCodeSet(Throwable var1, Object var2) {
      return this.unknownConversionCodeSet(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL unknownConversionCodeSet(Object var1) {
      return this.unknownConversionCodeSet(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL invalidCodeSetNumber(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079757, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.invalidCodeSetNumber", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL invalidCodeSetNumber(CompletionStatus var1) {
      return this.invalidCodeSetNumber(var1, (Throwable)null);
   }

   public INTERNAL invalidCodeSetNumber(Throwable var1) {
      return this.invalidCodeSetNumber(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL invalidCodeSetNumber() {
      return this.invalidCodeSetNumber(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL invalidCodeSetString(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079758, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.invalidCodeSetString", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL invalidCodeSetString(CompletionStatus var1, Object var2) {
      return this.invalidCodeSetString(var1, (Throwable)null, var2);
   }

   public INTERNAL invalidCodeSetString(Throwable var1, Object var2) {
      return this.invalidCodeSetString(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL invalidCodeSetString(Object var1) {
      return this.invalidCodeSetString(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL invalidCtbConverterName(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079759, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.invalidCtbConverterName", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL invalidCtbConverterName(CompletionStatus var1, Object var2) {
      return this.invalidCtbConverterName(var1, (Throwable)null, var2);
   }

   public INTERNAL invalidCtbConverterName(Throwable var1, Object var2) {
      return this.invalidCtbConverterName(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL invalidCtbConverterName(Object var1) {
      return this.invalidCtbConverterName(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL invalidBtcConverterName(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079760, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.invalidBtcConverterName", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL invalidBtcConverterName(CompletionStatus var1, Object var2) {
      return this.invalidBtcConverterName(var1, (Throwable)null, var2);
   }

   public INTERNAL invalidBtcConverterName(Throwable var1, Object var2) {
      return this.invalidBtcConverterName(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL invalidBtcConverterName(Object var1) {
      return this.invalidBtcConverterName(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL couldNotDuplicateCdrInputStream(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079761, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.couldNotDuplicateCdrInputStream", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL couldNotDuplicateCdrInputStream(CompletionStatus var1) {
      return this.couldNotDuplicateCdrInputStream(var1, (Throwable)null);
   }

   public INTERNAL couldNotDuplicateCdrInputStream(Throwable var1) {
      return this.couldNotDuplicateCdrInputStream(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL couldNotDuplicateCdrInputStream() {
      return this.couldNotDuplicateCdrInputStream(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL bootstrapApplicationException(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079762, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.bootstrapApplicationException", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL bootstrapApplicationException(CompletionStatus var1) {
      return this.bootstrapApplicationException(var1, (Throwable)null);
   }

   public INTERNAL bootstrapApplicationException(Throwable var1) {
      return this.bootstrapApplicationException(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL bootstrapApplicationException() {
      return this.bootstrapApplicationException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL duplicateIndirectionOffset(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079763, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.duplicateIndirectionOffset", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL duplicateIndirectionOffset(CompletionStatus var1) {
      return this.duplicateIndirectionOffset(var1, (Throwable)null);
   }

   public INTERNAL duplicateIndirectionOffset(Throwable var1) {
      return this.duplicateIndirectionOffset(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL duplicateIndirectionOffset() {
      return this.duplicateIndirectionOffset(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL badMessageTypeForCancel(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079764, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badMessageTypeForCancel", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL badMessageTypeForCancel(CompletionStatus var1) {
      return this.badMessageTypeForCancel(var1, (Throwable)null);
   }

   public INTERNAL badMessageTypeForCancel(Throwable var1) {
      return this.badMessageTypeForCancel(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL badMessageTypeForCancel() {
      return this.badMessageTypeForCancel(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL duplicateExceptionDetailMessage(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079765, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.duplicateExceptionDetailMessage", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL duplicateExceptionDetailMessage(CompletionStatus var1) {
      return this.duplicateExceptionDetailMessage(var1, (Throwable)null);
   }

   public INTERNAL duplicateExceptionDetailMessage(Throwable var1) {
      return this.duplicateExceptionDetailMessage(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL duplicateExceptionDetailMessage() {
      return this.duplicateExceptionDetailMessage(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL badExceptionDetailMessageServiceContextType(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079766, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badExceptionDetailMessageServiceContextType", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL badExceptionDetailMessageServiceContextType(CompletionStatus var1) {
      return this.badExceptionDetailMessageServiceContextType(var1, (Throwable)null);
   }

   public INTERNAL badExceptionDetailMessageServiceContextType(Throwable var1) {
      return this.badExceptionDetailMessageServiceContextType(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL badExceptionDetailMessageServiceContextType() {
      return this.badExceptionDetailMessageServiceContextType(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL unexpectedDirectByteBufferWithNonChannelSocket(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079767, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.unexpectedDirectByteBufferWithNonChannelSocket", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL unexpectedDirectByteBufferWithNonChannelSocket(CompletionStatus var1) {
      return this.unexpectedDirectByteBufferWithNonChannelSocket(var1, (Throwable)null);
   }

   public INTERNAL unexpectedDirectByteBufferWithNonChannelSocket(Throwable var1) {
      return this.unexpectedDirectByteBufferWithNonChannelSocket(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL unexpectedDirectByteBufferWithNonChannelSocket() {
      return this.unexpectedDirectByteBufferWithNonChannelSocket(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL unexpectedNonDirectByteBufferWithChannelSocket(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079768, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.unexpectedNonDirectByteBufferWithChannelSocket", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL unexpectedNonDirectByteBufferWithChannelSocket(CompletionStatus var1) {
      return this.unexpectedNonDirectByteBufferWithChannelSocket(var1, (Throwable)null);
   }

   public INTERNAL unexpectedNonDirectByteBufferWithChannelSocket(Throwable var1) {
      return this.unexpectedNonDirectByteBufferWithChannelSocket(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL unexpectedNonDirectByteBufferWithChannelSocket() {
      return this.unexpectedNonDirectByteBufferWithChannelSocket(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL invalidContactInfoListIteratorFailureException(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079770, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.invalidContactInfoListIteratorFailureException", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL invalidContactInfoListIteratorFailureException(CompletionStatus var1) {
      return this.invalidContactInfoListIteratorFailureException(var1, (Throwable)null);
   }

   public INTERNAL invalidContactInfoListIteratorFailureException(Throwable var1) {
      return this.invalidContactInfoListIteratorFailureException(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL invalidContactInfoListIteratorFailureException() {
      return this.invalidContactInfoListIteratorFailureException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL remarshalWithNowhereToGo(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079771, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.remarshalWithNowhereToGo", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL remarshalWithNowhereToGo(CompletionStatus var1) {
      return this.remarshalWithNowhereToGo(var1, (Throwable)null);
   }

   public INTERNAL remarshalWithNowhereToGo(Throwable var1) {
      return this.remarshalWithNowhereToGo(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL remarshalWithNowhereToGo() {
      return this.remarshalWithNowhereToGo(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL exceptionWhenSendingCloseConnection(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079772, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.exceptionWhenSendingCloseConnection", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL exceptionWhenSendingCloseConnection(CompletionStatus var1) {
      return this.exceptionWhenSendingCloseConnection(var1, (Throwable)null);
   }

   public INTERNAL exceptionWhenSendingCloseConnection(Throwable var1) {
      return this.exceptionWhenSendingCloseConnection(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL exceptionWhenSendingCloseConnection() {
      return this.exceptionWhenSendingCloseConnection(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL invocationErrorInReflectiveTie(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      INTERNAL var5 = new INTERNAL(1398079773, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.invocationErrorInReflectiveTie", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public INTERNAL invocationErrorInReflectiveTie(CompletionStatus var1, Object var2, Object var3) {
      return this.invocationErrorInReflectiveTie(var1, (Throwable)null, var2, var3);
   }

   public INTERNAL invocationErrorInReflectiveTie(Throwable var1, Object var2, Object var3) {
      return this.invocationErrorInReflectiveTie(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public INTERNAL invocationErrorInReflectiveTie(Object var1, Object var2) {
      return this.invocationErrorInReflectiveTie(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public INTERNAL badHelperWriteMethod(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079774, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badHelperWriteMethod", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL badHelperWriteMethod(CompletionStatus var1, Object var2) {
      return this.badHelperWriteMethod(var1, (Throwable)null, var2);
   }

   public INTERNAL badHelperWriteMethod(Throwable var1, Object var2) {
      return this.badHelperWriteMethod(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL badHelperWriteMethod(Object var1) {
      return this.badHelperWriteMethod(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL badHelperReadMethod(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079775, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badHelperReadMethod", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL badHelperReadMethod(CompletionStatus var1, Object var2) {
      return this.badHelperReadMethod(var1, (Throwable)null, var2);
   }

   public INTERNAL badHelperReadMethod(Throwable var1, Object var2) {
      return this.badHelperReadMethod(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL badHelperReadMethod(Object var1) {
      return this.badHelperReadMethod(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL badHelperIdMethod(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079776, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badHelperIdMethod", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL badHelperIdMethod(CompletionStatus var1, Object var2) {
      return this.badHelperIdMethod(var1, (Throwable)null, var2);
   }

   public INTERNAL badHelperIdMethod(Throwable var1, Object var2) {
      return this.badHelperIdMethod(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL badHelperIdMethod(Object var1) {
      return this.badHelperIdMethod(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL writeUndeclaredException(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079777, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.writeUndeclaredException", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL writeUndeclaredException(CompletionStatus var1, Object var2) {
      return this.writeUndeclaredException(var1, (Throwable)null, var2);
   }

   public INTERNAL writeUndeclaredException(Throwable var1, Object var2) {
      return this.writeUndeclaredException(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL writeUndeclaredException(Object var1) {
      return this.writeUndeclaredException(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL readUndeclaredException(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079778, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.readUndeclaredException", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL readUndeclaredException(CompletionStatus var1, Object var2) {
      return this.readUndeclaredException(var1, (Throwable)null, var2);
   }

   public INTERNAL readUndeclaredException(Throwable var1, Object var2) {
      return this.readUndeclaredException(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL readUndeclaredException(Object var1) {
      return this.readUndeclaredException(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL unableToSetSocketFactoryOrb(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079779, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.unableToSetSocketFactoryOrb", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL unableToSetSocketFactoryOrb(CompletionStatus var1) {
      return this.unableToSetSocketFactoryOrb(var1, (Throwable)null);
   }

   public INTERNAL unableToSetSocketFactoryOrb(Throwable var1) {
      return this.unableToSetSocketFactoryOrb(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL unableToSetSocketFactoryOrb() {
      return this.unableToSetSocketFactoryOrb(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL unexpectedException(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079780, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.unexpectedException", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL unexpectedException(CompletionStatus var1) {
      return this.unexpectedException(var1, (Throwable)null);
   }

   public INTERNAL unexpectedException(Throwable var1) {
      return this.unexpectedException(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL unexpectedException() {
      return this.unexpectedException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL noInvocationHandler(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079781, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.noInvocationHandler", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL noInvocationHandler(CompletionStatus var1, Object var2) {
      return this.noInvocationHandler(var1, (Throwable)null, var2);
   }

   public INTERNAL noInvocationHandler(Throwable var1, Object var2) {
      return this.noInvocationHandler(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL noInvocationHandler(Object var1) {
      return this.noInvocationHandler(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL invalidBuffMgrStrategy(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079782, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.invalidBuffMgrStrategy", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL invalidBuffMgrStrategy(CompletionStatus var1, Object var2) {
      return this.invalidBuffMgrStrategy(var1, (Throwable)null, var2);
   }

   public INTERNAL invalidBuffMgrStrategy(Throwable var1, Object var2) {
      return this.invalidBuffMgrStrategy(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL invalidBuffMgrStrategy(Object var1) {
      return this.invalidBuffMgrStrategy(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL javaStreamInitFailed(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079783, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.javaStreamInitFailed", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL javaStreamInitFailed(CompletionStatus var1) {
      return this.javaStreamInitFailed(var1, (Throwable)null);
   }

   public INTERNAL javaStreamInitFailed(Throwable var1) {
      return this.javaStreamInitFailed(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL javaStreamInitFailed() {
      return this.javaStreamInitFailed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL duplicateOrbVersionServiceContext(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079784, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.duplicateOrbVersionServiceContext", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL duplicateOrbVersionServiceContext(CompletionStatus var1) {
      return this.duplicateOrbVersionServiceContext(var1, (Throwable)null);
   }

   public INTERNAL duplicateOrbVersionServiceContext(Throwable var1) {
      return this.duplicateOrbVersionServiceContext(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL duplicateOrbVersionServiceContext() {
      return this.duplicateOrbVersionServiceContext(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL duplicateSendingContextServiceContext(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079785, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.duplicateSendingContextServiceContext", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL duplicateSendingContextServiceContext(CompletionStatus var1) {
      return this.duplicateSendingContextServiceContext(var1, (Throwable)null);
   }

   public INTERNAL duplicateSendingContextServiceContext(Throwable var1) {
      return this.duplicateSendingContextServiceContext(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL duplicateSendingContextServiceContext() {
      return this.duplicateSendingContextServiceContext(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL workQueueThreadInterrupted(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      INTERNAL var5 = new INTERNAL(1398079786, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.FINE, "ORBUTIL.workQueueThreadInterrupted", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public INTERNAL workQueueThreadInterrupted(CompletionStatus var1, Object var2, Object var3) {
      return this.workQueueThreadInterrupted(var1, (Throwable)null, var2, var3);
   }

   public INTERNAL workQueueThreadInterrupted(Throwable var1, Object var2, Object var3) {
      return this.workQueueThreadInterrupted(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public INTERNAL workQueueThreadInterrupted(Object var1, Object var2) {
      return this.workQueueThreadInterrupted(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public INTERNAL workerThreadCreated(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      INTERNAL var5 = new INTERNAL(1398079792, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.FINE, "ORBUTIL.workerThreadCreated", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public INTERNAL workerThreadCreated(CompletionStatus var1, Object var2, Object var3) {
      return this.workerThreadCreated(var1, (Throwable)null, var2, var3);
   }

   public INTERNAL workerThreadCreated(Throwable var1, Object var2, Object var3) {
      return this.workerThreadCreated(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public INTERNAL workerThreadCreated(Object var1, Object var2) {
      return this.workerThreadCreated(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public INTERNAL workerThreadThrowableFromRequestWork(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      INTERNAL var6 = new INTERNAL(1398079797, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.FINE, "ORBUTIL.workerThreadThrowableFromRequestWork", var7, ORBUtilSystemException.class, var6);
      }

      return var6;
   }

   public INTERNAL workerThreadThrowableFromRequestWork(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.workerThreadThrowableFromRequestWork(var1, (Throwable)null, var2, var3, var4);
   }

   public INTERNAL workerThreadThrowableFromRequestWork(Throwable var1, Object var2, Object var3, Object var4) {
      return this.workerThreadThrowableFromRequestWork(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public INTERNAL workerThreadThrowableFromRequestWork(Object var1, Object var2, Object var3) {
      return this.workerThreadThrowableFromRequestWork(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public INTERNAL workerThreadNotNeeded(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      INTERNAL var6 = new INTERNAL(1398079798, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.FINE, "ORBUTIL.workerThreadNotNeeded", var7, ORBUtilSystemException.class, var6);
      }

      return var6;
   }

   public INTERNAL workerThreadNotNeeded(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.workerThreadNotNeeded(var1, (Throwable)null, var2, var3, var4);
   }

   public INTERNAL workerThreadNotNeeded(Throwable var1, Object var2, Object var3, Object var4) {
      return this.workerThreadNotNeeded(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public INTERNAL workerThreadNotNeeded(Object var1, Object var2, Object var3) {
      return this.workerThreadNotNeeded(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public INTERNAL workerThreadDoWorkThrowable(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      INTERNAL var5 = new INTERNAL(1398079799, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.FINE, "ORBUTIL.workerThreadDoWorkThrowable", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public INTERNAL workerThreadDoWorkThrowable(CompletionStatus var1, Object var2, Object var3) {
      return this.workerThreadDoWorkThrowable(var1, (Throwable)null, var2, var3);
   }

   public INTERNAL workerThreadDoWorkThrowable(Throwable var1, Object var2, Object var3) {
      return this.workerThreadDoWorkThrowable(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public INTERNAL workerThreadDoWorkThrowable(Object var1, Object var2) {
      return this.workerThreadDoWorkThrowable(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public INTERNAL workerThreadCaughtUnexpectedThrowable(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      INTERNAL var5 = new INTERNAL(1398079800, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.workerThreadCaughtUnexpectedThrowable", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public INTERNAL workerThreadCaughtUnexpectedThrowable(CompletionStatus var1, Object var2, Object var3) {
      return this.workerThreadCaughtUnexpectedThrowable(var1, (Throwable)null, var2, var3);
   }

   public INTERNAL workerThreadCaughtUnexpectedThrowable(Throwable var1, Object var2, Object var3) {
      return this.workerThreadCaughtUnexpectedThrowable(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public INTERNAL workerThreadCaughtUnexpectedThrowable(Object var1, Object var2) {
      return this.workerThreadCaughtUnexpectedThrowable(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public INTERNAL workerThreadCreationFailure(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079801, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.SEVERE)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.SEVERE, "ORBUTIL.workerThreadCreationFailure", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL workerThreadCreationFailure(CompletionStatus var1, Object var2) {
      return this.workerThreadCreationFailure(var1, (Throwable)null, var2);
   }

   public INTERNAL workerThreadCreationFailure(Throwable var1, Object var2) {
      return this.workerThreadCreationFailure(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL workerThreadCreationFailure(Object var1) {
      return this.workerThreadCreationFailure(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL workerThreadSetNameFailure(CompletionStatus var1, Throwable var2, Object var3, Object var4, Object var5) {
      INTERNAL var6 = new INTERNAL(1398079802, var1);
      if (var2 != null) {
         var6.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var7 = new Object[]{var3, var4, var5};
         this.doLog(Level.WARNING, "ORBUTIL.workerThreadSetNameFailure", var7, ORBUtilSystemException.class, var6);
      }

      return var6;
   }

   public INTERNAL workerThreadSetNameFailure(CompletionStatus var1, Object var2, Object var3, Object var4) {
      return this.workerThreadSetNameFailure(var1, (Throwable)null, var2, var3, var4);
   }

   public INTERNAL workerThreadSetNameFailure(Throwable var1, Object var2, Object var3, Object var4) {
      return this.workerThreadSetNameFailure(CompletionStatus.COMPLETED_NO, var1, var2, var3, var4);
   }

   public INTERNAL workerThreadSetNameFailure(Object var1, Object var2, Object var3) {
      return this.workerThreadSetNameFailure(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2, var3);
   }

   public INTERNAL workQueueRequestWorkNoWorkFound(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      INTERNAL var5 = new INTERNAL(1398079804, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.workQueueRequestWorkNoWorkFound", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public INTERNAL workQueueRequestWorkNoWorkFound(CompletionStatus var1, Object var2, Object var3) {
      return this.workQueueRequestWorkNoWorkFound(var1, (Throwable)null, var2, var3);
   }

   public INTERNAL workQueueRequestWorkNoWorkFound(Throwable var1, Object var2, Object var3) {
      return this.workQueueRequestWorkNoWorkFound(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public INTERNAL workQueueRequestWorkNoWorkFound(Object var1, Object var2) {
      return this.workQueueRequestWorkNoWorkFound(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public INTERNAL threadPoolCloseError(CompletionStatus var1, Throwable var2) {
      INTERNAL var3 = new INTERNAL(1398079814, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.threadPoolCloseError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public INTERNAL threadPoolCloseError(CompletionStatus var1) {
      return this.threadPoolCloseError(var1, (Throwable)null);
   }

   public INTERNAL threadPoolCloseError(Throwable var1) {
      return this.threadPoolCloseError(CompletionStatus.COMPLETED_NO, var1);
   }

   public INTERNAL threadPoolCloseError() {
      return this.threadPoolCloseError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public INTERNAL threadGroupIsDestroyed(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079815, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.threadGroupIsDestroyed", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL threadGroupIsDestroyed(CompletionStatus var1, Object var2) {
      return this.threadGroupIsDestroyed(var1, (Throwable)null, var2);
   }

   public INTERNAL threadGroupIsDestroyed(Throwable var1, Object var2) {
      return this.threadGroupIsDestroyed(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL threadGroupIsDestroyed(Object var1) {
      return this.threadGroupIsDestroyed(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL threadGroupHasActiveThreadsInClose(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      INTERNAL var5 = new INTERNAL(1398079816, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.threadGroupHasActiveThreadsInClose", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public INTERNAL threadGroupHasActiveThreadsInClose(CompletionStatus var1, Object var2, Object var3) {
      return this.threadGroupHasActiveThreadsInClose(var1, (Throwable)null, var2, var3);
   }

   public INTERNAL threadGroupHasActiveThreadsInClose(Throwable var1, Object var2, Object var3) {
      return this.threadGroupHasActiveThreadsInClose(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public INTERNAL threadGroupHasActiveThreadsInClose(Object var1, Object var2) {
      return this.threadGroupHasActiveThreadsInClose(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public INTERNAL threadGroupHasSubGroupsInClose(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      INTERNAL var5 = new INTERNAL(1398079817, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.threadGroupHasSubGroupsInClose", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public INTERNAL threadGroupHasSubGroupsInClose(CompletionStatus var1, Object var2, Object var3) {
      return this.threadGroupHasSubGroupsInClose(var1, (Throwable)null, var2, var3);
   }

   public INTERNAL threadGroupHasSubGroupsInClose(Throwable var1, Object var2, Object var3) {
      return this.threadGroupHasSubGroupsInClose(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public INTERNAL threadGroupHasSubGroupsInClose(Object var1, Object var2) {
      return this.threadGroupHasSubGroupsInClose(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public INTERNAL threadGroupDestroyFailed(CompletionStatus var1, Throwable var2, Object var3) {
      INTERNAL var4 = new INTERNAL(1398079818, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.threadGroupDestroyFailed", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public INTERNAL threadGroupDestroyFailed(CompletionStatus var1, Object var2) {
      return this.threadGroupDestroyFailed(var1, (Throwable)null, var2);
   }

   public INTERNAL threadGroupDestroyFailed(Throwable var1, Object var2) {
      return this.threadGroupDestroyFailed(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public INTERNAL threadGroupDestroyFailed(Object var1) {
      return this.threadGroupDestroyFailed(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public INTERNAL interruptedJoinCallWhileClosingThreadPool(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      INTERNAL var5 = new INTERNAL(1398079819, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.interruptedJoinCallWhileClosingThreadPool", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public INTERNAL interruptedJoinCallWhileClosingThreadPool(CompletionStatus var1, Object var2, Object var3) {
      return this.interruptedJoinCallWhileClosingThreadPool(var1, (Throwable)null, var2, var3);
   }

   public INTERNAL interruptedJoinCallWhileClosingThreadPool(Throwable var1, Object var2, Object var3) {
      return this.interruptedJoinCallWhileClosingThreadPool(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public INTERNAL interruptedJoinCallWhileClosingThreadPool(Object var1, Object var2) {
      return this.interruptedJoinCallWhileClosingThreadPool(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public MARSHAL chunkOverflow(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.chunkOverflow", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL chunkOverflow(CompletionStatus var1) {
      return this.chunkOverflow(var1, (Throwable)null);
   }

   public MARSHAL chunkOverflow(Throwable var1) {
      return this.chunkOverflow(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL chunkOverflow() {
      return this.chunkOverflow(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL unexpectedEof(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.unexpectedEof", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL unexpectedEof(CompletionStatus var1) {
      return this.unexpectedEof(var1, (Throwable)null);
   }

   public MARSHAL unexpectedEof(Throwable var1) {
      return this.unexpectedEof(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL unexpectedEof() {
      return this.unexpectedEof(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL readObjectException(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079691, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.readObjectException", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL readObjectException(CompletionStatus var1) {
      return this.readObjectException(var1, (Throwable)null);
   }

   public MARSHAL readObjectException(Throwable var1) {
      return this.readObjectException(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL readObjectException() {
      return this.readObjectException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL characterOutofrange(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079692, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.characterOutofrange", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL characterOutofrange(CompletionStatus var1) {
      return this.characterOutofrange(var1, (Throwable)null);
   }

   public MARSHAL characterOutofrange(Throwable var1) {
      return this.characterOutofrange(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL characterOutofrange() {
      return this.characterOutofrange(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL dsiResultException(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079693, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.dsiResultException", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL dsiResultException(CompletionStatus var1) {
      return this.dsiResultException(var1, (Throwable)null);
   }

   public MARSHAL dsiResultException(Throwable var1) {
      return this.dsiResultException(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL dsiResultException() {
      return this.dsiResultException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL iiopinputstreamGrow(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079694, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.iiopinputstreamGrow", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL iiopinputstreamGrow(CompletionStatus var1) {
      return this.iiopinputstreamGrow(var1, (Throwable)null);
   }

   public MARSHAL iiopinputstreamGrow(Throwable var1) {
      return this.iiopinputstreamGrow(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL iiopinputstreamGrow() {
      return this.iiopinputstreamGrow(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL endOfStream(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079695, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.endOfStream", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL endOfStream(CompletionStatus var1) {
      return this.endOfStream(var1, (Throwable)null);
   }

   public MARSHAL endOfStream(Throwable var1) {
      return this.endOfStream(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL endOfStream() {
      return this.endOfStream(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL invalidObjectKey(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079696, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.invalidObjectKey", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL invalidObjectKey(CompletionStatus var1) {
      return this.invalidObjectKey(var1, (Throwable)null);
   }

   public MARSHAL invalidObjectKey(Throwable var1) {
      return this.invalidObjectKey(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL invalidObjectKey() {
      return this.invalidObjectKey(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL malformedUrl(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      MARSHAL var5 = new MARSHAL(1398079697, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.malformedUrl", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public MARSHAL malformedUrl(CompletionStatus var1, Object var2, Object var3) {
      return this.malformedUrl(var1, (Throwable)null, var2, var3);
   }

   public MARSHAL malformedUrl(Throwable var1, Object var2, Object var3) {
      return this.malformedUrl(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public MARSHAL malformedUrl(Object var1, Object var2) {
      return this.malformedUrl(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public MARSHAL valuehandlerReadError(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079698, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.valuehandlerReadError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL valuehandlerReadError(CompletionStatus var1) {
      return this.valuehandlerReadError(var1, (Throwable)null);
   }

   public MARSHAL valuehandlerReadError(Throwable var1) {
      return this.valuehandlerReadError(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL valuehandlerReadError() {
      return this.valuehandlerReadError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL valuehandlerReadException(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079699, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.valuehandlerReadException", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL valuehandlerReadException(CompletionStatus var1) {
      return this.valuehandlerReadException(var1, (Throwable)null);
   }

   public MARSHAL valuehandlerReadException(Throwable var1) {
      return this.valuehandlerReadException(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL valuehandlerReadException() {
      return this.valuehandlerReadException(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL badKind(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079700, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badKind", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL badKind(CompletionStatus var1) {
      return this.badKind(var1, (Throwable)null);
   }

   public MARSHAL badKind(Throwable var1) {
      return this.badKind(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL badKind() {
      return this.badKind(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL cnfeReadClass(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398079701, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.cnfeReadClass", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL cnfeReadClass(CompletionStatus var1, Object var2) {
      return this.cnfeReadClass(var1, (Throwable)null, var2);
   }

   public MARSHAL cnfeReadClass(Throwable var1, Object var2) {
      return this.cnfeReadClass(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL cnfeReadClass(Object var1) {
      return this.cnfeReadClass(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public MARSHAL badRepIdIndirection(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398079702, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badRepIdIndirection", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL badRepIdIndirection(CompletionStatus var1, Object var2) {
      return this.badRepIdIndirection(var1, (Throwable)null, var2);
   }

   public MARSHAL badRepIdIndirection(Throwable var1, Object var2) {
      return this.badRepIdIndirection(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL badRepIdIndirection(Object var1) {
      return this.badRepIdIndirection(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public MARSHAL badCodebaseIndirection(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398079703, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badCodebaseIndirection", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL badCodebaseIndirection(CompletionStatus var1, Object var2) {
      return this.badCodebaseIndirection(var1, (Throwable)null, var2);
   }

   public MARSHAL badCodebaseIndirection(Throwable var1, Object var2) {
      return this.badCodebaseIndirection(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL badCodebaseIndirection(Object var1) {
      return this.badCodebaseIndirection(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public MARSHAL unknownCodeset(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398079704, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.unknownCodeset", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL unknownCodeset(CompletionStatus var1, Object var2) {
      return this.unknownCodeset(var1, (Throwable)null, var2);
   }

   public MARSHAL unknownCodeset(Throwable var1, Object var2) {
      return this.unknownCodeset(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL unknownCodeset(Object var1) {
      return this.unknownCodeset(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public MARSHAL wcharDataInGiop10(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079705, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.wcharDataInGiop10", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL wcharDataInGiop10(CompletionStatus var1) {
      return this.wcharDataInGiop10(var1, (Throwable)null);
   }

   public MARSHAL wcharDataInGiop10(Throwable var1) {
      return this.wcharDataInGiop10(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL wcharDataInGiop10() {
      return this.wcharDataInGiop10(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL negativeStringLength(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398079706, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.negativeStringLength", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL negativeStringLength(CompletionStatus var1, Object var2) {
      return this.negativeStringLength(var1, (Throwable)null, var2);
   }

   public MARSHAL negativeStringLength(Throwable var1, Object var2) {
      return this.negativeStringLength(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL negativeStringLength(Object var1) {
      return this.negativeStringLength(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public MARSHAL expectedTypeNullAndNoRepId(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079707, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.expectedTypeNullAndNoRepId", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL expectedTypeNullAndNoRepId(CompletionStatus var1) {
      return this.expectedTypeNullAndNoRepId(var1, (Throwable)null);
   }

   public MARSHAL expectedTypeNullAndNoRepId(Throwable var1) {
      return this.expectedTypeNullAndNoRepId(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL expectedTypeNullAndNoRepId() {
      return this.expectedTypeNullAndNoRepId(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL readValueAndNoRepId(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079708, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.readValueAndNoRepId", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL readValueAndNoRepId(CompletionStatus var1) {
      return this.readValueAndNoRepId(var1, (Throwable)null);
   }

   public MARSHAL readValueAndNoRepId(Throwable var1) {
      return this.readValueAndNoRepId(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL readValueAndNoRepId() {
      return this.readValueAndNoRepId(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL unexpectedEnclosingValuetype(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      MARSHAL var5 = new MARSHAL(1398079710, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.unexpectedEnclosingValuetype", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public MARSHAL unexpectedEnclosingValuetype(CompletionStatus var1, Object var2, Object var3) {
      return this.unexpectedEnclosingValuetype(var1, (Throwable)null, var2, var3);
   }

   public MARSHAL unexpectedEnclosingValuetype(Throwable var1, Object var2, Object var3) {
      return this.unexpectedEnclosingValuetype(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public MARSHAL unexpectedEnclosingValuetype(Object var1, Object var2) {
      return this.unexpectedEnclosingValuetype(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public MARSHAL positiveEndTag(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      MARSHAL var5 = new MARSHAL(1398079711, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.positiveEndTag", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public MARSHAL positiveEndTag(CompletionStatus var1, Object var2, Object var3) {
      return this.positiveEndTag(var1, (Throwable)null, var2, var3);
   }

   public MARSHAL positiveEndTag(Throwable var1, Object var2, Object var3) {
      return this.positiveEndTag(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public MARSHAL positiveEndTag(Object var1, Object var2) {
      return this.positiveEndTag(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public MARSHAL nullOutCall(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079712, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.nullOutCall", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL nullOutCall(CompletionStatus var1) {
      return this.nullOutCall(var1, (Throwable)null);
   }

   public MARSHAL nullOutCall(Throwable var1) {
      return this.nullOutCall(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL nullOutCall() {
      return this.nullOutCall(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL writeLocalObject(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079713, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.writeLocalObject", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL writeLocalObject(CompletionStatus var1) {
      return this.writeLocalObject(var1, (Throwable)null);
   }

   public MARSHAL writeLocalObject(Throwable var1) {
      return this.writeLocalObject(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL writeLocalObject() {
      return this.writeLocalObject(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL badInsertobjParam(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398079714, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badInsertobjParam", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL badInsertobjParam(CompletionStatus var1, Object var2) {
      return this.badInsertobjParam(var1, (Throwable)null, var2);
   }

   public MARSHAL badInsertobjParam(Throwable var1, Object var2) {
      return this.badInsertobjParam(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL badInsertobjParam(Object var1) {
      return this.badInsertobjParam(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public MARSHAL customWrapperWithCodebase(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079715, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.customWrapperWithCodebase", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL customWrapperWithCodebase(CompletionStatus var1) {
      return this.customWrapperWithCodebase(var1, (Throwable)null);
   }

   public MARSHAL customWrapperWithCodebase(Throwable var1) {
      return this.customWrapperWithCodebase(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL customWrapperWithCodebase() {
      return this.customWrapperWithCodebase(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL customWrapperIndirection(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079716, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.customWrapperIndirection", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL customWrapperIndirection(CompletionStatus var1) {
      return this.customWrapperIndirection(var1, (Throwable)null);
   }

   public MARSHAL customWrapperIndirection(Throwable var1) {
      return this.customWrapperIndirection(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL customWrapperIndirection() {
      return this.customWrapperIndirection(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL customWrapperNotSingleRepid(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079717, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.customWrapperNotSingleRepid", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL customWrapperNotSingleRepid(CompletionStatus var1) {
      return this.customWrapperNotSingleRepid(var1, (Throwable)null);
   }

   public MARSHAL customWrapperNotSingleRepid(Throwable var1) {
      return this.customWrapperNotSingleRepid(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL customWrapperNotSingleRepid() {
      return this.customWrapperNotSingleRepid(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL badValueTag(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398079718, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.badValueTag", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL badValueTag(CompletionStatus var1, Object var2) {
      return this.badValueTag(var1, (Throwable)null, var2);
   }

   public MARSHAL badValueTag(Throwable var1, Object var2) {
      return this.badValueTag(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL badValueTag(Object var1) {
      return this.badValueTag(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public MARSHAL badTypecodeForCustomValue(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079719, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badTypecodeForCustomValue", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL badTypecodeForCustomValue(CompletionStatus var1) {
      return this.badTypecodeForCustomValue(var1, (Throwable)null);
   }

   public MARSHAL badTypecodeForCustomValue(Throwable var1) {
      return this.badTypecodeForCustomValue(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL badTypecodeForCustomValue() {
      return this.badTypecodeForCustomValue(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL errorInvokingHelperWrite(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079720, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.errorInvokingHelperWrite", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL errorInvokingHelperWrite(CompletionStatus var1) {
      return this.errorInvokingHelperWrite(var1, (Throwable)null);
   }

   public MARSHAL errorInvokingHelperWrite(Throwable var1) {
      return this.errorInvokingHelperWrite(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL errorInvokingHelperWrite() {
      return this.errorInvokingHelperWrite(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL badDigitInFixed(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079721, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badDigitInFixed", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL badDigitInFixed(CompletionStatus var1) {
      return this.badDigitInFixed(var1, (Throwable)null);
   }

   public MARSHAL badDigitInFixed(Throwable var1) {
      return this.badDigitInFixed(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL badDigitInFixed() {
      return this.badDigitInFixed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL refTypeIndirType(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079722, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.refTypeIndirType", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL refTypeIndirType(CompletionStatus var1) {
      return this.refTypeIndirType(var1, (Throwable)null);
   }

   public MARSHAL refTypeIndirType(Throwable var1) {
      return this.refTypeIndirType(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL refTypeIndirType() {
      return this.refTypeIndirType(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL badReservedLength(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079723, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badReservedLength", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL badReservedLength(CompletionStatus var1) {
      return this.badReservedLength(var1, (Throwable)null);
   }

   public MARSHAL badReservedLength(Throwable var1) {
      return this.badReservedLength(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL badReservedLength() {
      return this.badReservedLength(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL nullNotAllowed(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079724, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.nullNotAllowed", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL nullNotAllowed(CompletionStatus var1) {
      return this.nullNotAllowed(var1, (Throwable)null);
   }

   public MARSHAL nullNotAllowed(Throwable var1) {
      return this.nullNotAllowed(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL nullNotAllowed() {
      return this.nullNotAllowed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL unionDiscriminatorError(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079726, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.unionDiscriminatorError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL unionDiscriminatorError(CompletionStatus var1) {
      return this.unionDiscriminatorError(var1, (Throwable)null);
   }

   public MARSHAL unionDiscriminatorError(Throwable var1) {
      return this.unionDiscriminatorError(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL unionDiscriminatorError() {
      return this.unionDiscriminatorError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL cannotMarshalNative(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079727, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.cannotMarshalNative", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL cannotMarshalNative(CompletionStatus var1) {
      return this.cannotMarshalNative(var1, (Throwable)null);
   }

   public MARSHAL cannotMarshalNative(Throwable var1) {
      return this.cannotMarshalNative(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL cannotMarshalNative() {
      return this.cannotMarshalNative(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL cannotMarshalBadTckind(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079728, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.cannotMarshalBadTckind", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL cannotMarshalBadTckind(CompletionStatus var1) {
      return this.cannotMarshalBadTckind(var1, (Throwable)null);
   }

   public MARSHAL cannotMarshalBadTckind(Throwable var1) {
      return this.cannotMarshalBadTckind(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL cannotMarshalBadTckind() {
      return this.cannotMarshalBadTckind(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL invalidIndirection(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398079729, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.invalidIndirection", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL invalidIndirection(CompletionStatus var1, Object var2) {
      return this.invalidIndirection(var1, (Throwable)null, var2);
   }

   public MARSHAL invalidIndirection(Throwable var1, Object var2) {
      return this.invalidIndirection(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL invalidIndirection(Object var1) {
      return this.invalidIndirection(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public MARSHAL indirectionNotFound(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398079730, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.FINE, "ORBUTIL.indirectionNotFound", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL indirectionNotFound(CompletionStatus var1, Object var2) {
      return this.indirectionNotFound(var1, (Throwable)null, var2);
   }

   public MARSHAL indirectionNotFound(Throwable var1, Object var2) {
      return this.indirectionNotFound(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL indirectionNotFound(Object var1) {
      return this.indirectionNotFound(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public MARSHAL recursiveTypecodeError(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079731, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.recursiveTypecodeError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL recursiveTypecodeError(CompletionStatus var1) {
      return this.recursiveTypecodeError(var1, (Throwable)null);
   }

   public MARSHAL recursiveTypecodeError(Throwable var1) {
      return this.recursiveTypecodeError(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL recursiveTypecodeError() {
      return this.recursiveTypecodeError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL invalidSimpleTypecode(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079732, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.invalidSimpleTypecode", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL invalidSimpleTypecode(CompletionStatus var1) {
      return this.invalidSimpleTypecode(var1, (Throwable)null);
   }

   public MARSHAL invalidSimpleTypecode(Throwable var1) {
      return this.invalidSimpleTypecode(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL invalidSimpleTypecode() {
      return this.invalidSimpleTypecode(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL invalidComplexTypecode(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079733, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.invalidComplexTypecode", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL invalidComplexTypecode(CompletionStatus var1) {
      return this.invalidComplexTypecode(var1, (Throwable)null);
   }

   public MARSHAL invalidComplexTypecode(Throwable var1) {
      return this.invalidComplexTypecode(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL invalidComplexTypecode() {
      return this.invalidComplexTypecode(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL invalidTypecodeKindMarshal(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079734, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.invalidTypecodeKindMarshal", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL invalidTypecodeKindMarshal(CompletionStatus var1) {
      return this.invalidTypecodeKindMarshal(var1, (Throwable)null);
   }

   public MARSHAL invalidTypecodeKindMarshal(Throwable var1) {
      return this.invalidTypecodeKindMarshal(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL invalidTypecodeKindMarshal() {
      return this.invalidTypecodeKindMarshal(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL unexpectedUnionDefault(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079735, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.unexpectedUnionDefault", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL unexpectedUnionDefault(CompletionStatus var1) {
      return this.unexpectedUnionDefault(var1, (Throwable)null);
   }

   public MARSHAL unexpectedUnionDefault(Throwable var1) {
      return this.unexpectedUnionDefault(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL unexpectedUnionDefault() {
      return this.unexpectedUnionDefault(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL illegalUnionDiscriminatorType(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079736, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.illegalUnionDiscriminatorType", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL illegalUnionDiscriminatorType(CompletionStatus var1) {
      return this.illegalUnionDiscriminatorType(var1, (Throwable)null);
   }

   public MARSHAL illegalUnionDiscriminatorType(Throwable var1) {
      return this.illegalUnionDiscriminatorType(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL illegalUnionDiscriminatorType() {
      return this.illegalUnionDiscriminatorType(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL couldNotSkipBytes(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      MARSHAL var5 = new MARSHAL(1398079737, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.couldNotSkipBytes", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public MARSHAL couldNotSkipBytes(CompletionStatus var1, Object var2, Object var3) {
      return this.couldNotSkipBytes(var1, (Throwable)null, var2, var3);
   }

   public MARSHAL couldNotSkipBytes(Throwable var1, Object var2, Object var3) {
      return this.couldNotSkipBytes(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public MARSHAL couldNotSkipBytes(Object var1, Object var2) {
      return this.couldNotSkipBytes(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public MARSHAL badChunkLength(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      MARSHAL var5 = new MARSHAL(1398079738, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.badChunkLength", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public MARSHAL badChunkLength(CompletionStatus var1, Object var2, Object var3) {
      return this.badChunkLength(var1, (Throwable)null, var2, var3);
   }

   public MARSHAL badChunkLength(Throwable var1, Object var2, Object var3) {
      return this.badChunkLength(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public MARSHAL badChunkLength(Object var1, Object var2) {
      return this.badChunkLength(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public MARSHAL unableToLocateRepIdArray(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398079739, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.unableToLocateRepIdArray", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL unableToLocateRepIdArray(CompletionStatus var1, Object var2) {
      return this.unableToLocateRepIdArray(var1, (Throwable)null, var2);
   }

   public MARSHAL unableToLocateRepIdArray(Throwable var1, Object var2) {
      return this.unableToLocateRepIdArray(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL unableToLocateRepIdArray(Object var1) {
      return this.unableToLocateRepIdArray(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public MARSHAL badFixed(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      MARSHAL var5 = new MARSHAL(1398079740, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.badFixed", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public MARSHAL badFixed(CompletionStatus var1, Object var2, Object var3) {
      return this.badFixed(var1, (Throwable)null, var2, var3);
   }

   public MARSHAL badFixed(Throwable var1, Object var2, Object var3) {
      return this.badFixed(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public MARSHAL badFixed(Object var1, Object var2) {
      return this.badFixed(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public MARSHAL readObjectLoadClassFailure(CompletionStatus var1, Throwable var2, Object var3, Object var4) {
      MARSHAL var5 = new MARSHAL(1398079741, var1);
      if (var2 != null) {
         var5.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var6 = new Object[]{var3, var4};
         this.doLog(Level.WARNING, "ORBUTIL.readObjectLoadClassFailure", var6, ORBUtilSystemException.class, var5);
      }

      return var5;
   }

   public MARSHAL readObjectLoadClassFailure(CompletionStatus var1, Object var2, Object var3) {
      return this.readObjectLoadClassFailure(var1, (Throwable)null, var2, var3);
   }

   public MARSHAL readObjectLoadClassFailure(Throwable var1, Object var2, Object var3) {
      return this.readObjectLoadClassFailure(CompletionStatus.COMPLETED_NO, var1, var2, var3);
   }

   public MARSHAL readObjectLoadClassFailure(Object var1, Object var2) {
      return this.readObjectLoadClassFailure(CompletionStatus.COMPLETED_NO, (Throwable)null, var1, var2);
   }

   public MARSHAL couldNotInstantiateHelper(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398079742, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.couldNotInstantiateHelper", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL couldNotInstantiateHelper(CompletionStatus var1, Object var2) {
      return this.couldNotInstantiateHelper(var1, (Throwable)null, var2);
   }

   public MARSHAL couldNotInstantiateHelper(Throwable var1, Object var2) {
      return this.couldNotInstantiateHelper(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL couldNotInstantiateHelper(Object var1) {
      return this.couldNotInstantiateHelper(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public MARSHAL badToaOaid(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079743, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badToaOaid", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL badToaOaid(CompletionStatus var1) {
      return this.badToaOaid(var1, (Throwable)null);
   }

   public MARSHAL badToaOaid(Throwable var1) {
      return this.badToaOaid(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL badToaOaid() {
      return this.badToaOaid(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL couldNotInvokeHelperReadMethod(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398079744, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.couldNotInvokeHelperReadMethod", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL couldNotInvokeHelperReadMethod(CompletionStatus var1, Object var2) {
      return this.couldNotInvokeHelperReadMethod(var1, (Throwable)null, var2);
   }

   public MARSHAL couldNotInvokeHelperReadMethod(Throwable var1, Object var2) {
      return this.couldNotInvokeHelperReadMethod(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL couldNotInvokeHelperReadMethod(Object var1) {
      return this.couldNotInvokeHelperReadMethod(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public MARSHAL couldNotFindClass(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079745, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.couldNotFindClass", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL couldNotFindClass(CompletionStatus var1) {
      return this.couldNotFindClass(var1, (Throwable)null);
   }

   public MARSHAL couldNotFindClass(Throwable var1) {
      return this.couldNotFindClass(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL couldNotFindClass() {
      return this.couldNotFindClass(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL badArgumentsNvlist(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079746, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.badArgumentsNvlist", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL badArgumentsNvlist(CompletionStatus var1) {
      return this.badArgumentsNvlist(var1, (Throwable)null);
   }

   public MARSHAL badArgumentsNvlist(Throwable var1) {
      return this.badArgumentsNvlist(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL badArgumentsNvlist() {
      return this.badArgumentsNvlist(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL stubCreateError(CompletionStatus var1, Throwable var2) {
      MARSHAL var3 = new MARSHAL(1398079747, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.stubCreateError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public MARSHAL stubCreateError(CompletionStatus var1) {
      return this.stubCreateError(var1, (Throwable)null);
   }

   public MARSHAL stubCreateError(Throwable var1) {
      return this.stubCreateError(CompletionStatus.COMPLETED_NO, var1);
   }

   public MARSHAL stubCreateError() {
      return this.stubCreateError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public MARSHAL javaSerializationException(CompletionStatus var1, Throwable var2, Object var3) {
      MARSHAL var4 = new MARSHAL(1398079748, var1);
      if (var2 != null) {
         var4.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object[] var5 = new Object[]{var3};
         this.doLog(Level.WARNING, "ORBUTIL.javaSerializationException", var5, ORBUtilSystemException.class, var4);
      }

      return var4;
   }

   public MARSHAL javaSerializationException(CompletionStatus var1, Object var2) {
      return this.javaSerializationException(var1, (Throwable)null, var2);
   }

   public MARSHAL javaSerializationException(Throwable var1, Object var2) {
      return this.javaSerializationException(CompletionStatus.COMPLETED_NO, var1, var2);
   }

   public MARSHAL javaSerializationException(Object var1) {
      return this.javaSerializationException(CompletionStatus.COMPLETED_NO, (Throwable)null, var1);
   }

   public NO_IMPLEMENT genericNoImpl(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.genericNoImpl", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT genericNoImpl(CompletionStatus var1) {
      return this.genericNoImpl(var1, (Throwable)null);
   }

   public NO_IMPLEMENT genericNoImpl(Throwable var1) {
      return this.genericNoImpl(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT genericNoImpl() {
      return this.genericNoImpl(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_IMPLEMENT contextNotImplemented(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.contextNotImplemented", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT contextNotImplemented(CompletionStatus var1) {
      return this.contextNotImplemented(var1, (Throwable)null);
   }

   public NO_IMPLEMENT contextNotImplemented(Throwable var1) {
      return this.contextNotImplemented(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT contextNotImplemented() {
      return this.contextNotImplemented(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_IMPLEMENT getinterfaceNotImplemented(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1398079691, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.getinterfaceNotImplemented", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT getinterfaceNotImplemented(CompletionStatus var1) {
      return this.getinterfaceNotImplemented(var1, (Throwable)null);
   }

   public NO_IMPLEMENT getinterfaceNotImplemented(Throwable var1) {
      return this.getinterfaceNotImplemented(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT getinterfaceNotImplemented() {
      return this.getinterfaceNotImplemented(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_IMPLEMENT sendDeferredNotimplemented(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1398079692, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.sendDeferredNotimplemented", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT sendDeferredNotimplemented(CompletionStatus var1) {
      return this.sendDeferredNotimplemented(var1, (Throwable)null);
   }

   public NO_IMPLEMENT sendDeferredNotimplemented(Throwable var1) {
      return this.sendDeferredNotimplemented(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT sendDeferredNotimplemented() {
      return this.sendDeferredNotimplemented(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public NO_IMPLEMENT longDoubleNotImplemented(CompletionStatus var1, Throwable var2) {
      NO_IMPLEMENT var3 = new NO_IMPLEMENT(1398079693, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.longDoubleNotImplemented", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public NO_IMPLEMENT longDoubleNotImplemented(CompletionStatus var1) {
      return this.longDoubleNotImplemented(var1, (Throwable)null);
   }

   public NO_IMPLEMENT longDoubleNotImplemented(Throwable var1) {
      return this.longDoubleNotImplemented(CompletionStatus.COMPLETED_NO, var1);
   }

   public NO_IMPLEMENT longDoubleNotImplemented() {
      return this.longDoubleNotImplemented(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER noServerScInDispatch(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.noServerScInDispatch", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER noServerScInDispatch(CompletionStatus var1) {
      return this.noServerScInDispatch(var1, (Throwable)null);
   }

   public OBJ_ADAPTER noServerScInDispatch(Throwable var1) {
      return this.noServerScInDispatch(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER noServerScInDispatch() {
      return this.noServerScInDispatch(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER orbConnectError(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.orbConnectError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER orbConnectError(CompletionStatus var1) {
      return this.orbConnectError(var1, (Throwable)null);
   }

   public OBJ_ADAPTER orbConnectError(Throwable var1) {
      return this.orbConnectError(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER orbConnectError() {
      return this.orbConnectError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJ_ADAPTER adapterInactiveInActivation(CompletionStatus var1, Throwable var2) {
      OBJ_ADAPTER var3 = new OBJ_ADAPTER(1398079691, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.adapterInactiveInActivation", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public OBJ_ADAPTER adapterInactiveInActivation(CompletionStatus var1) {
      return this.adapterInactiveInActivation(var1, (Throwable)null);
   }

   public OBJ_ADAPTER adapterInactiveInActivation(Throwable var1) {
      return this.adapterInactiveInActivation(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJ_ADAPTER adapterInactiveInActivation() {
      return this.adapterInactiveInActivation(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST locateUnknownObject(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.locateUnknownObject", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST locateUnknownObject(CompletionStatus var1) {
      return this.locateUnknownObject(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST locateUnknownObject(Throwable var1) {
      return this.locateUnknownObject(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST locateUnknownObject() {
      return this.locateUnknownObject(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST badServerId(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.badServerId", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST badServerId(CompletionStatus var1) {
      return this.badServerId(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST badServerId(Throwable var1) {
      return this.badServerId(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST badServerId() {
      return this.badServerId(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST badSkeleton(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398079691, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badSkeleton", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST badSkeleton(CompletionStatus var1) {
      return this.badSkeleton(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST badSkeleton(Throwable var1) {
      return this.badSkeleton(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST badSkeleton() {
      return this.badSkeleton(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST servantNotFound(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398079692, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.servantNotFound", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST servantNotFound(CompletionStatus var1) {
      return this.servantNotFound(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST servantNotFound(Throwable var1) {
      return this.servantNotFound(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST servantNotFound() {
      return this.servantNotFound(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST noObjectAdapterFactory(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398079693, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.noObjectAdapterFactory", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST noObjectAdapterFactory(CompletionStatus var1) {
      return this.noObjectAdapterFactory(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST noObjectAdapterFactory(Throwable var1) {
      return this.noObjectAdapterFactory(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST noObjectAdapterFactory() {
      return this.noObjectAdapterFactory(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST badAdapterId(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398079694, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.badAdapterId", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST badAdapterId(CompletionStatus var1) {
      return this.badAdapterId(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST badAdapterId(Throwable var1) {
      return this.badAdapterId(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST badAdapterId() {
      return this.badAdapterId(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public OBJECT_NOT_EXIST dynAnyDestroyed(CompletionStatus var1, Throwable var2) {
      OBJECT_NOT_EXIST var3 = new OBJECT_NOT_EXIST(1398079695, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.dynAnyDestroyed", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public OBJECT_NOT_EXIST dynAnyDestroyed(CompletionStatus var1) {
      return this.dynAnyDestroyed(var1, (Throwable)null);
   }

   public OBJECT_NOT_EXIST dynAnyDestroyed(Throwable var1) {
      return this.dynAnyDestroyed(CompletionStatus.COMPLETED_NO, var1);
   }

   public OBJECT_NOT_EXIST dynAnyDestroyed() {
      return this.dynAnyDestroyed(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public TRANSIENT requestCanceled(CompletionStatus var1, Throwable var2) {
      TRANSIENT var3 = new TRANSIENT(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.requestCanceled", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public TRANSIENT requestCanceled(CompletionStatus var1) {
      return this.requestCanceled(var1, (Throwable)null);
   }

   public TRANSIENT requestCanceled(Throwable var1) {
      return this.requestCanceled(CompletionStatus.COMPLETED_NO, var1);
   }

   public TRANSIENT requestCanceled() {
      return this.requestCanceled(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN unknownCorbaExc(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398079689, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.unknownCorbaExc", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN unknownCorbaExc(CompletionStatus var1) {
      return this.unknownCorbaExc(var1, (Throwable)null);
   }

   public UNKNOWN unknownCorbaExc(Throwable var1) {
      return this.unknownCorbaExc(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN unknownCorbaExc() {
      return this.unknownCorbaExc(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN runtimeexception(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398079690, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.runtimeexception", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN runtimeexception(CompletionStatus var1) {
      return this.runtimeexception(var1, (Throwable)null);
   }

   public UNKNOWN runtimeexception(Throwable var1) {
      return this.runtimeexception(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN runtimeexception() {
      return this.runtimeexception(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN unknownServerError(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398079691, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.unknownServerError", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN unknownServerError(CompletionStatus var1) {
      return this.unknownServerError(var1, (Throwable)null);
   }

   public UNKNOWN unknownServerError(Throwable var1) {
      return this.unknownServerError(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN unknownServerError() {
      return this.unknownServerError(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN unknownDsiSysex(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398079692, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.unknownDsiSysex", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN unknownDsiSysex(CompletionStatus var1) {
      return this.unknownDsiSysex(var1, (Throwable)null);
   }

   public UNKNOWN unknownDsiSysex(Throwable var1) {
      return this.unknownDsiSysex(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN unknownDsiSysex() {
      return this.unknownDsiSysex(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN unknownSysex(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398079693, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.unknownSysex", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN unknownSysex(CompletionStatus var1) {
      return this.unknownSysex(var1, (Throwable)null);
   }

   public UNKNOWN unknownSysex(Throwable var1) {
      return this.unknownSysex(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN unknownSysex() {
      return this.unknownSysex(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN wrongInterfaceDef(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398079694, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.wrongInterfaceDef", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN wrongInterfaceDef(CompletionStatus var1) {
      return this.wrongInterfaceDef(var1, (Throwable)null);
   }

   public UNKNOWN wrongInterfaceDef(Throwable var1) {
      return this.wrongInterfaceDef(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN wrongInterfaceDef() {
      return this.wrongInterfaceDef(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN noInterfaceDefStub(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398079695, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.WARNING)) {
         Object var4 = null;
         this.doLog(Level.WARNING, "ORBUTIL.noInterfaceDefStub", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN noInterfaceDefStub(CompletionStatus var1) {
      return this.noInterfaceDefStub(var1, (Throwable)null);
   }

   public UNKNOWN noInterfaceDefStub(Throwable var1) {
      return this.noInterfaceDefStub(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN noInterfaceDefStub() {
      return this.noInterfaceDefStub(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }

   public UNKNOWN unknownExceptionInDispatch(CompletionStatus var1, Throwable var2) {
      UNKNOWN var3 = new UNKNOWN(1398079697, var1);
      if (var2 != null) {
         var3.initCause(var2);
      }

      if (this.logger.isLoggable(Level.FINE)) {
         Object var4 = null;
         this.doLog(Level.FINE, "ORBUTIL.unknownExceptionInDispatch", (Object[])var4, ORBUtilSystemException.class, var3);
      }

      return var3;
   }

   public UNKNOWN unknownExceptionInDispatch(CompletionStatus var1) {
      return this.unknownExceptionInDispatch(var1, (Throwable)null);
   }

   public UNKNOWN unknownExceptionInDispatch(Throwable var1) {
      return this.unknownExceptionInDispatch(CompletionStatus.COMPLETED_NO, var1);
   }

   public UNKNOWN unknownExceptionInDispatch() {
      return this.unknownExceptionInDispatch(CompletionStatus.COMPLETED_NO, (Throwable)null);
   }
}
