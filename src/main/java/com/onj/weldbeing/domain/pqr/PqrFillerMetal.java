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
public class PqrFillerMetal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pqr_info_id")
    private PqrInfo pqrInfo;

    private String process;
    private String sfaNo;
    private String awsClass;
    private String fNo;
    private String aNo;
    private String fillerProductForm;
    private Double sizeOfElectrode1;
    private Double sizeOfElectrode2;
    private Double sizeOfElectrode3;
    private Double depositWeldMetalThickness;
    private String wireFluxClass;
    private String fluxTradeName;
    private String brandName;
    private String supplemental;
    private String alloyElements;

    @Lob
    private String other;

}
