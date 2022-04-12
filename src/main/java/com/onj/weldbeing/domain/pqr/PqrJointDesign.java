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
public class PqrJointDesign extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pqr_info_id")
    private PqrInfo pqrInfo;

    private Double rootFaceMin;
    private Double rootFaceMax;
    private Double rootOpeningMin;
    private Double rootOpeningMax;
    private Double grooveAngleMin;
    private Double grooveAngleMax;

    @Lob
    private String other;

}
