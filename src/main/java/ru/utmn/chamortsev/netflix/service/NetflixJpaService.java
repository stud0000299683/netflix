package ru.utmn.chamortsev.netflix.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.utmn.chamortsev.netflix.model.Netflix;
import ru.utmn.chamortsev.netflix.repository.NetflixCsvRepository;
import ru.utmn.chamortsev.netflix.repository.NetflixJpaRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Profile("JpaEngine")
public class NetflixJpaService  implements NetflixServiceInterface{
    private final NetflixJpaRepository repository;

    public NetflixJpaService(NetflixJpaRepository repository){
        this.repository = repository;
    }

    public Iterable<Netflix> getAll(){
        return repository.findAll();
    }

    public Netflix getOne(String show_id){
        return repository.findById(show_id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись отсутствует"));
    }

    public Netflix add(Netflix netflix) {
        if (repository.existsById(netflix.getShow_id()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Такая запись уже есть");
        return repository.save(netflix);
    }

    public void update(Netflix netflix) {
        if (!repository.existsById(netflix.getShow_id()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись отсутствует");
        repository.save(netflix);
    }

    public void delete(String show_id) {
        if (!repository.existsById(show_id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Запись отсутствует");
        repository.deleteById(show_id);
    }

    @Override
    public Double avgReleaseYear() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .mapToDouble(Netflix::getRelease_year)
                .average()
                .orElse(Double.NaN);
    }

    @Override
    public Long countMovies() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(e -> e.getType() != null && e.getType().equalsIgnoreCase("Movie"))
                .count();
    }

    @Override
    public Long countTVShows() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(e -> e.getType() != null && e.getType().equalsIgnoreCase("TV Show"))
                .count();
    }

    @Override
    public Map<String, Object> getContentStats() {
        List<Netflix> allItems = StreamSupport.stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        long moviesCount = countMovies();
        long tvShowsCount = countTVShows();
        double avgYear = avgReleaseYear();

        var ratingStats = allItems.stream()
                .filter(e -> e.getRating() != null)
                .collect(Collectors.groupingBy(
                        Netflix::getRating,
                        Collectors.counting()
                ));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMovies", moviesCount);
        stats.put("totalTVShows", tvShowsCount);
        stats.put("totalContent", moviesCount + tvShowsCount);
        stats.put("avgReleaseYear", avgYear);
        stats.put("ratingDistribution", ratingStats);

        return stats;
    }


    public Map<String, Long> getRatingStatistics() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(e -> e.getRating() != null)
                .collect(Collectors.groupingBy(
                        Netflix::getRating,
                        Collectors.counting()
                ));
    }


    public Map<Integer, Long> getContentByYear() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(e -> e.getRelease_year() != null)
                .collect(Collectors.groupingBy(
                        Netflix::getRelease_year,
                        Collectors.counting()
                ));
    }
}