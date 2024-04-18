package com.bootsmytool.kenstore.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.bootsmytool.kenstore.models.Product;
import com.bootsmytool.kenstore.models.ProductDto;
import com.bootsmytool.kenstore.services.ProductsRepository;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/products")
public class ProductController {
	@Autowired
	private ProductsRepository repo;

	@GetMapping({"", "/"})
	public String showProductList (Model model) {
		List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("products", products);
		return "products/index";
	}
	
	@GetMapping("/create")
	public String showCreatePage (Model model) {
		ProductDto productDto = new ProductDto();
		model.addAttribute("productDto", productDto);
		return "products/CreateProduct";
	}
	
	@PostMapping("/create")
	public String createProduct (
	        @Valid @ModelAttribute ProductDto productDto,
	        BindingResult result
	        ) {
	    
	    if(productDto.getImageFile().isEmpty()) {
	        result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
	    }
	    if(result.hasErrors()) {
	        return "products/CreateProduct";
	    }
	    
	    // Lưu trữ sản phẩm
	    try {
	        // Lấy file ảnh từ ProductDto
	        MultipartFile image = productDto.getImageFile(); 
	        // Lấy thời gian hiện tại
	        Date createdAt = new Date(); 
	        // Tạo tên file lưu trữ
	        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename(); 
	        
	        // Đường dẫn lưu trữ
	        String uploadDir = "public/images/"; 
	        Path uploadPath = Paths.get(uploadDir); 
	        
	        // Kiểm tra nếu thư mục không tồn tại thì tạo mới
	        if (!Files.exists(uploadPath)) { 
	            Files.createDirectories(uploadPath); 
	        } 
	        
	        // Ghi file ảnh vào thư mục lưu trữ
	        try (InputStream inputStream = image.getInputStream()) { 
	            Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING); 
	        } 
	        
	        // Tạo mới đối tượng Product và lưu vào cơ sở dữ liệu
	        Product product = new Product();
	        product.setName(productDto.getName());
	        product.setBranch(productDto.getBranch());
	        product.setCategory(productDto.getCategory());
	        product.setPrice(productDto.getPrice());
	        product.setDescription(productDto.getDescription());
	        product.setCreatedAt(createdAt);
	        product.setImageFileName(storageFileName);

	        repo.save(product);
	    } catch (Exception ex) {
	        System.out.println("Exception: " + ex.getMessage()); 
	    }

	    return "redirect:/products";
	}
	
	@GetMapping("/edit")
	public String showEditPage(
			Model model,
			@RequestParam int id
			) {
		try {
			Product product  = repo.findById(id).get();
			model.addAttribute("product", product);
			
			ProductDto productDto = new ProductDto();
			productDto.setName(product.getName());
			productDto.setBranch(product.getBranch());
			productDto.setCategory(product.getCategory());
			productDto.setPrice(product.getPrice());
			productDto.setDescription(product.getDescription());
			
			model.addAttribute("productDto",productDto);
			
		} catch (Exception e) {
			System.out.println("Exception: "+ e.getMessage());
			return "redirect: /products";
		}
		return "products/EditProduct";
	}
	
	@PostMapping ("/edit")
	public String updateProduct (
			Model model,
			@RequestParam int id,
			@Valid @ModelAttribute ProductDto productDto,
			BindingResult result
			) {
		try {
			Product product  = repo.findById(id).get();
			model.addAttribute("product", product);
			
			if(result.hasErrors()) {
				return "products/EditProduct";
			}
			
			if (!productDto.getImageFile().isEmpty()) {
				// Delete old image
			    String uploadDir = "public/images/";
			    String oldFileName = product.getImageFileName();
			    if (oldFileName != null && !oldFileName.isEmpty()) {
			        try {
			            Path oldImagePath = Paths.get(uploadDir + oldFileName);
			            Files.delete(oldImagePath);
			        } catch (IOException ex) {
			            System.out.println("Failed to delete old image: " + ex.getMessage());
			            // Handle the exception accordingly, maybe log it or show an error message
			        }
			    }

			    // Save new image file
			    MultipartFile image = productDto.getImageFile();
			    Date createdAt = new Date();
			    String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

			    try {
			        Path uploadPath = Paths.get(uploadDir);
			        if (!Files.exists(uploadPath)) {
			            Files.createDirectories(uploadPath);
			        }

			        try (InputStream inputStream = image.getInputStream()) {
			            Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
			            
			            product.setImageFileName(storageFileName);
			        }
			    } catch (IOException ex) {
			        System.out.println("Failed to save new image: " + ex.getMessage());
			        // Handle the exception accordingly, maybe log it or show an error message
			    }
			}
			
			product.setName(productDto.getName());
			product.setBranch(productDto.getBranch());
			product.setCategory(productDto.getCategory());
			product.setPrice(productDto.getPrice());
			product.setDescription(productDto.getDescription());
			
			repo.save(product);

		} catch (Exception e) {
			System.out.println("Exception: "+ e.getMessage());
			return "redirect:/products";
		}
		
		return "redirect:/products";
	}
	
	@GetMapping ("/delete")
	public String deleteProduct(@RequestParam int id) {
		try {
			Product product = repo.findById(id).get();
			Path imagePath = Paths.get("public/images/" + product.getImageFileName());
			
			try {
				Files.delete(imagePath);
				
			} catch (Exception e) {
				System.out.println("Exception: " + e.getMessage());
			}
			repo.delete(product);
			
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		
		return "redirect:/products";
	}
}


