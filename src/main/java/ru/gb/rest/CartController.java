package ru.gb.rest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.gb.dto.ProductDto;
import ru.gb.entity.Cart;
import ru.gb.entity.Product;
import ru.gb.service.CartService;
import ru.gb.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
@Slf4j
public class CartController {

    private final ProductService productService;
    private final CartService cartService;
    private final Cart cart = Cart.builder().build();

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCartList(@PathVariable("cartId") Long id) {
        Optional<Cart> cartOptional = Optional.ofNullable(cartService.findById(id));
        if (cartOptional.isPresent()) {
            cart.setProducts(cartOptional.get().getProducts());
            return new ResponseEntity<>(cart.getProducts(),HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
    }

    //здесь должна быть  юзер и проверка на принадлежность юзера к корзинам(не знаю надо ли создавать новую корзину)
    @PostMapping
    public ResponseEntity<?> handlePost(@Validated @RequestBody ProductDto productDto) {
        List<Product> productList;
        Product product = productService.findById(productDto.getId());
        if (cart.getId() == null) {
            productList = new ArrayList<>();
            productList.add(product);
            cart.setProducts(productList);
            Cart cartFromDb = cartService.save(cart);
            cart.setId(cartFromDb.getId());
            return new ResponseEntity<>(cart, HttpStatus.CREATED);
        } else {
            Cart cartFromDb = cartService.findById(cart.getId());
            productList = cartFromDb.getProducts();
            productList.removeIf(product1 -> product1.getId().equals(productDto.getId()));
            productList.add(product);
            cart.setProducts(productList);
            cartService.save(cart);
            return new ResponseEntity<>(cart,HttpStatus.OK);
        }
    }

     @DeleteMapping("/{cartId}")
     public ResponseEntity<?> handleUpdate(@PathVariable("cartId") Long id,
                                           @Validated @RequestBody ProductDto productDto) {
         Optional<Cart> cartOptional = Optional.ofNullable(cartService.findById(id));
         if (cartOptional.isPresent()) {
             cart.setProducts(cartOptional.get().getProducts());
             cart.getProducts().removeIf(product -> product.getId().equals(productDto.getId()));
             cartService.save(cart);
             return new ResponseEntity<>(cart,HttpStatus.OK);
         }else {
             return  new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
         }
     }

}


