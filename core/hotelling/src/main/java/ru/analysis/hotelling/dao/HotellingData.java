package ru.analysis.hotelling.dao;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Table(name = "hotelling_data")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotellingData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "param_key")
    String key;

    @Column(name = "param_value")
    Double value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id")
    HotellingAnalysis analysis;
}
