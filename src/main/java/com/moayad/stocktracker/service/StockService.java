package com.moayad.stocktracker.service;

import com.moayad.stocktracker.client.StockClient;
import com.moayad.stocktracker.dto.*;
import com.moayad.stocktracker.entity.FavoriteStock;
import com.moayad.stocktracker.exception.FavoriteAlreadyExistsException;
import com.moayad.stocktracker.repository.FavoriteStockRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class StockService {

    private final StockClient stockClient;
    private FavoriteStockRepository favoriteStockRepository;

    public StockService(StockClient stockClient, FavoriteStockRepository favoriteStockRepository) {
        this.stockClient = stockClient;
        this.favoriteStockRepository = favoriteStockRepository;
    }
    @Cacheable(value = "stocks",key = "#stockSymbol")
    public StockResponse getStockForSymbol(final String stockSymbol) {

       final AlphaVantageResponse response = stockClient.getStockQuote(stockSymbol);
        return StockResponse.builder()
                .symbol(response.globalQuote().symbol())
                .price(Double.parseDouble(response.globalQuote().price()))
                .lastUpdated(response.globalQuote().lastTradingDay())
                .build();
    }

    public StockOverviewResponse getStockOverviewForSymbol(final String symbol){
        return stockClient.getStockOverview(symbol);
    }


    public List<DailyStockResponse> getHistory(String symbol, int days){
        StockHistoryResponse response = stockClient.getStockHistory(symbol);

        return response.timeSeries().entrySet().stream()
                .limit(days)
                .map(entry ->{
                    var date = entry.getKey();
                    var daily = entry.getValue();
                    return new DailyStockResponse(
                            date,
                            Double.parseDouble(daily.open()),
                            Double.parseDouble(daily.close()),
                            Double.parseDouble(daily.high()),
                            Double.parseDouble(daily.low()),
                            Long.parseLong(daily.volume())
                            );
                })
                .collect(Collectors.toList());
    }

    @Transactional //commit all or nothing
    public FavoriteStock addFavorite(final String symbol){
        if(favoriteStockRepository.existsBySymbol(symbol)){
            throw new FavoriteAlreadyExistsException(symbol);
        }
        FavoriteStock favoriteStock = FavoriteStock.builder()
                .symbol(symbol)
                .build();

        return favoriteStockRepository.save(favoriteStock);
    }

    public List<StockResponse> getFavoritesWithLivePrices(){
        List<FavoriteStock> favorites = favoriteStockRepository.findAll();

        return favorites.stream()
                .map(fav -> getStockForSymbol(fav.getSymbol()))
                .collect(Collectors.toList());

    }
}
