package com.evento.backend.web.rest;

import com.evento.backend.repository.AsientoSeleccionadoRepository;
import com.evento.backend.service.AsientoSeleccionadoService;
import com.evento.backend.service.dto.AsientoSeleccionadoDTO;
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
 * REST controller for managing {@link com.evento.backend.domain.AsientoSeleccionado}.
 */
@RestController
@RequestMapping("/api/asiento-seleccionados")
public class AsientoSeleccionadoResource {

    private static final Logger LOG = LoggerFactory.getLogger(AsientoSeleccionadoResource.class);

    private static final String ENTITY_NAME = "asientoSeleccionado";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AsientoSeleccionadoService asientoSeleccionadoService;

    private final AsientoSeleccionadoRepository asientoSeleccionadoRepository;

    public AsientoSeleccionadoResource(
        AsientoSeleccionadoService asientoSeleccionadoService,
        AsientoSeleccionadoRepository asientoSeleccionadoRepository
    ) {
        this.asientoSeleccionadoService = asientoSeleccionadoService;
        this.asientoSeleccionadoRepository = asientoSeleccionadoRepository;
    }

    /**
     * {@code POST  /asiento-seleccionados} : Create a new asientoSeleccionado.
     *
     * @param asientoSeleccionadoDTO the asientoSeleccionadoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new asientoSeleccionadoDTO, or with status {@code 400 (Bad Request)} if the asientoSeleccionado has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AsientoSeleccionadoDTO> createAsientoSeleccionado(
        @Valid @RequestBody AsientoSeleccionadoDTO asientoSeleccionadoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save AsientoSeleccionado : {}", asientoSeleccionadoDTO);
        if (asientoSeleccionadoDTO.getId() != null) {
            throw new BadRequestAlertException("A new asientoSeleccionado cannot already have an ID", ENTITY_NAME, "idexists");
        }
        asientoSeleccionadoDTO = asientoSeleccionadoService.save(asientoSeleccionadoDTO);
        return ResponseEntity.created(new URI("/api/asiento-seleccionados/" + asientoSeleccionadoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, asientoSeleccionadoDTO.getId().toString()))
            .body(asientoSeleccionadoDTO);
    }

    /**
     * {@code PUT  /asiento-seleccionados/:id} : Updates an existing asientoSeleccionado.
     *
     * @param id the id of the asientoSeleccionadoDTO to save.
     * @param asientoSeleccionadoDTO the asientoSeleccionadoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated asientoSeleccionadoDTO,
     * or with status {@code 400 (Bad Request)} if the asientoSeleccionadoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the asientoSeleccionadoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AsientoSeleccionadoDTO> updateAsientoSeleccionado(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AsientoSeleccionadoDTO asientoSeleccionadoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AsientoSeleccionado : {}, {}", id, asientoSeleccionadoDTO);
        if (asientoSeleccionadoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, asientoSeleccionadoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!asientoSeleccionadoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        asientoSeleccionadoDTO = asientoSeleccionadoService.update(asientoSeleccionadoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, asientoSeleccionadoDTO.getId().toString()))
            .body(asientoSeleccionadoDTO);
    }

    /**
     * {@code PATCH  /asiento-seleccionados/:id} : Partial updates given fields of an existing asientoSeleccionado, field will ignore if it is null
     *
     * @param id the id of the asientoSeleccionadoDTO to save.
     * @param asientoSeleccionadoDTO the asientoSeleccionadoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated asientoSeleccionadoDTO,
     * or with status {@code 400 (Bad Request)} if the asientoSeleccionadoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the asientoSeleccionadoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the asientoSeleccionadoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AsientoSeleccionadoDTO> partialUpdateAsientoSeleccionado(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AsientoSeleccionadoDTO asientoSeleccionadoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AsientoSeleccionado partially : {}, {}", id, asientoSeleccionadoDTO);
        if (asientoSeleccionadoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, asientoSeleccionadoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!asientoSeleccionadoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AsientoSeleccionadoDTO> result = asientoSeleccionadoService.partialUpdate(asientoSeleccionadoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, asientoSeleccionadoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /asiento-seleccionados} : get all the asientoSeleccionados.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of asientoSeleccionados in body.
     */
    @GetMapping("")
    public List<AsientoSeleccionadoDTO> getAllAsientoSeleccionados() {
        LOG.debug("REST request to get all AsientoSeleccionados");
        return asientoSeleccionadoService.findAll();
    }

    /**
     * {@code GET  /asiento-seleccionados/:id} : get the "id" asientoSeleccionado.
     *
     * @param id the id of the asientoSeleccionadoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the asientoSeleccionadoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AsientoSeleccionadoDTO> getAsientoSeleccionado(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AsientoSeleccionado : {}", id);
        Optional<AsientoSeleccionadoDTO> asientoSeleccionadoDTO = asientoSeleccionadoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(asientoSeleccionadoDTO);
    }

    /**
     * {@code DELETE  /asiento-seleccionados/:id} : delete the "id" asientoSeleccionado.
     *
     * @param id the id of the asientoSeleccionadoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsientoSeleccionado(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AsientoSeleccionado : {}", id);
        asientoSeleccionadoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
