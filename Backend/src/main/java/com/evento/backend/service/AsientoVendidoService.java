package com.evento.backend.service;

import com.evento.backend.domain.AsientoVendido;
import com.evento.backend.repository.AsientoVendidoRepository;
import com.evento.backend.service.dto.AsientoVendidoDTO;
import com.evento.backend.service.mapper.AsientoVendidoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.evento.backend.domain.AsientoVendido}.
 */
@Service
@Transactional
public class AsientoVendidoService {

    private static final Logger LOG = LoggerFactory.getLogger(AsientoVendidoService.class);

    private final AsientoVendidoRepository asientoVendidoRepository;

    private final AsientoVendidoMapper asientoVendidoMapper;

    public AsientoVendidoService(AsientoVendidoRepository asientoVendidoRepository, AsientoVendidoMapper asientoVendidoMapper) {
        this.asientoVendidoRepository = asientoVendidoRepository;
        this.asientoVendidoMapper = asientoVendidoMapper;
    }

    /**
     * Save a asientoVendido.
     *
     * @param asientoVendidoDTO the entity to save.
     * @return the persisted entity.
     */
    public AsientoVendidoDTO save(AsientoVendidoDTO asientoVendidoDTO) {
        LOG.debug("Request to save AsientoVendido : {}", asientoVendidoDTO);
        AsientoVendido asientoVendido = asientoVendidoMapper.toEntity(asientoVendidoDTO);
        asientoVendido = asientoVendidoRepository.save(asientoVendido);
        return asientoVendidoMapper.toDto(asientoVendido);
    }

    /**
     * Update a asientoVendido.
     *
     * @param asientoVendidoDTO the entity to save.
     * @return the persisted entity.
     */
    public AsientoVendidoDTO update(AsientoVendidoDTO asientoVendidoDTO) {
        LOG.debug("Request to update AsientoVendido : {}", asientoVendidoDTO);
        AsientoVendido asientoVendido = asientoVendidoMapper.toEntity(asientoVendidoDTO);
        asientoVendido = asientoVendidoRepository.save(asientoVendido);
        return asientoVendidoMapper.toDto(asientoVendido);
    }

    /**
     * Partially update a asientoVendido.
     *
     * @param asientoVendidoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AsientoVendidoDTO> partialUpdate(AsientoVendidoDTO asientoVendidoDTO) {
        LOG.debug("Request to partially update AsientoVendido : {}", asientoVendidoDTO);

        return asientoVendidoRepository
            .findById(asientoVendidoDTO.getId())
            .map(existingAsientoVendido -> {
                asientoVendidoMapper.partialUpdate(existingAsientoVendido, asientoVendidoDTO);

                return existingAsientoVendido;
            })
            .map(asientoVendidoRepository::save)
            .map(asientoVendidoMapper::toDto);
    }

    /**
     * Get all the asientoVendidos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AsientoVendidoDTO> findAll() {
        LOG.debug("Request to get all AsientoVendidos");
        return asientoVendidoRepository
            .findAll()
            .stream()
            .map(asientoVendidoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one asientoVendido by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AsientoVendidoDTO> findOne(Long id) {
        LOG.debug("Request to get AsientoVendido : {}", id);
        return asientoVendidoRepository.findById(id).map(asientoVendidoMapper::toDto);
    }

    /**
     * Delete the asientoVendido by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AsientoVendido : {}", id);
        asientoVendidoRepository.deleteById(id);
    }
}
