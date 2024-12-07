package ru.timur.web4_back_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "point")
public class PointEntity {
    @Id
    @GeneratedValue
    private long id;

    @NotNull
    private double x;

    @NotNull
    private double y;

    @NotNull
    private double r;

    @NotNull
    private boolean hit;

    @NotNull
    private Date reqTime;

    @NotNull
    private long procTime;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
