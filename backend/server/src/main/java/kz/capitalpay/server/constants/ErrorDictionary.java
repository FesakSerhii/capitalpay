package kz.capitalpay.server.constants;

import kz.capitalpay.server.dto.ResultDTO;

public class ErrorDictionary {

    public static final ResultDTO error101 = new ResultDTO(false, "Email used", 101);
    public static final ResultDTO error102 = new ResultDTO(false, "Confirm code not found or used", 102);
    public static final ResultDTO error103 = new ResultDTO(false, "Phone used", 103);
    public static final ResultDTO error104 = new ResultDTO(false, "User already exists", 104);
    public static final ResultDTO error105 = new ResultDTO(false, "Old password does not match", 105);
}
