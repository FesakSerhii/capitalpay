package kz.capitalpay.server.constants;

import kz.capitalpay.server.dto.ResultDTO;

public class ErrorDictionary {

    public static final ResultDTO error101 = new ResultDTO(false, "Email used", 101);
    public static final ResultDTO error102 = new ResultDTO(false, "Confirm code not found or used", 102);

}
