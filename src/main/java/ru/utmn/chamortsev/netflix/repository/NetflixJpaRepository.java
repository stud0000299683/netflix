package ru.utmn.chamortsev.netflix.repository;


import org.springframework.data.repository.CrudRepository;
import ru.utmn.chamortsev.netflix.model.Netflix;


public interface NetflixJpaRepository extends CrudRepository<Netflix, String> {

}
