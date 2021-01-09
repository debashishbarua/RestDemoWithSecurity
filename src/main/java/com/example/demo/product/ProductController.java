package com.example.demo.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/products")
public class ProductController {
	
	@Autowired
	private ProductService productService;

	@PostMapping
	public Product addProduct(@RequestBody Product product) {
		return productService.save(product);
	}

	@GetMapping("/{id}")
	public Product getProductDetails(@PathVariable int id) {
		return productService.findById(id);
	}

	@GetMapping
	public List<Product> getProductList() {
		return productService.findAll();
	}

	@PutMapping("/{id}")
	public Product updateProduct(@RequestBody Product product, @PathVariable int id) {
		return productService.updateProduct(product, id);
	}

	@DeleteMapping("/{id}")
	public void deleteProduct(@PathVariable int id) {
		productService.deleteById(id);
		
	}
}
