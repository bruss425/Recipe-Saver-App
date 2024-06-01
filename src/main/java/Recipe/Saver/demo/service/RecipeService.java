package Recipe.Saver.demo.service;

import Recipe.Saver.demo.domain.Recipe;
import Recipe.Saver.demo.repo.RecipeRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static Recipe.Saver.demo.Constant.Constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Slf4j
@Transactional (rollbackOn = Exception.class)
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepo recipeRepo;

    public Page<Recipe> getAllRecipe(int page, int size){
        return recipeRepo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    public Recipe getRecipe(String id){
        return recipeRepo.findById(id).orElseThrow(() -> new RuntimeException("Recipe not found"));
    }

    public Recipe createRecipe(Recipe recipe){
        return recipeRepo.save(recipe);
    }

    public void deleteRecipe(Recipe recipe){
        recipeRepo.delete(recipe); // this should work
    }

    public String uploadPhoto(String id, MultipartFile file){
        log.info("saving the picture for recipe ID: {}", id);
        Recipe recipe = getRecipe(id);
        String photoUrl = photoFunction.apply(id, file);
        recipe.setPhotoUrl(photoUrl);
        recipeRepo.save(recipe);
        return photoUrl;
    }

    // this is taking a string and returning a string
    // the purpose of this is to make sure the name contains the "." and then if we have one capture following extension
    // pretty much a fancy way to get the substring which will be .img, .png, or .jpg and .png is the default
    private final Function<String, String> fileExtension = filename -> Optional.of(filename)
            .filter(name -> name.contains(".")).map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");

    // this is taking a string and file and returning a string
    // this is building the string to save and  store the photo, pretty much making the url
    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String filename = id + fileExtension.apply(image.getOriginalFilename());
        try{
            Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            // if this location does not exist, then create a location for it
            if(!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
            // now save the file
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(id + fileExtension.apply(image.getOriginalFilename())), REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/recipes/image/" + id + fileExtension.apply(image.getOriginalFilename())).toUriString();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    };
}
