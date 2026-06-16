package com.erp_be.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    String message;

   ProductExCommon productExCommon;

    public ProductException(String message) {
        productExCommon = new ProductExCommon();
        this.productExCommon.setResult("ng");
        this.productExCommon.setStatus(400);
        this.productExCommon = new ProductExCommon(message);
    }


}