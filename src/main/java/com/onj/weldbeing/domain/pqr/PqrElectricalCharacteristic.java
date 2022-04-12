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
public class PqrElectricalCharacteristic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pqr_info_id")
    private PqrInfo pqrInfo;

    private String process;
    private String current;
    private String polarity;
    private String transferMode;
    private String tungstenElectrodeType;
    private Double tungstenElectrodeSize1;
    private Double tungstenElectrodeSize2;
    private Double tungstenElectrodeSize3;

    @Lob
    private String other;

}
