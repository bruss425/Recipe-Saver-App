package Recipe.Saver.demo.resource;

import static Recipe.Saver.demo.Constant.Constant.PHOTO_DIRECTORY;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.io.IOException;

import java.net.URI;import java.nio.file.Files;
import java.nio.file.Path; // Correct import for Path
import java.nio.file.Paths;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus; // Correct import for HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import Recipe.Saver.demo.domain.Recipe;
import Recipe.Saver.demo.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
@Slf4j
public class RecipeResource {

    private final RecipeService recipeService;

    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        Recipe createdRecipe = recipeService.createRecipe(recipe);
        return ResponseEntity.created(URI.create("/recipes/" + createdRecipe.getId())).body(createdRecipe);
    }

    @GetMapping
    public ResponseEntity<Page<Recipe>> getRecipes(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(recipeService.getAllRecipes(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok().body(recipeService.getRecipe(id));
    }

    @PutMapping("/photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("id") String id, @RequestParam("file") MultipartFile file) {
        String photoUrl = recipeService.uploadPhoto(id, file);
        return ResponseEntity.ok().body(photoUrl);
    }

    @GetMapping(path = "/image/{filename}", produces = { IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE })
    public ResponseEntity<byte[]> getPhoto(@PathVariable("filename") String filename) {
        try {
            // Construct the full file path
            Path filePath = Paths.get(PHOTO_DIRECTORY, filename).toAbsolutePath().normalize();
            
            // Log the full path being used to read the file
            log.info("Attempting to read file at path: {}", filePath);
            
            // Check if the file exists
            if (Files.exists(filePath)) {
                // Log successful file access before returning the file contents
                log.info("File found, reading contents from path: {}", filePath);
                return ResponseEntity.ok().body(Files.readAllBytes(filePath));
            } else {
                // Log if the file does not exist
                log.error("File not found at path: {}", filePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("File not found: " + filename).getBytes()); // Fixed typo
            }
        } catch (IOException e) {
            // Log any IO exceptions that occur
            log.error("Error reading file: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(("Error reading file: " + filename).getBytes());
        }
    }
}
