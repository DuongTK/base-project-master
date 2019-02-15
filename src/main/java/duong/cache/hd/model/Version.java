package duong.cache.hd.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "version")
public class Version extends BaseObject {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    private Map<String,String> properties;

    private String image;

    @Column(name = "sku_code",nullable = false)
    private String skuCode;

    @Column(name = "bar_code",nullable = false)
    private String barCode;

    private String price;

    public boolean compare(Version version){
        Collection<String> valuesIn = this.properties.values();
        Collection<String> valuesOut = version.properties.values();
        if (valuesIn.containsAll(valuesOut) && valuesOut.containsAll(valuesIn)){
            return true;
        }
        return false;
    }

}
