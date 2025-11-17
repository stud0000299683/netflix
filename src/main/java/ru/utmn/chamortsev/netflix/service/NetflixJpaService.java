package ru.utmn.chamortsev.netflix.service;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.utmn.chamortsev.netflix.model.Netflix;
import ru.utmn.chamortsev.netflix.repository.NetflixCsvRepository;
import ru.utmn.chamortsev.netflix.repository.NetflixJpaRepository;

import java.util.Collection;
import java.util.stream.StreamSupport;

@Service
@Profile("JpaEngine")
public class NetflixJpaService  implements NetflixServiceInterface{
    private final NetflixJpaRepository repository;
    private final NetflixCsvRepository csvRepository;

    public NetflixJpaService(NetflixJpaRepository repository, NetflixCsvRepository csvRepository){
        this.repository = repository;
        this.csvRepository = csvRepository;

        // Инициализация данных из CSV репозитория, если JPA репозиторий пустой
        if(repository.count() == 0 && csvRepository.count() > 0){
            Iterable<Netflix> all = csvRepository.findAll();
            Collection<Netflix> collection = StreamSupport.stream(all.spliterator(), false).toList();
            repository.saveAll(collection);
        }
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
}