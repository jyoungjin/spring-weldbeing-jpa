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
public class PqrBaseMetal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pqr_info_id")
    private PqrInfo pqrInfo;

    private String materialSpec;
    private String typeAndGrade;
    private String pNo;
    private String grNo;
    private Double thickness;
    private Double diameter;

    @Lob
    private String other;

}
