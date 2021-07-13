package kz.capitalpay.server.constants;

import kz.capitalpay.server.dto.ResultDTO;

public class ErrorDictionary {

    public static final ResultDTO error101 = new ResultDTO(false, "Email used", 101);
    public static final ResultDTO error102 = new ResultDTO(false, "Confirm code not found or used", 102);
    public static final ResultDTO error103 = new ResultDTO(false, "Phone used", 103);
    public static final ResultDTO error104 = new ResultDTO(false, "User already exists", 104);
    public static final ResultDTO error105 = new ResultDTO(false, "Old password does not match", 105);
    public static final ResultDTO error106 = new ResultDTO(false, "User not found", 106);
    public static final ResultDTO error107 = new ResultDTO(false, "Not enough rights to change the role", 107);
    public static final ResultDTO error108 = new ResultDTO(false, "User is not a merchant", 108);
    public static final ResultDTO error109 = new ResultDTO(false, "Support Request not found", 109);
    public static final ResultDTO error110 = new ResultDTO(false, "Not enough rights", 110);
    public static final ResultDTO error111 = new ResultDTO(false, "Currency already exists", 111);
    public static final ResultDTO error112 = new ResultDTO(false, "Currency not found", 112);
    public static final ResultDTO error113 = new ResultDTO(false, "Cashbox not found", 113);
    public static final ResultDTO error114 = new ResultDTO(false, "Paysystem not found", 114);
    public static final ResultDTO error115 = new ResultDTO(false, "Bill ID > 31 char", 115);
    public static final ResultDTO error116 = new ResultDTO(false, "Bill ID already exists", 116);
    public static final ResultDTO error117 = new ResultDTO(false, "Length param > 255 byte", 117);
    public static final ResultDTO error118 = new ResultDTO(false, "Payment not found", 118);
    public static final ResultDTO error119 = new ResultDTO(false, "Page not found", 119);
    public static final ResultDTO error120 = new ResultDTO(false, "Action available only for admin or operator", 120);
    public static final ResultDTO error121 = new ResultDTO(false, "Action available only for admin, operator or merchant.", 121);
    public static final ResultDTO error122 = new ResultDTO(false, "Action available only for owner cashboxes.", 122);
    public static final ResultDTO error123 = new ResultDTO(false, "BIN or IIN must contain 12 digits.", 123);
    public static final ResultDTO error124 = new ResultDTO(false, "BIN or IIN contains 12 identical digits.", 124);
    public static final ResultDTO error125 = new ResultDTO(false, "BIN or IIN must contains only digits.", 125);
    public static final ResultDTO error126 = new ResultDTO(false, "BIN or IIN isn't valid! Check input and try again.", 126);
}
