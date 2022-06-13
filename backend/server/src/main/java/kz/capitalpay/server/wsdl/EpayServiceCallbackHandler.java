/**
 * EpayServiceCallbackHandler.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */
package kz.capitalpay.server.wsdl;


/**
 *  EpayServiceCallbackHandler Callback class, Users can extend this class and implement
 *  their own receiveResult and receiveError methods.
 */
public abstract class EpayServiceCallbackHandler {
    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     * @param clientData Object mechanism by which the user can pass in user data
     * that will be avilable at the time this callback is called.
     */
    public EpayServiceCallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public EpayServiceCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */
    public Object getClientData() {
        return clientData;
    }

    /**
     * auto generated Axis2 call back method for is3DOrder method
     * override this method for handling normal response from is3DOrder operation
     */
    public void receiveResultis3DOrder(
            kz.capitalpay.server.wsdl.EpayServiceStub.Is3DOrderResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from is3DOrder operation
     */
    public void receiveErroris3DOrder(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for paymentOrderAcs method
     * override this method for handling normal response from paymentOrderAcs operation
     */
    public void receiveResultpaymentOrderAcs(
            kz.capitalpay.server.wsdl.EpayServiceStub.PaymentOrderAcsResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from paymentOrderAcs operation
     */
    public void receiveErrorpaymentOrderAcs(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for deleteCard method
     * override this method for handling normal response from deleteCard operation
     */
    public void receiveResultdeleteCard(
            kz.capitalpay.server.wsdl.EpayServiceStub.DeleteCardResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from deleteCard operation
     */
    public void receiveErrordeleteCard(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for checkOrder method
     * override this method for handling normal response from checkOrder operation
     */
    public void receiveResultcheckOrder(
            kz.capitalpay.server.wsdl.EpayServiceStub.CheckOrderResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from checkOrder operation
     */
    public void receiveErrorcheckOrder(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for airastanaPaymentAcsOrder method
     * override this method for handling normal response from airastanaPaymentAcsOrder operation
     */
    public void receiveResultairastanaPaymentAcsOrder(
            kz.capitalpay.server.wsdl.EpayServiceStub.AirastanaPaymentAcsOrderResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from airastanaPaymentAcsOrder operation
     */
    public void receiveErrorairastanaPaymentAcsOrder(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getCardList method
     * override this method for handling normal response from getCardList operation
     */
    public void receiveResultgetCardList(
            kz.capitalpay.server.wsdl.EpayServiceStub.GetCardListResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getCardList operation
     */
    public void receiveErrorgetCardList(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for refundOrderForCommerce method
     * override this method for handling normal response from refundOrderForCommerce operation
     */
    public void receiveResultrefundOrderForCommerce(
            kz.capitalpay.server.wsdl.EpayServiceStub.RefundOrderForCommerceResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from refundOrderForCommerce operation
     */
    public void receiveErrorrefundOrderForCommerce(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for cardidPaymentOrder method
     * override this method for handling normal response from cardidPaymentOrder operation
     */
    public void receiveResultcardidPaymentOrder(
            kz.capitalpay.server.wsdl.EpayServiceStub.CardidPaymentOrderResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from cardidPaymentOrder operation
     */
    public void receiveErrorcardidPaymentOrder(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for transferOrder method
     * override this method for handling normal response from transferOrder operation
     */
    public void receiveResulttransferOrder(
            kz.capitalpay.server.wsdl.EpayServiceStub.TransferOrderResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from transferOrder operation
     */
    public void receiveErrortransferOrder(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for paymentOrder method
     * override this method for handling normal response from paymentOrder operation
     */
    public void receiveResultpaymentOrder(
            kz.capitalpay.server.wsdl.EpayServiceStub.PaymentOrderResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from paymentOrder operation
     */
    public void receiveErrorpaymentOrder(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for checkForeignCard method
     * override this method for handling normal response from checkForeignCard operation
     */
    public void receiveResultcheckForeignCard(
            kz.capitalpay.server.wsdl.EpayServiceStub.CheckForeignCardResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from checkForeignCard operation
     */
    public void receiveErrorcheckForeignCard(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for controlOrderForCommerce method
     * override this method for handling normal response from controlOrderForCommerce operation
     */
    public void receiveResultcontrolOrderForCommerce(
            kz.capitalpay.server.wsdl.EpayServiceStub.ControlOrderForCommerceResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from controlOrderForCommerce operation
     */
    public void receiveErrorcontrolOrderForCommerce(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for recurrentOrder method
     * override this method for handling normal response from recurrentOrder operation
     */
    public void receiveResultrecurrentOrder(
            kz.capitalpay.server.wsdl.EpayServiceStub.RecurrentOrderResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from recurrentOrder operation
     */
    public void receiveErrorrecurrentOrder(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for formZip method
     * override this method for handling normal response from formZip operation
     */
    public void receiveResultformZip(
            kz.capitalpay.server.wsdl.EpayServiceStub.FormZipResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from formZip operation
     */
    public void receiveErrorformZip(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for checkZip method
     * override this method for handling normal response from checkZip operation
     */
    public void receiveResultcheckZip(
            kz.capitalpay.server.wsdl.EpayServiceStub.CheckZipResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from checkZip operation
     */
    public void receiveErrorcheckZip(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for airastanaPaymentOrder method
     * override this method for handling normal response from airastanaPaymentOrder operation
     */
    public void receiveResultairastanaPaymentOrder(
            kz.capitalpay.server.wsdl.EpayServiceStub.AirastanaPaymentOrderResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from airastanaPaymentOrder operation
     */
    public void receiveErrorairastanaPaymentOrder(java.lang.Exception e) {
    }
}
