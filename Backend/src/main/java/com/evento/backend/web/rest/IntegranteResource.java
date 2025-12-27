package com.evento.backend.web.rest;

import com.evento.backend.repository.IntegranteRepository;
import com.evento.backend.service.IntegranteService;
import com.evento.backend.service.dto.IntegranteDTO;
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
 * REST controller for managing {@link com.evento.backend.domain.Integrante}.
 */
@RestController
@RequestMapping("/api/integrantes")
public class IntegranteResource {

    private static final Logger LOG = LoggerFactory.getLogger(IntegranteResource.class);

    private static final String ENTITY_NAME = "integrante";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IntegranteService integranteService;

    private final IntegranteRepository integranteRepository;

    public IntegranteResource(IntegranteService integranteService, IntegranteRepository integranteRepository) {
        this.integranteService = integranteService;
        this.integranteRepository = integranteRepository;
    }

    /**
     * {@code POST  /integrantes} : Create a new integrante.
     *
     * @param integranteDTO the integranteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new integranteDTO, or with status {@code 400 (Bad Request)} if the integrante has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<IntegranteDTO> createIntegrante(@Valid @RequestBody IntegranteDTO integranteDTO) throws URISyntaxException {
        LOG.debug("REST request to save Integrante : {}", integranteDTO);
        if (integranteDTO.getId() != null) {
            throw new BadRequestAlertException("A new integrante cannot already have an ID", ENTITY_NAME, "idexists");
        }
        integranteDTO = integranteService.save(integranteDTO);
        return ResponseEntity.created(new URI("/api/integrantes/" + integranteDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, integranteDTO.getId().toString()))
            .body(integranteDTO);
    }

    /**
     * {@code PUT  /integrantes/:id} : Updates an existing integrante.
     *
     * @param id the id of the integranteDTO to save.
     * @param integranteDTO the integranteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated integranteDTO,
     * or with status {@code 400 (Bad Request)} if the integranteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the integranteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<IntegranteDTO> updateIntegrante(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody IntegranteDTO integranteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Integrante : {}, {}", id, integranteDTO);
        if (integranteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, integranteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!integranteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        integranteDTO = integranteService.update(integranteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, integranteDTO.getId().toString()))
            .body(integranteDTO);
    }

    /**
     * {@code PATCH  /integrantes/:id} : Partial updates given fields of an existing integrante, field will ignore if it is null
     *
     * @param id the id of the integranteDTO to save.
     * @param integranteDTO the integranteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated integranteDTO,
     * or with status {@code 400 (Bad Request)} if the integranteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the integranteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the integranteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<IntegranteDTO> partialUpdateIntegrante(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody IntegranteDTO integranteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Integrante partially : {}, {}", id, integranteDTO);
        if (integranteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, integranteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!integranteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<IntegranteDTO> result = integranteService.partialUpdate(integranteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, integranteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /integrantes} : get all the integrantes.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of integrantes in body.
     */
    @GetMapping("")
    public List<IntegranteDTO> getAllIntegrantes(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Integrantes");
        return integranteService.findAll();
    }

    /**
     * {@code GET  /integrantes/:id} : get the "id" integrante.
     *
     * @param id the id of the integranteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the integranteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<IntegranteDTO> getIntegrante(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Integrante : {}", id);
        Optional<IntegranteDTO> integranteDTO = integranteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(integranteDTO);
    }

    /**
     * {@code DELETE  /integrantes/:id} : delete the "id" integrante.
     *
     * @param id the id of the integranteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIntegrante(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Integrante : {}", id);
        integranteService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
