package com.moviebooking.entity;

import com.moviebooking.entity.enums.CastRoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movie_cast")
@Getter
@Setter
@NoArgsConstructor
public class MovieCast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "person_name", nullable = false)
    private String personName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false, length = 50)
    private CastRoleType roleType;

    @Column(name = "character_name")
    private String characterName;

    @Column(name = "display_order")
    private Integer displayOrder = 0;
}
