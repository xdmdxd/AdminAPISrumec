package com.srumec.adminapi.web;

import com.srumec.adminapi.domain.Event;
import com.srumec.adminapi.repo.EventRepository;
import com.srumec.adminapi.service.ModerationService;
import com.srumec.adminapi.web.dto.DecisionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/event")
public class EventController {

    private final EventRepository repo;
    private final ModerationService moderation;

    public EventController(EventRepository repo, ModerationService moderation) {
        this.repo = repo;
        this.moderation = moderation;
    }

    // GET /event – získá všechny události
    @GetMapping
    public List<Event> all() {
        return repo.findAll();
    }

    // GET /event/{id} – detail
    @GetMapping("/{id}")
    public Event one(@PathVariable UUID id) {
        return repo.findById(id).orElseThrow();
    }

    // POST /event/{id} – rozhodnutí (body: { uuid, approved })
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void decide(@PathVariable UUID id, @RequestBody DecisionDTO dto) {
        moderation.decide(id, dto);
    }
}
