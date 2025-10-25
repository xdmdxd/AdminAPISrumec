package com.srumec.adminapi.repo;

import com.srumec.adminapi.domain.BannedWord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannedWordRepository extends JpaRepository<BannedWord, Long> {}
