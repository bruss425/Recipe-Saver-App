package Recipe.Saver.demo.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Recipe {
    @Id
    @UuidGenerator
    @Column(name = "id", unique = true, updatable = false)

    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String category;
    private String rating;

    @ElementCollection // need to make sure this works as inteneded
    @Column(name = "ingredient")
    private List<String> ingredients;

    public void setPhotoUrl(String photoUrl) {
        this.imageUrl = photoUrl;
    }
}
