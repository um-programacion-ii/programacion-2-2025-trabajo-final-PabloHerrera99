package com.evento.backend.service;

import com.evento.backend.domain.*; // for static metamodels
import com.evento.backend.domain.Sesion;
import com.evento.backend.repository.SesionRepository;
import com.evento.backend.service.criteria.SesionCriteria;
import com.evento.backend.service.dto.SesionDTO;
import com.evento.backend.service.mapper.SesionMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Sesion} entities in the database.
 * The main input is a {@link SesionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SesionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SesionQueryService extends QueryService<Sesion> {

    private static final Logger LOG = LoggerFactory.getLogger(SesionQueryService.class);

    private final SesionRepository sesionRepository;

    private final SesionMapper sesionMapper;

    public SesionQueryService(SesionRepository sesionRepository, SesionMapper sesionMapper) {
        this.sesionRepository = sesionRepository;
        this.sesionMapper = sesionMapper;
    }

    /**
     * Return a {@link List} of {@link SesionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SesionDTO> findByCriteria(SesionCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Sesion> specification = createSpecification(criteria);
        return sesionMapper.toDto(sesionRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SesionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Sesion> specification = createSpecification(criteria);
        return sesionRepository.count(specification);
    }

    /**
     * Function to convert {@link SesionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Sesion> createSpecification(SesionCriteria criteria) {
        Specification<Sesion> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Sesion_.id),
                buildSpecification(criteria.getEstado(), Sesion_.estado),
                buildRangeSpecification(criteria.getFechaInicio(), Sesion_.fechaInicio),
                buildRangeSpecification(criteria.getUltimaActividad(), Sesion_.ultimaActividad),
                buildRangeSpecification(criteria.getExpiracion(), Sesion_.expiracion),
                buildSpecification(criteria.getActiva(), Sesion_.activa),
                buildSpecification(criteria.getUsuarioId(), root -> root.join(Sesion_.usuario, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getEventoId(), root -> root.join(Sesion_.evento, JoinType.LEFT).get(Evento_.id))
            );
        }
        return specification;
    }
}
