package Recipe.Saver.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import Recipe.Saver.demo.domain.Recipe;

@Repository
public interface RecipeRepo extends JpaRepository<Recipe, String> {
    Optional<Recipe> findById(String id);
    Optional<Recipe> findByName(String name);
    Optional<Recipe> findByIngredient(String ingredient);
    Optional<Recipe> findByCategory(String category);

}
