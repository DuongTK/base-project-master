package sapo.vn.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sapo.vn.product.utilities.RandomString;
import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "version")
public class Version extends AuditModel{
    @Id
    @JsonIgnore
    private String id = RandomString.generateString(9);

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id",nullable = false)
    private Product product;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    private List<String> properties;

    private String image;

    @Column(name = "sku_code",nullable = false)
    private String skuCode;

    @Column(name = "bar_code",nullable = false)
    private String barCode;

    private String price;

}
