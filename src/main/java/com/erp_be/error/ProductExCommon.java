package com.erp_be.error;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductExCommon extends GenericResponse implements Serializable {
    List<String> errors;

    public ProductExCommon(String message) {
        this.result = "ng";
        this.status = 400;
        this.errors = Collections.singletonList(message);
    }
}