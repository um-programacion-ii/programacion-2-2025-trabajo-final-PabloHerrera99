package com.evento.backend.service;

import com.evento.backend.domain.AsientoSeleccionado;
import com.evento.backend.repository.AsientoSeleccionadoRepository;
import com.evento.backend.service.dto.AsientoSeleccionadoDTO;
import com.evento.backend.service.mapper.AsientoSeleccionadoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.evento.backend.domain.AsientoSeleccionado}.
 */
@Service
@Transactional
public class AsientoSeleccionadoService {

    private static final Logger LOG = LoggerFactory.getLogger(AsientoSeleccionadoService.class);

    private final AsientoSeleccionadoRepository asientoSeleccionadoRepository;

    private final AsientoSeleccionadoMapper asientoSeleccionadoMapper;

    public AsientoSeleccionadoService(
        AsientoSeleccionadoRepository asientoSeleccionadoRepository,
        AsientoSeleccionadoMapper asientoSeleccionadoMapper
    ) {
        this.asientoSeleccionadoRepository = asientoSeleccionadoRepository;
        this.asientoSeleccionadoMapper = asientoSeleccionadoMapper;
    }

    /**
     * Save a asientoSeleccionado.
     *
     * @param asientoSeleccionadoDTO the entity to save.
     * @return the persisted entity.
     */
    public AsientoSeleccionadoDTO save(AsientoSeleccionadoDTO asientoSeleccionadoDTO) {
        LOG.debug("Request to save AsientoSeleccionado : {}", asientoSeleccionadoDTO);
        AsientoSeleccionado asientoSeleccionado = asientoSeleccionadoMapper.toEntity(asientoSeleccionadoDTO);
        asientoSeleccionado = asientoSeleccionadoRepository.save(asientoSeleccionado);
        return asientoSeleccionadoMapper.toDto(asientoSeleccionado);
    }

    /**
     * Update a asientoSeleccionado.
     *
     * @param asientoSeleccionadoDTO the entity to save.
     * @return the persisted entity.
     */
    public AsientoSeleccionadoDTO update(AsientoSeleccionadoDTO asientoSeleccionadoDTO) {
        LOG.debug("Request to update AsientoSeleccionado : {}", asientoSeleccionadoDTO);
        AsientoSeleccionado asientoSeleccionado = asientoSeleccionadoMapper.toEntity(asientoSeleccionadoDTO);
        asientoSeleccionado = asientoSeleccionadoRepository.save(asientoSeleccionado);
        return asientoSeleccionadoMapper.toDto(asientoSeleccionado);
    }

    /**
     * Partially update a asientoSeleccionado.
     *
     * @param asientoSeleccionadoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AsientoSeleccionadoDTO> partialUpdate(AsientoSeleccionadoDTO asientoSeleccionadoDTO) {
        LOG.debug("Request to partially update AsientoSeleccionado : {}", asientoSeleccionadoDTO);

        return asientoSeleccionadoRepository
            .findById(asientoSeleccionadoDTO.getId())
            .map(existingAsientoSeleccionado -> {
                asientoSeleccionadoMapper.partialUpdate(existingAsientoSeleccionado, asientoSeleccionadoDTO);

                return existingAsientoSeleccionado;
            })
            .map(asientoSeleccionadoRepository::save)
            .map(asientoSeleccionadoMapper::toDto);
    }

    /**
     * Get all the asientoSeleccionados.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AsientoSeleccionadoDTO> findAll() {
        LOG.debug("Request to get all AsientoSeleccionados");
        return asientoSeleccionadoRepository
            .findAll()
            .stream()
            .map(asientoSeleccionadoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one asientoSeleccionado by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AsientoSeleccionadoDTO> findOne(Long id) {
        LOG.debug("Request to get AsientoSeleccionado : {}", id);
        return asientoSeleccionadoRepository.findById(id).map(asientoSeleccionadoMapper::toDto);
    }

    /**
     * Delete the asientoSeleccionado by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AsientoSeleccionado : {}", id);
        asientoSeleccionadoRepository.deleteById(id);
    }
}
