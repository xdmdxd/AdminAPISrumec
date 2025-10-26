package com.srumec.adminapi.service;

import com.srumec.adminapi.domain.Event;
import com.srumec.adminapi.repo.EventRepository;
import com.srumec.adminapi.web.dto.DecisionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ModerationService {

    private final EventRepository repo;
    private final RestClient eventsClient;

    // base URL na srumec_events bere z env proměnné SRUMEC_EVENTS_URL (fallback na localhost)
    public ModerationService(EventRepository repo) {
        this.repo = repo;
        String baseUrl = System.getenv().getOrDefault("SRUMEC_EVENTS_URL", "http://localhost:8081");
        this.eventsClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public void decide(UUID id, DecisionDTO req) {
        // ModerationService (když načítáš event před rozhodnutím)
        var ev = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event not found: " + id));


        // volitelná kontrola, když FE pošle i uuid v těle
        if (req.uuid() != null && !id.equals(req.uuid())) {
            throw new IllegalArgumentException("Path id and body uuid differ");
        }

        if (req.approved()) {
            // ✅ schváleno → pošli event do srumec_events (endpoint si sjednotíte; teď placeholder)
            try {
                eventsClient.post()
                        .uri("/events/approved")   //TODO upravte na reálný endpoint
                        .body(ev)
                        .retrieve()
                        .toBodilessEntity();
            } catch (Exception ignored) {
                // necháme „fire-and-forget“ – případně zaloguj
            }
            //TODO
            // přidat sloupec sloupec 'approved_at' nebo 'state')
        } else {
            // ❌ zamítnuto → pošli také informaci o zamítnutí
            try {
                eventsClient.post()
                        .uri("/events/rejected")
                        .body(ev)
                        .retrieve()
                        .toBodilessEntity();
            }catch (Exception ignored) {
                // necháme „fire-and-forget“ – případně zaloguj
            }

            // a teprve potom smaž z DB
            repo.deleteById(id);
        }
    }

}
