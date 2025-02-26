package org.example.stockradar.feature.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Integer storeId;

    @Column(nullable = false, length = 50)
    private String storeName;

    //상품 관계설정
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    //재고상태 관계설정
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<StockStatus> stockStatuses = new ArrayList<>();

    //크롤러와 관계설정
    @OneToOne(fetch = FetchType.LAZY,mappedBy = "store")
    private StoreConfig storeConfig;

}
