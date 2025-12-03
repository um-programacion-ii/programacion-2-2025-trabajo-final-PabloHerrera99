package com.evento.backend.service;

import com.evento.backend.domain.Integrante;
import com.evento.backend.repository.IntegranteRepository;
import com.evento.backend.service.dto.IntegranteDTO;
import com.evento.backend.service.mapper.IntegranteMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.evento.backend.domain.Integrante}.
 */
@Service
@Transactional
public class IntegranteService {

    private static final Logger LOG = LoggerFactory.getLogger(IntegranteService.class);

    private final IntegranteRepository integranteRepository;

    private final IntegranteMapper integranteMapper;

    public IntegranteService(IntegranteRepository integranteRepository, IntegranteMapper integranteMapper) {
        this.integranteRepository = integranteRepository;
        this.integranteMapper = integranteMapper;
    }

    /**
     * Save a integrante.
     *
     * @param integranteDTO the entity to save.
     * @return the persisted entity.
     */
    public IntegranteDTO save(IntegranteDTO integranteDTO) {
        LOG.debug("Request to save Integrante : {}", integranteDTO);
        Integrante integrante = integranteMapper.toEntity(integranteDTO);
        integrante = integranteRepository.save(integrante);
        return integranteMapper.toDto(integrante);
    }

    /**
     * Update a integrante.
     *
     * @param integranteDTO the entity to save.
     * @return the persisted entity.
     */
    public IntegranteDTO update(IntegranteDTO integranteDTO) {
        LOG.debug("Request to update Integrante : {}", integranteDTO);
        Integrante integrante = integranteMapper.toEntity(integranteDTO);
        integrante = integranteRepository.save(integrante);
        return integranteMapper.toDto(integrante);
    }

    /**
     * Partially update a integrante.
     *
     * @param integranteDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<IntegranteDTO> partialUpdate(IntegranteDTO integranteDTO) {
        LOG.debug("Request to partially update Integrante : {}", integranteDTO);

        return integranteRepository
            .findById(integranteDTO.getId())
            .map(existingIntegrante -> {
                integranteMapper.partialUpdate(existingIntegrante, integranteDTO);

                return existingIntegrante;
            })
            .map(integranteRepository::save)
            .map(integranteMapper::toDto);
    }

    /**
     * Get all the integrantes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<IntegranteDTO> findAll() {
        LOG.debug("Request to get all Integrantes");
        return integranteRepository.findAll().stream().map(integranteMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the integrantes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<IntegranteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return integranteRepository.findAllWithEagerRelationships(pageable).map(integranteMapper::toDto);
    }

    /**
     * Get one integrante by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<IntegranteDTO> findOne(Long id) {
        LOG.debug("Request to get Integrante : {}", id);
        return integranteRepository.findOneWithEagerRelationships(id).map(integranteMapper::toDto);
    }

    /**
     * Delete the integrante by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Integrante : {}", id);
        integranteRepository.deleteById(id);
    }
}
