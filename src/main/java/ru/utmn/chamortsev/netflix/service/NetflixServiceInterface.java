package ru.utmn.chamortsev.netflix.service;

import ru.utmn.chamortsev.netflix.model.Netflix;

public interface NetflixServiceInterface {
    Iterable<Netflix> getAll();
    Netflix getOne(String show_id);
    Netflix add(Netflix netflix);
    void update(Netflix netflix);
    void delete(String show_id);
}
