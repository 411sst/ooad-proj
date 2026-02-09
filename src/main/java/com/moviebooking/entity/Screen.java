package com.moviebooking.entity;

import com.moviebooking.entity.enums.ScreenType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screens", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"theater_id", "screen_number"})
})
@Getter
@Setter
@NoArgsConstructor
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(name = "screen_number", nullable = false)
    private Integer screenNumber;

    @Column(name = "screen_name", length = 100)
    private String screenName;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer rows;

    @Column(name = "seat_columns", nullable = false)
    private Integer columns;

    @Enumerated(EnumType.STRING)
    @Column(name = "screen_type", nullable = false, length = 20)
    private ScreenType screenType;

    @Column(name = "sound_system", length = 100)
    private String soundSystem;

    @Column(name = "screen_size", length = 50)
    private String screenSize;

    @Column(name = "special_features", columnDefinition = "TEXT")
    private String specialFeatures;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> seats = new ArrayList<>();

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Showtime> showtimes = new ArrayList<>();
}
