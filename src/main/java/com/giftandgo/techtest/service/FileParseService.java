package com.giftandgo.techtest.service;

import com.giftandgo.techtest.domain.Outcome;
import com.giftandgo.techtest.domain.ValidationError;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class FileParseService {

    private static final Pattern UUID_REGEX_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    public List<Object> parseFile(MultipartFile file, boolean skipValidation) {

        List<Object> resultList = null;
        try {
            final List<String> stringList = new BufferedReader(new InputStreamReader(file.getInputStream())).lines().collect(Collectors.toList());

            resultList = IntStream.range(0, stringList.size())
                                    .mapToObj(i -> parseLine(stringList.get(i), i, skipValidation))
                                    .collect(Collectors.toList());
        } catch (IOException e) {
            resultList = new ArrayList<>();
            resultList.add(new ValidationError(e.getLocalizedMessage(), 1, ""));
        }

        return resultList;
    }

    private Object parseLine(String line, int index, boolean skipValidation) {

        String[] fields = line.split("\\|");

        if (fields.length != 7 && !skipValidation) {
            return new ValidationError("Invalid number of fields!", index + 1, line);
        }

        String uuid = getStringFromArray(fields, 0);
        String name = getStringFromArray(fields, 2);
        String transport = getStringFromArray(fields, 4);
        String avgSpeedStr = getStringFromArray(fields, 5);
        String topSpeedStr = getStringFromArray(fields, 6);

        Float avgSpeed = getNumber(avgSpeedStr);
        Float topSpeed = getNumber(topSpeedStr);

        if (!skipValidation) {
            if (avgSpeed == null || topSpeed  == null) {
                return new ValidationError("Invalid number!", index + 1, line);
            }

            if (!UUID_REGEX_PATTERN.matcher(uuid).matches()) {
                return new ValidationError("Invalid UUID!", index + 1, line);
            }
        }

        return new Outcome(name, transport, topSpeed);
    }

    private String getStringFromArray(String[] strArray, int index) {

        if (strArray.length > index) {
            return strArray[index];
        }
        return "";
    }

    private Float getNumber(String numberStr) {

        Float number = null;
        try {
            number = Float.parseFloat(numberStr);
        } catch (NumberFormatException e) {
        }
        return number;
    }
}
