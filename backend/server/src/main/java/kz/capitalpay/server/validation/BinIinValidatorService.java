package kz.capitalpay.server.validation;

import kz.capitalpay.server.dto.ResultDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static kz.capitalpay.server.constants.ErrorDictionary.*;

@Service
public class BinIinValidatorService {
    public ResultDTO checkBinIin(String iinBin) {
        if (iinBin.length() != 12) {
            return error123;
        }
        if (iinBin.replace(iinBin.substring(0, 1), "").length() == 0) {
            return error124;
        }
        boolean hasNotOnlyDigits = Arrays.stream(iinBin.split(""))
                .anyMatch(s -> !Character.isDigit(s.charAt(0)));
        if (hasNotOnlyDigits) {
            return error125;
        }
        List<Integer> numbers = Arrays.stream(iinBin.split(""))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        int summa = calculateSumma(numbers);
        if (summa == 10) {
            summa = calculateSummaAgainIfItIsTen(numbers);
        }
        int controlValue = numbers.get(11);
        if (controlValue == summa && summa > 0) {
            return new ResultDTO(true, "BIN or IIN is valid!", 0);
        }
        return error126;
    }

    private int calculateSumma(List<Integer> numbers) {
        int summa = numbers.get(0) +
                2 * numbers.get(1) +
                3 * numbers.get(2) +
                4 * numbers.get(3) +
                5 * numbers.get(4) +
                6 * numbers.get(5) +
                7 * numbers.get(6) +
                8 * numbers.get(7) +
                9 * numbers.get(8) +
                10 * numbers.get(9) +
                11 * numbers.get(10);
        return summa % 11;
    }

    private int calculateSummaAgainIfItIsTen(List<Integer> numbers) {
        int summa = 3 * numbers.get(0) +
                4 * numbers.get(1) +
                5 * numbers.get(2) +
                6 * numbers.get(3) +
                7 * numbers.get(4) +
                8 * numbers.get(5) +
                9 * numbers.get(6) +
                10 * numbers.get(7) +
                11 * numbers.get(8) +
                1 * numbers.get(9) +
                2 * numbers.get(10);
        return summa % 11;
    }
}
