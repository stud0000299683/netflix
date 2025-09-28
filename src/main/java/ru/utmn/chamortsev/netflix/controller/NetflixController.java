package ru.utmn.chamortsev.netflix.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.utmn.chamortsev.netflix.model.Netflix;
import ru.utmn.chamortsev.netflix.service.NetflixService;

import java.util.Collection;

@RestController
@RequestMapping("/api/netflix")
public class NetflixController {
    private final NetflixService netflixService;

    public NetflixController(NetflixService netflixService) {
        this.netflixService = netflixService;
    }

    @GetMapping("/hello")
    public String hello() {
        return netflixService.hello();
    }


    @GetMapping
    public Collection<Netflix> getAll() {
        return NetflixService.getAll();
    }


    @GetMapping("/{show_id}")
    public static Netflix getOne(@PathVariable("show_id") String show_id) {
        return NetflixService.getOne(show_id);
    }

//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping
//    public void add(@RequestBody Netflix netflix) {
//        NetflixService.add(netflix);
//    }

    @PostMapping
    public ResponseEntity<Netflix> add(@RequestBody Netflix netflix){
        Netflix e = netflixService.add(netflix);
        return new ResponseEntity<>(e, HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping
    public void update(@RequestBody Netflix netflix) {
        NetflixService.update(netflix);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{show_id}")
    public void delete(@PathVariable("show_id") String show_id) {
        NetflixService.delete(show_id);
    }
}