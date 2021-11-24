/**
 * EpayServiceSAXExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */
package kz.capitalpay.server.wsdl;

public class EpayServiceSAXExceptionException extends java.lang.Exception {
    private static final long serialVersionUID = 1637588635291L;
    private kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceSAXException faultMessage;

    public EpayServiceSAXExceptionException() {
        super("EpayServiceSAXExceptionException");
    }

    public EpayServiceSAXExceptionException(java.lang.String s) {
        super(s);
    }

    public EpayServiceSAXExceptionException(java.lang.String s,
        java.lang.Throwable ex) {
        super(s, ex);
    }

    public EpayServiceSAXExceptionException(java.lang.Throwable cause) {
        super(cause);
    }

    public void setFaultMessage(
        kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceSAXException msg) {
        faultMessage = msg;
    }

    public kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceSAXException getFaultMessage() {
        return faultMessage;
    }
}
