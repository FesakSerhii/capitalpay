/**
 * EpayServiceInvalidKeyExceptionException.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */
package kz.capitalpay.server.wsdl;

public class EpayServiceInvalidKeyExceptionException extends java.lang.Exception {
    private static final long serialVersionUID = 1637588635322L;
    private kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceInvalidKeyException faultMessage;

    public EpayServiceInvalidKeyExceptionException() {
        super("EpayServiceInvalidKeyExceptionException");
    }

    public EpayServiceInvalidKeyExceptionException(java.lang.String s) {
        super(s);
    }

    public EpayServiceInvalidKeyExceptionException(java.lang.String s,
                                                   java.lang.Throwable ex) {
        super(s, ex);
    }

    public EpayServiceInvalidKeyExceptionException(java.lang.Throwable cause) {
        super(cause);
    }

    public void setFaultMessage(
            kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceInvalidKeyException msg) {
        faultMessage = msg;
    }

    public kz.capitalpay.server.wsdl.EpayServiceStub.EpayServiceInvalidKeyException getFaultMessage() {
        return faultMessage;
    }
}
