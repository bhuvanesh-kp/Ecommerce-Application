package org.bhuvanesh.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
     private Long productId;
     private String productName;
     private String description;
     private String Image;
     private double price;
     private double quantity;
     private double specialPrice;
     private double discount;
}
