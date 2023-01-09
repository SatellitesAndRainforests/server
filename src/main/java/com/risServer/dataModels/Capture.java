package com.risServer.dataModels;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Data
@Getter
@Setter
public class Capture implements Serializable {

    private final Integer id;
    private final String time;          //set not updatable ??
    private final String species;
    private final String idStatus;
    private final String notes;
    private final float  temperature;   //set not updatable ??
    private final float  humidity;      //set not updatable ??
    private final String moonPhase;     //set not updatable ??
    private final Double longitude;     //set not updatable ??
    private final Double latitude;      //set not updatable ??
    private List<Image> images;



}