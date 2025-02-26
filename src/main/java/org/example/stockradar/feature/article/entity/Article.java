package org.example.stockradar.feature.article.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long articleId;

    @Column( nullable = false, length = 100)
    private String articleName;

    @Column( nullable = false, columnDefinition = "TEXT")
    private String articleContent;

    @Column( nullable = false, length = 200)
    private String sourceUrl;

    @Column( nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime publishedDate;

    @PrePersist
    protected void onCreate() {
        publishedDate = LocalDateTime.now();
    }
}
