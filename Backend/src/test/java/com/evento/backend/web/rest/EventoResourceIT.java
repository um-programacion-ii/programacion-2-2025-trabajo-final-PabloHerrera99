package com.evento.backend.web.rest;

import static com.evento.backend.domain.EventoAsserts.*;
import static com.evento.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static com.evento.backend.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.evento.backend.IntegrationTest;
import com.evento.backend.domain.Evento;
import com.evento.backend.domain.EventoTipo;
import com.evento.backend.repository.EventoRepository;
import com.evento.backend.service.EventoService;
import com.evento.backend.service.dto.EventoDTO;
import com.evento.backend.service.mapper.EventoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link EventoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EventoResourceIT {

    private static final Long DEFAULT_ID_CATEDRA = 1L;
    private static final Long UPDATED_ID_CATEDRA = 2L;
    private static final Long SMALLER_ID_CATEDRA = 1L - 1L;

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final String DEFAULT_RESUMEN = "AAAAAAAAAA";
    private static final String UPDATED_RESUMEN = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_DIRECCION = "AAAAAAAAAA";
    private static final String UPDATED_DIRECCION = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGEN = "AAAAAAAAAA";
    private static final String UPDATED_IMAGEN = "BBBBBBBBBB";

    private static final Integer DEFAULT_FILA_ASIENTOS = 1;
    private static final Integer UPDATED_FILA_ASIENTOS = 2;
    private static final Integer SMALLER_FILA_ASIENTOS = 1 - 1;

    private static final Integer DEFAULT_COLUMNA_ASIENTOS = 1;
    private static final Integer UPDATED_COLUMNA_ASIENTOS = 2;
    private static final Integer SMALLER_COLUMNA_ASIENTOS = 1 - 1;

    private static final BigDecimal DEFAULT_PRECIO_ENTRADA = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRECIO_ENTRADA = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRECIO_ENTRADA = new BigDecimal(0 - 1);

    private static final Boolean DEFAULT_ACTIVO = false;
    private static final Boolean UPDATED_ACTIVO = true;

    private static final Instant DEFAULT_FECHA_SINCRONIZACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_SINCRONIZACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/eventos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EventoRepository eventoRepository;

    @Mock
    private EventoRepository eventoRepositoryMock;

    @Autowired
    private EventoMapper eventoMapper;

    @Mock
    private EventoService eventoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEventoMockMvc;

    private Evento evento;

    private Evento insertedEvento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evento createEntity(EntityManager em) {
        Evento evento = new Evento()
            .idCatedra(DEFAULT_ID_CATEDRA)
            .titulo(DEFAULT_TITULO)
            .resumen(DEFAULT_RESUMEN)
            .descripcion(DEFAULT_DESCRIPCION)
            .fecha(DEFAULT_FECHA)
            .direccion(DEFAULT_DIRECCION)
            .imagen(DEFAULT_IMAGEN)
            .filaAsientos(DEFAULT_FILA_ASIENTOS)
            .columnaAsientos(DEFAULT_COLUMNA_ASIENTOS)
            .precioEntrada(DEFAULT_PRECIO_ENTRADA)
            .activo(DEFAULT_ACTIVO)
            .fechaSincronizacion(DEFAULT_FECHA_SINCRONIZACION);
        // Add required entity
        EventoTipo eventoTipo;
        if (TestUtil.findAll(em, EventoTipo.class).isEmpty()) {
            eventoTipo = EventoTipoResourceIT.createEntity();
            em.persist(eventoTipo);
            em.flush();
        } else {
            eventoTipo = TestUtil.findAll(em, EventoTipo.class).get(0);
        }
        evento.setEventoTipo(eventoTipo);
        return evento;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evento createUpdatedEntity(EntityManager em) {
        Evento updatedEvento = new Evento()
            .idCatedra(UPDATED_ID_CATEDRA)
            .titulo(UPDATED_TITULO)
            .resumen(UPDATED_RESUMEN)
            .descripcion(UPDATED_DESCRIPCION)
            .fecha(UPDATED_FECHA)
            .direccion(UPDATED_DIRECCION)
            .imagen(UPDATED_IMAGEN)
            .filaAsientos(UPDATED_FILA_ASIENTOS)
            .columnaAsientos(UPDATED_COLUMNA_ASIENTOS)
            .precioEntrada(UPDATED_PRECIO_ENTRADA)
            .activo(UPDATED_ACTIVO)
            .fechaSincronizacion(UPDATED_FECHA_SINCRONIZACION);
        // Add required entity
        EventoTipo eventoTipo;
        if (TestUtil.findAll(em, EventoTipo.class).isEmpty()) {
            eventoTipo = EventoTipoResourceIT.createUpdatedEntity();
            em.persist(eventoTipo);
            em.flush();
        } else {
            eventoTipo = TestUtil.findAll(em, EventoTipo.class).get(0);
        }
        updatedEvento.setEventoTipo(eventoTipo);
        return updatedEvento;
    }

    @BeforeEach
    void initTest() {
        evento = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedEvento != null) {
            eventoRepository.delete(insertedEvento);
            insertedEvento = null;
        }
    }

    @Test
    @Transactional
    void createEvento() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Evento
        EventoDTO eventoDTO = eventoMapper.toDto(evento);
        var returnedEventoDTO = om.readValue(
            restEventoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EventoDTO.class
        );

        // Validate the Evento in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEvento = eventoMapper.toEntity(returnedEventoDTO);
        assertEventoUpdatableFieldsEquals(returnedEvento, getPersistedEvento(returnedEvento));

        insertedEvento = returnedEvento;
    }

    @Test
    @Transactional
    void createEventoWithExistingId() throws Exception {
        // Create the Evento with an existing ID
        evento.setId(1L);
        EventoDTO eventoDTO = eventoMapper.toDto(evento);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEventoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTituloIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        evento.setTitulo(null);

        // Create the Evento, which fails.
        EventoDTO eventoDTO = eventoMapper.toDto(evento);

        restEventoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFechaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        evento.setFecha(null);

        // Create the Evento, which fails.
        EventoDTO eventoDTO = eventoMapper.toDto(evento);

        restEventoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrecioEntradaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        evento.setPrecioEntrada(null);

        // Create the Evento, which fails.
        EventoDTO eventoDTO = eventoMapper.toDto(evento);

        restEventoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEventos() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList
        restEventoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(evento.getId().intValue())))
            .andExpect(jsonPath("$.[*].idCatedra").value(hasItem(DEFAULT_ID_CATEDRA.intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].resumen").value(hasItem(DEFAULT_RESUMEN)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].fecha").value(hasItem(DEFAULT_FECHA.toString())))
            .andExpect(jsonPath("$.[*].direccion").value(hasItem(DEFAULT_DIRECCION)))
            .andExpect(jsonPath("$.[*].imagen").value(hasItem(DEFAULT_IMAGEN)))
            .andExpect(jsonPath("$.[*].filaAsientos").value(hasItem(DEFAULT_FILA_ASIENTOS)))
            .andExpect(jsonPath("$.[*].columnaAsientos").value(hasItem(DEFAULT_COLUMNA_ASIENTOS)))
            .andExpect(jsonPath("$.[*].precioEntrada").value(hasItem(sameNumber(DEFAULT_PRECIO_ENTRADA))))
            .andExpect(jsonPath("$.[*].activo").value(hasItem(DEFAULT_ACTIVO)))
            .andExpect(jsonPath("$.[*].fechaSincronizacion").value(hasItem(DEFAULT_FECHA_SINCRONIZACION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventosWithEagerRelationshipsIsEnabled() throws Exception {
        when(eventoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEventoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(eventoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(eventoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEventoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(eventoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEvento() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get the evento
        restEventoMockMvc
            .perform(get(ENTITY_API_URL_ID, evento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(evento.getId().intValue()))
            .andExpect(jsonPath("$.idCatedra").value(DEFAULT_ID_CATEDRA.intValue()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO))
            .andExpect(jsonPath("$.resumen").value(DEFAULT_RESUMEN))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.fecha").value(DEFAULT_FECHA.toString()))
            .andExpect(jsonPath("$.direccion").value(DEFAULT_DIRECCION))
            .andExpect(jsonPath("$.imagen").value(DEFAULT_IMAGEN))
            .andExpect(jsonPath("$.filaAsientos").value(DEFAULT_FILA_ASIENTOS))
            .andExpect(jsonPath("$.columnaAsientos").value(DEFAULT_COLUMNA_ASIENTOS))
            .andExpect(jsonPath("$.precioEntrada").value(sameNumber(DEFAULT_PRECIO_ENTRADA)))
            .andExpect(jsonPath("$.activo").value(DEFAULT_ACTIVO))
            .andExpect(jsonPath("$.fechaSincronizacion").value(DEFAULT_FECHA_SINCRONIZACION.toString()));
    }

    @Test
    @Transactional
    void getEventosByIdFiltering() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        Long id = evento.getId();

        defaultEventoFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEventoFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEventoFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEventosByIdCatedraIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where idCatedra equals to
        defaultEventoFiltering("idCatedra.equals=" + DEFAULT_ID_CATEDRA, "idCatedra.equals=" + UPDATED_ID_CATEDRA);
    }

    @Test
    @Transactional
    void getAllEventosByIdCatedraIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where idCatedra in
        defaultEventoFiltering("idCatedra.in=" + DEFAULT_ID_CATEDRA + "," + UPDATED_ID_CATEDRA, "idCatedra.in=" + UPDATED_ID_CATEDRA);
    }

    @Test
    @Transactional
    void getAllEventosByIdCatedraIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where idCatedra is not null
        defaultEventoFiltering("idCatedra.specified=true", "idCatedra.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByIdCatedraIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where idCatedra is greater than or equal to
        defaultEventoFiltering("idCatedra.greaterThanOrEqual=" + DEFAULT_ID_CATEDRA, "idCatedra.greaterThanOrEqual=" + UPDATED_ID_CATEDRA);
    }

    @Test
    @Transactional
    void getAllEventosByIdCatedraIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where idCatedra is less than or equal to
        defaultEventoFiltering("idCatedra.lessThanOrEqual=" + DEFAULT_ID_CATEDRA, "idCatedra.lessThanOrEqual=" + SMALLER_ID_CATEDRA);
    }

    @Test
    @Transactional
    void getAllEventosByIdCatedraIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where idCatedra is less than
        defaultEventoFiltering("idCatedra.lessThan=" + UPDATED_ID_CATEDRA, "idCatedra.lessThan=" + DEFAULT_ID_CATEDRA);
    }

    @Test
    @Transactional
    void getAllEventosByIdCatedraIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where idCatedra is greater than
        defaultEventoFiltering("idCatedra.greaterThan=" + SMALLER_ID_CATEDRA, "idCatedra.greaterThan=" + DEFAULT_ID_CATEDRA);
    }

    @Test
    @Transactional
    void getAllEventosByTituloIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where titulo equals to
        defaultEventoFiltering("titulo.equals=" + DEFAULT_TITULO, "titulo.equals=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllEventosByTituloIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where titulo in
        defaultEventoFiltering("titulo.in=" + DEFAULT_TITULO + "," + UPDATED_TITULO, "titulo.in=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllEventosByTituloIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where titulo is not null
        defaultEventoFiltering("titulo.specified=true", "titulo.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByTituloContainsSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where titulo contains
        defaultEventoFiltering("titulo.contains=" + DEFAULT_TITULO, "titulo.contains=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllEventosByTituloNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where titulo does not contain
        defaultEventoFiltering("titulo.doesNotContain=" + UPDATED_TITULO, "titulo.doesNotContain=" + DEFAULT_TITULO);
    }

    @Test
    @Transactional
    void getAllEventosByResumenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where resumen equals to
        defaultEventoFiltering("resumen.equals=" + DEFAULT_RESUMEN, "resumen.equals=" + UPDATED_RESUMEN);
    }

    @Test
    @Transactional
    void getAllEventosByResumenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where resumen in
        defaultEventoFiltering("resumen.in=" + DEFAULT_RESUMEN + "," + UPDATED_RESUMEN, "resumen.in=" + UPDATED_RESUMEN);
    }

    @Test
    @Transactional
    void getAllEventosByResumenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where resumen is not null
        defaultEventoFiltering("resumen.specified=true", "resumen.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByResumenContainsSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where resumen contains
        defaultEventoFiltering("resumen.contains=" + DEFAULT_RESUMEN, "resumen.contains=" + UPDATED_RESUMEN);
    }

    @Test
    @Transactional
    void getAllEventosByResumenNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where resumen does not contain
        defaultEventoFiltering("resumen.doesNotContain=" + UPDATED_RESUMEN, "resumen.doesNotContain=" + DEFAULT_RESUMEN);
    }

    @Test
    @Transactional
    void getAllEventosByFechaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where fecha equals to
        defaultEventoFiltering("fecha.equals=" + DEFAULT_FECHA, "fecha.equals=" + UPDATED_FECHA);
    }

    @Test
    @Transactional
    void getAllEventosByFechaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where fecha in
        defaultEventoFiltering("fecha.in=" + DEFAULT_FECHA + "," + UPDATED_FECHA, "fecha.in=" + UPDATED_FECHA);
    }

    @Test
    @Transactional
    void getAllEventosByFechaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where fecha is not null
        defaultEventoFiltering("fecha.specified=true", "fecha.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByDireccionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where direccion equals to
        defaultEventoFiltering("direccion.equals=" + DEFAULT_DIRECCION, "direccion.equals=" + UPDATED_DIRECCION);
    }

    @Test
    @Transactional
    void getAllEventosByDireccionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where direccion in
        defaultEventoFiltering("direccion.in=" + DEFAULT_DIRECCION + "," + UPDATED_DIRECCION, "direccion.in=" + UPDATED_DIRECCION);
    }

    @Test
    @Transactional
    void getAllEventosByDireccionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where direccion is not null
        defaultEventoFiltering("direccion.specified=true", "direccion.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByDireccionContainsSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where direccion contains
        defaultEventoFiltering("direccion.contains=" + DEFAULT_DIRECCION, "direccion.contains=" + UPDATED_DIRECCION);
    }

    @Test
    @Transactional
    void getAllEventosByDireccionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where direccion does not contain
        defaultEventoFiltering("direccion.doesNotContain=" + UPDATED_DIRECCION, "direccion.doesNotContain=" + DEFAULT_DIRECCION);
    }

    @Test
    @Transactional
    void getAllEventosByImagenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where imagen equals to
        defaultEventoFiltering("imagen.equals=" + DEFAULT_IMAGEN, "imagen.equals=" + UPDATED_IMAGEN);
    }

    @Test
    @Transactional
    void getAllEventosByImagenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where imagen in
        defaultEventoFiltering("imagen.in=" + DEFAULT_IMAGEN + "," + UPDATED_IMAGEN, "imagen.in=" + UPDATED_IMAGEN);
    }

    @Test
    @Transactional
    void getAllEventosByImagenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where imagen is not null
        defaultEventoFiltering("imagen.specified=true", "imagen.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByImagenContainsSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where imagen contains
        defaultEventoFiltering("imagen.contains=" + DEFAULT_IMAGEN, "imagen.contains=" + UPDATED_IMAGEN);
    }

    @Test
    @Transactional
    void getAllEventosByImagenNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where imagen does not contain
        defaultEventoFiltering("imagen.doesNotContain=" + UPDATED_IMAGEN, "imagen.doesNotContain=" + DEFAULT_IMAGEN);
    }

    @Test
    @Transactional
    void getAllEventosByFilaAsientosIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where filaAsientos equals to
        defaultEventoFiltering("filaAsientos.equals=" + DEFAULT_FILA_ASIENTOS, "filaAsientos.equals=" + UPDATED_FILA_ASIENTOS);
    }

    @Test
    @Transactional
    void getAllEventosByFilaAsientosIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where filaAsientos in
        defaultEventoFiltering(
            "filaAsientos.in=" + DEFAULT_FILA_ASIENTOS + "," + UPDATED_FILA_ASIENTOS,
            "filaAsientos.in=" + UPDATED_FILA_ASIENTOS
        );
    }

    @Test
    @Transactional
    void getAllEventosByFilaAsientosIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where filaAsientos is not null
        defaultEventoFiltering("filaAsientos.specified=true", "filaAsientos.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByFilaAsientosIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where filaAsientos is greater than or equal to
        defaultEventoFiltering(
            "filaAsientos.greaterThanOrEqual=" + DEFAULT_FILA_ASIENTOS,
            "filaAsientos.greaterThanOrEqual=" + (DEFAULT_FILA_ASIENTOS + 1)
        );
    }

    @Test
    @Transactional
    void getAllEventosByFilaAsientosIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where filaAsientos is less than or equal to
        defaultEventoFiltering(
            "filaAsientos.lessThanOrEqual=" + DEFAULT_FILA_ASIENTOS,
            "filaAsientos.lessThanOrEqual=" + SMALLER_FILA_ASIENTOS
        );
    }

    @Test
    @Transactional
    void getAllEventosByFilaAsientosIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where filaAsientos is less than
        defaultEventoFiltering("filaAsientos.lessThan=" + (DEFAULT_FILA_ASIENTOS + 1), "filaAsientos.lessThan=" + DEFAULT_FILA_ASIENTOS);
    }

    @Test
    @Transactional
    void getAllEventosByFilaAsientosIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where filaAsientos is greater than
        defaultEventoFiltering("filaAsientos.greaterThan=" + SMALLER_FILA_ASIENTOS, "filaAsientos.greaterThan=" + DEFAULT_FILA_ASIENTOS);
    }

    @Test
    @Transactional
    void getAllEventosByColumnaAsientosIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where columnaAsientos equals to
        defaultEventoFiltering("columnaAsientos.equals=" + DEFAULT_COLUMNA_ASIENTOS, "columnaAsientos.equals=" + UPDATED_COLUMNA_ASIENTOS);
    }

    @Test
    @Transactional
    void getAllEventosByColumnaAsientosIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where columnaAsientos in
        defaultEventoFiltering(
            "columnaAsientos.in=" + DEFAULT_COLUMNA_ASIENTOS + "," + UPDATED_COLUMNA_ASIENTOS,
            "columnaAsientos.in=" + UPDATED_COLUMNA_ASIENTOS
        );
    }

    @Test
    @Transactional
    void getAllEventosByColumnaAsientosIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where columnaAsientos is not null
        defaultEventoFiltering("columnaAsientos.specified=true", "columnaAsientos.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByColumnaAsientosIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where columnaAsientos is greater than or equal to
        defaultEventoFiltering(
            "columnaAsientos.greaterThanOrEqual=" + DEFAULT_COLUMNA_ASIENTOS,
            "columnaAsientos.greaterThanOrEqual=" + (DEFAULT_COLUMNA_ASIENTOS + 1)
        );
    }

    @Test
    @Transactional
    void getAllEventosByColumnaAsientosIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where columnaAsientos is less than or equal to
        defaultEventoFiltering(
            "columnaAsientos.lessThanOrEqual=" + DEFAULT_COLUMNA_ASIENTOS,
            "columnaAsientos.lessThanOrEqual=" + SMALLER_COLUMNA_ASIENTOS
        );
    }

    @Test
    @Transactional
    void getAllEventosByColumnaAsientosIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where columnaAsientos is less than
        defaultEventoFiltering(
            "columnaAsientos.lessThan=" + (DEFAULT_COLUMNA_ASIENTOS + 1),
            "columnaAsientos.lessThan=" + DEFAULT_COLUMNA_ASIENTOS
        );
    }

    @Test
    @Transactional
    void getAllEventosByColumnaAsientosIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where columnaAsientos is greater than
        defaultEventoFiltering(
            "columnaAsientos.greaterThan=" + SMALLER_COLUMNA_ASIENTOS,
            "columnaAsientos.greaterThan=" + DEFAULT_COLUMNA_ASIENTOS
        );
    }

    @Test
    @Transactional
    void getAllEventosByPrecioEntradaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where precioEntrada equals to
        defaultEventoFiltering("precioEntrada.equals=" + DEFAULT_PRECIO_ENTRADA, "precioEntrada.equals=" + UPDATED_PRECIO_ENTRADA);
    }

    @Test
    @Transactional
    void getAllEventosByPrecioEntradaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where precioEntrada in
        defaultEventoFiltering(
            "precioEntrada.in=" + DEFAULT_PRECIO_ENTRADA + "," + UPDATED_PRECIO_ENTRADA,
            "precioEntrada.in=" + UPDATED_PRECIO_ENTRADA
        );
    }

    @Test
    @Transactional
    void getAllEventosByPrecioEntradaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where precioEntrada is not null
        defaultEventoFiltering("precioEntrada.specified=true", "precioEntrada.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByPrecioEntradaIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where precioEntrada is greater than or equal to
        defaultEventoFiltering(
            "precioEntrada.greaterThanOrEqual=" + DEFAULT_PRECIO_ENTRADA,
            "precioEntrada.greaterThanOrEqual=" + UPDATED_PRECIO_ENTRADA
        );
    }

    @Test
    @Transactional
    void getAllEventosByPrecioEntradaIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where precioEntrada is less than or equal to
        defaultEventoFiltering(
            "precioEntrada.lessThanOrEqual=" + DEFAULT_PRECIO_ENTRADA,
            "precioEntrada.lessThanOrEqual=" + SMALLER_PRECIO_ENTRADA
        );
    }

    @Test
    @Transactional
    void getAllEventosByPrecioEntradaIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where precioEntrada is less than
        defaultEventoFiltering("precioEntrada.lessThan=" + UPDATED_PRECIO_ENTRADA, "precioEntrada.lessThan=" + DEFAULT_PRECIO_ENTRADA);
    }

    @Test
    @Transactional
    void getAllEventosByPrecioEntradaIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where precioEntrada is greater than
        defaultEventoFiltering(
            "precioEntrada.greaterThan=" + SMALLER_PRECIO_ENTRADA,
            "precioEntrada.greaterThan=" + DEFAULT_PRECIO_ENTRADA
        );
    }

    @Test
    @Transactional
    void getAllEventosByActivoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where activo equals to
        defaultEventoFiltering("activo.equals=" + DEFAULT_ACTIVO, "activo.equals=" + UPDATED_ACTIVO);
    }

    @Test
    @Transactional
    void getAllEventosByActivoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where activo in
        defaultEventoFiltering("activo.in=" + DEFAULT_ACTIVO + "," + UPDATED_ACTIVO, "activo.in=" + UPDATED_ACTIVO);
    }

    @Test
    @Transactional
    void getAllEventosByActivoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where activo is not null
        defaultEventoFiltering("activo.specified=true", "activo.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByFechaSincronizacionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where fechaSincronizacion equals to
        defaultEventoFiltering(
            "fechaSincronizacion.equals=" + DEFAULT_FECHA_SINCRONIZACION,
            "fechaSincronizacion.equals=" + UPDATED_FECHA_SINCRONIZACION
        );
    }

    @Test
    @Transactional
    void getAllEventosByFechaSincronizacionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where fechaSincronizacion in
        defaultEventoFiltering(
            "fechaSincronizacion.in=" + DEFAULT_FECHA_SINCRONIZACION + "," + UPDATED_FECHA_SINCRONIZACION,
            "fechaSincronizacion.in=" + UPDATED_FECHA_SINCRONIZACION
        );
    }

    @Test
    @Transactional
    void getAllEventosByFechaSincronizacionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        // Get all the eventoList where fechaSincronizacion is not null
        defaultEventoFiltering("fechaSincronizacion.specified=true", "fechaSincronizacion.specified=false");
    }

    @Test
    @Transactional
    void getAllEventosByEventoTipoIsEqualToSomething() throws Exception {
        EventoTipo eventoTipo;
        if (TestUtil.findAll(em, EventoTipo.class).isEmpty()) {
            eventoRepository.saveAndFlush(evento);
            eventoTipo = EventoTipoResourceIT.createEntity();
        } else {
            eventoTipo = TestUtil.findAll(em, EventoTipo.class).get(0);
        }
        em.persist(eventoTipo);
        em.flush();
        evento.setEventoTipo(eventoTipo);
        eventoRepository.saveAndFlush(evento);
        Long eventoTipoId = eventoTipo.getId();
        // Get all the eventoList where eventoTipo equals to eventoTipoId
        defaultEventoShouldBeFound("eventoTipoId.equals=" + eventoTipoId);

        // Get all the eventoList where eventoTipo equals to (eventoTipoId + 1)
        defaultEventoShouldNotBeFound("eventoTipoId.equals=" + (eventoTipoId + 1));
    }

    private void defaultEventoFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEventoShouldBeFound(shouldBeFound);
        defaultEventoShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEventoShouldBeFound(String filter) throws Exception {
        restEventoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(evento.getId().intValue())))
            .andExpect(jsonPath("$.[*].idCatedra").value(hasItem(DEFAULT_ID_CATEDRA.intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].resumen").value(hasItem(DEFAULT_RESUMEN)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].fecha").value(hasItem(DEFAULT_FECHA.toString())))
            .andExpect(jsonPath("$.[*].direccion").value(hasItem(DEFAULT_DIRECCION)))
            .andExpect(jsonPath("$.[*].imagen").value(hasItem(DEFAULT_IMAGEN)))
            .andExpect(jsonPath("$.[*].filaAsientos").value(hasItem(DEFAULT_FILA_ASIENTOS)))
            .andExpect(jsonPath("$.[*].columnaAsientos").value(hasItem(DEFAULT_COLUMNA_ASIENTOS)))
            .andExpect(jsonPath("$.[*].precioEntrada").value(hasItem(sameNumber(DEFAULT_PRECIO_ENTRADA))))
            .andExpect(jsonPath("$.[*].activo").value(hasItem(DEFAULT_ACTIVO)))
            .andExpect(jsonPath("$.[*].fechaSincronizacion").value(hasItem(DEFAULT_FECHA_SINCRONIZACION.toString())));

        // Check, that the count call also returns 1
        restEventoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEventoShouldNotBeFound(String filter) throws Exception {
        restEventoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEventoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEvento() throws Exception {
        // Get the evento
        restEventoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEvento() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evento
        Evento updatedEvento = eventoRepository.findById(evento.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEvento are not directly saved in db
        em.detach(updatedEvento);
        updatedEvento
            .idCatedra(UPDATED_ID_CATEDRA)
            .titulo(UPDATED_TITULO)
            .resumen(UPDATED_RESUMEN)
            .descripcion(UPDATED_DESCRIPCION)
            .fecha(UPDATED_FECHA)
            .direccion(UPDATED_DIRECCION)
            .imagen(UPDATED_IMAGEN)
            .filaAsientos(UPDATED_FILA_ASIENTOS)
            .columnaAsientos(UPDATED_COLUMNA_ASIENTOS)
            .precioEntrada(UPDATED_PRECIO_ENTRADA)
            .activo(UPDATED_ACTIVO)
            .fechaSincronizacion(UPDATED_FECHA_SINCRONIZACION);
        EventoDTO eventoDTO = eventoMapper.toDto(updatedEvento);

        restEventoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEventoToMatchAllProperties(updatedEvento);
    }

    @Test
    @Transactional
    void putNonExistingEvento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evento.setId(longCount.incrementAndGet());

        // Create the Evento
        EventoDTO eventoDTO = eventoMapper.toDto(evento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, eventoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEvento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evento.setId(longCount.incrementAndGet());

        // Create the Evento
        EventoDTO eventoDTO = eventoMapper.toDto(evento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(eventoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEvento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evento.setId(longCount.incrementAndGet());

        // Create the Evento
        EventoDTO eventoDTO = eventoMapper.toDto(evento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(eventoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEventoWithPatch() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evento using partial update
        Evento partialUpdatedEvento = new Evento();
        partialUpdatedEvento.setId(evento.getId());

        partialUpdatedEvento
            .titulo(UPDATED_TITULO)
            .resumen(UPDATED_RESUMEN)
            .direccion(UPDATED_DIRECCION)
            .imagen(UPDATED_IMAGEN)
            .columnaAsientos(UPDATED_COLUMNA_ASIENTOS)
            .fechaSincronizacion(UPDATED_FECHA_SINCRONIZACION);

        restEventoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEvento))
            )
            .andExpect(status().isOk());

        // Validate the Evento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEvento, evento), getPersistedEvento(evento));
    }

    @Test
    @Transactional
    void fullUpdateEventoWithPatch() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the evento using partial update
        Evento partialUpdatedEvento = new Evento();
        partialUpdatedEvento.setId(evento.getId());

        partialUpdatedEvento
            .idCatedra(UPDATED_ID_CATEDRA)
            .titulo(UPDATED_TITULO)
            .resumen(UPDATED_RESUMEN)
            .descripcion(UPDATED_DESCRIPCION)
            .fecha(UPDATED_FECHA)
            .direccion(UPDATED_DIRECCION)
            .imagen(UPDATED_IMAGEN)
            .filaAsientos(UPDATED_FILA_ASIENTOS)
            .columnaAsientos(UPDATED_COLUMNA_ASIENTOS)
            .precioEntrada(UPDATED_PRECIO_ENTRADA)
            .activo(UPDATED_ACTIVO)
            .fechaSincronizacion(UPDATED_FECHA_SINCRONIZACION);

        restEventoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEvento.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEvento))
            )
            .andExpect(status().isOk());

        // Validate the Evento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEventoUpdatableFieldsEquals(partialUpdatedEvento, getPersistedEvento(partialUpdatedEvento));
    }

    @Test
    @Transactional
    void patchNonExistingEvento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evento.setId(longCount.incrementAndGet());

        // Create the Evento
        EventoDTO eventoDTO = eventoMapper.toDto(evento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEventoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, eventoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEvento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evento.setId(longCount.incrementAndGet());

        // Create the Evento
        EventoDTO eventoDTO = eventoMapper.toDto(evento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(eventoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEvento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        evento.setId(longCount.incrementAndGet());

        // Create the Evento
        EventoDTO eventoDTO = eventoMapper.toDto(evento);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEventoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(eventoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Evento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEvento() throws Exception {
        // Initialize the database
        insertedEvento = eventoRepository.saveAndFlush(evento);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the evento
        restEventoMockMvc
            .perform(delete(ENTITY_API_URL_ID, evento.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return eventoRepository.count();
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

    protected Evento getPersistedEvento(Evento evento) {
        return eventoRepository.findById(evento.getId()).orElseThrow();
    }

    protected void assertPersistedEventoToMatchAllProperties(Evento expectedEvento) {
        assertEventoAllPropertiesEquals(expectedEvento, getPersistedEvento(expectedEvento));
    }

    protected void assertPersistedEventoToMatchUpdatableProperties(Evento expectedEvento) {
        assertEventoAllUpdatablePropertiesEquals(expectedEvento, getPersistedEvento(expectedEvento));
    }
}
