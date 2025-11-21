package ru.utmn.chamortsev.netflix.security;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends CrudRepository<Person, String> {

    Person findByEmailIgnoreCase(@Param("email") String email);
}
