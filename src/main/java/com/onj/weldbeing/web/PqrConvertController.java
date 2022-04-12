package com.onj.weldbeing.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.onj.weldbeing.config.KeyData;
import com.onj.weldbeing.domain.pqr.*;
import com.onj.weldbeing.service.pqr.PqrInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PqrConvertController {

    private final KeyData keyData;
    private final PqrInfoService pqrInfoService;

    @GetMapping("/company01/insert")
    public ArrayList<PqrInfo> getJsonFiles() throws IOException, JSONException, IllegalAccessException {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        String pattern = "json/01. Spraying Systems/*.json";

        ArrayList<PqrInfo> pqrList = new ArrayList<>();

        Resource[] resources = null;

        try {
            resources = resourcePatternResolver.getResources(pattern);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Resource resource : resources) {
            File file = resource.getFile();
            String jsonString = new String(Files.readAllBytes(file.toPath()));
            JSONObject pqrJson = new JSONObject(jsonString);

            PqrInfo pqrInfo = new PqrInfo();
            PqrJointDesign jointDesign = new PqrJointDesign();
            List<PqrWeldingParameter> weldingParameterList = new ArrayList<>();
            List<PqrBaseMetal> baseMetalList = new ArrayList<>();
            List<PqrFillerMetal> fillerMetalList = new ArrayList<>();
            List<PqrPosition> positionList = new ArrayList<>();
            PqrPreheat preheat = new PqrPreheat();
            List<PqrPostWeldHeatTreatment> postWeldHeatTreatmentList = new ArrayList<>();
            List<PqrGas> gasList = new ArrayList<>();
            List<PqrElectricalCharacteristic> electricalCharacteristicList = new ArrayList<>();
            List<PqrTechnique> techniqueList = new ArrayList<>();

            // pqrInfo 섹션 전처리
            for (String key : keyData.pqrKey.get("pqrInfo")) {
                if (!pqrJson.isNull(key)) {
                    pqrInfo = makePQRInfo(pqrJson.getJSONObject(key));
                    break;
                }
            }

            // weldingProcess 개수 파악
            ArrayList<String> weldingProcesses = new ArrayList<>();
            if (pqrInfo.getWeldingProcess1() != null) {
                weldingProcesses.add(pqrInfo.getWeldingProcess1());
            }
            if(pqrInfo.getWeldingProcess2() != null){
                weldingProcesses.add(pqrInfo.getWeldingProcess2());
            }
            if(pqrInfo.getWeldingProcess3() != null){
                weldingProcesses.add(pqrInfo.getWeldingProcess3());
            }

            // joint design 전처리
            for (String key : keyData.pqrKey.get("jointDesign")) {
                if (!pqrJson.isNull(key)) {
                    jointDesign = makeJointDesign(pqrJson.getJSONObject(key), pqrInfo);
                    break;
                }
            }

            // weldingParameters 전처리
            for (String key : keyData.pqrKey.get("weldingParameters")) {
                if (!pqrJson.isNull(key)) {
                    weldingParameterList = makeWeldingParameter(pqrJson.getJSONArray(key), pqrInfo);
                    break;
                }
            }

            // base metal 전처리
            for (String key : keyData.pqrKey.get("baseMetals")) {
                if (!pqrJson.isNull(key)) {
                    baseMetalList = makeBaseMetals(pqrJson.getJSONObject(key), pqrInfo);
                    break;
                }
            }

            // filler metal 전처리
            for (String key : keyData.pqrKey.get("fillerMetals")) {
                if (!pqrJson.isNull(key)) {
                    fillerMetalList = makeFillerMetals(pqrJson.getJSONObject(key), weldingProcesses, pqrInfo);
                    break;
                }
            }

            // position 전처리
            for (String key : keyData.pqrKey.get("positions")) {
                if (!pqrJson.isNull(key)) {
                    positionList = makePositions(pqrJson.getJSONObject(key), weldingProcesses, pqrInfo);
                    break;
                }
            }

            // preheat 전처리
            for (String key : keyData.pqrKey.get("preheats")) {
                if (!pqrJson.isNull(key)) {
                    preheat = makePreheat(pqrJson.getJSONObject(key), pqrInfo);
                    break;
                }
            }

            // postWeldHeatTreatment 전처리
            for (String key : keyData.pqrKey.get("postWeldHeatTreatments")) {
                if (!pqrJson.isNull(key)) {
                    postWeldHeatTreatmentList = makePostWeldHeatTreatments(pqrJson.getJSONObject(key), weldingProcesses, pqrInfo);
                    break;
                }
            }

            // gas 전처리
            for (String key : keyData.pqrKey.get("gases")) {
                if (!pqrJson.isNull(key)) {
                    gasList = makeGases(pqrJson.getJSONObject(key), weldingProcesses, pqrInfo);
                    break;
                }
            }

            // electricalCharacteristic 전처리
            for (String key : keyData.pqrKey.get("electricalCharacteristics")) {
                if (!pqrJson.isNull(key)) {
                    electricalCharacteristicList = makeElectricalCharacteristics(pqrJson.getJSONObject(key), weldingProcesses, pqrInfo);
                    break;
                }
            }

            // technique 전처리
            for (String key : keyData.pqrKey.get("techniques")) {
                if (!pqrJson.isNull(key)) {
                    techniqueList = makeTechniques(pqrJson.getJSONObject(key), weldingProcesses, pqrInfo);
                    break;
                }
            }

            pqrList.add(pqrInfo);

            pqrInfoService.insertPqrInfo(pqrInfo
                    ,jointDesign
                    ,weldingParameterList
                    ,baseMetalList
                    ,fillerMetalList
                    ,positionList
                    ,preheat
                    ,postWeldHeatTreatmentList
                    ,gasList
                    ,electricalCharacteristicList
                    ,techniqueList
            );

        }
        return pqrList;
    }

    // pqrInfo data 전처리
    private PqrInfo makePQRInfo(JSONObject pqrInfoJson) throws JSONException, IllegalAccessException {
        PqrInfo pqrInfo = new PqrInfo();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate date = LocalDate.parse(pqrInfoJson.getString("date").trim(), formatter);
        String[] types = pqrInfoJson.getString("type").split("\\+");

        pqrInfo.setPqrDate(date);
        pqrInfo.setWeldingCode("ASME");
        pqrInfo.setWeldingProcess1(pqrInfoJson.getString("welding_process1"));
        if(!pqrInfoJson.getString("welding_process2").trim().isEmpty()){
            pqrInfo.setWeldingProcess2(pqrInfoJson.getString("welding_process2"));
        }
        if(!pqrInfoJson.getString("welding_process3").trim().isEmpty()){
            pqrInfo.setWeldingProcess3(pqrInfoJson.getString("welding_process3"));
        }

        for (int i = 0; i < types.length; i++) {
            if (i == 0) {
                pqrInfo.setWeldingType1(types[0]);
            }else if(i==1){
                pqrInfo.setWeldingType2(types[1]);
            }
            else if(i==2){
                pqrInfo.setWeldingType3(types[2]);
            }
        }

        pqrInfoJson.remove("date");
        pqrInfoJson.remove("welding_process1");
        pqrInfoJson.remove("welding_process2");
        pqrInfoJson.remove("welding_process3");
        pqrInfoJson.remove("type");

        for (String key : keyData.pqrKey.getOrDefault("pqrNo", new HashSet<>())) {
            if (!pqrInfoJson.isNull(key)) {
                pqrInfo.setPqrNo(pqrInfoJson.getString(key).trim());
                pqrInfoJson.remove(key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("wpsNo", new HashSet<>())) {
            if (!pqrInfoJson.isNull(key)) {
                pqrInfo.setWpsNo(pqrInfoJson.getString(key).trim());
                pqrInfoJson.remove(key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("company", new HashSet<>())) {
            if (!pqrInfoJson.isNull(key)) {
                pqrInfo.setCompany(pqrInfoJson.getString(key).trim());
                pqrInfoJson.remove(key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("originalFileName", new HashSet<>())) {
            if (!pqrInfoJson.isNull(key)) {
                pqrInfo.setOriginFileName(pqrInfoJson.getString(key).trim());
                pqrInfoJson.remove(key);
            }
        }

        if(!pqrInfoJson.toString().equals("{}")){
            pqrInfo.setOther(pqrInfoJson.toString());
        }

        return pqrInfo;
    }

    // joint design data 전처리
    private PqrJointDesign makeJointDesign(JSONObject jointDesignJson, PqrInfo pqr) throws JSONException, IllegalAccessException {
        PqrJointDesign jointDesign = new PqrJointDesign();

        for (String key : keyData.pqrKey.getOrDefault("rootFace", new HashSet<>())) {
            if (!jointDesignJson.isNull(key)) {
                String rootFace = jointDesignJson.getString(key).trim().replaceAll("mm","");
                Double rootFaceMin;
                Double rootFaceMax;
                if(!rootFace.isEmpty()){
                    if(rootFace.contains("~")){
                        String[] ampsArr = rootFace.split("~");
                        rootFaceMin = Double.valueOf(ampsArr[0]);
                        rootFaceMax = Double.valueOf(ampsArr[1]);
                    }else{
                        rootFaceMin = Double.valueOf(rootFace);
                        rootFaceMax = Double.valueOf(rootFace);
                    }
                    jointDesign.setRootFaceMin(rootFaceMin);
                    jointDesign.setRootFaceMax(rootFaceMax);
                    jointDesignJson.remove(key);
                }
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("rootOpening", new HashSet<>())) {
            if (!jointDesignJson.isNull(key)) {
                String rootOpening = jointDesignJson.getString(key).trim().replaceAll("mm","");
                Double rootOpeningMin;
                Double rootOpeningMax;
                if(!rootOpening.isEmpty()) {
                    if (rootOpening.contains("~")) {
                        String[] ampsArr = rootOpening.split("~");
                        rootOpeningMin = Double.valueOf(ampsArr[0]);
                        rootOpeningMax = Double.valueOf(ampsArr[1]);
                    } else {
                        rootOpeningMin = Double.valueOf(rootOpening);
                        rootOpeningMax = Double.valueOf(rootOpening);
                    }
                    jointDesign.setRootOpeningMin(rootOpeningMin);
                    jointDesign.setRootOpeningMax(rootOpeningMax);
                    jointDesignJson.remove(key);
                }
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("grooveAngle", new HashSet<>())) {
            if (!jointDesignJson.isNull(key)) {
                String grooveAngle = jointDesignJson.getString(key).trim().replaceAll("°","");
                Double grooveAngleMin;
                Double grooveAngleMax;
                if(!grooveAngle.isEmpty()) {
                    if (grooveAngle.contains("~")) {
                        String[] ampsArr = grooveAngle.split("~");
                        grooveAngleMin = Double.valueOf(ampsArr[0]);
                        grooveAngleMax = Double.valueOf(ampsArr[1]);
                    } else {
                        grooveAngleMin = Double.valueOf(grooveAngle);
                        grooveAngleMax = Double.valueOf(grooveAngle);
                    }
                    jointDesign.setGrooveAngleMin(grooveAngleMin);
                    jointDesign.setGrooveAngleMax(grooveAngleMax);
                    jointDesignJson.remove(key);
                }
            }
        }

        if(!jointDesignJson.toString().trim().equals("{}")){
            jointDesign.setOther(jointDesignJson.toString());
        }

        return jointDesign;
    }

    // welding parameter data 전처리
    private List<PqrWeldingParameter> makeWeldingParameter(JSONArray weldingParametersJson, PqrInfo pqrInfo) throws JSONException, IllegalAccessException {

        ArrayList<PqrWeldingParameter> weldingParameters = new ArrayList<>();

        for (int i = 0; i < weldingParametersJson.length(); i++) {
            JSONObject weldingParameterJson = weldingParametersJson.getJSONObject(i);
            PqrWeldingParameter weldingParameter = new PqrWeldingParameter();

            if (weldingParameterJson.has("action")) {
                weldingParameter.setAction(weldingParameterJson.getString("action").trim());
            }else{
                for (String key : keyData.pqrKey.getOrDefault("process", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        weldingParameter.setProcess(weldingParameterJson.getString(key).trim());
                        weldingParameterJson.remove(key);
                    }
                }

                for (String key : keyData.pqrKey.getOrDefault("beadNo", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        weldingParameter.setBeadNo(weldingParameterJson.getString(key).trim());
                        weldingParameterJson.remove(key);
                    }
                }

                for (String key : keyData.pqrKey.getOrDefault("electrode", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        weldingParameter.setElectrode(weldingParameterJson.getString(key).trim());
                        weldingParameterJson.remove(key);
                    }
                }

                for (String key : keyData.pqrKey.getOrDefault("electrodeSize", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        String electrodeSize = weldingParameterJson.getString(key).trim().replaceAll("ø","");
                        if(electrodeSize.contains(",")){
                            electrodeSize = electrodeSize.split(",")[0];
                        }
                        weldingParameter.setElectrodeSize(Double.valueOf(electrodeSize));
                        weldingParameterJson.remove(key);
                    }
                }

                for (String key : keyData.pqrKey.getOrDefault("amps", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        String amps = weldingParameterJson.getString(key).trim();
                        Double ampMin;
                        Double ampMax;
                        if(amps.contains("~")){
                            String[] ampsArr = amps.split("~");
                            ampMin = Double.valueOf(ampsArr[0]);
                            ampMax = Double.valueOf(ampsArr[1]);
                        }else{
                            ampMin = Double.valueOf(amps);
                            ampMax = Double.valueOf(amps);
                        }
                        weldingParameter.setAmpMin(ampMin);
                        weldingParameter.setAmpMax(ampMax);
                        weldingParameterJson.remove(key);
                    }
                }

                for (String key : keyData.pqrKey.getOrDefault("volt", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        String amps = weldingParameterJson.getString(key).trim();
                        Double voltMin;
                        Double voltMax;
                        if(amps.contains("~")){
                            String[] ampsArr = amps.split("~");
                            voltMin = Double.valueOf(ampsArr[0]);
                            voltMax = Double.valueOf(ampsArr[1]);
                        }else{
                            voltMin = Double.valueOf(amps);
                            voltMax = Double.valueOf(amps);
                        }
                        weldingParameter.setVoltMin(voltMin);
                        weldingParameter.setVoltMax(voltMax);
                        weldingParameterJson.remove(key);
                    }
                }

                for (String key : keyData.pqrKey.getOrDefault("speed", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        String speed = weldingParameterJson.getString(key).trim();
                        Double speedMin;
                        Double speedMax;
                        if(!speed.trim().isEmpty()){

                            if(speed.contains("~")){
                                String[] speedArr = speed.split("~");
                                speedMin = Double.valueOf(speedArr[0]);
                                speedMax = Double.valueOf(speedArr[1]);
                            }else{
                                speedMin = Double.valueOf(speed);
                                speedMax = Double.valueOf(speed);
                            }

                            if(key.toLowerCase().contains("cm/min")){
                                speedMin = cmminTommmin(speedMin);
                                speedMax = cmminTommmin(speedMax);
                            }else if(key.toLowerCase().contains("im/min")){
                                speedMin = inminTommmin(speedMin);
                                speedMax = inminTommmin(speedMax);
                            }

                            weldingParameter.setSpeedMin(speedMin);
                            weldingParameter.setSpeedMax(speedMax);
                        }
                        weldingParameterJson.remove(key);
                    }
                }

                for(String key : keyData.pqrKey.getOrDefault("heatInput", new HashSet<>())){
                    if (!weldingParameterJson.isNull(key)) {
                        String heatInput = weldingParameterJson.getString(key).trim();
                        Double heatInputMin;
                        Double heatInputMax;
                        if(!heatInput.trim().isEmpty()){
                            if(heatInput.contains("~")){
                                String[] heatInputArr = heatInput.split("~");
                                heatInputMin = Double.valueOf(heatInputArr[0]);
                                heatInputMax = Double.valueOf(heatInputArr[1]);
                            }else{
                                heatInputMin = Double.valueOf(heatInput);
                                heatInputMax = Double.valueOf(heatInput);
                            }

                            if(key.toLowerCase().contains("kj/cm")){
                                heatInputMin = kjcmTokjmm(heatInputMin);
                                heatInputMax = kjcmTokjmm(heatInputMax);
                            }
                            weldingParameter.setHeatInputMin(heatInputMin);
                            weldingParameter.setHeatInputMax(heatInputMax);
                        }
                        weldingParameterJson.remove(key);
                    }
                }
                if(!weldingParameterJson.toString().trim().equals("{}")){
                    weldingParameter.setOther(weldingParameterJson.toString());
                }

            }

            weldingParameters.add(weldingParameter);
        }

        return weldingParameters;
    }

    // base metal 전처리
    private ArrayList<PqrBaseMetal> makeBaseMetals(JSONObject baseMetalsJson, PqrInfo pqr) throws JSONException, IllegalAccessException {
        ArrayList<PqrBaseMetal> baseMetals = new ArrayList<>();

        PqrBaseMetal baseMetal1 = new PqrBaseMetal();
        PqrBaseMetal baseMetal2 = new PqrBaseMetal();

        for (String key : keyData.pqrKey.getOrDefault("pNo", new HashSet<>())) {
            if (!baseMetalsJson.isNull(key)) {
                baseMetal1.setPNo(baseMetalsJson.getString(key).trim());
                baseMetal2.setPNo(baseMetalsJson.getString("to_"+key).trim());
                baseMetalsJson.remove(key);
                baseMetalsJson.remove("to_" + key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("grNo", new HashSet<>())) {
            if (!baseMetalsJson.isNull(key)) {
                baseMetal1.setGrNo(baseMetalsJson.getString(key).trim());
                baseMetal2.setGrNo(baseMetalsJson.getString("to_"+key).trim());
                baseMetalsJson.remove(key);
                baseMetalsJson.remove("to_" + key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("materialSpec", new HashSet<>())) {
            if (!baseMetalsJson.isNull(key)) {
                baseMetal1.setMaterialSpec(baseMetalsJson.getString(key).trim());
                baseMetal2.setMaterialSpec(baseMetalsJson.getString("to_"+key).trim());
                baseMetalsJson.remove(key);
                baseMetalsJson.remove("to_" + key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("typeAndGrade", new HashSet<>())) {
            if (!baseMetalsJson.isNull(key)) {
                baseMetal1.setTypeAndGrade(baseMetalsJson.getString(key).trim());
                baseMetal2.setTypeAndGrade(baseMetalsJson.getString("to_"+key).trim());
                baseMetalsJson.remove(key);
                baseMetalsJson.remove("to_" + key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("thickness", new HashSet<>())) {
            if (!baseMetalsJson.isNull(key)) {
                baseMetal1.setThickness(thicknessChange(baseMetalsJson.getString(key).trim()));
                baseMetal2.setThickness(thicknessChange(baseMetalsJson.getString("to_"+key).trim()));
                baseMetalsJson.remove(key);
                baseMetalsJson.remove("to_" + key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("diameter", new HashSet<>())) {
            if (!baseMetalsJson.isNull(key)) {
                baseMetal1.setDiameter(thicknessChange(baseMetalsJson.getString(key).trim()));
                baseMetal2.setDiameter(thicknessChange(baseMetalsJson.getString(key).trim()));
                baseMetalsJson.remove(key);
            }
        }

        if(!baseMetalsJson.toString().trim().equals("{}")){
            baseMetal1.setOther(baseMetalsJson.toString());
            baseMetal2.setOther(baseMetalsJson.toString());
        }

        baseMetals.add(baseMetal1);
        baseMetals.add(baseMetal2);

        return baseMetals;
    }

    // filler metal 전처리
    private ArrayList<PqrFillerMetal> makeFillerMetals(JSONObject fillerMetalsJson, ArrayList<String> weldingProcesses, PqrInfo pqr) throws JSONException, IllegalAccessException {
        ArrayList<PqrFillerMetal> fillerMetals = new ArrayList<>();
        HashSet<String> checkedKey = new HashSet<>();

        for (String process : weldingProcesses) {

            PqrFillerMetal fillerMetal = new PqrFillerMetal();
            fillerMetal.setProcess(process);

            for (String key : keyData.pqrKey.getOrDefault("sfaNo", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    checkedKey.add(key);
                    fillerMetal.setSfaNo(getByProcess(fillerMetalsJson,process,key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("awsClass", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    checkedKey.add(key);
                    fillerMetal.setAwsClass(getByProcess(fillerMetalsJson,process,key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("fNo", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    checkedKey.add(key);
                    fillerMetal.setFNo(getByProcess(fillerMetalsJson,process,key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("aNo", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    checkedKey.add(key);
                    fillerMetal.setANo(getByProcess(fillerMetalsJson,process,key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("fillerProductForm", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    checkedKey.add(key);
                    fillerMetal.setFillerProductForm(getByProcess(fillerMetalsJson,process,key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("sizeOfElectrode", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    checkedKey.add(key);

                    String[] str = getByProcess(fillerMetalsJson, process, key).toLowerCase().replaceAll("ø", "").split(",");
                    for (int i = 0; i < str.length; i++) {
                        if (i == 0) {
                            fillerMetal.setSizeOfElectrode1(thicknessChange(str[i]));
                        }else if (i == 1) {
                            fillerMetal.setSizeOfElectrode2(thicknessChange(str[i]));
                        }else if (i == 2) {
                            fillerMetal.setSizeOfElectrode3(thicknessChange(str[i]));
                        }
                    }
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("depositWeldMetalThickness", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    checkedKey.add(key);
                    fillerMetal.setDepositWeldMetalThickness(thicknessChange(getByProcess(fillerMetalsJson,process,key)));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("wireFluxClass", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    checkedKey.add(key);
                    fillerMetal.setWireFluxClass(getByProcess(fillerMetalsJson,process,key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("supplemental", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    checkedKey.add(key);
                    fillerMetal.setSupplemental(getByProcess(fillerMetalsJson,process,key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("alloyElements", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    checkedKey.add(key);
                    fillerMetal.setAlloyElements(getByProcess(fillerMetalsJson,process,key));
                }
            }

            fillerMetals.add(fillerMetal);
        }

        for (String removeKey : checkedKey) {
            fillerMetalsJson.remove(removeKey);
        }
        for (PqrFillerMetal fillerMetal : fillerMetals) {
            fillerMetal.setOther(fillerMetalsJson.toString());
        }

        return fillerMetals;
    }

    // position 전처리
    private ArrayList<PqrPosition> makePositions(JSONObject positionsJson, ArrayList<String> weldingProcesses, PqrInfo pqr) throws JSONException, IllegalAccessException {
        ArrayList<PqrPosition> positions = new ArrayList<>();
        HashSet<String> checkedKey = new HashSet<>();

        for (String process : weldingProcesses) {

            PqrPosition position = new PqrPosition();
            position.setProcess(process);

            for (String key : keyData.pqrKey.getOrDefault("positionOfGroove", new HashSet<>())) {
                if (!positionsJson.isNull(key)) {
                    checkedKey.add(key);
                    position.setPositionOfGroove(getByProcess(positionsJson,process,key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("progression", new HashSet<>())) {
                if (!positionsJson.isNull(key)) {
                    checkedKey.add(key);
                    position.setProgression(getByProcess(positionsJson, process, key));
                }
            }
            positions.add(position);
        }

        for (String removeKey : checkedKey) {
            positionsJson.remove(removeKey);
        }
        for (PqrPosition position : positions) {
            position.setOther(positionsJson.toString());
        }

        return positions;
    }

    // preheat 전처리
    private PqrPreheat makePreheat(JSONObject preheatJson, PqrInfo pqr) throws JSONException, IllegalAccessException {
        PqrPreheat preheat = new PqrPreheat();

        for (String key : keyData.pqrKey.getOrDefault("preheatTemp", new HashSet<>())) {
            if (!preheatJson.isNull(key)) {
                String str = preheatJson.getString(key).trim().replaceAll("°C","");
                if (isEmpty(str)) {
                    preheatJson.remove(key);
                    break;
                }
                double preheatTempMin;
                double preheatTempMax;
                if (str.contains("~")) {
                    preheatTempMin = Double.parseDouble(str.split("~")[0]);
                    preheatTempMax = Double.parseDouble(str.split("~")[1]);
                }else{
                    preheatTempMin = Double.parseDouble(str);
                    preheatTempMax = Double.parseDouble(str);
                }

                preheat.setPreheatTempMin(preheatTempMin);
                preheat.setPreheatTempMax(preheatTempMax);

                preheatJson.remove(key);
            }

        }

        for (String key : keyData.pqrKey.getOrDefault("interPassTemp", new HashSet<>())) {
            if (!preheatJson.isNull(key)) {
                String str = preheatJson.getString(key).trim().replaceAll("°C","");
                str = str.toLowerCase().replaceAll("max.","").trim();
                if (isEmpty(str)) {
                    preheatJson.remove(key);
                    break;
                }
                double interpassTempMin;
                double interpassTempMax;
                if (str.contains("~")) {
                    interpassTempMin = Double.parseDouble(str.split("~")[0]);
                    interpassTempMax = Double.parseDouble(str.split("~")[1]);
                }else{
                    interpassTempMin = Double.parseDouble(str);
                    interpassTempMax = Double.parseDouble(str);
                }
                preheat.setInterpassTempMin(interpassTempMin);
                preheat.setInterpassTempMax(interpassTempMax);

                preheatJson.remove(key);
            }
        }

        preheat.setOther(preheatJson.toString());

        return preheat;
    }

    // postWeldHeatTreatment 전처리
    private ArrayList<PqrPostWeldHeatTreatment> makePostWeldHeatTreatments(JSONObject postWeldHeatTreatmentJson, ArrayList<String> weldingProcesses, PqrInfo pqr) throws JSONException, IllegalAccessException {
        ArrayList<PqrPostWeldHeatTreatment> postWeldHeatTreatments = new ArrayList<>();

        PqrPostWeldHeatTreatment postWeldHeatTreatment = new PqrPostWeldHeatTreatment();

        for (String key : keyData.pqrKey.getOrDefault("temperature", new HashSet<>())) {
            if (!postWeldHeatTreatmentJson.isNull(key)) {
                String str = postWeldHeatTreatmentJson.getString(key).trim().replaceAll("°C","");
                if (isEmpty(str)) {
                    postWeldHeatTreatmentJson.remove(key);
                    break;
                }
                double temperatureMin;
                double temperatureMax;
                if (str.contains("~")) {
                    temperatureMin = Double.parseDouble(str.split("~")[0]);
                    temperatureMax = Double.parseDouble(str.split("~")[1]);
                }else{
                    temperatureMin = Double.parseDouble(str);
                    temperatureMax = Double.parseDouble(str);
                }

                postWeldHeatTreatment.setTemperatureMin(temperatureMin);
                postWeldHeatTreatment.setTemperatureMax(temperatureMax);

                postWeldHeatTreatmentJson.remove(key);
            }

        }

        for (String key : keyData.pqrKey.getOrDefault("holdingTime", new HashSet<>())) {

            if (!postWeldHeatTreatmentJson.isNull(key)) {

                postWeldHeatTreatment.setHoldingTime(postWeldHeatTreatmentJson.getString(key).trim());
                postWeldHeatTreatmentJson.remove(key);
            }
        }

        postWeldHeatTreatment.setCycle(1);
        postWeldHeatTreatment.setOther(postWeldHeatTreatmentJson.toString());
        postWeldHeatTreatments.add(postWeldHeatTreatment);

        return postWeldHeatTreatments;
    }

    // gas 전처리
    private ArrayList<PqrGas> makeGases(JSONObject gasJson, ArrayList<String> weldingProcesses, PqrInfo pqr) throws JSONException, IllegalAccessException {
        ArrayList<PqrGas> gases = new ArrayList<>();
        HashSet<String> checkedKey = new HashSet<>();

        for (String process : weldingProcesses) {

            PqrGas gas = new PqrGas();
            gas.setProcess(process);

            for (String outerKey : keyData.pqrKey.getOrDefault("shieldingGas", new HashSet<>())) {
                if (!gasJson.isNull(outerKey)) {
                    checkedKey.add(outerKey);
                    for (String key : keyData.pqrKey.getOrDefault("gas", new HashSet<>())) {
                        if (!gasJson.getJSONObject(outerKey).isNull(key)) {
                            if(!gasJson.getJSONObject(outerKey).isNull(key))
                                gas.setShieldingGas(getByProcess(gasJson.getJSONObject(outerKey), process, key));
                        }
                    }
                    for (String key : keyData.pqrKey.getOrDefault("percentComposition", new HashSet<>())) {
                        if (!gasJson.getJSONObject(outerKey).isNull(key)) {
                            gas.setShieldingGasComposition(getByProcess(gasJson.getJSONObject(outerKey),process,key));
                        }
                    }
                    for (String key : keyData.pqrKey.getOrDefault("flow", new HashSet<>())) {
                        if (!gasJson.getJSONObject(outerKey).isNull(key)) {
                            String str = getByProcess(gasJson.getJSONObject(outerKey),process,key);
                            if (!isEmpty(str)) {
                                str = str.toLowerCase().replaceAll("l/min", "");
                                if (str.contains("~")) {
                                    String[] arr = str.split("~");
                                    gas.setShieldingGasFlowMin(Double.valueOf(arr[0]));
                                    gas.setShieldingGasFlowMax(Double.valueOf(arr[1]));
                                } else {
                                    gas.setShieldingGasFlowMin(Double.valueOf(str));
                                    gas.setShieldingGasFlowMax(Double.valueOf(str));
                                }
                            }
                        }
                    }
                }
            }

            for (String outerKey : keyData.pqrKey.getOrDefault("gasBacking", new HashSet<>())) {
                if (!gasJson.isNull(outerKey)) {
                    checkedKey.add(outerKey);
                    for (String key : keyData.pqrKey.getOrDefault("gas", new HashSet<>())) {
                        if (!gasJson.getJSONObject(outerKey).isNull(key)) {
                            if(!gasJson.getJSONObject(outerKey).isNull(key))
                                gas.setBackingGas(getByProcess(gasJson.getJSONObject(outerKey), process, key));
                        }
                    }
                    for (String key : keyData.pqrKey.getOrDefault("percentComposition", new HashSet<>())) {
                        if (!gasJson.getJSONObject(outerKey).isNull(key)) {
                            gas.setBackingGasComposition(getByProcess(gasJson.getJSONObject(outerKey),process,key));
                        }
                    }
                    for (String key : keyData.pqrKey.getOrDefault("flow", new HashSet<>())) {
                        if (!gasJson.getJSONObject(outerKey).isNull(key)) {
                            String str = getByProcess(gasJson.getJSONObject(outerKey),process,key);
                            if (!isEmpty(str)) {
                                str = str.toLowerCase().replaceAll("l/min", "");
                                if (str.contains("~")) {
                                    String[] arr = str.split("~");
                                    gas.setBackingGasFlowMin(Double.valueOf(arr[0]));
                                    gas.setBackingGasFlowMax(Double.valueOf(arr[1]));
                                } else {
                                    gas.setBackingGasFlowMin(Double.valueOf(str));
                                    gas.setBackingGasFlowMax(Double.valueOf(str));
                                }
                            }
                        }
                    }
                }
            }

            for (String outerKey : keyData.pqrKey.getOrDefault("trailingGas", new HashSet<>())) {
                if (!gasJson.isNull(outerKey)) {
                    checkedKey.add(outerKey);
                    for (String key : keyData.pqrKey.getOrDefault("gas", new HashSet<>())) {
                        if (!gasJson.getJSONObject(outerKey).isNull(key)) {
                            gas.setTrailingGas(getByProcess(gasJson.getJSONObject(outerKey),process,key));
                        }
                    }
                    for (String key : keyData.pqrKey.getOrDefault("percentComposition", new HashSet<>())) {
                        if (!gasJson.getJSONObject(outerKey).isNull(key)) {
                            gas.setTrailingGasComposition(getByProcess(gasJson.getJSONObject(outerKey),process,key));
                        }
                    }for (String key : keyData.pqrKey.getOrDefault("flow", new HashSet<>())) {
                        if (!gasJson.getJSONObject(outerKey).isNull(key)) {
                            String str = getByProcess(gasJson.getJSONObject(outerKey),process,key);
                            if (!isEmpty(str)) {
                                str = str.toLowerCase().replaceAll("l/min","");
                                if (str.contains("~")) {
                                    String[] arr = str.split("~");
                                    gas.setTrailingGasFlowMin(Double.valueOf(arr[0]));
                                    gas.setTrailingGasFlowMax(Double.valueOf(arr[1]));
                                } else {
                                    gas.setTrailingGasFlowMin(Double.valueOf(str));
                                    gas.setTrailingGasFlowMax(Double.valueOf(str));
                                }
                            }
                        }
                    }
                }
            }

            gases.add(gas);
        }

        for (String removeKey : checkedKey) {
            gasJson.remove(removeKey);
        }
        for (PqrGas gas : gases) {
            gas.setOther(gasJson.toString());
        }

        return gases;
    }

    // electricalCharacteristic 전처리
    private ArrayList<PqrElectricalCharacteristic> makeElectricalCharacteristics(JSONObject electricalCharacteristicJson, ArrayList<String> weldingProcesses, PqrInfo pqr) throws JSONException, IllegalAccessException {
        ArrayList<PqrElectricalCharacteristic> electricalCharacteristics = new ArrayList<>();
        HashSet<String> checkedKey = new HashSet<>();

        for (String process : weldingProcesses) {

            PqrElectricalCharacteristic electricalCharacteristic = new PqrElectricalCharacteristic();
            electricalCharacteristic.setProcess(process);

            for (String key : keyData.pqrKey.getOrDefault("current", new HashSet<>())) {
                if (!electricalCharacteristicJson.isNull(key)) {
                    checkedKey.add(key);
                    electricalCharacteristic.setCurrent(getByProcess(electricalCharacteristicJson, process, key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("polarity", new HashSet<>())) {
                if (!electricalCharacteristicJson.isNull(key)) {
                    checkedKey.add(key);
                    electricalCharacteristic.setPolarity(getByProcess(electricalCharacteristicJson, process, key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("transferMode", new HashSet<>())) {
                if (!electricalCharacteristicJson.isNull(key)) {
                    checkedKey.add(key);
                    electricalCharacteristic.setTransferMode(getByProcess(electricalCharacteristicJson, process, key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("tungstenElectrodeType", new HashSet<>())) {
                if (!electricalCharacteristicJson.isNull(key)) {
                    checkedKey.add(key);
                    electricalCharacteristic.setTungstenElectrodeType(getByProcess(electricalCharacteristicJson, process, key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("tungstenElectrodeSize", new HashSet<>())) {
                if (!electricalCharacteristicJson.isNull(key)) {
                    checkedKey.add(key);

                    if (getByProcess(electricalCharacteristicJson, process, key) != null) {
                        String[] str = getByProcess(electricalCharacteristicJson, process, key).toLowerCase().replaceAll("ø", "").split(",");
                        for (int i = 0; i < str.length; i++) {
                            if (i == 0) {
                                electricalCharacteristic.setTungstenElectrodeSize1(thicknessChange(str[i]));
                            }else if (i == 1) {
                                electricalCharacteristic.setTungstenElectrodeSize2(thicknessChange(str[i]));
                            }else if (i == 2) {
                                electricalCharacteristic.setTungstenElectrodeSize3(thicknessChange(str[i]));
                            }
                        }
                    }

                }
            }

            electricalCharacteristics.add(electricalCharacteristic);
        }

        for (String removeKey : checkedKey) {
            electricalCharacteristicJson.remove(removeKey);
        }

        for (PqrElectricalCharacteristic electricalCharacteristic : electricalCharacteristics) {
            electricalCharacteristic.setOther(electricalCharacteristicJson.toString());
        }

        return electricalCharacteristics;
    }

    // technique 전처리
    private ArrayList<PqrTechnique> makeTechniques(JSONObject techniqueJson, ArrayList<String> weldingProcesses, PqrInfo pqr) throws JSONException, IllegalAccessException {
        ArrayList<PqrTechnique> techniques = new ArrayList<>();
        HashSet<String> checkedKey = new HashSet<>();

        for (String process : weldingProcesses) {

            PqrTechnique technique = new PqrTechnique();
            technique.setProcess(process);

            for (String key : keyData.pqrKey.getOrDefault("stringerOrWeaveBead", new HashSet<>())) {
                if (!techniqueJson.isNull(key)) {
                    checkedKey.add(key);
                    technique.setStringerOrWeaveBead(getByProcess(techniqueJson, process, key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("oscillation", new HashSet<>())) {
                if (!techniqueJson.isNull(key)) {
                    checkedKey.add(key);
                    technique.setOscillation(getByProcess(techniqueJson, process, key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("multiPassOrSinglePass", new HashSet<>())) {
                if (!techniqueJson.isNull(key)) {
                    checkedKey.add(key);
                    technique.setMultiPassOrSinglePass(getByProcess(techniqueJson, process, key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("singleOrMultipleElectrodes", new HashSet<>())) {
                if (!techniqueJson.isNull(key)) {
                    checkedKey.add(key);
                    technique.setSingleOrMultipleElectrode(getByProcess(techniqueJson, process, key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("closedToOutChamber", new HashSet<>())) {
                if (!techniqueJson.isNull(key)) {
                    checkedKey.add(key);
                    technique.setClosedToOutChamber(getByProcess(techniqueJson, process, key));
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("useOfThermalProcesses", new HashSet<>())) {
                if (!techniqueJson.isNull(key)) {
                    checkedKey.add(key);
                    technique.setUseOfThermalProcesses(getByProcess(techniqueJson, process, key));
                }
            }

            techniques.add(technique);
        }

        for (String removeKey : checkedKey) {
            techniqueJson.remove(removeKey);
        }

        for (PqrTechnique technique : techniques) {
            technique.setOther(techniqueJson.toString());
        }

        return techniques;
    }

    private Double cmminTommmin(Double x){
        return x*10;
    }

    private Double inminTommmin(Double x){
        return x*25.4;
    }

    private Double kjcmTokjmm(Double x){
        return x*10;
    }

    private Double thicknessChange(String str){

        str = str.trim().toLowerCase().replaceAll("ø","");

        if (isEmpty(str)) {
            return null;
        }

        if (str.contains("mm")) {
            return Double.parseDouble(str.replaceAll("mm",""));
        } else if (str.contains("cm")) {
            return Double.parseDouble(str.replaceAll("cm","")) * 10;
        } else if (str.contains("in")) {
            return Double.parseDouble(str.replaceAll("in","")) * 25.4;
        } else {
            return Double.valueOf(str);
        }

    }

    // 비어진 문자열이면 true return
    private boolean isEmpty(String str) throws NullPointerException{

        try {
            str = str.toLowerCase();
            if (str.equals("") || str.equals("-") || str.equals("n/a") || str.equals("none")) {
                return true;
            }
        } catch (NullPointerException e) {
        }

        return false;
    }

    // null check
    private boolean isStringEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // process에 분리된 값, 분리되지 않은 값에서 해당하는 값을 찾아오는 메서드
    private String getByProcess(JSONObject jsonObject, String process, String key) throws JSONException {

        try {
            Iterator<String> iterator = jsonObject.getJSONObject(key).keys();
            while (iterator.hasNext()) {
                String innerKey = iterator.next();
                if (process.contains(innerKey)) {
                    return jsonObject.getJSONObject(key).getString(innerKey).trim();
                }
            }
        } catch (JSONException e) {
            return jsonObject.getString(key);
        }

        return null;
    }

}