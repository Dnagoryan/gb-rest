package ru.gb.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.entity.Cart;


import java.util.List;

public interface CartDao  extends JpaRepository<Cart, Long> {


    Cart findCartsById(Long id);

}
