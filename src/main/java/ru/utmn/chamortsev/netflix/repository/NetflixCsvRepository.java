package ru.utmn.chamortsev.netflix.repository;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import ru.utmn.chamortsev.netflix.model.Netflix;
import ru.utmn.chamortsev.netflix.service.NetflixService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class NetflixCsvRepository implements CommonRepository<Netflix> {


    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    public static final Map<String, Netflix> netfixes = new HashMap<>();


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


    @PostConstruct
    public void readAllines() {
        try (
                InputStream is = NetflixService.class.getResourceAsStream("/netflix_titles.csv")
        ) {
            assert is != null;
            try (InputStreamReader streamReader = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(streamReader);
                 CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()
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
                    netflix.setDirectors(line[3]);
                    netflix.setCast(line[4]);
                    netflix.setCountry(line[5]);
                    netflix.setDate_added(parseDate(line[6]));
                    netflix.setRelease_year(parseInteger(line[7]));
                    netflix.setRating(line[8]);
                    netflix.setDuration(line[9]);
                    netflix.setListed_in(line[10]);
                    netflix.setDescription(line[11]);
                    netfixes.put(show_id, netflix);
                }
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        } catch (CsvException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Netflix save(Netflix domain) {
        netfixes.put(domain.getShow_id(), domain);
        return netfixes.get(domain.getShow_id());
    }

    @Override
    public Iterable<Netflix> save(Collection<Netflix> domains) {
        domains.forEach(this::save);
        //domains.forEach(d -> this.save(d));
        return findAll();
    }

    @Override
    public void delete(String show_id) {
        netfixes.remove(show_id);
    }
    @Override
    public void delete(Netflix domain) {
        delete(domain.getShow_id());
    }
    @Override
    public Netflix findById(String show_id) {
        return netfixes.get(show_id);
    }
    @Override
    public Iterable<Netflix> findAll() {return netfixes.values();}
    @Override
    public boolean exsists(String show_id) {
        return netfixes.containsKey(show_id);
    }
    @Override
    public long count() {return netfixes.size();}

}
