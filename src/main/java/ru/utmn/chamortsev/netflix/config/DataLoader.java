package ru.utmn.chamortsev.netflix.config;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.utmn.chamortsev.netflix.model.Netflix;
import ru.utmn.chamortsev.netflix.repository.NetflixJpaRepository;
import ru.utmn.chamortsev.netflix.security.Person;
import ru.utmn.chamortsev.netflix.security.PersonRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
public class DataLoader {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    @Bean
    public CommandLineRunner loadData(
            PersonRepository personRepository,
            NetflixJpaRepository netflixRepository,
            BCryptPasswordEncoder passwordEncoder) {

        return args -> {
            // Загрузка пользователей через JPA
            if (personRepository.count() == 0) {
                System.out.println("=== ЗАГРУЗКА ПОЛЬЗОВАТЕЛЕЙ ===");

                Person person1 = new Person();
                person1.setEmail("matt@example.com");
                person1.setName("Matt");
                person1.setPassword(passwordEncoder.encode("secret"));
                person1.setRole("USER");
                person1.setEnabled(true);
                person1.setBirthday(LocalDate.of(1980, 7, 3));

                Person person2 = new Person();
                person2.setEmail("mike@example.com");
                person2.setName("Mike");
                person2.setPassword(passwordEncoder.encode("secret"));
                person2.setRole("ADMIN");
                person2.setEnabled(true);
                person2.setBirthday(LocalDate.of(1982, 8, 5));

                Person person3 = new Person();
                person3.setEmail("dan@example.com");
                person3.setName("Dan");
                person3.setPassword(passwordEncoder.encode("secret"));
                person3.setRole("ADMIN");
                person3.setEnabled(false);
                person3.setBirthday(LocalDate.of(1976, 10, 11));

                Person person4 = new Person();
                person4.setEmail("admin@example.com");
                person4.setName("Administrator");
                person4.setPassword(passwordEncoder.encode("admin"));
                person4.setRole("ADMIN");
                person4.setEnabled(true);
                person4.setBirthday(LocalDate.of(1978, 12, 22));

                personRepository.save(person1);
                personRepository.save(person2);
                personRepository.save(person3);
                personRepository.save(person4);

                System.out.println("=== ПОЛЬЗОВАТЕЛИ ЗАГРУЖЕНЫ: " + personRepository.count() + " ===");
            }

            // Загрузка Netflix данных из CSV
            if (netflixRepository.count() == 0) {
                System.out.println("=== ЗАГРУЗКА NETFLIX ДАННЫХ ИЗ CSV ===");

                List<Netflix> netflixList = loadNetflixFromCsv();
                netflixRepository.saveAll(netflixList);

                System.out.println("=== NETFLIX ДАННЫЕ ЗАГРУЖЕНЫ ИЗ CSV: " + netflixList.size() + " записей ===");
            }
        };
    }

    private List<Netflix> loadNetflixFromCsv() {
        List<Netflix> netflixList = new ArrayList<>();

        try (InputStream is = getClass().getResourceAsStream("/netflix_titles.csv")) {
            if (is == null) {
                System.err.println("=== ФАЙЛ netflix_titles.csv НЕ НАЙДЕН В РЕСУРСАХ ===");
                return netflixList;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                 CSVReader csvReader = new CSVReader(reader)) {

                // Пропускаем заголовок
                String[] header = csvReader.readNext();
                if (header == null) {
                    System.err.println("=== CSV ФАЙЛ ПУСТОЙ ===");
                    return netflixList;
                }

                String[] line;
                int count = 0;
                while ((line = csvReader.readNext()) != null && count < 1000) { // Ограничим для теста
                    try {
                        Netflix netflix = parseNetflixLine(line);
                        if (netflix != null) {
                            netflixList.add(netflix);
                            count++;
                        }
                    } catch (Exception e) {
                        System.err.println("Ошибка парсинга строки: " + String.join(",", line));
                        e.printStackTrace();
                    }
                }

                System.out.println("=== УСПЕШНО РАСПАРСЕНО: " + count + " записей ===");

            } catch (CsvException e) {
                throw new RuntimeException("Ошибка чтения CSV", e);
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла netflix_titles.csv", e);
        }

        return netflixList;
    }

    private Netflix parseNetflixLine(String[] line) {
        if (line.length < 12) {
            System.err.println("Недостаточно данных в строке: " + line.length);
            return null;
        }

        Netflix netflix = new Netflix();

        // show_id
        netflix.setShow_id(line[0] != null ? line[0].trim() : null);
        if (netflix.getShow_id() == null || netflix.getShow_id().isEmpty()) {
            return null;
        }

        // type
        netflix.setType(line[1] != null ? line[1].trim() : null);

        // title
        netflix.setTitle(line[2] != null ? line[2].trim() : null);

        // directors
        netflix.setDirectors(line[3] != null ? line[3].trim() : null);

        // cast
        netflix.setCast(line[4] != null ? line[4].trim() : null);

        // country
        netflix.setCountry(line[5] != null ? line[5].trim() : null);

        // date_added
        netflix.setDate_added(parseDate(line[6]));

        // release_year
        netflix.setRelease_year(parseInteger(line[7]));

        // rating
        netflix.setRating(line[8] != null ? line[8].trim() : null);

        // duration
        netflix.setDuration(line[9] != null ? line[9].trim() : null);

        // listed_in
        netflix.setListed_in(line[10] != null ? line[10].trim() : null);

        // description
        netflix.setDescription(line[11] != null ? line[11].trim() : null);

        return netflix;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
            } catch (Exception e) {
                System.err.println("Ошибка парсинга даты: " + dateStr);
                return null;
            }
        }
        return null;
    }

    private Integer parseInteger(String intStr) {
        if (intStr != null && !intStr.trim().isEmpty()) {
            try {
                return Integer.parseInt(intStr.trim());
            } catch (NumberFormatException e) {
                System.err.println("Ошибка парсинга числа: " + intStr);
                return null;
            }
        }
        return null;
    }
}