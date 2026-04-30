package ru.analysis.hotelling.dao;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hotelling_analysis")
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotellingAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<HotellingData> dataPoints;

    LocalDateTime createdAt;
}
