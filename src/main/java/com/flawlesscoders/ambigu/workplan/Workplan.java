package com.flawlesscoders.ambigu.workplan;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Document(collection = "workplans")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Workplan {
    @Id
    private String id;
    private Date date;
    private String name;

    private List<Assigment> assigment;

    private boolean isPresent;
}
