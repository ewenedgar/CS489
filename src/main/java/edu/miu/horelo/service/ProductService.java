package edu.miu.horelo.service;

import edu.miu.horelo.dto.request.ProductRequest;
import edu.miu.horelo.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    Optional<ProductResponse> getProductById(Long id);
    List<ProductResponse> getProductsByEstore(Long estoreId);
    ProductResponse updateProduct(Long id, ProductRequest productRequest, Integer userId);
    void deleteProduct(Long id, Integer userId);

    ProductResponse createProduct(ProductRequest productRequest, Integer userId);
    ProductResponse updateProductLogo(Long estoreId, MultipartFile file);

    Page<ProductResponse> getEstoreProducts(Long estoreId, String itemType, int page, int size);

    Page<ProductResponse> getProductsWithoutEstore(int page, int size);
}
