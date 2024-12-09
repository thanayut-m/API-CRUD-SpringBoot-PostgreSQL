package com.example.api_crud_springboot_postgresql.dto;


import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ShopDto {
    private Integer shopId;
    private String shopName;
}
