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
public class PqrTechnique extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pqr_info_id")
    private PqrInfo pqrInfo;

    private String process;
    private String stringerOrWeaveBead;
    private String oscillation;
    private String multiPassOrSinglePass;
    private String singleOrMultipleElectrode;
    private String closedToOutChamber;
    private String useOfThermalProcesses;

    @Lob
    private String other;

}
