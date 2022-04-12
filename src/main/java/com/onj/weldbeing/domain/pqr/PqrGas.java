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
public class PqrGas extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pqr_info_id")
    private PqrInfo pqrInfo;

    private String process;
    private String shieldingGas;
    private String shieldingGasComposition;
    private Double shieldingGasFlowMin;
    private Double shieldingGasFlowMax;
    private String backingGas;
    private String backingGasComposition;
    private Double backingGasFlowMin;
    private Double backingGasFlowMax;
    private String trailingGas;
    private String trailingGasComposition;
    private Double trailingGasFlowMin;
    private Double trailingGasFlowMax;

    @Lob
    private String other;

}
