package ru.utmn.chamortsev.netflix.model;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Netflix {
    @Id
    private String show_id;

    @Column
    private String type;

    @Column
    private String title;

    @Column(length = 500)
    private String directors;

    @Column(name = "cast_members", length = 1000)
    private String cast;

    @Column(length = 500)
    private String country;

    @Column
    private LocalDate date_added;

    @Column
    private Integer release_year;

    @Column
    private String rating;

    @Column
    private String duration;

    @Column(length = 1000)
    private String listed_in;

    @Column(length = 2000)
    private String description;
}
