package kz.capitalpay.server.dto.rpc;

import kz.capitalpay.server.dto.rpc.ErrorRpc;
import kz.capitalpay.server.dto.rpc.JsonRpc;

public class ErrorDictionary {
    public static final ErrorRpc error32601 = new ErrorRpc(-32601L,"Procedure not found.");
    public static final ErrorRpc error32700 = new ErrorRpc(-32700L,"Parse error");
    public static final ErrorRpc error32600 = new ErrorRpc(-32600L,"Invalid JSON-RPC.");


}
