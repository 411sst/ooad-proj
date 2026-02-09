package com.moviebooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "preferred_genres", columnDefinition = "TEXT")
    private String preferredGenres;

    @Column(name = "preferred_languages", columnDefinition = "TEXT")
    private String preferredLanguages;

    @Column(name = "preferred_theater_types", columnDefinition = "TEXT")
    private String preferredTheaterTypes;

    @Column(name = "notification_email")
    private Boolean notificationEmail = true;

    @Column(name = "notification_sms")
    private Boolean notificationSms = false;

    @Column(name = "show_reminders")
    private Boolean showReminders = true;
}
