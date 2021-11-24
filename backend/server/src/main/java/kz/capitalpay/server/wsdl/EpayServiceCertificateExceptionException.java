/**
 * EpayServiceCertificateExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */
package kz.capitalpay.server.wsdl;

public class EpayServiceCertificateExceptionException extends java.lang.Exception {
    private static final long serialVersionUID = 1637588635311L;
    private kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceCertificateException faultMessage;

    public EpayServiceCertificateExceptionException() {
        super("EpayServiceCertificateExceptionException");
    }

    public EpayServiceCertificateExceptionException(java.lang.String s) {
        super(s);
    }

    public EpayServiceCertificateExceptionException(java.lang.String s,
        java.lang.Throwable ex) {
        super(s, ex);
    }

    public EpayServiceCertificateExceptionException(java.lang.Throwable cause) {
        super(cause);
    }

    public void setFaultMessage(
        kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceCertificateException msg) {
        faultMessage = msg;
    }

    public kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceCertificateException getFaultMessage() {
        return faultMessage;
    }
}
