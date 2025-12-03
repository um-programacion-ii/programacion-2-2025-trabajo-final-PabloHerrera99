package com.evento.backend.web.rest;

import com.evento.backend.repository.SesionRepository;
import com.evento.backend.service.SesionQueryService;
import com.evento.backend.service.SesionService;
import com.evento.backend.service.criteria.SesionCriteria;
import com.evento.backend.service.dto.SesionDTO;
import com.evento.backend.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.evento.backend.domain.Sesion}.
 */
@RestController
@RequestMapping("/api/sesions")
public class SesionResource {

    private static final Logger LOG = LoggerFactory.getLogger(SesionResource.class);

    private static final String ENTITY_NAME = "sesion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SesionService sesionService;

    private final SesionRepository sesionRepository;

    private final SesionQueryService sesionQueryService;

    public SesionResource(SesionService sesionService, SesionRepository sesionRepository, SesionQueryService sesionQueryService) {
        this.sesionService = sesionService;
        this.sesionRepository = sesionRepository;
        this.sesionQueryService = sesionQueryService;
    }

    /**
     * {@code POST  /sesions} : Create a new sesion.
     *
     * @param sesionDTO the sesionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sesionDTO, or with status {@code 400 (Bad Request)} if the sesion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SesionDTO> createSesion(@Valid @RequestBody SesionDTO sesionDTO) throws URISyntaxException {
        LOG.debug("REST request to save Sesion : {}", sesionDTO);
        if (sesionDTO.getId() != null) {
            throw new BadRequestAlertException("A new sesion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        sesionDTO = sesionService.save(sesionDTO);
        return ResponseEntity.created(new URI("/api/sesions/" + sesionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, sesionDTO.getId().toString()))
            .body(sesionDTO);
    }

    /**
     * {@code PUT  /sesions/:id} : Updates an existing sesion.
     *
     * @param id the id of the sesionDTO to save.
     * @param sesionDTO the sesionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sesionDTO,
     * or with status {@code 400 (Bad Request)} if the sesionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sesionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SesionDTO> updateSesion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SesionDTO sesionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Sesion : {}, {}", id, sesionDTO);
        if (sesionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sesionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sesionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        sesionDTO = sesionService.update(sesionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sesionDTO.getId().toString()))
            .body(sesionDTO);
    }

    /**
     * {@code PATCH  /sesions/:id} : Partial updates given fields of an existing sesion, field will ignore if it is null
     *
     * @param id the id of the sesionDTO to save.
     * @param sesionDTO the sesionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sesionDTO,
     * or with status {@code 400 (Bad Request)} if the sesionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the sesionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the sesionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SesionDTO> partialUpdateSesion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SesionDTO sesionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Sesion partially : {}, {}", id, sesionDTO);
        if (sesionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sesionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sesionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SesionDTO> result = sesionService.partialUpdate(sesionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sesionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sesions} : get all the sesions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sesions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SesionDTO>> getAllSesions(SesionCriteria criteria) {
        LOG.debug("REST request to get Sesions by criteria: {}", criteria);

        List<SesionDTO> entityList = sesionQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /sesions/count} : count all the sesions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSesions(SesionCriteria criteria) {
        LOG.debug("REST request to count Sesions by criteria: {}", criteria);
        return ResponseEntity.ok().body(sesionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sesions/:id} : get the "id" sesion.
     *
     * @param id the id of the sesionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sesionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SesionDTO> getSesion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Sesion : {}", id);
        Optional<SesionDTO> sesionDTO = sesionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sesionDTO);
    }

    /**
     * {@code DELETE  /sesions/:id} : delete the "id" sesion.
     *
     * @param id the id of the sesionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSesion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Sesion : {}", id);
        sesionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
