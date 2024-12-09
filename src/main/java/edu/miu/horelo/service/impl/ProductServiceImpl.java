package edu.miu.horelo.service.impl;

import edu.miu.horelo.advice.ResourceNotFoundException;
import edu.miu.horelo.dto.FileDTO;
import edu.miu.horelo.dto.SavedFileDTO;
import edu.miu.horelo.dto.request.ProductRequest;
import edu.miu.horelo.dto.response.EstoreResponse1;
import edu.miu.horelo.dto.response.ProductResponse;
import edu.miu.horelo.model.Estore;
import edu.miu.horelo.model.Product;
import edu.miu.horelo.model.SubCategory;
import edu.miu.horelo.model.User;
import edu.miu.horelo.repository.EstoreRepository;
import edu.miu.horelo.repository.ProductRepository;
import edu.miu.horelo.repository.SubCategoryRepository;
import edu.miu.horelo.repository.UserRepository;
import edu.miu.horelo.service.FileManagerService;
import edu.miu.horelo.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EstoreRepository estoreRepository;
    @Autowired
    private UserRepository userRepository;
@Autowired
private FileManagerService fileManagerService;
    @Autowired
    private SubCategoryRepository subCategoryRepository;

    // Create
    @Override
    public ProductResponse createProduct(ProductRequest productRequest, Integer userId) {
        Product product = mapToProduct(productRequest, userId);
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }


    // Read
    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }
    @Override
    public Optional<ProductResponse> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToProductResponse);
    }
    @Override
    public List<ProductResponse> getProductsByEstore(Long estoreId) {
        return productRepository.findByEstore_EstoreId(estoreId).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    // Update
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest, Integer userId) {
        // Fetch the existing product by ID
        Optional<Product> existingProductOpt = productRepository.findById(id);
        System.out.println("here " + id);

        // If product is present, update and save it
        if (existingProductOpt.isPresent()) {

            // Map the new product data from the request to the existing product
            Product updatedProduct = mapToProduct(productRequest, userId);

            // Ensure the productId remains unchanged
            updatedProduct.setProductId(id);

            // Save the updated product
            Product savedProduct = productRepository.save(updatedProduct);

            // Convert the saved product to a response and return it
            return mapToProductResponse(savedProduct);
        }

        // Return null or handle the case where the product doesn't exist (e.g., throw exception)
        return null;
    }
    @Override
    @Transactional
    public ProductResponse updateProductLogo(Long id, MultipartFile file){
        Product pdt = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("product not found with id " + id));
        // Check if a file is present and handle file upload
        if (file != null && !file.isEmpty()) {
            try {
                // Create a FileDTO instance
                FileDTO fileDTO = new FileDTO();
                fileDTO.setFile(file); // Set the MultipartFile
                fileDTO.setFileName(file.getOriginalFilename()); // Set the original file name

                // Get the existing avatar filename for deletion
                String oldAvatar = pdt.getImage();

                // Call fileManagerService to upload the file and delete the old one
                SavedFileDTO savedFile = fileManagerService.uploadFile(fileDTO, "product", oldAvatar); // null for default folder

                // Set the new avatar URL based on the uploaded file
                pdt.setImage(savedFile.getGeneratedFileName()); // Assuming you store the file URL or name
            } catch (Exception e) {
                throw new RuntimeException("Error uploading file", e);
            }
        }
        // Save the updated store back to the repository
        var savedProduct = productRepository.save(pdt);

        return mapToProductResponse(savedProduct);
    }

    @Override
    public Page<ProductResponse> getEstoreProducts(Long estoreId, String itemType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Query based on itemType
        Page<Product> productItems;
        if (itemType != null && !itemType.isBlank()) {
            productItems = productRepository.findProductByEstore_EstoreId(estoreId, pageable);
        } else {
            productItems = productRepository.findProductByEstore_EstoreId(estoreId, pageable);
        }

        // Map FoodItem entities to FoodItemResponse DTOs
        return productItems.map(this::mapToProductResponse);
    }

    @Override
    public Page<ProductResponse> getProductsWithoutEstore(int page, int size) {
        // Define pagination
        Pageable pageable = PageRequest.of(page, size);

        // Query food items without an associated estore
        Page<Product> foodItems = productRepository.findAll(pageable);

        // Map the results to FoodItemResponse DTOs
        return foodItems.map(this::mapToProductResponse);
    }


    // Delete
    @Override
    public void deleteProduct(Long id, Integer userId) {
        productRepository.deleteById(id);
    }


    // Helper methods to map between entity and DTO
    private Product mapToProduct(ProductRequest productRequest, Integer userId) {
        SubCategory subCategory  = subCategoryRepository.findBySubCategoryId(productRequest.subCategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("Sub category not found"));
        Product product = new Product();
        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setSubCategory(subCategory);
        product.setPrice(productRequest.price());
        // Convert EstoreResponse1 to Estore entity
        // Fetch the Estore entity using its ID
        Estore estore = estoreRepository.findById(productRequest.estoreId())
                .orElseThrow(() -> new EntityNotFoundException("Estore not found"));
        product.setEstore(estore); // Assuming bidirectional mapping is set up
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        product.setUser(user); // Placeholder, modify as per logic
        return product;
    }
    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getSubCategory(),
                product.getImage(),
                product.getCalories(),
                product.getPrice(),
                new EstoreResponse1(product.getEstore().getEstoreId(), product.getEstore().getName())
        );
    }
}