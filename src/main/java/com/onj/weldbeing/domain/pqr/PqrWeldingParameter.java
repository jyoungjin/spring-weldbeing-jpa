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
public class PqrWeldingParameter extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pqr_info_id")
    private PqrInfo pqrInfo;

    private String process;
    private String action;
    private String beadNo;
    private String electrode;
    private Double electrodeSize;
    private Double ampMin;
    private Double ampMax;
    private Double voltMin;
    private Double voltMax;
    private Double speedMin;
    private Double speedMax;
    private Double heatInputMin;
    private Double heatInputMax;

    @Lob
    private String other;

}
