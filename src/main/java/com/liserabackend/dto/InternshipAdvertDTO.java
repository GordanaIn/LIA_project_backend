package com.liserabackend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.LocalDate;

@Value
public class InternshipAdvertDTO {
    String id;
    String contactEmployer;
    String title;
    String description;
    LocalDate datePosted;
    String duration; //how long the internship last ex- 3 months
    int requiredNumber;
    String companyName;
    String orgNumber;
    @JsonCreator
    public InternshipAdvertDTO(
            @JsonProperty("id") String id,
            @JsonProperty("contactEmployer") String contactEmployer,
            @JsonProperty("title")  String title,
            @JsonProperty("description") String description,
            @JsonProperty("datePosted") LocalDate datePosted,
            @JsonProperty("duration")  String duration,
            @JsonProperty("requiredNumber")  int requiredNumber,
            @JsonProperty("companyName")  String companyName,
            @JsonProperty("orgNumber")  String orgNumber) {
        this.id=id;
        this.contactEmployer=contactEmployer;
        this.title=title;
        this.description=description;
        this.datePosted=datePosted;
        this.duration=duration;
        this.requiredNumber=requiredNumber;
        this.companyName=companyName;
        this.orgNumber=orgNumber;
    }

}
