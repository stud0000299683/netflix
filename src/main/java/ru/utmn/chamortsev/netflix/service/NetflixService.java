package ru.utmn.chamortsev.netflix.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.utmn.chamortsev.netflix.model.Netflix;
import ru.utmn.chamortsev.netflix.repository.NetflixCsvRepository;
import ru.utmn.chamortsev.netflix.repository.NetflixJdbcRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class NetflixService {

    NetflixCsvRepository repository;
    NetflixJdbcRepository repository2;


    public NetflixService(NetflixCsvRepository repository, NetflixJdbcRepository repository2) {
        this.repository = repository;
        this.repository2 = repository2;
        if(repository2.count() == 0 && repository.count() >0){
            Iterable<Netflix> all = repository.findAll();
            Collection<Netflix> collection = StreamSupport.stream(all.spliterator(), false).toList();
            repository2.save(collection);
        }
    }

    public String hello(){
        return"Hello Word";
    }

    public Iterable<Netflix> getAll(){
        return repository2.findAll();
    }

    public Netflix getOne(String show_id){
        if (!repository2.exsists(show_id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись отсутствует");
        return repository2.findById(show_id);
    }

    public Netflix add(Netflix netflix) {
        if (repository2.exsists(netflix.getShow_id()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Такая запись уже есть");
        return repository2.save(netflix);
    }

    public void update(Netflix netflix) {
        if (!repository2.exsists(netflix.getShow_id()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись отсутствует");
        repository2.save(netflix);
    }

    public void delete(String show_id) {
        if (!repository2.exsists(show_id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись отсутствует");
        repository2.delete(show_id);
    }

}
