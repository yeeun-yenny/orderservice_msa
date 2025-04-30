package com.playdata.productservice.product.service;

import com.playdata.productservice.common.configs.AwsS3Config;
import com.playdata.productservice.product.dto.ProductResDto;
import com.playdata.productservice.product.dto.ProductSaveReqDto;
import com.playdata.productservice.product.dto.ProductSearchDto;
import com.playdata.productservice.product.emtity.Product;
import com.playdata.productservice.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final AwsS3Config s3Config;

    public Product productCreate(ProductSaveReqDto dto) throws IOException {

        MultipartFile productImage = dto.getProductImage();

        // 상품을 등록하는 과정에서, 이미지 이름의 충돌이 발생할 수 있기 때문에
        // 랜덤한 문자열을 섞어서 파일 중복을 막아주자.
        String uniqueFileName
                = UUID.randomUUID() + "_" + productImage.getOriginalFilename();

        /*
        // 특정 로컬 경로에 이미지를 전송하고, 그 경로를 Entity에 세팅하자.
        File file
                = new File("/Users/yeni/Documents/playData_8기/upload/" + uniqueFileName);
        try {
            productImage.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패!");
        }
        */

        // 더 이상 로컬 경로에 이미지를 저장하지 않고, s3 버킷에 저장
        String imageUrl
                = s3Config.uploadToS3Bucket(productImage.getBytes(), uniqueFileName);

        Product product = dto.toEntity();
        product.setImagePath(imageUrl); // 파일명이 아닌 S3 오브젝트의 url이 저장될 것이다.

        return productRepository.save(product);
    }

    public List<ProductResDto> productList(ProductSearchDto dto, Pageable pageable) {
        Page<Product> products;
        if (dto.getCategory() == null) {
            products = productRepository.findAll(pageable);
        } else if (dto.getCategory().equals("name")) {
            products = productRepository.findByNameValue(dto.getSearchName(), pageable);
        } else {
            products = productRepository.findByCategoryValue(dto.getSearchName(), pageable);
        }

        List<Product> productList = products.getContent();

        return productList.stream()
                .map(Product::fromEntity)
                .collect(Collectors.toList());
    }

    public void productDelete(Long id) throws Exception {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Product with id " + id + " not found")
        );

        String imageUrl = product.getImagePath();
        s3Config.deleteFromS3Bucket(imageUrl);

        productRepository.deleteById(id);
    }

    public ProductResDto getProductInfo(Long prodId) {
        Product product = productRepository.findById(prodId).orElseThrow(
                () -> new EntityNotFoundException("Product with id " + prodId + " not found")
        );

        return product.fromEntity();
    }

    public void updateStockQuantity(Long prodId, int stockQuantity) {
        Product foundProduct = productRepository.findById(prodId).orElseThrow(
                () -> new EntityNotFoundException("Product with id " + prodId + " not found")
        );
        foundProduct.setStockQuantity(stockQuantity);
        productRepository.save(foundProduct);
    }
}
