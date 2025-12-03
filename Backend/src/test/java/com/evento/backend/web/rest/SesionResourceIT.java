package com.evento.backend.web.rest;

import static com.evento.backend.domain.SesionAsserts.*;
import static com.evento.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.evento.backend.IntegrationTest;
import com.evento.backend.domain.Evento;
import com.evento.backend.domain.Sesion;
import com.evento.backend.domain.User;
import com.evento.backend.domain.enumeration.EstadoSesion;
import com.evento.backend.repository.SesionRepository;
import com.evento.backend.repository.UserRepository;
import com.evento.backend.service.SesionService;
import com.evento.backend.service.dto.SesionDTO;
import com.evento.backend.service.mapper.SesionMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SesionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SesionResourceIT {

    private static final EstadoSesion DEFAULT_ESTADO = EstadoSesion.SELECCION_EVENTO;
    private static final EstadoSesion UPDATED_ESTADO = EstadoSesion.SELECCION_ASIENTOS;

    private static final Instant DEFAULT_FECHA_INICIO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_INICIO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ULTIMA_ACTIVIDAD = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ULTIMA_ACTIVIDAD = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXPIRACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_ACTIVA = false;
    private static final Boolean UPDATED_ACTIVA = true;

    private static final String ENTITY_API_URL = "/api/sesions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SesionRepository sesionRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private SesionRepository sesionRepositoryMock;

    @Autowired
    private SesionMapper sesionMapper;

    @Mock
    private SesionService sesionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSesionMockMvc;

    private Sesion sesion;

    private Sesion insertedSesion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sesion createEntity(EntityManager em) {
        Sesion sesion = new Sesion()
            .estado(DEFAULT_ESTADO)
            .fechaInicio(DEFAULT_FECHA_INICIO)
            .ultimaActividad(DEFAULT_ULTIMA_ACTIVIDAD)
            .expiracion(DEFAULT_EXPIRACION)
            .activa(DEFAULT_ACTIVA);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        sesion.setUsuario(user);
        // Add required entity
        Evento evento;
        if (TestUtil.findAll(em, Evento.class).isEmpty()) {
            evento = EventoResourceIT.createEntity(em);
            em.persist(evento);
            em.flush();
        } else {
            evento = TestUtil.findAll(em, Evento.class).get(0);
        }
        sesion.setEvento(evento);
        return sesion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sesion createUpdatedEntity(EntityManager em) {
        Sesion updatedSesion = new Sesion()
            .estado(UPDATED_ESTADO)
            .fechaInicio(UPDATED_FECHA_INICIO)
            .ultimaActividad(UPDATED_ULTIMA_ACTIVIDAD)
            .expiracion(UPDATED_EXPIRACION)
            .activa(UPDATED_ACTIVA);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedSesion.setUsuario(user);
        // Add required entity
        Evento evento;
        if (TestUtil.findAll(em, Evento.class).isEmpty()) {
            evento = EventoResourceIT.createUpdatedEntity(em);
            em.persist(evento);
            em.flush();
        } else {
            evento = TestUtil.findAll(em, Evento.class).get(0);
        }
        updatedSesion.setEvento(evento);
        return updatedSesion;
    }

    @BeforeEach
    void initTest() {
        sesion = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSesion != null) {
            sesionRepository.delete(insertedSesion);
            insertedSesion = null;
        }
    }

    @Test
    @Transactional
    void createSesion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Sesion
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);
        var returnedSesionDTO = om.readValue(
            restSesionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SesionDTO.class
        );

        // Validate the Sesion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSesion = sesionMapper.toEntity(returnedSesionDTO);
        assertSesionUpdatableFieldsEquals(returnedSesion, getPersistedSesion(returnedSesion));

        insertedSesion = returnedSesion;
    }

    @Test
    @Transactional
    void createSesionWithExistingId() throws Exception {
        // Create the Sesion with an existing ID
        sesion.setId(1L);
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSesionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sesion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sesion.setEstado(null);

        // Create the Sesion, which fails.
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);

        restSesionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFechaInicioIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sesion.setFechaInicio(null);

        // Create the Sesion, which fails.
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);

        restSesionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUltimaActividadIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sesion.setUltimaActividad(null);

        // Create the Sesion, which fails.
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);

        restSesionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExpiracionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sesion.setExpiracion(null);

        // Create the Sesion, which fails.
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);

        restSesionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActivaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sesion.setActiva(null);

        // Create the Sesion, which fails.
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);

        restSesionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSesions() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList
        restSesionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sesion.getId().intValue())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].fechaInicio").value(hasItem(DEFAULT_FECHA_INICIO.toString())))
            .andExpect(jsonPath("$.[*].ultimaActividad").value(hasItem(DEFAULT_ULTIMA_ACTIVIDAD.toString())))
            .andExpect(jsonPath("$.[*].expiracion").value(hasItem(DEFAULT_EXPIRACION.toString())))
            .andExpect(jsonPath("$.[*].activa").value(hasItem(DEFAULT_ACTIVA)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSesionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(sesionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSesionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(sesionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSesionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(sesionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSesionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(sesionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSesion() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get the sesion
        restSesionMockMvc
            .perform(get(ENTITY_API_URL_ID, sesion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sesion.getId().intValue()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.fechaInicio").value(DEFAULT_FECHA_INICIO.toString()))
            .andExpect(jsonPath("$.ultimaActividad").value(DEFAULT_ULTIMA_ACTIVIDAD.toString()))
            .andExpect(jsonPath("$.expiracion").value(DEFAULT_EXPIRACION.toString()))
            .andExpect(jsonPath("$.activa").value(DEFAULT_ACTIVA));
    }

    @Test
    @Transactional
    void getSesionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        Long id = sesion.getId();

        defaultSesionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSesionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSesionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSesionsByEstadoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where estado equals to
        defaultSesionFiltering("estado.equals=" + DEFAULT_ESTADO, "estado.equals=" + UPDATED_ESTADO);
    }

    @Test
    @Transactional
    void getAllSesionsByEstadoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where estado in
        defaultSesionFiltering("estado.in=" + DEFAULT_ESTADO + "," + UPDATED_ESTADO, "estado.in=" + UPDATED_ESTADO);
    }

    @Test
    @Transactional
    void getAllSesionsByEstadoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where estado is not null
        defaultSesionFiltering("estado.specified=true", "estado.specified=false");
    }

    @Test
    @Transactional
    void getAllSesionsByFechaInicioIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where fechaInicio equals to
        defaultSesionFiltering("fechaInicio.equals=" + DEFAULT_FECHA_INICIO, "fechaInicio.equals=" + UPDATED_FECHA_INICIO);
    }

    @Test
    @Transactional
    void getAllSesionsByFechaInicioIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where fechaInicio in
        defaultSesionFiltering(
            "fechaInicio.in=" + DEFAULT_FECHA_INICIO + "," + UPDATED_FECHA_INICIO,
            "fechaInicio.in=" + UPDATED_FECHA_INICIO
        );
    }

    @Test
    @Transactional
    void getAllSesionsByFechaInicioIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where fechaInicio is not null
        defaultSesionFiltering("fechaInicio.specified=true", "fechaInicio.specified=false");
    }

    @Test
    @Transactional
    void getAllSesionsByUltimaActividadIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where ultimaActividad equals to
        defaultSesionFiltering("ultimaActividad.equals=" + DEFAULT_ULTIMA_ACTIVIDAD, "ultimaActividad.equals=" + UPDATED_ULTIMA_ACTIVIDAD);
    }

    @Test
    @Transactional
    void getAllSesionsByUltimaActividadIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where ultimaActividad in
        defaultSesionFiltering(
            "ultimaActividad.in=" + DEFAULT_ULTIMA_ACTIVIDAD + "," + UPDATED_ULTIMA_ACTIVIDAD,
            "ultimaActividad.in=" + UPDATED_ULTIMA_ACTIVIDAD
        );
    }

    @Test
    @Transactional
    void getAllSesionsByUltimaActividadIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where ultimaActividad is not null
        defaultSesionFiltering("ultimaActividad.specified=true", "ultimaActividad.specified=false");
    }

    @Test
    @Transactional
    void getAllSesionsByExpiracionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where expiracion equals to
        defaultSesionFiltering("expiracion.equals=" + DEFAULT_EXPIRACION, "expiracion.equals=" + UPDATED_EXPIRACION);
    }

    @Test
    @Transactional
    void getAllSesionsByExpiracionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where expiracion in
        defaultSesionFiltering("expiracion.in=" + DEFAULT_EXPIRACION + "," + UPDATED_EXPIRACION, "expiracion.in=" + UPDATED_EXPIRACION);
    }

    @Test
    @Transactional
    void getAllSesionsByExpiracionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where expiracion is not null
        defaultSesionFiltering("expiracion.specified=true", "expiracion.specified=false");
    }

    @Test
    @Transactional
    void getAllSesionsByActivaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where activa equals to
        defaultSesionFiltering("activa.equals=" + DEFAULT_ACTIVA, "activa.equals=" + UPDATED_ACTIVA);
    }

    @Test
    @Transactional
    void getAllSesionsByActivaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where activa in
        defaultSesionFiltering("activa.in=" + DEFAULT_ACTIVA + "," + UPDATED_ACTIVA, "activa.in=" + UPDATED_ACTIVA);
    }

    @Test
    @Transactional
    void getAllSesionsByActivaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        // Get all the sesionList where activa is not null
        defaultSesionFiltering("activa.specified=true", "activa.specified=false");
    }

    @Test
    @Transactional
    void getAllSesionsByUsuarioIsEqualToSomething() throws Exception {
        User usuario;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            sesionRepository.saveAndFlush(sesion);
            usuario = UserResourceIT.createEntity();
        } else {
            usuario = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(usuario);
        em.flush();
        sesion.setUsuario(usuario);
        sesionRepository.saveAndFlush(sesion);
        Long usuarioId = usuario.getId();
        // Get all the sesionList where usuario equals to usuarioId
        defaultSesionShouldBeFound("usuarioId.equals=" + usuarioId);

        // Get all the sesionList where usuario equals to (usuarioId + 1)
        defaultSesionShouldNotBeFound("usuarioId.equals=" + (usuarioId + 1));
    }

    @Test
    @Transactional
    void getAllSesionsByEventoIsEqualToSomething() throws Exception {
        Evento evento;
        if (TestUtil.findAll(em, Evento.class).isEmpty()) {
            sesionRepository.saveAndFlush(sesion);
            evento = EventoResourceIT.createEntity(em);
        } else {
            evento = TestUtil.findAll(em, Evento.class).get(0);
        }
        em.persist(evento);
        em.flush();
        sesion.setEvento(evento);
        sesionRepository.saveAndFlush(sesion);
        Long eventoId = evento.getId();
        // Get all the sesionList where evento equals to eventoId
        defaultSesionShouldBeFound("eventoId.equals=" + eventoId);

        // Get all the sesionList where evento equals to (eventoId + 1)
        defaultSesionShouldNotBeFound("eventoId.equals=" + (eventoId + 1));
    }

    private void defaultSesionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSesionShouldBeFound(shouldBeFound);
        defaultSesionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSesionShouldBeFound(String filter) throws Exception {
        restSesionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sesion.getId().intValue())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].fechaInicio").value(hasItem(DEFAULT_FECHA_INICIO.toString())))
            .andExpect(jsonPath("$.[*].ultimaActividad").value(hasItem(DEFAULT_ULTIMA_ACTIVIDAD.toString())))
            .andExpect(jsonPath("$.[*].expiracion").value(hasItem(DEFAULT_EXPIRACION.toString())))
            .andExpect(jsonPath("$.[*].activa").value(hasItem(DEFAULT_ACTIVA)));

        // Check, that the count call also returns 1
        restSesionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSesionShouldNotBeFound(String filter) throws Exception {
        restSesionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSesionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSesion() throws Exception {
        // Get the sesion
        restSesionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSesion() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sesion
        Sesion updatedSesion = sesionRepository.findById(sesion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSesion are not directly saved in db
        em.detach(updatedSesion);
        updatedSesion
            .estado(UPDATED_ESTADO)
            .fechaInicio(UPDATED_FECHA_INICIO)
            .ultimaActividad(UPDATED_ULTIMA_ACTIVIDAD)
            .expiracion(UPDATED_EXPIRACION)
            .activa(UPDATED_ACTIVA);
        SesionDTO sesionDTO = sesionMapper.toDto(updatedSesion);

        restSesionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sesionDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Sesion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSesionToMatchAllProperties(updatedSesion);
    }

    @Test
    @Transactional
    void putNonExistingSesion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sesion.setId(longCount.incrementAndGet());

        // Create the Sesion
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSesionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sesionDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sesion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSesion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sesion.setId(longCount.incrementAndGet());

        // Create the Sesion
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSesionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sesionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sesion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSesion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sesion.setId(longCount.incrementAndGet());

        // Create the Sesion
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSesionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sesionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sesion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSesionWithPatch() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sesion using partial update
        Sesion partialUpdatedSesion = new Sesion();
        partialUpdatedSesion.setId(sesion.getId());

        partialUpdatedSesion.expiracion(UPDATED_EXPIRACION);

        restSesionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSesion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSesion))
            )
            .andExpect(status().isOk());

        // Validate the Sesion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSesionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSesion, sesion), getPersistedSesion(sesion));
    }

    @Test
    @Transactional
    void fullUpdateSesionWithPatch() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sesion using partial update
        Sesion partialUpdatedSesion = new Sesion();
        partialUpdatedSesion.setId(sesion.getId());

        partialUpdatedSesion
            .estado(UPDATED_ESTADO)
            .fechaInicio(UPDATED_FECHA_INICIO)
            .ultimaActividad(UPDATED_ULTIMA_ACTIVIDAD)
            .expiracion(UPDATED_EXPIRACION)
            .activa(UPDATED_ACTIVA);

        restSesionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSesion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSesion))
            )
            .andExpect(status().isOk());

        // Validate the Sesion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSesionUpdatableFieldsEquals(partialUpdatedSesion, getPersistedSesion(partialUpdatedSesion));
    }

    @Test
    @Transactional
    void patchNonExistingSesion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sesion.setId(longCount.incrementAndGet());

        // Create the Sesion
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSesionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sesionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sesionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sesion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSesion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sesion.setId(longCount.incrementAndGet());

        // Create the Sesion
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSesionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sesionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sesion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSesion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sesion.setId(longCount.incrementAndGet());

        // Create the Sesion
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSesionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sesionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sesion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSesion() throws Exception {
        // Initialize the database
        insertedSesion = sesionRepository.saveAndFlush(sesion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sesion
        restSesionMockMvc
            .perform(delete(ENTITY_API_URL_ID, sesion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sesionRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Sesion getPersistedSesion(Sesion sesion) {
        return sesionRepository.findById(sesion.getId()).orElseThrow();
    }

    protected void assertPersistedSesionToMatchAllProperties(Sesion expectedSesion) {
        assertSesionAllPropertiesEquals(expectedSesion, getPersistedSesion(expectedSesion));
    }

    protected void assertPersistedSesionToMatchUpdatableProperties(Sesion expectedSesion) {
        assertSesionAllUpdatablePropertiesEquals(expectedSesion, getPersistedSesion(expectedSesion));
    }
}
