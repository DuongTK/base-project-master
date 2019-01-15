package sapo.vn.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import sapo.vn.product.utilities.RandomString;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "product")
public class Product extends AuditModel{
    @Id
    @JsonIgnore
    private String id = RandomString.generateString(9);

    @Column(nullable = false)
    private String name;

    private String description;
    private String image;
    private String type;
    private String brand;

    @ElementCollection
    private List<String> tags;

    @JsonIgnore
    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Version> versions = new ArrayList<>();


}
