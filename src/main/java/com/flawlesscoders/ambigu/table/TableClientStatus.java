package com.flawlesscoders.ambigu.table;

import io.swagger.v3.oas.annotations.media.Schema;


public enum TableClientStatus {   //enum para table
    @Schema(description = "Table is available for use as a client")
    UNOCCUPIED,

    @Schema(description = "Table is currently in use as a client")
    OCCUPIED

}
