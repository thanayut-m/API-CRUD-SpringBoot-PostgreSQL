package com.example.api_crud_springboot_postgresql;

import com.example.api_crud_springboot_postgresql.dto.ShopDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class shopController {

    @PersistenceContext
    public EntityManager entityManager;

    @GetMapping("/select")
    public ResponseEntity<List<ShopDto>> shopList() {
        try {
            List<Tuple> resultList = entityManager.createNativeQuery("SELECT shop_id, shop_name FROM shop", Tuple.class).getResultList();

            if (resultList.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            } else {
                List<ShopDto> shopDtoList = new ArrayList<>();

                for (Tuple tuple : resultList) {
                    ShopDto shopDto = new ShopDto();
                    shopDto.setShopId(tuple.get("shop_id", Integer.class));
                    shopDto.setShopName(tuple.get("shop_name", String.class));
                    shopDtoList.add(shopDto);
                }
                return ResponseEntity.ok(shopDtoList);
            }
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
            return null;
        }
    }


    @GetMapping("/getById")
    public ResponseEntity<?> getById(
      @RequestParam("id") Integer id
    ) {
        try {
            List<Object[]> resultList = entityManager.createNativeQuery("SELECT shop_id, shop_name FROM shop WHERE shop_id = :id")
                    .setParameter("id", id)
                    .getResultList();

            if (resultList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ไม่พบร้านค้าที่มี shop_id: " + id);
            }

            Object[] result = resultList.get(0);
            ShopDto shopDto = new ShopDto();
            shopDto.setShopId((Integer) result[0]);
            shopDto.setShopName((String) result[1]);

            return ResponseEntity.ok(shopDto);

        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error :" + e.getMessage());
        }
    }

    @Transactional
    @PostMapping("/insert")
    public ResponseEntity<?> insert(
            @RequestBody ShopDto shopDto
    ){
        try {
            Object insertId = entityManager.createNativeQuery("INSERT INTO shop (shop_name) VALUES (:shopName) RETURNING shop_id")
                    .setParameter("shopName", shopDto.getShopName())
                    .getSingleResult();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Create success");

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("shopId", insertId);
            data.put("shopName", shopDto.getShopName());

            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("ERROR :" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Error occurred",
                    "error", e.getMessage()
            ));
        }
    }

    @Transactional
    @PostMapping("/update")
    public ResponseEntity<?> update(
            @RequestBody ShopDto shopDto
    ) {
        try {
            int rowEffect = entityManager.createNativeQuery("UPDATE shop SET shop_name = :shopName WHERE shop_id = :shopId")
                    .setParameter("shopId",shopDto.getShopId())
                    .setParameter("shopName",shopDto.getShopName())
                    .executeUpdate();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message","Update success");

            Map<String,Object> data = new LinkedHashMap<>();
            data.put("shopId",shopDto.getShopId());
            data.put("shopName",shopDto.getShopName());

            response.put("data",data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message" , "Error occurred" ,
                    "error" , e.getMessage()
            ));
        }
    }

    @Transactional
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(
            @RequestBody ShopDto shopDto
    ){
        try {
            int rowEffect = entityManager.createNativeQuery("DELETE FROM shop WHERE shop_id = :shopId")
                    .setParameter("shopId",shopDto.getShopId())
                    .executeUpdate();

            if (rowEffect > 0) {
                return ResponseEntity.ok("Delete success");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No record found for the given shopId");
            }
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred: " + e.getMessage());
        }
    }
}
