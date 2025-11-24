package ru.utmn.chamortsev.netflix.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("JpaEngine")
class NetflixJpaServiceTest {

    @Autowired
    NetflixJpaService service;

    @Test
    void avgReleaseYearTest() {
        Double result = service.avgReleaseYear();
        assertTrue(result > 0);
        assertTrue(result >= 1900 && result <= 2024);
    }

    @Test
    void countMoviesTest() {
        Long result = service.countMovies();
        assertTrue(result >= 0);
    }

    @Test
    void countTVShowsTest() {
        Long result = service.countTVShows();
        assertTrue(result >= 0);
    }

    @Test
    void getContentStatsTest() {
        var result = service.getContentStats();
        assertNotNull(result);
        assertTrue(result.containsKey("totalMovies"));
        assertTrue(result.containsKey("totalTVShows"));
        assertTrue(result.containsKey("avgReleaseYear"));
    }
}