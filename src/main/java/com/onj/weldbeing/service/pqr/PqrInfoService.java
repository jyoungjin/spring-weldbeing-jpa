package com.onj.weldbeing.service.pqr;

import com.onj.weldbeing.domain.pqr.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PqrInfoService {

    private final PqrInfoRepository pqrInfoRepository;
    private final PqrJointDesignRepository pqrJointDesignRepository;
    private final PqrWeldingParameterRepository pqrWeldingParameterRepository;
    private final PqrBaseMetalRepository pqrBaseMetalRepository;
    private final PqrFillerMetalRepository pqrFillerMetalRepository;
    private final PqrPositionRepository pqrPositionRepository;
    private final PqrPreheatRepository pqrPreheatRepository;
    private final PqrPostWeldHeatTreatmentRepository pqrPostWeldHeatTreatmentRepository;
    private final PqrGasRepository pqrGasRepository;
    private final PqrElectricalCharacteristicRepository pqrElectricalCharacteristicRepository;
    private final PqrTechniqueRepository pqrTechniqueRepository;

    public List<PqrInfo> getPqrInfoList(){
        return pqrInfoRepository.findAll();
    }

    public void insertPqrInfo(PqrInfo pqrInfo,
                              PqrJointDesign jointDesign,
                              List<PqrWeldingParameter> weldingParameterList,
                              List<PqrBaseMetal> baseMetalList,
                              List<PqrFillerMetal> fillerMetalList,
                              List<PqrPosition> positionList,
                              PqrPreheat preheat,
                              List<PqrPostWeldHeatTreatment> postWeldHeatTreatmentList,
                              List<PqrGas> gasList,
                              List<PqrElectricalCharacteristic> electricalCharacteristicList,
                              List<PqrTechnique> techniqueList
                              ) {

        pqrInfoRepository.save(pqrInfo);

        jointDesign.setPqrInfo(pqrInfo);
        pqrJointDesignRepository.save(jointDesign);

        for (PqrWeldingParameter pqrWeldingParameter : weldingParameterList) {
            pqrWeldingParameter.setPqrInfo(pqrInfo);
            pqrWeldingParameterRepository.save(pqrWeldingParameter);
        }

        for (PqrBaseMetal pqrBaseMetal : baseMetalList) {
            pqrBaseMetal.setPqrInfo(pqrInfo);
            pqrBaseMetalRepository.save(pqrBaseMetal);
        }

        for (PqrFillerMetal pqrFillerMetal : fillerMetalList) {
            pqrFillerMetal.setPqrInfo(pqrInfo);
            pqrFillerMetalRepository.save(pqrFillerMetal);
        }

        for (PqrPosition pqrPosition : positionList) {
            pqrPosition.setPqrInfo(pqrInfo);
            pqrPositionRepository.save(pqrPosition);
        }

        preheat.setPqrInfo(pqrInfo);
        pqrPreheatRepository.save(preheat);

        for (PqrPostWeldHeatTreatment pqrPostWeldHeatTreatment : postWeldHeatTreatmentList) {
            pqrPostWeldHeatTreatment.setPqrInfo(pqrInfo);
            pqrPostWeldHeatTreatmentRepository.save(pqrPostWeldHeatTreatment);
        }

        for (PqrGas pqrGas : gasList) {
            pqrGas.setPqrInfo(pqrInfo);
            pqrGasRepository.save(pqrGas);
        }

        for (PqrElectricalCharacteristic pqrElectricalCharacteristic : electricalCharacteristicList) {
            pqrElectricalCharacteristic.setPqrInfo(pqrInfo);
            pqrElectricalCharacteristicRepository.save(pqrElectricalCharacteristic);
        }

        for (PqrTechnique pqrTechnique : techniqueList) {
            pqrTechnique.setPqrInfo(pqrInfo);
            pqrTechniqueRepository.save(pqrTechnique);
        }

    }

}
