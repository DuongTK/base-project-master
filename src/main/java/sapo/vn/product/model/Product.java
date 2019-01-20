package sapo.vn.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import sapo.vn.product.utilities.RandomString;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "product")
public class Product extends BaseObject {
    @Id
    @JsonIgnore
    private String id= RandomString.generateString(9);

    @Column(nullable = false)
    private String name;

    private String description;
    private String image;
    private String type;
    private String brand;

    @ElementCollection
    private List<String> tags;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Version> versions ;


}
