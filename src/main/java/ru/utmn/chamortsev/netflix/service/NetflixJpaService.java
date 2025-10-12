package ru.utmn.chamortsev.netflix.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.utmn.chamortsev.netflix.model.Netflix;
import ru.utmn.chamortsev.netflix.repository.NetflixJpaRepository;

@Service
public class NetflixJpaService {
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

}
