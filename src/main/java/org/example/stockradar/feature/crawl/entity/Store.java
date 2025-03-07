package org.example.stockradar.feature.crawl.entity;

import jakarta.persistence.*;
import lombok.*;


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

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> products;
}
