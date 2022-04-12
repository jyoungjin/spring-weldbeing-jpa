package com.onj.weldbeing.domain.pqr;

import com.onj.weldbeing.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class PqrPostWeldHeatTreatment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pqr_info_id")
    private PqrInfo pqrInfo;

    private int cycle;
    private Double temperatureMin;
    private Double temperatureMax;
    private String holdingTime;

    @Lob
    private String other;

}
