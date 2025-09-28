package ru.utmn.chamortsev.netflix.model;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Netflix {
    String show_id;
    String type;
    String title;
    List<String> directors;
    List<String> cast;
    String country;
    LocalDate date_added;
    Integer release_year;
    String rating;
    String duration;
    List<String> listed_in;
    String description;


}
