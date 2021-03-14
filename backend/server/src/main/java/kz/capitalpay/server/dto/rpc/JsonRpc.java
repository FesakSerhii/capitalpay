package kz.capitalpay.server.dto.rpc;

public class JsonRpc {
    String jsonrpc;
    String method;
    Object params;

    Object result;

    ErrorRpc error;

    String id;

    public JsonRpc() {
    }

    public JsonRpc(String jsonrpc, Object result, String id) {
        this.jsonrpc = jsonrpc;
        this.result = result;
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public ErrorRpc getError() {
        return error;
    }

    public void setError(ErrorRpc error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
