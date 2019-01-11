//package sapo.vn.product.model;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
////@AllArgsConstructor
////@NoArgsConstructor
////@Data
////@Entity
////@Table(name = "version")
//public class Version {
//    @Id
//    @JsonIgnore
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private String id;
//
//    @Column(name = "product_id",nullable = false)
//    private String productId;
//    private String name;
//    private String color;
//    private String size;
//    private String material;
//    private String image;
//    @Column(name = "sku_code",nullable = false)
//    private String skuCode;
//    @Column(name = "bar_code",nullable = false)
//    private String bar_code;
//    private String price;
//
//    @Column(name = "time_create",nullable = false)
//    private String timeCreate;
//
//    @Column(name = "time_update",nullable = false)
//    private String timeUpdate;
//
//
//}
