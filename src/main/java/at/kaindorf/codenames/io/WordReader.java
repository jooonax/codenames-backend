package at.kaindorf.codenames.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: codenames
 * Created by: kocjod20
 * Date: 2024-05-10
 * Time: 11:12:32
 */
public class WordReader {
    public static List<String> readWords() {
        return new BufferedReader(new InputStreamReader(WordReader.class.getResourceAsStream("/nouns.csv")))
                .lines()
                .collect(Collectors.toList());
    }
}
