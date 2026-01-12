package com.moayad.stocktracker.controller;

import com.moayad.stocktracker.dto.DailyStockResponse;
import com.moayad.stocktracker.dto.FavoriteStockRequest;
import com.moayad.stocktracker.dto.StockOverviewResponse;
import com.moayad.stocktracker.dto.StockResponse;
import com.moayad.stocktracker.entity.FavoriteStock;
import com.moayad.stocktracker.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    public final StockService stockService;

    @Autowired
    public StockController(final StockService stockService){
        this.stockService = stockService;
    }

    @GetMapping("/{stockSymbol}")
    public StockResponse getStock(@PathVariable String stockSymbol){
        return stockService.getStockForSymbol(stockSymbol.toUpperCase());
    }

    @GetMapping("/{stockSymbol}/overview")
    public StockOverviewResponse getStockOverview(@PathVariable String stockSymbol){
        return stockService.getStockOverviewForSymbol(stockSymbol.toUpperCase());
    }

    @GetMapping("/{symbol}/history")
    public List<DailyStockResponse> getStockHistory(@PathVariable String symbol
            ,@RequestParam(defaultValue = "30") int days){
        return stockService.getHistory(symbol.toUpperCase(),days);
    }

    @PostMapping("/favorites")
    public ResponseEntity<FavoriteStock> saveFavoriteStock(@RequestBody FavoriteStockRequest request){
        final FavoriteStock saved = stockService.addFavorite(request.getSymbol());
        return ResponseEntity.ok().body(saved);
    }

    @GetMapping("/favorites")
    public List<StockResponse> getFavoriteStocksWithPrices(){
        return stockService.getFavoritesWithLivePrices();
    }
}
