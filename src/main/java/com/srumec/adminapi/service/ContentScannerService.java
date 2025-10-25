package com.srumec.adminapi.service;

import com.srumec.adminapi.repo.BannedWordRepository;
import org.springframework.stereotype.Service;

@Service
public class ContentScannerService {

    private final BannedWordRepository bannedRepo;

    public ContentScannerService(BannedWordRepository bannedRepo) {
        this.bannedRepo = bannedRepo;
    }

    public boolean isSuspicious(String text) {
        if (text == null) return false;
        String lower = text.toLowerCase();

        return bannedRepo.findAll().stream()
                .map(b -> b.getWord().toLowerCase())
                .anyMatch(lower::contains);
    }
}
