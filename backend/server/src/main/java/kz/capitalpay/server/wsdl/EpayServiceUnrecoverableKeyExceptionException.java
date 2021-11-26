/**
 * EpayServiceUnrecoverableKeyExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */
package kz.capitalpay.server.wsdl;

public class EpayServiceUnrecoverableKeyExceptionException extends java.lang.Exception {
    private static final long serialVersionUID = 1637588635287L;
    private kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceUnrecoverableKeyException faultMessage;

    public EpayServiceUnrecoverableKeyExceptionException() {
        super("EpayServiceUnrecoverableKeyExceptionException");
    }

    public EpayServiceUnrecoverableKeyExceptionException(java.lang.String s) {
        super(s);
    }

    public EpayServiceUnrecoverableKeyExceptionException(java.lang.String s,
        java.lang.Throwable ex) {
        super(s, ex);
    }

    public EpayServiceUnrecoverableKeyExceptionException(
        java.lang.Throwable cause) {
        super(cause);
    }

    public void setFaultMessage(
        kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceUnrecoverableKeyException msg) {
        faultMessage = msg;
    }

    public kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceUnrecoverableKeyException getFaultMessage() {
        return faultMessage;
    }
}
