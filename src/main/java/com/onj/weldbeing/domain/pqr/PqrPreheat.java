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
public class PqrPreheat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pqr_info_id")
    private PqrInfo pqrInfo;

    private Double preheatTempMin;
    private Double preheatTempMax;
    private Double interpassTempMin;
    private Double interpassTempMax;

    @Lob
    private String other;

}
