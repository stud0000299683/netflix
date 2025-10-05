package ru.utmn.chamortsev.netflix.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Netflix {
    private String show_id;
    private String type;
    private String title;
    private String directors;
    private String cast;
    private String country;
    private LocalDate date_added;
    private Integer release_year;
    private String rating;
    private String duration;
    private String listed_in;
    private String description;

}