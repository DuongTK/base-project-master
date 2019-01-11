package sapo.vn.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "test")
public class Product {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String title;
//    private String name;
//    private String description;
//    private String image;
//    private String type;
//    private String brand;
//    private ArrayList<String> tags;
//    private float time_create;
//    private float time_update;



}
