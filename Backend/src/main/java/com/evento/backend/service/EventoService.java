package com.evento.backend.service;

import com.evento.backend.domain.Evento;
import com.evento.backend.repository.EventoRepository;
import com.evento.backend.service.dto.EventoDTO;
import com.evento.backend.service.mapper.EventoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.evento.backend.domain.Evento}.
 */
@Service
@Transactional
public class EventoService {

    private static final Logger LOG = LoggerFactory.getLogger(EventoService.class);

    private final EventoRepository eventoRepository;

    private final EventoMapper eventoMapper;

    public EventoService(EventoRepository eventoRepository, EventoMapper eventoMapper) {
        this.eventoRepository = eventoRepository;
        this.eventoMapper = eventoMapper;
    }

    /**
     * Save a evento.
     *
     * @param eventoDTO the entity to save.
     * @return the persisted entity.
     */
    public EventoDTO save(EventoDTO eventoDTO) {
        LOG.debug("Request to save Evento : {}", eventoDTO);
        Evento evento = eventoMapper.toEntity(eventoDTO);
        evento = eventoRepository.save(evento);
        return eventoMapper.toDto(evento);
    }

    /**
     * Update a evento.
     *
     * @param eventoDTO the entity to save.
     * @return the persisted entity.
     */
    public EventoDTO update(EventoDTO eventoDTO) {
        LOG.debug("Request to update Evento : {}", eventoDTO);
        Evento evento = eventoMapper.toEntity(eventoDTO);
        evento = eventoRepository.save(evento);
        return eventoMapper.toDto(evento);
    }

    /**
     * Partially update a evento.
     *
     * @param eventoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EventoDTO> partialUpdate(EventoDTO eventoDTO) {
        LOG.debug("Request to partially update Evento : {}", eventoDTO);

        return eventoRepository
            .findById(eventoDTO.getId())
            .map(existingEvento -> {
                eventoMapper.partialUpdate(existingEvento, eventoDTO);

                return existingEvento;
            })
            .map(eventoRepository::save)
            .map(eventoMapper::toDto);
    }

    /**
     * Get all the eventos with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EventoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return eventoRepository.findAllWithEagerRelationships(pageable).map(eventoMapper::toDto);
    }

    /**
     * Get one evento by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EventoDTO> findOne(Long id) {
        LOG.debug("Request to get Evento : {}", id);
        return eventoRepository.findOneWithEagerRelationships(id).map(eventoMapper::toDto);
    }

    /**
     * Delete the evento by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Evento : {}", id);
        eventoRepository.deleteById(id);
    }
}
