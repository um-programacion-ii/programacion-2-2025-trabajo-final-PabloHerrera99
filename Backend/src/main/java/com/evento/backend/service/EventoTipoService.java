package com.evento.backend.service;

import com.evento.backend.domain.EventoTipo;
import com.evento.backend.repository.EventoTipoRepository;
import com.evento.backend.service.dto.EventoTipoDTO;
import com.evento.backend.service.mapper.EventoTipoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.evento.backend.domain.EventoTipo}.
 */
@Service
@Transactional
public class EventoTipoService {

    private static final Logger LOG = LoggerFactory.getLogger(EventoTipoService.class);

    private final EventoTipoRepository eventoTipoRepository;

    private final EventoTipoMapper eventoTipoMapper;

    public EventoTipoService(EventoTipoRepository eventoTipoRepository, EventoTipoMapper eventoTipoMapper) {
        this.eventoTipoRepository = eventoTipoRepository;
        this.eventoTipoMapper = eventoTipoMapper;
    }

    /**
     * Save a eventoTipo.
     *
     * @param eventoTipoDTO the entity to save.
     * @return the persisted entity.
     */
    public EventoTipoDTO save(EventoTipoDTO eventoTipoDTO) {
        LOG.debug("Request to save EventoTipo : {}", eventoTipoDTO);
        EventoTipo eventoTipo = eventoTipoMapper.toEntity(eventoTipoDTO);
        eventoTipo = eventoTipoRepository.save(eventoTipo);
        return eventoTipoMapper.toDto(eventoTipo);
    }

    /**
     * Update a eventoTipo.
     *
     * @param eventoTipoDTO the entity to save.
     * @return the persisted entity.
     */
    public EventoTipoDTO update(EventoTipoDTO eventoTipoDTO) {
        LOG.debug("Request to update EventoTipo : {}", eventoTipoDTO);
        EventoTipo eventoTipo = eventoTipoMapper.toEntity(eventoTipoDTO);
        eventoTipo = eventoTipoRepository.save(eventoTipo);
        return eventoTipoMapper.toDto(eventoTipo);
    }

    /**
     * Partially update a eventoTipo.
     *
     * @param eventoTipoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EventoTipoDTO> partialUpdate(EventoTipoDTO eventoTipoDTO) {
        LOG.debug("Request to partially update EventoTipo : {}", eventoTipoDTO);

        return eventoTipoRepository
            .findById(eventoTipoDTO.getId())
            .map(existingEventoTipo -> {
                eventoTipoMapper.partialUpdate(existingEventoTipo, eventoTipoDTO);

                return existingEventoTipo;
            })
            .map(eventoTipoRepository::save)
            .map(eventoTipoMapper::toDto);
    }

    /**
     * Get all the eventoTipos.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EventoTipoDTO> findAll() {
        LOG.debug("Request to get all EventoTipos");
        return eventoTipoRepository.findAll().stream().map(eventoTipoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one eventoTipo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EventoTipoDTO> findOne(Long id) {
        LOG.debug("Request to get EventoTipo : {}", id);
        return eventoTipoRepository.findById(id).map(eventoTipoMapper::toDto);
    }

    /**
     * Delete the eventoTipo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete EventoTipo : {}", id);
        eventoTipoRepository.deleteById(id);
    }
}
