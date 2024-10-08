package Recipe.Saver.demo.service;

import static Recipe.Saver.demo.Constant.Constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import Recipe.Saver.demo.domain.Recipe;
import Recipe.Saver.demo.repo.RecipeRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepo recipeRepo;

    public Page<Recipe> getAllRecipes(int page, int size) {
        return recipeRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    public Recipe getRecipe(String id) {
        return recipeRepo.findById(id).orElseThrow(() -> new RuntimeException("recipe not found"));
    }

    public Recipe createRecipe(Recipe recipe) {
        return recipeRepo.save(recipe);
    }

    public void deleterecipe(Recipe recipe) {
        // Assignment
    }

    public String uploadPhoto(String id, MultipartFile file) {
        log.info("Saving picture for user ID: {}", id);
        Recipe recipe = getRecipe(id);
        String photoUrl = photoFunction.apply(id, file);
        recipe.setPhotoUrl(photoUrl);
        recipeRepo.save(recipe);
        return photoUrl;
    }

    private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String filename = id + fileExtension.apply(image.getOriginalFilename());
        try {
           Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
log.info("Storing file at location: {}", fileStorageLocation);
            if(!Files.exists(fileStorageLocation)) { Files.createDirectories(fileStorageLocation); }
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/recipes/image/" + filename).toUriString();
        }catch (Exception exception) {
            throw new RuntimeException("Unable to save image");
        }
    };
}