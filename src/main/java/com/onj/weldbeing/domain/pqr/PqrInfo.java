package com.onj.weldbeing.domain.pqr;

import com.onj.weldbeing.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class PqrInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String weldingCode;
    private String company;
    private String pqrNo;
    private LocalDate pqrDate;
    private String wpsNo;
    private int wpsRevNo;
    private String weldingProcess1;
    private String weldingProcess2;
    private String weldingProcess3;
    private String weldingType1;
    private String weldingType2;
    private String weldingType3;
    private String originFileName;

    @Lob
    private String other;

    @OneToMany(mappedBy = "pqrInfo")
    private List<PqrWeldingParameter> pqrWeldingParameterList = new ArrayList<>();

    @OneToOne(mappedBy = "pqrInfo")
    private PqrJointDesign pqrJointDesign;

}
