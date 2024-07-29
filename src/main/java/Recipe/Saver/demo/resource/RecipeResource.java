package Recipe.Saver.demo.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import Recipe.Saver.demo.domain.Recipe;
import Recipe.Saver.demo.service.RecipeService;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeResource { 
    private final RecipeService recipeService;

    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        //return ResponseEntity.ok().body(recipeService.createRecipe(recipe));
        return ResponseEntity.created(URI.create("/recipes/id")).body(recipeService.createRecipe(recipe));
    }

    @GetMapping
    public ResponseEntity<Page<Recipe>> getRecipes(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(recipeService.getAllRecipe(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok().body(recipeService.getRecipe(id));
    }

    @PutMapping("/photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("id") String id, @RequestParam("file")MultipartFile file) {
        return ResponseEntity.ok().body(recipeService.uploadPhoto(id, file));
    }

   // @GetMapping(path = "/image/{filename}", produces = { IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE })
   // public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
   //     return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));   }
}