package com.moayad.stocktracker.repository;

import com.moayad.stocktracker.entity.FavoriteStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteStockRepository extends JpaRepository<FavoriteStock,Long> {

    boolean existsBySymbol(String symbol);


}
