package com.cuongpq.hamster.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class FileUtil {

    private FileUtil() {
    }

    public static List<String> readTokens(String key) {
        try {
            File file = new File("data.txt");
            Scanner scanner = new Scanner(file);
            List<String> tokens = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String value = scanner.nextLine();
                if (!StringUtils.hasText(value)) {
                    continue;
                }
                if (value.startsWith(key)) {
                    tokens.add(value.split("=", 2)[1]);
                }

            }
            return tokens;
        } catch (Exception e) {
            log.error(e.getMessage());
            return List.of();
        }
    }
}
