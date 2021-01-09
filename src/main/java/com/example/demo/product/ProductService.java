package com.example.demo.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.exception.ProductNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	public Product save(Product product) {
		return productRepository.save(product);
	}

	public Product findById(int id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product Id : " + id + " does not exits"));
	}

	@GetMapping
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	public Product updateProduct(Product product, int id) {
		return productRepository.findById(id).map(p -> {
			p.setName(product.getName());
			p.setDescription(product.getDescription());
			p.setPrice(product.getPrice());
			p.setCategory(product.getCategory());
			return productRepository.save(p);
		}).orElseThrow(() -> new ProductNotFoundException("Product Id : \" + id + \" does not exits"));
	}

	@DeleteMapping("/{id}")
	public void deleteById(int id) {
		productRepository.deleteById(id);

	}

}
