package ru.utmn.chamortsev.netflix.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.utmn.chamortsev.netflix.model.Netflix;

import ru.utmn.chamortsev.netflix.service.NetflixServiceInterface;


@RestController
@RequestMapping("/api/netflix")
public class NetflixController {

    private final NetflixServiceInterface netflixService;

    public NetflixController(NetflixServiceInterface netflixService) {
        this.netflixService = netflixService;
    }

//    @GetMapping("/hello")
//    public String hello() {
//        return netflixService.hello();
//    }


    @GetMapping
    public Iterable<Netflix> getAll() {
        return netflixService.getAll();
    }


    @GetMapping("/{show_id}")
    public Netflix getOne(@PathVariable("show_id") String show_id) {
        return netflixService.getOne(show_id);
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
        netflixService.update(netflix);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{show_id}")
    public void delete(@PathVariable("show_id") String show_id) {
        netflixService.delete(show_id);
    }
}