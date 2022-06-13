/**
 * EpayServiceIOExceptionException.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */
package kz.capitalpay.server.wsdl;

public class EpayServiceIOExceptionException extends java.lang.Exception {
    private static final long serialVersionUID = 1637588635280L;
    private kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceIOException faultMessage;

    public EpayServiceIOExceptionException() {
        super("EpayServiceIOExceptionException");
    }

    public EpayServiceIOExceptionException(java.lang.String s) {
        super(s);
    }

    public EpayServiceIOExceptionException(java.lang.String s,
                                           java.lang.Throwable ex) {
        super(s, ex);
    }

    public EpayServiceIOExceptionException(java.lang.Throwable cause) {
        super(cause);
    }

    public void setFaultMessage(
            kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceIOException msg) {
        faultMessage = msg;
    }

    public kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceIOException getFaultMessage() {
        return faultMessage;
    }
}
