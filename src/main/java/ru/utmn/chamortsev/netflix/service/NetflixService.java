package ru.utmn.chamortsev.netflix.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.utmn.chamortsev.netflix.model.Netflix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NetflixService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    public static final Map<String, Netflix> netfixes = new HashMap<>();


    @PostConstruct
    public void readAllines() {
        try (
                InputStream is = NetflixService.class.getResourceAsStream("/netflix_titles.csv");
                InputStreamReader streamReader = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(streamReader);
                CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
                ){
                List<String[]> lines = csvReader.readAll();
            for (String[] line : lines) {
                Netflix netflix = new Netflix();
                String show_id =line[0];
                if (show_id==null)
                    continue;
                netflix.setShow_id(show_id);
                netflix.setType(line[1]);
                netflix.setTitle(line[2]);
                netflix.setDirectors(parseStringList(line[3]));
                netflix.setCast(parseStringList(line[4]));
                netflix.setCountry(line[5]);
                netflix.setDate_added(parseDate(line[6]));
                netflix.setRelease_year(parseInteger(line[7]));
                netflix.setRating(line[8]);
                netflix.setDuration(line[9]);
                netflix.setListed_in(parseStringList(line[10]));
                netflix.setDescription(line[11]);
                netfixes.put(show_id, netflix);
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        } catch (CsvException e){
            throw new RuntimeException(e);
        }
    }

    private List<String> parseStringList(String data) {
        List<String> result = new ArrayList<>();
        if (data != null && !data.trim().isEmpty() && !data.trim().equals("''")) {
            String[] items = data.trim().split(",\\s*");
            for (String item : items) {
                String trimmedItem = item.trim();
                if (!trimmedItem.isEmpty() && !trimmedItem.equals("''")) {
                    result.add(trimmedItem);
                }
            }
        }
        return result;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        }
        return null;
    }

    private Integer parseInteger(String intStr) {
        if (intStr != null && !intStr.trim().isEmpty()) {
            return Integer.parseInt(intStr.trim());
        }
        return null;
    }

    public String hello(){
        return"Hello Word";
    }

    public static Collection<Netflix> getAll(){
        return netfixes.values();
    }

    public static Netflix getOne(String show_id){
        if (!netfixes.containsKey(show_id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись отсутствует");
        return netfixes.get(show_id);
    }

    public static Netflix add(Netflix netflix) {
        if (netfixes.containsKey(netflix.getShow_id()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Такая запись уже есть");
        netfixes.put(netflix.getShow_id(), netflix);
        return netflix;
    }

    public static void update(Netflix netflix) {
        if (!netfixes.containsKey(netflix.getShow_id()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись отсутствует");
        netfixes.put(netflix.getShow_id(), netflix);
    }

    public static void delete(String show_id) {
        if (!netfixes.containsKey(show_id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись отсутствует");
        netfixes.remove(show_id);
    }

}
