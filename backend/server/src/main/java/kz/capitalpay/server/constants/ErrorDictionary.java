package kz.capitalpay.server.constants;

import kz.capitalpay.server.dto.ResultDTO;

public class ErrorDictionary {

    public static final ResultDTO INVALID_SIGNATURE = new ResultDTO(false, "Invalid signature", -1);
    public static final ResultDTO EMAIL_USED = new ResultDTO(false, "Email used", 101);
    public static final ResultDTO CONFIRM_CODE_NOT_FOUND = new ResultDTO(false, "Confirm code not found or used", 102);
    public static final ResultDTO PHONE_USED = new ResultDTO(false, "Phone used", 103);
    public static final ResultDTO USER_ALREADY_EXISTS = new ResultDTO(false, "User already exists", 104);
    public static final ResultDTO OLD_PASSWORD_DOES_NOT_MATCH = new ResultDTO(false, "Old password does not match", 105);
    public static final ResultDTO USER_NOT_FOUND = new ResultDTO(false, "User not found", 106);
    public static final ResultDTO NOT_ENOUGH_RIGHTS_TO_CHANGE_ROLE = new ResultDTO(false, "Not enough rights to change the role", 107);
    public static final ResultDTO NOT_A_MERCHANT = new ResultDTO(false, "User is not a merchant", 108);
    public static final ResultDTO SUPPORT_REQUEST_NOT_FOUND = new ResultDTO(false, "Support Request not found", 109);
    public static final ResultDTO NOT_ENOUGH_RIGHTS = new ResultDTO(false, "Not enough rights", 110);
    public static final ResultDTO CURRENCY_ALREADY_EXISTS = new ResultDTO(false, "Currency already exists", 111);
    public static final ResultDTO CURRENCY_NOT_FOUND = new ResultDTO(false, "Currency not found", 112);
    public static final ResultDTO CASHBOX_NOT_FOUND = new ResultDTO(false, "Cashbox not found", 113);
    public static final ResultDTO PAYSYSTEM_NOT_FOUND = new ResultDTO(false, "Paysystem not found", 114);
    public static final ResultDTO BILL_ID_IS_TOO_LONG = new ResultDTO(false, "Bill ID > 31 char", 115);
    public static final ResultDTO BILL_ID_ALREADY_EXISTS = new ResultDTO(false, "Bill ID already exists", 116);
    public static final ResultDTO PARAM_IS_TOO_LONG = new ResultDTO(false, "Length param > 255 byte", 117);
    public static final ResultDTO PAYMENT_NOT_FOUND = new ResultDTO(false, "Payment not found", 118);
    public static final ResultDTO PAGE_NOT_FOUND = new ResultDTO(false, "Page not found", 119);
    public static final ResultDTO AVAILABLE_ONLY_FOR_ADMIN_OR_OPERATOR = new ResultDTO(false, "Action available only for admin or operator", 120);
    public static final ResultDTO AVAILABLE_ONLY_FOR_ADMIN_OPERATOR_AND_MERCHANT = new ResultDTO(false, "Action available only for admin, operator or merchant.", 121);
    public static final ResultDTO AVAILABLE_ONLY_FOR_CASHBOXES = new ResultDTO(false, "Action available only for owner cashboxes.", 122);
    public static final ResultDTO MUST_CONTAIN_12_DIGITS = new ResultDTO(false, "BIN or IIN must contain 12 digits.", 123);
    public static final ResultDTO CONTAINS_IDENTICAL_DIGITS = new ResultDTO(false, "BIN or IIN contains 12 identical digits.", 124);
    public static final ResultDTO MUST_CONTAIN_ONLY_DIGITS = new ResultDTO(false, "BIN or IIN must contains only digits.", 125);
    public static final ResultDTO INVALID_BIN_OR_IIN = new ResultDTO(false, "BIN or IIN isn't valid! Check input and try again.", 126);
    public static final ResultDTO CLIENT_FEE_GREATER_THAN_TOTAL_FEE = new ResultDTO(false, "Client fee cannot be grater than total fee.", 127);
    public static final ResultDTO CARD_REGISTRATION_ERROR = new ResultDTO(false, "Card registration error!", 129);
    public static final ResultDTO CARD_NOT_FOUND = new ResultDTO(false, "Card not found!", 130);
    public static final ResultDTO P2P_SETTINGS_NOT_FOUND = new ResultDTO(false, "P2P setiings not found!", 132);
    public static final ResultDTO AVAILABLE_ONLY_FOR_CASHBOX_OWNER = new ResultDTO(false, "Action available only for card owner!", 133);
    public static final ResultDTO P2P_IS_NOT_ALLOWED = new ResultDTO(false, "P2p is not allowed!", 134);
    public static final ResultDTO BANK_ERROR = new ResultDTO(false, "Bank error", 135);
    public static final ResultDTO CARD_ALREADY_EXISTS = new ResultDTO(false, "Card already exists!", 136);
    public static final ResultDTO EMPTY_FILE = new ResultDTO(false, "File is empty!", 137);


}

