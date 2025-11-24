package ru.utmn.chamortsev.netflix.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import ru.utmn.chamortsev.netflix.model.Netflix;
import ru.utmn.chamortsev.netflix.repository.CommonRepository;

import java.util.*;
import java.util.stream.StreamSupport;

@Service
@Profile({"CsvEngine", "JdbcEngine"})
public class NetflixService implements NetflixServiceInterface{

    CommonRepository<Netflix> repository;

    public NetflixService(
            CommonRepository<Netflix> repository,
            @Qualifier("CsvRepository") CommonRepository<Netflix> repository2) {
        this.repository = repository;

        if (repository2.getClass().equals(repository.getClass())){
            return;
        }

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
        return repository.findAll();
    }

    public Netflix getOne(String show_id){
        if (!repository.exsists(show_id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись отсутствует");
        return repository.findById(show_id);
    }

    public Netflix add(Netflix netflix) {
        if (repository.exsists(netflix.getShow_id()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Такая запись уже есть");
        return repository.save(netflix);
    }

    public void update(Netflix netflix) {
        if (!repository.exsists(netflix.getShow_id()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись отсутствует");
        repository.save(netflix);
    }

    public void delete(String show_id) {
        if (!repository.exsists(show_id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись отсутствует");
        repository.delete(show_id);
    }

    @Override
    public Double avgReleaseYear() {
        var targetStream = StreamSupport.stream(repository.findAll().spliterator(), false);
        var result = targetStream.mapToDouble(Netflix::getRelease_year).average().orElse(Double.NaN);
        return result;
    }

    @Override
    public Long countMovies() {
        var targetStream = StreamSupport.stream(repository.findAll().spliterator(), false);
        return targetStream.filter(e -> "Movie".equalsIgnoreCase(e.getType())).count();
    }

    @Override
    public Long countTVShows() {
        var targetStream = StreamSupport.stream(repository.findAll().spliterator(), false);
        return targetStream.filter(e -> "TV Show".equalsIgnoreCase(e.getType())).count();
    }

    @Override
    public Map<String, Object> getContentStats() {
        var targetStream = StreamSupport.stream(repository.findAll().spliterator(), false);
        var moviesCount = targetStream.filter(e -> "Movie".equalsIgnoreCase(e.getType())).count();

        targetStream = StreamSupport.stream(repository.findAll().spliterator(), false);
        var tvShowsCount = targetStream.filter(e -> "TV Show".equalsIgnoreCase(e.getType())).count();

        targetStream = StreamSupport.stream(repository.findAll().spliterator(), false);
        var avgYear = targetStream.mapToDouble(Netflix::getRelease_year).average().orElse(Double.NaN);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMovies", moviesCount);
        stats.put("totalTVShows", tvShowsCount);
        stats.put("avgReleaseYear", avgYear);
        stats.put("totalContent", moviesCount + tvShowsCount);

        return stats;
    }

}
