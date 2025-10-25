package com.srumec.adminapi.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "banned_word")
public class BannedWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
}
