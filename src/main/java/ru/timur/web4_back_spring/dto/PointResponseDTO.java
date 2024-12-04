package ru.timur.web4_back_spring.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.timur.web4_back_spring.entity.PointEntity;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointResponseDTO implements Serializable {
    private double x;
    private double y;
    private double r;
    private boolean hit;
    private Date reqTime;
    private long procTime;

    public PointResponseDTO(@NotNull PointEntity pointEntity) {
        this.x = pointEntity.getX();
        this.y = pointEntity.getY();
        this.r = pointEntity.getR();
        this.hit = pointEntity.isHit();
        this.reqTime = pointEntity.getReqTime();
        this.procTime = pointEntity.getProcTime();
    }
}
