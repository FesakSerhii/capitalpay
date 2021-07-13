package kz.capitalpay.server.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BinIinValidator implements ConstraintValidator<BinIinConstraint, String> {

    @Override
    public boolean isValid(String iin, ConstraintValidatorContext context) {
        if (iin.length() != 12) {
            return false;
        }
        if (iin.replace(iin.substring(0, 1), "").length() == 0) {
            return false;
        }
        List<Integer> arr = Arrays.stream(iin.split("")).map(Integer::parseInt).collect(Collectors.toList());
        int summa = 1 * arr.get(0) +
                2 * arr.get(1) +
                3 * arr.get(2) +
                4 * arr.get(3) +
                5 * arr.get(4) +
                6 * arr.get(5) +
                7 * arr.get(6) +
                8 * arr.get(7) +
                9 * arr.get(8) +
                10 * arr.get(9) +
                11 * arr.get(10);
        summa = summa % 11;
        if (summa == 10) {
            summa = 3 * arr.get(0) +
                    4 * arr.get(1) +
                    5 * arr.get(2) +
                    6 * arr.get(3) +
                    7 * arr.get(4) +
                    8 * arr.get(5) +
                    9 * arr.get(6) +
                    10 * arr.get(7) +
                    11 * arr.get(8) +
                    1 * arr.get(9) +
                    2 * arr.get(10);
            summa = summa % 11;
        }
        int controlValue = arr.get(11);
        return controlValue == summa && summa > 0;
    }
}
