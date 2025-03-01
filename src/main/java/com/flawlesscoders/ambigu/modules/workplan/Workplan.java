package com.flawlesscoders.ambigu.modules.workplan;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Indicates if there is any work plan in progress",  defaultValue = "false")
    private boolean isPresent;

    @Schema(description = "Indicates if the work plan is a favorite",  defaultValue = "false")
    private boolean isFavorite;

    @Schema(description = "Indicates if the work plan is in a valid search",  defaultValue = "true")
    private boolean isExisting;
}
