package com.srumec.adminapi.web;

import com.srumec.adminapi.domain.Alert;
import com.srumec.adminapi.domain.Event;
import com.srumec.adminapi.repo.AlertRepository;
import com.srumec.adminapi.repo.EventRepository;
import com.srumec.adminapi.service.ContentScannerService;
import com.srumec.adminapi.web.dto.CreateEventDTO;
import com.srumec.adminapi.web.dto.DecisionDTO;
import com.srumec.adminapi.service.ModerationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/event")
public class EventController {

    private final EventRepository repo;
    private final ModerationService moderation;
    private final ContentScannerService scanner;
    private final AlertRepository alertRepo;

    public EventController(EventRepository repo,
                           ModerationService moderation,
                           ContentScannerService scanner,
                           AlertRepository alertRepo) {
        this.repo = repo;
        this.moderation = moderation;
        this.scanner = scanner;
        this.alertRepo = alertRepo;
    }

    // ✅ CREATE (kontroluje riziková slova)
    @PostMapping(consumes = "application/json", produces = "application/json")
    public Event create(@RequestBody CreateEventDTO dto) {
        var e = new Event();
        e.setNazev(dto.nazev());
        e.setPopis(dto.popis());
        e.setLat(dto.lat());
        e.setLng(dto.lng());
        e.setUserId(dto.userId());

        // Kontrola obsahu
        boolean suspicious = scanner.isSuspicious(dto.nazev() + " " + dto.popis());
        e = repo.save(e); // nejdřív ulož event, ať má ID

        if (suspicious) {
            var alert = new Alert();
            alert.setTitle("Podezřelý obsah: " + dto.nazev());
            alert.setId(e.getId());
            alertRepo.save(alert);
        }

        return e;
    }

    // GET /event – všechny události
    @GetMapping
    public List<Event> all() {
        return repo.findAll();
    }

    // GET /event/{id}
    @GetMapping("/{id}")
    public Event one(@PathVariable UUID id) {
        return repo.findById(id).orElseThrow();
    }

    // POST /event/{id} – schválení / zamítnutí
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void decide(@PathVariable UUID id, @RequestBody DecisionDTO dto) {
        moderation.decide(id, dto);
    }
}
