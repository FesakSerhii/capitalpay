/**
 * EpayServiceSignatureExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */
package kz.capitalpay.server.wsdl;

public class EpayServiceSignatureExceptionException extends java.lang.Exception {
    private static final long serialVersionUID = 1637588635303L;
    private kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceSignatureException faultMessage;

    public EpayServiceSignatureExceptionException() {
        super("EpayServiceSignatureExceptionException");
    }

    public EpayServiceSignatureExceptionException(java.lang.String s) {
        super(s);
    }

    public EpayServiceSignatureExceptionException(java.lang.String s,
        java.lang.Throwable ex) {
        super(s, ex);
    }

    public EpayServiceSignatureExceptionException(java.lang.Throwable cause) {
        super(cause);
    }

    public void setFaultMessage(
        kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceSignatureException msg) {
        faultMessage = msg;
    }

    public kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceSignatureException getFaultMessage() {
        return faultMessage;
    }
}
