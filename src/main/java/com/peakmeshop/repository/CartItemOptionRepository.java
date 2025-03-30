package com.peakmeshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.peakmeshop.entity.CartItemOption;

@Repository
public interface CartItemOptionRepository extends JpaRepository<CartItemOption, Long> {

    List<CartItemOption> findByCartItemId(Long cartItemId);

    @Modifying
    @Query("DELETE FROM CartItemOption cio WHERE cio.cartItem.id = :cartItemId")
    void deleteByCartItemId(Long cartItemId);
}