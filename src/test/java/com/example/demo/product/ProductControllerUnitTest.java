package com.example.demo.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.demo.exception.ProductNotFoundException;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = ProductController.class, 
            useDefaultFilters = false, 
            excludeAutoConfiguration = SecurityAutoConfiguration.class, 
            includeFilters = {
		       @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ProductController.class) }
)
class ProductControllerUnitTest {

	private static final int CREATED_PRODUCT_ID = 10;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService service;

	@Test
	@DisplayName("POST /product/1 - Success")
	void testAddProduct() throws Exception {
		Product productMock = new Product(CREATED_PRODUCT_ID, "name1", "category1", "description1", 750.00);
		doReturn(productMock).when(service).save(any());
		String expected = "{\"id\":" + CREATED_PRODUCT_ID+",\"name\":\"name1\",\"category\":\"category1\",\"description\":\"description1\",\"price\":750.00}";

		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.post("/products")
				.content(expected)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()) .andReturn();
				JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
	}

	@Test
	@DisplayName("GET /products/1 - Found")
	void testGetProductDetails() throws Exception {
		Product product = new Product(1, "name1", "category1", "description1", 750.00);
		when(service.findById(anyInt())).thenReturn(product);
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/products/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		String expected = "{id:1,name:name1,category:category1,description:description1,price:750.00}";
		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
	}

	@Test()
	@DisplayName("GET /products/50 - NotFound")
	void testGetProductDetails_NotFound() throws Exception {
		when(service.findById(anyInt())).thenThrow(new ProductNotFoundException("Product Not Found"));
	    mockMvc.perform(MockMvcRequestBuilders
		         .get("/products/50"))
				.andExpect(status().isNotFound());
		
	}
	
	@Test
	@DisplayName("GET /products - Success")
	void testGetProductList() throws Exception {
		List<Product> mockList = Arrays.asList(
				new Product(1, "name1", "category1", "description1", 750.00),
				new Product(2, "name2", "category2", "description2", 750.00));
		when(service.findAll()).thenReturn(mockList);
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/products").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		String expected = "["
				+ "{id:1,name:name1,category:category1,description:description1,price:750.00}" + ","
				+ "{id:2,name:name2,category:category2,description:description2,price:750.00}" + "]";
		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
	}

	@Test
	@DisplayName("PUT /products/1 - Success")
	void testUpdateProduct() throws Exception {
		Product productMock = new Product(CREATED_PRODUCT_ID, "name2", "category2", "description1", 750.00);
		doReturn(productMock).when(service).updateProduct(any(),anyInt());
		String expected = "{\"id\":" + CREATED_PRODUCT_ID+",\"name\":\"name2\",\"category\":\"category2\",\"description\":\"description1\",\"price\":750.00}";
		MvcResult result=mockMvc.perform(MockMvcRequestBuilders.put("/products/"+CREATED_PRODUCT_ID)
				.content(expected)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()) .andReturn();
				JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);

	}

	@Test
	@DisplayName("DELETE /products/1 - Present")
	void testDeleteProduct() throws Exception {
		doNothing().when(service).deleteById(anyInt());
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/products/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		System.out.println("Result: " + result);
	}

}
